package org.sqlunet.verbnet.provider;

import android.app.SearchManager;

import org.junit.Test;
import org.sqlunet.verbnet.provider.VerbNetDispatcher.Result;

import java.util.Arrays;

public class QueriesUnitTest
{
	private final int[] codes = {};
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
		Result r1 = queryLegacyMain(code, uriLast, projection, selection, selectionArgs);
		Result r2 = queryProviderMain(code, uriLast, projection, selection, selectionArgs);
		check(code, r1, r2);
	}

	private void queryXAgainstY(final int code, final String uriLast, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder)
	{
	}

	public static Result queryProviderMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return VerbNetDispatcher.queryMain(code, uriLast, projection0, selection0, selectionArgs0);
	}

	public static Result queryProviderSearch(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return VerbNetDispatcher.querySearch(code, projection0, selection0, selectionArgs0);
	}

	public static Result queryProviderSuggest(final int code, final String uriLast)
	{
		return VerbNetDispatcher.querySuggest(code, uriLast);
	}

	public static Result queryLegacyMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String[] projection = projection0;
		String selection = selection0;
		String groupBy = null;

		switch (code)
		{
			case VerbNetDispatcher.VNCLASS:
				table = VerbNetContract.VnClasses.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += VerbNetContract.VnClasses.CLASSID + " = " + uriLast;
				break;

			case VerbNetDispatcher.VNCLASSES:
				table = VerbNetContract.VnClasses.TABLE;
				break;

			case VerbNetDispatcher.VNCLASSES_X_BY_VNCLASS:
				groupBy = "classid";
				table = "vnclasses " + //
						"LEFT JOIN vngroupingmaps USING (classid) " + //
						"LEFT JOIN vngroupings USING (groupingid)";
				break;

			// J O I N S

			case VerbNetDispatcher.WORDS_VNCLASSES:
				table = "words " + //
						"INNER JOIN vnwords USING (wordid) " + //
						"INNER JOIN vnclassmembersenses USING (vnwordid) " + //
						"LEFT JOIN vnclasses USING (classid)";
				break;

			case VerbNetDispatcher.VNCLASSES_VNMEMBERS_X_BY_WORD:
				groupBy = "vnwordid";
				table = "vnclassmembersenses " + //
						"LEFT JOIN vnwords USING (vnwordid) " + //
						"LEFT JOIN vngroupingmaps USING (classid, vnwordid) " + //
						"LEFT JOIN vngroupings USING (groupingid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				break;

			case VerbNetDispatcher.VNCLASSES_VNROLES_X_BY_VNROLE:
				groupBy = "roleid";
				table = "vnrolemaps " + //
						"INNER JOIN vnroles USING (roleid) " + //
						"INNER JOIN vnroletypes USING (roletypeid) " + //
						"LEFT JOIN vnrestrs USING (restrsid)";
				break;

			case VerbNetDispatcher.VNCLASSES_VNFRAMES_X_BY_VNFRAME:
				groupBy = "frameid";
				table = "vnframemaps " + //
						"INNER JOIN vnframes USING (frameid) " + //
						"LEFT JOIN vnframenames USING (nameid) " + //
						"LEFT JOIN vnframesubnames USING (subnameid) " + //
						"LEFT JOIN vnsyntaxes USING (syntaxid) " + //
						"LEFT JOIN vnsemantics USING (semanticsid) " + //
						"LEFT JOIN vnexamplemaps USING (frameid) " + //
						"LEFT JOIN vnexamples USING (exampleid)";
				break;

			// L O O K U P

			case VerbNetDispatcher.LOOKUP_FTS_EXAMPLES:
				table = "vnexamples_example_fts4";
				break;

			case VerbNetDispatcher.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE:
				groupBy = "exampleid";
				//$FALL-THROUGH$
				//noinspection fallthrough

			case VerbNetDispatcher.LOOKUP_FTS_EXAMPLES_X:
				table = "vnexamples_example_fts4 " + //
						"LEFT JOIN vnclasses USING (classid)";
				break;

			// S U G G E S T

			case VerbNetDispatcher.SUGGEST_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "vnwords";
				projection = new String[]{"vnwordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY}; //
				selection = "word LIKE ? || '%'";
			}

			case VerbNetDispatcher.SUGGEST_FTS_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "vnwords_word_fts4";
				projection = new String[]{"vnwordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				selection = "word MATCH ?";
			}

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs0, groupBy);
	}

	public static Result queryLegacySearch(final int code, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String groupBy = null;

		switch (code)
		{
			case VerbNetDispatcher.LOOKUP_FTS_EXAMPLES:
				table = "vnexamples_example_fts4";
				break;

			case VerbNetDispatcher.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE:
				groupBy = "exampleid";
				//$FALL-THROUGH$
				//noinspection fallthrough

			case VerbNetDispatcher.LOOKUP_FTS_EXAMPLES_X:
				table = "vnexamples_example_fts4 " + //
						"LEFT JOIN vnclasses USING (classid)";
				break;

			default:
				return null;
		}
		return new Result(table, projection0, selection0, selectionArgs0, groupBy);
	}

	public static Result queryLegacySuggest(final int code, final String uriLast)
	{
		String table;
		String[] projection;
		String selection;
		String[] selectionArgs;

		switch (code)
		{
			case VerbNetDispatcher.SUGGEST_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "vnwords";
				projection = new String[]{"vnwordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY}; //
				selection = "word LIKE ? || '%'";
				selectionArgs = new String[]{uriLast};
				break;
			}

			case VerbNetDispatcher.SUGGEST_FTS_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "vnwords_word_fts4";
				projection = new String[]{"vnwordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				selection = "word MATCH ?";
				selectionArgs = new String[]{uriLast + '*'};
				break;
			}

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, null);
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
