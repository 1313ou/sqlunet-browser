/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.predicatematrix.browser;

import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.predicatematrix.PmRolePointer;
import org.sqlunet.predicatematrix.R;
import org.sqlunet.predicatematrix.loaders.PredicateRoleFromWordModule;
import org.sqlunet.predicatematrix.loaders.PredicateRoleModule;
import org.sqlunet.predicatematrix.settings.Settings;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.Nullable;

/**
 * PredicateMatrix result fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PredicateMatrixFragment extends TreeFragment
{
	// static private final String TAG = "PredicateMatrixF";

	static public final String FRAGMENT_TAG = "predicatematrix";

	/**
	 * Constructor
	 */
	public PredicateMatrixFragment()
	{
		this.layoutId = R.layout.fragment_predicatematrix;
		this.treeContainerId = R.id.data_contents;
		this.headerId = R.string.predicatematrix_predicates;
		this.iconId = R.drawable.predicatematrix;
	}

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// query
		final Bundle args = getArguments();
		assert args != null;
		final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
		final Parcelable pointer = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU ? args.getParcelable(ProviderArgs.ARG_QUERYPOINTER, Parcelable.class) : args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);
		assert pointer != null;

		// view mode
		final Settings.PMMode mode = Settings.PMMode.getPref(requireContext());

		// root node
		final TreeNode queryNode = this.treeRoot.getChildren().iterator().next();

		// module
		Module module = (pointer instanceof PmRolePointer) ? new PredicateRoleModule(this, mode) : new PredicateRoleFromWordModule(this, mode);
		module.init(type, pointer);
		module.process(queryNode);
	}
}
