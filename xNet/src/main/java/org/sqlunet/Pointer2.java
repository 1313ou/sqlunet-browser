package org.sqlunet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable id pointer + string pointer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Pointer2 extends Pointer
{
	/**
	 * Id2
	 */
	protected final String id2;

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<Pointer2> CREATOR = new Parcelable.Creator<Pointer2>()
	{
		@Override
		public Pointer2 createFromParcel(final Parcel parcel)
		{
			return new Pointer2(parcel);
		}

		@Override
		public Pointer2[] newArray(final int size)
		{
			return new Pointer2[size];
		}
	};

	/**
	 * Constructor
	 *
	 * @param id  id
	 * @param id2 id2
	 */
	protected Pointer2(final long id, final String id2)
	{
		super(id);
		this.id2 = id2;
	}

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	protected Pointer2(final Parcel parcel)
	{
		super(parcel);
		this.id2 = parcel.readString();
	}

	/**
	 * Get id2
	 *
	 * @return frame id
	 */
	@SuppressWarnings({"unused"})
	public String getId2()
	{
		return this.id2;
	}

	@Override
	public void writeToParcel(final Parcel parcel, final int flags)
	{
		super.writeToParcel(parcel, flags);
		parcel.writeString(this.id2);
	}
}
