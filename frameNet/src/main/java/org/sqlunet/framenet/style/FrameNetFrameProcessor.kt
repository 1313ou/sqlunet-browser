/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.framenet.style;

import org.sqlunet.style.Preprocessor;

/**
 * FrameNet frame preprocessor
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameNetFrameProcessor extends Preprocessor
{
	// static final String TAG = "FrameNetProcessor";
	/**
	 * Replacers for preprocessor
	 */
	static private final String[] replacers = { //
			"<fex name=[\"']([^\"']+)[\"']>([^<]*)</fex>", "<fex>$2</fex> <xfen>[$1]</xfen>",};

	/**
	 * Constructor
	 */
	public FrameNetFrameProcessor()
	{
		super(FrameNetFrameProcessor.replacers);
	}
}
