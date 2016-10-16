package org.sqlunet.wordnet.loaders;

import org.sqlunet.HasWordId;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;
import org.sqlunet.wordnet.R;

import android.app.Fragment;
import android.os.Parcelable;

public class SenseModule extends SynsetModule
{
	/**
	 * Query
	 */
	private Long wordid;

	/**
	 * Constructor
	 *
	 * @param fragment0 fragment
	 */
	public SenseModule(final Fragment fragment0)
	{
		super(fragment0);
	}

	@Override
	public void init(final Parcelable parcelable)
	{
		super.init(parcelable);

		// get arguments
		if (parcelable instanceof HasWordId)
		{
			final HasWordId query = (HasWordId) parcelable;
			this.wordid = query.getWordId();
		}
	}

	// L O A D E R S

	@SuppressWarnings("boxing")
	@Override
	public void process(final TreeNode node)
	{
		if (this.wordid == null || this.synsetid == null)
			return;

		// sub nodes
		final TreeNode dataNode = TreeFactory.newTextNode("data", SenseModule.this.getContext()); //$NON-NLS-1$
		final TreeNode membersNode = TreeFactory.newTextNode("members", SenseModule.this.getContext()); //$NON-NLS-1$
		final TreeNode linksNode = TreeFactory.newQueryNode(new LinksQuery(this.synsetid, this.wordid, R.drawable.ic_other, "Links"), SenseModule.this.getContext()); //$NON-NLS-1$
		final TreeNode samplesNode = TreeFactory.newQueryNode(new SamplesQuery(this.synsetid, R.drawable.sample, "Samples"), SenseModule.this.getContext()); //$NON-NLS-1$

		// attach result
		node.addChildren(dataNode, membersNode, linksNode, samplesNode);

		// synset
		synset(this.synsetid, dataNode, false);

		// members
		members(this.synsetid, membersNode, false);

		// morph
		morphs(this.wordid, node);

		// special
		if (this.pos != null)
		{
			switch (this.pos)
			{
			case 'v':
				vframes(this.synsetid, this.wordid, node);
				vframesentences(this.synsetid, this.wordid, node);
				break;

			case 'a':
				adjposition(this.synsetid, this.wordid, node);
				break;
			}
		}
		else
		{
			vframes(this.synsetid, this.wordid, node);
			vframesentences(this.synsetid, this.wordid, node);
			adjposition(this.synsetid, this.wordid, node);
		}

		TreeView.expand(node, false);
	}
}
