/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet.loaders;

import android.os.Parcelable;

import org.sqlunet.HasPos;
import org.sqlunet.HasSynsetId;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.wordnet.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Module for WordNet relation
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

public class RelationModule extends BaseModule
{
	/**
	 * Synset id
	 */
	@Nullable
	Long synsetId;

	/**
	 * Pos
	 */
	@Nullable
	Character pos;

	/**
	 * Expand flag
	 */
	boolean expand;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public RelationModule(@NonNull final TreeFragment fragment)
	{
		super(fragment);
		this.expand = true;
	}

	/**
	 * Set expandContainer
	 *
	 * @param expand expandContainer flag
	 */
	public void setExpand(final boolean expand)
	{
		this.expand = expand;
	}

	@SuppressWarnings("WeakerAccess")
	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.synsetId = null;
		this.pos = null;
		if (pointer instanceof HasSynsetId)
		{
			final HasSynsetId synsetPointer = (HasSynsetId) pointer;
			long value = synsetPointer.getSynsetId();
			if (value != 0)
			{
				this.synsetId = value;
			}
		}
		if (pointer instanceof HasPos)
		{
			final HasPos posPointer = (HasPos) pointer;
			this.pos = posPointer.getPos();
		}
	}

	private static final int HYPERNYM = 1;
	private static final int HYPONYM = 2;

	@Override
	public void process(@NonNull final TreeNode parent)
	{
		if (this.synsetId != null && this.synsetId != 0)
		{
			// anchor nodes
			final TreeNode synsetNode = TreeFactory.makeTextNode(this.senseLabel, false).addTo(parent);
			final TreeNode membersNode = TreeFactory.makeIconTextNode(this.membersLabel, R.drawable.members, false).addTo(parent);

			// synset
			synset(this.synsetId, synsetNode, false);

			// members
			// members(this.synsetId, membersNode);
			memberSet(this.synsetId, membersNode, true, false);

			// up relations
			if (this.expand)
			{
				TreeFactory.makeHotQueryNode(this.upLabel, R.drawable.up, false, new SubRelationsQuery(this.synsetId, HYPERNYM, this.maxRecursion, true)).addTo(parent);
			}
			else
			{
				TreeFactory.makeQueryNode(this.upLabel, R.drawable.up, false, new SubRelationsQuery(this.synsetId, HYPERNYM, this.maxRecursion, false)).addTo(parent);
			}
			// down relations
			if (this.expand)
			{
				TreeFactory.makeHotQueryNode(this.downLabel, R.drawable.down, false, new SubRelationsQuery(this.synsetId, HYPONYM, this.maxRecursion, false)).addTo(parent);
			}
			else
			{
				TreeFactory.makeQueryNode(this.downLabel, R.drawable.down, false, new SubRelationsQuery(this.synsetId, HYPONYM, this.maxRecursion, false)).addTo(parent);
			}
		}
		else
		{
			TreeFactory.setNoResult(parent);
		}
	}
}
