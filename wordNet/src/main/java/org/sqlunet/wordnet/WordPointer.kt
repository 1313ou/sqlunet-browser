/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet

import android.os.Parcel
import android.os.Parcelable
import org.sqlunet.HasWordId
import org.sqlunet.Pointer

/**
 * Parcelable word
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class WordPointer : Pointer, HasWordId {

    /**
     * Constructor from parcel, reads back fields IN THE ORDER they were written
     */
    private constructor(parcel: Parcel) : super(parcel)

    /**
     * Constructor
     *
     * @param wordId word id
     */
    constructor(wordId: Long) : super(wordId)

    override fun getWordId(): Long {
        return id
    }

    override fun toString(): String {
        return "wordid=$id"
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<WordPointer> {

            override fun createFromParcel(parcel: Parcel): WordPointer {
                return WordPointer(parcel)
            }

            override fun newArray(size: Int): Array<WordPointer?> {
                return arrayOfNulls(size)
            }
        }
    }
}
