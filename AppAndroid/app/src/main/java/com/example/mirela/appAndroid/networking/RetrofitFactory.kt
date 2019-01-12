package com.example.mirela.appAndroid.networking

import com.example.mirela.appAndroid.api.ChocolateAPI
import com.google.gson.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit

class RetrofitFactory(private val token: String) {
    private val okHttpClientBuilder = OkHttpClient.Builder()
        .connectTimeout(3000, TimeUnit.MILLISECONDS)
        .writeTimeout(3000, TimeUnit.MILLISECONDS)
        .readTimeout(3000, TimeUnit.MILLISECONDS)


    init {
        okHttpClientBuilder.addInterceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        }.build()
    }

    fun getRetrofitInstance(): Retrofit {

        val builder = GsonBuilder()
            .registerTypeAdapter(Date::class.java, object : JsonDeserializer<Date> {
                @Throws(JsonParseException::class)
                override fun deserialize(
                    jsonElement: JsonElement,
                    type: Type,
                    context: JsonDeserializationContext
                ): Date {
                    return Date(jsonElement.asJsonPrimitive.asLong)
                }
            })
            .create()
        return Retrofit.Builder().client(okHttpClientBuilder.build())
            .baseUrl(ChocolateAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(builder))
            .build()
    }

}