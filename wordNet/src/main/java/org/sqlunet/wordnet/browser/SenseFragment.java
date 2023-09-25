/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet.browser;

import org.sqlunet.browser.Module;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.wordnet.R;
import org.sqlunet.wordnet.loaders.SenseModule;

import androidx.annotation.NonNull;

/**
 * A fragment representing a sense
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SenseFragment extends SynsetFragment
{
	/**
	 * Constructor
	 */
	public SenseFragment()
	{
		super();
		this.headerId = R.string.wordnet_senses;
	}

	@NonNull
	@Override
	protected Module makeModule()
	{
		final SenseModule module = new SenseModule(this);
		module.setMaxRecursionLevel(this.maxRecursion);
		if (this.parameters != null)
		{
			module.setDisplayRelationNames(this.parameters.getBoolean(ProviderArgs.ARG_RENDER_DISPLAY_SEM_RELATION_NAME_KEY, true), this.parameters.getBoolean(ProviderArgs.ARG_RENDER_DISPLAY_LEX_RELATION_NAME_KEY, true));
		}
		return module;
	}
}
