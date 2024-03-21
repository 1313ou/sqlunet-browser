/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet.loaders;

import android.os.Parcelable;

import org.sqlunet.HasWordId;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.treeview.control.Link;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.wordnet.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Module for WordNet sense
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

public class SenseModule extends SynsetModule
{
	/**
	 * Word id
	 */
	@Nullable
	private Long wordId;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public SenseModule(@NonNull final TreeFragment fragment)
	{
		super(fragment);
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		super.unmarshal(pointer);

		this.wordId = null;
		if (pointer instanceof HasWordId)
		{
			final HasWordId wordPointer = (HasWordId) pointer;
			this.wordId = wordPointer.getWordId();
		}
	}

	@Override
	public void process(@NonNull final TreeNode parent)
	{
		if (this.wordId != null && this.wordId != 0 && this.synsetId != null && this.synsetId != 0)
		{
			// anchor nodes
			final TreeNode synsetNode = TreeFactory.makeTextNode(this.senseLabel, false).addTo(parent);
			final TreeNode membersNode = TreeFactory.makeIconTextNode(this.membersLabel, R.drawable.members, false).addTo(parent);

			// synset
			synset(this.synsetId, synsetNode, false);

			// member set
			members(this.synsetId, membersNode);

			// morph
			final TreeNode morphsNode = TreeFactory.makeTreeNode(this.morphsLabel, R.drawable.morph, false).addTo(parent);
			morphs(this.wordId, morphsNode);

			// relations and samples
			if (this.expand)
			{
				Link link = new RelationLink(this.synsetId, this.maxRecursion, this.fragment);
				TreeFactory.makeLinkHotQueryNode(this.relationsLabel, R.drawable.ic_relations, false, new RelationsQuery(this.synsetId, this.wordId), link, R.drawable.ic_link_relation).addTo(parent);
			}
			else
			{
				TreeFactory.makeQueryNode(this.relationsLabel, R.drawable.ic_relations, false, new RelationsQuery(this.synsetId, this.wordId)).addTo(parent);
			}

			if (this.expand)
			{
				TreeFactory.makeHotQueryNode(this.samplesLabel, R.drawable.sample, false, new SamplesQuery(this.synsetId)).addTo(parent);
			}
			else
			{
				TreeFactory.makeQueryNode(this.samplesLabel, R.drawable.sample, false, new SamplesQuery(this.synsetId)).addTo(parent);
			}

			// special
			if (this.pos != null)
			{
				switch (this.pos)
				{
					case 'v':
						final TreeNode vframesNode = TreeFactory.makeTreeNode(this.verbFramesLabel, R.drawable.verbframe, false).addTo(parent);
						final TreeNode vtemplatesNode = TreeFactory.makeTreeNode(this.verbTemplatesLabel, R.drawable.verbtemplate, false).addTo(parent);
						vFrames(this.synsetId, this.wordId, vframesNode);
						vTemplates(this.synsetId, this.wordId, vtemplatesNode);
						break;

					case 'a':
						final TreeNode adjpositionsNode = TreeFactory.makeTreeNode(this.adjPositionsLabel, R.drawable.adjposition, false).addTo(parent);
						adjPosition(this.synsetId, this.wordId, adjpositionsNode);
						break;
				}
			}
			else
			{
				final TreeNode vframesNode = TreeFactory.makeTreeNode(this.verbFramesLabel, R.drawable.verbframe, false).addTo(parent);
				final TreeNode vtemplatesNode = TreeFactory.makeTreeNode(this.verbTemplatesLabel, R.drawable.verbtemplate, false).addTo(parent);
				final TreeNode adjpositionsNode = TreeFactory.makeTreeNode(this.adjPositionsLabel, R.drawable.adjposition, false).addTo(parent);
				vFrames(this.synsetId, this.wordId, vframesNode);
				vTemplates(this.synsetId, this.wordId, vtemplatesNode);
				adjPosition(this.synsetId, this.wordId, adjpositionsNode);
			}
		}
		else
		{
			TreeFactory.setNoResult(parent);
		}
	}
}
