package com.example.mirela.appAndroid.api

import com.example.mirela.appAndroid.POJO.LoginResponse
import com.example.mirela.appAndroid.POJO.User

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface UserAPI {

    @POST("auth")
    fun loginUser(@Body user:User): Call<LoginResponse>

    companion object {
        val BASE_URL = AppResource.BASE_URL_AUTH
    }
}
