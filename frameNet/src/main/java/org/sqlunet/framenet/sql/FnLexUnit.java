/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
	public final String pos;

	/**
	 * Definition
	 */
	public final String definition;

	/**
	 * Source dictionary
	 */
	public final String dictionary;

	/**
	 * Buddy frame
	 */
	@Nullable
	public final FnFrame frame;

	/**
	 * Incorporated frame element
	 */
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
	@Nullable
	static public FnLexUnit makeFromId(final SQLiteDatabase connection, final long luId)
	{
		try (FnLexUnitQuery query = new FnLexUnitQuery(connection, luId))
		{
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
				return new FnLexUnit(luId, lexUnit, pos, definition, dictionary, incorporatedFe, frameId, frame, frameDescription);
			}
		}
		return null;
	}

	/**
	 * Make pairs of (word id-lex units) from query built from word
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return paris of (word id-list of lex units)
	 */
	@NonNull
	static public Pair<Long, List<FnLexUnit>> makeFromWord(final SQLiteDatabase connection, final String word)
	{
		List<FnLexUnit> result = null;
		try (FnLexUnitQueryFromWord query = new FnLexUnitQueryFromWord(connection, false, word))
		{
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
				final String incorporatedFe = query.getIncorporatedFe();
				final long frameId = query.getFrameId();
				final String frame = query.getFrame();
				final String frameDefinition = query.getFrameDefinition();
				if (result == null)
				{
					result = new ArrayList<>();
				}
				result.add(new FnLexUnit(luId, lexUnit, pos, definition, dictionary, incorporatedFe, frameId, frame, frameDefinition));
			}
			return new Pair<>(wordId, result);
		}
	}

	/**
	 * Make pairs of (word id-lex units) from query built from fn word
	 *
	 * @param connection connection
	 * @param fnWord     target fn word
	 * @return pairs of (fn word id-list of lex units)
	 */
	@NonNull
	static public Pair<Long, List<FnLexUnit>> makeFromFnWord(final SQLiteDatabase connection, final String fnWord)
	{
		List<FnLexUnit> result = null;
		try (FnLexUnitQueryFromFnWord query = new FnLexUnitQueryFromFnWord(connection, true, fnWord))
		{
			query.execute();

			long fnWordId = 0;
			while (query.next())
			{
				fnWordId = query.getFnWordId();

				final long luId = query.getLuId();
				final String lexUnit = query.getLexUnit();
				final String pos = query.getPos();
				final String definition = query.getLexUnitDefinition();
				final String dictionary = query.getLexUnitDictionary();
				final String incorporatedFe = query.getIncorporatedFe();
				final long frameId = query.getFrameId();
				final String frame = query.getFrame();
				final String frameDefinition = query.getFrameDefinition();
				if (result == null)
				{
					result = new ArrayList<>();
				}
				result.add(new FnLexUnit(luId, lexUnit, pos, definition, dictionary, incorporatedFe, frameId, frame, frameDefinition));
			}
			return new Pair<>(fnWordId, result);
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
	@Nullable
	static public List<FnLexUnit> makeFromWordId(final SQLiteDatabase connection, final long wordId, final Character pos)
	{
		List<FnLexUnit> result = null;
		try (FnLexUnitQueryFromWordId query = new FnLexUnitQueryFromWordId(connection, false, wordId, pos))
		{
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
				if (result == null)
				{
					result = new ArrayList<>();
				}
				result.add(new FnLexUnit(luId, lexUnit, luPos, definition, dictionary, incorporatedFe, frameId, frame, frameDescription));
			}
		}
		return result;
	}

	/**
	 * Make list of lex units from query built from word id
	 *
	 * @param connection connection
	 * @param fnWordId   fn word id to build query from
	 * @param pos        pos to build query from, null if any
	 * @return list of lex units
	 */
	@Nullable
	static public List<FnLexUnit> makeFromFnWordId(final SQLiteDatabase connection, final long fnWordId, final Character pos)
	{
		List<FnLexUnit> result = null;
		try (FnLexUnitQueryFromFnWordId query = new FnLexUnitQueryFromFnWordId(connection, true, fnWordId, pos);)
		{
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
				if (result == null)
				{
					result = new ArrayList<>();
				}
				result.add(new FnLexUnit(luId, lexUnit, luPos, definition, dictionary, incorporatedFe, frameId, frame, frameDescription));
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
	@Nullable
	static public List<FnLexUnit> makeFromFrame(final SQLiteDatabase connection, final long frameId)
	{
		List<FnLexUnit> result = null;
		try (FnLexUnitQueryFromFrameId query = new FnLexUnitQueryFromFrameId(connection, frameId))
		{
			query.execute();

			while (query.next())
			{
				final long luId = query.getLuId();
				final String lexUnit = query.getLexUnit();
				final String pos = query.getPos();
				final String definition = query.getDefinition();
				final String dictionary = query.getDictionary();
				final String incorporatedFe = query.getIncorporatedFe();
				if (result == null)
				{
					result = new ArrayList<>();
				}
				result.add(new FnLexUnit(luId, lexUnit, pos, definition, dictionary, incorporatedFe, 0, null, null));
			}
		}
		return result;
	}
}
