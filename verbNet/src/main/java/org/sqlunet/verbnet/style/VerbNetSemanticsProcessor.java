package org.sqlunet.verbnet.style;

import org.sqlunet.style.Preprocessor;

public class VerbNetSemanticsProcessor extends Preprocessor
{
	// static final String TAG = "FrameNetProcessor"; //$NON-NLS-1$

	// processor

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
