package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable sentence
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnSentencePointer implements Parcelable
{
	/**
	 * Sentence id
	 */
	private final long sentenceId;

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<FnSentencePointer> CREATOR = new Parcelable.Creator<FnSentencePointer>()
	{
		@Override
		public FnSentencePointer createFromParcel(final Parcel parcel)
		{
			return new FnSentencePointer(parcel);
		}

		@Override
		public FnSentencePointer[] newArray(final int size)
		{
			return new FnSentencePointer[size];
		}
	};

	/**
	 * Constructor
	 */
	public FnSentencePointer(final long sentenceId)
	{
		this.sentenceId = sentenceId;
	}

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	private FnSentencePointer(final Parcel parcel)
	{
		this.sentenceId = parcel.readLong();
	}

	/**
	 * Get sentence id
	 *
	 * @return sentence id
	 */
	@SuppressWarnings("boxing")
	public Long getSentenceId()
	{
		if (this.sentenceId != 0)
		{
			return this.sentenceId;
		}
		return null;
	}

	@Override
	public void writeToParcel(final Parcel parcel, final int flags)
	{
		parcel.writeLong(this.sentenceId);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}
}
