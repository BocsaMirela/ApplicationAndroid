package com.example.mirela.appAndroid.bus

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context


class ChocolateController(context: Context) : ViewModel() {
    private val chocolateRepository: ChocolateRepository

    val allBusses: List<Chocolate>
        get() = chocolateRepository.allChocolates

    init {

        chocolateRepository = ChocolateRepository.getInstance(ChocolateDatabase.getAppDatabase(context).busDAO())
    }

    fun createBus(chocolate: Chocolate) {
        chocolateRepository.addChocolate(chocolate)
    }

    fun getBusses(howMany: Int, start: Int): List<Chocolate> {
        return chocolateRepository.getBusses(howMany, start)
    }

    fun updateBus(number: Int?, added: Boolean?) {
        chocolateRepository.updateChocolate(number, added)
    }


    class Factory(ctxt: Context) : ViewModelProvider.Factory {
        private val ctxt: Context

        init {
            this.ctxt = ctxt.applicationContext
        }

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChocolateController(ctxt) as T
        }
    }
}
