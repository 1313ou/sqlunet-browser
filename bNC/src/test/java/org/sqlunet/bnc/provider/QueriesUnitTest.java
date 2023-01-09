/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.bnc.provider;

import org.junit.Test;
import org.sqlunet.bnc.provider.BNCControl.Result;

import java.util.Arrays;
import java.util.Objects;

public class QueriesUnitTest
{
	private final int[] codes = {BNCControl.BNC, BNCControl.WORDS_BNC};

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

	private void queriesLegacyAgainstProvider(final int code, @SuppressWarnings("SameParameterValue") final String uriLast, final String[] projection, @SuppressWarnings("SameParameterValue") final String selection, final String[] selectionArgs, @SuppressWarnings({"SameParameterValue","unused"}) final String sortOrder)
	{
		Result r1 = QueriesLegacy.queryLegacy(code, uriLast, projection, selection, selectionArgs);
		Result r2 = queryProvider(code, uriLast, projection, selection, selectionArgs);
		check(code, r1, r2);
	}

	public static Result queryProvider(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return queryProviderMain(code, uriLast, projection0, selection0, selectionArgs0);
	}

	public static Result queryProviderMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return BNCControl.queryMain(code, uriLast, projection0, selection0, selectionArgs0);
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
