package com.example.mirela.appAndroid.utils

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import com.example.mirela.appAndroid.chocolate.Chocolate
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
        dateUpdated: Date,
        username: String
    ): Long {
        val sqlSQLiteDatabase = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DbHelper.COLUMN_DESCRIPTION, description)
            put(DbHelper.COLUMN_IMAGE_PATH, imagePath)
            put(DbHelper.COLUMN_DATE, data.time)
            put(DbHelper.COLUMN_DATE_UPDATED, dateUpdated.time)
            put(DbHelper.COLUMN_USERNAME, username)
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
                val chocolate = Chocolate(
                    cursor.getLong(cursor.getColumnIndex(DbHelper.UID)),
                    cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DESCRIPTION)),
                    cursor.getLong(cursor.getColumnIndex(DbHelper.COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_IMAGE_PATH)),
                    cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_USERNAME)).toInt()
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

    fun updateChocolate(
        id: Long,
        newDescription: String,
        newDate: Date,
        newImagePath: String,
        lastUpdateDate: Date
    ): Int {

        val db = dbHelper.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(DbHelper.COLUMN_DESCRIPTION, newDescription)
        contentValues.put(DbHelper.COLUMN_IMAGE_PATH, newImagePath)
        contentValues.put(DbHelper.COLUMN_DATE, newDate.time)
        contentValues.put(DbHelper.COLUMN_DATE_UPDATED, lastUpdateDate.time)
        val whereArgs = arrayOf(id.toString())

        val rez = db.update(DbHelper.TABLE_NAME, contentValues, "${DbHelper.UID} = ?", whereArgs)
        return rez
    }

    fun getChocolatesCursor(): Cursor {
        val db = dbHelper.readableDatabase
        val qb = SQLiteQueryBuilder()
        qb.tables = DbHelper.TABLE_NAME
        val projection =
            arrayOf(
                DbHelper.UID,
                DbHelper.COLUMN_DESCRIPTION,
                DbHelper.COLUMN_DATE,
                DbHelper.COLUMN_IMAGE_PATH,
                DbHelper.COLUMN_DATE_UPDATED,
                DbHelper.COLUMN_USERNAME
            )

        val sortOrder = "${DbHelper.COLUMN_DATE} ASC"
        val cursor = qb.query(db, projection, null, null, null, null, sortOrder)
        return cursor
    }

}