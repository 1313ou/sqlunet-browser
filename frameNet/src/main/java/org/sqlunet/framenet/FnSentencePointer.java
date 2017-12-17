package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

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
	static public final Parcelable.Creator<FnSentencePointer> CREATOR = new Parcelable.Creator<FnSentencePointer>()
	{
		@Override
		public FnSentencePointer createFromParcel(@NonNull final Parcel parcel)
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
	private FnSentencePointer(@NonNull final Parcel parcel)
	{
		super(parcel);
	}
}
