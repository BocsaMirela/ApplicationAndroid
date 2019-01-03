package com.example.mirela.appAndroid.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE $TABLE_NAME (" +
                "$UID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_DESCRIPTION TEXT, " +
                "$COLUMN_DATE DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "$COLUMN_DATE_UPDATED DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "$COLUMN_IMAGE_PATH TEXT, " +
                "$COLUMN_USERNAME TEXT " + ")"
        db?.execSQL(query)

        val queryDel = "CREATE TABLE $TABLE_NAME_DELETED (" +
                "$UID INTEGER, " +
                "$COLUMN_USERNAME TEXT," + "PRIMARY KEY($UID , $COLUMN_USERNAME))"
        db?.execSQL(queryDel)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion != newVersion) {
            var deleteQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
            db?.execSQL(deleteQuery)
            onCreate(db)

            deleteQuery = "DROP TABLE IF EXISTS $TABLE_NAME_DELETED"
            db?.execSQL(deleteQuery)
            onCreate(db)
        }
    }

    companion object {
        private const val DATABASE_NAME = "chocolatesDB.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "Chocolates"
        const val TABLE_NAME_DELETED = "DeletedChocolates"
        const val UID = "id"
        const val COLUMN_DESCRIPTION = "Description"
        const val COLUMN_DATE = "Data"
        const val COLUMN_DATE_UPDATED = "LastUpdateDate"
        const val COLUMN_IMAGE_PATH = "ImagePath"
        const val COLUMN_USERNAME = "Username"
    }
}
