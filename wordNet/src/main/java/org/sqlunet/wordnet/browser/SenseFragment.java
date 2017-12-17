package org.sqlunet.wordnet.browser;

import android.support.annotation.NonNull;

import org.sqlunet.browser.Module;
import org.sqlunet.wordnet.loaders.SenseModule;

/**
 * A fragment representing a sense
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SenseFragment extends SynsetFragment
{
	// static private final String TAG = "SynsetFragment";

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
