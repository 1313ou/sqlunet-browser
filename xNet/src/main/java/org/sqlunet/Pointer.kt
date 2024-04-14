/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet

import android.os.Parcel
import android.os.Parcelable

/**
 * Parcelable id pointer
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class Pointer : IPointer {

    /**
     * Id of pointed-to object
     */
    val id: Long

    /**
     * Constructor from parcel, reads back fields IN THE ORDER they were written
     */
    protected constructor(parcel: Parcel) {
        id = parcel.readLong()
    }

    /**
     * Constructor
     *
     * @param id id
     */
    protected constructor(id: Long) {
        this.id = id
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return id.toString()
    }

    companion object CREATOR : Parcelable.Creator<Pointer> {

        override fun createFromParcel(parcel: Parcel): Pointer {
            return Pointer(parcel)
        }

        override fun newArray(size: Int): Array<Pointer?> {
            return arrayOfNulls(size)
        }
    }
}
