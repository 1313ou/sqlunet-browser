/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.vn.selector;

import android.os.Parcel;

import org.sqlunet.wordnet.SensePointer;

import androidx.annotation.NonNull;

/**
 * SelectorPointer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SelectorPointer extends SensePointer
{
	/**
	 * Constructor from parcel
	 *
	 * @param parcel parcel
	 */
	protected SelectorPointer(@NonNull Parcel parcel)
	{
		super(parcel);
	}

	/**
	 * Constructor
	 *
	 * @param synsetId synset id
	 * @param wordId   word id
	 */
	protected SelectorPointer(final long synsetId, final long wordId)
	{
		super(synsetId, wordId);
	}
}
