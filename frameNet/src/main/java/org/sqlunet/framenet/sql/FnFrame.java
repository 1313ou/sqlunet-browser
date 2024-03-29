/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * Frame
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnFrame
{
	/**
	 * Frame id
	 */
	public final long frameId;

	/**
	 * Frame name
	 */
	public final String frameName;

	/**
	 * Frame definition
	 */
	public final String frameDefinition;

	/**
	 * Semantic types
	 */
	@Nullable
	public final List<FnSemType> semTypes;

	/**
	 * Related frames
	 */
	@Nullable
	public final List<FnRelatedFrame> relatedFrames;

	/**
	 * Constructor
	 *
	 * @param frameId         frame id
	 * @param frameName       frame
	 * @param frameDefinition frame definition
	 * @param semTypes        semtypes
	 * @param relatedFrames   related frames
	 */
	FnFrame(final long frameId, final String frameName, final String frameDefinition, @Nullable final List<FnSemType> semTypes, @Nullable final List<FnRelatedFrame> relatedFrames)
	{
		this.frameId = frameId;
		this.frameName = frameName;
		this.frameDefinition = frameDefinition;
		this.semTypes = semTypes;
		this.relatedFrames = relatedFrames;
	}

	/**
	 * Frame factory
	 *
	 * @param connection connection
	 * @param frameId    target frame id
	 * @return frame
	 */
	@Nullable
	static public FnFrame make(final SQLiteDatabase connection, final long frameId)
	{
		try (FnFrameQuery query = new FnFrameQuery(connection, frameId))
		{
			query.execute();

			if (query.next())
			{
				final String frameName = query.getFrame();
				final String frameDescription = query.getFrameDescription();
				// final long frameId = query.getFrameId();
				final List<FnSemType> semTypes = FnSemType.make(query.getSemTypes());
				final List<FnRelatedFrame> relatedFrames = FnRelatedFrame.make(query.getRelatedFrames());
				return new FnFrame(frameId, frameName, frameDescription, semTypes, relatedFrames);
			}
		}
		return null;
	}
}
