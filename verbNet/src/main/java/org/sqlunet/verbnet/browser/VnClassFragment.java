package org.sqlunet.verbnet.browser;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.Module;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.control.TreeController;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.verbnet.R;
import org.sqlunet.verbnet.loaders.ClassModule;
import org.sqlunet.view.TreeFactory;

/**
 * A fragment representing a VerbNet class
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VnClassFragment extends Fragment
{
	/**
	 * State of tree
	 */
	static private final String STATE_TREEVIEW = "state_treeview";

	/**
	 * Tree view
	 */
	private TreeView treeView;

	/**
	 * Constructor
	 */
	public VnClassFragment()
	{
		//
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(R.layout.fragment_vnclass, container, false);

		// container
		final ViewGroup containerView = (ViewGroup) view.findViewById(R.id.data_contents);

		// root node
		final TreeNode root = TreeNode.makeRoot();
		final TreeNode queryNode = TreeFactory.addTreeNode(root, "Classes", R.drawable.verbnet, getActivity());

		// tree
		this.treeView = new TreeView(getActivity(), root);
		this.treeView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom); // R.style.TreeNodeStyleDivided
		this.treeView.setDefaultController(TreeController.class);
		containerView.addView(this.treeView.getView());

		// saved state
		if (savedInstanceState != null)
		{
			final String state = savedInstanceState.getString(STATE_TREEVIEW);
			if (state != null && !state.isEmpty())
			{
				this.treeView.restoreState(state);
				return view;
			}
		}

		// query
		final Bundle args = getArguments();
		final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
		if (args.containsKey(ProviderArgs.ARG_QUERYPOINTER))
		{
			// pointer
			final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);

			// module
			final Module module = new ClassModule(this);
			module.init(type, pointer);
			module.process(queryNode);
		}

		return view;
	}

	@Override
	public void onSaveInstanceState(final Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putString(STATE_TREEVIEW, this.treeView.getSaveState());
	}
}
