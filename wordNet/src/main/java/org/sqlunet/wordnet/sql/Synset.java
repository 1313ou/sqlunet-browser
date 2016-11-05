package org.sqlunet.wordnet.sql;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Synset
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class Synset extends BasicSynset
{
	private static final String TAG = "Synset";
	/**
	 * Constructor from data
	 *
	 * @param synsetId    synset id
	 * @param definition  definition
	 * @param lexDomainId lexdomain id
	 * @param sample      sample
	 */
	@SuppressWarnings("unused")
	protected Synset(final long synsetId, final String definition, final int lexDomainId, final String sample)
	{
		super(synsetId, definition, lexDomainId, sample);
	}

	/**
	 * Constructor from query for synsets
	 *
	 * @param query query for synsets
	 */
	Synset(final SynsetsQueryCommand query)
	{
		super(query.getSynsetId(), query.getDefinition(), query.getLexDomainId(), query.getSample());
	}

	/**
	 * Constructor from query for synset
	 *
	 * @param query query for synset
	 */
	Synset(final SynsetQueryCommand query)
	{
		super(query.getSynsetId(), query.getDefinition(), query.getLexDomainId(), query.getSample());
	}

	/**
	 * Constructor from query for synsets of a given type
	 *
	 * @param query query for synsets of a given type
	 */
	Synset(final TypedSynsetsQueryCommand query)
	{
		super(query.getSynsetId(), query.getDefinition(), query.getLexDomainId(), query.getSample());
	}

	/**
	 * Constructor from query for linked synsets
	 *
	 * @param query query for linked synsets
	 */
	Synset(final LinksQueryCommand query)
	{
		super(query.getSynsetId(), query.getDefinition(), query.getLexDomainId(), query.getSample());
	}

	/**
	 * Constructor from query for synsets linked through a given relation type
	 *
	 * @param query query for synsets linked through a given relation type
	 */
	Synset(final TypedLinksQueryCommand query)
	{
		super(query.getSynsetId(), query.getDefinition(), query.getLexDomainId(), query.getSample());
	}

	/**
	 * Get words in the synset as a list
	 *
	 * @param connection connection to the database
	 * @return list of words in synset
	 */
	public List<Word> getSynsetWords(final SQLiteDatabase connection)
	{
		SynsetWordsQueryCommand query = null;
		List<Word> words = new ArrayList<>();
		try
		{
			query = new SynsetWordsQueryCommand(connection, this.synsetId);
			query.execute();

			while (query.next())
			{
				final String lemma = query.getLemma();
				final long id = query.getId();
				words.add(new Word(lemma, id));
			}
		}
		catch (final SQLException e)
		{
			Log.e(TAG, "While querying synset words", e);
			words = null;
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return words;
	}

	/**
	 * Get words in the synset as a string
	 *
	 * @param connection connection to the database
	 * @return list of words in synset as a comma-separated string
	 */
	@SuppressWarnings("unused")
	public String getSynsetWordsAsString(final SQLiteDatabase connection)
	{
		final StringBuilder sb = new StringBuilder();

		// synset words
		final List<Word> words = getSynsetWords(connection);

		// stringify
		for (int i = 0; i < words.size(); i++)
		{
			final Word word = words.get(i);
			final String lemma = word.lemma.replace('_', ' ');
			if (i != 0)
			{
				sb.append(',');
			}
			sb.append(lemma);
		}
		return sb.toString();
	}

	/**
	 * Get the synset's part-of-speech
	 *
	 * @return synset's part-of-speech
	 */
	public String getPosName()
	{
		return Mapping.getPosName(this.lexDomainId);
	}

	/**
	 * Get the synset's lexdomain name
	 *
	 * @return synset's lexdomain name
	 */
	public String getLexDomainName()
	{
		return Mapping.getLexDomainName(this.lexDomainId);
	}

	/**
	 * Get synsets linked to the synset
	 *
	 * @param connection connection
	 * @param wordId     word id (for lexical links)
	 * @return list of synsets linked to the synset
	 */
	public List<Link> getLinks(final SQLiteDatabase connection, final long wordId)
	{
		LinksQueryCommand query = null;
		List<Link> links = new ArrayList<>();
		try
		{
			query = new LinksQueryCommand(connection);
			query.setFromSynset(this.synsetId);
			query.setFromWord(wordId);
			query.execute();

			while (query.next())
			{
				final Link link = new Link(query);
				links.add(link);
			}
		}
		catch (final SQLException e)
		{
			Log.e(TAG, "While querying links", e);
			links = null;
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return links;
	}

	/**
	 * Get synsets linked to the synset through a given relation type
	 *
	 * @param connection connection
	 * @param wordId     word id (for lexical links)
	 * @param linkType   link type
	 * @return list of synsets linked to the synset through a given relation type
	 */
	public List<Link> getTypedLinks(final SQLiteDatabase connection, final long wordId, final int linkType)
	{
		TypedLinksQueryCommand query = null;
		List<Link> links = new ArrayList<>();
		try
		{
			query = new TypedLinksQueryCommand(connection);
			query.setFromSynset(this.synsetId);
			query.setFromWord(wordId);
			query.setLinkType(linkType);
			query.execute();

			while (query.next())
			{
				final Link link = new Link(query);
				links.add(link);
			}
		}
		catch (final SQLException e)
		{
			Log.e(TAG, "While querying typed links", e);
			links = null;
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return links;
	}
}
