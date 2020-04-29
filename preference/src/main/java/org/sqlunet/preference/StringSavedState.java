/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.preference;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;

/**
 * Saved state
 */
class StringSavedState extends Preference.BaseSavedState
{
	/**
	 * The value
	 */
	@Nullable
	String value;

	/**
	 * Constructor from superstate
	 *
	 * @param superState superstate
	 */
	@SuppressWarnings("WeakerAccess")
	public StringSavedState(final Parcelable superState)
	{
		super(superState);
	}

	/**
	 * Constructor from parcel
	 *
	 * @param parcel source parcel
	 */
	@SuppressWarnings("WeakerAccess")
	public StringSavedState(@NonNull final Parcel parcel)
	{
		super(parcel);

		// get the preference's value
		this.value = parcel.readString();
	}

	@Override
	public void writeToParcel(@NonNull final Parcel dest, final int flags)
	{
		super.writeToParcel(dest, flags);

		// write the preference's value
		dest.writeString(this.value);
	}

	/**
	 * Standard creator object using an instance of this class
	 */
	public static final Creator<StringSavedState> CREATOR = new Creator<StringSavedState>()
	{
		@NonNull
		@Override
		public StringSavedState createFromParcel(@NonNull final Parcel in)
		{
			return new StringSavedState(in);
		}

		@NonNull
		@Override
		public StringSavedState[] newArray(final int size)
		{
			return new StringSavedState[size];
		}
	};
}
