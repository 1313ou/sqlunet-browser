package org.sqlunet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable word
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Word implements Parcelable
{
	/**
	 * Members
	 */
	public String word;

	/**
	 * Constructor
	 */
	public Word()
	{
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	@SuppressWarnings("unused")
	public Word(final Parcel pc)
	{
		this.word = pc.readString();
	}

	/**
	 * Creator
	 */
	public static final Creator<Word> CREATOR = new Creator<Word>()
	{
		@Override
		public Word createFromParcel(Parcel in)
		{
			return new Word(in);
		}

		@Override
		public Word[] newArray(int size)
		{
			return new Word[size];
		}
	};

	/**
	 * Get word
	 *
	 * @return word
	 */
	public String getWord()
	{
		return this.word;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(final Parcel pc, final int flags)
	{
		pc.writeString(this.word);
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return this.word;
	}
}
