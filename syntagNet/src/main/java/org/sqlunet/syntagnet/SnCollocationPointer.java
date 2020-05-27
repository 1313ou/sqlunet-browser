/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.Pointer;

import androidx.annotation.NonNull;

/**
 * Parcelable collocation pointer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SnCollocationPointer extends Pointer
{
	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	static public final Parcelable.Creator<SnCollocationPointer> CREATOR = new Parcelable.Creator<SnCollocationPointer>()
	{
		@NonNull
		@Override
		public SnCollocationPointer createFromParcel(@NonNull final Parcel parcel)
		{
			return new SnCollocationPointer(parcel);
		}

		@NonNull
		@Override
		public SnCollocationPointer[] newArray(final int size)
		{
			return new SnCollocationPointer[size];
		}
	};

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 *
	 * @param parcel parcel
	 */
	private SnCollocationPointer(@NonNull final Parcel parcel)
	{
		super(parcel);
	}

	/**
	 * Constructor
	 *
	 * @param roleSetId role set id
	 */
	public SnCollocationPointer(final long roleSetId)
	{
		super(roleSetId);
	}
}
