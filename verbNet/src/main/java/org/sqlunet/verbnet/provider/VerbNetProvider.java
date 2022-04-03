/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.verbnet.provider;

import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.provider.BaseProvider;
import org.sqlunet.sql.SqlFormatter;
import org.sqlunet.verbnet.provider.VerbNetContract.Lookup_VnExamples;
import org.sqlunet.verbnet.provider.VerbNetContract.Lookup_VnExamples_X;
import org.sqlunet.verbnet.provider.VerbNetContract.Suggest_FTS_VnWords;
import org.sqlunet.verbnet.provider.VerbNetContract.Suggest_VnWords;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnFrames_X;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnMembers_X;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnRoles_X;
import org.sqlunet.verbnet.provider.VerbNetContract.VnWords;
import org.sqlunet.verbnet.provider.VerbNetContract.Words_VnClasses;
import org.sqlunet.verbnet.provider.VerbNetDispatcher.Result;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * VerbNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VerbNetProvider extends BaseProvider
{
	static private final String TAG = "VerbNetProvider";

	// C O N T E N T   P R O V I D E R   A U T H O R I T Y

	static private final String AUTHORITY = makeAuthority("verbnet_authority");

	// U R I M A T C H E R

	static private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static
	{
		matchURIs();
	}

	static private void matchURIs()
	{
		uriMatcher.addURI(AUTHORITY, VnClasses.TABLE, VerbNetDispatcher.VNCLASS1);
		uriMatcher.addURI(AUTHORITY, VnClasses.TABLE, VerbNetDispatcher.VNCLASSES);
		uriMatcher.addURI(AUTHORITY, Words_VnClasses.TABLE, VerbNetDispatcher.WORDS_VNCLASSES);
		uriMatcher.addURI(AUTHORITY, VnClasses_VnMembers_X.TABLE_BY_WORD, VerbNetDispatcher.VNCLASSES_VNMEMBERS_X_BY_WORD);
		uriMatcher.addURI(AUTHORITY, VnClasses_VnRoles_X.TABLE_BY_ROLE, VerbNetDispatcher.VNCLASSES_VNROLES_X_BY_VNROLE);
		uriMatcher.addURI(AUTHORITY, VnClasses_VnFrames_X.TABLE_BY_FRAME, VerbNetDispatcher.VNCLASSES_VNFRAMES_X_BY_VNFRAME);

		uriMatcher.addURI(AUTHORITY, Lookup_VnExamples.TABLE + "/", VerbNetDispatcher.LOOKUP_FTS_EXAMPLES);
		uriMatcher.addURI(AUTHORITY, Lookup_VnExamples_X.TABLE + "/", VerbNetDispatcher.LOOKUP_FTS_EXAMPLES_X);
		uriMatcher.addURI(AUTHORITY, Lookup_VnExamples_X.TABLE_BY_EXAMPLE + "/", VerbNetDispatcher.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE);

		uriMatcher.addURI(AUTHORITY, Suggest_VnWords.TABLE + "/*", VerbNetDispatcher.SUGGEST_WORDS);
		uriMatcher.addURI(AUTHORITY, Suggest_VnWords.TABLE + "/", VerbNetDispatcher.SUGGEST_WORDS);
		uriMatcher.addURI(AUTHORITY, Suggest_FTS_VnWords.TABLE + "/*", VerbNetDispatcher.SUGGEST_FTS_WORDS);
		uriMatcher.addURI(AUTHORITY, Suggest_FTS_VnWords.TABLE + "/", VerbNetDispatcher.SUGGEST_FTS_WORDS);
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
	public VerbNetProvider()
	{
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
		switch (VerbNetProvider.uriMatcher.match(uri))
		{
			// I T E M S
			case VerbNetDispatcher.VNCLASS1:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + VnClasses.TABLE;

			// T A B L E S
			case VerbNetDispatcher.VNCLASSES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + VnClasses.TABLE;

			// J O I N S
			case VerbNetDispatcher.WORDS_VNCLASSES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_VnClasses.TABLE;
			case VerbNetDispatcher.VNCLASSES_VNMEMBERS_X_BY_WORD:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + VnClasses_VnMembers_X.TABLE_BY_WORD;
			case VerbNetDispatcher.VNCLASSES_VNROLES_X_BY_VNROLE:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + VnClasses_VnRoles_X.TABLE_BY_ROLE;
			case VerbNetDispatcher.VNCLASSES_VNFRAMES_X_BY_VNFRAME:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + VnClasses_VnFrames_X.TABLE_BY_FRAME;

			// L O O K U P
			case VerbNetDispatcher.LOOKUP_FTS_EXAMPLES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_VnExamples.TABLE;
			case VerbNetDispatcher.LOOKUP_FTS_EXAMPLES_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_VnExamples_X.TABLE;
			case VerbNetDispatcher.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_VnExamples_X.TABLE_BY_EXAMPLE;

			// S U G G E S T
			case VerbNetDispatcher.SUGGEST_WORDS:
			case VerbNetDispatcher.SUGGEST_FTS_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + VnWords.TABLE;

			default:
				throw new UnsupportedOperationException("Illegal MIME type");
		}
	}

	// Q U E R Y

	@Nullable
	@SuppressWarnings("boxing")
	@Override
	public Cursor query(@NonNull final Uri uri, final String[] projection0, final String selection0, final String[] selectionArgs0, String sortOrder0)
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
		final int code = VerbNetProvider.uriMatcher.match(uri);
		Log.d(VerbNetProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code));
		if (code == UriMatcher.NO_MATCH)
		{
			throw new RuntimeException("Malformed URI " + uri);
		}

		Result result;
		// MAIN
		result = VerbNetDispatcher.queryMain(code, uri.getLastPathSegment(), projection0, selection0, selectionArgs0);
		if (result == null)
		{
			// TEXTSEARCH
			result = VerbNetDispatcher.querySearch(code, projection0, selection0, selectionArgs0);
		}
		// MAIN || TEXTSEARCH
		if (result != null)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, result.table, result.projection, result.selection, result.groupBy, null, sortOrder0, null);
			logSql(sql, selectionArgs0);
			if (BaseProvider.logSql)
			{
				Log.d(TAG + "SQL", SqlFormatter.format(sql).toString());
				Log.d(TAG + "ARG", BaseProvider.argsToString(selectionArgs0));
			}

			// do query
			try
			{
				final Cursor cursor = this.db.rawQuery(sql, selectionArgs0);
				Log.d(TAG + "COUNT", cursor.getCount() + " items");
				return cursor;
				//return this.db.query(table, actualProjection, actualSelection, selectionArgs, groupBy, null, sortOrder, null);
			}
			catch (@NonNull final SQLiteException e)
			{
				Log.d(TAG + "SQL", sql);
				Log.e(TAG, "WordNet provider query failed", e);
				return null;
			}
		}

		// SUGGEST
		result = VerbNetDispatcher.querySuggest(code, uri.getLastPathSegment());
		if (result != null)
		{
			return this.db.query(result.table, result.projection, result.selection, result.selectionArgs, result.groupBy, null, null);
		}
		return null;
	}
}
