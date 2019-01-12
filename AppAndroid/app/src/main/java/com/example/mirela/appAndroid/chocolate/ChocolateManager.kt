package com.example.mirela.appAndroid.chocolate

import com.example.mirela.appAndroid.api.ChocolateAPI
import com.example.mirela.appAndroid.networking.RetrofitFactory
import retrofit2.Call

class ChocolateManager(token: String) {
    private val api = RetrofitFactory(token).getRetrofitInstance().create(ChocolateAPI::class.java)

    fun getAllChocolatesServer(): Call<List<Chocolate>> {
        return api.getChocolates()
    }

    fun addChocolateServer(chocolate: Chocolate): Call<Chocolate> {
        return api.addChocolate(chocolate)
    }

    fun deleteChocolateServer(id: Int, userId: Int?): Call<Chocolate> {
        return api.deleteChocolate(id, userId)
    }

    fun updateChocolateServer(chocolate: Chocolate, userId: Int?): Call<Chocolate> {
        return api.updateChocolate(chocolate.id.toInt(), userId, chocolate)
    }

    fun check(): Call<String> {
        return api.check()
    }

}