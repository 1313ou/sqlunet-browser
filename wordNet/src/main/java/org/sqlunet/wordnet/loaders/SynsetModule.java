package org.sqlunet.wordnet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasPos;
import org.sqlunet.HasSynsetId;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;
import org.sqlunet.wordnet.R;

/**
 * Module for WordNet synset
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

public class SynsetModule extends BasicModule
{
	/**
	 * Synset id
	 */
	Long synsetId;

	/**
	 * Pos
	 */
	Character pos;

	/**
	 * Expand flag
	 */
	@SuppressWarnings("WeakerAccess")
	protected boolean expand;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public SynsetModule(final Fragment fragment)
	{
		super(fragment);
		this.expand = true;
	}

	/**
	 * Set expand
	 *
	 * @param expand expand flag
	 */
	public void setExpand(final boolean expand)
	{
		this.expand = expand;
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.synsetId = null;
		this.pos = null;
		if (pointer instanceof HasSynsetId)
		{
			final HasSynsetId synsetPointer = (HasSynsetId) pointer;
			this.synsetId = synsetPointer.getSynsetId();
		}
		if (pointer instanceof HasPos)
		{
			final HasPos posPointer = (HasPos) pointer;
			this.pos = posPointer.getPos();
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.synsetId != null && this.synsetId != 0)
		{
			// sub nodes
			final TreeNode dataNode = TreeFactory.newTextNode("data", this.context); //
			final TreeNode membersNode = TreeFactory.newTextNode("members", this.context); //
			final TreeNode linksNode = TreeFactory.newQueryNode(new LinksQueryData(this.synsetId, 0, R.drawable.ic_other, "Links"), this.context); //
			final TreeNode samplesNode = TreeFactory.newQueryNode(new SamplesQueryData(this.synsetId, R.drawable.sample, "Samples"), this.context); //

			// attach result
			node.addChildren(dataNode, membersNode, linksNode, samplesNode);

			// synset
			synset(this.synsetId, dataNode, false);

			// members
			members(this.synsetId, membersNode, false);

			// samples
			samples(this.synsetId, samplesNode, false);

			// special
			if (this.pos != null)
			{
				switch (this.pos)
				{
					case 'v':
						this.vFrames(this.synsetId, node);
						this.vFrameSentences(this.synsetId, node);
						break;

					case 'a':
						this.adjPosition(this.synsetId, node);
						break;
				}
			}

			// expand
			TreeView.expand(node, false);
			if (this.expand)
			{
				TreeView.expand(linksNode, false);
				TreeView.expand(samplesNode, false);
			}
		}
	}
}
