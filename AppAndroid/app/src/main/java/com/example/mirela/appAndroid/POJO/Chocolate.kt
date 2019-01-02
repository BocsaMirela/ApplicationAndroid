package com.example.mirela.appAndroid.POJO

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Chocolate(
    var id: Long,
    var description: String,
    var date: Date,
    var imagePath: String,
    var lastUpdateDate: Date
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        Date(parcel.readLong()),
        parcel.readString(),
        Date(parcel.readLong())
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(description)
        parcel.writeLong(date.time)
        parcel.writeString(imagePath)
        parcel.writeLong(lastUpdateDate.time)
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

}

