package org.sqlunet.wordnet.browser;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.HasWordId;
import org.sqlunet.browser.Module;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.renderer.IconTreeRenderer;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;
import org.sqlunet.wordnet.R;
import org.sqlunet.wordnet.loaders.SenseModule;
import org.sqlunet.wordnet.loaders.SynsetModule;

/**
 * A fragment representing a synset.
 *
 * @author Bernard Bou
 */
public class SynsetFragment extends Fragment
{

	/**
	 * Constructor
	 */
	public SynsetFragment()
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
		// layout
		final View rootView = inflater.inflate(R.layout.fragment_sense, container, false);
		final ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.data_contents);

		// root node
		final TreeNode root = TreeNode.makeRoot();
		final TreeNode queryNode = TreeFactory.addTreeItemNode(root, "WordNet", R.drawable.wordnet, getActivity()); //$NON-NLS-1$

		// tree
		TreeView treeView = new TreeView(getActivity(), root);
		treeView.setDefaultAnimation(true);
		treeView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom); // R.style.TreeNodeStyleDivided
		treeView.setDefaultViewHolder(IconTreeRenderer.class);
		containerView.addView(treeView.getView());

		// saved state
		if (savedInstanceState != null)
		{
			final String state = savedInstanceState.getString("treeViewState"); //$NON-NLS-1$
			if (state != null && !state.isEmpty())
			{
				treeView.restoreState(state);
				return rootView;
			}
		}

		// query
		final Bundle args = getArguments();
		if (args.containsKey(SqlUNetContract.ARG_QUERYPOINTER))
		{
			final Parcelable parcelable = args.getParcelable(SqlUNetContract.ARG_QUERYPOINTER);
			boolean isSense = false;
			if (parcelable instanceof HasWordId)
			{
				final HasWordId query = (HasWordId) parcelable;
				final Long id = query.getWordId();
				isSense = id != null;
			}

			// module
			Module module = isSense ? new SenseModule(this) : new SynsetModule(this);
			module.init(parcelable);
			module.process(queryNode);
		}

		return rootView;
	}
}
