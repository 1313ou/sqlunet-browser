/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.model.TreeFactory;
import org.sqlunet.treeview.control.RootController;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.settings.Settings;
import org.sqlunet.treeview.view.TreeViewer;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

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
	private TreeViewer treeViewer;

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
		// view
		final View view = inflater.inflate(this.layoutId, container, false);

		// tree
		// Log.d(TAG, "Create tree");
		boolean use2dScroll = getScroll2D();
		this.treeViewer = new TreeViewer(requireContext(), this.treeRoot);
		final View treeview = this.treeViewer.makeTreeView(inflater, use2dScroll);

		// container
		final ViewGroup treeContainer = view.findViewById(this.treeContainerId);
		treeContainer.addView(treeview);

		return view;
	}

	protected boolean getScroll2D()
	{
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
		return prefs.getBoolean(Settings.PREF_SCROLL_2D, false);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		// Log.d(TAG, "Expand treeview");

		assert this.treeViewer != null;
		this.treeViewer.expandAll();
	}

	@Nullable
	public TreeViewer getTreeViewer()
	{
		return this.treeViewer;
	}
}
