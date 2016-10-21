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
	 * Word
	 */
	public String word;

	/**
	 * Creator
	 */
	public static final Creator<Word> CREATOR = new Creator<Word>()
	{
		@Override
		public Word createFromParcel(Parcel parcel)
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
	 * Constructor
	 */
	public Word()
	{
	}

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	@SuppressWarnings("unused")
	public Word(final Parcel parcel)
	{
		this.word = parcel.readString();
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
	public void writeToParcel(final Parcel parcel, final int flags)
	{
		parcel.writeString(this.word);
	}

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
