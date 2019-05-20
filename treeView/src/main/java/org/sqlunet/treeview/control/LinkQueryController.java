package org.sqlunet.treeview.control;

import android.content.Context;
import android.view.View;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.NonNull;

/**
 * Query controller (expanding this controller will trigger query)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class LinkQueryController extends ColdQueryController
{
	// static private final String TAG = "LinkQueryController";

	/**
	 * Constructor
	 *
	 * @param breakExpand whether this controller breaks expansion
	 */
	public LinkQueryController(final boolean breakExpand)
	{
		super(breakExpand);
		this.layoutRes = R.layout.layout_tree_link;
	}

	@NonNull
	@Override
	public View createNodeView(@NonNull final Context context, final TreeNode node, @NonNull final CompositeValue value)
	{
		final View view = super.createNodeView(context, node, value);

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
		final CompositeValue value = (CompositeValue) this.node.getValue();
		if (value != null)
		{
			assert value.payload != null;
			final Link link = (Link) value.payload[1];
			link.process();
		}
	}
}
