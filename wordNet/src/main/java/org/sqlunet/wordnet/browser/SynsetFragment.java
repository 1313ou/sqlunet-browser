package org.sqlunet.wordnet.browser;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.treeview.control.IconTreeController;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;
import org.sqlunet.wordnet.R;
import org.sqlunet.wordnet.loaders.SynsetModule;

/**
 * A fragment representing a synset.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SynsetFragment extends Fragment
{
	/**
	 * Tree view
	 */
	private TreeView treeView;

	/**
	 * Whether to expand
	 */
	private boolean expand;

	/**
	 * Constructor
	 */
	public SynsetFragment()
	{
		this.expand = true;
	}

	/**
	 * Set expand
	 */
	public void setExpand(final boolean expand)
	{
		this.expand = expand;
	}

	/**
	 * Module factory
	 *
	 * @return module
	 */
	@SuppressWarnings("WeakerAccess")
	protected SynsetModule makeModule()
	{
		return new SynsetModule(this);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// layout
		final View rootView = inflater.inflate(R.layout.fragment_sense, container, false);
		final ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.data_contents);

		// root node
		final TreeNode root = TreeNode.makeRoot();
		final TreeNode queryNode = TreeFactory.addTreeNode(root, "WordNet", R.drawable.wordnet, getActivity()); //

		// tree
		this.treeView = new TreeView(getActivity(), root);
		this.treeView.setDefaultAnimation(true);
		this.treeView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom); // R.style.TreeNodeStyleDivided
		this.treeView.setDefaultController(IconTreeController.class);
		containerView.addView(this.treeView.getView());

		// saved state
		if (savedInstanceState != null)
		{
			final String state = savedInstanceState.getString("treeViewState"); //
			if (state != null && !state.isEmpty())
			{
				this.treeView.restoreState(state);
				return rootView;
			}
		}

		// query
		final Bundle args = getArguments();
		final int action = args.getInt(SqlUNetContract.ARG_QUERYACTION);
		if (args.containsKey(SqlUNetContract.ARG_QUERYPOINTER))
		{
			// pointer
			final Parcelable pointer = args.getParcelable(SqlUNetContract.ARG_QUERYPOINTER);

			// module
			SynsetModule module = makeModule();
			module.setExpand(SynsetFragment.this.expand);
			module.init(action, pointer);
			module.process(queryNode);
		}

		return rootView;
	}

	@Override
	public void onSaveInstanceState(final Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putString("treeViewState", this.treeView.getSaveState()); //
	}
}
