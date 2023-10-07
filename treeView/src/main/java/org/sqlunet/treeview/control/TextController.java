/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.treeview.control;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.NonNull;

/**
 * Text controller
 *
 * @author Bogdan Melnychuk on 2/11/15.
 */
public class TextController extends Controller<Object>
{
	/**
	 * Constructor
	 *
	 * @param breakExpand whether this controller breaks expansion
	 */
	public TextController(final boolean breakExpand)
	{
		super(breakExpand);
	}

	@NonNull
	@Override
	public View createNodeView(@NonNull final Context context, final TreeNode node, final Object value, final int minHeight)
	{
		final TextView textView = new TextView(context);
		if (minHeight > 0)
		{
			textView.setMinimumHeight(minHeight);
		}

		if (value instanceof CompositeValue)
		{
			final CompositeValue data = (CompositeValue) value;
			textView.setText(data.text);
			textView.setCompoundDrawablePadding(10);
			textView.setCompoundDrawablesWithIntrinsicBounds(data.icon, 0, 0, 0);
		}
		else if (value instanceof CharSequence)
		{
			textView.setText((CharSequence) value);
		}
		else
		{
			throw new IllegalArgumentException(value.toString());
		}
		return textView;
	}
}
