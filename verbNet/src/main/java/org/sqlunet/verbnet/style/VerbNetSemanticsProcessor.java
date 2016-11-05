package org.sqlunet.verbnet.style;

import org.sqlunet.style.Preprocessor;

/**
 * VerbNet preprocessor
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VerbNetSemanticsProcessor extends Preprocessor
{
	// static final String TAG = "VerbNetSemanticsProcessor";
	/**
	 * Replacers
	 */
	private static final String[] replacers = { //
			// "([^\\(\n]*)\\((.*)\\)","<pred>$1</pred> ($2)",
			// "(event:E|(?:start|end|result)\\(E\\))","<event>$1</event>"
	};

	/**
	 * Constructor
	 */
	public VerbNetSemanticsProcessor()
	{
		super(VerbNetSemanticsProcessor.replacers);
	}
}
