/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.selector;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.HasPos;

import androidx.annotation.NonNull;

/**
 * SelectorPointer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PosSelectorPointer extends SelectorPointer implements HasPos
{
	/**
	 * POS
	 */
	@SuppressWarnings("WeakerAccess")
	protected final Character pos;

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	static public final Parcelable.Creator<PosSelectorPointer> CREATOR = new Parcelable.Creator<PosSelectorPointer>()
	{
		@NonNull
		@Override
		public PosSelectorPointer createFromParcel(@NonNull final Parcel parcel)
		{
			return new PosSelectorPointer(parcel);
		}

		@NonNull
		@Override
		public PosSelectorPointer[] newArray(final int size)
		{
			return new PosSelectorPointer[size];
		}
	};

	/**
	 * Constructor from parcel
	 *
	 * @param parcel parcel
	 */
	@SuppressWarnings("WeakerAccess")
	protected PosSelectorPointer(@NonNull Parcel parcel)
	{
		super(parcel);
		String posStr = parcel.readString();
		assert posStr != null;
		this.pos = posStr.charAt(0);
	}

	/**
	 * Constructor
	 *
	 * @param synsetId synset id
	 * @param wordId   word id
	 * @param pos      pos
	 */
	public PosSelectorPointer(final long synsetId, final long wordId, final Character pos)
	{
		super(synsetId, wordId);
		this.pos = pos;
	}

	@NonNull
	@Override
	public Character getPos()
	{
		return this.pos;
	}


	@Override
	public void writeToParcel(@NonNull final Parcel parcel, final int flags)
	{
		super.writeToParcel(parcel, flags);
		parcel.writeString(this.pos.toString());
	}

	@SuppressWarnings("SameReturnValue")
	@Override
	public int describeContents()
	{
		return 0;
	}

	@NonNull
	@Override
	public String toString()
	{
		return super.toString() + ' ' + "pos=" + this.pos;
	}

}
