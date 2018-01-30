package com.ragabuza.personalreminder.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by diego.moyses on 12/28/2017.
 */
data class Reminder(
        val id: Long,
        var done: String,
        var active: Boolean,
        val reminder: String = "",
        val type: String,
        val rWhen: String,
        val condition: String = "",
        val extra: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(done)
        parcel.writeByte(if (active) 1 else 0)
        parcel.writeString(reminder)
        parcel.writeString(type)
        parcel.writeString(rWhen)
        parcel.writeString(condition)
        parcel.writeString(extra)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Reminder> {
        val WIFI = "WIFI"
        val BLUETOOTH = "BLUETOOTH"
        val LOCATION = "LOCATION"
        val TIME = "TIME"
        val SIMPLE = "SIMPLE"
        val IS = "IS"
        val ISNOT = "ISNOT"
        override fun createFromParcel(parcel: Parcel): Reminder {
            return Reminder(parcel)
        }

        override fun newArray(size: Int): Array<Reminder?> {
            return arrayOfNulls(size)
        }
    }
}