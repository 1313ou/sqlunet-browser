package org.sqlunet.verbnet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable class
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VnClassPointer implements Parcelable
{
	/**
	 * Class od
	 */
	public Long classId;

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<VnClassPointer> CREATOR = new Parcelable.Creator<VnClassPointer>()
	{
		@Override
		public VnClassPointer createFromParcel(final Parcel parcel)
		{
			return new VnClassPointer(parcel);
		}

		@Override
		public VnClassPointer[] newArray(final int size)
		{
			return new VnClassPointer[size];
		}
	};

	/**
	 * Constructor
	 */
	public VnClassPointer()
	{
		this.classId = null;
	}

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	@SuppressWarnings("boxing")
	private VnClassPointer(final Parcel parcel)
	{
		this();
		long parcelClassId = parcel.readLong();
		if (parcelClassId != -1)
		{
			this.classId = parcelClassId;
		}
	}

	/**
	 * Get class id
	 *
	 * @return class id
	 */
	public Long getClassId()
	{
		if (this.classId != 0)
		{
			return this.classId;
		}
		return null;
	}

	@Override
	public void writeToParcel(final Parcel parcel, final int flags)
	{
		parcel.writeLong(this.classId == null ? -1 : this.classId);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}
}
