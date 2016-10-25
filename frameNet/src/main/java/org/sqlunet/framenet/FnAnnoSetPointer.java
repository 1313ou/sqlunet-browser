package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.Pointer;

/**
 * Parcelable annoSet
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnAnnoSetPointer extends Pointer
{
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
	 *
	 * @param annoSetId annoSet id
	 */
	public FnAnnoSetPointer(final long annoSetId)
	{
		super(annoSetId);
	}

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	private FnAnnoSetPointer(final Parcel parcel)
	{
		super(parcel);
	}
}
