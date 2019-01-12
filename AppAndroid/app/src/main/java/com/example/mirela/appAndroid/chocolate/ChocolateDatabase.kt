package com.example.mirela.appAndroid.chocolate

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.example.mirela.appAndroid.syncronize.DeletedItem
import com.example.mirela.appAndroid.syncronize.DeletedItemsDAO
import com.example.mirela.appAndroid.utils.Converters

@Database(entities = [Chocolate::class, DeletedItem::class], version = 1)
@TypeConverters(Converters::class)
abstract class ChocolateDatabase : RoomDatabase() {

    abstract fun chocolateDAO(): ChocolateDAO
    abstract fun deletedItemsDAO(): DeletedItemsDAO

    companion object {
        private var INSTANCE: ChocolateDatabase? = null

        fun getAppDatabase(context: Context): ChocolateDatabase {
            if (INSTANCE == null) {
                INSTANCE =
                        Room.databaseBuilder(context.applicationContext, ChocolateDatabase::class.java, "chocolatesDataBase")
                            .allowMainThreadQueries().build()

            }
            return INSTANCE as ChocolateDatabase

        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

}