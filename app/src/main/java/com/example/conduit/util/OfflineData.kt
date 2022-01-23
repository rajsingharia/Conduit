package com.example.conduit.util
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.example.conduit.R
import com.example.conduit.util.Constants.OFFLINE_TOKEN_KEY
import javax.inject.Inject


const val PREFERENCE_NAME="my_preference"

class OfflineData(activity: Activity) {

    private val sharedPreferences: SharedPreferences = activity.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)

    private val token = sharedPreferences.getString(OFFLINE_TOKEN_KEY,null)

    fun getUserToken():String? = token

    fun putUserToken(token: String?) {
        sharedPreferences.edit().putString(OFFLINE_TOKEN_KEY,token).apply()
    }
}

