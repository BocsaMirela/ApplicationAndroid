package com.example.mirela.appAndroid.networking

import android.os.AsyncTask
import com.example.mirela.appAndroid.POJO.Chocolate
import com.example.mirela.appAndroid.POJO.User
import com.google.gson.GsonBuilder

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.IOException

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.util.*

object Tasks {
    val client = OkHttpClient()
    val IP = "http://10.152.2.148:8080"
    val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()

    class IsLoggedTask : AsyncTask<Void, Void, User>() {
        override fun doInBackground(vararg params: Void): User? {
            val req = Request.Builder()
                .url("$IP/user/get")
                .get()
                .build()
            try {
                val response = client.newCall(req).execute()
                if (response.isSuccessful) {
                    val obj = JSONObject(response.body()!!.string())
                    return User(obj.getString("username"), "")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }
    }

    class LoginTask : AsyncTask<User, Void, Boolean>() {
        override fun doInBackground(vararg params: User): Boolean? {
            val req = Request.Builder()
                .url("$IP/user/login")
                .post(RequestBody.create(MediaType.parse("application/json"), gson.toJson(params[0])))
                .build()
            try {
                val response = client.newCall(req).execute()
                return response.isSuccessful
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return false
        }
    }

    class GetAllTask : AsyncTask<Void, Void, List<Chocolate>>() {
        override fun doInBackground(vararg params: Void): List<Chocolate>? {
            var objects: MutableList<Chocolate>? = null
            val req = Request.Builder()
                .url("$IP/chocolate/getall")
                .get()
                .build()
            try {
                val response = client.newCall(req).execute()
                objects = ArrayList()
                val arr = JSONArray(response.body()!!.string())
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    objects.add(
                        Chocolate(
                            obj.getLong("id"),
                            obj.getString("description"),
                            Date(obj.getLong("date")),
                            obj.getString("imagePath"),
                            Date(obj.getLong("lastUpdateDate")),
                            obj.getString("username")
                        )
                    )
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return objects
        }
    }

    class AddTask : AsyncTask<Chocolate, Void, Boolean>() {
        override fun doInBackground(vararg params: Chocolate): Boolean? {
            val json = gson.toJson(params[0])
            val req = Request.Builder()
                .url("$IP/chocolate/add")
                .post(RequestBody.create(MediaType.parse("application/json"), json))
                .build()
            try {
                val response = client.newCall(req).execute()
                return response.isSuccessful
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }

        }
    }

    class RemoveTask : AsyncTask<Int, Void, Boolean>() {
        override fun doInBackground(vararg params: Int?): Boolean {
            val req = Request.Builder()
                .url(IP + "/chocolate/remove/" + params[0])
                .get()
                .build()
            try {
                val response = client.newCall(req).execute()
                return response.isSuccessful
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
        }

    }

    class UpdateTask : AsyncTask<Chocolate, Void, Boolean>() {
        override fun doInBackground(vararg params: Chocolate): Boolean? {
            val json = gson.toJson(params[0])
            val req = Request.Builder()
                .url(IP + "/chocolate/update/" + params[0].id)
                .post(RequestBody.create(MediaType.parse("application/json"), json))
                .build()
            try {
                val response = client.newCall(req).execute()
                return response.isSuccessful

            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }

        }
    }

}

