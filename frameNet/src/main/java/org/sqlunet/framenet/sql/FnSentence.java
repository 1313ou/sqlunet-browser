/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Sentence
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnSentence
{
	/**
	 * Sentence id
	 */
	public final long sentenceId;

	/**
	 * Sentence text
	 */
	public final String text;

	/**
	 * Constructor
	 *
	 * @param sentenceId sentence id
	 * @param text       sentence text
	 */
	FnSentence(final long sentenceId, final String text)
	{
		this.text = text;
		this.sentenceId = sentenceId;
	}

	/**
	 * Make sets of sentences from query built from frameId
	 *
	 * @param connection connection
	 * @param sentenceId is the sentence id to build query from
	 * @return sentence
	 */
	@Nullable
	static public FnSentence make(final SQLiteDatabase connection, final long sentenceId)
	{
		try (FnSentenceQuery query = new FnSentenceQuery(connection, sentenceId))
		{
			query.execute();

			if (query.next())
			{
				// final long sentenceId = query.getSentenceId();
				final String text = query.getText();

				return new FnSentence(sentenceId, text);
			}
		}
		return null;
	}

	/**
	 * Make sets of sentences from query built from lex unit id
	 *
	 * @param connection connection
	 * @param luId       is the lex unit id to build query from
	 * @return list of sentences
	 */
	@Nullable
	static public List<FnSentence> makeFromLexicalUnit(final SQLiteDatabase connection, final long luId)
	{
		List<FnSentence> result = null;
		try (FnSentenceQueryFromLexUnitId query = new FnSentenceQueryFromLexUnitId(connection, luId))
		{
			query.execute();

			while (query.next())
			{
				final long sentenceId = query.getSentenceId();
				final String text = query.getText();
				if (result == null)
				{
					result = new ArrayList<>();
				}
				result.add(new FnSentence(sentenceId, text));
			}
		}
		return result;
	}
}
