/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet

import android.os.Parcel
import android.os.Parcelable
import org.sqlunet.Pointer

/**
 * Parcelable frame
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FnFramePointer : Pointer {

    /**
     * Constructor from parcel, reads back fields IN THE ORDER they were written
     */
    private constructor(parcel: Parcel) : super(parcel)

    /**
     * Constructor
     *
     * @param frameId frame id
     */
    constructor(frameId: Long) : super(frameId)

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<FnFramePointer> {

            override fun createFromParcel(parcel: Parcel): FnFramePointer {
                return FnFramePointer(parcel)
            }

            override fun newArray(size: Int): Array<FnFramePointer?> {
                return arrayOfNulls(size)
            }
        }
    }
}
