package com.example.mirela.appAndroid.utils

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder

object DeleteItemsDBAdapter {
    private lateinit var dbHelper: DbHelper

    fun setDbHelper(newDbHelper: DbHelper) {
        dbHelper = newDbHelper
    }

    fun insertDeletedItems(
        id: Int,
        username: String
    ): Long {
        val sqlSQLiteDatabase = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DbHelper.COLUMN_USERNAME, username)
            put(DbHelper.UID, id)
        }
        val newRowId = sqlSQLiteDatabase.insert(DbHelper.TABLE_NAME_DELETED, null, values)
        sqlSQLiteDatabase.close()
        return newRowId
    }

    fun getAllDeletedChocolates(): List<Pair<Long, String>> {
        val cursor = getCursor()
        val chocolatesList = mutableListOf<Pair<Long, String>>()
        with(cursor) {
            while (moveToNext()) {
                val item = Pair(
                    cursor.getLong(cursor.getColumnIndex(com.example.mirela.appAndroid.utils.DbHelper.UID)),
                    cursor.getString(
                        cursor.getColumnIndex(com.example.mirela.appAndroid.utils.DbHelper.COLUMN_USERNAME)
                    )
                )
                chocolatesList.add(item)
            }
        }
        return chocolatesList
    }

    fun deleteItem(id: Long, username: String): Int {
        val db = dbHelper.writableDatabase
        val rez = db.delete(
            DbHelper.TABLE_NAME_DELETED,
            "${DbHelper.UID}  =? and ${DbHelper.COLUMN_USERNAME}=?",
            arrayOf(id.toString(), username)
        )
        db.close()
        return rez

    }

    fun getCursor(): Cursor {
        val db = dbHelper.readableDatabase
        val qb = SQLiteQueryBuilder()
        qb.tables = DbHelper.TABLE_NAME_DELETED
        val projection =
            arrayOf(DbHelper.UID, DbHelper.COLUMN_USERNAME)
        val cursor = qb.query(db, projection, null, null, null, null, null)
        return cursor
    }

}