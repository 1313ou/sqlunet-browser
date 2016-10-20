package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.Pointer;

/**
 * Parcelable valence unit
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnValenceUnitPointer extends Pointer
{
	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<FnValenceUnitPointer> CREATOR = new Parcelable.Creator<FnValenceUnitPointer>()
	{
		@Override
		public FnValenceUnitPointer createFromParcel(final Parcel parcel)
		{
			return new FnValenceUnitPointer(parcel);
		}

		@Override
		public FnValenceUnitPointer[] newArray(final int size)
		{
			return new FnValenceUnitPointer[size];
		}
	};

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
	private FnValenceUnitPointer(final Parcel parcel)
	{
		super(parcel);
	}
}
