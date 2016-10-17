package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Frame
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
	 * @param frameId         is the frame id
	 * @param frameName       is the frame
	 * @param frameDefinition is the frame definition
	 * @param semTypes        are the semtypes
	 * @param relatedFrames   are the related frames
	 */
	FnFrame(final long frameId, final String frameName, final String frameDefinition, final List<FnSemType> semTypes, final List<FnRelatedFrame> relatedFrames)
	{
		this.frameId = frameId;
		this.frameName = frameName;
		this.frameDefinition = frameDefinition;
		this.semTypes = semTypes;
		this.relatedFrames = relatedFrames;
	}

	public static FnFrame make(final SQLiteDatabase connection, final long targetFrameId)
	{
		FnFrame result = null;
		FnFrameQueryCommand query = null;
		try
		{
			query = new FnFrameQueryCommand(connection, targetFrameId);
			query.execute();

			if (query.next())
			{
				final String frameName = query.getFrame();
				final String frameDescription = query.getFrameDescription();
				final long frameId = query.getFrameId();
				final List<FnSemType> semTypes = FnSemType.make(query.getSemTypes());
				final List<FnRelatedFrame> relatedFrames = FnRelatedFrame.make(query.getRelatedFrames());
				result = new FnFrame(frameId, frameName, frameDescription, semTypes, relatedFrames);
			}
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return result;
	}
}
