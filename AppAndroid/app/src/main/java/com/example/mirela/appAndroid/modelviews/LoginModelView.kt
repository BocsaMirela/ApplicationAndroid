package com.example.user.myapplication.modelviews

import com.example.user.myapplication.api.UserResource
import com.example.user.myapplication.user.User

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginModelView {

    fun addUser(user: User): Call<User> {
        val retrofit = Retrofit.Builder()
                .baseUrl(UserResource.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(UserResource::class.java)
        return api.addUser(user)
    }

    fun loginUser(username: String, password: String): Call<Boolean> {
        val retrofit = Retrofit.Builder()
                .baseUrl(UserResource.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(UserResource::class.java)
        return api.loginUser(username, password)
    }

    companion object {
        private val TAG = LoginModelView::class.java.canonicalName
    }
}