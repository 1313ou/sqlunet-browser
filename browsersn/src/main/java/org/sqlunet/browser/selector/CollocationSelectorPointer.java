/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.selector;

import android.os.Parcel;

import org.sqlunet.Has2Pos;
import org.sqlunet.Has2SynsetId;
import org.sqlunet.Has2WordId;
import org.sqlunet.HasTarget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * SelectorPointer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class CollocationSelectorPointer extends PosSelectorPointer implements Has2SynsetId, Has2WordId, Has2Pos, HasTarget
{
	/**
	 * Synset 2 id
	 */
	private final long synset2Id;

	/**
	 * Word 2 id
	 */
	private final long word2Id;

	/**
	 * POS 2
	 */
	@Nullable
	private final Character pos2;

	/**
	 * Target
	 */
	private final int target;

	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	static public final Creator<CollocationSelectorPointer> CREATOR = new Creator<CollocationSelectorPointer>()
	{
		@NonNull
		@Override
		public CollocationSelectorPointer createFromParcel(@NonNull final Parcel parcel)
		{
			return new CollocationSelectorPointer(parcel);
		}

		@NonNull
		@Override
		public CollocationSelectorPointer[] newArray(final int size)
		{
			return new CollocationSelectorPointer[size];
		}
	};

	/**
	 * Constructor from parcel
	 *
	 * @param parcel parcel
	 */
	private CollocationSelectorPointer(@NonNull Parcel parcel)
	{
		super(parcel);
		this.synset2Id = parcel.readLong();
		this.word2Id = parcel.readLong();
		String posStr = parcel.readString();
		this.pos2 = posStr == null ? null : posStr.charAt(0);
		this.target = parcel.readInt();
	}

	/**
	 * Constructor
	 *
	 * @param synset1Id synset 1 id
	 * @param word1Id   word 1 id
	 * @param pos1      pos 1
	 * @param synset2Id synset 2 id
	 * @param word2Id   word 2 id
	 * @param pos2      pos 2
	 * @param target    target (1, 2 or 0 unspecified)
	 */
	public CollocationSelectorPointer(final long synset1Id, final long word1Id, final Character pos1, final long synset2Id, final long word2Id, @Nullable final Character pos2, int target)
	{
		super(synset1Id, word1Id, pos1);
		this.synset2Id = synset2Id;
		this.word2Id = word2Id;
		this.pos2 = pos2;
		this.target = target;
	}

	@Override
	public long getSynset2Id()
	{
		return this.synset2Id;
	}

	@Override
	public long getWord2Id()
	{
		return this.word2Id;
	}

	@Nullable
	@Override
	public Character getPos2()
	{
		return this.pos2;
	}

	@Override
	public int getTarget()
	{
		return this.target;
	}

	@Override
	public void writeToParcel(@NonNull final Parcel parcel, final int flags)
	{
		super.writeToParcel(parcel, flags);
		parcel.writeLong(this.synset2Id);
		parcel.writeLong(this.word2Id);
		parcel.writeString(this.pos2 == null ? null : this.pos2.toString());
		parcel.writeInt(this.target);
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
		return super.toString() + ' ' + "word2=" + this.word2Id + ' ' + "synset2=" + this.synset2Id + ' ' + "pos2=" + this.pos2 + ' ' + "target=" + this.target;
	}
}
