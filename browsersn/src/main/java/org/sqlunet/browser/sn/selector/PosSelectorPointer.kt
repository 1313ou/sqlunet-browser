/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.sn.selector

import android.os.Parcel
import android.os.Parcelable
import org.sqlunet.HasPos

/**
 * SelectorPointer
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class PosSelectorPointer : SelectorPointer, HasPos {

    /**
     * POS
     */
    protected val pos: Char
        @JvmName("getPosProperty")
        get

    /**
     * Constructor from parcel
     *
     * @param parcel parcel
     */
    protected constructor(parcel: Parcel) : super(parcel) {
        val posStr = parcel.readString()!!
        pos = posStr[0]
    }

    /**
     * Constructor
     *
     * @param synsetId synset id
     * @param wordId   word id
     * @param pos      pos
     */
    constructor(synsetId: Long, wordId: Long, pos: Char) : super(synsetId, wordId) {
        this.pos = pos
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(pos.toString())
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun getPos(): Char {
        return pos
    }

    override fun toString(): String {
        return super.toString() + ' ' + "pos=" + pos
    }

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<PosSelectorPointer> {

            override fun createFromParcel(parcel: Parcel): PosSelectorPointer {
                return PosSelectorPointer(parcel)
            }

            override fun newArray(size: Int): Array<PosSelectorPointer?> {
                return arrayOfNulls(size)
            }
        }
    }
}
