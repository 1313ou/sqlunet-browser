package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable annoSet
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnAnnoSetPointer implements Parcelable
{
	/**
	 * AnnoSet id
	 */
	public long annoSetId;

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<FnAnnoSetPointer> CREATOR = new Parcelable.Creator<FnAnnoSetPointer>()
	{
		@Override
		public FnAnnoSetPointer createFromParcel(final Parcel parcel)
		{
			return new FnAnnoSetPointer(parcel);
		}

		@Override
		public FnAnnoSetPointer[] newArray(final int size)
		{
			return new FnAnnoSetPointer[size];
		}
	};

	/**
	 * Constructor
	 */
	public FnAnnoSetPointer()
	{
		//
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	private FnAnnoSetPointer(final Parcel parcel)
	{
		this.annoSetId = parcel.readLong();
	}

	/**
	 * Get annoSetId
	 *
	 * @return annoSetId
	 */
	@SuppressWarnings("boxing")
	public Long getAnnoSetId()
	{
		if (this.annoSetId != 0)
		{
			return this.annoSetId;
		}
		return null;
	}

	@Override
	public void writeToParcel(final Parcel parcel, final int flags)
	{
		parcel.writeLong(this.annoSetId);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}
}
