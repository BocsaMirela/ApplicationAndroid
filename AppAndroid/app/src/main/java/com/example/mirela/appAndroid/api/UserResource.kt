package com.example.user.myapplication.api

import com.example.user.myapplication.user.User

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface UserResource {

    @POST("addUser")
    fun addUser(@Body user: User): Call<User>

    @POST("login")
    fun loginUser(@Query("username") username: String, @Query("password") password: String): Call<Boolean>

    companion object {
        val BASE_URL = AppResource.BASE_URL + "users/"
    }
}
