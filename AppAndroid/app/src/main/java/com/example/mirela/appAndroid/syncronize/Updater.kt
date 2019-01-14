package com.example.mirela.appAndroid.syncronize

import android.content.Context
import com.example.mirela.appAndroid.chocolate.Chocolate
import com.example.mirela.appAndroid.modelviews.ChocolatesViewModel
import com.example.mirela.appAndroid.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

object Updater {

    fun tryToConnect(
        chocolatesViewModel: ChocolatesViewModel,
        deletedItemsDAO: DeletedItemsDAO,
        applicationContext: Context
    ) {
        Thread {
            while (true) {
                try {
                    Thread.sleep(4000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                chocolatesViewModel.check().enqueue(object : Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {
                    }

                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        syncronizeData(chocolatesViewModel, deletedItemsDAO, applicationContext)
                    }

                })
                chocolatesViewModel.getChocolatesFromServer().enqueue(object : Callback<List<Chocolate>> {
                    override fun onResponse(call: Call<List<Chocolate>>, response: Response<List<Chocolate>>) {
                        println(" m-am conectat" + response.body())
                        val chocos = response.body()
                        chocos?.also {
                            chocolatesViewModel.items.value = chocos
                        }
                    }

                    override fun onFailure(call: Call<List<Chocolate>>, t: Throwable) {
                    }
                })
            }
        }.start()
    }

    fun syncronizeData(
        chocolatesViewModel: ChocolatesViewModel,
        deletedItemsDAO: DeletedItemsDAO,
        applicationContext: Context
    ) {
        val newInsertedItems = chocolatesViewModel.getAllToInsert()
        val newUpdatedItems = chocolatesViewModel.getAllToUpdate()
        val newDeletedItems = deletedItemsDAO.getDeletedItems()

        val userId = SessionManager(applicationContext).userId?.toInt()

        newInsertedItems.forEach { chocolate ->
            Thread.sleep(100)
            chocolate.wasInserted = 1
            chocolatesViewModel.addChocolateServer(chocolate).enqueue(object : Callback<Chocolate> {
                override fun onResponse(call: Call<Chocolate>, response: Response<Chocolate>) {
                    chocolate.wasInserted = 1
                    chocolatesViewModel.updateChocolate(chocolate)
                }

                override fun onFailure(call: Call<Chocolate>, t: Throwable) {
                    chocolate.wasInserted = 0
                }

            })
        }

        newUpdatedItems.forEach { chocolate ->
            Thread.sleep(100)
            chocolate.wasUpdated = 1
            chocolatesViewModel.updateChocolateServer(chocolate, userId).enqueue(object : Callback<Chocolate> {
                override fun onResponse(call: Call<Chocolate>, response: Response<Chocolate>) {
                    chocolate.wasUpdated = 1
                    chocolatesViewModel.updateChocolate(chocolate)
                }

                override fun onFailure(call: Call<Chocolate>, t: Throwable) {
                    chocolate.wasUpdated = 0
                }

            })
        }

        newDeletedItems.forEach { item ->
            Thread.sleep(100)
            chocolatesViewModel.deleteChocolateServer(item.id.toInt(), item.userId)
                .enqueue((object : Callback<Chocolate> {
                    override fun onResponse(call: Call<Chocolate>, response: Response<Chocolate>) {
                        val choco = response.body()
                        deletedItemsDAO.delete(DeletedItem(item.id, item.userId))
                    }

                    override fun onFailure(call: Call<Chocolate>, t: Throwable) {
                    }

                }))
        }

    }

    fun syncronizeDataLocal(
        chocolatesViewModel: ChocolatesViewModel,
        serverData: List<Chocolate>
    ) {

        serverData.forEach { chocolate ->
            chocolatesViewModel.saveChocolate(chocolate)
        }

    }
}