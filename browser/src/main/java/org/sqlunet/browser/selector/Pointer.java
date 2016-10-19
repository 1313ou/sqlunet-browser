package org.sqlunet.browser.selector;

import android.os.Parcel;

import org.sqlunet.wordnet.SensePointer;

/**
 * Pointer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Pointer extends SensePointer
{
	/**
	 * Constructor
	 */
	public Pointer()
	{
		super();
	}

	/**
	 * Constructor from parcel
	 *
	 * @param parcel parcel
	 */
	protected Pointer(Parcel parcel)
	{
		super(parcel);
	}
}
