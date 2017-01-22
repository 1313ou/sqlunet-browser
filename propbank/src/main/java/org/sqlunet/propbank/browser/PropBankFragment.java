package org.sqlunet.propbank.browser;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.HasXId;
import org.sqlunet.browser.Module;
import org.sqlunet.propbank.R;
import org.sqlunet.propbank.loaders.RoleSetFromWordModule;
import org.sqlunet.propbank.loaders.RoleSetModule;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.control.TreeController;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;

/**
 * A fragment representing a PropBank search
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PropBankFragment extends Fragment
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
	public PropBankFragment()
	{
		//
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(R.layout.fragment_propbank, container, false);

		// container
		final ViewGroup containerView = (ViewGroup) view.findViewById(R.id.data_contents);

		// root node
		final TreeNode root = TreeNode.makeRoot();
		final TreeNode queryNode = TreeFactory.addTreeNode(root, "PropBank", R.drawable.propbank, getActivity());

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
			final Module module = (pointer instanceof HasXId) ? new RoleSetModule(this) : new RoleSetFromWordModule(this);
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
