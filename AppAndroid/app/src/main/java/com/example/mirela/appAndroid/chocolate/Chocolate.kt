package com.example.mirela.appAndroid.chocolate

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import java.util.*

@Entity(tableName = "chocolates", primaryKeys = ["id", "userId"])
class Chocolate(
    var id: Long,
    var body: String,
    var date: Long,
    var imagePath: String,
    var userId: Int,
    var wasUpdated: Int,
    var wasInserted: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(body)
        parcel.writeLong(date)
        parcel.writeString(imagePath)
        parcel.writeInt(userId)
        parcel.writeInt(wasUpdated)
        parcel.writeInt(wasInserted)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Chocolate> {
        override fun createFromParcel(parcel: Parcel): Chocolate {
            return Chocolate(parcel)
        }

        override fun newArray(size: Int): Array<Chocolate?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        other?.also {
            return (other as Chocolate).id == this.id && (other as Chocolate).userId == this.userId
        }
        return false
    }
}

