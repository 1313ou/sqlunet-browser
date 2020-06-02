/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.verbnet.browser;

import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.HasXId;
import org.sqlunet.browser.Module;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.verbnet.R;
import org.sqlunet.verbnet.loaders.ClassFromWordModule;
import org.sqlunet.verbnet.loaders.ClassModule;

import androidx.annotation.Nullable;

/**
 * A fragment representing a VerbNet search
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VerbNetFragment extends TreeFragment
{
	// static private final String TAG = "VerbNetF";

	/**
	 * Constructor
	 */
	public VerbNetFragment()
	{
		this.layoutId = R.layout.fragment_verbnet;
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
			final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);

			// root node
			final TreeNode queryNode = this.treeRoot.getChildren().iterator().next();

			// module
			final Module module = pointer instanceof HasXId ? new ClassModule(this) : new ClassFromWordModule(this);
			module.init(type, pointer);
			module.process(queryNode);
		}
	}
}
