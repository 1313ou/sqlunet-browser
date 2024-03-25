/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.vn.selector

import android.os.Parcel
import org.sqlunet.wordnet.SensePointer

/**
 * SelectorPointer
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class SelectorPointer : SensePointer {

    /**
     * Constructor from parcel
     *
     * @param parcel parcel
     */
    protected constructor(parcel: Parcel) : super(parcel)

    /**
     * Constructor
     *
     * @param synsetId synset id
     * @param wordId   word id
     */
    protected constructor(synsetId: Long, wordId: Long) : super(synsetId, wordId)
}
