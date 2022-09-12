/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.predicatematrix;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.Pointer;

import androidx.annotation.NonNull;

/**
 * Parcelable PredicateMatrix role
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PmRolePointer extends Pointer
{
	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	static public final Parcelable.Creator<PmRolePointer> CREATOR = new Parcelable.Creator<PmRolePointer>()
	{
		@NonNull
		@Override
		public PmRolePointer createFromParcel(@NonNull final Parcel parcel)
		{
			return new PmRolePointer(parcel);
		}

		@NonNull
		@Override
		public PmRolePointer[] newArray(final int size)
		{
			return new PmRolePointer[size];
		}
	};

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 *
	 * @param parcel parcel
	 */
	private PmRolePointer(@NonNull final Parcel parcel)
	{
		super(parcel);
	}

	/**
	 * Constructor
	 *
	 * @param roleId role id
	 */
	public PmRolePointer(final long roleId)
	{
		super(roleId);
	}
}
