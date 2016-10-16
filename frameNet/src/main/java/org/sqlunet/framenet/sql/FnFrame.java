package org.sqlunet.framenet.sql;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author bbou
 *
 */
/**
 * FrameQuery
 *
 * @author Bernard Bou
 */
public class FnFrame
{
	public final long frameId;

	public final String frameName;

	public final String frameDefinition;

	public final List<FnSemType> semTypes;

	public final List<FnRelatedFrame> relatedFrames;

	/**
	 * Constructor
	 *
	 * @param thisFrameId
	 *            is the frame id
	 * @param thisFrameName
	 *            is the frame
	 * @param thisFrameDefinition
	 *            is the frame definition
	 * @param theseSemTypes
	 *            are the semtypes
	 * @param theseRelatedFrames
	 *            are the related frames
	 */
	FnFrame(final long thisFrameId, final String thisFrameName, final String thisFrameDefinition, final List<FnSemType> theseSemTypes, final List<FnRelatedFrame> theseRelatedFrames)
	{
		this.frameId = thisFrameId;
		this.frameName = thisFrameName;
		this.frameDefinition = thisFrameDefinition;
		this.semTypes = theseSemTypes;
		this.relatedFrames = theseRelatedFrames;
	}

	public static FnFrame make(final SQLiteDatabase thisConnection, final long thisTargetFrameId)
	{
		FnFrame thisResult = null;
		FnFrameQueryCommand thisQuery = null;
		try
		{
			thisQuery = new FnFrameQueryCommand(thisConnection, thisTargetFrameId);
			thisQuery.execute();

			if (thisQuery.next())
			{
				final String thisFrameName = thisQuery.getFrame();
				final String thisFrameDescription = thisQuery.getFrameDescription();
				final long thisFrameId = thisQuery.getFrameId();
				final List<FnSemType> theseSemTypes = FnSemType.make(thisQuery.getSemTypes());
				final List<FnRelatedFrame> theseRelatedFrames = FnRelatedFrame.make(thisQuery.getRelatedFrames());
				thisResult = new FnFrame(thisFrameId, thisFrameName, thisFrameDescription, theseSemTypes, theseRelatedFrames);
			}
		}
		finally
		{
			if (thisQuery != null)
			{
				thisQuery.release();
			}
		}
		return thisResult;
	}
}
