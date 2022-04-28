package org.sqlunet.framenet.provider;

import org.junit.Test;
import org.sqlunet.framenet.provider.FrameNetDispatcher.Result;

import java.util.Arrays;
import java.util.Objects;

public class QueriesUnitTest
{
	private final int[] codes = {FrameNetDispatcher.LEXUNIT, FrameNetDispatcher.LEXUNITS, FrameNetDispatcher.LEXUNITS_X_BY_LEXUNIT, FrameNetDispatcher.FRAME, FrameNetDispatcher.FRAMES, FrameNetDispatcher.FRAMES_X_BY_FRAME, FrameNetDispatcher.FRAMES_RELATED, FrameNetDispatcher.SENTENCE, FrameNetDispatcher.SENTENCES, FrameNetDispatcher.ANNOSET, FrameNetDispatcher.ANNOSETS, FrameNetDispatcher.SENTENCES_LAYERS_X, FrameNetDispatcher.ANNOSETS_LAYERS_X, FrameNetDispatcher.PATTERNS_LAYERS_X, FrameNetDispatcher.VALENCEUNITS_LAYERS_X, FrameNetDispatcher.PATTERNS_SENTENCES, FrameNetDispatcher.VALENCEUNITS_SENTENCES, FrameNetDispatcher.GOVERNORS_ANNOSETS, FrameNetDispatcher.WORDS_LEXUNITS_FRAMES, FrameNetDispatcher.LEXUNITS_OR_FRAMES, FrameNetDispatcher.FRAMES_FES, FrameNetDispatcher.FRAMES_FES_BY_FE, FrameNetDispatcher.LEXUNITS_SENTENCES, FrameNetDispatcher.LEXUNITS_SENTENCES_BY_SENTENCE, FrameNetDispatcher.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS, FrameNetDispatcher.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE, FrameNetDispatcher.LEXUNITS_GOVERNORS, FrameNetDispatcher.LEXUNITS_REALIZATIONS, FrameNetDispatcher.LEXUNITS_REALIZATIONS_BY_REALIZATION, FrameNetDispatcher.LEXUNITS_GROUPREALIZATIONS, FrameNetDispatcher.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN, FrameNetDispatcher.LOOKUP_FTS_WORDS, FrameNetDispatcher.LOOKUP_FTS_SENTENCES, FrameNetDispatcher.LOOKUP_FTS_SENTENCES_X, FrameNetDispatcher.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE, FrameNetDispatcher.SUGGEST_WORDS, FrameNetDispatcher.SUGGEST_FTS_WORDS,};

	@SuppressWarnings("FieldCanBeLocal")
	private final String uriLast = "LAST";
	private final String[] projection = {"PROJ1", "PROJ2", "PROJ3"};
	@SuppressWarnings("FieldCanBeLocal")
	private final String selection = "SEL";
	private final String[] selectionArgs = {"ARG1", "ARG2", "ARG3"};
	@SuppressWarnings("FieldCanBeLocal")
	private final String sortOrder = "SORT";

	@Test
	public void queriesLegacyAgainstProvider()
	{
		for (int code : codes)
		{
			queriesLegacyAgainstProvider(code, uriLast, projection, selection, selectionArgs, sortOrder);
		}
	}

	private void queriesLegacyAgainstProvider(final int code, @SuppressWarnings("SameParameterValue") final String uriLast, final String[] projection, @SuppressWarnings("SameParameterValue") final String selection, final String[] selectionArgs, @SuppressWarnings("SameParameterValue") final String sortOrder)
	{
		Result r1 = QueriesLegacy.queryLegacy(code, uriLast, projection, selection, selectionArgs);
		Result r2 = queryProvider(code, uriLast, projection, selection, selectionArgs);
		check(code, r1, r2);
	}

	public static Result queryProvider(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		Result r = queryProviderMain(code, uriLast, projection0, selection0, selectionArgs0);
		if (r == null)
		{
			r = queryProviderSearch(code, projection0, selection0, selectionArgs0);
			if (r == null)
			{
				r = queryProviderSuggest(code, uriLast);
			}
		}
		return r;
	}

	public static Result queryProviderMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return FrameNetDispatcher.queryMain(code, uriLast, projection0, selection0, selectionArgs0);
	}

	public static Result queryProviderSearch(final int code, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return FrameNetDispatcher.querySearch(code, projection0, selection0, selectionArgs0);
	}

	public static Result queryProviderSuggest(final int code, final String uriLast)
	{
		return FrameNetDispatcher.querySuggest(code, uriLast);
	}

	private void check(final int code, final Result r1, final Result r2)
	{
		assert equals(r1.table, r2.table) : "Code=" + code + "\n" + r1.table + "\n!=\n" + r2.table;
		assert Arrays.equals(r1.projection, r2.projection) : "Code=" + code + "\n" + Arrays.toString(r1.projection) + "\n!=\n" + Arrays.toString(r2.projection);
		assert equals(r1.selection, r2.selection) : "Code=" + code + "\n" + r1.selection + "\n!=\n" + r2.selection;
		assert Arrays.equals(r1.selectionArgs, r2.selectionArgs) : "Code=" + code + "\n" + Arrays.toString(r1.selectionArgs) + "\n!=\n" + Arrays.toString(r2.selectionArgs);
		assert equals(r1.groupBy, r2.groupBy) : "Code=" + code + "\n" + r1.groupBy + "\n!=\n" + r2.groupBy;
	}

	private static boolean equals(Object a, Object b)
	{
		return Objects.equals(a, b);
	}
}
