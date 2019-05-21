/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
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

			// members
			members(this.synsetId, membersNode);

			// morph
			final TreeNode morphsNode = TreeFactory.makeTreeNode("Morphs", R.drawable.morph, false).addTo(parent);
			morphs(this.wordId, morphsNode);

			// links and samples
			@SuppressWarnings("unused") final TreeNode linksNode = this.expand ?
					TreeFactory.makeHotQueryNode("Links", R.drawable.ic_links, false, new LinksQuery(this.synsetId, this.wordId)).addTo(parent) :
					TreeFactory.makeQueryNode("Links", R.drawable.ic_links, false, new LinksQuery(this.synsetId, this.wordId)).addTo(parent);
			@SuppressWarnings("unused") final TreeNode samplesNode = this.expand ?
					TreeFactory.makeHotQueryNode("Samples", R.drawable.sample, false, new SamplesQuery(this.synsetId)).addTo(parent) :
					TreeFactory.makeQueryNode("Samples", R.drawable.sample, false, new SamplesQuery(this.synsetId)).addTo(parent);

			// special
			if (this.pos != null)
			{
				switch (this.pos)
				{
					case 'v':
						final TreeNode vframesNode = TreeFactory.makeTreeNode("Verb frames", R.drawable.verbframe, false).addTo(parent);
						final TreeNode vframeSentencesNode = TreeFactory.makeTreeNode("Verb frame sentences", R.drawable.verbframesentence, false).addTo(parent);
						vFrames(this.synsetId, this.wordId, vframesNode);
						vFrameSentences(this.synsetId, this.wordId, vframeSentencesNode);
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
				final TreeNode vframeSentencesNode = TreeFactory.makeTreeNode("Verb frame sentences", R.drawable.verbframesentence, false).addTo(parent);
				final TreeNode adjpositionsNode = TreeFactory.makeTreeNode("Adj positions", R.drawable.adjposition, false).addTo(parent);
				vFrames(this.synsetId, this.wordId, vframesNode);
				vFrameSentences(this.synsetId, this.wordId, vframeSentencesNode);
				adjPosition(this.synsetId, this.wordId, adjpositionsNode);
			}
		}
		else
		{
			TreeFactory.setNoResult(parent);
		}
	}
}
