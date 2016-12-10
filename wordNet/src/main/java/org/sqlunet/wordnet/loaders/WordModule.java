package org.sqlunet.wordnet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasWordId;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.FireEvent;
import org.sqlunet.view.TreeFactory;

/**
 * Module for WordNet word
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

public class WordModule extends BasicModule
{
	/**
	 * Word id
	 */
	Long wordId;

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
	public WordModule(final Fragment fragment)
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
		this.wordId = null;
		if (pointer instanceof HasWordId)
		{
			final HasWordId wordPointer = (HasWordId) pointer;
			this.wordId = wordPointer.getWordId();
		}
	}

	@Override
	public void process(final TreeNode parent)
	{
		if (this.wordId != null && this.wordId != 0)
		{
			// sub nodes
			final TreeNode sensesNode = TreeFactory.newTextNode("Senses", this.context);

			// attach node
			parent.addChild(sensesNode);

			// synset
			senses(this.wordId, sensesNode);

			/*
			// links and samples
			final TreeNode linksNode = TreeFactory.newQueryNode(new LinksQuery(this.wordId, 0, R.drawable.ic_links, "Links"), this.expand, this.context).addTo(parent);
			final TreeNode samplesNode = TreeFactory.newQueryNode(new SamplesQuery(this.wordId, R.drawable.sample, "Samples"), this.expand, this.context).addTo(parent);

			// fire event
			FireEvent.onQueryReady(linksNode);
			FireEvent.onQueryReady(samplesNode);
			*/

			FireEvent.onResults(parent);
		}
	}
}
