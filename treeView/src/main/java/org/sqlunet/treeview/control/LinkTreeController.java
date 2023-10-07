/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.treeview.control;

import android.content.Context;
import android.view.View;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.NonNull;

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
	 * @param breakExpand whether this controller breaks expansion
	 */
	public LinkTreeController(final boolean breakExpand)
	{
		super(breakExpand);
		this.layoutRes = R.layout.layout_tree_link;
	}

	@NonNull
	@Override
	public View createNodeView(@NonNull final Context context, @NonNull final TreeNode node, @NonNull final CompositeValue value, final int minHeight)
	{
		final View view = super.createNodeView(context, node, value, minHeight);

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
			final Link link = (Link) value.payload[0];
			link.process();
		}
	}
}
