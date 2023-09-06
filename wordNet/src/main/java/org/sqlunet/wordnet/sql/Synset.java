/*
 * Copyright (c) 2023. Bernard Bou
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
 * Synset
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class Synset extends BasicSynset
{
	static private final String TAG = "Synset";

	/**
	 * Constructor from data
	 *
	 * @param synsetId   synset id
	 * @param definition definition
	 * @param domainId   domain id
	 * @param sample     sample
	 */
	protected Synset(final long synsetId, final String definition, final int domainId, final String sample)
	{
		super(synsetId, definition, domainId, sample);
	}

	/**
	 * Constructor from query for synsets
	 *
	 * @param query query for synsets
	 */
	Synset(@NonNull final SynsetsQueryFromWordId query)
	{
		super(query.getSynsetId(), query.getDefinition(), query.getDomainId(), query.getSample());
	}

	/**
	 * Constructor from query for synset
	 *
	 * @param query query for synset
	 */
	Synset(@NonNull final SynsetQuery query)
	{
		super(query.getSynsetId(), query.getDefinition(), query.getDomainId(), query.getSample());
	}

	/**
	 * Constructor from query for synsets of a given type
	 *
	 * @param query query for synsets of a given type
	 */
	Synset(@NonNull final SynsetsQueryFromWordIdAndCondition query)
	{
		super(query.getSynsetId(), query.getDefinition(), query.getDomainId(), query.getSample());
	}

	/**
	 * Constructor from query for related synsets
	 *
	 * @param query query for related synsets
	 */
	Synset(@NonNull final RelatedsQueryFromSynsetId query)
	{
		super(query.getSynsetId(), query.getDefinition(), query.getDomainId(), query.getSamples());
	}

	/**
	 * Constructor from query for synsets related through a given relation type id
	 *
	 * @param query query for synsets related through a given relation type id
	 */
	Synset(@NonNull final RelatedsQueryFromSynsetIdAndRelationId query)
	{
		super(query.getSynsetId(), query.getDefinition(), query.getDomainId(), query.getSamples());
	}

	/**
	 * Get words in the synset as a list
	 *
	 * @param connection connection to the database
	 * @return list of words in synset
	 */
	@Nullable
	public List<Word> getSynsetWords(final SQLiteDatabase connection)
	{
		try (SynsetWordsQuery query = new SynsetWordsQuery(connection, this.synsetId))
		{
			query.execute();

			List<Word> words = new ArrayList<>();
			while (query.next())
			{
				final String word = query.getWord();
				final long id = query.getId();
				words.add(new Word(word, id));
			}
			return words;
		}
		catch (@NonNull final SQLException e)
		{
			Log.e(TAG, "While querying synset words", e);
			return null;
		}
	}

	/**
	 * Get words in the synset as a string
	 *
	 * @param connection connection to the database
	 * @return list of words in synset as a comma-separated string
	 */
	@NonNull
	public String getSynsetWordsAsString(final SQLiteDatabase connection)
	{
		final StringBuilder sb = new StringBuilder();

		// synset words
		final List<Word> words = getSynsetWords(connection);

		// stringify
		if (words != null)
		{
			for (int i = 0; i < words.size(); i++)
			{
				final Word word = words.get(i);
				final String word2 = word.word.replace('_', ' ');
				if (i != 0)
				{
					sb.append(',');
				}
				sb.append(word2);
			}
		}
		return sb.toString();
	}

	/**
	 * Get the synset's part-of-speech
	 *
	 * @return synset's part-of-speech
	 */
	@NonNull
	public String getPosName()
	{
		return Mapping.getDomainPosName(this.domainId);
	}

	/**
	 * Get the synset's domain name
	 *
	 * @return synset's domain name
	 */
	@NonNull
	public String getDomainName()
	{
		return Mapping.getDomainName(this.domainId);
	}

	/**
	 * Get synsets related to the synset
	 *
	 * @param connection connection
	 * @param wordId     word id (for lexical relations)
	 * @return list of synsets related to the synset
	 */
	@Nullable
	public List<Related> getRelateds(final SQLiteDatabase connection, final long wordId)
	{
		try (RelatedsQueryFromSynsetId query = new RelatedsQueryFromSynsetId(connection))
		{
			query.setFromSynset(this.synsetId);
			query.setFromWord(wordId);
			query.execute();

			List<Related> relateds = new ArrayList<>();
			while (query.next())
			{
				final Related related = new Related(query);
				relateds.add(related);
			}
			return relateds;
		}
		catch (@NonNull final SQLException e)
		{
			Log.e(TAG, "While querying relateds", e);
			return null;
		}
	}

	/**
	 * Get synsets related to the synset through a given relation type id
	 *
	 * @param connection connection
	 * @param wordId     word id (for lexical relations)
	 * @param relationId relation type id
	 * @return list of synsets related to the synset through a given relation type
	 */
	@Nullable
	public List<Related> getTypedRelateds(final SQLiteDatabase connection, final long wordId, final int relationId)
	{
		try (RelatedsQueryFromSynsetIdAndRelationId query = new RelatedsQueryFromSynsetIdAndRelationId(connection))
		{
			query.setFromSynset(this.synsetId);
			query.setFromWord(wordId);
			query.setRelation(relationId);
			query.execute();

			List<Related> relateds = new ArrayList<>();
			while (query.next())
			{
				final Related related = new Related(query);
				relateds.add(related);
			}
			return relateds;
		}
		catch (@NonNull final SQLException e)
		{
			Log.e(TAG, "While querying typed relations", e);
			return null;
		}
	}
}
