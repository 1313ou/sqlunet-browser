package org.sqlunet.framenet.browser;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.Module;
import org.sqlunet.framenet.R;
import org.sqlunet.framenet.loaders.AnnoSetFromPatternModule;
import org.sqlunet.framenet.loaders.AnnoSetFromValenceUnitModule;
import org.sqlunet.framenet.loaders.AnnoSetModule;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.control.TreeController;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;

/**
 * A fragment representing an annoSet.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnAnnoSetFragment extends Fragment
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
	public FnAnnoSetFragment()
	{
		//
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(R.layout.fragment_fnannoset, container, false);

		// container
		final ViewGroup containerView = (ViewGroup) view.findViewById(R.id.data_contents);

		// query
		final Bundle args = getArguments();
		final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);

		// header
		String header = "AnnoSet";
		switch (type)
		{
			case ProviderArgs.ARG_QUERYTYPE_FNPATTERN:
				header = "AnnoSets for Pattern";
				break;
			case ProviderArgs.ARG_QUERYTYPE_FNVALENCEUNIT:
				header = "AnnoSets for Valence Unit";
				break;
			default:
		}

		// root node
		final TreeNode root = TreeNode.makeRoot();
		final TreeNode queryNode = TreeFactory.addTreeNode(root, header, R.drawable.annoset, getActivity());

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

		// module
		if (args.containsKey(ProviderArgs.ARG_QUERYPOINTER))
		{
			// pointer
			final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);
			Module module;
			switch (type)
			{
				case ProviderArgs.ARG_QUERYTYPE_FNANNOSET:
					module = new AnnoSetModule(this);
					break;
				case ProviderArgs.ARG_QUERYTYPE_FNPATTERN:
					module = new AnnoSetFromPatternModule(this);
					break;
				case ProviderArgs.ARG_QUERYTYPE_FNVALENCEUNIT:
					module = new AnnoSetFromValenceUnitModule(this);
					break;
				default:
					return view;
			}
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
