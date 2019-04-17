package org.sqlunet.wordnet.loaders;

import android.content.Context;
import android.os.Parcelable;

import org.sqlunet.HasWordId;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.wordnet.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
		if (this.wordId == null || this.synsetId == null)
		{
			return;
		}

		// sub nodes
		final TreeNode synsetNode = TreeFactory.addTextNode(parent, "Sense");
		final TreeNode membersNode = TreeFactory.addIconTextNode(parent, "Members", R.drawable.members);
		final TreeNode morphsNode = TreeFactory.addTreeNode(parent, "Morphs", R.drawable.ic_other);

		// attach result
		parent.addChildren(synsetNode, membersNode, morphsNode);

		// synset
		synset(this.synsetId, synsetNode, false);

		// members
		members(this.synsetId, membersNode);

		// morph
		morphs(this.wordId, morphsNode);

		// special
		if (this.pos != null)
		{
			switch (this.pos)
			{
				case 'v':
					final TreeNode vframesNode = TreeFactory.addTreeNode(parent, "Verb frames", R.drawable.ic_other);
					final TreeNode vframeSentencesNode = TreeFactory.addTreeNode(parent, "Verb frame sentences", R.drawable.ic_other);
					vFrames(this.synsetId, this.wordId, vframesNode);
					vFrameSentences(this.synsetId, this.wordId, vframeSentencesNode);
					break;

				case 'a':
					final TreeNode adjpositionsNode = TreeFactory.addTreeNode(parent, "Adj positions", R.drawable.ic_other);
					adjPosition(this.synsetId, this.wordId, adjpositionsNode);
					break;
			}
		}
		else
		{
			final TreeNode vframesNode = TreeFactory.addTreeNode(parent, "Verb frames", R.drawable.ic_other);
			final TreeNode vframeSentencesNode = TreeFactory.addTreeNode(parent, "Verb frame sentences", R.drawable.ic_other);
			final TreeNode adjpositionsNode = TreeFactory.addTreeNode(parent, "Adj positions", R.drawable.ic_other);
			vFrames(this.synsetId, this.wordId, vframesNode);
			vFrameSentences(this.synsetId, this.wordId, vframeSentencesNode);
			adjPosition(this.synsetId, this.wordId, adjpositionsNode);
		}

		// links and samples
		final TreeNode linksNode = this.expand ?
				TreeFactory.addHotQueryNode(parent, "Links", R.drawable.ic_links, new LinksQuery(this.synsetId, this.wordId)).addTo(parent) :
				TreeFactory.addQueryNode(parent, "Links", R.drawable.ic_links, new LinksQuery(this.synsetId, this.wordId)).addTo(parent);
		final TreeNode samplesNode = this.expand ?
				TreeFactory.addHotQueryNode(parent, "Samples", R.drawable.sample, new SamplesQuery(this.synsetId)).addTo(parent) :
				TreeFactory.addQueryNode(parent, "Samples", R.drawable.sample, new SamplesQuery(this.synsetId)).addTo(parent);
	}
}
