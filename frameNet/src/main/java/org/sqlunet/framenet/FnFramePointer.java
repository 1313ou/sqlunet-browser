package org.sqlunet.framenet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable frame
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnFramePointer implements Parcelable
{
	/**
	 * Members
	 */
	public Long frameid;

	private String pos;

	/**
	 * Constructor
	 */
	public FnFramePointer()
	{
		this.frameid = null;
		this.pos = null;
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	@SuppressWarnings("boxing")
	private FnFramePointer(final Parcel pc)
	{
		this();
		long frameid0 = pc.readLong();
		if (frameid0 != -1)
		{
			this.frameid = frameid0;
		}
		this.pos = pc.readString();
	}

	@SuppressWarnings({"boxing", "unused"})
	public Character getPos()
	{
		if (this.pos != null && !this.pos.isEmpty())
		{
			return this.pos.charAt(0);
		}
		return null;
	}

	@SuppressWarnings({"unused"})
	public Long getFrameId()
	{
		if (this.frameid != 0)
		{
			return this.frameid;
		}
		return null;
	}

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<FnFramePointer> CREATOR = new Parcelable.Creator<FnFramePointer>()
	{
		@Override
		public FnFramePointer createFromParcel(final Parcel pc)
		{
			return new FnFramePointer(pc);
		}

		@Override
		public FnFramePointer[] newArray(final int size)
		{
			return new FnFramePointer[size];
		}
	};

	@Override
	public void writeToParcel(final Parcel pc, final int flags)
	{
		pc.writeLong(this.frameid == null ? -1 : this.frameid);
		pc.writeString(this.pos);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}
}
