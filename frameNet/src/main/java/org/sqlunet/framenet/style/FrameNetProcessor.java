package org.sqlunet.framenet.style;

import org.sqlunet.style.Preprocessor;

public class FrameNetProcessor extends Preprocessor
{
	// static final String TAG = "FrameNetProcessor"; //$NON-NLS-1$

	// processor

	private static final String[] replacers = { //
			"<ex>", "\n<ex>", // //$NON-NLS-1$ //$NON-NLS-2$
	};

	/**
	 * Constructor
	 */
	public FrameNetProcessor()
	{
		super(FrameNetProcessor.replacers);
	}

	public CharSequence[] split(final CharSequence text0)
	{
		CharSequence text = process(text0);
		return text.toString().split("\n");
	}
}
