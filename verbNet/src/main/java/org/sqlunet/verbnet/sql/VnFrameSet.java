package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bbou
 */

/**
 * Frames attached to a VerbNet class
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VnFrameSet
{
	/**
	 * Frames
	 */
	public final List<VnFrame> frames;

	/**
	 * Constructor
	 *
	 * @param frames is the list of frames
	 */
	private VnFrameSet(final List<VnFrame> frames)
	{
		this.frames = frames;
	}

	/**
	 * Make a verbnet frame set from query built from class id, word id and synset id
	 *
	 * @param connection is the database connection
	 * @param classId    is the class id to build query from
	 * @param wordId     is the word id to build query from
	 * @param synsetId   is the synset id to build query from (null for any)
	 * @return list of VerbNet frame sets
	 */
	static public VnFrameSet make(final SQLiteDatabase connection, final long classId, final long wordId, final Long synsetId)
	{
		VnFrameQueryFromSenseCommand query = null;
		VnFrameSet frameSet = null;

		try
		{
			query = new VnFrameQueryFromSenseCommand(connection, classId, wordId, synsetId);
			query.execute();

			while (query.next())
			{
				// data from resultset
				// final long frameId = query.getFrameId();
				final String number = query.getNumber();
				final String xTag = query.getXTag();
				final String description1 = query.getDescription1();
				final String description2 = query.getDescription2();
				final String syntax = query.getSyntax();
				final String semantics = query.getSemantics();
				final String concatExamples = query.getExample();
				final String[] examples = concatExamples.split("\\|"); //$NON-NLS-1$
				// final float quality = query.getQuality();
				// final boolean synsetSpecific = query.getSynsetSpecific();

				// frame
				final VnFrame frame = new VnFrame(number, xTag, description1, description2, syntax, semantics, examples);

				// allocate
				if (frameSet == null)
				{
					frameSet = new VnFrameSet(new ArrayList<VnFrame>());
				}

				// if same class, add role to frame set
				frameSet.frames.add(frame);
			}
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return frameSet;
	}

	public static VnFrameSet make(final SQLiteDatabase connection, final long classId)
	{
		VnFrameQueryCommand query = null;
		VnFrameSet frameSet = null;

		try
		{
			query = new VnFrameQueryCommand(connection, classId);
			query.execute();

			while (query.next())
			{
				// data from resultset
				// final long frameId = query.getFrameId();
				final String number = query.getNumber();
				final String xTag = query.getXTag();
				final String description1 = query.getDescription1();
				final String description2 = query.getDescription2();
				final String syntax = query.getSyntax();
				final String semantics = query.getSemantics();
				final String exampleConcat = query.getExamples();
				final String[] examples = exampleConcat.split("\\|"); //$NON-NLS-1$

				// frame
				final VnFrame frame = new VnFrame(number, xTag, description1, description2, syntax, semantics, examples);

				// allocate
				if (frameSet == null)
				{
					frameSet = new VnFrameSet(new ArrayList<VnFrame>());
				}

				// if same class, add role to frame set
				frameSet.frames.add(frame);
			}
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return frameSet;
	}
}
