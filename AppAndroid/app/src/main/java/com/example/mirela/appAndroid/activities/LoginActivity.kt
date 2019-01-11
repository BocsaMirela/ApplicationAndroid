package com.example.mirela.appAndroid.activities

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import com.example.mirela.appAndroid.MainActivity
import com.example.mirela.appAndroid.POJO.LoginResponse

import com.example.mirela.appAndroid.R
import com.example.mirela.appAndroid.utils.SessionManager
import com.example.user.myapplication.modelviews.LoginModelView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private val loginModelView = LoginModelView()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sessionManager = SessionManager(applicationContext)
        if (sessionManager.isLoggedIn) {
            moveToMain()
        }

    }

    fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        finish()
    }

    fun onLogin(view: View) {

        val username = (findViewById<View>(R.id.editUserName) as EditText).text.toString()
        val password = (findViewById<View>(R.id.editUserPassword) as EditText).text.toString()

        loginModelView.loginUser(username, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.body() != null) {
                    val loginResponse = response.body()
                    loginResponse?.also {
                        if (loginResponse.token != "" && loginResponse.message != "Wrong") {
                            Log.e("info ",loginResponse.token + loginResponse.id)
                            sessionManager.createLoginSession(loginResponse.token, loginResponse.id)
                            moveToMain()
                        } else {
                            val alertDialog = AlertDialog.Builder(this@LoginActivity).create()
                            alertDialog.setTitle("Info")
                            alertDialog.setMessage("Cannot connect to server!! Try later :( ")
                            alertDialog.setButton(
                                AlertDialog.BUTTON_NEUTRAL, "OK"
                            ) { dialog,
                                which ->
                                dialog.dismiss()
                            }
                            alertDialog.show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                val alertDialog = AlertDialog.Builder(this@LoginActivity).create()
                alertDialog.setTitle("Info")
                alertDialog.setMessage("Cannot connect to server!! Try later :( ")
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog,
                    which ->
                    dialog.dismiss()
                }
                alertDialog.show()
            }
        })

    }
}
