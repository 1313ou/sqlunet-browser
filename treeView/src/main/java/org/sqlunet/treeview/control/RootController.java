package org.sqlunet.treeview.control;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RootController extends Controller<Void>
{
	private ViewGroup contentView;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public RootController(final Context context)
	{
		super(context);
	}

	@Nullable
	@Override
	public View createNodeView(TreeNode node, Void value)
	{
		return null;
	}

	@NonNull
	@Override
	public ViewGroup getChildrenContainerView()
	{
		return this.contentView;
	}

	public void setContentView(final ViewGroup contentView)
	{
		this.contentView = contentView;
	}
}
