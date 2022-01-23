package com.example.conduit.viewmodel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conduit.model.ArticleAddResponse
import com.example.conduit.model.ArticleRequest
import com.example.conduit.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddArticleViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {

    val articleAdded = MutableLiveData<ArticleAddResponse>()

    fun createArticle(token:String?, articleRequest: ArticleRequest) = viewModelScope.launch {
        repository.createArticle(token,articleRequest).let {
            if(it.isSuccessful){
                articleAdded.postValue(it.body())
            }
        }
    }
}