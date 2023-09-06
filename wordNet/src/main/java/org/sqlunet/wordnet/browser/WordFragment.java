/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet.browser;

import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.browser.TreeFragment;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.wordnet.R;
import org.sqlunet.wordnet.loaders.WordModule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A fragment representing a synset.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WordFragment extends TreeFragment
{
	// static private final String TAG = "WordF";

	/**
	 * Constructor
	 */
	public WordFragment()
	{
		this.layoutId = R.layout.fragment_word;
		this.treeContainerId = R.id.data_contents;
		this.headerId = R.string.wordnet_words;
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
		if (args.containsKey(ProviderArgs.ARG_QUERYPOINTER))
		{
			// pointer
			final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);

			// root node
			final TreeNode queryNode = this.treeRoot.getChildren().iterator().next();

			// module
			final WordModule module = makeModule();
			module.init(type, pointer);
			module.process(queryNode);
		}
	}

	/**
	 * Module factory
	 *
	 * @return module
	 */
	@NonNull
	private WordModule makeModule()
	{
		return new WordModule(this);
	}
}
