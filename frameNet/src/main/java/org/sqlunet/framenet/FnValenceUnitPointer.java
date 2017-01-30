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
	static public final Parcelable.Creator<FnValenceUnitPointer> CREATOR = new Parcelable.Creator<FnValenceUnitPointer>()
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
	 *
	 * @param vuId valence unit id
	 */
	public FnValenceUnitPointer(final long vuId)
	{
		super(vuId);
	}

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	private FnValenceUnitPointer(final Parcel parcel)
	{
		super(parcel);
	}
}
