package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.Pointer;

/**
 * Parcelable pattern
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnPatternPointer extends Pointer
{
	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	static public final Parcelable.Creator<FnPatternPointer> CREATOR = new Parcelable.Creator<FnPatternPointer>()
	{
		@Override
		public FnPatternPointer createFromParcel(final Parcel parcel)
		{
			return new FnPatternPointer(parcel);
		}

		@Override
		public FnPatternPointer[] newArray(final int size)
		{
			return new FnPatternPointer[size];
		}
	};

	/**
	 * Constructor
	 *
	 * @param patternId pattern id
	 */
	public FnPatternPointer(final long patternId)
	{
		super(patternId);
	}

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	private FnPatternPointer(final Parcel parcel)
	{
		super(parcel);
	}
}
