package org.sqlunet.verbnet.browser;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.Module;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.renderer.IconTreeRenderer;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.verbnet.R;
import org.sqlunet.verbnet.loaders.ClassModule;
import org.sqlunet.view.TreeFactory;

/**
 * A fragment representing a lexunit
 *
 * @author Bernard Bou
 */
public class VnClassFragment extends Fragment
{
	/**
	 * Tree view
	 */
	private TreeView treeView;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// views
		final View rootView = inflater.inflate(R.layout.fragment_vnclass, container, false);
		final ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.data_contents);

		// root node
		final TreeNode root = TreeNode.makeRoot();
		final TreeNode queryNode = TreeFactory.addTreeItemNode(root, "Classes", R.drawable.verbnet, getActivity()); //$NON-NLS-1$

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
		Module module = new ClassModule(this);
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

	/**
	 * Constructor
	 */
	public VnClassFragment()
	{
		//
	}
}
