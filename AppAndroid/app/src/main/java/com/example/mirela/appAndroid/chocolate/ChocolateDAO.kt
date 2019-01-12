package com.example.mirela.appAndroid.chocolate

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*


@Dao
interface ChocolateDAO {

    @Query("SELECT * FROM chocolates")
    fun getChocolates(): LiveData<List<Chocolate>>

    @Query("SELECT * FROM chocolates")
    fun getChocolatesTest(): List<Chocolate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chocolate: Chocolate): Long

    @Delete
    fun delete(chocolate: Chocolate)

    @Update
    fun update(chocolate: Chocolate)

    @get:Query("SELECT * FROM chocolates")
    val allChocolatesTest: List<Chocolate>

    @Query("select * from chocolates where wasInserted = 0")
    fun getNewInsertedChocolates(): List<Chocolate>

    @Query("select * from chocolates where wasUpdated = 0")
    fun getUpdatedChocolates(): List<Chocolate>

    @Query("DELETE FROM chocolates where id=:idC AND userId=:userId")
    fun delete(idC: Int, userId: Int)

}
