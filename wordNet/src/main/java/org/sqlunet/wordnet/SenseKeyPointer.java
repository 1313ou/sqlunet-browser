package org.sqlunet.wordnet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.HasSenseKey;

/**
 * Parcelable sensekey
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SenseKeyPointer implements Parcelable, HasSenseKey
{
	private String senseKey;

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Creator<SenseKeyPointer> CREATOR = new Creator<SenseKeyPointer>()
	{
		@Override
		public SenseKeyPointer createFromParcel(final Parcel parcel)
		{
			return new SenseKeyPointer(parcel);
		}

		@Override
		public SenseKeyPointer[] newArray(final int size)
		{
			return new SenseKeyPointer[size];
		}
	};

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	SenseKeyPointer(final Parcel parcel)
	{
		this.senseKey = parcel.readString();
	}

	/**
	 * Constructor
	 *
	 * @param senseKey sense key
	 */
	public SenseKeyPointer(final String senseKey)
	{
		this.senseKey = senseKey;
	}

	@SuppressWarnings("boxing")
	@Override
	public String getSenseKey()
	{
		return this.senseKey;
	}

	@Override
	public void writeToParcel(final Parcel parcel, final int flags)
	{
		parcel.writeString(this.senseKey);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return "sensekey=" + //
				this.senseKey;
	}
}
