package org.sqlunet.wordnet.browser;

import org.sqlunet.browser.Module;
import org.sqlunet.wordnet.loaders.SenseModule;

/**
 * A fragment representing a sense
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SenseFragment extends SynsetFragment
{
	static private final String TAG = "SenseFragment";

	/**
	 * Constructor
	 */
	public SenseFragment()
	{
		super();
	}

	@Override
	protected Module makeModule()
	{
		return new SenseModule(this);
	}
}
