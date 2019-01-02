package com.example.mirela.appAndroid.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mirela.appAndroid.POJO.Chocolate
import com.example.mirela.appAndroid.utils.ChocolateAdapter

class MyBroadcastReceiver(private val adapter: ChocolateAdapter) : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        val chocolate: Chocolate? = intent?.getParcelableExtra("chocolate")
        val action = intent?.getStringExtra("action")
        Log.e("receive", chocolate.toString())
        if (chocolate != null) {
            when (action) {
                "DELETE" -> {
                    adapter.removeItem(chocolate)
                }
                "UPDATE" -> {
                    adapter.removeItem(chocolate)
                    adapter.insertItem(chocolate)
                }
            }

        }

    }

}