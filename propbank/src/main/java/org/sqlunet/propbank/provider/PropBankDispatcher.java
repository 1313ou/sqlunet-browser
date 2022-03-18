/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.propbank.provider;

import android.app.SearchManager;

import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets;
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_X;

/**
 * PropBank provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PropBankDispatcher
{
	// table codes
	static final int PBROLESET = 10;
	static final int PBROLESETS = 11;

	// join codes
	static final int PBROLESETS_X = 100;
	static final int PBROLESETS_X_BY_ROLESET = 101;
	static final int WORDS_PBROLESETS = 110;
	static final int PBROLESETS_PBROLES = 120;
	static final int PBROLESETS_PBEXAMPLES = 130;
	static final int PBROLESETS_PBEXAMPLES_BY_EXAMPLE = 131;

	// search text codes
	static final int LOOKUP_FTS_EXAMPLES = 501;
	static final int LOOKUP_FTS_EXAMPLES_X = 511;
	static final int LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE = 512;

	// suggest
	static final int SUGGEST_WORDS = 601;
	static final int SUGGEST_FTS_WORDS = 602;

	static public class Result
	{
		final String table;
		final String[] projection;
		final String selection;
		final String[] selectionArgs;
		final String groupBy;

		public Result(final String table, final String[] projection, final String selection, final String[] selectionArgs, final String groupBy)
		{
			this.table = table;
			this.projection = projection;
			this.selection = selection;
			this.selectionArgs = selectionArgs;
			this.groupBy = groupBy;
		}
	}

	public static Result queryMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String selection = selection0;
		String groupBy = null;

		switch (code)
		{
			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case PBROLESET:
				table = PbRoleSets.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += PbRoleSets.ROLESETID + " = ?";
				break;

			case PBROLESETS:
				table = PbRoleSets.TABLE;
				break;

			// J O I N S

			case PBROLESETS_X_BY_ROLESET:
				groupBy = PbRoleSets_X.ROLESETID;
				//$FALL-THROUGH$
				//noinspection fallthrough
			case PBROLESETS_X:
				table = "pbrolesets " + //
						"LEFT JOIN pbrolesetmembers AS " + PropBankContract.MEMBER + " USING (rolesetid) " + //
						"LEFT JOIN pbwords AS " + PropBankContract.WORD + " ON " + PropBankContract.MEMBER + ".pbwordid = " + PropBankContract.WORD + ".pbwordid";
				break;

			case WORDS_PBROLESETS:
				table = "words " + //
						"INNER JOIN pbwords USING (wordid) " + //
						"INNER JOIN pbrolesets USING (pbwordid)";
				break;

			case PBROLESETS_PBROLES:
				table = "pbrolesets " + //
						"INNER JOIN pbroles USING (rolesetid) " + //
						"LEFT JOIN pbfuncs USING (func) " + //
						"LEFT JOIN pbvnthetas USING (theta)";
				break;

			case PBROLESETS_PBEXAMPLES_BY_EXAMPLE:
				groupBy = PropBankContract.EXAMPLE + ".exampleid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case PBROLESETS_PBEXAMPLES:
				table = "pbrolesets " + //
						"INNER JOIN pbexamples AS " + PropBankContract.EXAMPLE + " USING (rolesetid) " + //
						"LEFT JOIN pbrels AS " + PropBankContract.REL + " USING (exampleid) " + //
						"LEFT JOIN pbargs AS " + PropBankContract.ARG + " USING (exampleid) " + //
						"LEFT JOIN pbfuncs AS " + PropBankContract.FUNC + " ON (" + PropBankContract.ARG + ".func = " + PropBankContract.FUNC + ".func) " + //
						"LEFT JOIN pbaspects USING (aspect) " + //
						"LEFT JOIN pbforms USING (form) " + //
						"LEFT JOIN pbtenses USING (tense) " + //
						"LEFT JOIN pbvoices USING (voice) " + //
						"LEFT JOIN pbpersons USING (person) " + //
						"LEFT JOIN pbroles USING (rolesetid,narg) " + //
						"LEFT JOIN pbvnthetas USING (theta)";
				break;

			default:
				return null;
		}
		return new Result(table, projection0, selection0, selectionArgs0, groupBy);
	}

	public static Result querySearch(final int code, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String groupBy = null;

		switch (code)
		{
			case LOOKUP_FTS_EXAMPLES:
				table = "pbexamples_text_fts4";
				break;
			case LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE:
				groupBy = "exampleid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case LOOKUP_FTS_EXAMPLES_X:
				table = "pbexamples_text_fts4 " + //
						"LEFT JOIN pbrolesets USING (rolesetid)";
				break;

			default:
				return null;
		}
		return new Result(table, projection0, selection0, selectionArgs0, groupBy);
	}

	public static Result querySuggest(final int code, final String uriLast)
	{
		String table;
		String[] projection;
		String selection;
		String[] selectionArgs;

		switch (code)
		{
			case SUGGEST_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "pbwords";
				projection = new String[]{"pbwordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				selection = "word LIKE ? || '%'";
				selectionArgs = new String[]{uriLast};
				break;
			}

			case SUGGEST_FTS_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "pbwords_word_fts4";
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
