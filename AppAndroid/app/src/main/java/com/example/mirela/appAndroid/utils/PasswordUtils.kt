package com.example.mirela.appAndroid.utils

import java.security.MessageDigest

import kotlin.experimental.and

object PasswordUtils {
    fun hash(passwordToHash: String, salt: String): String? {
        var generatedPassword: String? = null
        try {
            val md = MessageDigest.getInstance("SHA-512")
            md.update(salt.toByteArray(charset("UTF-8")))
            val bytes = md.digest(passwordToHash.toByteArray(charset("UTF-8")))
            val sb = StringBuilder()
            for (i in bytes.indices) {
                sb.append(Integer.toString((bytes[i] and 0xff.toByte()) + 0x100, 16).substring(1))
            }
            generatedPassword = sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return generatedPassword
    }
}
