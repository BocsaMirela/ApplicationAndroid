package com.example.mirela.appAndroid.networking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class GcmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
//        // Get a GCM object instance
//        val gcm: GoogleCloudMessaging = GoogleCloudMessaging.getInstance(context)
//        // Get the type of GCM message
//        val messageType: String? = gcm.getMessageType(intent)
//
//        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE == messageType
//                && intent.getBooleanExtra("com.example.restaurant.SYNC_REQ", false)) {
//            ContentResolver.requestSync(Account(ACCOUNT, ACCOUNT_TYPE), AUTHORITY, null)
//        }
    }
    companion object {
        private val TAG = GcmBroadcastReceiver::class.java.simpleName
    }
}