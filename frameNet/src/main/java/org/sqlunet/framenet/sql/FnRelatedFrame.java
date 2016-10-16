package org.sqlunet.framenet.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * SemType
 *
 * @author Bernard Bou
 */
class FnRelatedFrame
{
	public final long frameId;

	public final String frameName;

	public final String relation;

	/**
	 * Constructor
	 *
	 * @param thisFrameId
	 *            is the related frame id
	 * @param thisFrameName
	 *            is the related frame name
	 * @param thisRelation
	 *            is the relation
	 */
	private FnRelatedFrame(final long thisFrameId, final String thisFrameName, final String thisRelation)
	{
		this.frameId = thisFrameId;
		this.frameName = thisFrameName;
		this.relation = thisRelation;
	}

	/**
	 * Make related frames from string
	 *
	 * @param theseRelatedFramesString
	 *            (id:rel|id:rel...)
	 * @return list of related frames
	 */
	public static List<FnRelatedFrame> make(final String theseRelatedFramesString)
	{
		if (theseRelatedFramesString == null)
			return null;
		List<FnRelatedFrame> thisResult = null;
		final String[] theseRelatedFrames = theseRelatedFramesString.split("\\|"); //$NON-NLS-1$
		for (final String thisRelatedFrame : theseRelatedFrames)
		{
			if (thisResult == null)
			{
				thisResult = new ArrayList<>();
			}
			final String[] theseFields = thisRelatedFrame.split(":"); //$NON-NLS-1$
			final long thisFrameId = Long.parseLong(theseFields[0]);
			final String thisFrameName = theseFields[1];
			final String thisRelation = theseFields[2];
			thisResult.add(new FnRelatedFrame(thisFrameId, thisFrameName, thisRelation));
		}
		return thisResult;
	}
}
