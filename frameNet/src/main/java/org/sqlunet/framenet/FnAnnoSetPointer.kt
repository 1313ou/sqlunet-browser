/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet

import android.os.Parcel
import android.os.Parcelable
import org.sqlunet.Pointer

/**
 * Parcelable annoSet
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FnAnnoSetPointer : Pointer {

    /**
     * Constructor from parcel, reads back fields IN THE ORDER they were written
     */
    private constructor(parcel: Parcel) : super(parcel)

    /**
     * Constructor
     *
     * @param annoSetId annoSet id
     */
    constructor(annoSetId: Long) : super(annoSetId)

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<FnAnnoSetPointer> {

            override fun createFromParcel(parcel: Parcel): FnAnnoSetPointer {
                return FnAnnoSetPointer(parcel)
            }

            override fun newArray(size: Int): Array<FnAnnoSetPointer?> {
                return arrayOfNulls(size)
            }
        }
    }
}
