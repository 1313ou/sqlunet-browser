package org.sqlunet.treeview.renderer;

import android.content.Context;

import org.sqlunet.treeview.R;

/**
 * LinkRenderer renderer (expanding this renderer will trigger link search)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class LinkRenderer extends IconLeafRenderer
{
	// private static final String TAG = "LinkRenderer"; //

	public LinkRenderer(final Context context)
	{
		super(context);
		this.layoutRes = R.layout.layout_link;
	}

	@Override
	public void onExpandEvent(boolean expand)
	{
		super.onExpandEvent(expand);
		if (expand && this.node.isLeaf())
		{
			followLink();
		}
	}

	/**
	 * Follow link
	 */
	private void followLink()
	{
		final LinkData link = (LinkData) this.node.getValue();
		link.process();
	}

	// D A T A

	/**
	 * LinkRenderer Data
	 */
	public static abstract class LinkData extends Value
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
		public LinkData(long id, int icon, CharSequence text)
		{
			super(icon, text);
			this.id = id;
		}

		/**
		 * Process
		 */
		abstract public void process();
	}
}
