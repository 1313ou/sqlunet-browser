package org.sqlunet.framenet.style;

import org.sqlunet.style.Preprocessor;

public class FrameNetFrameProcessor extends Preprocessor
{
	// --Commented out by Inspection (10/15/16 8:11 PM):static final String TAG = "FrameNetProcessor"; //$NON-NLS-1$

	// processor

	private static final String[] replacers = { //
		"<fex name=[\"\']([^\"\']+)[\"\']>([^<]*)</fex>", "<fex>$2</fex> <xfen>[$1]</xfen>", }; //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * Constructor
	 */
	public FrameNetFrameProcessor()
	{
		super(FrameNetFrameProcessor.replacers);
	}
}
