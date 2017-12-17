package org.sqlunet.wordnet.browser;

import android.support.annotation.NonNull;

import org.sqlunet.browser.Module;
import org.sqlunet.wordnet.loaders.SenseKeyModule;

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
