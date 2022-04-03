package org.sqlunet.verbnet.provider;

import android.app.SearchManager;

import org.junit.Test;
import org.sqlunet.verbnet.provider.VerbNetDispatcher.Result;

import java.util.Arrays;

public class QueriesUnitTest
{
	private final int[] codes = {VerbNetDispatcher.VNCLASS1, VerbNetDispatcher.VNCLASSES, VerbNetDispatcher.VNCLASSES_X_BY_VNCLASS, VerbNetDispatcher.WORDS_VNCLASSES, VerbNetDispatcher.VNCLASSES_VNMEMBERS_X_BY_WORD, VerbNetDispatcher.VNCLASSES_VNROLES_X_BY_VNROLE, VerbNetDispatcher.VNCLASSES_VNFRAMES_X_BY_VNFRAME, VerbNetDispatcher.LOOKUP_FTS_EXAMPLES, VerbNetDispatcher.LOOKUP_FTS_EXAMPLES_X, VerbNetDispatcher.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE, VerbNetDispatcher.SUGGEST_WORDS, VerbNetDispatcher.SUGGEST_FTS_WORDS,};
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

	public static Result queryLegacy(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		Result r = queryLegacyMain(code, uriLast, projection0, selection0, selectionArgs0);
		if (r == null)
		{
			r = queryLegacySearch(code, projection0, selection0, selectionArgs0);
			if (r == null)
			{
				r = queryLegacySuggest(code, uriLast);
			}
		}
		return r;
	}

	public static Result queryProviderMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return VerbNetDispatcher.queryMain(code, uriLast, projection0, selection0, selectionArgs0);
	}

	public static Result queryProviderSearch(final int code, final String[] projection0, final String selection0, final String[] selectionArgs0)
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
		String[] selectionArgs = selectionArgs0;
		String groupBy = null;

		switch (code)
		{
			// T A B L E S

			case VerbNetDispatcher.VNCLASSES:
				table = VerbNetContract.VnClasses.TABLE;
				break;

			case VerbNetDispatcher.VNCLASSES_X_BY_VNCLASS:
				groupBy = "classid";
				table = "vn_classes " + //
						"LEFT JOIN vn_members_groupings USING (classid) " + //
						"LEFT JOIN vn_groupings USING (groupingid)";
				break;

			// I T E M S

			case VerbNetDispatcher.VNCLASS1:
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

			// J O I N S

			case VerbNetDispatcher.WORDS_VNCLASSES:
				table = "words " + //
						"INNER JOIN vn_words USING (wordid) " + //
						"INNER JOIN vn_members_senses USING (vnwordid) " + //
						"LEFT JOIN vn_classes USING (classid)";
				break;

			case VerbNetDispatcher.VNCLASSES_VNMEMBERS_X_BY_WORD:
				groupBy = "vnwordid";
				table = "words " +
						"INNER JOIN vn_members_senses USING (wordid) " +
						"LEFT JOIN vn_members_groupings USING (classid, vnwordid) " + //
						"LEFT JOIN vn_groupings USING (groupingid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				break;

			case VerbNetDispatcher.VNCLASSES_VNROLES_X_BY_VNROLE:
				groupBy = "roleid";
				table = "vn_classes " + //
						"INNER JOIN vn_roles USING (classid) " + //
						"INNER JOIN vn_roletypes USING (roletypeid) " + //
						"LEFT JOIN vn_restrs USING (restrsid)";
				break;

			case VerbNetDispatcher.VNCLASSES_VNFRAMES_X_BY_VNFRAME:
				groupBy = "frameid";
				table = "vn_classes_frames " + //
						"INNER JOIN vn_frames USING (frameid) " + //
						"LEFT JOIN vn_framenames USING (framenameid) " + //
						"LEFT JOIN vn_framesubnames USING (framesubnameid) " + //
						"LEFT JOIN vn_syntaxes USING (syntaxid) " + //
						"LEFT JOIN vn_semantics USING (semanticsid) " + //
						"LEFT JOIN vn_frames_examples USING (frameid) " + //
						"LEFT JOIN vn_examples USING (exampleid)";
				break;

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, groupBy);
	}

	public static Result queryLegacySearch(final int code, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String groupBy = null;

		switch (code)
		{
			case VerbNetDispatcher.LOOKUP_FTS_EXAMPLES:
				table = "vn_examples_example_fts4";
				break;

			case VerbNetDispatcher.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE:
				groupBy = "exampleid";
				//$FALL-THROUGH$
				//noinspection fallthrough

			case VerbNetDispatcher.LOOKUP_FTS_EXAMPLES_X:
				table = "vn_examples_example_fts4 " + //
						"LEFT JOIN vn_classes USING (classid)";
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
				table = "vn_words INNER JOIN words USING (wordid)";
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
				table = "vn_words_word_fts4";
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
