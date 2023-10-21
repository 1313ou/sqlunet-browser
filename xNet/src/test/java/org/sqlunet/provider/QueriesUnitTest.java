/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.provider;

import org.junit.Test;
import org.sqlunet.provider.XNetControl.Result;

import java.util.Arrays;
import java.util.Objects;

import androidx.annotation.Nullable;

public class QueriesUnitTest
{
	private final int[] codes = {XNetControl.PREDICATEMATRIX, XNetControl.PREDICATEMATRIX_VERBNET, XNetControl.PREDICATEMATRIX_PROPBANK, XNetControl.PREDICATEMATRIX_FRAMENET, XNetControl.WORDS_FNWORDS_PBWORDS_VNWORDS, XNetControl.WORDS_PBWORDS_VNWORDS, XNetControl.WORDS_VNWORDS_VNCLASSES, XNetControl.WORDS_VNWORDS_VNCLASSES_U, XNetControl.WORDS_PBWORDS_PBROLESETS, XNetControl.WORDS_PBWORDS_PBROLESETS_U, XNetControl.WORDS_FNWORDS_FNFRAMES_U, XNetControl.SOURCES, //
			XNetControl.WORDS_VNWORDS_VNCLASSES_1, XNetControl.WORDS_VNWORDS_VNCLASSES_2, XNetControl.WORDS_VNWORDS_VNCLASSES_1U2, XNetControl.WORDS_VNWORDS_VNCLASSES_U, //
			XNetControl.WORDS_PBWORDS_PBROLESETS_1, XNetControl.WORDS_PBWORDS_PBROLESETS_2, XNetControl.WORDS_PBWORDS_PBROLESETS_1U2, XNetControl.WORDS_PBWORDS_PBROLESETS_U, //
			XNetControl.WORDS_FNWORDS_FNFRAMES_1, XNetControl.WORDS_FNWORDS_FNFRAMES_2, XNetControl.WORDS_FNWORDS_FNFRAMES_1U2, XNetControl.WORDS_FNWORDS_FNFRAMES_U,};

	private final String uriLast = "LAST";
	private final String[] projection = {"PROJ1", "PROJ2", "PROJ3"};
	private final String selection = "SEL";
	private final String[] selectionArgs = {"ARG1", "ARG2", "ARG3"};
	private final String sortOrder = "SORT";

	@Test
	public void queriesUnion()
	{
		for (int code : new int[]{XNetControl.WORDS_VNWORDS_VNCLASSES_U, XNetControl.WORDS_PBWORDS_PBROLESETS_U, XNetControl.WORDS_FNWORDS_FNFRAMES_U})
		{
			System.out.println("CODE: " + code);
			Result r = QueriesLegacy.queryLegacy(code, uriLast, projection, selection, selectionArgs);
			assert r != null;
			System.out.println("table :" + r.table);
			System.out.println("projection :" + Arrays.toString(r.projection));
			System.out.println("selection :" + r.selection);
			System.out.println("args :" + Arrays.toString(r.selectionArgs));
			System.out.println("groupby :" + r.groupBy);
			System.out.println();
		}
	}

	@Test
	public void queriesUnionLegacyAgainstProvider()
	{
		for (int code : new int[]{XNetControl.WORDS_VNWORDS_VNCLASSES_U, XNetControl.WORDS_PBWORDS_PBROLESETS_U, XNetControl.WORDS_FNWORDS_FNFRAMES_U})
		{
			queriesLegacyAgainstProvider(code, uriLast, projection, selection, selectionArgs, sortOrder);
		}
	}

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

	@Nullable
	public static Result queryProvider(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return queryProviderMain(code, uriLast, projection0, selection0, selectionArgs0);
	}

	@Nullable
	public static Result queryProviderMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return XNetControl.queryMain(code, uriLast, projection0, selection0, selectionArgs0);
	}

	private void check(final int code, @Nullable final Result r1, @Nullable final Result r2)
	{
		assert r1 != null;
		assert r2 != null;
		assert equals(r1.table, r2.table) : "Code=" + code + " table " + "\n" + r1.table + "\n!=\n" + r2.table;
		assert Arrays.equals(r1.projection, r2.projection) : "Code=" + code + " projection " + "\n" + Arrays.toString(r1.projection) + "\n!=\n" + Arrays.toString(r2.projection);
		assert equals(r1.selection, r2.selection) : "Code=" + code + " selection " + "\n" + r1.selection + "\n!=\n" + r2.selection;
		assert Arrays.equals(r1.selectionArgs, r2.selectionArgs) : "Code=" + code + " args " + "\n" + Arrays.toString(r1.selectionArgs) + "\n!=\n" + Arrays.toString(r2.selectionArgs);
		assert equals(r1.groupBy, r2.groupBy) : "Code=" + code + " group by " + "\n" + r1.groupBy + "\n!=\n" + r2.groupBy;
	}

	private static boolean equals(Object a, Object b)
	{
		return Objects.equals(a, b);
	}
}
