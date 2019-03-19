package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import org.sqlunet.Pointer;

/**
 * Parcelable frame
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnFramePointer extends Pointer
{
	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	static public final Parcelable.Creator<FnFramePointer> CREATOR = new Parcelable.Creator<FnFramePointer>()
	{
		@Override
		public FnFramePointer createFromParcel(@NonNull final Parcel parcel)
		{
			return new FnFramePointer(parcel);
		}

		@Override
		public FnFramePointer[] newArray(final int size)
		{
			return new FnFramePointer[size];
		}
	};

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	private FnFramePointer(@NonNull final Parcel parcel)
	{
		super(parcel);
	}

	/**
	 * Constructor
	 *
	 * @param frameId frame id
	 */
	public FnFramePointer(final long frameId)
	{
		super(frameId);
	}
}
