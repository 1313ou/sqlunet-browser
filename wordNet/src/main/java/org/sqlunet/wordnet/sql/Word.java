/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.sql;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
	 * @param lemma word string
	 * @param id    database id
	 */
	public Word(final String lemma, final long id)
	{
		super(lemma, id);
	}

	/**
	 * Constructor
	 *
	 * @param query database query
	 */
	private Word(@NonNull final WordQuery query)
	{
		super(query.getLemma(), query.getId());
	}

	/**
	 * Constructor
	 *
	 * @param query database query
	 */
	private Word(@NonNull final WordQueryFromLemma query)
	{
		super(query.getLemma(), query.getId());
	}

	/**
	 * Make word
	 *
	 * @param connection connection
	 * @param lemma      target string
	 * @return Word or null
	 */
	@Nullable
	static public Word make(final SQLiteDatabase connection, final String lemma)
	{
		Word word = null;
		WordQueryFromLemma query = null;
		try
		{
			query = new WordQueryFromLemma(connection, lemma);
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
	 * Get synsets containing a given word and of a given part-of-speech or lexdomain id
	 *
	 * @param connection     connection
	 * @param targetType     target type to restrict search to
	 * @param lexDomainBased is whether the query is lexdomain based
	 * @return list of synsets for a given word
	 */
	@Nullable
	public List<Synset> getTypedSynsets(final SQLiteDatabase connection, final int targetType, final boolean lexDomainBased)
	{
		SynsetsQueryFromWordIdAndCondition query = null;
		List<Synset> synsets = new ArrayList<>();
		try
		{
			query = new SynsetsQueryFromWordIdAndCondition(connection, lexDomainBased);
			query.setWordId(this.id);
			if (lexDomainBased)
			{
				query.setLexDomainType(targetType);
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

	/**
	 * Get lexdomain-link type map for a given word
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return lexdomain-link type map for a given word
	 */
	@Nullable
	@SuppressWarnings("unused")
	static public Map<Integer, Set<Integer>> getLinkTypes(final SQLiteDatabase connection, final String word)
	{
		LinkTypesQueryFromWord query = null;
		Map<Integer, Set<Integer>> map = new TreeMap<>();
		try
		{
			query = new LinkTypesQueryFromWord(connection);
			query.setWord(word);
			query.execute();

			while (query.next())
			{
				final int linkType = query.getLinkType();
				if (linkType == 0)
				{
					continue;
				}
				final Integer key = query.getLexDomainId();

				Set<Integer> values;
				if (map.containsKey(key))
				{
					values = map.get(key);
				}
				else
				{
					values = new TreeSet<>();
					map.put(key, values);
				}
				assert values != null;
				values.add(linkType);
			}
		}
		catch (@NonNull final SQLException e)
		{
			Log.e(TAG, "While querying link types", e);
			map = null;
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return map;
	}
}