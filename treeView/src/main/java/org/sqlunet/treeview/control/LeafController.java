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
 * Leaf controller with icon
 *
 * @author Bogdan Melnychuk on 2/12/15.
 */
public class LeafController extends Controller<CompositeValue>
{
	/// static private final String TAG = "LeafController";

	/**
	 * Resource used (changed by derived classes)
	 */
	protected int layoutRes = R.layout.layout_leaf;

	/**
	 * Constructor
	 *
	 * @param breakExpand whether this controller breaks expansion
	 */
	public LeafController(final boolean breakExpand)
	{
		super(breakExpand);
	}

	@NonNull
	@Override
	public View createNodeView(@NonNull final Context context, final TreeNode node, @NonNull final CompositeValue value, final int treeRowMinHeight)
	{
		final LayoutInflater inflater = LayoutInflater.from(context);
		@SuppressLint("InflateParams") final View view = inflater.inflate(this.layoutRes, null, false);
		assert view != null;
		if (treeRowMinHeight > 0)
		{
			view.setMinimumHeight(treeRowMinHeight);
		}

		// junction
		// final ImageView junctionView = (ImageView) view.findViewById(R.id.junction_icon);

		// icon
		final ImageView iconView = view.findViewById(R.id.node_icon);
		iconView.setImageResource(value.icon);

		// text
		TextView valueView = view.findViewById(R.id.node_value);
		valueView.setText(value.text);

		return view;
	}
}
