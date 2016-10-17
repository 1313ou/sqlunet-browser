package org.sqlunet.framenet.browser;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.Module;
import org.sqlunet.framenet.R;
import org.sqlunet.framenet.loaders.SentenceModule;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.renderer.IconTreeRenderer;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;

/**
 * A fragment representing a sentence.
 *
 * @author Bernard Bou
 */
public class FnSentenceFragment extends Fragment
{

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public FnSentenceFragment()
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
		final View rootView = inflater.inflate(R.layout.fragment_fnsentence, container, false);
		final ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.data_contents);

		// root node
		final TreeNode root = TreeNode.makeRoot();
		final TreeNode queryNode = TreeFactory.addTreeItemNode(root, "Sentence", R.drawable.sentence, getActivity()); //$NON-NLS-1$

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
		final Parcelable pointer = args.getParcelable(SqlUNetContract.ARG_QUERYPOINTER);

		// module
		Module module = new SentenceModule(this);
		module.init(pointer);
		module.process(queryNode);

		return rootView;
	}
}
