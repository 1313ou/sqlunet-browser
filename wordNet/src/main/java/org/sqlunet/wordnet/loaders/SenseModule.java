package org.sqlunet.wordnet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasWordId;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;
import org.sqlunet.wordnet.R;

/**
 * Module for WordNet sense
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

public class SenseModule extends SynsetModule
{
	/**
	 * WordId
	 */
	private Long wordId;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public SenseModule(final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	public void init(final Parcelable parcelable)
	{
		super.init(parcelable);

		// get arguments
		if (parcelable instanceof HasWordId)
		{
			final HasWordId query = (HasWordId) parcelable;
			this.wordId = query.getWordId();
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.wordId == null || this.synsetId == null)
		{
			return;
		}

		// sub nodes
		final TreeNode dataNode = TreeFactory.newTextNode("data", SenseModule.this.getContext()); //$NON-NLS-1$
		final TreeNode membersNode = TreeFactory.newTextNode("members", SenseModule.this.getContext()); //$NON-NLS-1$
		final TreeNode linksNode = TreeFactory.newQueryNode(new LinksQuery(this.synsetId, this.wordId, R.drawable.ic_other, "Links"), SenseModule.this.getContext()); //$NON-NLS-1$
		final TreeNode samplesNode = TreeFactory.newQueryNode(new SamplesQuery(this.synsetId, R.drawable.sample, "Samples"), SenseModule.this.getContext()); //$NON-NLS-1$

		// attach result
		node.addChildren(dataNode, membersNode, linksNode, samplesNode);

		// synset
		synset(this.synsetId, dataNode, false);

		// members
		members(this.synsetId, membersNode, false);

		// morph
		morphs(this.wordId, node);

		// special
		if (this.pos != null)
		{
			switch (this.pos)
			{
				case 'v':
					vFrames(this.synsetId, this.wordId, node);
					vFrameSentences(this.synsetId, this.wordId, node);
					break;

				case 'a':
					adjPosition(this.synsetId, this.wordId, node);
					break;
			}
		}
		else
		{
			vFrames(this.synsetId, this.wordId, node);
			vFrameSentences(this.synsetId, this.wordId, node);
			adjPosition(this.synsetId, this.wordId, node);
		}

		TreeView.expand(node, false);
	}
}
