/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet

import android.os.Parcel
import android.os.Parcelable
import org.sqlunet.Pointer

/**
 * Parcelable collocation pointer
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SnCollocationPointer : Pointer {

    /**
     * Constructor from parcel, reads back fields IN THE ORDER they were written
     *
     * @param parcel parcel
     */
    private constructor(parcel: Parcel) : super(parcel)

    /**
     * Constructor
     *
     * @param collocationId collocation id
     */
    constructor(collocationId: Long) : super(collocationId)

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<SnCollocationPointer> {

            override fun createFromParcel(parcel: Parcel): SnCollocationPointer {
                return SnCollocationPointer(parcel)
            }

            override fun newArray(size: Int): Array<SnCollocationPointer?> {
                return arrayOfNulls(size)
            }
        }
    }
}
