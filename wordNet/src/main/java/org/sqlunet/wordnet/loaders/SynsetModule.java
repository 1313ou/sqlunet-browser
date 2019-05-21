/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
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
			final TreeNode synsetNode = TreeFactory.makeTextNode("Synset", false).addTo(parent);
			final TreeNode membersNode = TreeFactory.makeIconTextNode("Members", R.drawable.members, false).addTo(parent);

			// synset
			synset(this.synsetId, synsetNode, false);

			// members
			members(this.synsetId, membersNode);

			// special
			if (this.pos != null)
			{
				switch (this.pos)
				{
					case 'v':
						this.vFrames(this.synsetId, parent);
						this.vFrameSentences(this.synsetId, parent);
						break;

					case 'a':
						this.adjPosition(this.synsetId, parent);
						break;
				}
			}

			// links and samples
			@SuppressWarnings("unused") final TreeNode linksNode = this.expand ?
					TreeFactory.makeHotQueryNode("Links", R.drawable.ic_links, false, new LinksQuery(this.synsetId, 0)).addTo(parent) :
					TreeFactory.makeQueryNode("Links", R.drawable.ic_links, false, new LinksQuery(this.synsetId, 0)).addTo(parent);
			@SuppressWarnings("unused") final TreeNode samplesNode = this.expand ?
					TreeFactory.makeQueryNode("Samples", R.drawable.sample, false, new SamplesQuery(this.synsetId)).addTo(parent) :
					TreeFactory.makeQueryNode("Samples", R.drawable.sample, false, new SamplesQuery(this.synsetId)).addTo(parent);
		}
		else
		{
			TreeFactory.setNoResult(parent);
		}
	}
}
