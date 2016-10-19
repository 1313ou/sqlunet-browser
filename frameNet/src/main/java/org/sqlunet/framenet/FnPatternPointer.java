package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.Pointer;

/**
 * Parcelable annoset
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnPatternPointer extends Pointer
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
