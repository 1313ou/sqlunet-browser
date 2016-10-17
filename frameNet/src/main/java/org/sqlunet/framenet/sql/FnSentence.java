package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Sentence
 *
 * @author Bernard Bou
 */
public class FnSentence
{
	public final String text;

	public final long sentenceId;

	FnSentence(final long sentenceId, final String text)
	{
		this.text = text;
		this.sentenceId = sentenceId;
	}

	/**
	 * Make sets of sentences from query built from frameid
	 *
	 * @param connection  is the database connection
	 * @param sentenceId0 is the sentence id to build query from
	 * @return sentence
	 */
	public static FnSentence make(final SQLiteDatabase connection, final long sentenceId0)
	{
		FnSentence result = null;
		FnSentenceQueryCommand query = null;
		try
		{
			query = new FnSentenceQueryCommand(connection, sentenceId0);
			query.execute();

			if (query.next())
			{
				final long sentenceId = query.getSentenceId();
				final String text = query.getText();

				result = new FnSentence(sentenceId, text);
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
	 * Make sets of sentences from query built from frameid
	 *
	 * @param connection is the database connection
	 * @param luId       is the luid to build query from
	 * @return list of sentences
	 */
	public static List<FnSentence> makeFromLexicalUnit(final SQLiteDatabase connection, final long luId)
	{
		final List<FnSentence> result = new ArrayList<>();
		FnSentenceQueryFromLexicalUnitCommand query = null;
		try
		{
			query = new FnSentenceQueryFromLexicalUnitCommand(connection, luId);
			query.execute();

			while (query.next())
			{
				final long sentenceId = query.getSentenceId();
				final String text = query.getText();

				result.add(new FnSentence(sentenceId, text));
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
