package org.sqlunet.wordnet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.HasPos;
import org.sqlunet.HasSynsetId;
import org.sqlunet.Pointer2;

/**
 * Parcelable synset
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SynsetPointer extends Pointer2 implements HasSynsetId, HasPos
{
	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<SynsetPointer> CREATOR = new Parcelable.Creator<SynsetPointer>()
	{
		@Override
		public SynsetPointer createFromParcel(final Parcel parcel)
		{
			return new SynsetPointer(parcel);
		}

		@Override
		public SynsetPointer[] newArray(final int size)
		{
			return new SynsetPointer[size];
		}
	};

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	SynsetPointer(final Parcel parcel)
	{
		super(parcel);
	}

	/**
	 * Constructor
	 *
	 * @param synsetId synset id
	 * @param pos      pos
	 */
	public SynsetPointer(final Long synsetId, final String pos)
	{
		super(synsetId == null ? 0 : synsetId, pos);
	}

	@SuppressWarnings("boxing")
	@Override
	public Character getPos()
	{
		if (this.id2 != null && !this.id2.isEmpty())
		{
			return this.id2.charAt(0);
		}
		return null;
	}

	@SuppressWarnings("boxing")
	@Override
	public Long getSynsetId()
	{
		if (this.id != 0)
		{
			return this.id;
		}
		return null;
	}

	@Override
	public String toString()
	{
		return "synsetid=" + //
				this.id +
				' ' +
				"pos=" + //
				this.id2;
	}
}
