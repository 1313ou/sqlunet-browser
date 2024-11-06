/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank

import android.os.Parcel
import android.os.Parcelable
import org.sqlunet.Pointer

/**
 * Parcelable role set pointer
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class PbRoleSetPointer : Pointer {

    /**
     * Constructor from parcel, reads back fields IN THE ORDER they were written
     *
     * @param parcel parcel
     */
    private constructor(parcel: Parcel) : super(parcel)

    /**
     * Constructor
     *
     * @param roleSetId role set id
     */
    constructor(roleSetId: Long) : super(roleSetId)

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        @Suppress("unused")
        @JvmField
        val CREATOR = object : Parcelable.Creator<PbRoleSetPointer> {

            override fun createFromParcel(parcel: Parcel): PbRoleSetPointer {
                return PbRoleSetPointer(parcel)
            }

            override fun newArray(size: Int): Array<PbRoleSetPointer?> {
                return arrayOfNulls(size)
            }
        }
    }
}
