/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * SyntagNet collocation
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class Collocation
{
	static class CollocationX extends Collocation
	{
		/**
		 * POS 1
		 */
		@SuppressWarnings("WeakerAccess")
		public final char pos1;

		/**
		 * POS 2
		 */
		@SuppressWarnings("WeakerAccess")
		public final char pos2;

		/**
		 * Definition 1
		 */
		@SuppressWarnings("WeakerAccess")
		public final String definition1;

		/**
		 * Definition 2
		 */
		@SuppressWarnings("WeakerAccess")
		public final String definition2;

		public CollocationX(final long word1Id, final long word2Id, final long synset1Id, final long synset2Id, final String word1, final String word2, final char pos1, final char pos2, final String definition1, final String definition2)
		{
			super(word1Id, word2Id, synset1Id, synset2Id, word1, word2);
			this.pos1 = pos1;
			this.pos2 = pos2;
			this.definition1 = definition1;
			this.definition2 = definition2;
		}
	}

	/**
	 * Word1 Id
	 */
	public final long word1Id;

	/**
	 * Word2 id
	 */
	public final long word2Id;

	/**
	 * Synset2 id
	 */
	public final long synset1Id;

	/**
	 * Synset2 id
	 */
	public final long synset2Id;

	/**
	 * Word1
	 */
	public final String word1;

	/**
	 * Word2
	 */
	public final String word2;

	/**
	 * Constructor
	 *
	 * @param word1Id   word 1 id
	 * @param word2Id   word 2 id
	 * @param synset1Id synset 1 id
	 * @param synset2Id synset 2 id
	 * @param word1     word 1
	 * @param word2     word 2
	 */
	private Collocation(final long word1Id, final long word2Id, final long synset1Id, final long synset2Id, final String word1, final String word2)
	{
		super();
		this.word1Id = word1Id;
		this.word2Id = word2Id;
		this.synset1Id = synset1Id;
		this.synset2Id = synset2Id;
		this.word1 = word1;
		this.word2 = word2;
	}

	/**
	 * Make sets of SyntagNet collocations from query built from word id
	 *
	 * @param connection connection
	 * @param word       is the word to build query from
	 * @return list of SyntagNet collocations
	 */
	@NonNull
	static public List<Collocation> makeFromWord(final SQLiteDatabase connection, final String word)
	{
		final List<Collocation> result = new ArrayList<>();
		CollocationQueryFromWord query = null;
		try
		{
			query = new CollocationQueryFromWord(connection, word);
			query.execute();

			while (query.next())
			{
				final long word1Id = query.getWord1Id();
				final long word2Id = query.getWord2Id();
				final long synset1Id = query.getSynset1Id();
				final long synset2Id = query.getSynset2Id();
				final String word1 = query.getWord1();
				final String word2 = query.getWord2();
				result.add(new Collocation(word1Id, word2Id, synset1Id, synset2Id, word1, word2));
			}
			return result;
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
	}

	/**
	 * Make sets of SyntagNet collocations from query built from word id
	 *
	 * @param connection connection
	 * @param wordId     is the word id to build query from
	 * @return list of SyntagNet collocations
	 */
	@NonNull
	static public List<Collocation> makeFromWordId(final SQLiteDatabase connection, final long wordId)
	{
		final List<Collocation> result = new ArrayList<>();
		CollocationQueryFromWordId query = null;
		try
		{
			query = new CollocationQueryFromWordId(connection, wordId);
			query.execute();

			while (query.next())
			{
				final long word1Id = query.getWord1Id();
				final long word2Id = query.getWord2Id();
				final long synset1Id = query.getSynset1Id();
				final long synset2Id = query.getSynset2Id();
				final String word1 = query.getWord1();
				final String word2 = query.getWord2();
				result.add(new Collocation(word1Id, word2Id, synset1Id, synset2Id, word1, word2));
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
	 * Make sets of SyntagNet collocations from query built from word id
	 *
	 * @param connection connection
	 * @param wordId     is the word id to build query from
	 * @return list of SyntagNet collocations
	 */
	@NonNull
	static public List<Collocation> makeFromWordIdAndSynsetId(final SQLiteDatabase connection, final long wordId, final long synsetId)
	{
		final List<Collocation> result = new ArrayList<>();
		CollocationQueryFromWordIdAndSynsetId query = null;
		try
		{
			query = new CollocationQueryFromWordIdAndSynsetId(connection, wordId, synsetId);
			query.execute();

			while (query.next())
			{
				final long word1Id = query.getWord1Id();
				final long word2Id = query.getWord2Id();
				final long synset1Id = query.getSynset1Id();
				final long synset2Id = query.getSynset2Id();
				final String word1 = query.getWord1();
				final String word2 = query.getWord2();
				result.add(new Collocation(word1Id, word2Id, synset1Id, synset2Id, word1, word2));
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
	 * Make sets of SyntagNet collocations from query built from collocation id
	 *
	 * @param connection    connection
	 * @param collocationId is the collocation id to build query from
	 * @return list of SyntagNet collocations
	 */
	@NonNull
	static public List<Collocation> make(final SQLiteDatabase connection, final long collocationId)
	{
		final List<Collocation> result = new ArrayList<>();
		CollocationQuery query = null;
		try
		{
			query = new CollocationQuery(connection, collocationId);
			query.execute();

			while (query.next())
			{
				final long word1Id = query.getWord1Id();
				final long word2Id = query.getWord2Id();
				final long synset1Id = query.getSynset1Id();
				final long synset2Id = query.getSynset2Id();
				final String word1 = query.getWord1();
				final String word2 = query.getWord2();
				result.add(new Collocation(word1Id, word2Id, synset1Id, synset2Id, word1, word2));
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
