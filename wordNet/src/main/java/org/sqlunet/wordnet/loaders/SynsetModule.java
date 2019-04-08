package org.sqlunet.wordnet.loaders;

import android.os.Parcelable;

import org.sqlunet.HasPos;
import org.sqlunet.HasSynsetId;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.FireEvent;
import org.sqlunet.wordnet.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Module for WordNet synset
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

public class SynsetModule extends BaseModule
{
	/**
	 * Synset id
	 */
	@Nullable
	Long synsetId;

	/**
	 * Pos
	 */
	@Nullable
	Character pos;

	/**
	 * Expand flag
	 */
	boolean expand;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public SynsetModule(@NonNull final Fragment fragment)
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
	public void process(@NonNull final TreeNode parent)
	{
		if (this.synsetId != null && this.synsetId != 0)
		{
			// sub nodes
			final TreeNode synsetNode = TreeFactory.newTextNode("Synset");
			final TreeNode membersNode = TreeFactory.newNode("Members", R.drawable.members);

			// attach result
			parent.addChildren(synsetNode, membersNode);

			// synset
			synset(this.synsetId, synsetNode, false);

			// members
			members(this.synsetId, membersNode);

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
			final TreeNode linksNode = this.expand ? TreeFactory.newQueryNode("Links", R.drawable.ic_links, new LinksQuery(this.synsetId, 0)).addTo(parent) : TreeFactory.newQueryNode("Links", R.drawable.ic_links, new LinksQuery(this.synsetId, 0)).addTo(parent);
			final TreeNode samplesNode = this.expand ? TreeFactory.newQueryNode("Samples", R.drawable.sample, new SamplesQuery(this.synsetId)).addTo(parent) : TreeFactory.newQueryNode("Samples", R.drawable.sample, new SamplesQuery(this.synsetId)).addTo(parent);

			// fire event
			FireEvent.onQueryReady(linksNode);
			FireEvent.onQueryReady(samplesNode);

			FireEvent.onResults(parent);
		}
	}
}
