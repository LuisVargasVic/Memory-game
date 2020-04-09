package com.adventa.memorable

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by jonathan on 11/16/18.
 */

data class MemoryItem(
    val image: Int,
    val type: Int,
    var view: Boolean,
    var match: Boolean,
    var number: Int
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    )

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0?.writeInt(image)
        p0?.writeInt(type)
        p0?.writeByte(if (view) 1 else 0)
        p0?.writeByte(if (match) 1 else 0)
        p0?.writeInt(number)
    }

    companion object CREATOR : Parcelable.Creator<MemoryItem> {
        override fun createFromParcel(parcel: Parcel): MemoryItem {
            return MemoryItem(parcel)
        }

        override fun newArray(size: Int): Array<MemoryItem?> {
            return arrayOfNulls(size)
        }
    }
    override fun describeContents(): Int {
        return 0
    }
}
