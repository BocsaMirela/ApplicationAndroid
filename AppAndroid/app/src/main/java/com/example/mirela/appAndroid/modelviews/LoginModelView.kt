package com.example.mirela.appAndroid.modelviews

import com.example.mirela.appAndroid.POJO.LoginResponse
import com.example.mirela.appAndroid.POJO.User
import com.example.mirela.appAndroid.api.UserAPI
import com.google.gson.GsonBuilder

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginModelView {
    fun loginUser(username: String, password: String): Call<LoginResponse> {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        val retrofit = Retrofit.Builder()
                .baseUrl(UserAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        val api = retrofit.create(UserAPI::class.java)
        return api.loginUser(User(username, password))
    }

    companion object {
        private val TAG = LoginModelView::class.java.canonicalName
    }
}