package com.example.conduit.viewmodel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conduit.data.remote.NetworkResult
import com.example.conduit.model.*
import com.example.conduit.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {


    val signupUser = MutableLiveData<UserResponse>()
    val loginUser = MutableLiveData<UserResponse>()
    val currentUser = MutableLiveData<NetworkResult<UserResponse>>()
    val updateUser = MutableLiveData<UserResponse>()

    fun signUpUser(userRequestRegister: UserRequestRegister) = viewModelScope.launch {
        repository.signUpUser(userRequestRegister).let {
            if(it.isSuccessful){
                signupUser.postValue(it.body())
            }
        }
    }

    fun loginUser(userRequestLogin: UserRequestLogin) = viewModelScope.launch {
        repository.loginUser(userRequestLogin).let {
            if(it.isSuccessful){
                loginUser.postValue(it.body())
            }
        }
    }

    fun getCurrentUser(token:String?) = viewModelScope.launch {
        currentUser.value = NetworkResult.Loading()
        repository.getCurrentUser(token).let{
            when(it){
                is NetworkResult.Success -> {
                    currentUser.value = NetworkResult.Success(it.data!!)
                }
                is NetworkResult.Error -> {
                    currentUser.value = NetworkResult.Error(it.message,it.data)
                }
            }
        }
    }

    fun updateUser(token: String?,user: UpdateRequestUser) = viewModelScope.launch {
        repository.updateUser(token,user).let {
            if(it.isSuccessful){
                updateUser.postValue(it.body())
            }
        }
    }

}