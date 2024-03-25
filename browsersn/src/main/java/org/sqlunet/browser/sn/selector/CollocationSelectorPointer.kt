/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.sn.selector

import android.os.Parcel
import android.os.Parcelable
import org.sqlunet.Has2Pos
import org.sqlunet.Has2SynsetId
import org.sqlunet.Has2WordId
import org.sqlunet.HasTarget

/**
 * SelectorPointer
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class CollocationSelectorPointer : PosSelectorPointer, Has2SynsetId, Has2WordId, Has2Pos, HasTarget {
    /**
     * Synset 2 id
     */
    private val synset2Id: Long

    /**
     * Word 2 id
     */
    private val word2Id: Long

    /**
     * POS 2
     */
    private val pos2: Char?

    /**
     * Target
     */
    private val target: Int

    /**
     * Constructor from parcel
     *
     * @param parcel parcel
     */
    private constructor(parcel: Parcel) : super(parcel) {
        synset2Id = parcel.readLong()
        word2Id = parcel.readLong()
        val posStr = parcel.readString()
        pos2 = posStr?.get(0)
        target = parcel.readInt()
    }

    /**
     * Constructor
     *
     * @param synset1Id synset 1 id
     * @param word1Id   word 1 id
     * @param pos1      pos 1
     * @param synset2Id synset 2 id
     * @param word2Id   word 2 id
     * @param pos2      pos 2
     * @param target    target (1, 2 or 0 unspecified)
     */
    constructor(synset1Id: Long, word1Id: Long, pos1: Char?, synset2Id: Long, word2Id: Long, pos2: Char?, target: Int) : super(synset1Id, word1Id, pos1!!) {
        this.synset2Id = synset2Id
        this.word2Id = word2Id
        this.pos2 = pos2
        this.target = target
    }

    override fun getSynset2Id(): Long {
        return synset2Id
    }

    override fun getWord2Id(): Long {
        return word2Id
    }

    override fun getPos2(): Char? {
        return pos2
    }

    override fun getTarget(): Int {
        return target
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeLong(synset2Id)
        parcel.writeLong(word2Id)
        parcel.writeString(pos2?.toString())
        parcel.writeInt(target)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return super.toString() + ' ' + "word2=" + word2Id + ' ' + "synset2=" + synset2Id + ' ' + "pos2=" + pos2 + ' ' + "target=" + target
    }

    companion object CREATOR : Parcelable.Creator<CollocationSelectorPointer> {
        override fun createFromParcel(parcel: Parcel): CollocationSelectorPointer {
            return CollocationSelectorPointer(parcel)
        }

        override fun newArray(size: Int): Array<CollocationSelectorPointer?> {
            return arrayOfNulls(size)
        }
    }
}
