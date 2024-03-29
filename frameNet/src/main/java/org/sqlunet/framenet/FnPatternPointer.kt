/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet

import android.os.Parcel
import android.os.Parcelable
import org.sqlunet.Pointer

/**
 * Parcelable pattern
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FnPatternPointer : Pointer {

    /**
     * Constructor from parcel, reads back fields IN THE ORDER they were written
     */
    private constructor(parcel: Parcel) : super(parcel)

    /**
     * Constructor
     *
     * @param patternId pattern id
     */
    constructor(patternId: Long) : super(patternId)

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FnPatternPointer> {

        override fun createFromParcel(parcel: Parcel): FnPatternPointer {
            return FnPatternPointer(parcel)
        }

        override fun newArray(size: Int): Array<FnPatternPointer?> {
            return arrayOfNulls(size)
        }
    }
}
