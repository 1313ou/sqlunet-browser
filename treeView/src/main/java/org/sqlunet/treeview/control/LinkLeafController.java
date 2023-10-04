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
 * Link leaf controller
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class LinkLeafController extends LeafController
{
	// static private final String TAG = "LinkLeafController";

	/**
	 * Constructor
	 *
	 * @param breakExpand whether this controller breaks expansion
	 */
	public LinkLeafController(final boolean breakExpand)
	{
		super(breakExpand);
		this.layoutRes = Controller.V2 ? R.layout.layout_leaf_link_2 : R.layout.layout_leaf_link;
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

	@Override
	public void fire()
	{
		followLink();
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
