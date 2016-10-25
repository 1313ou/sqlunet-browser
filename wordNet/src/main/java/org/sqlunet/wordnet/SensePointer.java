package org.sqlunet.wordnet;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.HasWordId;

/**
 * Parcelable sense
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SensePointer extends SynsetPointer implements HasWordId
{
	/**
	 * Word id : compulsory
	 */
	private final Long wordId;

	/**
	 * Lemma : optional/nullable (may be retrieved if need be)
	 */
	private final String lemma;

	/**
	 * Cased : optional/nullable (may be retrieved if need be)
	 */
	private final String cased;

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<SensePointer> CREATOR = new Parcelable.Creator<SensePointer>()
	{
		@Override
		public SensePointer createFromParcel(final Parcel parcel)
		{
			return new SensePointer(parcel);
		}

		@Override
		public SensePointer[] newArray(final int size)
		{
			return new SensePointer[size];
		}
	};

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	protected SensePointer(final Parcel parcel)
	{
		super(parcel);
		this.wordId = (Long) parcel.readValue(getClass().getClassLoader());
		this.lemma = parcel.readString();
		this.cased = parcel.readString();
	}

	/**
	 * Constructor
	 *
	 * @param synsetId synset id
	 * @param pos      pos
	 * @param wordId   word id
	 * @param lemma    lemma
	 * @param cased    cased
	 */
	public SensePointer(final long synsetId, final String pos, final long wordId, final String lemma, final String cased)
	{
		super(synsetId, pos);
		this.wordId = wordId;
		this.lemma = lemma;
		this.cased = cased;
	}

	@Override
	public Long getWordId()
	{
		if (this.wordId != -1)
		{
			return this.wordId;
		}
		return null;
	}

	/**
	 * Get lemma
	 *
	 * @return lemma
	 */
	@SuppressWarnings("unused")
	public String getLemma()
	{
		return this.lemma;
	}

	/**
	 * Get cased word
	 *
	 * @return cased word
	 */
	@SuppressWarnings("unused")
	public String getCased()
	{
		return this.cased;
	}

	@Override
	public void writeToParcel(final Parcel parcel, final int flags)
	{
		super.writeToParcel(parcel, flags);

		parcel.writeValue(this.wordId);
		parcel.writeString(this.lemma);
		parcel.writeString(this.cased);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return super.toString() +
				' ' +
				"wordid=" + //
				this.wordId +
				' ' +
				"lemma=" + //
				this.lemma +
				' ' +
				"cased=" + //
				this.cased;
	}
}
