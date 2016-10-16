package org.sqlunet.verbnet.sql;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author bbou
 *
 */
/**
 * Frames attached to a VerbNet class
 *
 * @author Bernard Bou
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
	 * @param frames
	 *            is the list of frames
	 */
	private VnFrameSet(final List<VnFrame> frames)
	{
		this.frames = frames;
	}

	/**
	 * Make a verbnet frame set from query built from classid, wordid and synsetid
	 *
	 * @param connection
	 *            is the database connection
	 * @param classId
	 *            is the class id to build query from
	 * @param wordId
	 *            is the word id to build query from
	 * @param synsetId
	 *            is the synset id to build query from (null for any)
	 * @return list of VerbNet frame sets
	 */
	static public VnFrameSet make(final SQLiteDatabase connection, final long classId, final long wordId, final Long synsetId)
	{
		VnFrameQueryFromSenseCommand query = null;
		VnFrameSet thisFrameSet = null;

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
				if (thisFrameSet == null)
				{
					thisFrameSet = new VnFrameSet(new ArrayList<VnFrame>());
				}

				// if same class, add role to frame set
				thisFrameSet.frames.add(frame);
			}
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return thisFrameSet;
	}

	public static VnFrameSet make(final SQLiteDatabase connection, final long classId)
	{
		VnFrameQueryCommand thisQuery = null;
		VnFrameSet thisFrameSet = null;

		try
		{
			thisQuery = new VnFrameQueryCommand(connection, classId);
			thisQuery.execute();

			while (thisQuery.next())
			{
				// data from resultset
				// final long thisFrameId = thisQuery.getFrameId();
				final String thisNumber = thisQuery.getNumber();
				final String thisXTag = thisQuery.getXTag();
				final String thisDescription1 = thisQuery.getDescription1();
				final String thisDescription2 = thisQuery.getDescription2();
				final String thisSyntax = thisQuery.getSyntax();

				final String thisSemantics = thisQuery.getSemantics();

				final String thisExampleConcat = thisQuery.getExamples();
				final String[] theseExamples = thisExampleConcat.split("\\|"); //$NON-NLS-1$

				// frame
				final VnFrame thisFrame = new VnFrame(thisNumber, thisXTag, thisDescription1, thisDescription2, thisSyntax, thisSemantics, theseExamples);

				// allocate
				if (thisFrameSet == null)
				{
					thisFrameSet = new VnFrameSet(new ArrayList<VnFrame>());
				}

				// if same class, add role to frame set
				thisFrameSet.frames.add(thisFrame);
			}
		}
		finally
		{
			if (thisQuery != null)
			{
				thisQuery.release();
			}
		}
		return thisFrameSet;
	}
}
