/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet.loaders;

import android.os.Parcelable;

import org.sqlunet.HasPos;
import org.sqlunet.HasSynsetId;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.treeview.control.Link;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.wordnet.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Module for WordNet synset
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

public class SynsetModule extends BaseModule
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
	public SynsetModule(@NonNull final TreeFragment fragment)
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

	@Override
	public void process(@NonNull final TreeNode parent)
	{
		if (this.synsetId != null && this.synsetId != 0)
		{
			// anchor nodes
			final TreeNode synsetNode = TreeFactory.makeTextNode(this.synsetLabel, false).addTo(parent);
			final TreeNode membersNode = TreeFactory.makeIconTextNode(this.membersLabel, R.drawable.members, false).addTo(parent);

			// synset
			synset(this.synsetId, synsetNode, false);

			// member set
			members(this.synsetId, membersNode);

			// special
			if (this.pos != null)
			{
				switch (this.pos)
				{
					case 'v':
						this.vFrames(this.synsetId, parent);
						this.vTemplates(this.synsetId, parent);
						break;

					case 'a':
						this.adjPosition(this.synsetId, parent);
						break;
				}
			}

			// relations and samples
			if (this.expand)
			{
				Link link = new RelationLink(this.synsetId, this.maxRecursion, this.fragment);
				TreeFactory.makeLinkHotQueryNode(this.relationsLabel, R.drawable.ic_relations, false, new RelationsQuery(this.synsetId, 0), link, R.drawable.ic_link_relation).addTo(parent);
			}
			else
			{
				TreeFactory.makeQueryNode(this.relationsLabel, R.drawable.ic_relations, false, new RelationsQuery(this.synsetId, 0)).addTo(parent);
			}

			if (this.expand)
			{
				TreeFactory.makeHotQueryNode(this.samplesLabel, R.drawable.sample, false, new SamplesQuery(this.synsetId)).addTo(parent);
			}
			else
			{
				TreeFactory.makeQueryNode(this.samplesLabel, R.drawable.sample, false, new SamplesQuery(this.synsetId)).addTo(parent);
			}
		}
		else
		{
			TreeFactory.setNoResult(parent);
		}
	}
}
