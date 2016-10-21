package org.sqlunet.treeview.renderer;

import android.content.Context;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Link holder
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class LinkHolder extends IconLeafRenderer
{
	// private static final String TAG = "LinkHolder"; //$NON-NLS-1$

	public LinkHolder(Context context)
	{
		super(context);
		this.layoutRes = R.layout.layout_link;
	}

	@Override
	public void toggle(boolean active)
	{
		super.toggle(active);
		if (active && this.node.isLeaf())
		{
			// Log.d(TAG, "size=" + this.node.size());
			followLink();
		}
	}

	/**
	 * Follow link
	 */
	private void followLink()
	{
		final Link link = (Link) this.node.getValue();
		link.process(this.node);
	}

	// D A T A

	/**
	 * Link
	 */
	public static abstract class Link extends IconTreeItem
	{
		/**
		 * Id used in link
		 */
		public final long id;

		/**
		 * Constructor
		 *
		 * @param id id
		 * @param icon extra icon
		 * @param text label text
		 */
		public Link(long id, int icon, CharSequence text)
		{
			super(icon, text);
			this.id = id;
		}

		/**
		 * Process
		 * @param node node
		 */
		abstract public void process(@SuppressWarnings("UnusedParameters") final TreeNode node);
	}
}
