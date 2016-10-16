package org.sqlunet.wordnet;

import org.sqlunet.HasWordId;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable sense
 *
 * @author Bernard Bou
 */
public class SensePointer extends SynsetPointer implements Parcelable, HasWordId
{
	/**
	 * wordid : compulsory
	 */
	private Long wordid;

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
		this.wordid = null;
		this.lemma = null;
		this.cased = null;
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	protected SensePointer(final Parcel parcel)
	{
		super(parcel);
		this.wordid = (Long) parcel.readValue(getClass().getClassLoader());
		this.lemma = parcel.readString();
		this.cased = parcel.readString();
	}

	@SuppressWarnings("boxing")
	public void setWord(final Long wordid, final String lemma, final String cased)
	{
		this.wordid = wordid == null ? -1L : wordid;
		this.lemma = lemma;
		this.cased = cased;
	}

	@SuppressWarnings("boxing")
	@Override
	public Long getWordId()
	{
		if (this.wordid != -1)
			return this.wordid;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sqlunet.wordnet.Synset#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(final Parcel parcel, final int flags)
	{
		super.writeToParcel(parcel, flags);

		parcel.writeValue(this.wordid);
		parcel.writeString(this.lemma);
		parcel.writeString(this.cased);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sqlunet.wordnet.Synset#describeContents()
	 */
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
				"wordid=" + //$NON-NLS-1$
				this.wordid +
				' ' +
				"lemma=" + //$NON-NLS-1$
				this.lemma +
				' ' +
				"cased=" + //$NON-NLS-1$
				this.cased;
	}
}
