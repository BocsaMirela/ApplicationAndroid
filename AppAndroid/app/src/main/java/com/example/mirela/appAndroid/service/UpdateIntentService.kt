package com.example.mirela.appAndroid.service

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import com.example.mirela.appAndroid.POJO.Chocolate
import com.example.mirela.appAndroid.networking.Tasks
import com.example.mirela.appAndroid.utils.ChocolatesDatabaseAdapter

class UpdateIntentService : IntentService("UpdateIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        intent?.apply {
            val item = intent.getParcelableExtra<Chocolate>("item")
            val rez = ChocolatesDatabaseAdapter.updateChocolate(item.id, item.description, item.date, item.imagePath,item.lastUpdateDate)
            if (rez > 0) {
                Log.d("send", "sending update")
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.putExtra("chocolate", item)
                sendIntent.putExtra("action", "UPDATE")
                if(Tasks.UpdateTask().execute(item).get()){
                }else{
                    Toast.makeText(applicationContext,"Something went wrong or no connection", Toast.LENGTH_LONG).show();
                }
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(sendIntent)
            }
        }

    }
}