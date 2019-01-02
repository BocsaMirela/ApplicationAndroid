package com.example.mirela.appAndroid.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.mirela.appAndroid.MainActivity
import com.example.mirela.appAndroid.POJO.User

import com.example.mirela.appAndroid.R
import com.example.mirela.appAndroid.networking.Tasks
import com.example.mirela.appAndroid.utils.PasswordUtils

import java.util.concurrent.ExecutionException

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        try {
            val user = Tasks.IsLoggedTask().execute().get()
            if (user != null) {
                moveToMain(user)
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

    }

    fun moveToMain(user: User) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("username", user.name)
        this.startActivity(intent)
    }

    fun onLogin(view: View) {

        val username = (findViewById<View>(R.id.editUserName) as EditText).text.toString()
        val password = (findViewById<View>(R.id.editUserPassword) as EditText).text.toString()

        val user = User(username, PasswordUtils.hash(password, "1234asdf"))
        try {
//            if (Tasks.LoginTask().execute(user).get()) {
            if (true) {
                moveToMain(user)
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
