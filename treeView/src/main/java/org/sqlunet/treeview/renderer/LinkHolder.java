package org.sqlunet.treeview.renderer;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;

import android.content.Context;

/**
 * @author Bernard Bou
 */
public class LinkHolder extends IconLeafRenderer
{
	// private static final String TAG = "LinkHolder"; //$NON-NLS-1$

	public LinkHolder(Context context)
	{
		super(context);
		this.layoutRes = R.layout.layout_link;
	}

	/*
	 * (non-Javadoc)
	 * @see org.sqlunet.treeview.model.TreeNode.Renderer#toggle(boolean)
	 */
	@Override
	public void toggle(boolean active)
	{
		super.toggle(active);
		if (active && this.mNode.isLeaf())
		{
			// Log.d(TAG, "size=" + this.mNode.size());
			followLink();
		}
	}

	// D A T A

	private void followLink()
	{
		final Link link = (Link) this.mNode.getValue();
		link.process(this.mNode);
	}

	public static abstract class Link extends IconTreeItem
	{
		public final long id;

		public Link(long id0, int icon, CharSequence text)
		{
			super(icon, text);
			this.id = id0;
		}

		abstract public void process(final TreeNode node);
	}
}
