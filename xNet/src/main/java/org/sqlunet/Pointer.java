package org.sqlunet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable pointer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Pointer implements Parcelable
{
	/**
	 * Members
	 */
	public long id;

	/**
	 * Constructor
	 */
	protected Pointer()
	{
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	protected Pointer(final Parcel pc)
	{
		this.id = pc.readLong();
	}

	/**
	 * Creator
	 */
	public static final Creator<Pointer> CREATOR = new Creator<Pointer>()
	{
		@Override
		public Pointer createFromParcel(Parcel in)
		{
			return new Pointer(in);
		}

		@Override
		public Pointer[] newArray(int size)
		{
			return new Pointer[size];
		}
	};

	/**
	 * Get id
	 *
	 * @return id
	 */
	@SuppressWarnings("boxing")
	public Long getId()
	{
		if (this.id != 0)
		{
			return this.id;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(final Parcel pc, final int flags)
	{
		pc.writeLong(this.id);
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return Long.toString(this.id);
	}
}
