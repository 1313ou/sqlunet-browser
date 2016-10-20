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
	 * wordId : compulsory
	 */
	private Long wordId;

	/**
	 * pos : optional/nullable (may be retrieved if need be)
	 */
	private String lemma;

	/**
	 * cased : optional/nullable (may be retrieved if need be)
	 */
	private String cased;

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
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	protected SensePointer(final Parcel parcel)
	{
		super(parcel);
		this.wordId = (Long) parcel.readValue(getClass().getClassLoader());
		this.lemma = parcel.readString();
		this.cased = parcel.readString();
	}

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

	@SuppressWarnings("unused")
	public String getLemma()
	{
		return this.lemma;
	}

	@SuppressWarnings("unused")
	public String getCased()
	{
		return this.cased;
	}

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<SensePointer> CREATOR = new Parcelable.Creator<SensePointer>()
	{
		@Override
		public SensePointer createFromParcel(final Parcel pc)
		{
			return new SensePointer(pc);
		}

		@Override
		public SensePointer[] newArray(final int size)
		{
			return new SensePointer[size];
		}
	};

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
