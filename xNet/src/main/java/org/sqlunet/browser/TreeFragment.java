/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.model.TreeFactory;
import org.sqlunet.treeview.control.RootController;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

/**
 * A fragment representing a synset.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class TreeFragment extends Fragment
{
	// static private final String TAG = "TreeF";

	/**
	 * Tree model root
	 */
	@NonNull
	protected final TreeNode treeRoot;

	/**
	 * Tree view
	 */
	@Nullable
	private TreeView treeView;

	// Data

	protected int layoutId;

	protected int treeContainerId;

	@StringRes
	protected int headerId;

	@DrawableRes
	protected int iconId;

	/**
	 * Constructor
	 */
	protected TreeFragment()
	{
		super();
		this.treeRoot = new TreeNode(null, new RootController());
	}

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// root node
		this.treeRoot.setSelectable(false);

		// sub root node
		String header = getResources().getString(this.headerId);
		TreeFactory.makeTreeNode(header, this.iconId, false).addTo(this.treeRoot);
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		// Log.d(TAG, "onCreateView() " + this);

		// view
		final View view = inflater.inflate(this.layoutId, container, false);

		// container
		final ViewGroup treeContainer = view.findViewById(this.treeContainerId);

		// tree
		// Log.d(TAG, "Create treeview");
		this.treeView = new TreeView(requireContext(), this.treeRoot);
		treeContainer.addView(this.treeView.makeView());

		// saved state
		//if (savedInstanceState != null)
		//{
			// Log.d(TAG, "Restore instance state " + this);
		//}

		return view;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		// Log.d(TAG, "Expand treeview");
		assert this.treeView != null;
		this.treeView.expandAll();
	}

	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		// Log.d(TAG, "Save instance state " + this);
		super.onSaveInstanceState(outState);
	}

	@Nullable
	public TreeView getTreeView()
	{
		return this.treeView;
	}
}
