/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.framenet.browser;

import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.framenet.R;
import org.sqlunet.framenet.loaders.AnnoSetFromPatternModule;
import org.sqlunet.framenet.loaders.AnnoSetFromValenceUnitModule;
import org.sqlunet.framenet.loaders.AnnoSetModule;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.Nullable;

/**
 * A fragment representing an annoSet.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnAnnoSetFragment extends TreeFragment
{
	// static private final String TAG = "FnAnnoSetF";

	/**
	 * Constructor
	 */
	public FnAnnoSetFragment()
	{
		this.layoutId = R.layout.fragment_fnannoset;
		this.treeContainerId = R.id.data_contents;
		this.iconId = R.drawable.annoset;
		this.headerId = R.string.framenet_annosets;

		// header
		final Bundle args = getArguments();
		if (args != null)
		{
			final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
			switch (type)
			{
				case ProviderArgs.ARG_QUERYTYPE_FNPATTERN:
					this.headerId =  R.string.framenet_annosets_for_pattern;
					break;
				case ProviderArgs.ARG_QUERYTYPE_FNVALENCEUNIT:
					this.headerId =  R.string.framenet_annosets_for_valenceunit;
					break;
				default:
					break;
			}
		}
	}

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// query
		final Bundle args = getArguments();
		assert args != null;
		if (args.containsKey(ProviderArgs.ARG_QUERYPOINTER))
		{
			// pointer
			final Parcelable pointer = getPointer(args);
			final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);

			// root node
			final TreeNode queryNode = this.treeRoot.getChildren().iterator().next();

			// module
			Module module;
			switch (type)
			{
				case ProviderArgs.ARG_QUERYTYPE_FNANNOSET:
					module = new AnnoSetModule(this);
					break;
				case ProviderArgs.ARG_QUERYTYPE_FNPATTERN:
					module = new AnnoSetFromPatternModule(this);
					break;
				case ProviderArgs.ARG_QUERYTYPE_FNVALENCEUNIT:
					module = new AnnoSetFromValenceUnitModule(this);
					break;
				default:
					return;
			}
			module.init(type, pointer);
			module.process(queryNode);
		}
	}
}
