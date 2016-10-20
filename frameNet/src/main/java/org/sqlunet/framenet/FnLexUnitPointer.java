package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable lex unit
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnLexUnitPointer implements Parcelable
{
	/**
	 * Lex unit id
	 */
	public long luId;

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<FnLexUnitPointer> CREATOR = new Parcelable.Creator<FnLexUnitPointer>()
	{
		@Override
		public FnLexUnitPointer createFromParcel(final Parcel parcel)
		{
			return new FnLexUnitPointer(parcel);
		}

		@Override
		public FnLexUnitPointer[] newArray(final int size)
		{
			return new FnLexUnitPointer[size];
		}
	};

	/**
	 * Constructor
	 */
	public FnLexUnitPointer()
	{
		//
	}

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	private FnLexUnitPointer(final Parcel parcel)
	{
		this.luId = parcel.readLong();
	}

	/**
	 * Get lex unit id
	 *
	 * @return lex unit id
	 */
	@SuppressWarnings("boxing")
	public Long getLuId()
	{
		if (this.luId != 0)
		{
			return this.luId;
		}
		return null;
	}

	@Override
	public void writeToParcel(final Parcel parcel, final int flags)
	{
		parcel.writeLong(this.luId);
	}


	@Override
	public int describeContents()
	{
		return 0;
	}
}
