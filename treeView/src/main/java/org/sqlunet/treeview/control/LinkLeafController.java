package org.sqlunet.treeview.control;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Link leaf controller
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class LinkLeafController extends LeafController
{
	// static private final String TAG = "LinkLeafController";

	/**
	 * Constructor
	 */
	public LinkLeafController()
	{
		super();
		this.layoutRes = R.layout.layout_leaf_link;
	}

	@Override
	public View createNodeView(@NonNull final Context context, final TreeNode node, @NonNull final Value value)
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

	@Override
	public void onExpandEvent(boolean triggerQueries)
	{
		super.onExpandEvent(triggerQueries);

		if (triggerQueries)
		{
			followLink();
		}
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
			final Link link = (Link) value.payload[0];
			link.process();
		}
	}
}
