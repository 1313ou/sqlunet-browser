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
	/**
	 * Lex unit id
	 */
	public final long luId;

	/**
	 * Lex unit string
	 */
	public final String lexUnit;

	/**
	 * Pos
	 */
	@SuppressWarnings("unused")
	public final String pos;

	/**
	 * Definition
	 */
	public final String definition;

	/**
	 * Source dictionary
	 */
	@SuppressWarnings("unused")
	public final String dictionary;

	/**
	 * Buddy frame
	 */
	public final FnFrame frame;

	/**
	 * Incorporated frame element
	 */
	@SuppressWarnings("unused")
	public final String incorporatedFe;

	/**
	 * Constructor
	 *
	 * @param luId            lex unit id
	 * @param lexUnit         lex unit string
	 * @param pos             pos
	 * @param definition      definition
	 * @param dictionary      definition dictionary
	 * @param incorporatedFe  incorporated frame element
	 * @param frameId         frame id
	 * @param frame           frame
	 * @param frameDefinition frame definition
	 */
	private FnLexUnit(final long luId, final String lexUnit, final String pos, final String definition, final String dictionary, final String incorporatedFe, final long frameId, final String frame, final String frameDefinition)
	{
		super();
		this.luId = luId;
		this.lexUnit = lexUnit;
		this.pos = pos;
		this.definition = definition;
		this.dictionary = dictionary;
		this.incorporatedFe = incorporatedFe;
		this.frame = frameId == 0 ? null : new FnFrame(frameId, frame, frameDefinition, null, null);
	}

	/**
	 * Lex unit from lex unit id
	 *
	 * @param connection connection
	 * @param luId       lex unit id
	 * @return lex units
	 */
	public static FnLexUnit makeFromId(final SQLiteDatabase connection, final long luId)
	{
		FnLexUnit result = null;
		FnLexUnitQuery query = null;
		try
		{
			query = new FnLexUnitQuery(connection, luId);
			query.execute();

			if (query.next())
			{
				// final long luId = query.getLuId();
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

	/**
	 * Make pairs of (word id-lex units) from query built from word
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return paris of (word id-list of lex units)
	 */
	static public Pair<Long, List<FnLexUnit>> makeFromWord(final SQLiteDatabase connection, final String word)
	{
		final List<FnLexUnit> result = new ArrayList<>();
		FnLexUnitQueryFromWord query = null;
		try
		{
			query = new FnLexUnitQueryFromWord(connection, word);
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
	 * Make list of lex units from query built from word id
	 *
	 * @param connection connection
	 * @param wordId     word id to build query from
	 * @param pos        pos to build query from, null if any
	 * @return list of lex units
	 */
	static public List<FnLexUnit> makeFromWordId(final SQLiteDatabase connection, final long wordId, final Character pos)
	{
		final List<FnLexUnit> result = new ArrayList<>();
		FnLexUnitQueryFromWordId query = null;
		try
		{
			query = new FnLexUnitQueryFromWordId(connection, wordId, pos);
			query.execute();

			while (query.next())
			{
				final long luId = query.getLuId();
				final String lexUnit = query.getLexUnit();
				final String luPos = query.getPos();
				final String definition = query.getDefinition();
				final String dictionary = query.getDictionary();
				final String incorporatedFe = query.getIncorporatedFe();
				final long frameId = query.getFrameId();
				final String frame = query.getFrame();
				final String frameDescription = query.getFrameDescription();

				result.add(new FnLexUnit(luId, lexUnit, luPos, definition, dictionary, incorporatedFe, frameId, frame, frameDescription));
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

	/**
	 * Make list of lex units from query built from frame id
	 *
	 * @param connection connection
	 * @param frameId    frame id
	 * @return list of lex units
	 */
	public static List<FnLexUnit> makeFromFrame(final SQLiteDatabase connection, final long frameId)
	{
		final List<FnLexUnit> result = new ArrayList<>();
		FnLexUnitQueryFromFrameId query = null;
		try
		{
			query = new FnLexUnitQueryFromFrameId(connection, frameId);
			query.execute();

			while (query.next())
			{
				final long luId = query.getLuId();
				final String lexUnit = query.getLexUnit();
				final String pos = query.getPos();
				final String definition = query.getDefinition();
				final String dictionary = query.getDictionary();
				final String incorporatedFe = query.getIncorporatedFe();
				result.add(new FnLexUnit(luId, lexUnit, pos, definition, dictionary, incorporatedFe, 0, null, null));
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
