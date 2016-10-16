package org.sqlunet.framenet.browser;

import org.sqlunet.browser.Module;
import org.sqlunet.framenet.R;
import org.sqlunet.framenet.loaders.FrameModule;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.treeview.renderer.IconTreeRenderer;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment representing a frame
 *
 * @author Bernard Bou
 */
public class FnFrameFragment extends Fragment
{
	/**
	 * Tree view
	 */
	private TreeView treeView;

	/**
	 * Constructor
	 */
	public FnFrameFragment()
	{
		//
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// views
		final View rootView = inflater.inflate(R.layout.fragment_fnframe, container, false);
		final ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.data_contents);

		// root node
		final TreeNode root = TreeNode.makeRoot();
		final TreeNode queryNode = TreeFactory.addTreeItemNode(root, "Frame", R.drawable.fnframe, getActivity()); //$NON-NLS-1$

		// tree
		this.treeView = new TreeView(getActivity(), root);
		this.treeView.setDefaultAnimation(true);
		this.treeView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom); // R.style.TreeNodeStyleDivided
		this.treeView.setDefaultViewHolder(IconTreeRenderer.class);
		containerView.addView(this.treeView.getView());

		// saved state
		if (savedInstanceState != null)
		{
			final String state = savedInstanceState.getString("treeViewState"); //$NON-NLS-1$
			if (state != null && !state.isEmpty())
			{
				this.treeView.restoreState(state);
				return rootView;
			}
		}

		// query
		final Bundle args = getArguments();
		final Parcelable pointer = args.getParcelable(SqlUNetContract.ARG_QUERYPOINTER);

		// module
		/*
	  Module
	 */
		Module module = new FrameModule(this);
		module.init(pointer);
		module.process(queryNode);

		return rootView;
	}

	@Override
	public void onSaveInstanceState(final Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putString("treeViewState", this.treeView.getSaveState()); //$NON-NLS-1$
	}
}
