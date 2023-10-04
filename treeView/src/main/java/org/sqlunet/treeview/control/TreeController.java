/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.treeview.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.NonNull;

/**
 * Tree controller with icon
 *
 * @author Bogdan Melnychuk on 2/12/15.
 */
public class TreeController extends Controller<CompositeValue>
{
	// static private final String TAG = "TreeController";

	/**
	 * Junction view
	 */
	@SuppressWarnings("WeakerAccess")
	protected ImageView junctionView;

	/**
	 * Resource used (changed by derived classes)
	 */
	int layoutRes = R.layout.layout_tree_2;

	/**
	 * Constructor
	 *
	 * @param breakExpand whether this controller breaks expansion
	 */
	public TreeController(final boolean breakExpand)
	{
		super(breakExpand);
	}

	@SuppressWarnings("WeakerAccess")
	@NonNull
	@Override
	public View createNodeView(@NonNull final Context context, @NonNull final TreeNode node, @NonNull final CompositeValue value)
	{
		final LayoutInflater inflater = LayoutInflater.from(context);
		@SuppressLint("InflateParams") final View view = inflater.inflate(this.layoutRes, null, false);
		assert view != null;

		// junction icon (arrow)
		this.junctionView = view.findViewById(R.id.junction_icon);
		if (node.isDeadend())
		{
			markDeadend();
		}
		else
		{
			markCollapsed(); // this.junctionView.setImageResource(R.drawable.ic_collapsed);
		}

		// icon
		if (value.icon != 0)
		{
			final ImageView iconView = view.findViewById(R.id.node_icon);
			iconView.setImageResource(value.icon);
		}

		// text
		TextView valueView = view.findViewById(R.id.node_value);
		valueView.setText(value.text);

		return view;
	}

	@Override
	public void onExpandEvent()
	{
		if (this.node.isDeadend())
		{
			markDeadend();
		}
		else
		{
			markExpanded();
		}
	}

	@Override
	public void onCollapseEvent()
	{
		if (this.node.isDeadend())
		{
			markDeadend();
		}
		else
		{
			markCollapsed();
		}
	}

	@Override
	public void deadend()
	{
		markDeadend();
	}

	@SuppressWarnings("WeakerAccess")
	protected void markExpanded()
	{
		this.junctionView.setImageResource(R.drawable.ic_expanded);
	}

	@SuppressWarnings("WeakerAccess")
	protected void markCollapsed()
	{
		this.junctionView.setImageResource(R.drawable.ic_collapsed);
	}

	@SuppressWarnings("WeakerAccess")
	protected void markDeadend()
	{
		this.junctionView.setImageResource(R.drawable.ic_deadend);
	}
}
