package org.sqlunet.wordnet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.HasWordId;

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
	public static final Parcelable.Creator<SensePointer> CREATOR = new Parcelable.Creator<SensePointer>()
	{
		@Override
		public SensePointer createFromParcel(final Parcel parcel)
		{
			return new SensePointer(parcel);
		}

		@Override
		public SensePointer[] newArray(final int size)
		{
			return new SensePointer[size];
		}
	};

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	protected SensePointer(final Parcel parcel)
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
	public void writeToParcel(final Parcel parcel, final int flags)
	{
		super.writeToParcel(parcel, flags);
		parcel.writeLong(this.wordId);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return super.toString() + ' ' + "wordid=" + Long.toString(this.wordId);
	}
}
