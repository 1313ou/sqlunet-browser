/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.verbnet.browser;

import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.verbnet.R;
import org.sqlunet.verbnet.loaders.ClassModule;

import androidx.annotation.Nullable;

/**
 * A fragment representing a VerbNet class
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VnClassFragment extends TreeFragment
{
	// static private final String TAG = "VnClassF";

	/**
	 * Constructor
	 */
	public VnClassFragment()
	{
		this.layoutId = R.layout.fragment_vnclass;
		this.treeContainerId = R.id.data_contents;
		this.headerId = R.string.verbnet_classes;
		this.iconId = R.drawable.verbnet;
	}

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// query
		final Bundle args = getArguments();
		assert args != null;
		final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
		if (args.containsKey(ProviderArgs.ARG_QUERYPOINTER))
		{
			// pointer
			final Parcelable pointer = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU ? args.getParcelable(ProviderArgs.ARG_QUERYPOINTER, Parcelable.class) : args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);

			// root node
			final TreeNode queryNode = this.treeRoot.getChildren().iterator().next();

			// module
			final Module module = new ClassModule(this);
			module.init(type, pointer);
			module.process(queryNode);
		}
	}
}
