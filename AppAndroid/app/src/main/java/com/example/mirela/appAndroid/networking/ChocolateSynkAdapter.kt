package com.example.mirela.appAndroid.networking

import android.accounts.Account
import android.content.*
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import com.example.mirela.appAndroid.POJO.Chocolate
import com.example.mirela.appAndroid.utils.DbHelper

class ChocolateSyncAdapter : AbstractThreadedSyncAdapter {

    private var sPreferences: SharedPreferences? = null
    private var restService: Tasks? = null
    private var contentResolver: ContentResolver? = null

    private fun init(c: Context) {
        sPreferences = PreferenceManager.getDefaultSharedPreferences(c)

        restService = Tasks
        contentResolver = c.getContentResolver()
    }

    constructor(context: Context, autoInitialize: Boolean) : super(context, autoInitialize) {
        init(context)
    }

    constructor(context: Context, autoInitialize: Boolean, allowParallelSyncs: Boolean) : super(context, autoInitialize, allowParallelSyncs) {
        init(context)
    }

    private fun menuToContentValues(chocolate: Chocolate): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(DbHelper.UID, chocolate.id)
        contentValues.put(DbHelper.COLUMN_DESCRIPTION, chocolate.description)
        contentValues.put(DbHelper.COLUMN_IMAGE_PATH, chocolate.imagePath)
        contentValues.put(DbHelper.COLUMN_DATE, chocolate.date.time)
        return contentValues
    }

    override fun onPerformSync(account: Account, bundle: Bundle, s: String,
                               contentProviderClient: ContentProviderClient, syncResult: SyncResult) {
        try {
            val localDBVersion = sPreferences!!.getInt("DB_VERSION", 0)

            Log.d(TAG, "onPerformSync: localDBversion " + Integer.toString(localDBVersion) + " serverDBVersion " + Integer.toString(1))
            if (true) {
                // fetch menu items from server and update the local DB
                val contentValList = ArrayList<ContentValues>()
                for (menuItem in Tasks.GetAllTask().get()) {
                    val contentValues = menuToContentValues(menuItem)
                    contentValues.putNull("id")
                    contentValList.add(contentValues)
                }
                val deletedRows = contentProviderClient.delete(ChocolateContentProvider.CONTENT_URI, null, null)
                val insertedRows = contentProviderClient.bulkInsert(ChocolateContentProvider.CONTENT_URI, contentValList.toArray(arrayOfNulls<ContentValues>(contentValList.size)))
                Log.d(TAG, "completed sync: deleted " + Integer.toString(deletedRows) + " inserted " + Integer.toString(insertedRows))

                // update local db version
                sPreferences!!.edit().putInt("DB_VERSION", 1).commit()

                // notify content provider listeners
                contentResolver!!.notifyChange(ChocolateContentProvider.CONTENT_URI, null)
            }

        } catch (e: Exception) {
            Log.d(TAG, "Exception in sync", e)
            syncResult.hasHardError()
        }

    }

    companion object {
        private val TAG = "ChocolateSyncAdapter"
    }
}
