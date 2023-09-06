/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet;

import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
	@Nullable
	private final String word;

	/**
	 * Creator
	 */
	static public final Creator<Word> CREATOR = new Creator<Word>()
	{
		@NonNull
		@Override
		public Word createFromParcel(@NonNull Parcel parcel)
		{
			return new Word(parcel);
		}

		@NonNull
		@Override
		public Word[] newArray(int size)
		{
			return new Word[size];
		}
	};

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	private Word(@NonNull final Parcel parcel)
	{
		this.word = parcel.readString();
	}

	/**
	 * Constructor
	 *
	 * @param word word
	 */
	public Word(@Nullable final String word)
	{
		this.word = word;
	}

	/**
	 * Get word
	 *
	 * @return word
	 */
	@Nullable
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
		return this.word == null ? "" : this.word;
	}
}
