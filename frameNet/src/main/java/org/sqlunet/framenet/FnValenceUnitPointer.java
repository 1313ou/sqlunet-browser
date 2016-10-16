package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.Pointer;

/**
 * Parcelable annoset
 *
 * @author Bernard Bou
 */
public class FnValenceUnitPointer extends Pointer implements Parcelable
{
	/**
	 * Constructor
	 */
	public FnValenceUnitPointer()
	{
		super();
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	private FnValenceUnitPointer(final Parcel pc)
	{
		super(pc);
	}

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<FnValenceUnitPointer> CREATOR = new Parcelable.Creator<FnValenceUnitPointer>()
	{
		@Override
		public FnValenceUnitPointer createFromParcel(final Parcel pc)
		{
			return new FnValenceUnitPointer(pc);
		}

		@Override
		public FnValenceUnitPointer[] newArray(final int size)
		{
			return new FnValenceUnitPointer[size];
		}
	};
}
