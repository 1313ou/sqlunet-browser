package org.sqlunet.provider;

import org.junit.Test;
import org.sqlunet.provider.XSqlUNetDispatcher.Result;

import java.util.Arrays;

public class QueriesUnitTest
{
	private final int[] codes = {XSqlUNetDispatcher.PREDICATEMATRIX, XSqlUNetDispatcher.PREDICATEMATRIX_VERBNET, XSqlUNetDispatcher.PREDICATEMATRIX_PROPBANK, XSqlUNetDispatcher.PREDICATEMATRIX_FRAMENET, XSqlUNetDispatcher.WORDS_FNWORDS_PBWORDS_VNWORDS, XSqlUNetDispatcher.WORDS_PBWORDS_VNWORDS, XSqlUNetDispatcher.WORDS_VNWORDS_VNCLASSES, XSqlUNetDispatcher.WORDS_VNWORDS_VNCLASSES_U, XSqlUNetDispatcher.WORDS_PBWORDS_PBROLESETS, XSqlUNetDispatcher.WORDS_PBWORDS_PBROLESETS_U, XSqlUNetDispatcher.WORDS_FNWORDS_FNFRAMES_U, XSqlUNetDispatcher.SOURCES,};
	private final String uriLast = "LAST";
	private final String[] projection = {"PROJ1", "PROJ2", "PROJ3"};
	private final String selection = "SEL";
	private final String[] selectionArgs = {"ARG1", "ARG2", "ARG3"};
	private final String sortOrder = "SORT";

	@Test
	public void queriesUnion()
	{
		for (int code : new int[]{XSqlUNetDispatcher.WORDS_VNWORDS_VNCLASSES_U, XSqlUNetDispatcher.WORDS_PBWORDS_PBROLESETS_U, XSqlUNetDispatcher.WORDS_FNWORDS_FNFRAMES_U})
		{
			System.out.println("CODE: " + code);
			Result r = QueriesLegacy.queryLegacy(code, uriLast, projection, selection, selectionArgs);
			System.out.println("table :" + r.table);
			System.out.println("projection :" + Arrays.toString(r.projection));
			System.out.println("selection :" + r.selection);
			System.out.println("args :" + Arrays.toString(r.selectionArgs));
			System.out.println("groupby :" + r.groupBy);
			System.out.println();
		}
	}

	@Test
	public void queriesLegacyAgainstProvider()
	{
		for (int i = 0; i < codes.length; i++)
		{
			int code = codes[i];
			queriesLegacyAgainstProvider(code, uriLast, projection, selection, selectionArgs, sortOrder);
		}
	}

	private void queriesLegacyAgainstProvider(final int code, final String uriLast, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder)
	{
		Result r1 = QueriesLegacy.queryLegacy(code, uriLast, projection, selection, selectionArgs);
		Result r2 = queryProvider(code, uriLast, projection, selection, selectionArgs);
		check(code, r1, r2);
	}

	public static Result queryProvider(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		Result r = queryProviderMain(code, uriLast, projection0, selection0, selectionArgs0);
		return r;
	}

	public static Result queryProviderMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return XSqlUNetDispatcher.queryMain(code, uriLast, projection0, selection0, selectionArgs0);
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
		return (a == b) || (a != null && a.equals(b));
	}
}
