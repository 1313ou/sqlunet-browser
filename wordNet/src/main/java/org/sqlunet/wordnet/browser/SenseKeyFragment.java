/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.browser;

import org.sqlunet.browser.Module;
import org.sqlunet.wordnet.R;
import org.sqlunet.wordnet.loaders.SenseKeyModule;

import androidx.annotation.NonNull;

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
		return module;
	}
}
