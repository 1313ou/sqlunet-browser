/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.syntagnet.loaders;

import android.os.Parcelable;

import org.sqlunet.Has2SynsetId;
import org.sqlunet.Has2WordId;
import org.sqlunet.HasSynsetId;
import org.sqlunet.HasTarget;
import org.sqlunet.HasWordId;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Module for SyntagNet collocation from id
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class CollocationsModule extends BaseModule
{
	/**
	 * Collocation id
	 */
	@Nullable
	private Long synset1Id;

	/**
	 * Collocation id
	 */
	@Nullable
	private Long synset2Id;

	/**
	 * Collocation id
	 */
	@Nullable
	private Long word1Id;

	/**
	 * Collocation id
	 */
	@Nullable
	private Long word2Id;

	/**
	 * Target
	 */
	private int target;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public CollocationsModule(@NonNull final TreeFragment fragment)
	{
		super(fragment);
		this.synset1Id = null;
		this.synset2Id = null;
		this.word1Id = null;
		this.word2Id = null;
		this.target = 0;
	}

	@Override
	protected boolean isTargetSecond(final long word1Id, final long word2Id)
	{
		if (1 == this.target)
		{
			return false;
		}
		else if (2 == this.target)
		{
			return true;
		}
		else
		{
			return this.word2Id != null && this.word2Id.equals(this.word1Id) && this.word2Id.equals(word2Id);
		}
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		if (pointer instanceof HasWordId)
		{
			final HasWordId pointer1 = (HasWordId) pointer;
			long id = pointer1.getWordId();
			this.word1Id = id == -1 ? null : pointer1.getWordId();
		}
		if (pointer instanceof Has2WordId)
		{
			final Has2WordId pointer2 = (Has2WordId) pointer;
			long id = pointer2.getWord2Id();
			this.word2Id = id == -1 ? null : pointer2.getWord2Id();
		}
		else
		{
			this.word2Id = this.word1Id;
		}
		if (pointer instanceof HasSynsetId)
		{
			final HasSynsetId pointer1 = (HasSynsetId) pointer;
			long id = pointer1.getSynsetId();
			this.synset1Id = id == -1 ? null : pointer1.getSynsetId();
		}
		if (pointer instanceof Has2SynsetId)
		{
			final Has2SynsetId pointer2 = (Has2SynsetId) pointer;
			long id = pointer2.getSynset2Id();
			this.synset2Id = id == -1 ? null : pointer2.getSynset2Id();
		}
		else
		{
			this.synset2Id = this.synset1Id;
		}
		if (pointer instanceof HasTarget)
		{
			final HasTarget pointer2 = (HasTarget) pointer;
			this.target = pointer2.getTarget();
		}
		else
		{
			this.target = 0;
		}
	}

	@Override
	public void process(@NonNull final TreeNode parent)
	{
		if (this.word1Id != null || this.word2Id != null || this.synset1Id != null || this.synset2Id != null)
		{
			collocations(this.word1Id, this.word2Id, this.synset1Id, this.synset2Id, parent);
		}
		else
		{
			TreeFactory.setNoResult(parent);
		}
	}
}
