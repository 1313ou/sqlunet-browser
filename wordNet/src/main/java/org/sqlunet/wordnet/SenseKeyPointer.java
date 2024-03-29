/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.HasSenseKey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Parcelable sensekey
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SenseKeyPointer implements Parcelable, HasSenseKey
{
	@Nullable
	private final String senseKey;

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	static public final Creator<SenseKeyPointer> CREATOR = new Creator<SenseKeyPointer>()
	{
		@NonNull
		@Override
		public SenseKeyPointer createFromParcel(@NonNull final Parcel parcel)
		{
			return new SenseKeyPointer(parcel);
		}

		@NonNull
		@Override
		public SenseKeyPointer[] newArray(final int size)
		{
			return new SenseKeyPointer[size];
		}
	};

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	private SenseKeyPointer(@NonNull final Parcel parcel)
	{
		this.senseKey = parcel.readString();
	}

	/**
	 * Constructor
	 *
	 * @param senseKey sense key
	 */
	public SenseKeyPointer(@Nullable final String senseKey)
	{
		this.senseKey = senseKey;
	}

	@Nullable
	@Override
	public String getSenseKey()
	{
		return this.senseKey;
	}

	@Override
	public void writeToParcel(@NonNull final Parcel parcel, final int flags)
	{
		parcel.writeString(this.senseKey);
	}

	@SuppressWarnings("SameReturnValue")
	@Override
	public int describeContents()
	{
		return 0;
	}

	@NonNull
	@Override
	public String toString()
	{
		return "sensekey=" + //
				this.senseKey;
	}
}
