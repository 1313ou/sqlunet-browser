/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.browser;

import org.sqlunet.browser.Module;
import org.sqlunet.wordnet.loaders.SenseModule;

import androidx.annotation.NonNull;

/**
 * A fragment representing a sense
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SenseFragment extends SynsetFragment
{
	// static private final String TAG = "SynsetF";

	/**
	 * Constructor
	 */
	public SenseFragment()
	{
		super();
	}

	@NonNull
	@Override
	protected Module makeModule()
	{
		final SenseModule module = new SenseModule(this);
		module.setMaxRecursionLevel(this.maxRecursion);
		return module;
	}
}
