/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.predicatematrix

import android.os.Parcel
import android.os.Parcelable
import org.sqlunet.Pointer

/**
 * Parcelable PredicateMatrix role
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class PmRolePointer : Pointer {

    /**
     * Constructor from parcel, reads back fields IN THE ORDER they were written
     *
     * @param parcel parcel
     */
    private constructor(parcel: Parcel) : super(parcel)

    /**
     * Constructor
     *
     * @param roleId role id
     */
    constructor(roleId: Long) : super(roleId)

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<PmRolePointer> {

            override fun createFromParcel(parcel: Parcel): PmRolePointer {
                return PmRolePointer(parcel)
            }

            override fun newArray(size: Int): Array<PmRolePointer?> {
                return arrayOfNulls(size)
            }
        }
    }
}
