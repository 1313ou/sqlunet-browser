/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet

import android.os.Parcel
import android.os.Parcelable
import org.sqlunet.HasSenseKey

/**
 * Parcelable sensekey
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SenseKeyPointer : Parcelable, HasSenseKey {

    private val senseKey: String?

    /**
     * Constructor from parcel, reads back fields IN THE ORDER they were written
     */
    private constructor(parcel: Parcel) {
        senseKey = parcel.readString()
    }

    /**
     * Constructor
     *
     * @param senseKey sense key
     */
    constructor(senseKey: String?) {
        this.senseKey = senseKey
    }

    override fun getSenseKey(): String? {
        return senseKey
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(senseKey)
    }

    override fun toString(): String {
        return "sensekey=" +
                senseKey
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SenseKeyPointer> {

        override fun createFromParcel(parcel: Parcel): SenseKeyPointer {
            return SenseKeyPointer(parcel)
        }

        override fun newArray(size: Int): Array<SenseKeyPointer?> {
            return arrayOfNulls(size)
        }
    }
}
