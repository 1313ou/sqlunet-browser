/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.treeview.control;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

/**
 * Query controller (expanding this controller will trigger query)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class LinkQueryController extends ColdQueryController
{
	// static private final String TAG = "LinkQueryController";

	/**
	 * Link button image resource id, may be 0
	 */
	private final int buttonImageRes;

	/**
	 * Constructor
	 *
	 * @param breakExpand whether this controller breaks expansion
	 * @param buttonImageRes image drawable id for button, 0 for default
	 */
	public LinkQueryController(final boolean breakExpand, @DrawableRes final int buttonImageRes)
	{
		super(breakExpand);
		this.layoutRes = R.layout.layout_tree_link;
		this.buttonImageRes = buttonImageRes;
	}

	@NonNull
	@Override
	public View createNodeView(@NonNull final Context context, @NonNull final TreeNode node, @NonNull final CompositeValue value)
	{
		final View view = super.createNodeView(context, node, value);

		// link button and listener
		final ImageView hotLink = view.findViewById(R.id.node_link);
		if (hotLink != null)
		{
			if (this.buttonImageRes != 0)
			{
				hotLink.setImageDrawable(AppCompatResources.getDrawable(context, this.buttonImageRes));
			}
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
