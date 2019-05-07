package org.sqlunet.wordnet.loaders;

import android.os.Parcelable;

import org.sqlunet.HasPos;
import org.sqlunet.HasSynsetId;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.wordnet.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
	public SynsetModule(@NonNull final TreeFragment fragment)
	{
		super(fragment);
		this.expand = true;
	}

	/**
	 * Set expandContainer
	 *
	 * @param expand expandContainer flag
	 */
	public void setExpand(final boolean expand)
	{
		this.expand = expand;
	}

	@SuppressWarnings("WeakerAccess")
	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.synsetId = null;
		this.pos = null;
		if (pointer instanceof HasSynsetId)
		{
			final HasSynsetId synsetPointer = (HasSynsetId) pointer;
			long value = synsetPointer.getSynsetId();
			if (value != 0)
			{
				this.synsetId = value;
			}
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
			// anchor nodes
			final TreeNode synsetNode = TreeFactory.addTextNode(parent, "Synset");
			final TreeNode membersNode = TreeFactory.addIconTextNode(parent, "Members", R.drawable.members);

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
			@SuppressWarnings("unused") final TreeNode linksNode = this.expand ? TreeFactory.addHotQueryNode(parent, "Links", R.drawable.ic_links, new LinksQuery(this.synsetId, 0)) : TreeFactory.addQueryNode(parent, "Links", R.drawable.ic_links, new LinksQuery(this.synsetId, 0));
			@SuppressWarnings("unused") final TreeNode samplesNode = this.expand ? TreeFactory.addQueryNode(parent, "Samples", R.drawable.sample, new SamplesQuery(this.synsetId)) : TreeFactory.addQueryNode(parent, "Samples", R.drawable.sample, new SamplesQuery(this.synsetId));
		}
		else
		{
			TreeFactory.setNoResult(parent);
		}
	}
}
