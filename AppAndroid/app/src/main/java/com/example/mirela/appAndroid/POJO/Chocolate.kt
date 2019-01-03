package com.example.mirela.appAndroid.POJO

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Chocolate(
    var id: Long,
    var description: String,
    var date: Date,
    var imagePath: String,
    var lastUpdateDate: Date,
    var username:String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        Date(parcel.readLong()),
        parcel.readString(),
        Date(parcel.readLong()),
        parcel.readString()
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(description)
        parcel.writeLong(date.time)
        parcel.writeString(imagePath)
        parcel.writeLong(lastUpdateDate.time)
        parcel.writeString(username)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chocolate

        if (id != other.id) return false
        if (username != other.username) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<Chocolate> {
        override fun createFromParcel(parcel: Parcel): Chocolate {
            return Chocolate(parcel)
        }

        override fun newArray(size: Int): Array<Chocolate?> {
            return arrayOfNulls(size)
        }
    }


}

