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
	 * Members
	 */
	private final long sentenceId;

	/**
	 * Constructor
	 */
	public FnSentencePointer(final long sentenceId)
	{
		this.sentenceId = sentenceId;
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	private FnSentencePointer(final Parcel pc)
	{
		this.sentenceId = pc.readLong();
	}

	@SuppressWarnings("boxing")
	public Long getSentenceId()
	{
		if (this.sentenceId != 0)
		{
			return this.sentenceId;
		}
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

	@Override
	public void writeToParcel(final Parcel pc, final int flags)
	{
		pc.writeLong(this.sentenceId);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}
}
