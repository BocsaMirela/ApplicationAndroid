package com.example.mirela.appAndroid.utils

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import com.example.mirela.appAndroid.POJO.Chocolate
import java.util.*

object ChocolatesDatabaseAdapter {
    private lateinit var dbHelper: DbHelper

    fun setDbHelper(newDbHelper: DbHelper) {
        dbHelper = newDbHelper
    }

    fun insertChocolate(
        description: String,
        data: Date,
        imagePath: String,
        dateUpdated: Date
    ): Long {
        val sqlSQLiteDatabase = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DbHelper.COLUMN_DESCRIPTION, description)
            put(DbHelper.COLUMN_IMAGE_PATH, imagePath)
            put(DbHelper.COLUMN_DATE, data.time)
        }
        val newRowId = sqlSQLiteDatabase.insert(DbHelper.TABLE_NAME, null, values)
        sqlSQLiteDatabase.close()
        return newRowId
    }

    fun getAllChocolates(): List<Chocolate> {
        val cursor = getChocolatesCursor()

        val chocolatesList = mutableListOf<Chocolate>()
        with(cursor) {
            while (moveToNext()) {
                val chocolate = com.example.mirela.appAndroid.POJO.Chocolate(
                        cursor.getLong(cursor.getColumnIndex(com.example.mirela.appAndroid.utils.DbHelper.UID)),
                        cursor.getString(cursor.getColumnIndex(com.example.mirela.appAndroid.utils.DbHelper.COLUMN_DESCRIPTION)),
                        java.util.Date(cursor.getLong(cursor.getColumnIndex(com.example.mirela.appAndroid.utils.DbHelper.Companion.COLUMN_DATE))),
                        cursor.getString(cursor.getColumnIndex(com.example.mirela.appAndroid.utils.DbHelper.COLUMN_IMAGE_PATH))
                )
                chocolatesList.add(chocolate)
            }
        }
        return chocolatesList
    }

    fun deleteChocolate(id: Long): Int {
        val db = dbHelper.writableDatabase
        val rez = db.delete(DbHelper.TABLE_NAME, "${DbHelper.UID}  =?", Array(1) { id.toString() })
        db.close()
        return rez

    }

    fun updateChocolate(id: Long, newDescription: String, newDate: Date, newImagePath: String): Int {

        val db = dbHelper.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(DbHelper.COLUMN_DESCRIPTION, newDescription)
        contentValues.put(DbHelper.COLUMN_IMAGE_PATH, newImagePath)
        contentValues.put(DbHelper.COLUMN_DATE, newDate.time)
        val whereArgs = arrayOf(id.toString())

        val rez = db.update(DbHelper.TABLE_NAME, contentValues, "${DbHelper.UID} = ?", whereArgs)
        return rez
    }

    fun getChocolatesCursor(): Cursor {
        val db = dbHelper.readableDatabase
        val qb = SQLiteQueryBuilder()
        qb.tables = DbHelper.TABLE_NAME
        val projection = arrayOf(DbHelper.UID, DbHelper.COLUMN_DESCRIPTION, DbHelper.COLUMN_DATE, DbHelper.COLUMN_IMAGE_PATH)

        val sortOrder = "${DbHelper.COLUMN_DATE} ASC"
        val cursor = qb.query(db, projection, null, null, null, null, sortOrder)
        cursor.moveToFirst()
        return cursor
    }

}