package org.sqlunet.wordnet.provider;

import android.app.SearchManager;

import org.junit.Test;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.wordnet.provider.WordNetDispatcher.Factory;
import org.sqlunet.wordnet.provider.WordNetDispatcher.Result;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Queries factory, which will execute on the development machine (host).
 */
public class QueriesUnitTest
{
	private final int[] codes = {WordNetDispatcher.WORDS, WordNetDispatcher.WORD, WordNetDispatcher.SENSES, WordNetDispatcher.SENSE, WordNetDispatcher.SYNSETS, WordNetDispatcher.SYNSET, WordNetDispatcher.SEMRELATIONS, WordNetDispatcher.LEXRELATIONS, WordNetDispatcher.RELATIONS, WordNetDispatcher.POSES, WordNetDispatcher.DOMAINS, WordNetDispatcher.ADJPOSITIONS, WordNetDispatcher.SAMPLES, WordNetDispatcher.DICT, WordNetDispatcher.WORDS_SENSES_SYNSETS, WordNetDispatcher.WORDS_SENSES_CASEDWORDS_SYNSETS, WordNetDispatcher.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS, WordNetDispatcher.SENSES_WORDS, WordNetDispatcher.SENSES_WORDS_BY_SYNSET, WordNetDispatcher.SENSES_SYNSETS_POSES_DOMAINS, WordNetDispatcher.SYNSETS_POSES_DOMAINS, WordNetDispatcher.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET, WordNetDispatcher.SEMRELATIONS_SYNSETS, WordNetDispatcher.SEMRELATIONS_SYNSETS_X, WordNetDispatcher.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET, WordNetDispatcher.LEXRELATIONS_SENSES, WordNetDispatcher.LEXRELATIONS_SENSES_X, WordNetDispatcher.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET, WordNetDispatcher.SENSES_VFRAMES, WordNetDispatcher.SENSES_VTEMPLATES, WordNetDispatcher.SENSES_ADJPOSITIONS, WordNetDispatcher.LEXES_MORPHS, WordNetDispatcher.WORDS_LEXES_MORPHS, WordNetDispatcher.WORDS_LEXES_MORPHS_BY_WORD, WordNetDispatcher.LOOKUP_FTS_WORDS, WordNetDispatcher.LOOKUP_FTS_DEFINITIONS, WordNetDispatcher.LOOKUP_FTS_SAMPLES, WordNetDispatcher.SUGGEST_WORDS, WordNetDispatcher.SUGGEST_FTS_WORDS, WordNetDispatcher.SUGGEST_FTS_DEFINITIONS, WordNetDispatcher.SUGGEST_FTS_SAMPLES,};
	private final String uriLast = "LAST";
	private final String[] projection = {"PROJ1", "PROJ2", "PROJ3"};
	private final String selection = "SEL";
	private final String[] selectionArgs = {"ARG1", "ARG2", "ARG3"};
	private final String sortOrder = "SORT";
	private final Factory factory = s -> "SUBQUERY";

	@Test
	public void queriesLegacyAgainstNew()
	{
		for (int i = 0; i < codes.length; i++)
		{
			int code = codes[i];
			queryLegacyAgainstNew(code, uriLast, projection, selection, selectionArgs, sortOrder);
		}
	}

	@Test
	public void queriesLegacyAgainstProvider()
	{
		for (int i = 0; i < codes.length; i++)
		{
			int code = codes[i];
			queryLegacyAgainstProvider(code, uriLast, projection, selection, selectionArgs, sortOrder);
		}
	}

	@Test
	public void queriesNewAgainstProvider()
	{
		for (int i = 0; i < codes.length; i++)
		{
			int code = codes[i];
			queryNewAgainstProvider(code, uriLast, projection, selection, selectionArgs, sortOrder);
		}
	}

	public void queryLegacyAgainstNew(int code, @NonNull final String uriLast, final String[] projection, @Nullable final String selection, final String[] selectionArgs, final String sortOrder)
	{
		Result r1 = QueriesLegacy.queryLegacy(code, uriLast, projection, selection, selectionArgs, sortOrder, factory);
		Result r2 = QueriesNew.queryNew(code, uriLast, projection, selection, selectionArgs, sortOrder, factory);
		check(code, r1, r2);
	}

	public void queryLegacyAgainstProvider(int code, @NonNull final String uriLast, final String[] projection, @Nullable final String selection, final String[] selectionArgs, final String sortOrder)
	{
		Result r1 = QueriesLegacy.queryLegacy(code, uriLast, projection, selection, selectionArgs, sortOrder, factory);
		Result r2 = queryProvider(code, uriLast, projection, selection, selectionArgs, sortOrder, factory);
		check(code, r1, r2);
	}

	public void queryNewAgainstProvider(int code, @NonNull final String uriLast, final String[] projection, @Nullable final String selection, final String[] selectionArgs, final String sortOrder)
	{
		Result r1 = QueriesNew.queryNew(code, uriLast, projection, selection, selectionArgs, sortOrder, factory);
		Result r2 = queryProvider(code, uriLast, projection, selection, selectionArgs, sortOrder, factory);
		check(code, r1, r2);
	}

	public Result queryProvider(int code, @NonNull final String uriLast, final String[] projection0, @Nullable final String selection0, final String[] selectionArgs0, final String sortOrder0, final Factory subqueryFactory)
	{
		Result r = WordNetDispatcher.queryMain(code, uriLast, projection0, selection0, selectionArgs0);
		if (r == null)
		{
			r = WordNetDispatcher.queryAllRelations(code, projection0, selection0, selectionArgs0, subqueryFactory);
			if (r == null)
			{
				r = WordNetDispatcher.querySearch(code, projection0, selection0, selectionArgs0);
				if (r == null)
				{
					r = WordNetDispatcher.querySuggest(code, uriLast);
				}
			}
		}
		return r;
	}

	private void check(final int code, final Result r1, final Result r2)
	{
		assert equals(r1.table, r2.table) : "Code=" + code + "\n" + r1.table + "\n!=\n" + r2.table;
		assert Arrays.equals(r1.projection, r2.projection) : "Code=" + code + "\n" + Arrays.toString(r1.projection) + "\n!=\n" + Arrays.toString(r2.projection);
		assert equals(r1.selection, r2.selection) : "Code=" + code + "\n" + r1.selection + "\n!= " + r2.selection;
		assert Arrays.equals(r1.selectionArgs, r2.selectionArgs) : "Code=" + code + "\n" + Arrays.toString(r1.selectionArgs) + "\n!=\n" + Arrays.toString(r2.selectionArgs);
		assert equals(r1.groupBy, r2.groupBy) : "Code=" + code + "\n" + r1.groupBy + "\n!=\n" + r2.groupBy;
	}

	private static boolean equals(Object a, Object b)
	{
		return (a == b) || (a != null && a.equals(b));
	}
}