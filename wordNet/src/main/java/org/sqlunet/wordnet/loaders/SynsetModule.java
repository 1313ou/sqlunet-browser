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
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public SynsetModule(final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	public void init(final Parcelable parcelable)
	{
		super.init(parcelable);

		// get arguments
		if (parcelable instanceof HasSynsetId)
		{
			final HasSynsetId query = (HasSynsetId) parcelable;
			this.synsetId = query.getSynsetId();
		}
		if (parcelable instanceof HasPos)
		{
			final HasPos query = (HasPos) parcelable;
			this.pos = query.getPos();
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.synsetId != null && this.synsetId != 0)
		{
			// sub nodes
			final TreeNode dataNode = TreeFactory.newTextNode("data", SynsetModule.this.getContext()); //
			final TreeNode membersNode = TreeFactory.newTextNode("members", SynsetModule.this.getContext()); //
			final TreeNode samplesNode = TreeFactory.newTextNode("samples", SynsetModule.this.getContext()); // TreeFactory.newQueryNode(new SamplesQuery(this.synsetId, R.drawable.sample, "Samples"), SynsetModule.this.getContext()); //
			final TreeNode linksNode = TreeFactory.newQueryNode(new LinksQuery(this.synsetId, 0, R.drawable.ic_other, "Links"), SynsetModule.this.getContext()); //

			// attach result
			node.addChildren(dataNode, membersNode, linksNode, samplesNode);

			// synset
			synset(this.synsetId, dataNode, false);

			// members
			members(this.synsetId, membersNode, false);

			// samples
			samples(this.synsetId, samplesNode, false);

			// semLinks
			semLinks(this.synsetId, linksNode);

			// lexLinks
			lexLinks(this.synsetId, linksNode);

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

			TreeView.expand(node, false);
		}
	}
}
