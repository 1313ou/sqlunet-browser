package org.sqlunet.propbank;

import android.os.Parcel;
import android.os.Parcelable;

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
	public static final Parcelable.Creator<PbRoleSetPointer> CREATOR = new Parcelable.Creator<PbRoleSetPointer>()
	{
		@Override
		public PbRoleSetPointer createFromParcel(final Parcel parcel)
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
	 * Constructor
	 *
	 * @param roleSetId role set id
	 */
	public PbRoleSetPointer(final long roleSetId)
	{
		super(roleSetId);
	}

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 *
	 * @param parcel parcel
	 */
	@SuppressWarnings("boxing")
	private PbRoleSetPointer(final Parcel parcel)
	{
		super(parcel);
	}
}
