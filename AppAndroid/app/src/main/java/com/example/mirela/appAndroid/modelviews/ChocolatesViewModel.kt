package com.example.mirela.appAndroid.modelviews

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.util.Log
import com.example.mirela.appAndroid.chocolate.*

import retrofit2.Call


class ChocolatesViewModel(token: String, application: Application) : ViewModel() {

    private val chocolateRepository: ChocolateRepository =
        ChocolateRepository.getInstance(ChocolateDatabase.getAppDatabase(application).chocolateDAO())

    private val chocolateManager = ChocolateManager(token)

    var items: MutableLiveData<List<Chocolate>> = MutableLiveData()

    fun getAllChocolates(): LiveData<List<Chocolate>> {
        return chocolateRepository.getAllChocolates()
    }

    fun getAllToInsert(): List<Chocolate> {
        return chocolateRepository.getAllToInsert()
    }

    fun getAllToUpdate(): List<Chocolate> {
        return chocolateRepository.getAllToUpdate()
    }

    fun getChocolatesFromServer(): Call<List<Chocolate>> {
        return chocolateManager.getAllChocolatesServer()
    }

    fun saveChocolate(chocolate: Chocolate): Long {
        return chocolateRepository.addChocolate(chocolate)
    }

    fun updateChocolate(chocolate: Chocolate) {
        chocolateRepository.updateChocolate(chocolate)
    }

    fun deleteChocolate(chocolate: Chocolate) {
        chocolateRepository.deleteChocolate(chocolate)
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

    fun check(): Call<String> {
        return chocolateManager.check()
    }

    class Factory(private val token: String, private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChocolatesViewModel(token, application) as T
        }
    }

}
