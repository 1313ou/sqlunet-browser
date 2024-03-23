/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet

import android.os.Parcel
import android.os.Parcelable
import org.sqlunet.Pointer

/**
 * Parcelable valence unit
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FnValenceUnitPointer : Pointer {

    /**
     * Constructor from parcel, reads back fields IN THE ORDER they were written
     */
    private constructor(parcel: Parcel) : super(parcel)

    /**
     * Constructor
     *
     * @param vuId valence unit id
     */
    constructor(vuId: Long) : super(vuId)

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FnValenceUnitPointer> {
        override fun createFromParcel(parcel: Parcel): FnValenceUnitPointer {
            return FnValenceUnitPointer(parcel)
        }

        override fun newArray(size: Int): Array<FnValenceUnitPointer?> {
            return arrayOfNulls(size)
        }
    }
}
