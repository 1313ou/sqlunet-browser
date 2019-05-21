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
 * Text controller
 *
 * @author Bogdan Melnychuk on 2/11/15.
 */
public class TextController extends Controller<CharSequence>
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
	public View createNodeView(@NonNull final Context context, final TreeNode node, final CharSequence value)
	{
		final TextView textView = new TextView(context);
		textView.setText(value);
		return textView;
	}
}
