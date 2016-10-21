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
	private Long wordId;

	/**
	 * Lemma : optional/nullable (may be retrieved if need be)
	 */
	private String lemma;

	/**
	 * Cased : optional/nullable (may be retrieved if need be)
	 */
	private String cased;

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
	 * Constructor
	 */
	public SensePointer()
	{
		super();
		this.wordId = null;
		this.lemma = null;
		this.cased = null;
	}

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
	 * Set word
	 *
	 * @param wordId word id
	 * @param lemma  lemma
	 * @param cased  cased word
	 */
	@SuppressWarnings("boxing")
	public void setWord(final Long wordId, final String lemma, final String cased)
	{
		this.wordId = wordId == null ? -1L : wordId;
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
				"wordId=" + //$NON-NLS-1$
				this.wordId +
				' ' +
				"lemma=" + //$NON-NLS-1$
				this.lemma +
				' ' +
				"cased=" + //$NON-NLS-1$
				this.cased;
	}
}
