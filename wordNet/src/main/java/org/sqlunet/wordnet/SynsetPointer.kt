/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet

import android.os.Parcel
import android.os.Parcelable
import org.sqlunet.HasSynsetId
import org.sqlunet.Pointer

/**
 * Parcelable synset
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class SynsetPointer : Pointer, HasSynsetId {

    /**
     * Constructor from parcel, reads back fields IN THE ORDER they were written
     */
    protected constructor(parcel: Parcel) : super(parcel)

    /**
     * Constructor
     *
     * @param synsetId synset id
     */
    constructor(synsetId: Long) : super(synsetId)

    override fun getSynsetId(): Long {
        return id
    }

    override fun toString(): String {
        return "synsetid=$id"
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<SynsetPointer> {

            override fun createFromParcel(parcel: Parcel): SynsetPointer {
                return SynsetPointer(parcel)
            }

            override fun newArray(size: Int): Array<SynsetPointer?> {
                return arrayOfNulls(size)
            }
        }
    }
}
