package org.sqlunet.framenet;

import org.sqlunet.Pointer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable annoset
 *
 * @author Bernard Bou
 */
public class FnPatternPointer extends Pointer implements Parcelable
{
	/**
	 * Constructor
	 */
	public FnPatternPointer()
	{
		super();
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	private FnPatternPointer(final Parcel pc)
	{
		super(pc);
	}

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<FnPatternPointer> CREATOR = new Parcelable.Creator<FnPatternPointer>()
	{
		@Override
		public FnPatternPointer createFromParcel(final Parcel pc)
		{
			return new FnPatternPointer(pc);
		}

		@Override
		public FnPatternPointer[] newArray(final int size)
		{
			return new FnPatternPointer[size];
		}
	};
}
