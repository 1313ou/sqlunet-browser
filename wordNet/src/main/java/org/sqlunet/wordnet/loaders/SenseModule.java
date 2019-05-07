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
			final TreeNode synsetNode = TreeFactory.addTextNode(parent, "Sense");
			final TreeNode membersNode = TreeFactory.addIconTextNode(parent, "Members", R.drawable.members);

			// synset
			synset(this.synsetId, synsetNode, false);

			// members
			members(this.synsetId, membersNode);

			// morph
			final TreeNode morphsNode = TreeFactory.addTreeNode(parent, "Morphs", R.drawable.morph);
			morphs(this.wordId, morphsNode);

			// links and samples
			@SuppressWarnings("unused") final TreeNode linksNode = this.expand ?
					TreeFactory.addHotQueryNode(parent, "Links", R.drawable.ic_links, new LinksQuery(this.synsetId, this.wordId)) :
					TreeFactory.addQueryNode(parent, "Links", R.drawable.ic_links, new LinksQuery(this.synsetId, this.wordId));
			@SuppressWarnings("unused") final TreeNode samplesNode = this.expand ? TreeFactory.addHotQueryNode(parent, "Samples", R.drawable.sample, new SamplesQuery(this.synsetId)) : TreeFactory.addQueryNode(parent, "Samples", R.drawable.sample, new SamplesQuery(this.synsetId));

			// special
			if (this.pos != null)
			{
				switch (this.pos)
				{
					case 'v':
						final TreeNode vframesNode = TreeFactory.addTreeNode(parent, "Verb frames", R.drawable.verbframe);
						final TreeNode vframeSentencesNode = TreeFactory.addTreeNode(parent, "Verb frame sentences", R.drawable.verbframesentence);
						vFrames(this.synsetId, this.wordId, vframesNode);
						vFrameSentences(this.synsetId, this.wordId, vframeSentencesNode);
						break;

					case 'a':
						final TreeNode adjpositionsNode = TreeFactory.addTreeNode(parent, "Adj positions", R.drawable.adjposition);
						adjPosition(this.synsetId, this.wordId, adjpositionsNode);
						break;
				}
			}
			else
			{
				final TreeNode vframesNode = TreeFactory.addTreeNode(parent, "Verb frames", R.drawable.verbframe);
				final TreeNode vframeSentencesNode = TreeFactory.addTreeNode(parent, "Verb frame sentences", R.drawable.verbframesentence);
				final TreeNode adjpositionsNode = TreeFactory.addTreeNode(parent, "Adj positions", R.drawable.adjposition);
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
