package org.sqlunet.verbnet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable class
 *
 * @author Bernard Bou
 */
public class VnClassPointer implements Parcelable
{
	/**
	 * Members
	 */
	public Long classid;

	/**
	 * Constructor
	 */
	public VnClassPointer()
	{
		this.classid = null;
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	@SuppressWarnings("boxing")
	private VnClassPointer(final Parcel pc)
	{
		this();
		long classid0 = pc.readLong();
		if (classid0 != -1)
		{
			this.classid = classid0;
		}
	}

	public Long getClassId()
	{
		if (this.classid != 0)
		{
			return this.classid;
		}
		return null;
	}

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<VnClassPointer> CREATOR = new Parcelable.Creator<VnClassPointer>()
	{
		@Override
		public VnClassPointer createFromParcel(final Parcel pc)
		{
			return new VnClassPointer(pc);
		}

		@Override
		public VnClassPointer[] newArray(final int size)
		{
			return new VnClassPointer[size];
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
		pc.writeLong(this.classid == null ? -1 : this.classid);
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
