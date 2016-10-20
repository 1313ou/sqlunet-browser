package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Lex unit
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnLexUnit
{
	public final long luId;

	public final String lexUnit;

	@SuppressWarnings("unused")
	public final String pos;

	public final String definition;

	@SuppressWarnings("unused")
	public final String dictionary;

	public final FnFrame frame;

	@SuppressWarnings("unused")
	public final String incorporatedFe;

	private FnLexUnit(final long luId, final String lexUnit, final String pos, final String definition, final String dictionary, final String incorporatedFe, final long frameId, final String frame, final String frameDefinition)
	{
		super();
		this.luId = luId;
		this.lexUnit = lexUnit;
		this.pos = pos;
		this.definition = definition;
		this.dictionary = dictionary;
		this.incorporatedFe = incorporatedFe;
		this.frame = frameId == -1 ? null : new FnFrame(frameId, frame, frameDefinition, null, null);
	}

	/**
	 * Make sets of lexunits from query built from word
	 *
	 * @param connection is the database connection
	 * @param targetWord is the word to build query from
	 * @return word id + list of lexunits
	 */
	static public Pair<Long, List<FnLexUnit>> makeFromWord(final SQLiteDatabase connection, final String targetWord)
	{
		final List<FnLexUnit> result = new ArrayList<>();
		FnLexUnitQueryCommandFromWord query = null;
		try
		{
			query = new FnLexUnitQueryCommandFromWord(connection, targetWord);
			query.execute();

			long wordId = 0;
			while (query.next())
			{
				wordId = query.getWordId();

				final long luId = query.getLuId();
				final String lexUnit = query.getLexUnit();
				final String pos = query.getPos();
				final String definition = query.getLexUnitDefinition();
				final String dictionary = query.getLexUnitDictionary();
				final String incorporatedFe = query.getIncoporatedFe();
				final long frameId = query.getFrameId();
				final String frame = query.getFrame();
				final String frameDefinition = query.getFrameDefinition();

				result.add(new FnLexUnit(luId, lexUnit, pos, definition, dictionary, incorporatedFe, frameId, frame, frameDefinition));
			}
			return new Pair<>(wordId, result);
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
	}

	/**
	 * Make sets of lexunits from query built from word id
	 *
	 * @param connection   is the database connection
	 * @param targetWordId is the word id to build query from
	 * @param targetPos    is the pos to build query from, null if any
	 * @return list of lexunits
	 */
	static public List<FnLexUnit> makeFromWordId(final SQLiteDatabase connection, final long targetWordId, final Character targetPos)
	{
		final List<FnLexUnit> result = new ArrayList<>();
		FnWordLexUnitQueryCommand query = null;
		try
		{
			query = new FnWordLexUnitQueryCommand(connection, targetWordId, targetPos);
			query.execute();

			while (query.next())
			{
				final long luId = query.getLuId();
				final String lexUnit = query.getLexUnit();
				final String pos = query.getPos();
				final String definition = query.getDefinition();
				final String dictionary = query.getDictionary();
				final String incorporatedFe = query.getIncorporatedFe();
				final long frameId = query.getFrameId();
				final String frame = query.getFrame();
				final String frameDescription = query.getFrameDescription();

				result.add(new FnLexUnit(luId, lexUnit, pos, definition, dictionary, incorporatedFe, frameId, frame, frameDescription));
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

	public static List<FnLexUnit> makeFromFrame(final SQLiteDatabase connection, final long targetFrameId)
	{
		final List<FnLexUnit> result = new ArrayList<>();
		FnFrameLexUnitQueryCommand query = null;
		try
		{
			query = new FnFrameLexUnitQueryCommand(connection, targetFrameId);
			query.execute();

			while (query.next())
			{
				final long luId = query.getLuId();
				final String lexUnit = query.getLexUnit();
				final String pos = query.getPos();
				final String definition = query.getDefinition();
				final String dictionary = query.getDictionary();
				final String incorporatedFe = query.getIncorporatedFe();
				result.add(new FnLexUnit(luId, lexUnit, pos, definition, dictionary, incorporatedFe, -1, null, null));
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

	public static FnLexUnit makeFromId(final SQLiteDatabase connection, final long targetLuId)
	{
		FnLexUnit result = null;
		FnLexUnitQueryCommand query = null;
		try
		{
			query = new FnLexUnitQueryCommand(connection, targetLuId);
			query.execute();

			if (query.next())
			{
				final long luId = query.getLuId();
				final String lexUnit = query.getLexUnit();
				final String pos = query.getPos();
				final String definition = query.getDefinition();
				final String dictionary = query.getDictionary();
				final String incorporatedFe = query.getIncorporatedFe();
				final long frameId = query.getFrameId();
				final String frame = query.getFrame();
				final String frameDescription = query.getFrameDescription();
				result = new FnLexUnit(luId, lexUnit, pos, definition, dictionary, incorporatedFe, frameId, frame, frameDescription);
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
