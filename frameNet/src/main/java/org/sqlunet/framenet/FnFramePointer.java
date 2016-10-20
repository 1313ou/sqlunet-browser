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
	public Long frameId;

	private String pos;

	/**
	 * Constructor
	 */
	public FnFramePointer()
	{
		this.frameId = null;
		this.pos = null;
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	@SuppressWarnings("boxing")
	private FnFramePointer(final Parcel parcel)
	{
		this();
		long parcelFrameId = parcel.readLong();
		if (parcelFrameId != -1)
		{
			this.frameId = parcelFrameId;
		}
		this.pos = parcel.readString();
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
		if (this.frameId != 0)
		{
			return this.frameId;
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
		pc.writeLong(this.frameId == null ? -1 : this.frameId);
		pc.writeString(this.pos);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}
}
