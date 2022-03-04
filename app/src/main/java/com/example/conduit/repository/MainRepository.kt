package com.example.conduit.repository

import android.util.Log
import com.example.conduit.data.local.AuthDao
import com.example.conduit.data.local.offlineModel.OfflineArticle
import com.example.conduit.data.local.offlineModel.OfflineArticleResponse
import com.example.conduit.model.*
import com.example.conduit.data.remote.ConduitApiService
import com.example.conduit.data.remote.NetworkResult
import com.example.conduit.util.Constants.FAVOURITE
import com.example.conduit.util.Constants.GLOBAL
import com.example.conduit.util.Constants.MINE
import com.example.conduit.util.Constants.MYFEED
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiService: ConduitApiService,
    private val authDao: AuthDao
) {

    suspend fun getArticles(token: String?) : NetworkResult<ArticlesResponse> = returnArticleResponse(token,GLOBAL)

    suspend fun getArticleByTag(tag:String) = apiService.getArticleByTag(tag)

    suspend fun getCurrentUser(token:String?): NetworkResult<UserResponse>{
        try {
            val response = apiService.getCurrentUser(token)
            if(response.isSuccessful){
                response.body()?.let{
                    authDao.insertUser(it.user!!)
                    val userResponse = authDao.getUser()
                    Log.d("database",userResponse.username)
                    return NetworkResult.Success(UserResponse(userResponse))
                }

            }
            else{
                val userResponse = authDao.getUser()
                return NetworkResult.Error(response.message(),UserResponse(userResponse))
            }
        }
        catch (e: IOException){
            val userResponse = authDao.getUser()
            return NetworkResult.Error("Check Your Internet Connection",UserResponse(userResponse))
        }
        return NetworkResult.Error("Something Went Wrong")
    }


    suspend fun getMyArticles(token: String?,author: String?,limit:Int?) :NetworkResult<ArticlesResponse> = returnArticleResponse(token = token, author = author, limit = limit, type = MINE)
    suspend fun getMyFavouritedArticles(token: String?,author: String?,limit:Int?) :NetworkResult<ArticlesResponse> = returnArticleResponse(token = token, author = author, limit = limit, type = FAVOURITE)
    suspend fun getMyFeed(token: String?) :NetworkResult<ArticlesResponse> = returnArticleResponse(token,MYFEED)


    suspend fun signUpUser(userRequestRegister:UserRequestRegister) = apiService.signUpUser(userRequestRegister)
    suspend fun loginUser(userRequestLogin: UserRequestLogin) = apiService.loginUser(userRequestLogin)
    suspend fun updateUser(token: String?,user: UpdateRequestUser) = apiService.upDateUser(token,user)
    suspend fun createArticle(token: String?, articleRequest: ArticleRequest) = apiService.createArticle(token, articleRequest)
    suspend fun getSingleArticleBySlug(slug:String?,token: String?) :NetworkResult<Response<SingleArticleResponseBySlug>> {
        return try {
            val response = apiService.getSingleArticleBySlug(slug,token)
            NetworkResult.Success(response)
        } catch (e :IOException){
            NetworkResult.Error("Server Not Found")
        }

    }
    suspend fun getCommentForArticle(slug: String?,token: String?) : NetworkResult<Response<CommentResponse>>{
        return try {
            val response = apiService.getCommentForArticle(slug, token)
            NetworkResult.Success(response)
        }
        catch (e :IOException){
            NetworkResult.Error("Server Not Found")
        }

    }
    suspend fun favouriteArticle(slug: String?, token: String?) = apiService.favouriteArticle(slug, token)
    suspend fun unFavouriteArticle(slug: String?,token: String?) : Response<FavouriteResponse>{
        val response = apiService.unFavouriteArticle(slug, token)
        try {
            if (slug != null) {
                authDao.deleteSpecificArticle(FAVOURITE,slug)
            }
        }
        catch (e: IOException){

        }
        return response
    }
    suspend fun updateArticle(slug: String?,token: String?,articleRequest: ArticleRequest) = apiService.updateArticle(slug, token, articleRequest)
    suspend fun deleteArticle(slug: String?,token: String?) = apiService.deleteArticle(slug, token)
    suspend fun postComment(slug: String?,token: String?,commentRequest: CommentRequest) = apiService.postComment(slug,token, commentRequest)
    suspend fun deleteComment(slug: String?,commentId:Int,token: String?) = apiService.deleteComment(slug, commentId,token)
    suspend fun followUser(celebName: String?,token: String?,user: UserXX) = apiService.followUser(celebName, token, user)

    suspend fun getTags() = apiService.getTags()
    suspend fun unFollowUser(celebName: String?,token: String?) : Response<FollowResponse>{
        val response = apiService.unFollowUser(celebName, token)
        try {
            authDao.deleteArticle(MYFEED)
        }
        catch (e: IOException){

        }
        return response
    }


    private suspend fun returnArticleResponse(token: String?,type: String,limit: Int?=null,author: String?=null):NetworkResult<ArticlesResponse>{
        return try {
            var response :Response<ArticlesResponse>? = null
            when(type){
                MYFEED-> response = apiService.getMyFeed(token)
                FAVOURITE -> response = apiService.getMyFavouritedArticles(token,limit,author)
                MINE -> response = apiService.getMyArticles(token,limit,author)
                GLOBAL -> response = apiService.getArticles(token)
            }

            if(response!!.isSuccessful){
                //Store in offline database
                insertArticleInOffline(response.body()!!,type)
                val articleResponse = getArticleFromOffline(type)
                NetworkResult.Success(articleResponse)
            } else{
                val articleResponse = getArticleFromOffline(type)
                NetworkResult.Error(response.message(),articleResponse)
            }

        } catch (e: IOException){
            val articleResponse = getArticleFromOffline(type)
            NetworkResult.Error("Check Internet Connection",articleResponse)
        }
    }

    private suspend fun insertArticleInOffline(article:ArticlesResponse,type:String){

        for(eachArticle in article.articles){
            val tags = getTagsForOfflineInsert(eachArticle)
            val author = eachArticle.author
            val offlineArticle = OfflineArticle(type,
                author.username,
                eachArticle.body,
                eachArticle.createdAt,
                eachArticle.description,
                eachArticle.favorited,
                eachArticle.favoritesCount,
                eachArticle.slug,
                eachArticle.title,
                eachArticle.updatedAt,
                tags
            )
            authDao.insertAuthor(author)
            authDao.insertArticle(offlineArticle)
        }
    }

    private suspend fun getArticleFromOffline(type: String): ArticlesResponse {
        val listArticle = mutableListOf<Article>()

        val offlineArticleResponse: List<OfflineArticleResponse> = authDao.getInsertedArticles(type)
        for (article in offlineArticleResponse) {
            val tags = getTagsFromOfflineInsertedData(article)
            listArticle.add(
                Article(
                    Author(article.bio, article.following, article.image, article.username),
                    article.body,
                    article.createdAt,
                    article.description,
                    article.favorited,
                    article.favoritesCount,
                    article.slug,
                    tags,
                    article.title,
                    article.updatedAt
                )
            )
        }

        return ArticlesResponse(listArticle, listArticle.count())
    }

    private fun getTagsFromOfflineInsertedData(article: OfflineArticleResponse): List<String> {
        val list = article.tagList.split(",").toList()
        return list.subList(0,list.size-1)
    }

    private fun getTagsForOfflineInsert(article: Article): String {
        var tag = ""
        for(eachTag in article.tagList) tag = "$tag$eachTag,"
        return tag
    }
}
