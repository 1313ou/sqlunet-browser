package org.sqlunet.treeview.control;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;

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
	 */
	public LinkQueryController()
	{
		super();
		this.layoutRes = R.layout.layout_tree_link;
	}

	@Override
	public View createNodeView(@NonNull final Context context, final TreeNode node, @NonNull final CompositeValue value)
	{
		final View view = super.createNodeView(context, node, value);
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
		final CompositeValue value = (CompositeValue) this.node.getValue();
		if (value != null)
		{
			assert value.payload != null;
			final Link link = (Link) value.payload[1];
			link.process();
		}
	}
}
