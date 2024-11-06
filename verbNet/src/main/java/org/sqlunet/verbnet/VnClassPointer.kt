/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet

import android.os.Parcel
import android.os.Parcelable
import org.sqlunet.Pointer

/**
 * Parcelable class
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class VnClassPointer : Pointer {

    /**
     * Constructor
     *
     * @param classId class id
     */
    constructor(classId: Long) : super(classId)

    /**
     * Constructor from parcel, reads back fields IN THE ORDER they were written
     */
    private constructor(parcel: Parcel) : super(parcel)

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        @Suppress("unused")
        @JvmField
        val CREATOR = object : Parcelable.Creator<VnClassPointer> {

            override fun createFromParcel(parcel: Parcel): VnClassPointer {
                return VnClassPointer(parcel)
            }

            override fun newArray(size: Int): Array<VnClassPointer?> {
                return arrayOfNulls(size)
            }
        }
    }
}
