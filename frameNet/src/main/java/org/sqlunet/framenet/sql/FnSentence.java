/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
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
		FnSentence result = null;
		FnSentenceQuery query = null;
		try
		{
			query = new FnSentenceQuery(connection, sentenceId);
			query.execute();

			if (query.next())
			{
				// final long sentenceId = query.getSentenceId();
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
	 * Make sets of sentences from query built from lex unit id
	 *
	 * @param connection connection
	 * @param luId       is the lex unit id to build query from
	 * @return list of sentences
	 */
	@NonNull
	static public List<FnSentence> makeFromLexicalUnit(final SQLiteDatabase connection, final long luId)
	{
		final List<FnSentence> result = new ArrayList<>();
		FnSentenceQueryFromLexUnitId query = null;
		try
		{
			query = new FnSentenceQueryFromLexUnitId(connection, luId);
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
