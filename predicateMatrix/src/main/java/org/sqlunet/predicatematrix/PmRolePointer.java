package org.sqlunet.predicatematrix;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable frame
 *
 * @author Bernard Bou
 */
public class PmRolePointer implements Parcelable
{
	/**
	 * Members
	 */
	public Long roleid;

	/**
	 * Constructor
	 */
	public PmRolePointer()
	{
		this.roleid = null;
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	@SuppressWarnings("boxing")
	private PmRolePointer(final Parcel pc)
	{
		this();
		long roleid0 = pc.readLong();
		if (roleid0 != -1)
		{
			this.roleid = roleid0;
		}
	}

	public Long getRoleId()
	{
		if (this.roleid != 0)
		{
			return this.roleid;
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

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(final Parcel pc, final int flags)
	{
		pc.writeLong(this.roleid == null ? -1 : this.roleid);
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return "roleid=" + this.roleid; //$NON-NLS-1$
	}
}
