package org.sqlunet.browser.xselector;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.HasXId;
import org.sqlunet.browser.selector.Pointer;

public class XPointer extends Pointer implements HasXId
{
	private static final int WORDNETSOURCE = 0x00001;

	private static final int VERBNETSOURCE = 0x00002;

	private static final int PROPBANKSOURCE = 0x00004;

	private static final int FRAMENETSOURCE = 0x00008;

	private static final int PMVERBNETSOURCE = 0x00020;

	private static final int PMPROPBANKSOURCE = 0x00040;

	private static final int PMFRAMENETSOURCE = 0x00080;

	/**
	 * xwordid : optional/nullable
	 */
	private Long xwordid;

	/**
	 * xid : optional/nullable
	 */
	private Long xid;

	/**
	 * xid : optional/nullable
	 */
	private Long xclassid;

	/**
	 * xid : optional/nullable
	 */
	private Long xinstanceid;

	/**
	 * xsources : optional/nullable
	 */
	private String xsources;

	/**
	 * xmask
	 */
	private long xmask;

	/**
	 * Constructor
	 */
	public XPointer()
	{
		super();
		this.xid = null;
		this.xwordid = null;
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	@SuppressWarnings("boxing")
	private XPointer(Parcel parcel)
	{
		super(parcel);

		long xid0 = parcel.readLong();
		if (xid0 != -1)
			this.xid = xid0;

		long xclassid0 = parcel.readLong();
		if (xclassid0 != -1)
			this.xclassid = xclassid0;

		long xinstanceid0 = parcel.readLong();
		if (xinstanceid0 != -1)
			this.xinstanceid = xinstanceid0;

		long xwordid0 = parcel.readLong();
		if (xwordid0 != -1)
			this.xwordid = xwordid0;

		this.xsources = parcel.readString();
		this.xmask = parcel.readLong();
	}

	public void setXid(Long xid)
	{
		this.xid = xid;
	}

	public Long getXclassid()
	{
		return this.xclassid;
	}

	public void setXclassid(Long xclassid)
	{
		this.xclassid = xclassid;
	}

	public Long getXinstanceid()
	{
		return this.xinstanceid;
	}

	public void setXinstanceid(Long xinstanceid)
	{
		this.xinstanceid = xinstanceid;
	}

	@SuppressWarnings("unused")
	public Long getXwordid()
	{
		return this.xwordid;
	}

	@SuppressWarnings("unused")
	public void setXwordid(Long xwordid)
	{
		this.xwordid = xwordid;
	}

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	public static final Parcelable.Creator<XPointer> CREATOR = new Parcelable.Creator<XPointer>()
	{
		@Override
		public XPointer createFromParcel(final Parcel parcel)
		{
			return new XPointer(parcel);
		}

		@Override
		public XPointer[] newArray(final int size)
		{
			return new XPointer[size];
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sqlunet.wordnet.Synset#writeToParcel(android.os.Parcel, int)
	 */
	@SuppressWarnings("boxing")
	@Override
	public void writeToParcel(final Parcel parcel, final int flags)
	{
		super.writeToParcel(parcel, flags);

		parcel.writeLong(this.xid == null ? -1 : this.xid);
		parcel.writeLong(this.xclassid == null ? -1 : this.xclassid);
		parcel.writeLong(this.xinstanceid == null ? -1 : this.xinstanceid);
		parcel.writeLong(this.xwordid == null ? -1 : this.xwordid);
		parcel.writeString(this.xsources);
		parcel.writeLong(this.xmask);
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
	public String getXsources()
	{
		return this.xsources;
	}

	public void setXsources(String xsources)
	{
		this.xsources = xsources;
		this.xmask = 0L;
		if (xsources.contains("wn")) //$NON-NLS-1$
			this.xmask |= WORDNETSOURCE;

		if (xsources.contains("vn")) //$NON-NLS-1$
			this.xmask |= VERBNETSOURCE;
		if (xsources.contains("pb")) //$NON-NLS-1$
			this.xmask |= PROPBANKSOURCE;
		if (xsources.contains("fn")) //$NON-NLS-1$
			this.xmask |= FRAMENETSOURCE;

		if (xsources.contains("pmvn")) //$NON-NLS-1$
			this.xmask |= PMVERBNETSOURCE;
		if (xsources.contains("pmpb")) //$NON-NLS-1$
			this.xmask |= PMPROPBANKSOURCE;
		if (xsources.contains("pmfn")) //$NON-NLS-1$
			this.xmask |= PMFRAMENETSOURCE;
	}

	@SuppressWarnings("unused")
	public long getXmask()
	{
		return this.xmask;
	}

	@Override
	public String toString()
	{
		return super.toString() +
				' ' +
				"xid=" + //$NON-NLS-1$
				this.xid +
				' ' +
				"xclassid=" + //$NON-NLS-1$
				this.xclassid +
				' ' +
				"xinstanceid=" + //$NON-NLS-1$
				this.xinstanceid +
				' ' +
				"xwordid=" + //$NON-NLS-1$
				this.xwordid +
				' ' +
				"xsources=" + //$NON-NLS-1$
				this.xsources +
				"xmask=" + //$NON-NLS-1$
				Long.toHexString(this.xmask);
	}
}
