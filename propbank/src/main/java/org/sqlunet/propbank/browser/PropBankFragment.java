/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.propbank.browser;

import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.HasXId;
import org.sqlunet.browser.Module;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.propbank.R;
import org.sqlunet.propbank.loaders.RoleSetFromWordModule;
import org.sqlunet.propbank.loaders.RoleSetModule;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.Nullable;

/**
 * A fragment representing a PropBank search
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PropBankFragment extends TreeFragment
{
	// static private final String TAG = "PropBankF";

	static public final String FRAGMENT_TAG = "propbank";

	/**
	 * Constructor
	 */
	public PropBankFragment()
	{
		this.layoutId = R.layout.fragment_propbank;
		this.treeContainerId = R.id.data_contents;
		this.headerId = R.string.propbank_rolesets;
		this.iconId = R.drawable.propbank;
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
			final Module module = (pointer instanceof HasXId) ? new RoleSetModule(this) : new RoleSetFromWordModule(this);
			module.init(type, pointer);
			module.process(queryNode);
		}
	}
}
