package com.example.conduit.data.remote
import com.example.conduit.model.*
import retrofit2.Response
import retrofit2.http.*


interface ConduitApiService {


    @GET("articles")
    suspend fun getArticles(
        @Header ("Authorization") token:String?,
        @Query ("limit") limit:Int? = null
    ) : Response<ArticlesResponse>



    @GET("articles")
    suspend fun getMyArticles(
        @Header ("Authorization") token:String?,
        @Query ("limit") limit:Int? = null,
        @Query ("author") authorName: String?,
    ) : Response<ArticlesResponse>

    @GET("articles")
    suspend fun getMyFavouritedArticles(
        @Header ("Authorization") token:String?,
        @Query ("limit") limit:Int? = null,
        @Query ("favorited") authorName: String?,
    ) : Response<ArticlesResponse>


    @GET("articles/feed")
    suspend fun getMyFeed(
        @Header ("Authorization") token:String?,
    ) : Response<ArticlesResponse>



    @GET("articles/{slug}")
    suspend fun getSingleArticleBySlug(
        @Path("slug") slug:String?,
        @Header ("Authorization") token:String?
    ) : Response<SingleArticleResponseBySlug>



    @GET("articles/{slug}/comments")
    suspend fun getCommentForArticle(
        @Path("slug") slug:String?,
        @Header ("Authorization") token:String?
    ) : Response<CommentResponse>


    @POST("articles")
    suspend fun createArticle(
        @Header ("Authorization") token:String?,
        @Body articleRequest: ArticleRequest
    ) : Response<ArticleAddResponse>


    @POST("articles/{slug}/favorite")
    suspend fun favouriteArticle(
        @Path("slug") slug:String?,
        @Header ("Authorization") token:String?
    ) : Response<FavouriteResponse>


    @DELETE("articles/{slug}/favorite")
    suspend fun unFavouriteArticle(
        @Path("slug") slug:String?,
        @Header ("Authorization") token:String?
    ) : Response<FavouriteResponse>

    @PUT("articles/{slug}")
    suspend fun updateArticle(
        @Path("slug") slug:String?,
        @Header ("Authorization") token:String?,
        @Body articleRequest: ArticleRequest
    ) : Response<ArticlesResponse>

    @DELETE("articles/{slug}")
    suspend fun deleteArticle(
        @Path("slug") slug:String?,
        @Header ("Authorization") token:String?
    ) : Response<ArticlesResponse>


    @POST("articles/{slug}/comments")
    suspend fun postComment(
        @Path("slug") slug:String?,
        @Header ("Authorization") token:String?,
        @Body commentRequest: CommentRequest
    ) : Response<CommentPostResponse>

    @DELETE("articles/{slug}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("slug") slug:String?,
        @Path("commentId") commentId:Int,
        @Header ("Authorization") token:String?
    ) : Response<CommentPostResponse>


    @POST("profiles/{username}/follow")
    suspend fun followUser(
        @Path("username") celebName: String?,
        @Header ("Authorization") token:String?,
        @Body user: UserXX
    ) : Response<FollowResponse>

    @DELETE("profiles/{username}/follow")
    suspend fun unFollowUser(
        @Path("username") celebName: String?,
        @Header ("Authorization") token:String?
    ) : Response<FollowResponse>


    @POST("users")
    suspend fun signUpUser(
        @Body userRequestRegister: UserRequestRegister
    ) : Response<UserResponse>



    @POST("users/login")
    suspend fun loginUser(
        @Body userRequestLogin: UserRequestLogin
    ) : Response<UserResponse>



    @GET("user")
    suspend fun getCurrentUser(
        @Header ("Authorization") token:String?
    ) : Response<UserResponse>

    @PUT("user")
    suspend fun upDateUser(
        @Header ("Authorization") token:String?,
        @Body user: UpdateRequestUser
    ) : Response<UserResponse>

}