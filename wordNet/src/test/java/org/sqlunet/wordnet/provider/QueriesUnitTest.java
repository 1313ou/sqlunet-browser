package org.sqlunet.wordnet.provider;

import org.junit.Test;
import org.sqlunet.wordnet.provider.WordNetControl.Factory;
import org.sqlunet.wordnet.provider.WordNetControl.Result;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Queries factory, which will execute on the development machine (host).
 */
public class QueriesUnitTest
{
	private final int[] codes = {WordNetControl.WORDS, WordNetControl.WORD, WordNetControl.SENSES, WordNetControl.SENSE, WordNetControl.SYNSETS, WordNetControl.SYNSET, WordNetControl.SEMRELATIONS, WordNetControl.LEXRELATIONS, WordNetControl.RELATIONS, WordNetControl.POSES, WordNetControl.DOMAINS, WordNetControl.ADJPOSITIONS, WordNetControl.SAMPLES, WordNetControl.DICT, WordNetControl.WORDS_SENSES_SYNSETS, WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS, WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS, WordNetControl.SENSES_WORDS, WordNetControl.SENSES_WORDS_BY_SYNSET, WordNetControl.SENSES_SYNSETS_POSES_DOMAINS, WordNetControl.SYNSETS_POSES_DOMAINS, WordNetControl.ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET, WordNetControl.SEMRELATIONS_SYNSETS, WordNetControl.SEMRELATIONS_SYNSETS_X, WordNetControl.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET, WordNetControl.LEXRELATIONS_SENSES, WordNetControl.LEXRELATIONS_SENSES_X, WordNetControl.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET, WordNetControl.SENSES_VFRAMES, WordNetControl.SENSES_VTEMPLATES, WordNetControl.SENSES_ADJPOSITIONS, WordNetControl.LEXES_MORPHS, WordNetControl.WORDS_LEXES_MORPHS, WordNetControl.WORDS_LEXES_MORPHS_BY_WORD, WordNetControl.LOOKUP_FTS_WORDS, WordNetControl.LOOKUP_FTS_DEFINITIONS, WordNetControl.LOOKUP_FTS_SAMPLES, WordNetControl.SUGGEST_WORDS, WordNetControl.SUGGEST_FTS_WORDS, WordNetControl.SUGGEST_FTS_DEFINITIONS, WordNetControl.SUGGEST_FTS_SAMPLES,};
	private final String uriLast = "LAST";
	private final String[] projection = {"PROJ1", "PROJ2", "PROJ3"};
	private final String selection = "SEL";
	private final String[] selectionArgs = {"ARG1", "ARG2", "ARG3"};
	private final String sortOrder = "SORT";
	private final Factory factory = s -> Q.ANYRELATIONS_QUERY.TABLE;

	@Test
	public void queriesLegacyAgainstNew()
	{
		for (int code : codes)
		{
			queryLegacyAgainstNew(code, uriLast, projection, selection, selectionArgs, sortOrder);
		}
	}

	@Test
	public void queriesLegacyAgainstProvider()
	{
		for (int code : codes)
		{
			queryLegacyAgainstProvider(code, uriLast, projection, selection, selectionArgs, sortOrder);
		}
	}

	@Test
	public void queriesNewAgainstProvider()
	{
		for (int code : codes)
		{
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
		Result r2 = queryProvider(code, uriLast, projection, selection, selectionArgs, sortOrder);
		check(code, r1, r2);
	}

	public void queryNewAgainstProvider(int code, @NonNull final String uriLast, final String[] projection, @Nullable final String selection, final String[] selectionArgs, final String sortOrder)
	{
		Result r1 = QueriesNew.queryNew(code, uriLast, projection, selection, selectionArgs, sortOrder, factory);
		Result r2 = queryProvider(code, uriLast, projection, selection, selectionArgs, sortOrder);
		check(code, r1, r2);
	}

	public Result queryProvider(int code, @NonNull final String uriLast, final String[] projection0, @Nullable final String selection0, final String[] selectionArgs0, final String sortOrder0)
	{
		Result r = WordNetControl.queryMain(code, uriLast, projection0, selection0, selectionArgs0);
		if (r == null)
		{
			r = WordNetControl.queryAnyRelations(code, projection0, selection0, selectionArgs0);
			if (r == null)
			{
				r = WordNetControl.querySearch(code, projection0, selection0, selectionArgs0);
				if (r == null)
				{
					r = WordNetControl.querySuggest(code, uriLast);
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