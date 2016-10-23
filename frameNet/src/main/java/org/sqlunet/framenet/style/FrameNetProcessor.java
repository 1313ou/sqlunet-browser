package org.sqlunet.framenet.style;

import org.sqlunet.style.Preprocessor;

/**
 * FrameNet pre processor
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameNetProcessor extends Preprocessor
{
	// static final String TAG = "FrameNetProcessor"; //

	/**
	 * Replacers for preprocessor
	 */
	private static final String[] replacers = { //
			"<ex>", "\n<ex>", //
	};

	/**
	 * Constructor
	 */
	public FrameNetProcessor()
	{
		super(FrameNetProcessor.replacers);
	}

	/**
	 * Split utility
	 *
	 * @param text text to split
	 * @return split text
	 */
	public CharSequence[] split(final CharSequence text)
	{
		CharSequence processedText = process(text);
		return processedText.toString().split("\n");
	}
}
