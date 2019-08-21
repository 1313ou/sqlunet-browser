/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.propbank.provider;

import android.app.SearchManager;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.propbank.provider.PropBankContract.Lookup_PbExamples;
import org.sqlunet.propbank.provider.PropBankContract.Lookup_PbExamples_X;
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets;
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_PbExamples;
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_PbRoles;
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_X;
import org.sqlunet.propbank.provider.PropBankContract.PbWords;
import org.sqlunet.propbank.provider.PropBankContract.Suggest_FTS_PbWords;
import org.sqlunet.propbank.provider.PropBankContract.Suggest_PbWords;
import org.sqlunet.propbank.provider.PropBankContract.Words_PbRoleSets;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.sql.SqlFormatter;

import androidx.annotation.NonNull;

/**
 * PropBank provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PropBankProvider extends BaseProvider
{
	static private final String TAG = "PropBankProvider";

	// C O N T E N T   P R O V I D E R   A U T H O R I T Y

	static private final String AUTHORITY = makeAuthority("propbank_authority");

	// U R I M A T C H E R

	// uri matcher
	static private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static
	{
		matchURIs();
	}

	// table codes
	static private final int PBROLESET = 10;
	static private final int PBROLESETS = 11;

	// join codes
	static private final int PBROLESETS_X = 100;
	static private final int PBROLESETS_X_BY_ROLESET = 101;
	static private final int WORDS_PBROLESETS = 110;
	static private final int PBROLESETS_PBROLES = 120;
	static private final int PBROLESETS_PBEXAMPLES = 130;
	static private final int PBROLESETS_PBEXAMPLES_BY_EXAMPLE = 131;

	// search text codes
	static private final int LOOKUP_FTS_EXAMPLES = 501;
	static private final int LOOKUP_FTS_EXAMPLES_X = 511;
	static private final int LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE = 512;

	// suggest
	static private final int SUGGEST_WORDS = 601;
	static private final int SUGGEST_FTS_WORDS = 602;

	static private void matchURIs()
	{
		PropBankProvider.uriMatcher.addURI(AUTHORITY, PbRoleSets.TABLE, PropBankProvider.PBROLESET);
		PropBankProvider.uriMatcher.addURI(AUTHORITY, PbRoleSets.TABLE, PropBankProvider.PBROLESETS);
		PropBankProvider.uriMatcher.addURI(AUTHORITY, PbRoleSets_X.TABLE, PropBankProvider.PBROLESETS_X);
		PropBankProvider.uriMatcher.addURI(AUTHORITY, PbRoleSets_X.TABLE_BY_ROLESET, PropBankProvider.PBROLESETS_X_BY_ROLESET);
		PropBankProvider.uriMatcher.addURI(AUTHORITY, Words_PbRoleSets.TABLE, PropBankProvider.WORDS_PBROLESETS);
		PropBankProvider.uriMatcher.addURI(AUTHORITY, PbRoleSets_PbRoles.TABLE, PropBankProvider.PBROLESETS_PBROLES);
		PropBankProvider.uriMatcher.addURI(AUTHORITY, PbRoleSets_PbExamples.TABLE, PropBankProvider.PBROLESETS_PBEXAMPLES);
		PropBankProvider.uriMatcher.addURI(AUTHORITY, PbRoleSets_PbExamples.TABLE_BY_EXAMPLE, PropBankProvider.PBROLESETS_PBEXAMPLES_BY_EXAMPLE);

		PropBankProvider.uriMatcher.addURI(AUTHORITY, Lookup_PbExamples.TABLE + "/", PropBankProvider.LOOKUP_FTS_EXAMPLES);
		PropBankProvider.uriMatcher.addURI(AUTHORITY, Lookup_PbExamples_X.TABLE + "/", PropBankProvider.LOOKUP_FTS_EXAMPLES_X);
		PropBankProvider.uriMatcher.addURI(AUTHORITY, Lookup_PbExamples_X.TABLE_BY_EXAMPLE + "/", PropBankProvider.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE);

		PropBankProvider.uriMatcher.addURI(AUTHORITY, Suggest_PbWords.TABLE + "/*", PropBankProvider.SUGGEST_WORDS);
		PropBankProvider.uriMatcher.addURI(AUTHORITY, Suggest_PbWords.TABLE + "/", PropBankProvider.SUGGEST_WORDS);
		PropBankProvider.uriMatcher.addURI(AUTHORITY, Suggest_FTS_PbWords.TABLE + "/*", PropBankProvider.SUGGEST_FTS_WORDS);
		PropBankProvider.uriMatcher.addURI(AUTHORITY, Suggest_FTS_PbWords.TABLE + "/", PropBankProvider.SUGGEST_FTS_WORDS);
	}

	static public String makeUri(final String table)
	{
		return BaseProvider.SCHEME + AUTHORITY + '/' + table;
	}

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public PropBankProvider()
	{
		//
	}

	// C L O S E

	/**
	 * Close provider
	 *
	 * @param context context
	 */
	static public void close(@NonNull final Context context)
	{
		final Uri uri = Uri.parse(BaseProvider.SCHEME + AUTHORITY);
		closeProvider(context, uri);
	}

	// M I M E

	@Override
	public String getType(@NonNull final Uri uri)
	{
		switch (PropBankProvider.uriMatcher.match(uri))
		{
			// T A B L E S
			case PBROLESET:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PbRoleSets.TABLE;
			case PBROLESETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PbRoleSets.TABLE;
			case PBROLESETS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PbRoleSets_X.TABLE;
			case PBROLESETS_X_BY_ROLESET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PbRoleSets_X.TABLE_BY_ROLESET;
			case WORDS_PBROLESETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_PbRoleSets.TABLE;
			case PBROLESETS_PBROLES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PbRoleSets_PbRoles.TABLE;
			case PBROLESETS_PBEXAMPLES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PbRoleSets_PbExamples.TABLE;
			case PBROLESETS_PBEXAMPLES_BY_EXAMPLE:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PbRoleSets_PbExamples.TABLE_BY_EXAMPLE;

			// L O O K U P
			case LOOKUP_FTS_EXAMPLES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_PbExamples.TABLE;
			case LOOKUP_FTS_EXAMPLES_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_PbExamples_X.TABLE;
			case LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_PbExamples_X.TABLE_BY_EXAMPLE;

			// S U G G E S T
			case SUGGEST_WORDS:
			case SUGGEST_FTS_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PbWords.TABLE;

			default:
				throw new UnsupportedOperationException("Illegal MIME type");
		}
	}

	// Q U E R Y

	@SuppressWarnings("boxing")
	@Override
	public Cursor query(@NonNull final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder)
	{
		if (this.db == null)
		{
			try
			{
				open();
			}
			catch (SQLiteCantOpenDatabaseException e)
			{
				return null;
			}
		}

		// choose the table to query and a sort order based on the code returned for the incoming URI
		String actualSelection = selection;
		String actualSortOrder = sortOrder;
		final int code = PropBankProvider.uriMatcher.match(uri);
		Log.d(PropBankProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code));
		String groupBy = null;
		String table;
		switch (code)
		{
			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case PBROLESET:
				table = PbRoleSets.TABLE;
				if (actualSelection != null)
				{
					actualSelection += " AND ";
				}
				else
				{
					actualSelection = "";
				}
				actualSelection += PbRoleSets.ROLESETID + " = ?";
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
				actualSortOrder = "narg";
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
				actualSortOrder = PropBankContract.EXAMPLE + ".exampleid,narg";
				break;

			// L O O K U P

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

			// S U G G E S T

			case SUGGEST_WORDS:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}
				table = "pbwords";
				return this.db.query(table, new String[]{"pbwordid AS _id", //
								"lemma AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
								"lemma AS " + SearchManager.SUGGEST_COLUMN_QUERY}, //
						"lemma LIKE ? || '%'", //
						new String[]{last}, null, null, null);
			}

			case SUGGEST_FTS_WORDS:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}
				table = "pbwords_word_fts4";
				return this.db.query(table, new String[]{"pbwordid AS _id", //
								"lemma AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
								"lemma AS " + SearchManager.SUGGEST_COLUMN_QUERY}, //
						"lemma MATCH ?", //
						new String[]{last + '*'}, null, null, null);
			}

			default:
			case UriMatcher.NO_MATCH:
				throw new RuntimeException("Malformed URI " + uri);
		}

		final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, actualSelection, groupBy, null, actualSortOrder, null);
		logSql(sql, selectionArgs);
		if (BaseProvider.logSql)
		{
			Log.d(PropBankProvider.TAG + "SQL", SqlFormatter.format(sql).toString());
			Log.d(PropBankProvider.TAG + "ARGS", BaseProvider.argsToString(selectionArgs));
		}

		// do query
		try
		{
			return this.db.rawQuery(sql, selectionArgs);
			//return this.db.query(table, projection, actualSelection, selectionArgs, groupBy, null, actualSortOrder, null);
		}
		catch (SQLiteException e)
		{
			Log.d(TAG + "SQL", sql);
			Log.e(TAG, "PropBank provider query failed", e);
			return null;
		}
	}
}
