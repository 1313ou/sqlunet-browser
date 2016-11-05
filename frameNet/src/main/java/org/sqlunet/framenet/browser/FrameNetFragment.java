package org.sqlunet.framenet.browser;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.HasXId;
import org.sqlunet.browser.Module;
import org.sqlunet.framenet.R;
import org.sqlunet.framenet.loaders.FrameModule;
import org.sqlunet.framenet.loaders.LexUnitFromWordModule;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.treeview.control.IconTreeController;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;

/**
 * A fragment representing a framenet search from a (word, pos)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameNetFragment extends Fragment
{
	/**
	 * Tree view
	 */
	private TreeView treeView;

	/**
	 * Constructor
	 */
	public FrameNetFragment()
	{
		//
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// views
		final View rootView = inflater.inflate(R.layout.fragment_framenet, container, false);
		final ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.data_contents);

		// root node
		final TreeNode root = TreeNode.makeRoot();
		final TreeNode queryNode = TreeFactory.addTreeNode(root, "FrameNet", R.drawable.framenet, getActivity());
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
				return rootView;
			}
		}

		// query
		final Bundle args = getArguments();
		final int action = args.getInt(SqlUNetContract.ARG_QUERYACTION);
		final Parcelable pointer = args.getParcelable(SqlUNetContract.ARG_QUERYPOINTER);

		// module
		Module module = pointer instanceof HasXId ? new FrameModule(this) : new LexUnitFromWordModule(this);
		module.init(action, pointer);
		module.process(queryNode);

		return rootView;
	}

	@Override
	public void onSaveInstanceState(final Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putString("treeViewState", this.treeView.getSaveState());
	}
}
