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
 * More controller with icon
 *
 * @author Bogdan Melnychuk on 2/12/15.
 */
public class MoreController extends Controller<CompositeValue>
{
	/// static private final String TAG = "LeafController";

	/**
	 * Resource used (changed by derived classes)
	 */
	private final int layoutRes = R.layout.layout_more;

	/**
	 * Constructor
	 *
	 * @param breakExpand whether this controller breaks expansion
	 */
	public MoreController(final boolean breakExpand)
	{
		super(breakExpand);
	}

	@NonNull
	@Override
	public View createNodeView(@NonNull final Context context, final TreeNode node, @NonNull final CompositeValue value, final int minHeight)
	{
		final LayoutInflater inflater = LayoutInflater.from(context);
		@SuppressLint("InflateParams") final View view = inflater.inflate(this.layoutRes, null, false);
		assert view != null;
		if (minHeight > 0)
		{
			view.setMinimumHeight(minHeight);
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
