package com.example.mirela.appAndroid.syncronize

import android.arch.persistence.room.*
import com.example.mirela.appAndroid.chocolate.Chocolate

@Dao
interface DeletedItemsDAO {

    @Query("SELECT * FROM deletedItems")
    fun getDeletedItems(): List<DeletedItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deletedItem: DeletedItem):Long

    @Delete
    fun delete(deletedItem: DeletedItem)



}