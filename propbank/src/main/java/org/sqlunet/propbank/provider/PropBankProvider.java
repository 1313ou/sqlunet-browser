/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.propbank.provider;

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
import org.sqlunet.propbank.provider.PropBankControl.Result;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.sql.SqlFormatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

	static private void matchURIs()
	{
		uriMatcher.addURI(AUTHORITY, PbRoleSets.URI, PropBankControl.PBROLESET);
		uriMatcher.addURI(AUTHORITY, PbRoleSets.URI, PropBankControl.PBROLESETS);
		uriMatcher.addURI(AUTHORITY, PbRoleSets_X.URI, PropBankControl.PBROLESETS_X);
		uriMatcher.addURI(AUTHORITY, PbRoleSets_X.URI_BY_ROLESET, PropBankControl.PBROLESETS_X_BY_ROLESET);
		uriMatcher.addURI(AUTHORITY, Words_PbRoleSets.URI, PropBankControl.WORDS_PBROLESETS);
		uriMatcher.addURI(AUTHORITY, PbRoleSets_PbRoles.URI, PropBankControl.PBROLESETS_PBROLES);
		uriMatcher.addURI(AUTHORITY, PbRoleSets_PbExamples.URI, PropBankControl.PBROLESETS_PBEXAMPLES);
		uriMatcher.addURI(AUTHORITY, PbRoleSets_PbExamples.URI_BY_EXAMPLE, PropBankControl.PBROLESETS_PBEXAMPLES_BY_EXAMPLE);

		uriMatcher.addURI(AUTHORITY, Lookup_PbExamples.URI + "/", PropBankControl.LOOKUP_FTS_EXAMPLES);
		uriMatcher.addURI(AUTHORITY, Lookup_PbExamples_X.URI + "/", PropBankControl.LOOKUP_FTS_EXAMPLES_X);
		uriMatcher.addURI(AUTHORITY, Lookup_PbExamples_X.URI_BY_EXAMPLE + "/", PropBankControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE);

		uriMatcher.addURI(AUTHORITY, Suggest_PbWords.URI + "/*", PropBankControl.SUGGEST_WORDS);
		uriMatcher.addURI(AUTHORITY, Suggest_PbWords.URI + "/", PropBankControl.SUGGEST_WORDS);
		uriMatcher.addURI(AUTHORITY, Suggest_FTS_PbWords.URI + "/*", PropBankControl.SUGGEST_FTS_WORDS);
		uriMatcher.addURI(AUTHORITY, Suggest_FTS_PbWords.URI + "/", PropBankControl.SUGGEST_FTS_WORDS);
	}

	@NonNull
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

	@NonNull
	@Override
	public String getType(@NonNull final Uri uri)
	{
		switch (PropBankProvider.uriMatcher.match(uri))
		{
			// T A B L E S
			case PropBankControl.PBROLESET:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PbRoleSets.URI;
			case PropBankControl.PBROLESETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PbRoleSets.URI;
			case PropBankControl.PBROLESETS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PbRoleSets_X.URI;
			case PropBankControl.PBROLESETS_X_BY_ROLESET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PbRoleSets_X.URI_BY_ROLESET;
			case PropBankControl.WORDS_PBROLESETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_PbRoleSets.URI;
			case PropBankControl.PBROLESETS_PBROLES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PbRoleSets_PbRoles.URI;
			case PropBankControl.PBROLESETS_PBEXAMPLES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PbRoleSets_PbExamples.URI;
			case PropBankControl.PBROLESETS_PBEXAMPLES_BY_EXAMPLE:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PbRoleSets_PbExamples.URI_BY_EXAMPLE;

			// L O O K U P
			case PropBankControl.LOOKUP_FTS_EXAMPLES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_PbExamples.URI;
			case PropBankControl.LOOKUP_FTS_EXAMPLES_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_PbExamples_X.URI;
			case PropBankControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_PbExamples_X.URI_BY_EXAMPLE;

			// S U G G E S T
			case PropBankControl.SUGGEST_WORDS:
			case PropBankControl.SUGGEST_FTS_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PbWords.URI;

			default:
				throw new UnsupportedOperationException("Illegal MIME type");
		}
	}

	// Q U E R Y

	@Nullable
	@SuppressWarnings("boxing")
	@Override
	public Cursor query(@NonNull final Uri uri, final String[] projection0, final String selection0, final String[] selectionArgs0, final String sortOrder0)
	{
		if (this.db == null)
		{
			try
			{
				openReadOnly();
			}
			catch (SQLiteCantOpenDatabaseException e)
			{
				return null;
			}
		}

		// choose the table to query and a sort order based on the code returned for the incoming URI
		final int code = PropBankProvider.uriMatcher.match(uri);
		Log.d(PropBankProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code));
		if (code == UriMatcher.NO_MATCH)
		{
			throw new RuntimeException("Malformed URI " + uri);
		}

		Result result;
		// MAIN
		result = PropBankControl.queryMain(code, uri.getLastPathSegment(), projection0, selection0, selectionArgs0);
		if (result == null)
		{
			// TEXTSEARCH
			result = PropBankControl.querySearch(code, projection0, selection0, selectionArgs0);
		}
		// MAIN || TEXTSEARCH
		if (result != null)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, result.table, result.projection, result.selection, result.groupBy, null, sortOrder0, null);
			logSql(sql, selectionArgs0);
			if (BaseProvider.logSql)
			{
				Log.d(TAG + "SQL", SqlFormatter.format(sql).toString());
				Log.d(TAG + "ARG", BaseProvider.argsToString(result.selectionArgs == null ? selectionArgs0 : result.selectionArgs));
			}

			// do query
			try
			{
				final Cursor cursor = this.db.rawQuery(sql, result.selectionArgs == null ? selectionArgs0 : result.selectionArgs);
				Log.d(TAG + "COUNT", cursor.getCount() + " items");
				return cursor;
			}
			catch (@NonNull final SQLiteException e)
			{
				Log.d(TAG + "SQL", sql);
				Log.e(TAG, "WordNet provider query failed", e);
			}
			return null;
		}

		// SUGGEST
		result = PropBankControl.querySuggest(code, uri.getLastPathSegment());
		if (result != null)
		{
			return this.db.query(result.table, result.projection, result.selection, result.selectionArgs, result.groupBy, null, null);
		}
		return null;
	}
}
