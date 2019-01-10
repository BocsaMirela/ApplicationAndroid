package com.example.mirela.appAndroid.bus

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = arrayOf(Chocolate::class), version = 1)
abstract class ChocolateDatabase : RoomDatabase() {

    abstract fun busDAO(): ChocolateDAO

    companion object {
        var INSTANCE: ChocolateDatabase? = null

        fun getAppDatabase(context: Context): ChocolateDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, ChocolateDatabase::class.java, "bus-database").allowMainThreadQueries().build()

            }
            return INSTANCE as ChocolateDatabase

        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

}