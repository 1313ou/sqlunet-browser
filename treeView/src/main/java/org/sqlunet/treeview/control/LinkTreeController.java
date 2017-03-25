package org.sqlunet.treeview.control;

import android.content.Context;
import android.view.View;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Link tree controller
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class LinkTreeController extends TreeController
{
	// static private final String TAG = "LinkTreeController";

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public LinkTreeController(final Context context)
	{
		super(context);
		this.layoutRes = R.layout.layout_tree_link;
	}

	@Override
	public View createNodeView(final TreeNode node, final Value value)
	{
		final View view = super.createNodeView(node, value);

		// link listener
		final View hotLink = view.findViewById(R.id.node_link);
		if (hotLink != null)
		{
			hotLink.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					followLink();
				}
			});
		}
		return view;
	}

	/**
	 * Follow link
	 */
	private void followLink()
	{
		final Value value = (Value) this.node.getValue();
		if (value != null)
		{
			final Link link = (Link) value.payload[0];
			link.process();
		}
	}
}
