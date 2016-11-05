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

/**
 * Word
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Word extends BasicWord
{
	private static final String TAG = "Word";
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
	private Word(final WordQueryCommand query)
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
	static public Word make(final SQLiteDatabase connection, final String lemma)
	{
		Word word = null;
		WordQueryCommand query = null;
		try
		{
			query = new WordQueryCommand(connection, lemma);
			query.execute();

			if (query.next())
			{
				word = new Word(query);
			}
		}
		catch (final SQLException e)
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
	public List<Synset> getSynsets(final SQLiteDatabase connection)
	{
		SynsetsQueryCommand query = null;
		List<Synset> synsets = new ArrayList<>();
		try
		{
			query = new SynsetsQueryCommand(connection, this.id);
			query.execute();

			while (query.next())
			{
				final Synset synset = new Synset(query);
				synsets.add(synset);
			}
		}
		catch (final SQLException e)
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
	public List<Synset> getTypedSynsets(final SQLiteDatabase connection, final int targetType, final boolean lexDomainBased)
	{
		TypedSynsetsQueryCommand query = null;
		List<Synset> synsets = new ArrayList<>();
		try
		{
			query = new TypedSynsetsQueryCommand(connection, lexDomainBased);
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
		catch (final SQLException e)
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
	@SuppressWarnings("unused")
	public static Map<Integer, Set<Integer>> getLinkTypes(final SQLiteDatabase connection, final String word)
	{
		LinkTypesQueryCommand query = null;
		Map<Integer, Set<Integer>> map = new TreeMap<>();
		try
		{
			query = new LinkTypesQueryCommand(connection);
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
				values.add(linkType);
			}
		}
		catch (final SQLException e)
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