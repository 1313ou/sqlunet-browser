package org.sqlunet.treeview.control;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Query controller (expanding this controller will trigger query)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class LinkQueryController extends QueryController
{
	// static private final String TAG = "LinkQueryController";

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public LinkQueryController(final Context context, final boolean triggerNow)
	{
		super(context, triggerNow);
		this.layoutRes = R.layout.layout_tree_link;
	}

	@Override
	public View createNodeView(final TreeNode node, @NonNull final Value value)
	{
		final View view = super.createNodeView(node, value);
		assert view != null;

		// link listener
		final View hotLink = view.findViewById(R.id.node_link);
		if (hotLink != null)
		{
			hotLink.setOnClickListener(v -> followLink());
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
			assert value.payload != null;
			final Link link = (Link) value.payload[1];
			link.process();
		}
	}
}
