/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.Pointer;

import androidx.annotation.NonNull;

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
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	private FnSentencePointer(@NonNull final Parcel parcel)
	{
		super(parcel);
	}

	/**
	 * Constructor
	 *
	 * @param sentenceId sentence id
	 */
	public FnSentencePointer(final long sentenceId)
	{
		super(sentenceId);
	}
}
