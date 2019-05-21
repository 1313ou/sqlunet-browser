/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.Pointer;

import androidx.annotation.NonNull;

/**
 * Parcelable lex unit
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnLexUnitPointer extends Pointer
{
	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	static public final Parcelable.Creator<FnLexUnitPointer> CREATOR = new Parcelable.Creator<FnLexUnitPointer>()
	{
		@Override
		public FnLexUnitPointer createFromParcel(@NonNull final Parcel parcel)
		{
			return new FnLexUnitPointer(parcel);
		}

		@Override
		public FnLexUnitPointer[] newArray(final int size)
		{
			return new FnLexUnitPointer[size];
		}
	};

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	private FnLexUnitPointer(@NonNull final Parcel parcel)
	{
		super(parcel);
	}

	/**
	 * Constructor
	 *
	 * @param luId lex unit id
	 */
	public FnLexUnitPointer(final long luId)
	{
		super(luId);
	}
}
