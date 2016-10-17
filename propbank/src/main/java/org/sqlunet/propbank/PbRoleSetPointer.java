package org.sqlunet.propbank;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable roleset
 *
 * @author Bernard Bou
 */
public class PbRoleSetPointer implements Parcelable
{
	/**
	 * Members
	 */
	public Long rolesetid;

	/**
	 * Constructor
	 */
	public PbRoleSetPointer()
	{
		this.rolesetid = null;
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	@SuppressWarnings("boxing")
	private PbRoleSetPointer(final Parcel pc)
	{
		this();
		long frameid0 = pc.readLong();
		if (frameid0 != -1)
		{
			this.rolesetid = frameid0;
		}
	}

	public Long getRoleSetId()
	{
		if (this.rolesetid != 0)
		{
			return this.rolesetid;
		}
		return null;
	}

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<PbRoleSetPointer> CREATOR = new Parcelable.Creator<PbRoleSetPointer>()
	{
		@Override
		public PbRoleSetPointer createFromParcel(final Parcel pc)
		{
			return new PbRoleSetPointer(pc);
		}

		@Override
		public PbRoleSetPointer[] newArray(final int size)
		{
			return new PbRoleSetPointer[size];
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(final Parcel pc, final int flags)
	{
		pc.writeLong(this.rolesetid == null ? -1 : this.rolesetid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents()
	{
		return 0;
	}
}
