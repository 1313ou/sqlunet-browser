package org.sqlunet.predicatematrix.browser;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.Module;
import org.sqlunet.predicatematrix.PmRolePointer;
import org.sqlunet.predicatematrix.R;
import org.sqlunet.predicatematrix.loaders.PredicateRoleFromWordModule;
import org.sqlunet.predicatematrix.loaders.PredicateRoleModule;
import org.sqlunet.predicatematrix.settings.Settings;
import org.sqlunet.predicatematrix.style.PredicateMatrixFactories;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.renderer.IconTreeRenderer;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;

/**
 * A fragment representing a predicate matrix
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PredicateMatrixFragment extends Fragment
{
	/**
	 * Tree view
	 */
	private TreeView treeView;

	/**
	 * Constructor
	 */
	public PredicateMatrixFragment()
	{
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// query
		final Bundle args = getArguments();
		final int action = args.getInt(SqlUNetContract.ARG_QUERYACTION);
		final Parcelable pointer = args.getParcelable(SqlUNetContract.ARG_QUERYPOINTER);
		assert pointer != null;

		// module
		final SpannableStringBuilder hsb = new SpannableStringBuilder();
		hsb.append("PredicateMatrix "); //
		Spanner.append(hsb, pointer.toString(), 0, PredicateMatrixFactories.wordFactory);

		// views
		final View rootView = inflater.inflate(R.layout.fragment_predicatematrix, container, false);
		if (action == SqlUNetContract.ARG_QUERYACTION_PM || action == SqlUNetContract.ARG_QUERYACTION_PMROLE)
		{
			final ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.data_contents);

			// root node
			final TreeNode root = TreeNode.makeRoot();
			final TreeNode queryNode = TreeFactory.addTreeItemNode(root, hsb, R.drawable.predicatematrix, getActivity());

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

			// view mode
			final Settings.PMMode mode = Settings.PMMode.getPref(getActivity());

			// module
			Module module = (pointer instanceof PmRolePointer) ? new PredicateRoleModule(this, mode) : new PredicateRoleFromWordModule(this, mode);
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
