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
	public void process(final TreeNode node)
	{
		if (this.wordId == null || this.synsetId == null)
		{
			return;
		}

		// sub nodes
		final TreeNode dataNode = TreeFactory.newTextNode("data", this.context); //
		final TreeNode membersNode = TreeFactory.newTextNode("members", this.context); //
		final TreeNode linksNode = TreeFactory.newQueryNode(new LinksQueryData(this.synsetId, this.wordId, R.drawable.ic_other, "Links"), this.context); //
		final TreeNode samplesNode = TreeFactory.newQueryNode(new SamplesQueryData(this.synsetId, R.drawable.sample, "Samples"), this.context); //

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

		// expand
		linksNode.setExpanded(this.expand);
		samplesNode.setExpanded(this.expand);
		TreeView.expand(node, false);
	}
}
