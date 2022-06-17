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
 * Related, a related synset
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class Related extends Synset
{
	static private final String TAG = "Related";

	/**
	 * <code>relationId</code> relation type id
	 */
	private final int relationId;

	/**
	 * <code>word</code> related word (lexrelations)
	 */
	@Nullable
	public final String word;

	/**
	 * <code>wordId</code> related word id (lexrelations)
	 */
	public final long wordId;

	/**
	 * <code>fromSynsetId</code> source synset id
	 */
	public final long fromSynsetId;

	/**
	 * <code>fromWordId</code> source synset id
	 */
	public final long fromWordId;

	/**
	 * Constructor from query for synsets related to a given synset
	 *
	 * @param query query for synsets related to a given synset
	 */
	public Related(@NonNull final RelatedsQueryFromSynsetId query)
	{
		// construct synset
		super(query);

		// relation data
		final String[] words = query.getWords();
		final long[] wordIds = query.getWordIds();

		this.relationId = query.getRelationId();
		this.word = words == null ? null : (words.length == 1 ? words[0] : null);
		this.wordId = wordIds == null ? 0 : (wordIds.length == 1 ? wordIds[0] : 0);
		this.fromSynsetId = query.getFromSynset();
		this.fromWordId = query.getFromWord();
	}

	/**
	 * Constructor from query for synsets related to a given synset through a given relation type id
	 *
	 * @param query is a query for synsets related to a given synset through a given relation type id
	 */
	Related(@NonNull final RelatedsQueryFromSynsetIdAndRelationId query)
	{
		// construct synset
		super(query);

		// relation data
		final String[] words = query.getWords();
		final long[] wordIds = query.getWordIds();

		this.relationId = query.getRelationId();
		this.word = words == null ? null : (words.length == 1 ? words[0] : null);
		this.wordId = wordIds == null ? 0 : (wordIds.length == 1 ? wordIds[0] : 0);
		this.fromSynsetId = query.getFromSynset();
		this.fromWordId = query.getFromWord();
	}

	/**
	 * Get relation name
	 *
	 * @return relation name
	 */
	public String getRelationName()
	{
		return Mapping.getRelationName(this.relationId);
	}

	/**
	 * Get whether relation can recurse
	 *
	 * @return true if the relation can recurse
	 */
	public boolean canRecurse()
	{
		return Mapping.canRecurse(this.relationId);
	}

	/**
	 * Override : recurse only on relations of the same relation type
	 */
	@Nullable
	@Override
	public List<Related> getRelateds(final SQLiteDatabase connection, final long wordId)
	{
		try (RelatedsQueryFromSynsetIdAndRelationId query = new RelatedsQueryFromSynsetIdAndRelationId(connection))
		{
			query.setFromSynset(this.synsetId);
			query.setFromWord(wordId);
			query.setRelation(this.relationId);
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
			Log.e(TAG, "While querying", e);
			return null;
		}
	}
}