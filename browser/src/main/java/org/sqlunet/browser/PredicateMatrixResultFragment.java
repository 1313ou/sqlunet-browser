package org.sqlunet.browser;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sqlunet.predicatematrix.PmRolePointer;
import org.sqlunet.predicatematrix.loaders.PredicateRoleFromWordModule;
import org.sqlunet.predicatematrix.loaders.PredicateRoleModule;
import org.sqlunet.predicatematrix.settings.Settings;
import org.sqlunet.predicatematrix.style.PredicateMatrixFactories;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.control.TreeController;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;

/**
 * PredicateMatrix result fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PredicateMatrixResultFragment extends Fragment
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
	public PredicateMatrixResultFragment()
	{
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(R.layout.fragment_predicatematrix_result, container, false);

		// query
		final Bundle args = getArguments();
		final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
		final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);
		assert pointer != null;

		// query view
		final TextView queryView = (TextView) view.findViewById(R.id.queryView);
		queryView.setText(pointer.toString());

		// header
		final SpannableStringBuilder hsb = new SpannableStringBuilder();
		hsb.append("PredicateMatrix ");
		Spanner.append(hsb, pointer.toString(), 0, PredicateMatrixFactories.wordFactory);

		if (type == ProviderArgs.ARG_QUERYTYPE_PM || type == ProviderArgs.ARG_QUERYTYPE_PMROLE)
		{
			// container insert point
			final ViewGroup containerView = (ViewGroup) view.findViewById(R.id.data_contents);

			// root node
			final TreeNode root = TreeNode.makeRoot();
			final TreeNode queryNode = TreeFactory.addTreeNode(root, hsb, R.drawable.predicatematrix, getActivity());

			// tree
			this.treeView = new TreeView(getActivity(), root);
			this.treeView.setDefaultAnimation(true);
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

			// view mode
			final Settings.PMMode mode = Settings.PMMode.getPref(getActivity());

			// module
			Module module = (pointer instanceof PmRolePointer) ? new PredicateRoleModule(this, mode) : new PredicateRoleFromWordModule(this, mode);
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
