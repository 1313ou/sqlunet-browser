package org.sqlunet.predicatematrix;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable PredicateMatrix role
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PmRolePointer implements Parcelable
{
	/**
	 * Role id
	 */
	public Long roleId;

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<PmRolePointer> CREATOR = new Parcelable.Creator<PmRolePointer>()
	{
		@Override
		public PmRolePointer createFromParcel(final Parcel parcel)
		{
			return new PmRolePointer(parcel);
		}

		@Override
		public PmRolePointer[] newArray(final int size)
		{
			return new PmRolePointer[size];
		}
	};

	/**
	 * Constructor
	 */
	public PmRolePointer()
	{
		this.roleId = null;
	}

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
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

	@Override
	public void writeToParcel(final Parcel parcel, final int flags)
	{
		parcel.writeLong(this.roleId == null ? -1 : this.roleId);
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
