package org.sqlunet.framenet.style;

import org.sqlunet.style.Preprocessor;

/**
 * FrameNet frame preprocessor
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameNetFrameProcessor extends Preprocessor
{
	// static final String TAG = "FrameNetProcessor"; //$NON-NLS-1$

	/**
	 * Replacers for preprocessor
	 */
	private static final String[] replacers = { //
			"<fex name=[\"\']([^\"\']+)[\"\']>([^<]*)</fex>", "<fex>$2</fex> <xfen>[$1]</xfen>",}; //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * Constructor
	 */
	public FrameNetFrameProcessor()
	{
		super(FrameNetFrameProcessor.replacers);
	}
}
