package com.example.mirela.appAndroid

import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.mirela.appAndroid.POJO.Chocolate
import com.example.mirela.appAndroid.activities.AddActivity
import com.example.mirela.appAndroid.activities.UpdateActivity
import com.example.mirela.appAndroid.networking.ChocolateContentProvider
import com.example.mirela.appAndroid.networking.Tasks
import com.example.mirela.appAndroid.utils.*
import java.text.SimpleDateFormat
import java.util.*

// The authority for the sync adapter's content provider
const val AUTHORITY = "com.example.android.datasync.provider"
// An account type, in the form of a domain name
const val ACCOUNT_TYPE = "example.com"
// The account name
const val ACCOUNT = "dummyaccount"

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var adapter: ChocolateAdapter
    private lateinit var mAccount: Account


    private val ADD_NEW_PHOTO_REQUEST = 1
    private val UPDATE_PHOTO_REQUEST = 2
//    private lateinit var myBroadcastReceiver: BroadcastReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = DbHelper(this)
        ChocolatesDatabaseAdapter.setDbHelper(dbHelper)

        recyclerView = this.findViewById(R.id.savedChocolates)

        viewManager = GridLayoutManager(this@MainActivity, 1)
        recyclerView.layoutManager = viewManager
        adapter = ChocolateAdapter()
        recyclerView.adapter = adapter

        val itemTouchHelper =
            ItemTouchHelper(
                SwipeToDeleteCallback(
                    this@MainActivity,
                    adapter,
                    findViewById(R.id.coordinatorLayout)
                )
            )
        itemTouchHelper.attachToRecyclerView(recyclerView)

        LoadAsyncTask(adapter, ChocolatesDatabaseAdapter).execute()

//        mAccount = createSyncAcount()
    }

    private fun createSyncAcount(): Account {
        val accountManager = getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        return Account(ACCOUNT, ACCOUNT_TYPE).also { newAccount ->
            accountManager.addAccountExplicitly(newAccount, null, null)
            ContentResolver.setSyncAutomatically(newAccount, ChocolateContentProvider.AUTHORITY, true);
            if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            } else {
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADD_NEW_PHOTO_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                data?.apply {
                    val description = data.getStringExtra("description")
                    val dataR = data.getStringExtra("data")
                    val time = data.getStringExtra("time")
                    val imagePath = data.getStringExtra("imagePath")
                    val date = SimpleDateFormat("dd-MM-yyyy HH:mm").parse("$dataR $time")
                    val dateUpdate = Date()

                    val newId = ChocolatesDatabaseAdapter.insertChocolate(description, date, imagePath, dateUpdate)

                    if (newId > 0) {
                        val item = Chocolate(newId, description, date, imagePath, dateUpdate)
                        adapter.insertItem(item)
                        if (Tasks.AddTask().execute(item).get()) {
                            Toast.makeText(this@MainActivity, "Add was done", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong or no connection! The new chocolate will be saved in your local data",
                                Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                }
            }
        } else if (requestCode == UPDATE_PHOTO_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val chocolate: Chocolate? = intent?.getParcelableExtra("item")
                chocolate?.apply {
                    adapter.removeItem(chocolate)
                    adapter.insertItem(chocolate)
                    adapter.notifyDataSetChanged()
                }
                if (Tasks.UpdateTask().execute(chocolate).get()) {
                    Toast.makeText(this@MainActivity, "Update was done", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Something went wrong or no connection! The new chocolate will be updated in your local data",
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.update -> {
                updateChocolate(item.order)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }


    private fun updateChocolate(id: Int) {
        val intent = Intent(this, UpdateActivity::class.java)
        val chocolate = adapter.getChocolatesList()[id]
        intent.putExtra("description", chocolate.description)
        intent.putExtra("imagePath", chocolate.imagePath)
        intent.putExtra("data", chocolate.date.time)
        intent.putExtra("id", chocolate.id)
        startActivityForResult(intent, UPDATE_PHOTO_REQUEST)
    }

    override fun onStop() {
        super.onStop()
//        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(myBroadcastReceiver)

    }

    fun onAdd(v: View) {
        val intent = Intent(this, AddActivity::class.java)
        startActivityForResult(intent, ADD_NEW_PHOTO_REQUEST)
    }

    class LoadAsyncTask(
        private val adapter: ChocolateAdapter,
        private val NotesDatabaseAdapter: ChocolatesDatabaseAdapter
    ) :
        AsyncTask<Void, Void, List<Chocolate>>() {
        override fun doInBackground(vararg params: Void?): List<Chocolate> {
            return ChocolatesDatabaseAdapter.getAllChocolates()
        }

        override fun onPostExecute(result: List<Chocolate>?) {
            super.onPostExecute(result)
            Log.d("size", result?.size.toString())
            adapter.setChocolatesList(result ?: ArrayList())
            adapter.notifyDataSetChanged()
        }

    }
}
