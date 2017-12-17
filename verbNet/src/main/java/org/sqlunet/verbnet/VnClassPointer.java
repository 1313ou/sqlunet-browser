package org.sqlunet.verbnet;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.sqlunet.Pointer;

/**
 * Parcelable class
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VnClassPointer extends Pointer
{
	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	static public final Parcelable.Creator<VnClassPointer> CREATOR = new Parcelable.Creator<VnClassPointer>()
	{
		@Override
		public VnClassPointer createFromParcel(@NonNull final Parcel parcel)
		{
			return new VnClassPointer(parcel);
		}

		@Override
		public VnClassPointer[] newArray(final int size)
		{
			return new VnClassPointer[size];
		}
	};

	/**
	 * Constructor
	 *
	 * @param classId class id
	 */
	public VnClassPointer(final long classId)
	{
		super(classId);
	}

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	private VnClassPointer(@NonNull final Parcel parcel)
	{
		super(parcel);
	}
}
