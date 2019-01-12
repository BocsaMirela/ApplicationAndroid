package com.example.mirela.appAndroid.api

import com.example.mirela.appAndroid.chocolate.Chocolate
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.DELETE
import retrofit2.http.PUT


interface ChocolateAPI {

    @GET("all")
    fun getChocolates(): Call<List<Chocolate>>

    @POST(".")
    fun addChocolate(@Body chocolate: Chocolate): Call<Chocolate>

    @PUT("{id}/{userId}")
    fun updateChocolate(@Path("id") id: Int, @Path("userId") userId: Int?,@Body chocolate: Chocolate): Call<Chocolate>

    @DELETE("{id}/{userId}")
    fun deleteChocolate(@Path("id") id: Int, @Path("userId") userId: Int?): Call<Chocolate>

    @GET("check")
    fun check() :Call<String>

    companion object {
        val BASE_URL = AppResource.BASE_URL
    }
}
