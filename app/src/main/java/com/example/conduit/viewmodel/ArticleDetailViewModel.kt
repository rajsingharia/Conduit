package com.example.conduit.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conduit.data.remote.NetworkResult
import com.example.conduit.model.CommentResponse
import com.example.conduit.model.SingleArticleResponseBySlug
import com.example.conduit.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val repository: MainRepository
) :ViewModel(){

    val comments = MutableLiveData<CommentResponse>()
    val singleArticleBySlug = MutableLiveData<SingleArticleResponseBySlug>()
    val error = MutableLiveData<String>()

    fun getArticleBySlug(slug:String?,token: String?) = viewModelScope.launch {
        repository.getSingleArticleBySlug(slug,token).let{
            when(it){
                is NetworkResult.Success -> {
                    singleArticleBySlug.postValue(it.data?.body())
                }
                is NetworkResult.Error -> {
                    error.postValue(it.message)
                }
            }
        }
    }

    fun getCommentForArticle(slug: String?,token: String?) = viewModelScope.launch {
        repository.getCommentForArticle(slug,token).let {
            when(it){
                is NetworkResult.Success -> {
                    comments.postValue(it.data?.body())
                }
                is NetworkResult.Error -> {
                    error.postValue(it.message)
                }
            }
        }
    }

}