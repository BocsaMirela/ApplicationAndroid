package com.example.mirela.appAndroid.bus

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.example.mirela.appAndroid.bus.Chocolate

@Dao
interface ChocolateDAO {

    @get:Query("SELECT * FROM busses")
    val allBusses: List<Chocolate>

    @Insert
    fun insert(chocolate: Chocolate)

    @Query("SELECT * FROM busses LIMIT:howMany OFFSET:start")
    fun getBusses(howMany: Int, start: Int): List<Chocolate>

    @Query("UPDATE busses SET added=:added WHERE busses.number=:number")
    fun updateBus(number: Int?, added: Boolean?)
}
