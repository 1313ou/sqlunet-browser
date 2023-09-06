/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.HasWordId;

import androidx.annotation.NonNull;

/**
 * Parcelable sense
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SensePointer extends SynsetPointer implements HasWordId
{
	/**
	 * Word id
	 */
	private final long wordId;

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	static public final Parcelable.Creator<SensePointer> CREATOR = new Parcelable.Creator<SensePointer>()
	{
		@NonNull
		@Override
		public SensePointer createFromParcel(@NonNull final Parcel parcel)
		{
			return new SensePointer(parcel);
		}

		@NonNull
		@Override
		public SensePointer[] newArray(final int size)
		{
			return new SensePointer[size];
		}
	};

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	protected SensePointer(@NonNull final Parcel parcel)
	{
		super(parcel);
		this.wordId = parcel.readLong();
	}

	/**
	 * Constructor
	 *
	 * @param synsetId synset id
	 * @param wordId   word id
	 */
	public SensePointer(final long synsetId, final long wordId)
	{
		super(synsetId);
		this.wordId = wordId;
	}

	@Override
	public long getWordId()
	{
		return this.wordId;
	}

	@Override
	public void writeToParcel(@NonNull final Parcel parcel, final int flags)
	{
		super.writeToParcel(parcel, flags);
		parcel.writeLong(this.wordId);
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
		return super.toString() + ' ' + "wordid=" + this.wordId;
	}
}
