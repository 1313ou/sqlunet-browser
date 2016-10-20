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
	 * Members
	 */
	public Long classId;

	/**
	 * Constructor
	 */
	public VnClassPointer()
	{
		this.classId = null;
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
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

	public Long getClassId()
	{
		if (this.classId != 0)
		{
			return this.classId;
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

	@Override
	public void writeToParcel(final Parcel pc, final int flags)
	{
		pc.writeLong(this.classId == null ? -1 : this.classId);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}
}
