package org.sqlunet.wordnet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasPos;
import org.sqlunet.HasSynsetId;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.TreeFactory;
import org.sqlunet.view.Update;
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
	public void process(final TreeNode parent)
	{
		if (this.synsetId != null && this.synsetId != 0)
		{
			// sub nodes
			final TreeNode synsetNode = TreeFactory.newTextNode("Data", this.context); //
			final TreeNode membersNode = TreeFactory.newTextNode("Members", this.context); //

			// attach result
			parent.addChildren(synsetNode, membersNode);

			// synset
			synset(this.synsetId, synsetNode, false);

			// members
			members(this.synsetId, membersNode, false);

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
			final TreeNode linksNode = TreeFactory.newQueryNode(new LinksQuery(this.synsetId, 0, R.drawable.ic_other, "Links"), this.expand, this.context).addTo(parent);
			final TreeNode samplesNode = TreeFactory.newQueryNode(new SamplesQuery(this.synsetId, R.drawable.sample, "Samples"), this.expand, this.context).addTo(parent);

			// fire event
			Update.onQueryReady(linksNode);
			Update.onQueryReady(samplesNode);
			Update.onResults(parent);
		}
	}
}
