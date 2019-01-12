package com.example.mirela.appAndroid.syncronize

import android.arch.persistence.room.Entity
import android.os.Parcel
import android.os.Parcelable
import com.example.mirela.appAndroid.chocolate.Chocolate

@Entity(tableName = "deletedItems", primaryKeys = ["id", "userId"])
class DeletedItem(var id: Long, var userId: Int)

