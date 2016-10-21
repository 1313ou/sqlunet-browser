package org.sqlunet.propbank;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable role set pointer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PbRoleSetPointer implements Parcelable
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
	 * Role set id
	 */
	public Long roleSetId;

	/**
	 * Constructor
	 */
	public PbRoleSetPointer()
	{
		this.roleSetId = null;
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 *
	 * @param parcel parcel
	 */
	@SuppressWarnings("boxing")
	private PbRoleSetPointer(final Parcel parcel)
	{
		this();
		long parcelRoleSetId = parcel.readLong();
		if (parcelRoleSetId != -1)
		{
			this.roleSetId = parcelRoleSetId;
		}
	}

	/**
	 * Get role set id
	 *
	 * @return role set id
	 */
	public Long getRoleSetId()
	{
		if (this.roleSetId != 0)
		{
			return this.roleSetId;
		}
		return null;
	}

	@Override
	public void writeToParcel(final Parcel pc, final int flags)
	{
		pc.writeLong(this.roleSetId == null ? -1 : this.roleSetId);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}
}
