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
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<FnFramePointer> CREATOR = new Parcelable.Creator<FnFramePointer>()
	{
		@Override
		public FnFramePointer createFromParcel(final Parcel parcel)
		{
			return new FnFramePointer(parcel);
		}

		@Override
		public FnFramePointer[] newArray(final int size)
		{
			return new FnFramePointer[size];
		}
	};

	/**
	 * Frame id
	 */
	public Long frameId;

	/**
	 * Pos
	 */
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

	/**
	 * Get pos
	 *
	 * @return pos
	 */
	@SuppressWarnings({"boxing", "unused"})
	public Character getPos()
	{
		if (this.pos != null && !this.pos.isEmpty())
		{
			return this.pos.charAt(0);
		}
		return null;
	}

	/**
	 * Get frame id
	 *
	 * @return frame id
	 */
	@SuppressWarnings({"unused"})
	public Long getFrameId()
	{
		if (this.frameId != 0)
		{
			return this.frameId;
		}
		return null;
	}

	@Override
	public void writeToParcel(final Parcel parcel, final int flags)
	{
		parcel.writeLong(this.frameId == null ? -1 : this.frameId);
		parcel.writeString(this.pos);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}
}
