package org.sqlunet.browser;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.treeview.control.TreeController;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;
import org.sqlunet.xnet.R;

/**
 * A fragment representing a synset.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class TreeFragment extends Fragment
{
	static private final String TAG = "TreeF";

	/**
	 * Tree view
	 */
	@Nullable
	protected TreeView treeView;

	// Data

	protected int layoutId;

	protected int treeContainerId;

	protected String header;

	protected int iconId;

	/**
	 * Constructor
	 */
	public TreeFragment()
	{
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		Log.d(TAG, "ON CREATE (TREE) VIEW " + this);

		// retain instance
		setRetainInstance(true);

		// view
		final View view = inflater.inflate(this.layoutId, container, false);

		// container
		final ViewGroup treeContainer = view.findViewById(this.treeContainerId);

		// root node
		final TreeNode root = TreeNode.makeRoot();
		TreeFactory.addTreeNode(root, header, iconId, requireContext());

		// tree
		this.treeView = new TreeView(requireContext(), root);
		this.treeView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom); // R.style.TreeNodeStyleDivided
		this.treeView.setDefaultController(TreeController.class);
		treeContainer.addView(this.treeView.getView());

		// saved state
		if (savedInstanceState != null)
		{
			Log.d(TAG, "restore instance state " + this);
		}

		return view;
	}

	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		Log.d(TAG, "save instance state " + this);
		super.onSaveInstanceState(outState);
	}
}
