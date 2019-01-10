package com.example.user.myapplication.api

import com.example.mirela.appAndroid.api.AppResource
import com.example.user.myapplication.bus.Bus

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BusResource {

    @get:GET("busses")
    val busses: Call<List<Bus>>

    @POST("busses")
    fun addBus(@Body bus: Bus): Call<Bus>

    @GET("busses/bussesPaginated")
    fun getBussesPaginated(@Query("start") start: Int?, @Query("howMany") howMany: Int?): Call<List<Bus>>

    companion object {
        val BASE_URL = AppResource.BASE_URL
    }
}
