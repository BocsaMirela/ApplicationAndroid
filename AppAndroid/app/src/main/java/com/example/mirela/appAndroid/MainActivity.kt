package com.example.mirela.appAndroid

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.mirela.appAndroid.chocolate.Chocolate
import com.example.mirela.appAndroid.activities.AddActivity
import com.example.mirela.appAndroid.activities.UpdateActivity
import com.example.mirela.appAndroid.chocolate.ChocolateDatabase
import com.example.mirela.appAndroid.modelviews.ChocolatesViewModel
import com.example.mirela.appAndroid.syncronize.DeletedItemsDAO
import com.example.mirela.appAndroid.syncronize.Updater
import com.example.mirela.appAndroid.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Console
import java.lang.Exception
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), OnClickInterface {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var adapter: ChocolateAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var chocolatesViewModel: ChocolatesViewModel
    private lateinit var deletedItemsDAO: DeletedItemsDAO
    private lateinit var mProgressBar: ProgressBar
    private var lastID = 0


    private val ADD_NEW_PHOTO_REQUEST = 1
    private val UPDATE_PHOTO_REQUEST = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        initialize()
        adapter.setOnClickListener(this)
        val itemTouchHelper = ItemTouchHelper(
            SwipeToDeleteCallback(
                this@MainActivity,
                adapter,
                chocolatesViewModel,
                deletedItemsDAO,
                findViewById(R.id.coordinatorLayout)
            )
        )
        itemTouchHelper.attachToRecyclerView(recyclerView)
        getDataFromServer()
    }

    override fun onClick(view: View, position: Int) {
        updateChocolate(position)
    }

    private fun initialize() {
        sessionManager = SessionManager(applicationContext)
        sessionManager.checkLogin()
        val token = sessionManager.userToken!!

        chocolatesViewModel = ViewModelProviders.of(this, ChocolatesViewModel.Factory(token, application))
            .get(ChocolatesViewModel::class.java)

        recyclerView = this.findViewById(R.id.savedChocolates)

        viewManager = GridLayoutManager(this@MainActivity, 1)
        recyclerView.layoutManager = viewManager
        adapter = ChocolateAdapter()
        recyclerView.adapter = adapter

        deletedItemsDAO = ChocolateDatabase.getAppDatabase(application).deletedItemsDAO()

        mProgressBar = findViewById(R.id.progress_bar)

        recyclerView.visibility = View.GONE
        mProgressBar.visibility = View.VISIBLE
        Log.e("visible initialize ", (mProgressBar.visibility == View.VISIBLE).toString())

        chocolatesViewModel.getAllChocolates().observe(this, Observer { chocolates ->
            Log.e(" obs ", chocolates?.size.toString())
            chocolates?.also {
                if (chocolatesViewModel.items.value == null) {
                    chocolatesViewModel.items.value = chocolates
                }
            }

        })

        chocolatesViewModel.items.observe(this, Observer { chocolates ->
            Log.e(" obs bun ", chocolates?.size.toString())
            chocolates?.also {
                adapter.setChocolatesList(chocolates)
                adapter.notifyDataSetChanged()
                Log.e(" obs bun", " invisible true ")
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        makeProgressBarVisible()
        if (requestCode == ADD_NEW_PHOTO_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                data?.apply {
                    val chocolate = data.getParcelableExtra<Chocolate>("item")
                    lastID++
                    chocolate.id = lastID.toLong()
                    addChocolateToServer(chocolate)
                }
            }
        } else if (requestCode == UPDATE_PHOTO_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val chocolate = data?.getParcelableExtra<Chocolate>("item")
                val token = sessionManager.userToken
                val userId = sessionManager.userId?.toInt()
                chocolate?.also {
                    updateFromServer(token, userId, chocolate)
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
        intent.putExtra("item", chocolate)
        startActivityForResult(intent, UPDATE_PHOTO_REQUEST)
    }

    fun onAdd(v: View) {
        val intent = Intent(this, AddActivity::class.java)
        startActivityForResult(intent, ADD_NEW_PHOTO_REQUEST)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.logoutMenu -> {
                sessionManager.logoutUser()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun getDataFromServer() {
        Updater.syncronizeData(
            chocolatesViewModel,
            deletedItemsDAO,
            applicationContext
        )
        chocolatesViewModel.getChocolatesFromServer().enqueue(object : Callback<List<Chocolate>> {
            override fun onResponse(call: Call<List<Chocolate>>, response: Response<List<Chocolate>>) {
                println(" m-am conectat" + response.body())
                if (response.body() != null) {
                    val chocolates = response.body()
                    println("m-am conectat " + chocolates?.size)
                    chocolates?.also {
                        if (chocolates.isNotEmpty()) {
                            lastID = chocolates.sortedBy { chocolate -> chocolate.id }.last().id.toInt() + 1
                            makeRecyclerViewVisible()

                            chocolatesViewModel.items.value = chocolates
                            Updater.syncronizeDataLocal(chocolatesViewModel, chocolates)
                        }
                    }

                }
            }

            override fun onFailure(call: Call<List<Chocolate>>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "Something went wrong or no connection! Loading local data",
                    Toast.LENGTH_LONG
                ).show()
                val items = chocolatesViewModel.items.value
                items?.also {

                    makeRecyclerViewVisible()
                    adapter.setChocolatesList(items)
                    adapter.notifyDataSetChanged()
                }
                Updater.tryToConnect(chocolatesViewModel, deletedItemsDAO, applicationContext)
            }

        })
    }

    private fun addChocolateToServer(chocolate: Chocolate) {
        chocolatesViewModel.addChocolateServer(chocolate).enqueue(object : Callback<Chocolate> {
            override fun onFailure(call: Call<Chocolate>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "Something went wrong or no connection! The new chocolate will be saved in your local data",
                    Toast.LENGTH_LONG
                ).show()
                chocolate.wasInserted = 0
                val newR = chocolatesViewModel.saveChocolate(chocolate)
                if (newR > 0) {
                    Log.e("added ", newR.toString())
                    adapter.insertItem(chocolate)
                    chocolatesViewModel.items.value = adapter.getChocolatesList()
                    makeRecyclerViewVisible()
                }
            }


            override fun onResponse(call: Call<Chocolate>, response: Response<Chocolate>) {
                Toast.makeText(this@MainActivity, "Add was done", Toast.LENGTH_LONG).show()
                val newR = chocolatesViewModel.saveChocolate(chocolate)
                if (newR > 0) {
                    adapter.insertItem(chocolate)
                    chocolatesViewModel.items.value = adapter.getChocolatesList()
                    makeRecyclerViewVisible()

                }
            }

        })
    }

    private fun updateFromServer(token: String?, userId: Int?, chocolate: Chocolate) {
        chocolatesViewModel.updateChocolateServer(chocolate, userId).enqueue(object : Callback<Chocolate> {
            override fun onFailure(call: Call<Chocolate>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "Something went wrong or no connection! Update from local data",
                    Toast.LENGTH_LONG
                ).show()

                chocolate.wasUpdated = 0
                chocolatesViewModel.updateChocolate(chocolate)

                adapter.removeItem(chocolate)
                adapter.insertItem(chocolate)

                chocolatesViewModel.items.value = adapter.getChocolatesList()
                makeRecyclerViewVisible()

            }

            override fun onResponse(call: Call<Chocolate>, response: Response<Chocolate>) {
                Toast.makeText(applicationContext, "Update done", Toast.LENGTH_LONG).show()
                chocolatesViewModel.updateChocolate(chocolate)
                adapter.removeItem(chocolate)
                adapter.insertItem(chocolate)
                chocolatesViewModel.items.value = adapter.getChocolatesList()
                makeRecyclerViewVisible()

            }

        })
    }

    fun makeRecyclerViewVisible() {
        recyclerView.visibility = View.VISIBLE
        mProgressBar.visibility = View.GONE
    }


    private fun makeProgressBarVisible() {
        mProgressBar.visibility = View.VISIBLE
    }

    override fun onResume() {
        if (chocolatesViewModel.items.value != null) {
//            mProgressBar.visibility = View.GONE
        }
        super.onResume()

    }

}
