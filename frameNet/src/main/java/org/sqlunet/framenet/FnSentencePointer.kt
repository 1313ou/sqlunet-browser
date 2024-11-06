/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet

import android.os.Parcel
import android.os.Parcelable
import org.sqlunet.Pointer

/**
 * Parcelable sentence
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FnSentencePointer : Pointer {

    /**
     * Constructor from parcel, reads back fields IN THE ORDER they were written
     */
    private constructor(parcel: Parcel) : super(parcel)

    /**
     * Constructor
     *
     * @param sentenceId sentence id
     */
    constructor(sentenceId: Long) : super(sentenceId)

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<FnSentencePointer> {

            override fun createFromParcel(parcel: Parcel): FnSentencePointer {
                return FnSentencePointer(parcel)
            }

            override fun newArray(size: Int): Array<FnSentencePointer?> {
                return arrayOfNulls(size)
            }
        }
    }
}
