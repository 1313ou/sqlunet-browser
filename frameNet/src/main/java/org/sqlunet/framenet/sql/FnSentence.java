package org.sqlunet.framenet.sql;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

public class FnSentence
{
	public final String text;

	public final long sentenceId;

	FnSentence(final long thisSentenceId, final String thisText)
	{
		this.text = thisText;
		this.sentenceId = thisSentenceId;
	}

	/**
	 * Make sets of sentences from query built from frameid
	 *
	 * @param thisConnection
	 *            is the database connection
	 * @param thisSentenceId0
	 *            is the sentence id to build query from
	 * @return sentence
	 */
	public static FnSentence make(final SQLiteDatabase thisConnection, final long thisSentenceId0)
	{
		FnSentence thisResult = null;
		FnSentenceQueryCommand thisQuery = null;
		try
		{
			thisQuery = new FnSentenceQueryCommand(thisConnection, thisSentenceId0);
			thisQuery.execute();

			if (thisQuery.next())
			{
				final long thisSentenceId = thisQuery.getSentenceId();
				final String thisText = thisQuery.getText();

				thisResult = new FnSentence(thisSentenceId, thisText);
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

	/**
	 * Make sets of sentences from query built from frameid
	 *
	 * @param thisConnection
	 *            is the database connection
	 * @param theLuId
	 *            is the frameid to build query from
	 * @return list of sentences
	 */
	public static List<FnSentence> makeFromLexicalUnit(final SQLiteDatabase thisConnection, final long theLuId)
	{
		final List<FnSentence> thisResult = new ArrayList<>();
		FnSentenceQueryFromLexicalUnitCommand thisQuery = null;
		try
		{
			thisQuery = new FnSentenceQueryFromLexicalUnitCommand(thisConnection, theLuId);
			thisQuery.execute();

			while (thisQuery.next())
			{
				final long thisSentenceId = thisQuery.getSentenceId();
				final String thisText = thisQuery.getText();

				thisResult.add(new FnSentence(thisSentenceId, thisText));
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
