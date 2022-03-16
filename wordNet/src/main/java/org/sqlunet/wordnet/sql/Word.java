/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.sql;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Word
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Word extends BasicWord
{
	static private final String TAG = "Word";

	/**
	 * Word
	 *
	 * @param word word string
	 * @param id   database id
	 */
	public Word(final String word, final long id)
	{
		super(word, id);
	}

	/**
	 * Constructor
	 *
	 * @param query database query
	 */
	private Word(@NonNull final WordQuery query)
	{
		super(query.getWord(), query.getId());
	}

	/**
	 * Constructor
	 *
	 * @param query database query
	 */
	private Word(@NonNull final WordQueryFromWord query)
	{
		super(query.getWord(), query.getId());
	}

	/**
	 * Make word
	 *
	 * @param connection connection
	 * @param str      target string
	 * @return Word or null
	 */
	@Nullable
	static public Word make(final SQLiteDatabase connection, final String str)
	{
		Word word = null;
		WordQueryFromWord query = null;
		try
		{
			query = new WordQueryFromWord(connection, str);
			query.execute();

			if (query.next())
			{
				word = new Word(query);
			}
		}
		catch (@NonNull final SQLException e)
		{
			Log.e(TAG, "While querying word", e);
			// word can only be null here
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return word;
	}

	/**
	 * Make word
	 *
	 * @param connection connection
	 * @param wordId     target id
	 * @return Word or null
	 */
	@Nullable
	static public Word make(final SQLiteDatabase connection, final long wordId)
	{
		Word word = null;
		WordQuery query = null;
		try
		{
			query = new WordQuery(connection, wordId);
			query.execute();

			if (query.next())
			{
				word = new Word(query);
			}
		}
		catch (@NonNull final SQLException e)
		{
			Log.e(TAG, "While querying word", e);
			// word can only be null here
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return word;
	}

	/**
	 * Get synsets containing a given word
	 *
	 * @param connection connection
	 * @return list of synsets containing a given word
	 */
	@Nullable
	public List<Synset> getSynsets(final SQLiteDatabase connection)
	{
		SynsetsQueryFromWordId query = null;
		List<Synset> synsets = new ArrayList<>();
		try
		{
			query = new SynsetsQueryFromWordId(connection, this.id);
			query.execute();

			while (query.next())
			{
				final Synset synset = new Synset(query);
				synsets.add(synset);
			}
		}
		catch (@NonNull final SQLException e)
		{
			Log.e(TAG, "While querying synsets", e);
			synsets = null;
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return synsets;
	}

	/**
	 * Get synsets containing a given word and of a given part-of-speech or domain id
	 *
	 * @param connection  connection
	 * @param targetType  target type to restrict search to
	 * @param domainBased is whether the query is domain based
	 * @return list of synsets for a given word
	 */
	@Nullable
	public List<Synset> getTypedSynsets(final SQLiteDatabase connection, final int targetType, final boolean domainBased)
	{
		SynsetsQueryFromWordIdAndCondition query = null;
		List<Synset> synsets = new ArrayList<>();
		try
		{
			query = new SynsetsQueryFromWordIdAndCondition(connection, domainBased);
			query.setWordId(this.id);
			if (domainBased)
			{
				query.setDomainType(targetType);
			}
			else
			{
				query.setPosType(targetType);
			}
			query.execute();

			while (query.next())
			{
				final Synset synset = new Synset(query);
				synsets.add(synset);
			}
		}
		catch (@NonNull final SQLException e)
		{
			Log.e(TAG, "While querying typed synsets", e);
			synsets = null;
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return synsets;
	}
}