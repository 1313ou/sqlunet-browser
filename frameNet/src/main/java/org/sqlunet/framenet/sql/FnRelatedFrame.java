package org.sqlunet.framenet.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * Related frame
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnRelatedFrame
{
	/**
	 * Related frame id
	 */
	public final long frameId;

	/**
	 * Related frame name
	 */
	public final String frameName;

	/**
	 * Relation
	 */
	public final String relation;

	/**
	 * Constructor
	 *
	 * @param frameId   related frame id
	 * @param frameName related frame name
	 * @param relation  relation
	 */
	private FnRelatedFrame(final long frameId, final String frameName, final String relation)
	{
		this.frameId = frameId;
		this.frameName = frameName;
		this.relation = relation;
	}

	/**
	 * Make related frames from string
	 *
	 * @param relatedFramesString (id:rel|id:rel...)
	 * @return list of related frames
	 */
	public static List<FnRelatedFrame> make(final String relatedFramesString)
	{
		if (relatedFramesString == null)
		{
			return null;
		}
		List<FnRelatedFrame> result = null;
		final String[] relatedFrames = relatedFramesString.split("\\|"); //$NON-NLS-1$
		for (final String relatedFrame : relatedFrames)
		{
			if (result == null)
			{
				result = new ArrayList<>();
			}
			final String[] fields = relatedFrame.split(":"); //$NON-NLS-1$
			final long frameId = Long.parseLong(fields[0]);
			final String frameName = fields[1];
			final String relation = fields[2];
			result.add(new FnRelatedFrame(frameId, frameName, relation));
		}
		return result;
	}
}
