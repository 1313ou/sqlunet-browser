package org.sqlunet.wordnet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.HasPos;
import org.sqlunet.HasSynsetId;

/**
 * Parcelable synset
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SynsetPointer implements Parcelable, HasSynsetId, HasPos
{
	/**
	 * synsetId : compulsory
	 */
	private long synsetId;

	/**
	 * pos : optional/nullable (may be retrieved if need be)
	 */
	private String pos;

	/**
	 * Constructor
	 */
	public SynsetPointer()
	{
		this.synsetId = -1;
		this.pos = null;
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	SynsetPointer(final Parcel pc)
	{
		this.synsetId = pc.readLong();
		this.pos = pc.readString();
	}

	public void setSynset(final Long synsetId, final String pos)
	{
		this.synsetId = synsetId == null ? -1 : synsetId;
		this.pos = pos;
	}

	@SuppressWarnings("boxing")
	@Override
	public Character getPos()
	{
		if (this.pos != null && !this.pos.isEmpty())
		{
			return this.pos.charAt(0);
		}
		return null;
	}

	@SuppressWarnings("boxing")
	@Override
	public Long getSynsetId()
	{
		if (this.synsetId != -1)
		{
			return this.synsetId;
		}
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

	@Override
	public void writeToParcel(final Parcel pc, final int flags)
	{
		pc.writeLong(this.synsetId);
		pc.writeString(this.pos);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return "synsetId=" + //$NON-NLS-1$
				this.synsetId +
				' ' +
				"pos=" + //$NON-NLS-1$
				this.pos;
	}
}
