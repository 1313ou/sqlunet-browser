/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.browser;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.wordnet.R;
import org.sqlunet.wordnet.loaders.SynsetModule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A fragment representing a synset.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SynsetFragment extends TreeFragment
{
	static private final String TAG = "SynsetF";

	/**
	 * State of tree
	 */
	static private final String STATE_EXPAND = "state_expand";

	/**
	 * Whether to expandContainer
	 */
	private boolean expand;

	/**
	 * Max recursion level
	 */
	int maxRecursion;

	/**
	 * Constructor
	 */
	public SynsetFragment()
	{
		this.expand = true;
		this.layoutId = R.layout.fragment_sense;
		this.treeContainerId = R.id.data_contents;
		this.headerId = R.string.wordnet_synsets;
		this.iconId = R.drawable.wordnet;
	}

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// query
		final Bundle args = getArguments();
		assert args != null;
		final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
		this.maxRecursion = args.containsKey(ProviderArgs.ARG_QUERYRECURSE) ? args.getInt(ProviderArgs.ARG_QUERYRECURSE) : -1;

		// saved state
		if (savedInstanceState != null)
		{
			Log.d(TAG, "restore instance state " + this);
			this.expand = savedInstanceState.getBoolean(STATE_EXPAND);
		}

		// load
		if (args.containsKey(ProviderArgs.ARG_QUERYPOINTER))
		{
			// pointer
			final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);

			// root node
			final TreeNode queryNode = this.treeRoot.getChildren().iterator().next();

			// module
			final Module module = makeModule();
			module.init(type, pointer);
			module.process(queryNode);
		}
	}

	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		Log.d(TAG, "save instance state " + this);
		super.onSaveInstanceState(outState);
		outState.putBoolean(STATE_EXPAND, this.expand);
	}

	/**
	 * Module factory
	 *
	 * @return module
	 */
	@SuppressWarnings("WeakerAccess")
	@NonNull
	Module makeModule()
	{
		final SynsetModule module = new SynsetModule(this);
		module.setMaxRecursionLevel(this.maxRecursion);
		module.setExpand(this.expand);
		return module;
	}

	/**
	 * Set expandContainer
	 */
	public void setExpand(final boolean expand)
	{
		this.expand = expand;
	}
}
