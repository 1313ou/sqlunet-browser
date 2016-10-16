package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable sentence
 *
 * @author Bernard Bou
 */
public class FnSentencePointer implements Parcelable
{
	/**
	 * Members
	 */
	private final long sentenceid;

	/**
	 * Constructor
	 */
	public FnSentencePointer(final long sentenceid0)
	{
		this.sentenceid = sentenceid0;
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	private FnSentencePointer(final Parcel pc)
	{
		this.sentenceid = pc.readLong();
	}

	@SuppressWarnings("boxing")
	public Long getSentenceId()
	{
		if (this.sentenceid != 0)
			return this.sentenceid;
		return null;
	}

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<FnSentencePointer> CREATOR = new Parcelable.Creator<FnSentencePointer>()
	{
		@Override
		public FnSentencePointer createFromParcel(final Parcel pc)
		{
			return new FnSentencePointer(pc);
		}

		@Override
		public FnSentencePointer[] newArray(final int size)
		{
			return new FnSentencePointer[size];
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(final Parcel pc, final int flags)
	{
		pc.writeLong(this.sentenceid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents()
	{
		return 0;
	}
}
