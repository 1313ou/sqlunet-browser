/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet

import android.os.Parcel
import android.os.Parcelable
import org.sqlunet.Pointer

/**
 * Parcelable lex unit
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FnLexUnitPointer : Pointer {

    /**
     * Constructor from parcel, reads back fields IN THE ORDER they were written
     */
    private constructor(parcel: Parcel) : super(parcel)

    /**
     * Constructor
     *
     * @param luId lex unit id
     */
    constructor(luId: Long) : super(luId)

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FnLexUnitPointer> {

        override fun createFromParcel(parcel: Parcel): FnLexUnitPointer {
            return FnLexUnitPointer(parcel)
        }

        override fun newArray(size: Int): Array<FnLexUnitPointer?> {
            return arrayOfNulls(size)
        }
    }
}
