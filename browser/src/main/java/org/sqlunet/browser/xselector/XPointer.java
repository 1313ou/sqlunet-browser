package org.sqlunet.browser.xselector;

import android.os.Parcel;
import android.os.Parcelable;

import org.sqlunet.HasXId;
import org.sqlunet.browser.selector.Pointer;

/**
 * X pointer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
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
	 * xWordId : optional/nullable
	 */
	private Long xWordId;

	/**
	 * xId : optional/nullable
	 */
	private Long xId;

	/**
	 * xId : optional/nullable
	 */
	private Long xClassId;

	/**
	 * xId : optional/nullable
	 */
	private Long xInstanceId;

	/**
	 * xSources : optional/nullable
	 */
	private String xSources;

	/**
	 * xMask
	 */
	private long xMask;

	/**
	 * Constructor
	 */
	public XPointer()
	{
		super();
		this.xId = null;
		this.xWordId = null;
	}

	/**
	 * Constructor from Parcel, reads back fields IN THE ORDER they were written
	 */
	@SuppressWarnings("boxing")
	private XPointer(final Parcel parcel)
	{
		super(parcel);

		long parcelXId = parcel.readLong();
		if (parcelXId != -1)
		{
			this.xId = parcelXId;
		}

		long parcelXClassId = parcel.readLong();
		if (parcelXClassId != -1)
		{
			this.xClassId = parcelXClassId;
		}

		long parcelXInstanceId = parcel.readLong();
		if (parcelXInstanceId != -1)
		{
			this.xInstanceId = parcelXInstanceId;
		}

		long parcelXWordId = parcel.readLong();
		if (parcelXWordId != -1)
		{
			this.xWordId = parcelXWordId;
		}

		this.xSources = parcel.readString();
		this.xMask = parcel.readLong();
	}

	/**
	 * Set x id
	 *
	 * @param xId xId
	 */
	public void setXId(final Long xId)
	{
		this.xId = xId;
	}

	@Override
	public Long getXClassId()
	{
		return this.xClassId;
	}

	/**
	 * Set x class id
	 *
	 * @param xClassId x class id
	 */
	public void setXClassId(final Long xClassId)
	{
		this.xClassId = xClassId;
	}

	@Override
	public Long getXInstanceId()
	{
		return this.xInstanceId;
	}

	/**
	 * Set x instance id
	 *
	 * @param xInstanceId x instance id
	 */
	public void setXInstanceId(final Long xInstanceId)
	{
		this.xInstanceId = xInstanceId;
	}

	@SuppressWarnings("unused")
	public Long getXWordId()
	{
		return this.xWordId;
	}

	@SuppressWarnings("unused")
	public void setXWordId(final Long xWordId)
	{
		this.xWordId = xWordId;
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

	@Override
	public void writeToParcel(final Parcel parcel, final int flags)
	{
		super.writeToParcel(parcel, flags);

		parcel.writeLong(this.xId == null ? -1 : this.xId);
		parcel.writeLong(this.xClassId == null ? -1 : this.xClassId);
		parcel.writeLong(this.xInstanceId == null ? -1 : this.xInstanceId);
		parcel.writeLong(this.xWordId == null ? -1 : this.xWordId);
		parcel.writeString(this.xSources);
		parcel.writeLong(this.xMask);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public String getXSources()
	{
		return this.xSources;
	}

	/**
	 * Set x sources
	 *
	 * @param xSources x sources
	 */
	public void setXSources(final String xSources)
	{
		this.xSources = xSources;
		this.xMask = 0L;
		if (xSources.contains("wn")) //$NON-NLS-1$
		{
			this.xMask |= WORDNETSOURCE;
		}

		if (xSources.contains("vn")) //$NON-NLS-1$
		{
			this.xMask |= VERBNETSOURCE;
		}
		if (xSources.contains("pb")) //$NON-NLS-1$
		{
			this.xMask |= PROPBANKSOURCE;
		}
		if (xSources.contains("fn")) //$NON-NLS-1$
		{
			this.xMask |= FRAMENETSOURCE;
		}

		if (xSources.contains("pmvn")) //$NON-NLS-1$
		{
			this.xMask |= PMVERBNETSOURCE;
		}
		if (xSources.contains("pmpb")) //$NON-NLS-1$
		{
			this.xMask |= PMPROPBANKSOURCE;
		}
		if (xSources.contains("pmfn")) //$NON-NLS-1$
		{
			this.xMask |= PMFRAMENETSOURCE;
		}
	}

	/**
	 * Get x mask
	 *
	 * @return x mask
	 */
	@SuppressWarnings("unused")
	public long getXMask()
	{
		return this.xMask;
	}

	@Override
	public String toString()
	{
		return super.toString() +
				' ' +
				"xId=" + //$NON-NLS-1$
				this.xId +
				' ' +
				"xClassId=" + //$NON-NLS-1$
				this.xClassId +
				' ' +
				"xInstanceId=" + //$NON-NLS-1$
				this.xInstanceId +
				' ' +
				"xWordId=" + //$NON-NLS-1$
				this.xWordId +
				' ' +
				"xSources=" + //$NON-NLS-1$
				this.xSources +
				"xMask=" + //$NON-NLS-1$
				Long.toHexString(this.xMask);
	}
}
