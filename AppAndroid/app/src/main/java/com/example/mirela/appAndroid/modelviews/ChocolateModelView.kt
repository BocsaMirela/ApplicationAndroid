package com.example.mirela.appAndroid.modelviews

import com.example.mirela.appAndroid.api.ChocolateResource
import com.example.mirela.appAndroid.chocolate.Chocolate

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BusModelView {
    val busses: Call<List<Chocolate>>
        get() {
            val retrofit = Retrofit.Builder()
                    .baseUrl(ChocolateResource.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            val api = retrofit.create(ChocolateResource::class.java)
            return api.chocolates
        }

    fun addBus(chocolate: Chocolate): Call<Chocolate> {
        val retrofit = Retrofit.Builder()
                .baseUrl(ChocolateResource.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(ChocolateResource::class.java)
        return api.addChocolate(chocolate)
    }
}
