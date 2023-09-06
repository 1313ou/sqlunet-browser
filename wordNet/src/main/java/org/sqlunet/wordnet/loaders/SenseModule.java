/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet.loaders;

import android.os.Parcelable;

import org.sqlunet.HasWordId;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.model.TreeFactory;
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
			final TreeNode synsetNode = TreeFactory.makeTextNode("Sense", false).addTo(parent);
			final TreeNode membersNode = TreeFactory.makeIconTextNode("Members", R.drawable.members, false).addTo(parent);

			// synset
			synset(this.synsetId, synsetNode, false);

			// members2
			members(this.synsetId, membersNode);

			// morph
			final TreeNode morphsNode = TreeFactory.makeTreeNode("Morphs", R.drawable.morph, false).addTo(parent);
			morphs(this.wordId, morphsNode);

			// relations and samples
			if (this.expand)
			{
				TreeFactory.makeHotQueryNode("Relations", R.drawable.ic_relations, false, new RelationsQuery(this.synsetId, this.wordId)).addTo(parent);
			}
			else
			{
				TreeFactory.makeQueryNode("Relations", R.drawable.ic_relations, false, new RelationsQuery(this.synsetId, this.wordId)).addTo(parent);
			}

			if (this.expand)
			{
				TreeFactory.makeHotQueryNode("Samples", R.drawable.sample, false, new SamplesQuery(this.synsetId)).addTo(parent);
			}
			else
			{
				TreeFactory.makeQueryNode("Samples", R.drawable.sample, false, new SamplesQuery(this.synsetId)).addTo(parent);
			}

			// special
			if (this.pos != null)
			{
				switch (this.pos)
				{
					case 'v':
						final TreeNode vframesNode = TreeFactory.makeTreeNode("Verb frames", R.drawable.verbframe, false).addTo(parent);
						final TreeNode vtemplatesNode = TreeFactory.makeTreeNode("Verb templates", R.drawable.verbtemplate, false).addTo(parent);
						vFrames(this.synsetId, this.wordId, vframesNode);
						vTemplates(this.synsetId, this.wordId, vtemplatesNode);
						break;

					case 'a':
						final TreeNode adjpositionsNode = TreeFactory.makeTreeNode("Adj positions", R.drawable.adjposition, false).addTo(parent);
						adjPosition(this.synsetId, this.wordId, adjpositionsNode);
						break;
				}
			}
			else
			{
				final TreeNode vframesNode = TreeFactory.makeTreeNode("Verb frames", R.drawable.verbframe, false).addTo(parent);
				final TreeNode vtemplatesNode = TreeFactory.makeTreeNode("Verb templates", R.drawable.verbtemplate, false).addTo(parent);
				final TreeNode adjpositionsNode = TreeFactory.makeTreeNode("Adj positions", R.drawable.adjposition, false).addTo(parent);
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
