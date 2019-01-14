package com.example.mirela.appAndroid.utils


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.auth0.android.jwt.JWT
import com.example.mirela.appAndroid.activities.LoginActivity

class SessionManager(var _context: Context) {
    private var pref: SharedPreferences
    private var editor: Editor
    private var PRIVATE_MODE = 0

    val userToken: String?
        get() {
            return pref.getString(KEY_NAME_TOKEN, null)
        }

    val userId: String?
        get() {
            return pref.getString(KEY_NAME_ID, null)
        }

    // Get Login State
    val isLoggedIn: Boolean
        get() = pref.getBoolean(IS_LOGIN, false) && !JWT(userToken!!).isExpired(10)

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun createLoginSession(name: String, id: Int) {
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(KEY_NAME_TOKEN, name)
        editor.putString(KEY_NAME_ID, id.toString())
        // commit changes
        editor.commit()
    }

    fun checkLogin() {
        // Check login status
        if (!this.isLoggedIn) {
            val i = Intent(_context, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            _context.startActivity(i)
        }

    }

    fun logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear()
        editor.commit()

        val i = Intent(_context, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        _context.startActivity(i)
    }

    companion object {

        private const val PREF_NAME = "AndroidHivePref"

        private const val IS_LOGIN = "IsLoggedIn"

        const val KEY_NAME_TOKEN = "token"

        const val KEY_NAME_ID = "id"
    }
}
