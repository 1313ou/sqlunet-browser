/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.treeview.control;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.NonNull;

/**
 * Simple controller
 *
 * @author Bogdan Melnychuk on 2/11/15.
 */
public class SimpleController extends Controller<Object>
{
	/**
	 * Constructor
	 */
	public SimpleController()
	{
		super(false);
	}

	@NonNull
	@Override
	public View createNodeView(@NonNull final Context context, final TreeNode node, final Object value)
	{
		final TextView textView = new TextView(context);
		textView.setText(String.valueOf(value));
		return textView;
	}
}
