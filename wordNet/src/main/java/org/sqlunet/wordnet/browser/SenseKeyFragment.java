/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet.browser;

import org.sqlunet.browser.Module;
import org.sqlunet.wordnet.R;
import org.sqlunet.wordnet.loaders.SenseKeyModule;

import androidx.annotation.NonNull;

import static org.sqlunet.provider.ProviderArgs.ARG_RENDER_DISPLAY_LEX_RELATION_NAME_KEY;
import static org.sqlunet.provider.ProviderArgs.ARG_RENDER_DISPLAY_SEM_RELATION_NAME_KEY;

/**
 * A fragment representing a sense (from sensekey)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SenseKeyFragment extends SynsetFragment
{
	/**
	 * Constructor
	 */
	public SenseKeyFragment()
	{
		super();
		this.headerId = R.string.wordnet_sensekeys;
	}

	@NonNull
	@Override
	protected Module makeModule()
	{
		final SenseKeyModule module = new SenseKeyModule(this);
		module.setMaxRecursionLevel(this.maxRecursion);
		if (this.parameters != null)
		{
			module.setDisplayRelationNames(this.parameters.getBoolean(ARG_RENDER_DISPLAY_SEM_RELATION_NAME_KEY, true), this.parameters.getBoolean(ARG_RENDER_DISPLAY_LEX_RELATION_NAME_KEY, true));
		}
		return module;
	}
}
