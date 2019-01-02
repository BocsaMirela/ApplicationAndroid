package com.example.mirela.appAndroid.networking

import android.content.*
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import com.example.mirela.appAndroid.utils.DbHelper
import java.util.HashMap

class ChocolateContentProvider : ContentProvider() {

    private var dbHelper: DbHelper? = null

    override fun onCreate(): Boolean {
        dbHelper = DbHelper(context)
        return true
    }

    override fun query(
            uri: Uri,
            projection: Array<String>?,
            selection: String?,
            selectionArgs: Array<String>?,
            sortOrder: String?
    ): Cursor? {
        var selection = selection
        val qb = SQLiteQueryBuilder()
        qb.tables = DbHelper.TABLE_NAME
        qb.setProjectionMap(notesProjectionMap)

        when (sUriMatcher.match(uri)) {
            NOTES -> {
            }
            NOTESid -> {
                selection = selection!! + "_id = "
                uri.lastPathSegment
            }
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }

        val db = dbHelper!!.readableDatabase
        val c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder)

        c.setNotificationUri(context!!.contentResolver, uri)
        return c
    }


    override fun delete(uri: Uri, where: String?, whereArgs: Array<String>?): Int {
        var where = where
        val db = dbHelper!!.writableDatabase
        when (sUriMatcher.match(uri)) {
            NOTES -> {
            }
            NOTESid -> {
                where = where!! + "_id = "
                uri.lastPathSegment
            }
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }

        val count = db.delete(DbHelper.TABLE_NAME, where, whereArgs)
        context!!.contentResolver.notifyChange(uri, null)
        return count
    }

    override fun getType(uri: Uri): String? {
        when (sUriMatcher.match(uri)) {
            NOTES -> return CONTENT_TYPE
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
    }

    override fun insert(uri: Uri, initialValues: ContentValues?): Uri? {
        if (sUriMatcher.match(uri) != NOTES) {
            throw IllegalArgumentException("Unknown URI $uri")
        }

        val values: ContentValues = if (initialValues != null) {
            ContentValues(initialValues)
        } else {
            ContentValues()
        }

        val db = dbHelper!!.writableDatabase
        val rowId = db.insert(DbHelper.TABLE_NAME, DbHelper.COLUMN_DESCRIPTION, values)
        if (rowId > 0) {
            val noteUri = ContentUris.withAppendedId(CONTENT_URI, rowId)
            context!!.contentResolver.notifyChange(noteUri, null)
            return noteUri
        }

        throw SQLException("Failed to insert row into $uri")
    }

    override fun update(uri: Uri, values: ContentValues?, where: String?, whereArgs: Array<String>?): Int {
        val db = dbHelper!!.writableDatabase
        val count: Int
        when (sUriMatcher.match(uri)) {
            NOTES -> count = db.update(DbHelper.TABLE_NAME, values, where, whereArgs)
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }

        context!!.contentResolver.notifyChange(uri, null)
        return count
    }

    companion object {


        private val DATABASE_NAME = "chocolates.db"

        private val DATABASE_VERSION = 1

        private const val CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.example.mirela.trainingprobema3.utils.ChocolateContentProvider"

        const val AUTHORITY = "com.example.mirela.trainingprobema3.utils.ChocolateContentProvider"

        private val sUriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        private const val NOTES = 1

        private const val NOTESid = 2

        private var notesProjectionMap: HashMap<String, String>? = null
        val CONTENT_URI = Uri.parse("content://$AUTHORITY/${DbHelper.TABLE_NAME}")


        init {
            sUriMatcher.addURI(AUTHORITY, DbHelper.TABLE_NAME, NOTES)
            sUriMatcher.addURI(AUTHORITY, "${DbHelper.TABLE_NAME}/#", NOTESid)

            notesProjectionMap = HashMap()
            notesProjectionMap!![DbHelper.UID] = DbHelper.UID
            notesProjectionMap!![DbHelper.COLUMN_DESCRIPTION] = DbHelper.COLUMN_DESCRIPTION
            notesProjectionMap!![DbHelper.COLUMN_DATE] = DbHelper.COLUMN_DATE
            notesProjectionMap!![DbHelper.COLUMN_IMAGE_PATH] = DbHelper.COLUMN_IMAGE_PATH
        }
    }
}