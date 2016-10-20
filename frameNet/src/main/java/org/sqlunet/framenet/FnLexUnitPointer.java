package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable frame
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnLexUnitPointer implements Parcelable
{
	/**
	 * Members
	 */
	public long luId;

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
		this.luId = pc.readLong();
	}

	@SuppressWarnings("boxing")
	public Long getLuId()
	{
		if (this.luId != 0)
		{
			return this.luId;
		}
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


	@Override
	public void writeToParcel(final Parcel pc, final int flags)
	{
		pc.writeLong(this.luId);
	}


	@Override
	public int describeContents()
	{
		return 0;
	}
}
