/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet

import android.os.Parcel
import android.os.Parcelable
import org.sqlunet.HasWordId

/**
 * Parcelable sense
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class SensePointer : SynsetPointer, HasWordId {

    /**
     * Word id
     */
    val wordId: Long
        @JvmName("getWordIdProperty")
        get

    /**
     * Constructor from parcel, reads back fields IN THE ORDER they were written
     */
    protected constructor(parcel: Parcel) : super(parcel) {
        wordId = parcel.readLong()
    }

    /**
     * Constructor
     *
     * @param synsetId synset id
     * @param wordId   word id
     */
    constructor(synsetId: Long, wordId: Long) : super(synsetId) {
        this.wordId = wordId
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeLong(wordId)
    }

    override fun getWordId(): Long {
        return wordId
    }

    override fun toString(): String {
        return super.toString() + ' ' + "wordid=" + wordId
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SensePointer> {
        override fun createFromParcel(parcel: Parcel): SensePointer {
            return SensePointer(parcel)
        }

        override fun newArray(size: Int): Array<SensePointer?> {
            return arrayOfNulls(size)
        }
    }
}
