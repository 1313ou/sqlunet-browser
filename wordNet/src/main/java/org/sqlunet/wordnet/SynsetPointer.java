package org.sqlunet.wordnet;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.sqlunet.HasSynsetId;
import org.sqlunet.Pointer;

/**
 * Parcelable synset
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SynsetPointer extends Pointer implements HasSynsetId
{
	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	static public final Parcelable.Creator<SynsetPointer> CREATOR = new Parcelable.Creator<SynsetPointer>()
	{
		@Override
		public SynsetPointer createFromParcel(@NonNull final Parcel parcel)
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
	SynsetPointer(@NonNull final Parcel parcel)
	{
		super(parcel);
	}

	/**
	 * Constructor
	 *
	 * @param synsetId synset id
	 */
	public SynsetPointer(final long synsetId)
	{
		super(synsetId);
	}

	@Override
	public long getSynsetId()
	{
		return this.id;
	}

	@SuppressWarnings("WeakerAccess")
	@Override
	public String toString()
	{
		return "synsetid=" + this.id;
	}
}
