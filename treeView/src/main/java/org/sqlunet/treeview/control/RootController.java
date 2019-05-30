/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.treeview.control;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.SubtreeView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RootController extends Controller<Void>
{
	private ViewGroup contentView;

	/**
	 * Constructor
	 */
	public RootController()
	{
		super(false);
	}

	@Nullable
	@Override
	public View createNodeView(@NonNull final Context context, TreeNode node, Void value)
	{
		return null;
	}

	@NonNull
	@Override
	public ViewGroup getChildrenView()
	{
		return this.contentView;
	}

	@NonNull
	@Override
	public SubtreeView getSubtreeView()
	{
		if (this.contentView != null && this.contentView.getChildCount() > 0)
		{
			return (SubtreeView) this.contentView.getChildAt(0);
		}
		return null;
	}

	public void setContentView(final ViewGroup contentView)
	{
		this.contentView = contentView;
	}
}
