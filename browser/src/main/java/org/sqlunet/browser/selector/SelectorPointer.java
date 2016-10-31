package org.sqlunet.browser.selector;

import android.os.Parcel;

import org.sqlunet.wordnet.SensePointer;

/**
 * SelectorPointer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SelectorPointer extends SensePointer
{
	/**
	 * Constructor
	 *
	 * @param synsetId synset id
	 * @param pos      pos
	 * @param wordId   word id
	 * @param lemma    lemma
	 * @param cased    cased
	 */
	public SelectorPointer(final long synsetId, final String pos, final long wordId, final String lemma, final String cased)
	{
		super(synsetId, pos, wordId, lemma, cased);
	}

	/**
	 * Constructor from parcel
	 *
	 * @param parcel parcel
	 */
	protected SelectorPointer(Parcel parcel)
	{
		super(parcel);
	}
}
