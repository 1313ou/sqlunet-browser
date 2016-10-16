package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

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
		this.frame = frameId == -1 ?
				null :
				new FnFrame(frameId, frame, frameDefinition, null, null);
	}

	/**
	 * Make sets of lexunits from query built from word
	 *
	 * @param thisConnection is the database connection
	 * @param thisTargetWord is the word to build query from
	 * @return wordid + list of lexunits
	 */
	static public Pair<Long, List<FnLexUnit>> makeFromWord(final SQLiteDatabase thisConnection, final String thisTargetWord)
	{
		final List<FnLexUnit> thisResult = new ArrayList<>();
		FnLexUnitQueryCommandFromWord thisQuery = null;
		try
		{
			thisQuery = new FnLexUnitQueryCommandFromWord(thisConnection, thisTargetWord);
			thisQuery.execute();

			long thisWordId = 0;
			while (thisQuery.next())
			{
				thisWordId = thisQuery.getWordId();

				final long thisLuId = thisQuery.getLuId();
				final String thisLexUnit = thisQuery.getLexUnit();
				final String thisPos = thisQuery.getPos();
				final String thisDefinition = thisQuery.getLexUnitDefinition();
				final String thisDictionary = thisQuery.getLexUnitDictionary();
				final String thisIncorporatedFe = thisQuery.getIncoporatedFe();
				final long thisFrameId = thisQuery.getFrameId();
				final String thisFrame = thisQuery.getFrame();
				final String thisFrameDefinition = thisQuery.getFrameDefinition();

				thisResult.add(new FnLexUnit(thisLuId, thisLexUnit, thisPos, thisDefinition, thisDictionary, thisIncorporatedFe, thisFrameId, thisFrame, thisFrameDefinition));
			}
			return new Pair<>(thisWordId, thisResult);
		} finally
		{
			if (thisQuery != null)
			{
				thisQuery.release();
			}
		}
	}

	/**
	 * Make sets of lexunits from query built from wordid
	 *
	 * @param thisConnection   is the database connection
	 * @param thisTargetWordId is the word id to build query from
	 * @param thisTargetPos    is the pos to build query from, null if any
	 * @return list of lexunits
	 */
	static public List<FnLexUnit> makeFromWordId(final SQLiteDatabase thisConnection, final long thisTargetWordId, final Character thisTargetPos)
	{
		final List<FnLexUnit> thisResult = new ArrayList<>();
		FnWordLexUnitQueryCommand thisQuery = null;
		try
		{
			thisQuery = new FnWordLexUnitQueryCommand(thisConnection, thisTargetWordId, thisTargetPos);
			thisQuery.execute();

			while (thisQuery.next())
			{
				final long thisLuId = thisQuery.getLuId();
				final String thisLexUnit = thisQuery.getLexUnit();
				final String thisPos = thisQuery.getPos();
				final String thisDefinition = thisQuery.getDefinition();
				final String thisDictionary = thisQuery.getDictionary();
				final String thisIncorporatedFe = thisQuery.getIncorporatedFe();
				final long thisFrameId = thisQuery.getFrameId();
				final String thisFrame = thisQuery.getFrame();
				final String thisFrameDescription = thisQuery.getFrameDescription();

				thisResult.add(new FnLexUnit(thisLuId, thisLexUnit, thisPos, thisDefinition, thisDictionary, thisIncorporatedFe, thisFrameId, thisFrame, thisFrameDescription));
			}
		} finally
		{
			if (thisQuery != null)
			{
				thisQuery.release();
			}
		}
		return thisResult;
	}

	public static List<FnLexUnit> makeFromFrame(final SQLiteDatabase thisConnection, final long thisTargetFrameId)
	{
		final List<FnLexUnit> thisResult = new ArrayList<>();
		FnFrameLexUnitQueryCommand thisQuery = null;
		try
		{
			thisQuery = new FnFrameLexUnitQueryCommand(thisConnection, thisTargetFrameId);
			thisQuery.execute();

			while (thisQuery.next())
			{
				final long thisLuId = thisQuery.getLuId();
				final String thisLexUnit = thisQuery.getLexUnit();
				final String thisPos = thisQuery.getPos();
				final String thisDefinition = thisQuery.getDefinition();
				final String thisDictionary = thisQuery.getDictionary();
				final String thisIncorporatedFe = thisQuery.getIncorporatedFe();
				thisResult.add(new FnLexUnit(thisLuId, thisLexUnit, thisPos, thisDefinition, thisDictionary, thisIncorporatedFe, -1, null, null));
			}
		} finally
		{
			if (thisQuery != null)
			{
				thisQuery.release();
			}
		}
		return thisResult;
	}

	public static FnLexUnit makeFromId(final SQLiteDatabase thisConnection, final long thisTargetLuId)
	{
		FnLexUnit thisResult = null;
		FnLexUnitQueryCommand thisQuery = null;
		try
		{
			thisQuery = new FnLexUnitQueryCommand(thisConnection, thisTargetLuId);
			thisQuery.execute();

			if (thisQuery.next())
			{
				final long thisLuId = thisQuery.getLuId();
				final String thisLexUnit = thisQuery.getLexUnit();
				final String thisPos = thisQuery.getPos();
				final String thisDefinition = thisQuery.getDefinition();
				final String thisDictionary = thisQuery.getDictionary();
				final String thisIncorporatedFe = thisQuery.getIncorporatedFe();
				final long thisFrameId = thisQuery.getFrameId();
				final String thisFrame = thisQuery.getFrame();
				final String thisFrameDefinition = thisQuery.getFrameDescription();
				thisResult = new FnLexUnit(thisLuId, thisLexUnit, thisPos, thisDefinition, thisDictionary, thisIncorporatedFe, thisFrameId, thisFrame, thisFrameDefinition);
			}
		} finally
		{
			if (thisQuery != null)
			{
				thisQuery.release();
			}
		}
		return thisResult;
	}

}
