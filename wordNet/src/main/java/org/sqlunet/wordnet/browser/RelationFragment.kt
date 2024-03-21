/*
 * Copyright (c) 2023. Bernard Bou
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
import org.sqlunet.wordnet.loaders.RelationModule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static org.sqlunet.provider.ProviderArgs.ARG_RENDER_DISPLAY_LEX_RELATION_NAME_KEY;
import static org.sqlunet.provider.ProviderArgs.ARG_RENDER_DISPLAY_SEM_RELATION_NAME_KEY;

/**
 * A fragment representing a relation tree.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class RelationFragment extends TreeFragment
{
	static private final String TAG = "RelationF";

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
	 * Parameters
	 */
	Bundle parameters;

	/**
	 * Constructor
	 */
	public RelationFragment()
	{
		this.expand = true;
		this.layoutId = R.layout.fragment_relation;
		this.treeContainerId = R.id.data_contents;
		this.headerId = R.string.wordnet_tree;
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
		this.parameters = args.containsKey(ProviderArgs.ARG_RENDERPARAMETERS) ? args.getBundle(ProviderArgs.ARG_RENDERPARAMETERS) : null;

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
			final Parcelable pointer = getPointer(args);

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
	protected Module makeModule()
	{
		final RelationModule module = new RelationModule(this);
		module.setMaxRecursionLevel(this.maxRecursion);
		if (this.parameters != null)
		{
			module.setDisplayRelationNames(this.parameters.getBoolean(ARG_RENDER_DISPLAY_SEM_RELATION_NAME_KEY, true), this.parameters.getBoolean(ARG_RENDER_DISPLAY_LEX_RELATION_NAME_KEY, true));
		}
		module.setExpand(this.expand);
		return module;
	}

	@Override
	protected boolean getScroll2D()
	{
		return true;
	}

	/**
	 * Set expand container
	 */
	public void setExpand(final boolean expand)
	{
		this.expand = expand;
	}
}
