package org.sqlunet.browser;

import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.predicatematrix.PmRolePointer;
import org.sqlunet.predicatematrix.loaders.PredicateRoleFromWordModule;
import org.sqlunet.predicatematrix.loaders.PredicateRoleModule;
import org.sqlunet.predicatematrix.settings.Settings;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.model.TreeNode;

/**
 * PredicateMatrix progressMessage fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PredicateMatrixResultFragment extends TreeFragment
{
	static private final String TAG = "PmResultF";

	/**
	 * Constructor
	 */
	public PredicateMatrixResultFragment()
	{
		this.layoutId = R.layout.fragment_predicatematrix_result;
		this.treeContainerId = org.sqlunet.bnc.R.id.data_contents;
		this.header = "PredicateMatrix";
		this.iconId = org.sqlunet.bnc.R.drawable.predicatematrix;
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// query
		final Bundle args = getArguments();
		final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
		final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);
		assert pointer != null;

		// view mode
		final Settings.PMMode mode = Settings.PMMode.getPref(getActivity());

		// root node
		final TreeNode root = this.treeView.getRoot();
		final TreeNode queryNode = root.getChildren().iterator().next();

		// module
		Module module = (pointer instanceof PmRolePointer) ? new PredicateRoleModule(this, mode) : new PredicateRoleFromWordModule(this, mode);
		module.init(type, pointer);
		module.process(queryNode);
	}
}
