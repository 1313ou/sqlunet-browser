/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.HasSenseKey;

import androidx.annotation.NonNull;

/**
 * Parcelable sensekey
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SenseKeyPointer implements Parcelable, HasSenseKey
{
	private final String senseKey;

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	static public final Creator<SenseKeyPointer> CREATOR = new Creator<SenseKeyPointer>()
	{
		@Override
		public SenseKeyPointer createFromParcel(@NonNull final Parcel parcel)
		{
			return new SenseKeyPointer(parcel);
		}

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
	public SenseKeyPointer(final String senseKey)
	{
		this.senseKey = senseKey;
	}

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
