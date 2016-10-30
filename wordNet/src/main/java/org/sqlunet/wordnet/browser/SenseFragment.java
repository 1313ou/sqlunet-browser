package org.sqlunet.wordnet.browser;

import org.sqlunet.wordnet.loaders.SenseModule;
import org.sqlunet.wordnet.loaders.SynsetModule;

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
	}

	@Override
	protected SynsetModule makeModule()
	{
		return new SenseModule(this);
	}
}
