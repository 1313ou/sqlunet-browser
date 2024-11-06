/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.xn.xselector

import android.os.Parcel
import android.os.Parcelable
import org.sqlunet.HasXId
import org.sqlunet.browser.xn.selector.SelectorPointer

/**
 * X pointer
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class XSelectorPointer : SelectorPointer, HasXId {

    /**
     * xId : optional/nullable
     */
    private var xId: Long = 0

    /**
     * xId : optional/nullable
     */
    private var xClassId: Long = 0

    /**
     * xId : (fn=lex unit that is member of frame) optional/nullable
     */
    private var xMemberId: Long = 0

    /**
     * xSources : optional/nullable
     */
    private val xSources: String?

    /**
     * xMask
     */
    private val xMask: Long

    /**
     * xGroup (0=wordnet, 1=verbnet, 2=propbank, 3=framenet, 4=bnc)
     */
    val xGroup: Int

    /**
     * Constructor from parcel, reads back fields IN THE ORDER they were written
     */
    private constructor(parcel: Parcel) : super(parcel) {
        val parcelXId = parcel.readLong()
        if (parcelXId != -1L) {
            xId = parcelXId
        }
        val parcelXClassId = parcel.readLong()
        if (parcelXClassId != -1L) {
            xClassId = parcelXClassId
        }
        val parcelXMemberId = parcel.readLong()
        if (parcelXMemberId != -1L) {
            xMemberId = parcelXMemberId
        }
        xSources = parcel.readString()
        xMask = parcel.readLong()
        xGroup = parcel.readInt()
    }

    /**
     * Constructor
     *
     * @param synsetId  synset id
     * @param wordId    word id
     * @param xId       x id
     * @param xClassId  x class id
     * @param xMemberId x member id
     * @param xSources  x sources
     * @param xMask     x mask
     * @param xGroup    x group id
     */
    constructor(synsetId: Long, wordId: Long, xId: Long, xClassId: Long, xMemberId: Long, xSources: String?, xMask: Long, xGroup: Int) : super(synsetId, wordId) {
        this.xId = xId
        this.xClassId = xClassId
        this.xMemberId = xMemberId
        this.xSources = xSources
        this.xMask = xMask
        this.xGroup = xGroup
    }

    /**
     * Get x id
     *
     * @return target object id
     */
    override fun getXId(): Long? {
        return if (xId == 0L) null else xId
    }

    /**
     * Get class id
     *
     * @return role class id
     */
    override fun getXClassId(): Long? {
        return if (xClassId == 0L) null else xClassId
    }

    /**
     * Get member id (fn=lex unit that is member of frame) id
     *
     * @return role id
     */
    override fun getXMemberId(): Long? {
        return if (xMemberId == 0L) null else xMemberId
    }

    /**
     * Get info sources
     *
     * @return info sources
     */
    override fun getXSources(): String? {
        return xSources
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeLong(xId)
        parcel.writeLong(xClassId)
        parcel.writeLong(xMemberId)
        parcel.writeString(xSources)
        parcel.writeLong(xMask)
        parcel.writeInt(xGroup)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return super.toString() + ' ' + "xid=" +
                xId + ' ' + "xclassid=" +
                xClassId + ' ' + "xmemberid=" +
                xMemberId + ' ' + "xsources=" +
                xSources + ' ' + "xmask=" +
                java.lang.Long.toHexString(xMask) + ' ' + "xgroup=" +
                xGroup
    }

    /**
     * Determine whether WordNet is only source
     *
     * @return whether to expand
     */
    fun wordNetOnly(): Boolean {
        var result = true
        val mask = xMask
        if (mask and WORDNETSOURCE.toLong().inv() != 0L) {
            result = false
        }
        return result
    }

    companion object {

        private const val WORDNETSOURCE = 0x00001
        private const val VERBNETSOURCE = 0x00002
        private const val PROPBANKSOURCE = 0x00004
        private const val FRAMENETSOURCE = 0x00008
        private const val PMVERBNETSOURCE = 0x00020
        private const val PMPROPBANKSOURCE = 0x00040
        private const val PMFRAMENETSOURCE = 0x00080

        /**
         * Set x sources
         *
         * @param xSources x sources
         * @return mask
         */
        fun getMask(xSources: String): Long {
            var mask: Long = 0
            if (xSources.contains("wn")) {
                mask = mask or WORDNETSOURCE.toLong()
            }
            if (xSources.contains("vn")) {
                mask = mask or VERBNETSOURCE.toLong()
            }
            if (xSources.contains("pb")) {
                mask = mask or PROPBANKSOURCE.toLong()
            }
            if (xSources.contains("fn")) {
                mask = mask or FRAMENETSOURCE.toLong()
            }
            if (xSources.contains("pmvn")) {
                mask = mask or PMVERBNETSOURCE.toLong()
            }
            if (xSources.contains("pmpb")) {
                mask = mask or PMPROPBANKSOURCE.toLong()
            }
            if (xSources.contains("pmfn")) {
                mask = mask or PMFRAMENETSOURCE.toLong()
            }
            return mask
        }

        @Suppress("unused")
        @JvmField
        val CREATOR = object : Parcelable.Creator<XSelectorPointer> {

            override fun createFromParcel(parcel: Parcel): XSelectorPointer {
                return XSelectorPointer(parcel)
            }

            override fun newArray(size: Int): Array<XSelectorPointer?> {
                return arrayOfNulls(size)
            }
        }
    }
}
