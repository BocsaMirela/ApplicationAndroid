package com.example.mirela.appAndroid.service

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import com.example.mirela.appAndroid.POJO.Chocolate
import com.example.mirela.appAndroid.networking.Tasks
import com.example.mirela.appAndroid.utils.ChocolatesDatabaseAdapter
import com.example.mirela.appAndroid.utils.DeleteItemsDBAdapter

class DeleteIntentService : IntentService("DeleteIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        val chocolate = intent?.getParcelableExtra<Chocolate>("item")
        chocolate?.also {
            if (Tasks.RemoveTask().execute(it.id.toInt()).get()) {
                Toast.makeText(applicationContext, "Chocolate deleted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Something went wrong or no connection! The chocolate will be deleted from your local data",
                    Toast.LENGTH_LONG
                ).show();
                DeleteItemsDBAdapter.insertDeletedItems(it.id.toInt(),it.username)
            }
            val rez = ChocolatesDatabaseAdapter.deleteChocolate(it.id)
            if (rez > 0) {
                Log.d("send", "sending delete")
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(Intent(Intent.ACTION_SEND).apply {
                    putExtra("chocolate", it)
                    putExtra("action", "DELETE")
                })
            }
        }
    }
}