/*
 * Copyright (c) 2022. Bernard Bou
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
	static class WithDefinitionAndPos extends Collocation
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

		WithDefinitionAndPos(final long collocationId, final long word1Id, final long word2Id, final long synset1Id, final long synset2Id, final String word1, final String word2, final char pos1, final char pos2, final String definition1, final String definition2)
		{
			super(collocationId, word1Id, word2Id, synset1Id, synset2Id, word1, word2);
			this.pos1 = pos1;
			this.pos2 = pos2;
			this.definition1 = definition1;
			this.definition2 = definition2;
		}

		/**
		 * Make sets of SyntagNet collocations from query built from word
		 *
		 * @param connection connection
		 * @param targetWord is the word to build query from
		 * @return list of SyntagNet collocations
		 */
		@NonNull
		static List<Collocation.WithDefinitionAndPos> makeFromWord(final SQLiteDatabase connection, final String targetWord)
		{
			final List<Collocation.WithDefinitionAndPos> result = new ArrayList<>();
			try (CollocationQueryFromWord query = new CollocationQueryFromWord(connection, targetWord))
			{
				query.execute();

				while (query.next())
				{
					Collocation.WithDefinitionAndPos collocation = makeCollocationWithDefinitionAndPos(query);
					result.add(collocation);
				}
				return result;
			}
		}

		/**
		 * Make sets of SyntagNet collocations from query built from word id
		 *
		 * @param connection   connection
		 * @param targetWordId is the word id to build query from
		 * @return list of SyntagNet collocations
		 */
		@NonNull
		static List<Collocation.WithDefinitionAndPos> makeFromWordId(final SQLiteDatabase connection, final long targetWordId)
		{
			final List<Collocation.WithDefinitionAndPos> result = new ArrayList<>();
			try (CollocationQueryFromWordId query = new CollocationQueryFromWordId(connection, targetWordId))
			{
				query.execute();

				while (query.next())
				{
					Collocation.WithDefinitionAndPos collocation = makeCollocationWithDefinitionAndPos(query);
					result.add(collocation);
				}
			}
			return result;
		}

		/**
		 * Make sets of SyntagNet collocations from query built from word ids
		 *
		 * @param connection    connection
		 * @param targetWordId  is the word id to build query from
		 * @param targetWord2Id is the word 2 id to build query from
		 * @return list of SyntagNet collocations
		 */
		@NonNull
		static public List<WithDefinitionAndPos> makeFromWordIds(final SQLiteDatabase connection, final long targetWordId, final long targetWord2Id)
		{
			final List<Collocation.WithDefinitionAndPos> result = new ArrayList<>();
			try (CollocationQueryFromWordIds query = new CollocationQueryFromWordIds(connection, targetWordId, targetWord2Id))
			{
				query.execute();

				while (query.next())
				{
					Collocation.WithDefinitionAndPos collocation = makeCollocationWithDefinitionAndPos(query);
					result.add(collocation);
				}
			}
			return result;
		}

		/**
		 * Make sets of SyntagNet collocations from query built from word id ad synset id
		 *
		 * @param connection   connection
		 * @param targetWordId is the word id to build query from
		 * @return list of SyntagNet collocations
		 */
		@NonNull
		static List<Collocation.WithDefinitionAndPos> makeFromWordIdAndSynsetId(final SQLiteDatabase connection, final long targetWordId, final long targetSynsetId)
		{
			final List<Collocation.WithDefinitionAndPos> result = new ArrayList<>();
			try (CollocationQueryFromWordIdAndSynsetId query = new CollocationQueryFromWordIdAndSynsetId(connection, targetWordId, targetSynsetId))
			{
				query.execute();

				while (query.next())
				{
					Collocation.WithDefinitionAndPos collocation = makeCollocationWithDefinitionAndPos(query);
					result.add(collocation);
				}
			}
			return result;
		}

		/**
		 * Make sets of SyntagNet collocations from query built from word ids ad synset ids
		 *
		 * @param connection      connection
		 * @param targetWordId    is the word id to build query from
		 * @param targetWord2Id   is the word 2 id to build query from
		 * @param targetSynsetId  is the synset id to build query from
		 * @param targetSynset2Id is the synset 2 id to build query from
		 * @return list of SyntagNet collocations
		 */
		@NonNull
		static public List<WithDefinitionAndPos> makeFromWordIdAndSynsetIds(final SQLiteDatabase connection, final long targetWordId, final long targetSynsetId, final long targetWord2Id, final long targetSynset2Id)
		{
			final List<Collocation.WithDefinitionAndPos> result = new ArrayList<>();
			try (CollocationQueryFromWordIdsAndSynsetIds query = new CollocationQueryFromWordIdsAndSynsetIds(connection, targetWordId, targetSynsetId, targetWord2Id, targetSynset2Id))
			{
				query.execute();

				while (query.next())
				{
					Collocation.WithDefinitionAndPos collocation = makeCollocationWithDefinitionAndPos(query);
					result.add(collocation);
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
		static public List<Collocation.WithDefinitionAndPos> make(final SQLiteDatabase connection, final long collocationId)
		{
			final List<Collocation.WithDefinitionAndPos> result = new ArrayList<>();
			try (CollocationQuery query = new CollocationQuery(connection, collocationId))
			{
				query.execute();

				while (query.next())
				{
					Collocation.WithDefinitionAndPos collocation = makeCollocationWithDefinitionAndPos(query);
					result.add(collocation);
				}
			}
			return result;
		}

		@NonNull
		private static Collocation.WithDefinitionAndPos makeCollocationWithDefinitionAndPos(@NonNull BaseCollocationQuery query)
		{
			final long collocationId = query.getId();
			final long word1Id = query.getWord1Id();
			final long word2Id = query.getWord2Id();
			final long synset1Id = query.getSynset1Id();
			final long synset2Id = query.getSynset2Id();
			final String word1 = query.getWord1();
			final String word2 = query.getWord2();
			final Character pos1 = query.getPos1();
			assert pos1 != null;
			final Character pos2 = query.getPos2();
			assert pos2 != null;
			final String definition1 = query.getDefinition1();
			final String definition2 = query.getDefinition2();
			return new Collocation.WithDefinitionAndPos(collocationId, word1Id, word2Id, synset1Id, synset2Id, word1, word2, pos1, pos2, definition1, definition2);
		}
	}

	/**
	 * Id
	 */
	final long collocationId;

	/**
	 * Word1 Id
	 */
	final long word1Id;

	/**
	 * Word2 id
	 */
	final long word2Id;

	/**
	 * Synset2 id
	 */
	final long synset1Id;

	/**
	 * Synset2 id
	 */
	final long synset2Id;

	/**
	 * Word1
	 */
	final String word1;

	/**
	 * Word2
	 */
	final String word2;

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
	private Collocation(final long collocationId, final long word1Id, final long word2Id, final long synset1Id, final long synset2Id, final String word1, final String word2)
	{
		super();
		this.collocationId = collocationId;
		this.word1Id = word1Id;
		this.word2Id = word2Id;
		this.synset1Id = synset1Id;
		this.synset2Id = synset2Id;
		this.word1 = word1;
		this.word2 = word2;
	}

	@NonNull
	public static List<Collocation> makeSelectorFromWord(final SQLiteDatabase connection, final String targetWord)
	{
		final List<Collocation> result = new ArrayList<>();
		try (CollocationQueryFromWord query = new CollocationQueryFromWord(connection, targetWord))
		{
			query.execute();

			while (query.next())
			{
				Collocation collocation = makeCollocation(query);
				result.add(collocation);
			}
			return result;
		}
	}

	@NonNull
	private static Collocation makeCollocation(@NonNull BaseCollocationQuery query)
	{
		final long collocationId = query.getId();
		final long word1Id = query.getWord1Id();
		final long word2Id = query.getWord2Id();
		final long synset1Id = query.getSynset1Id();
		final long synset2Id = query.getSynset2Id();
		final String word1 = query.getWord1();
		final String word2 = query.getWord2();
		return new Collocation(collocationId, word1Id, word2Id, synset1Id, synset2Id, word1, word2);
	}
}
