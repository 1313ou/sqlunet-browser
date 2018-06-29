package org.sqlunet.browser.xselector;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.sqlunet.HasXId;
import org.sqlunet.browser.selector.SelectorPointer;

/**
 * X pointer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class XSelectorPointer extends SelectorPointer implements HasXId
{
	private static final int WORDNETSOURCE = 0x00001;

	private static final int VERBNETSOURCE = 0x00002;

	private static final int PROPBANKSOURCE = 0x00004;

	private static final int FRAMENETSOURCE = 0x00008;

	private static final int PMVERBNETSOURCE = 0x00020;

	private static final int PMPROPBANKSOURCE = 0x00040;

	private static final int PMFRAMENETSOURCE = 0x00080;

	/**
	 * xId : optional/nullable
	 */
	private long xId;

	/**
	 * xId : optional/nullable
	 */
	private long xClassId;

	/**
	 * xId : (fn=lex unit that is member of frame) optional/nullable
	 */
	private long xMemberId;

	/**
	 * xSources : optional/nullable
	 */
	private final String xSources;

	/**
	 * xMask
	 */
	private final long xMask;

	/**
	 * xGroup (0=wordnet, 1=verbnet, 2=propbank, 3=framenet, 4=bnc)
	 */
	private final int xGroup;

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	static public final Parcelable.Creator<XSelectorPointer> CREATOR = new Parcelable.Creator<XSelectorPointer>()
	{
		@Override
		public XSelectorPointer createFromParcel(@NonNull final Parcel parcel)
		{
			return new XSelectorPointer(parcel);
		}

		@Override
		public XSelectorPointer[] newArray(final int size)
		{
			return new XSelectorPointer[size];
		}
	};

	/**
	 * Constructor
	 *
	 * @param synsetId  synset id
	 * @param wordId    word id
	 * @param xId       x id
	 * @param xClassId  x class id
	 * @param xMemberId x member id
	 * @param xSources  x sources
	 * @param xMask     x mask
	 * @param xGroup    x group id
	 */
	XSelectorPointer(final long synsetId, final long wordId, final long xId, final long xClassId, final long xMemberId, final String xSources, final long xMask, final int xGroup)
	{
		super(synsetId, wordId);
		this.xId = xId;
		this.xClassId = xClassId;
		this.xMemberId = xMemberId;
		this.xSources = xSources;
		this.xMask = xMask;
		this.xGroup = xGroup;
	}

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	@SuppressWarnings("boxing")
	private XSelectorPointer(@NonNull final Parcel parcel)
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

		long parcelXMemberId = parcel.readLong();
		if (parcelXMemberId != -1)
		{
			this.xMemberId = parcelXMemberId;
		}

		this.xSources = parcel.readString();
		this.xMask = parcel.readLong();
		this.xGroup = parcel.readInt();
	}

	/**
	 * Get x id
	 *
	 * @return target object id
	 */
	@Nullable
	@Override
	public Long getXId()
	{
		return this.xId == 0 ? null : this.xId;
	}

	/**
	 * Get class id
	 *
	 * @return role class id
	 */
	@Nullable
	@Override
	public Long getXClassId()
	{
		return this.xClassId == 0 ? null : this.xClassId;
	}

	/**
	 * Get member id (fn=lex unit that is member of frame) id
	 *
	 * @return role id
	 */
	@Nullable
	@Override
	public Long getXMemberId()
	{
		return this.xMemberId == 0 ? null : this.xMemberId;
	}

	/**
	 * Get info sources
	 *
	 * @return info sources
	 */
	@Override
	public String getXSources()
	{
		return this.xSources;
	}

	/**
	 * Get group
	 *
	 * @return xgroup
	 */
	public int getXGroup()
	{
		return this.xGroup;
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
	public void writeToParcel(@NonNull final Parcel parcel, final int flags)
	{
		super.writeToParcel(parcel, flags);

		parcel.writeLong(this.xId);
		parcel.writeLong(this.xClassId);
		parcel.writeLong(this.xMemberId);
		parcel.writeString(this.xSources);
		parcel.writeLong(this.xMask);
		parcel.writeInt(this.xGroup);
	}

	@SuppressWarnings("SameReturnValue")
	@Override
	public int describeContents()
	{
		return 0;
	}

	@NonNull
	@Override
	public String toString()
	{
		return super.toString() +
				' ' +
				"xid=" + //
				this.xId +
				' ' +
				"xclassid=" + //
				this.xClassId +
				' ' +
				"xmemberid=" + //
				this.xMemberId +
				' ' +
				"xsources=" + //
				this.xSources +
				' ' +
				"xmask=" + //
				Long.toHexString(this.xMask) +
				' ' +
				"xgroup=" + //
				Integer.toString(this.xGroup);
	}

	/**
	 * Set x sources
	 *
	 * @param xSources x sources
	 * @return mask
	 */
	static public long getMask(@NonNull final String xSources)
	{
		long mask = 0;
		if (xSources.contains("wn")) //
		{
			mask |= WORDNETSOURCE;
		}

		if (xSources.contains("vn")) //
		{
			mask |= VERBNETSOURCE;
		}
		if (xSources.contains("pb")) //
		{
			mask |= PROPBANKSOURCE;
		}
		if (xSources.contains("fn")) //
		{
			mask |= FRAMENETSOURCE;
		}

		if (xSources.contains("pmvn")) //
		{
			mask |= PMVERBNETSOURCE;
		}
		if (xSources.contains("pmpb")) //
		{
			mask |= PMPROPBANKSOURCE;
		}
		if (xSources.contains("pmfn")) //
		{
			mask |= PMFRAMENETSOURCE;
		}
		return mask;
	}


	/**
	 * Determine whether WordNet is only source
	 *
	 * @return whether to expand
	 */
	public boolean wordNetOnly()
	{
		boolean result = true;
		long mask = this.getXMask();
		if ((mask & ~XSelectorPointer.WORDNETSOURCE) != 0)
		{
			result = false;
		}
		return result;
	}
}
