package org.sqlunet.propbank.provider;

import android.app.SearchManager;

import org.sqlunet.propbank.provider.PropBankControl.Result;

public class QueriesLegacy
{
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

	public static Result queryLegacyMain(final int code,  @SuppressWarnings("unused") final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String selection = selection0;
		String groupBy = null;

		switch (code)
		{
			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case PropBankControl.PBROLESET:
				table = PropBankContract.PbRoleSets.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += PropBankContract.PbRoleSets.ROLESETID + " = ?";
				break;

			case PropBankControl.PBROLESETS:
				table = PropBankContract.PbRoleSets.TABLE;
				break;

			// J O I N S

			case PropBankControl.PBROLESETS_X_BY_ROLESET:
				groupBy = PropBankContract.PbRoleSets_X.ROLESETID;
				//$FALL-THROUGH$
				//noinspection fallthrough

			case PropBankControl.PBROLESETS_X:
				table = "pb_rolesets " + //
						"LEFT JOIN pb_members AS " + PropBankContract.AS_MEMBERS + " USING (rolesetid) " + //
						"LEFT JOIN pb_words AS " + PropBankContract.AS_PBWORDS + " USING (pbwordid) " + //
						"LEFT JOIN words AS " + PropBankContract.AS_WORDs + " USING (wordid)";
				break;

			case PropBankControl.WORDS_PBROLESETS:
				table = "words " + //
						"INNER JOIN pb_words USING (wordid) " + //
						"INNER JOIN pb_rolesets USING (pbwordid)";
				break;

			case PropBankControl.PBROLESETS_PBROLES:
				table = "pb_rolesets " + //
						"INNER JOIN pb_roles USING (rolesetid) " + //
						"LEFT JOIN pb_argtypes USING (argtypeid) " + //
						"LEFT JOIN pb_funcs USING (funcid) " + //
						"LEFT JOIN pb_thetas USING (thetaid)";
				break;

			case PropBankControl.PBROLESETS_PBEXAMPLES_BY_EXAMPLE:
				groupBy = PropBankContract.AS_EXAMPLES + ".exampleid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case PropBankControl.PBROLESETS_PBEXAMPLES:
				table = "pb_rolesets " + //
						"INNER JOIN pb_examples AS " + PropBankContract.AS_EXAMPLES + " USING (rolesetid) " + //
						"LEFT JOIN pb_rels AS " + PropBankContract.AS_RELATIONS + " USING (exampleid) " + //
						"LEFT JOIN pb_args AS " + PropBankContract.AS_ARGS + " USING (exampleid) " + //
						"LEFT JOIN pb_argtypes USING (argtypeid) " + //
						"LEFT JOIN pb_funcs AS " + PropBankContract.AS_FUNCS + " ON (" + PropBankContract.AS_ARGS + ".funcid = " + PropBankContract.AS_FUNCS + ".funcid) " + //
						"LEFT JOIN pb_aspects USING (aspectid) " + //
						"LEFT JOIN pb_forms USING (formid) " + //
						"LEFT JOIN pb_tenses USING (tenseid) " + //
						"LEFT JOIN pb_voices USING (voiceid) " + //
						"LEFT JOIN pb_persons USING (personid) " + //
						"LEFT JOIN pb_roles USING (rolesetid,argtypeid) " + //
						"LEFT JOIN pb_thetas USING (thetaid)";
				break;

			default:
				return null;
		}
		return new Result(table, projection0, selection, selectionArgs0, groupBy);
	}

	public static Result queryLegacySearch(final int code, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String groupBy = null;

		switch (code)
		{
			case PropBankControl.LOOKUP_FTS_EXAMPLES:
				table = "pb_examples_text_fts4";
				break;
			case PropBankControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE:
				groupBy = "exampleid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case PropBankControl.LOOKUP_FTS_EXAMPLES_X:
				table = "pb_examples_text_fts4 " + //
						"LEFT JOIN pb_rolesets USING (rolesetid)";
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
			case PropBankControl.SUGGEST_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "pb_words INNER JOIN words USING (wordid)";
				projection = new String[]{"pbwordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				selection = "word LIKE ? || '%'";
				selectionArgs = new String[]{uriLast};
				break;
			}

			case PropBankControl.SUGGEST_FTS_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "pb_words_word_fts4";
				projection = new String[]{"pbwordid AS _id", //
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
}
