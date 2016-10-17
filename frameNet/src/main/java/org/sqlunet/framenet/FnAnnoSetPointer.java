package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable annoset
 *
 * @author Bernard Bou
 */
public class FnAnnoSetPointer implements Parcelable
{
	/**
	 * Members
	 */
	public long annosetid;

	/**
	 * Constructor
	 */
	public FnAnnoSetPointer()
	{
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	private FnAnnoSetPointer(final Parcel pc)
	{
		this.annosetid = pc.readLong();
	}

	@SuppressWarnings("boxing")
	public Long getAnnoSetId()
	{
		if (this.annosetid != 0)
		{
			return this.annosetid;
		}
		return null;
	}

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<FnAnnoSetPointer> CREATOR = new Parcelable.Creator<FnAnnoSetPointer>()
	{
		@Override
		public FnAnnoSetPointer createFromParcel(final Parcel pc)
		{
			return new FnAnnoSetPointer(pc);
		}

		@Override
		public FnAnnoSetPointer[] newArray(final int size)
		{
			return new FnAnnoSetPointer[size];
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
		pc.writeLong(this.annosetid);
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
