package com.example.mirela.appAndroid.modelviews

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.example.mirela.appAndroid.api.ChocolateResource
import com.example.mirela.appAndroid.chocolate.Chocolate
import com.example.mirela.appAndroid.chocolate.ChocolateDatabase
import com.example.mirela.appAndroid.chocolate.ChocolateManager
import com.example.mirela.appAndroid.chocolate.ChocolateRepository
import com.example.mirela.appAndroid.networking.RetrofitFactory
import com.google.gson.GsonBuilder

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.JsonParseException
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonDeserializer
import java.lang.reflect.Type
import java.util.*
import okhttp3.OkHttpClient


class ChocolatesModelView(token: String, application: Application) : ViewModel() {

    private val chocolateRepository: ChocolateRepository =
        ChocolateRepository.getInstance(ChocolateDatabase.getAppDatabase(application).chocolateDAO())

    private val chocolateManager = ChocolateManager(token)

    var items: MutableLiveData<List<Chocolate>>

    init {
        items = chocolateRepository.allChocolates
    }

    val getChocolatesFromServer: Call<List<Chocolate>>
        get() {
            return chocolateManager.getAllChocolatesServer()
        }

    fun addChocolateServer(chocolate: Chocolate): Call<Chocolate> {
        return chocolateManager.addChocolateServer(chocolate)
    }

    fun deleteChocolateServer(id: Int, userId: Int?): Call<Chocolate> {
        return chocolateManager.deleteChocolateServer(id, userId)
    }

    fun updateChocolateServer(chocolate: Chocolate, userId: Int?): Call<Chocolate> {
        return chocolateManager.updateChocolateServer(chocolate, userId)
    }

}
