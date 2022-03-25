package org.sqlunet.syntagnet.provider;

import org.junit.Test;
import org.sqlunet.syntagnet.provider.SyntagNetDispatcher.Result;

import java.util.Arrays;

public class QueriesUnitTest
{
	private final int[] codes = {SyntagNetDispatcher.COLLOCATIONS, SyntagNetDispatcher.COLLOCATIONS_X};

	private final String uriLast = "LAST";
	private final String[] projection = {"PROJ1", "PROJ2", "PROJ3"};
	private final String selection = "SEL";
	private final String[] selectionArgs = {"ARG1", "ARG2", "ARG3"};
	private final String sortOrder = "SORT";

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
		Result r1 = queryLegacy(code, uriLast, projection, selection, selectionArgs);
		Result r2 = queryProvider(code, uriLast, projection, selection, selectionArgs);
		check(code, r1, r2);
	}

	private void queryXAgainstY(final int code, final String uriLast, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder)
	{
	}

	public static Result queryProvider(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		Result r = queryProviderMain(code, uriLast, projection0, selection0, selectionArgs0);
		return r;
	}

	public static Result queryLegacy(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		Result r = queryLegacyMain(code, uriLast, projection0, selection0, selectionArgs0);
		return r;
	}

	public static Result queryProviderMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return SyntagNetDispatcher.queryMain(code, uriLast, projection0, selection0, selectionArgs0);
	}

	public static Result queryLegacyMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String[] projection = projection0;
		String selection = selection0;
		String[] selectionArgs = selectionArgs0;
		String groupBy = null;

		switch (code)
		{
			case SyntagNetDispatcher.COLLOCATIONS:
				table = SyntagNetContract.SnCollocations.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += SyntagNetContract.SnCollocations.COLLOCATIONID + " = ?";
				break;

			// J O I N S

			case SyntagNetDispatcher.COLLOCATIONS_X:
				table = "sn_syntagms " + //
						"JOIN words AS " + SyntagNetContract.W1 + " ON (word1id = " + SyntagNetContract.W1 + ".wordid) " + //
						"JOIN words AS " + SyntagNetContract.W2 + " ON (word2id = " + SyntagNetContract.W2 + ".wordid) " + //
						"JOIN synsets AS " + SyntagNetContract.S1 + " ON (synset1id = " + SyntagNetContract.S1 + ".synsetid) " + //
						"JOIN synsets AS " + SyntagNetContract.S2 + " ON (synset2id = " + SyntagNetContract.S2 + ".synsetid)";
				break;

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, groupBy);
	}

	private void check(final int code, final Result r1, final Result r2)
	{
		assert equals(r1.table, r2.table) : "Code=" + code + " " + r1.table + " != " + r2.table;
		assert Arrays.equals(r1.projection, r2.projection) : "Code=" + code + " " + Arrays.toString(r1.projection) + " != " + Arrays.toString(r2.projection);
		assert equals(r1.selection, r2.selection) : "Code=" + code + " " + r1.selection + " != " + r2.selection;
		assert Arrays.equals(r1.selectionArgs, r2.selectionArgs) : "Code=" + code + " " + Arrays.toString(r1.selectionArgs) + " != " + Arrays.toString(r2.selectionArgs);
		assert equals(r1.groupBy, r2.groupBy) : "Code=" + code + " " + r1.groupBy + " != " + r2.groupBy;
	}

	private static boolean equals(Object a, Object b)
	{
		return (a == b) || (a != null && a.equals(b));
	}
}
