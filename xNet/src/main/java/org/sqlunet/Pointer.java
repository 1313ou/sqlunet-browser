package org.sqlunet;

import android.os.Parcel;

/**
 * Parcelable id pointer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Pointer implements IPointer
{
	/**
	 * Id of pointed-to object
	 */
	protected final long id;

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
	 * Constructor
	 *
	 * @param id id
	 */
	protected Pointer(final long id)
	{
		this.id = id;
	}

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	protected Pointer(final Parcel parcel)
	{
		this.id = parcel.readLong();
	}

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

	@Override
	public void writeToParcel(final Parcel parcel, final int flags)
	{
		parcel.writeLong(this.id);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return Long.toString(this.id);
	}
}
