package org.sqlunet.predicatematrix;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.Pointer;

/**
 * Parcelable PredicateMatrix role
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PmRolePointer extends Pointer
{
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
	 *
	 * @param roleId role id
	 */
	public PmRolePointer(final long roleId)
	{
		super(roleId);
	}


	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 *
	 * @param parcel parcel
	 */
	private PmRolePointer(final Parcel parcel)
	{
		super(parcel);
	}
}
