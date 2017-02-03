package org.sqlunet.wordnet.browser;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.Module;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.control.TreeController;
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
	static private final String TAG = "SynsetFragment";

	/**
	 * State of tree
	 */
	static private final String STATE_TREEVIEW = "state_treeview";

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

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(R.layout.fragment_sense, container, false);

		// container
		final ViewGroup containerView = (ViewGroup) view.findViewById(R.id.data_contents);

		// root node
		final TreeNode root = TreeNode.makeRoot();
		final TreeNode queryNode = TreeFactory.addTreeNode(root, "WordNet", R.drawable.wordnet, getActivity());

		// tree
		this.treeView = new TreeView(getActivity(), root);
		this.treeView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom); // R.style.TreeNodeStyleDivided
		this.treeView.setDefaultController(TreeController.class);
		containerView.addView(this.treeView.getView());

		// saved state
		if (savedInstanceState != null)
		{
			Log.d(TAG, "restore instance state " + this);
			final String state = savedInstanceState.getString(STATE_TREEVIEW);
			if (state != null && !state.isEmpty())
			{
				this.treeView.restoreState(state);
			}
		}
		return view;
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// root node
		final TreeNode root = this.treeView.getRoot();
		final TreeNode queryNode = root.getChildren().iterator().next();

		// query
		final Bundle args = getArguments();
		final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
		if (args.containsKey(ProviderArgs.ARG_QUERYPOINTER))
		{
			// pointer
			final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);

			// module
			final Module module = makeModule();
			module.init(type, pointer);
			module.process(queryNode);
		}
	}

	@Override
	public void onSaveInstanceState(final Bundle outState)
	{
		Log.d(TAG, "save instance state " + this);
		super.onSaveInstanceState(outState);
		outState.putString(STATE_TREEVIEW, this.treeView.getSaveState());
	}

	/**
	 * Module factory
	 *
	 * @return module
	 */
	@SuppressWarnings("WeakerAccess")
	protected Module makeModule()
	{
		final SynsetModule module = new SynsetModule(this);
		module.setExpand(SynsetFragment.this.expand);
		return module;
	}

	/**
	 * Set expand
	 */
	public void setExpand(final boolean expand)
	{
		this.expand = expand;
	}
}
