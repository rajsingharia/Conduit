package com.example.conduit.viewmodel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conduit.model.ArticleRequest
import com.example.conduit.model.ArticlesResponse
import com.example.conduit.repository.MainRepository
import com.example.conduit.data.remote.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateAndDeleteArticleViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {

    val updateArticleResponse : MutableLiveData<NetworkResult<ArticlesResponse>> = MutableLiveData()
    val deleteArticleResponse = MutableLiveData<String?>()


    fun updateArticle(slug: String?, token: String?,articleRequest: ArticleRequest) = viewModelScope.launch {
        updateArticleResponse.value = NetworkResult.Loading()
        repository.updateArticle(slug,token,articleRequest).let {
            if(it.isSuccessful){
                updateArticleResponse.value = NetworkResult.Success(it.body()!!)
            }
            else{
                updateArticleResponse.value = NetworkResult.Error(it.message())
            }
        }
    }

    fun deleteArticle(slug: String?, token: String?) = viewModelScope.launch {
        repository.deleteArticle(slug,token).let {
            if(it.code() == 204 || it.code() == 200){
                deleteArticleResponse.postValue(it.code().toString())
            }
        }
    }

}