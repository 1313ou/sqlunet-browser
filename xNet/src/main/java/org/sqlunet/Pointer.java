package org.sqlunet;

import android.os.Parcel;
import android.support.annotation.NonNull;

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
	static public final Creator<Pointer> CREATOR = new Creator<Pointer>()
	{
		@Override
		public Pointer createFromParcel(@NonNull Parcel in)
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
	protected Pointer(@NonNull final Parcel parcel)
	{
		this.id = parcel.readLong();
	}

	/**
	 * Get id
	 *
	 * @return id
	 */
	public long getId()
	{
		return this.id;
	}

	@Override
	public void writeToParcel(@NonNull final Parcel parcel, final int flags)
	{
		parcel.writeLong(this.id);
	}

	@SuppressWarnings("SameReturnValue")
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
