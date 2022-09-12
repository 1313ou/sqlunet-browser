/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.syntagnet.loaders;

import org.sqlunet.browser.Module;
import org.sqlunet.syntagnet.provider.SyntagNetContract;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Queries
{
	public static Module.ContentProviderSql prepareSnSelect(final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = SyntagNetContract.SnCollocations_X.URI;
		providerSql.projection = new  String[]{ //
				SyntagNetContract.SnCollocations_X.COLLOCATIONID + " AS _id", //
				SyntagNetContract.SnCollocations_X.WORD1ID, //
				SyntagNetContract.SnCollocations_X.WORD2ID, //
				SyntagNetContract.SnCollocations_X.SYNSET1ID, //
				SyntagNetContract.SnCollocations_X.SYNSET2ID, //
				SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD1, //
				SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD2, //
				SyntagNetContract.AS_SYNSETS1 + '.' + SyntagNetContract.SnCollocations_X.POS + " AS " + SyntagNetContract.POS1, //
				SyntagNetContract.AS_SYNSETS2 + '.' + SyntagNetContract.SnCollocations_X.POS + " AS " + SyntagNetContract.POS2, //
		};
		providerSql.selection = SyntagNetContract.SnCollocations_X.WORD1ID + " = ? OR " + SyntagNetContract.SnCollocations_X.WORD2ID + " = ?"; //
		providerSql.selectionArgs = new  String[]{Long.toString(wordId), Long.toString(wordId), Long.toString(wordId)};
		providerSql.sortBy = SyntagNetContract.SnCollocations_X.WORD2ID + " = ?" + ',' + SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + ',' + SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareCollocation(final long collocationId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = SyntagNetContract.SnCollocations_X.URI;
		providerSql.projection = new String[]{ //
				SyntagNetContract.SnCollocations_X.COLLOCATIONID, //
				SyntagNetContract.SnCollocations_X.WORD1ID, //
				SyntagNetContract.SnCollocations_X.WORD2ID, //
				SyntagNetContract.SnCollocations_X.SYNSET1ID, //
				SyntagNetContract.SnCollocations_X.SYNSET2ID, //
				SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD1, //
				SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD2, //
				SyntagNetContract.AS_SYNSETS1 + '.' + SyntagNetContract.SnCollocations_X.DEFINITION + " AS " + SyntagNetContract.DEFINITION1, //
				SyntagNetContract.AS_SYNSETS2 + '.' + SyntagNetContract.SnCollocations_X.DEFINITION + " AS " + SyntagNetContract.DEFINITION2, //
				SyntagNetContract.AS_SYNSETS1 + '.' + SyntagNetContract.SnCollocations_X.POS + " AS " + SyntagNetContract.POS1, //
				SyntagNetContract.AS_SYNSETS2 + '.' + SyntagNetContract.SnCollocations_X.POS + " AS " + SyntagNetContract.POS2, //
		};
		providerSql.selection = SyntagNetContract.SnCollocations_X.COLLOCATIONID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(collocationId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareCollocations(final Long word1Id, @Nullable final Long word2Id, final Long synset1Id, final Long synset2Id)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = SyntagNetContract.SnCollocations_X.URI;
		providerSql.projection = new String[]{ //
				SyntagNetContract.SnCollocations_X.COLLOCATIONID, //
				SyntagNetContract.SnCollocations_X.WORD1ID, //
				SyntagNetContract.SnCollocations_X.WORD1ID, //
				SyntagNetContract.SnCollocations_X.WORD2ID, //
				SyntagNetContract.SnCollocations_X.SYNSET1ID, //
				SyntagNetContract.SnCollocations_X.SYNSET2ID, //
				SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD1, //
				SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD2, //
				SyntagNetContract.AS_SYNSETS1 + '.' + SyntagNetContract.SnCollocations_X.DEFINITION + " AS " + SyntagNetContract.DEFINITION1, //
				SyntagNetContract.AS_SYNSETS2 + '.' + SyntagNetContract.SnCollocations_X.DEFINITION + " AS " + SyntagNetContract.DEFINITION2, //
				SyntagNetContract.AS_SYNSETS1 + '.' + SyntagNetContract.SnCollocations_X.POS + " AS " + SyntagNetContract.POS1, //
				SyntagNetContract.AS_SYNSETS2 + '.' + SyntagNetContract.SnCollocations_X.POS + " AS " + SyntagNetContract.POS2, //
		};
		providerSql.selection = selection(word1Id, word2Id, synset1Id, synset2Id);
		providerSql.selectionArgs = selectionArgs(word1Id, word2Id, synset1Id, synset2Id, word2Id);
		providerSql.sortBy = SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + ',' + SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD;
		if (word2Id != null)
		{
			providerSql.sortBy = SyntagNetContract.SnCollocations_X.WORD2ID + " = ?" + ',' + providerSql.sortBy;
		}
		return providerSql;
	}

	public static Module.ContentProviderSql prepareCollocations(final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = SyntagNetContract.SnCollocations_X.URI;
		providerSql.projection = new String[]{ //
				SyntagNetContract.SnCollocations_X.WORD1ID, //
				SyntagNetContract.SnCollocations_X.WORD2ID, //
				SyntagNetContract.SnCollocations_X.SYNSET1ID, //
				SyntagNetContract.SnCollocations_X.SYNSET2ID, //
				SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD1, //
				SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD2,};
		providerSql.selection = SyntagNetContract.SnCollocations_X.WORD1ID + " = ? OR " + SyntagNetContract.SnCollocations_X.WORD2ID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(wordId), Long.toString(wordId), Long.toString(wordId)};
		providerSql.sortBy = SyntagNetContract.SnCollocations_X.WORD2ID + " = ?" + ',' + SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + ',' + SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareCollocations(final String word)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = SyntagNetContract.SnCollocations_X.URI;
		providerSql.projection = new String[]{ //
				SyntagNetContract.SnCollocations_X.WORD1ID, //
				SyntagNetContract.SnCollocations_X.WORD2ID, //
				SyntagNetContract.SnCollocations_X.SYNSET1ID, //
				SyntagNetContract.SnCollocations_X.SYNSET2ID, //
				SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD1, //
				SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD2,};
		providerSql.selection = SyntagNetContract.SnCollocations_X.WORD1ID + " = ? OR " + SyntagNetContract.SnCollocations_X.WORD2ID + " = ?";
		providerSql.selectionArgs = new String[]{word};
		providerSql.sortBy = SyntagNetContract.SnCollocations_X.WORD2ID + " = ?" + ',' + SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + ',' + SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD;
		return providerSql;
	}

	/**
	 * Make selection. When both arg1 and arg2 are equal, take it to mean the position (after, before) is indifferent (so OR)
	 *
	 * @param word1Id   word 1 id
	 * @param word2Id   word 2 id
	 * @param synset1Id synset 1 id
	 * @param synset2Id synset 2 id
	 * @return selection string
	 */
	@NonNull
	private static String selection(@Nullable final Long word1Id, @Nullable final Long word2Id, @Nullable final Long synset1Id, @Nullable final Long synset2Id)
	{
		String wordSelection1 = word1Id == null ? "" : SyntagNetContract.SnCollocations_X.WORD1ID + " = ?";
		String wordSelection2 = word2Id == null ? "" : SyntagNetContract.SnCollocations_X.WORD2ID + " = ?";
		String wordSelection = wordSelection1.isEmpty() || wordSelection2.isEmpty() ? wordSelection1 + wordSelection2 : wordSelection1 + (word1Id.equals(word2Id) ? " OR " : " AND ") + wordSelection2;

		String synsetSelection1 = synset1Id == null ? "" : SyntagNetContract.SnCollocations_X.SYNSET1ID + " = ?";
		String synsetSelection2 = synset2Id == null ? "" : SyntagNetContract.SnCollocations_X.SYNSET2ID + " = ?";
		String synsetSelection = synsetSelection1.isEmpty() || synsetSelection2.isEmpty() ? synsetSelection1 + synsetSelection2 : synsetSelection1 + (synset1Id.equals(synset2Id) ? " OR " : " AND ") + synsetSelection2;

		return wordSelection.isEmpty() || synsetSelection.isEmpty() ? (wordSelection + synsetSelection) : ('(' + wordSelection + ") AND (" + synsetSelection + ')');
	}

	/**
	 * Make selection arguments
	 *
	 * @param word1Id   word 1 id
	 * @param word2Id   word 2 id
	 * @param synset1Id synset 1 id
	 * @param synset2Id synset 2 id
	 * @param orderId   id param used in order clause
	 * @return selection arguments
	 */
	@NonNull
	private static String[] selectionArgs(@Nullable final Long word1Id, @Nullable final Long word2Id, @Nullable final Long synset1Id, @Nullable final Long synset2Id, @Nullable final Long orderId)
	{
		List<String> args = new ArrayList<>();
		if (word1Id != null)
		{
			args.add(Long.toString(word1Id));
		}
		if (word2Id != null)
		{
			args.add(Long.toString(word2Id));
		}
		if (synset1Id != null)
		{
			args.add(Long.toString(synset1Id));
		}
		if (synset2Id != null)
		{
			args.add(Long.toString(synset2Id));
		}
		if (orderId != null)
		{
			args.add(Long.toString(orderId));
		}
		return args.toArray(new String[0]);
	}
}
