package org.sqlunet.wordnet;

import org.sqlunet.HasPos;
import org.sqlunet.HasSynsetId;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable synset
 *
 * @author Bernard Bou
 */
public class SynsetPointer implements Parcelable, HasSynsetId, HasPos
{
	/**
	 * synsetid : compulsory
	 */
	private long synsetid;

	/**
	 * pos : optional/nullable (may be retrieved if need be)
	 */
	private String pos;

	/**
	 * Constructor
	 */
	public SynsetPointer()
	{
		this.synsetid = -1;
		this.pos = null;
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	SynsetPointer(final Parcel pc)
	{
		this.synsetid = pc.readLong();
		this.pos = pc.readString();
	}

	@SuppressWarnings("boxing")
	public void setSynset(final Long synsetid, final String pos)
	{
		this.synsetid = synsetid == null ? -1 : synsetid;
		this.pos = pos;
	}

	@SuppressWarnings("boxing")
	@Override
	public Character getPos()
	{
		if (this.pos != null && !this.pos.isEmpty())
			return this.pos.charAt(0);
		return null;
	}

	@SuppressWarnings("boxing")
	@Override
	public Long getSynsetId()
	{
		if (this.synsetid != -1)
			return this.synsetid;
		return null;
	}

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<SynsetPointer> CREATOR = new Parcelable.Creator<SynsetPointer>()
	{
		@Override
		public SynsetPointer createFromParcel(final Parcel pc)
		{
			return new SynsetPointer(pc);
		}

		@Override
		public SynsetPointer[] newArray(final int size)
		{
			return new SynsetPointer[size];
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
		pc.writeLong(this.synsetid);
		pc.writeString(this.pos);
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

	@Override
	public String toString()
	{
		return "synsetid=" + //$NON-NLS-1$
				this.synsetid +
				' ' +
				"pos=" + //$NON-NLS-1$
				this.pos;
	}
}
