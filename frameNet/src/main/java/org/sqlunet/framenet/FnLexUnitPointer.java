package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable frame
 *
 * @author Bernard Bou
 */
public class FnLexUnitPointer implements Parcelable
{
	/**
	 * Members
	 */
	public long luid;

	/**
	 * Constructor
	 */
	public FnLexUnitPointer()
	{
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	private FnLexUnitPointer(final Parcel pc)
	{
		this.luid = pc.readLong();
	}

	@SuppressWarnings("boxing")
	public Long getLuId()
	{
		if (this.luid != 0)
			return this.luid;
		return null;
	}

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<FnLexUnitPointer> CREATOR = new Parcelable.Creator<FnLexUnitPointer>()
			{
		@Override
		public FnLexUnitPointer createFromParcel(final Parcel pc)
		{
			return new FnLexUnitPointer(pc);
		}

		@Override
		public FnLexUnitPointer[] newArray(final int size)
		{
			return new FnLexUnitPointer[size];
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
				pc.writeLong(this.luid);
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
