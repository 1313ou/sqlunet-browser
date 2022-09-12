/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.Pointer;

import androidx.annotation.NonNull;

/**
 * Parcelable annoSet
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnAnnoSetPointer extends Pointer
{
	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	static public final Parcelable.Creator<FnAnnoSetPointer> CREATOR = new Parcelable.Creator<FnAnnoSetPointer>()
	{
		@NonNull
		@Override
		public FnAnnoSetPointer createFromParcel(@NonNull final Parcel parcel)
		{
			return new FnAnnoSetPointer(parcel);
		}

		@NonNull
		@Override
		public FnAnnoSetPointer[] newArray(final int size)
		{
			return new FnAnnoSetPointer[size];
		}
	};

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	private FnAnnoSetPointer(@NonNull final Parcel parcel)
	{
		super(parcel);
	}

	/**
	 * Constructor
	 *
	 * @param annoSetId annoSet id
	 */
	public FnAnnoSetPointer(final long annoSetId)
	{
		super(annoSetId);
	}
}
