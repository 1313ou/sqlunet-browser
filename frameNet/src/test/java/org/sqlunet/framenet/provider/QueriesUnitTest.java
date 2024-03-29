/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.framenet.provider;

import org.junit.Test;
import org.sqlunet.framenet.provider.FrameNetControl.Result;

import java.util.Arrays;
import java.util.Objects;

import androidx.annotation.Nullable;

public class QueriesUnitTest
{
	private final int[] codes = {FrameNetControl.LEXUNIT, FrameNetControl.LEXUNITS, FrameNetControl.LEXUNITS_X_BY_LEXUNIT, FrameNetControl.FRAME, FrameNetControl.FRAMES, FrameNetControl.FRAMES_X_BY_FRAME, FrameNetControl.FRAMES_RELATED, FrameNetControl.SENTENCE, FrameNetControl.SENTENCES, FrameNetControl.ANNOSET, FrameNetControl.ANNOSETS, FrameNetControl.SENTENCES_LAYERS_X, FrameNetControl.ANNOSETS_LAYERS_X, FrameNetControl.PATTERNS_LAYERS_X, FrameNetControl.VALENCEUNITS_LAYERS_X, FrameNetControl.PATTERNS_SENTENCES, FrameNetControl.VALENCEUNITS_SENTENCES, FrameNetControl.GOVERNORS_ANNOSETS, FrameNetControl.WORDS_LEXUNITS_FRAMES, FrameNetControl.LEXUNITS_OR_FRAMES, FrameNetControl.FRAMES_FES, FrameNetControl.FRAMES_FES_BY_FE, FrameNetControl.LEXUNITS_SENTENCES, FrameNetControl.LEXUNITS_SENTENCES_BY_SENTENCE, FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS, FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE, FrameNetControl.LEXUNITS_GOVERNORS, FrameNetControl.LEXUNITS_REALIZATIONS, FrameNetControl.LEXUNITS_REALIZATIONS_BY_REALIZATION, FrameNetControl.LEXUNITS_GROUPREALIZATIONS, FrameNetControl.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN, FrameNetControl.LOOKUP_FTS_WORDS, FrameNetControl.LOOKUP_FTS_SENTENCES, FrameNetControl.LOOKUP_FTS_SENTENCES_X, FrameNetControl.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE, FrameNetControl.SUGGEST_WORDS, FrameNetControl.SUGGEST_FTS_WORDS,};

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

	private void queriesLegacyAgainstProvider(final int code, @SuppressWarnings("SameParameterValue") final String uriLast, final String[] projection, @SuppressWarnings("SameParameterValue") final String selection, final String[] selectionArgs, @SuppressWarnings({"SameParameterValue", "unused"}) final String sortOrder)
	{
		Result r1 = QueriesLegacy.queryLegacy(code, uriLast, projection, selection, selectionArgs);
		Result r2 = queryProvider(code, uriLast, projection, selection, selectionArgs);
		check(code, r1, r2);
	}

	@Nullable
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

	@Nullable
	public static Result queryProviderMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return FrameNetControl.queryMain(code, uriLast, projection0, selection0, selectionArgs0);
	}

	@Nullable
	public static Result queryProviderSearch(final int code, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return FrameNetControl.querySearch(code, projection0, selection0, selectionArgs0);
	}

	@Nullable
	public static Result queryProviderSuggest(final int code, final String uriLast)
	{
		return FrameNetControl.querySuggest(code, uriLast);
	}

	private void check(final int code, @Nullable final Result r1, @Nullable final Result r2)
	{
		assert r1 != null;
		assert r2 != null;
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
