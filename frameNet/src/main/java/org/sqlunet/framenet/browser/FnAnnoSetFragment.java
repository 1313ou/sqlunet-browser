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
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.renderer.IconTreeRenderer;
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
		// query
		final Bundle args = getArguments();
		final int action = args.getInt(SqlUNetContract.ARG_QUERYACTION);
		final Parcelable pointer = args.getParcelable(SqlUNetContract.ARG_QUERYPOINTER);

		// module
		String header = "AnnoSet"; //$NON-NLS-1$
		switch (action)
		{
			case SqlUNetContract.ARG_QUERYACTION_FNPATTERN:
				header = "AnnoSets for Pattern"; //$NON-NLS-1$
				break;
			case SqlUNetContract.ARG_QUERYACTION_FNVALENCEUNIT:
				header = "AnnoSets for Valence Unit"; //$NON-NLS-1$
				break;
			default:
		}

		// views
		final View rootView = inflater.inflate(R.layout.fragment_fnannoset, container, false);
		final ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.data_contents);

		// root node
		final TreeNode root = TreeNode.makeRoot();
		final TreeNode queryNode = TreeFactory.addTreeItemNode(root, header, R.drawable.annoset, getActivity());

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

		// module
		Module module;
		switch (action)
		{
			case SqlUNetContract.ARG_QUERYACTION_FNANNOSET:
				module = new AnnoSetModule(this);
				break;
			case SqlUNetContract.ARG_QUERYACTION_FNPATTERN:
				module = new AnnoSetFromPatternModule(this);
				break;
			case SqlUNetContract.ARG_QUERYACTION_FNVALENCEUNIT:
				module = new AnnoSetFromValenceUnitModule(this);
				break;
			default:
				return rootView;
		}
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
