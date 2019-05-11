package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

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
	 * @param frames list of frames
	 */
	private VnFrameSet(final List<VnFrame> frames)
	{
		this.frames = frames;
	}

	/**
	 * Make a set of frames from query built from class id, word id and synset id
	 *
	 * @param connection connection
	 * @param classId    class id to build query from
	 * @param wordId     word id to build query from
	 * @param synsetId   synset id to build query from (null for any)
	 * @return set of frames
	 */
	@Nullable
	static public VnFrameSet make(final SQLiteDatabase connection, final long classId, final long wordId, final Long synsetId)
	{
		VnFrameQueryFromClassIdAndSense query = null;
		VnFrameSet frameSet = null;

		try
		{
			query = new VnFrameQueryFromClassIdAndSense(connection, classId, wordId, synsetId);
			query.execute();

			while (query.next())
			{
				// data from result set
				// final long frameId = query.getFrameId();
				final String number = query.getNumber();
				final String xTag = query.getXTag();
				final String description1 = query.getDescription1();
				final String description2 = query.getDescription2();
				final String syntax = query.getSyntax();
				final String semantics = query.getSemantics();
				final String concatExamples = query.getExample();
				final String[] examples = concatExamples.split("\\|");
				// final float quality = query.getQuality();
				// final boolean synsetSpecific = query.getSynsetSpecific();

				// frame
				final VnFrame frame = new VnFrame(number, xTag, description1, description2, syntax, semantics, examples);

				// allocate
				if (frameSet == null)
				{
					frameSet = new VnFrameSet(new ArrayList<>());
				}

				// if same class, addItem role to frame set
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

	/**
	 * Make frame set for class from query built from class id
	 *
	 * @param connection connection
	 * @param classId    class id
	 * @return set of frames
	 */
	@Nullable
	static public VnFrameSet make(final SQLiteDatabase connection, final long classId)
	{
		VnFrameQueryFromClassId query = null;
		VnFrameSet frameSet = null;

		try
		{
			query = new VnFrameQueryFromClassId(connection, classId);
			query.execute();

			while (query.next())
			{
				// data from result set
				// final long frameId = query.getFrameId();
				final String number = query.getNumber();
				final String xTag = query.getXTag();
				final String description1 = query.getDescription1();
				final String description2 = query.getDescription2();
				final String syntax = query.getSyntax();
				final String semantics = query.getSemantics();
				final String exampleConcat = query.getExamples();
				final String[] examples = exampleConcat.split("\\|");
				// frame
				final VnFrame frame = new VnFrame(number, xTag, description1, description2, syntax, semantics, examples);

				// allocate
				if (frameSet == null)
				{
					frameSet = new VnFrameSet(new ArrayList<>());
				}

				// if same class, addItem role to frame set
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
