package org.sqlunet.propbank.browser;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.Module;
import org.sqlunet.propbank.R;
import org.sqlunet.propbank.loaders.RoleSetModule;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.control.IconTreeController;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;

/**
 * A fragment representing a PropBank role set
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PbRoleSetFragment extends Fragment
{
	/**
	 * Tree view
	 */
	private TreeView treeView;

	/**
	 * Constructor
	 */
	public PbRoleSetFragment()
	{
		//
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(R.layout.fragment_pbroleset, container, false);

		// container
		final ViewGroup containerView = (ViewGroup) view.findViewById(R.id.data_contents);

		// root node
		final TreeNode root = TreeNode.makeRoot();
		final TreeNode queryNode = TreeFactory.addTreeNode(root, "RoleSet", R.drawable.propbank, getActivity());

		// tree
		this.treeView = new TreeView(getActivity(), root);
		this.treeView.setDefaultAnimation(true);
		this.treeView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom); // R.style.TreeNodeStyleDivided
		this.treeView.setDefaultController(IconTreeController.class);
		containerView.addView(this.treeView.getView());

		// saved state
		if (savedInstanceState != null)
		{
			final String state = savedInstanceState.getString("treeViewState");
			if (state != null && !state.isEmpty())
			{
				this.treeView.restoreState(state);
				return view;
			}
		}

		// query
		final Bundle args = getArguments();
		final int action = args.getInt(ProviderArgs.ARG_QUERYACTION);
		if (args.containsKey(ProviderArgs.ARG_QUERYPOINTER))
		{
			// pointer
			final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);

			// module
			final Module module = new RoleSetModule(this);
			module.init(action, pointer);
			module.process(queryNode);
		}

		return view;
	}

	@Override
	public void onSaveInstanceState(final Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putString("treeViewState", this.treeView.getSaveState());
	}
}
