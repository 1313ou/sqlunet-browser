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
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SynsetFragment extends Fragment
{
	/**
	 * Tree view
	 */
	private TreeView treeView;

	/**
	 * Constructor
	 */
	public SynsetFragment()
	{
		//
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// layout
		final View rootView = inflater.inflate(R.layout.fragment_sense, container, false);
		final ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.data_contents);

		// root node
		final TreeNode root = TreeNode.makeRoot();
		final TreeNode queryNode = TreeFactory.addTreeItemNode(root, "WordNet", R.drawable.wordnet, getActivity()); //

		// tree
		this.treeView = new TreeView(getActivity(), root);
		this.treeView.setDefaultAnimation(true);
		this.treeView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom); // R.style.TreeNodeStyleDivided
		this.treeView.setDefaultRenderer(IconTreeRenderer.class);
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
			final Parcelable pointer = args.getParcelable(SqlUNetContract.ARG_QUERYPOINTER);
			boolean isSense = false;
			if (pointer instanceof HasWordId)
			{
				final HasWordId wordId = (HasWordId) pointer;
				final Long id = wordId.getWordId();
				isSense = id != null;
			}

			// module
			Module module = isSense ? new SenseModule(this) : new SynsetModule(this);
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
