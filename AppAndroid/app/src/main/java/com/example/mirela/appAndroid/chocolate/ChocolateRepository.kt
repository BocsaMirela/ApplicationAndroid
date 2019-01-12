package com.example.mirela.appAndroid.chocolate

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import android.util.Log
import java.io.Console
import java.lang.Exception

class ChocolateRepository(private val chocolateDAO: ChocolateDAO) {

    fun getAllChocolates(): LiveData<List<Chocolate>> {
        val r = LoadAsyncTask(chocolateDAO).execute().get()
        Log.e(" chocolate all ", r.value?.size.toString())
        return r
    }

    fun getAllToInsert(): List<Chocolate> {
        return LoadNewInsertedAsyncTask(chocolateDAO).execute().get()
    }

    fun getAllToUpdate(): List<Chocolate> {
        return LoadUpdatedAsyncTask(chocolateDAO).execute().get()
    }

    fun updateChocolate(chocolate: Chocolate) {
        UpdateAsyncTask(chocolateDAO).execute(chocolate)
    }

    fun deleteChocolate(chocolate: Chocolate) {
        DeleteAsyncTask(chocolateDAO).execute(chocolate)
    }

    fun addChocolate(chocolate: Chocolate): Long {
        return AddAsyncTask(chocolateDAO).execute(chocolate).get()
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

    private class LoadAsyncTask(val chocolateDAOTask: ChocolateDAO) :
        AsyncTask<Void, Void, LiveData<List<Chocolate>>>() {
        override fun doInBackground(vararg params: Void?): LiveData<List<Chocolate>> {
            val choco = chocolateDAOTask.getChocolates()
            Log.e(" from db all ", choco.value?.size.toString())
            return choco
        }

    }

    private class AddAsyncTask(val chocolateDAOTask: ChocolateDAO) :
        AsyncTask<Chocolate, Void, Long>() {
        override fun doInBackground(vararg params: Chocolate?): Long {
            return try {
                chocolateDAOTask.insert(params[0]!!)
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        }
    }

    private class UpdateAsyncTask(val chocolateDAOTask: ChocolateDAO) :
        AsyncTask<Chocolate, Void, Boolean>() {
        override fun doInBackground(vararg params: Chocolate?): Boolean {
            return try {
                chocolateDAOTask.update(params[0]!!)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private class DeleteAsyncTask(val chocolateDAOTask: ChocolateDAO) :
        AsyncTask<Chocolate, Void, Boolean>() {
        override fun doInBackground(vararg params: Chocolate?): Boolean {
            return try {
                chocolateDAOTask.delete(params[0]!!)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private class LoadNewInsertedAsyncTask(val chocolateDAOTask: ChocolateDAO) :
        AsyncTask<Void, Void, List<Chocolate>>() {
        override fun doInBackground(vararg params: Void?): List<Chocolate> {
            Log.e(" from db ", "called")
            return chocolateDAOTask.getNewInsertedChocolates()
        }

    }

    private class LoadUpdatedAsyncTask(val chocolateDAOTask: ChocolateDAO) :
        AsyncTask<Void, Void, List<Chocolate>>() {
        override fun doInBackground(vararg params: Void?): List<Chocolate> {
            Log.e(" from db ", "called")
            return chocolateDAOTask.getUpdatedChocolates()
        }

    }

}
