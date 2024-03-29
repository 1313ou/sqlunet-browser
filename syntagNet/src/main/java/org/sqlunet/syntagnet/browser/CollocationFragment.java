/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.syntagnet.browser;

import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.syntagnet.R;
import org.sqlunet.syntagnet.SnCollocationPointer;
import org.sqlunet.syntagnet.loaders.CollocationModule;
import org.sqlunet.syntagnet.loaders.CollocationsModule;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.Nullable;

/**
 * A fragment representing a SyntagNet collocation
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class CollocationFragment extends TreeFragment
{
	// static private final String TAG = "CollocationF";

	/**
	 * Constructor
	 */
	public CollocationFragment()
	{
		this.layoutId = R.layout.fragment_collocation;
		this.treeContainerId = R.id.data_contents;
		this.headerId = R.string.syntagnet_collocations;
		this.iconId = R.drawable.syntagnet;
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
			final Module module = (pointer instanceof SnCollocationPointer) ? new CollocationModule(this) : new CollocationsModule(this);
			//final Module module = type == ProviderArgs.ARG_QUERYTYPE_COLLOCATION ? new CollocationModule(this) : new CollocationsModule(this);
			module.init(type, pointer);
			module.process(queryNode);
		}
	}
}
