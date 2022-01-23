package com.example.conduit.viewmodel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conduit.model.*
import com.example.conduit.repository.MainRepository
import com.example.conduit.data.remote.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: MainRepository
) :ViewModel() {

    val globalArticle = MutableLiveData<NetworkResult<ArticlesResponse>>()
    val myArticle = MutableLiveData<NetworkResult<ArticlesResponse>>()
    val myFavouritedArticle = MutableLiveData<NetworkResult<ArticlesResponse>>()

    val myFeed = MutableLiveData<NetworkResult<ArticlesResponse>>()

    val favourted = MutableLiveData<FavouriteResponse>()
    val postedCommentResponse = MutableLiveData<CommentPostResponse>()
    val deleteCommentResponse = MutableLiveData<String?>()
    val followResponse = MutableLiveData<FollowResponse>()


    fun getArticles(token: String?) = viewModelScope.launch {
        globalArticle.value = NetworkResult.Loading()
        repository.getArticles(token).let {
            when(it){
                is NetworkResult.Success -> {
                    globalArticle.value = NetworkResult.Success(it.data!!)
                }

                is NetworkResult.Error -> {
                    globalArticle.value = NetworkResult.Error(it.message,it.data)
                }
            }
        }
    }

    //my articles in account section
    fun getMyArticles(token:String?,limit:Int?,authorName: String?) = viewModelScope.launch {
        repository.getMyArticles(token,authorName,limit).let {
            myArticle.value = NetworkResult.Loading()
            when(it){
                is NetworkResult.Success -> {
                    myArticle.value = NetworkResult.Success(it.data!!)
                }
                is NetworkResult.Error -> {
                    myArticle.value = NetworkResult.Error(it.message,it.data)
                }
            }
        }
    }

    fun getMyFavouriteArticles(token:String?, limit:Int?, authorName: String?) = viewModelScope.launch {
        repository.getMyFavouritedArticles(token,authorName,limit).let {
            myFavouritedArticle.value = NetworkResult.Loading()
            when(it){
                is NetworkResult.Success -> {
                    myFavouritedArticle.value = NetworkResult.Success(it.data!!)
                }
                is NetworkResult.Error -> {
                    myFavouritedArticle.value = NetworkResult.Error(it.message,it.data)
                }
                is NetworkResult.Loading -> {
                    myFavouritedArticle.value = NetworkResult.Loading()
                }
            }
        }
    }

    fun getMyFeed(token: String?) = viewModelScope.launch {
        repository.getMyFeed(token).let{

            myFeed.value = NetworkResult.Loading()
            when(it){
                is NetworkResult.Success -> {
                    myFeed.value = NetworkResult.Success(it.data!!)
                }
                is NetworkResult.Error -> {
                    myFeed.value = NetworkResult.Error(it.message,it.data)
                }
            }
        }
    }



    fun favouriteArticle(slug: String?, token: String?) = viewModelScope.launch {
        repository.favouriteArticle(slug,token).let {
            if(it.isSuccessful){
                favourted.postValue(it.body())
            }
        }
    }

    fun unFavouriteArticle(slug: String?, token: String?) = viewModelScope.launch {
        repository.unFavouriteArticle(slug,token).let {
            if(it.isSuccessful){
                favourted.postValue(it.body())
            }
        }
    }

    fun postComment(slug: String?,token: String?,commentRequest: CommentRequest) = viewModelScope.launch {
        repository.postComment(slug,token,commentRequest).let{
            if(it.isSuccessful){
                postedCommentResponse.postValue(it.body())
            }
        }
    }

    fun deleteComment(slug: String?,commentId:Int,token: String?) = viewModelScope.launch {
        repository.deleteComment(slug,commentId,token).let{
            if(it.code() == 204 || it.code() == 200){
                deleteCommentResponse.postValue(it.code().toString())
            }
        }
    }

    fun followUser(celebName: String?,token: String?,user: UserXX) = viewModelScope.launch {
        repository.followUser(celebName,token, user).let {
            if(it.isSuccessful){
                followResponse.postValue(it.body())
            }
        }
    }

    fun unFollowUser(celebName: String?,token: String?) = viewModelScope.launch {
        repository.unFollowUser(celebName,token).let {
            if(it.isSuccessful){
                followResponse.postValue(it.body())
            }
        }
    }





}