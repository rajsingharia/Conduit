package com.example.conduit.viewmodel
import android.util.Log
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
    val error = MutableLiveData<String>()

    fun signUpUser(userRequestRegister: UserRequestRegister) = viewModelScope.launch {
        repository.signUpUser(userRequestRegister).let {
            if(it.isSuccessful){
                signupUser.postValue(it.body())
            }
            else if(it.code() == 422){
                error.postValue("username or email is already taken")
            }
        }
    }

    fun loginUser(userRequestLogin: UserRequestLogin) = viewModelScope.launch {
        repository.loginUser(userRequestLogin).let {
            if(it.isSuccessful){
                loginUser.postValue(it.body())
            }
            else if(it.code() == 403){
                error.postValue("email or password is invalid")
            }
        }
    }

    fun getCurrentUser(token:String?) = viewModelScope.launch {
        currentUser.value = NetworkResult.Loading()
        repository.getCurrentUser(token).let{
            currentUser.value = it
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