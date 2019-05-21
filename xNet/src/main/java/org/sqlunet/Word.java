/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet;

import android.os.Parcel;

import androidx.annotation.NonNull;

/**
 * Parcelable word
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Word implements IPointer
{
	/**
	 * Word
	 */
	private final String word;

	/**
	 * Creator
	 */
	static public final Creator<Word> CREATOR = new Creator<Word>()
	{
		@Override
		public Word createFromParcel(@NonNull Parcel parcel)
		{
			return new Word(parcel);
		}

		@Override
		public Word[] newArray(int size)
		{
			return new Word[size];
		}
	};

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	@SuppressWarnings("unused")
	private Word(@NonNull final Parcel parcel)
	{
		this.word = parcel.readString();
	}

	/**
	 * Constructor
	 *
	 * @param word word
	 */
	public Word(final String word)
	{
		this.word = word;
	}

	/**
	 * Get word
	 *
	 * @return word
	 */
	public String getWord()
	{
		return this.word;
	}

	@Override
	public void writeToParcel(@NonNull final Parcel parcel, final int flags)
	{
		parcel.writeString(this.word);
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
		return this.word;
	}
}
