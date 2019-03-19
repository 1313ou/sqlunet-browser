package org.sqlunet.propbank;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import org.sqlunet.Pointer;

/**
 * Parcelable role set pointer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PbRoleSetPointer extends Pointer
{
	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	static public final Parcelable.Creator<PbRoleSetPointer> CREATOR = new Parcelable.Creator<PbRoleSetPointer>()
	{
		@Override
		public PbRoleSetPointer createFromParcel(@NonNull final Parcel parcel)
		{
			return new PbRoleSetPointer(parcel);
		}

		@Override
		public PbRoleSetPointer[] newArray(final int size)
		{
			return new PbRoleSetPointer[size];
		}
	};

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 *
	 * @param parcel parcel
	 */
	private PbRoleSetPointer(@NonNull final Parcel parcel)
	{
		super(parcel);
	}

	/**
	 * Constructor
	 *
	 * @param roleSetId role set id
	 */
	public PbRoleSetPointer(final long roleSetId)
	{
		super(roleSetId);
	}
}
