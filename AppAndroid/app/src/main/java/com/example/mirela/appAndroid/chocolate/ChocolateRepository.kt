package com.example.mirela.appAndroid.bus

import android.arch.lifecycle.LiveData

class ChocolateRepository private constructor(private val chocolateDAO: ChocolateDAO) {
    private val chocolateLiveData: LiveData<Chocolate>? = null

    val allChocolates: List<Chocolate>
        get() = chocolateDAO.allBusses

    fun updateChocolate(number: Int?, added: Boolean?) {
        chocolateDAO.updateBus(number, added)
    }


    fun addChocolate(chocolate: Chocolate) {
        chocolateDAO.insert(chocolate)
    }

    fun getBusses(howMany: Int, start: Int): List<Chocolate> {
        return chocolateDAO.getBusses(howMany, start)
    }

    companion object {
        private var instance: ChocolateRepository? = null

        fun getInstance(chocolateDAO: ChocolateDAO): ChocolateRepository {
            if (instance == null) {
                instance = ChocolateRepository(chocolateDAO)
            }
            return instance as ChocolateRepository
        }
    }
}
