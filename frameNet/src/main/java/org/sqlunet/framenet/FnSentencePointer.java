package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.Pointer;

/**
 * Parcelable sentence
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnSentencePointer extends Pointer
{
	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<FnSentencePointer> CREATOR = new Parcelable.Creator<FnSentencePointer>()
	{
		@Override
		public FnSentencePointer createFromParcel(final Parcel parcel)
		{
			return new FnSentencePointer(parcel);
		}

		@Override
		public FnSentencePointer[] newArray(final int size)
		{
			return new FnSentencePointer[size];
		}
	};

	/**
	 * Constructor
	 *
	 * @param sentenceId sentence id
	 */
	public FnSentencePointer(final long sentenceId)
	{
		super(sentenceId);
	}

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	private FnSentencePointer(final Parcel parcel)
	{
		super(parcel);
	}
}
