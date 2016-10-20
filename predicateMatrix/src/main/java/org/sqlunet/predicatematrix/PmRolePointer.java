package org.sqlunet.predicatematrix;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable frame
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PmRolePointer implements Parcelable
{
	/**
	 * Members
	 */
	public Long roleId;

	/**
	 * Constructor
	 */
	public PmRolePointer()
	{
		this.roleId = null;
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 *
	 * @param parcel parcel
	 */
	@SuppressWarnings("boxing")
	private PmRolePointer(final Parcel parcel)
	{
		this();
		long parcelRoleId = parcel.readLong();
		if (parcelRoleId != -1)
		{
			this.roleId = parcelRoleId;
		}
	}

	/**
	 * Get role id
	 *
	 * @return return role id
	 */
	public Long getRoleId()
	{
		if (this.roleId != 0)
		{
			return this.roleId;
		}
		return null;
	}

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<PmRolePointer> CREATOR = new Parcelable.Creator<PmRolePointer>()
	{
		@Override
		public PmRolePointer createFromParcel(final Parcel pc)
		{
			return new PmRolePointer(pc);
		}

		@Override
		public PmRolePointer[] newArray(final int size)
		{
			return new PmRolePointer[size];
		}
	};

	@Override
	public void writeToParcel(final Parcel pc, final int flags)
	{
		pc.writeLong(this.roleId == null ? -1 : this.roleId);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return "roleId=" + this.roleId; //$NON-NLS-1$
	}
}
