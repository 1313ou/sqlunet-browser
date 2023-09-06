/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.provider;

import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.provider.XNetContract.PredicateMatrix;
import org.sqlunet.provider.XNetContract.PredicateMatrix_FrameNet;
import org.sqlunet.provider.XNetContract.PredicateMatrix_PropBank;
import org.sqlunet.provider.XNetContract.PredicateMatrix_VerbNet;
import org.sqlunet.provider.XNetContract.Sources;
import org.sqlunet.provider.XNetContract.Meta;
import org.sqlunet.provider.XNetContract.Words_FnWords_FnFrames_U;
import org.sqlunet.provider.XNetContract.Words_FnWords_PbWords_VnWords;
import org.sqlunet.provider.XNetContract.Words_PbWords_PbRoleSets;
import org.sqlunet.provider.XNetContract.Words_PbWords_PbRoleSets_U;
import org.sqlunet.provider.XNetContract.Words_PbWords_VnWords;
import org.sqlunet.provider.XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords;
import org.sqlunet.provider.XNetContract.Words_VnWords_VnClasses;
import org.sqlunet.provider.XNetContract.Words_VnWords_VnClasses_U;
import org.sqlunet.provider.XNetControl.Result;
import org.sqlunet.sql.SqlFormatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Extended cross WordNet-FrameNet-PropBank-VerbNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class XSqlUNetProvider extends BaseProvider
{
	static private final String TAG = "XSqlUNetProvider";

	// C O N T E N T   P R O V I D E R   A U T H O R I T Y

	static private final String AUTHORITY = makeAuthority("xsqlunet_authority");

	// U R I M A T C H E R

	static private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static
	{
		matchURIs();
	}

	static private void matchURIs()
	{
		uriMatcher.addURI(AUTHORITY, Words_FnWords_PbWords_VnWords.URI, XNetControl.WORDS_FNWORDS_PBWORDS_VNWORDS);
		uriMatcher.addURI(AUTHORITY, Words_Pronunciations_FnWords_PbWords_VnWords.URI, XNetControl.WORDS_PRONUNCIATIONS_FNWORDS_PBWORDS_VNWORDS);
		uriMatcher.addURI(AUTHORITY, Words_PbWords_VnWords.URI, XNetControl.WORDS_PBWORDS_VNWORDS);
		uriMatcher.addURI(AUTHORITY, PredicateMatrix.URI, XNetControl.PREDICATEMATRIX);
		uriMatcher.addURI(AUTHORITY, PredicateMatrix_VerbNet.URI, XNetControl.PREDICATEMATRIX_VERBNET);
		uriMatcher.addURI(AUTHORITY, PredicateMatrix_PropBank.URI, XNetControl.PREDICATEMATRIX_PROPBANK);
		uriMatcher.addURI(AUTHORITY, PredicateMatrix_FrameNet.URI, XNetControl.PREDICATEMATRIX_FRAMENET);
		uriMatcher.addURI(AUTHORITY, Words_VnWords_VnClasses.URI, XNetControl.WORDS_VNWORDS_VNCLASSES);
		uriMatcher.addURI(AUTHORITY, Words_VnWords_VnClasses_U.URI, XNetControl.WORDS_VNWORDS_VNCLASSES_U);
		uriMatcher.addURI(AUTHORITY, Words_PbWords_PbRoleSets.URI, XNetControl.WORDS_PBWORDS_PBROLESETS);
		uriMatcher.addURI(AUTHORITY, Words_PbWords_PbRoleSets_U.URI, XNetControl.WORDS_PBWORDS_PBROLESETS_U);
		uriMatcher.addURI(AUTHORITY, Words_FnWords_FnFrames_U.URI, XNetControl.WORDS_FNWORDS_FNFRAMES_U);
		uriMatcher.addURI(AUTHORITY, XNetContract.Sources.URI, XNetControl.SOURCES);
		uriMatcher.addURI(AUTHORITY, XNetContract.Meta.URI, XNetControl.META);
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
	public XSqlUNetProvider()
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
		switch (uriMatcher.match(uri))
		{
			case XNetControl.WORDS_FNWORDS_PBWORDS_VNWORDS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_FnWords_PbWords_VnWords.URI;
			case XNetControl.WORDS_PRONUNCIATIONS_FNWORDS_PBWORDS_VNWORDS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_Pronunciations_FnWords_PbWords_VnWords.URI;
			case XNetControl.WORDS_PBWORDS_VNWORDS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_PbWords_VnWords.URI;
			case XNetControl.PREDICATEMATRIX:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PredicateMatrix.URI;
			case XNetControl.PREDICATEMATRIX_VERBNET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PredicateMatrix_VerbNet.URI;
			case XNetControl.PREDICATEMATRIX_PROPBANK:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PredicateMatrix_PropBank.URI;
			case XNetControl.PREDICATEMATRIX_FRAMENET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PredicateMatrix_FrameNet.URI;
			case XNetControl.WORDS_VNWORDS_VNCLASSES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_VnWords_VnClasses.URI;
			case XNetControl.WORDS_VNWORDS_VNCLASSES_U:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_VnWords_VnClasses_U.URI;
			case XNetControl.WORDS_PBWORDS_PBROLESETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_PbWords_PbRoleSets.URI;
			case XNetControl.WORDS_PBWORDS_PBROLESETS_U:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_PbWords_PbRoleSets_U.URI;
			case XNetControl.WORDS_FNWORDS_FNFRAMES_U:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_FnWords_FnFrames_U.URI;
			case XNetControl.SOURCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Sources.URI;
			case XNetControl.META:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Meta.URI;
			default:
				throw new UnsupportedOperationException("Illegal MIME type");
		}
	}

	// Q U E R Y

	/**
	 * Query
	 *
	 * @param uri           uri
	 * @param projection0    projection
	 * @param selection0     selection
	 * @param selectionArgs0 selection arguments
	 * @param sortOrder0     sort order
	 * @return cursor
	 */
	@Nullable
	@Override
	public Cursor query(@NonNull final Uri uri, @Nullable final String[] projection0, final String selection0, final String[] selectionArgs0, final String sortOrder0)
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
		final int code = XSqlUNetProvider.uriMatcher.match(uri);
		Log.d(XSqlUNetProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code));
		if (code == UriMatcher.NO_MATCH)
		{
			throw new RuntimeException("Malformed URI " + uri);
		}

		Result result = XNetControl.queryMain(code, uri.getLastPathSegment(), projection0, selection0, selectionArgs0);
		if (result != null)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, result.table, result.projection, result.selection, result.groupBy, null, sortOrder0, null);
			logSql(sql, result.selectionArgs == null ? selectionArgs0 : result.selectionArgs);
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
		}
		return null;
	}
}
