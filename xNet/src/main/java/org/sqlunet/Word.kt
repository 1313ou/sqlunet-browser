/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet

import android.os.Parcel
import android.os.Parcelable

/**
 * Parcelable word
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class Word : IPointer {

    /**
     * Word
     */
    val word: String?

    /**
     * Constructor from parcel, reads back fields IN THE ORDER they were written
     */
    private constructor(parcel: Parcel) {
        word = parcel.readString()
    }

    /**
     * Constructor
     *
     * @param word word
     */
    constructor(word: String?) {
        this.word = word
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(word)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return word ?: ""
    }

    companion object CREATOR : Parcelable.Creator<Word> {

        override fun createFromParcel(parcel: Parcel): Word {
            return Word(parcel)
        }

        override fun newArray(size: Int): Array<Word?> {
            return arrayOfNulls(size)
        }
    }
}
