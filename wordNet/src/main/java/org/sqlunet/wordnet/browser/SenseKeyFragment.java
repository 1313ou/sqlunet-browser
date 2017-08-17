package org.sqlunet.wordnet.browser;

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

	@Override
	protected Module makeModule()
	{
		final SenseKeyModule module = new SenseKeyModule(this);
		module.setMaxRecursionLevel(this.maxRecursion);
		return module;
	}
}
