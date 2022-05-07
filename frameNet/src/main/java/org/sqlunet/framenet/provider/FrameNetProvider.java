/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet.provider;

import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.framenet.provider.FrameNetContract.AnnoSets;
import org.sqlunet.framenet.provider.FrameNetContract.AnnoSets_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.Words;
import org.sqlunet.framenet.provider.FrameNetContract.Frames;
import org.sqlunet.framenet.provider.FrameNetContract.Frames_FEs;
import org.sqlunet.framenet.provider.FrameNetContract.Frames_Related;
import org.sqlunet.framenet.provider.FrameNetContract.Frames_X;
import org.sqlunet.framenet.provider.FrameNetContract.Governors_AnnoSets_Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_FERealizations_ValenceUnits;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_Governors;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_X;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_or_Frames;
import org.sqlunet.framenet.provider.FrameNetContract.Lookup_FnSentences;
import org.sqlunet.framenet.provider.FrameNetContract.Lookup_FnSentences_X;
import org.sqlunet.framenet.provider.FrameNetContract.Lookup_FnWords;
import org.sqlunet.framenet.provider.FrameNetContract.Patterns_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.Patterns_Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.Sentences_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.Suggest_FTS_FnWords;
import org.sqlunet.framenet.provider.FrameNetContract.Suggest_FnWords;
import org.sqlunet.framenet.provider.FrameNetContract.ValenceUnits_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.ValenceUnits_Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.Words_LexUnits_Frames;
import org.sqlunet.framenet.provider.FrameNetDispatcher.Result;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.sql.SqlFormatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * FrameNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameNetProvider extends BaseProvider
{
	static private final String TAG = "FrameNetProvider";

	// C O N T E N T   P R O V I D E R   A U T H O R I T Y

	static private final String AUTHORITY = makeAuthority("framenet_authority");

	// U R I M A T C H E R

	static private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static
	{
		matchURIs();
	}


	static private void matchURIs()
	{
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits.TABLE, FrameNetDispatcher.LEXUNIT);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits.TABLE, FrameNetDispatcher.LEXUNITS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_X.TABLE_BY_LEXUNIT, FrameNetDispatcher.LEXUNITS_X_BY_LEXUNIT);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_or_Frames.TABLE, FrameNetDispatcher.LEXUNITS_OR_FRAMES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_or_Frames.TABLE_FN, FrameNetDispatcher.LEXUNITS_OR_FRAMES_FN);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames.TABLE, FrameNetDispatcher.FRAME);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames.TABLE, FrameNetDispatcher.FRAMES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames_X.TABLE_BY_FRAME, FrameNetDispatcher.FRAMES_X_BY_FRAME);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames_Related.TABLE, FrameNetDispatcher.FRAMES_RELATED);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Sentences.TABLE, FrameNetDispatcher.SENTENCE);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Sentences.TABLE, FrameNetDispatcher.SENTENCES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, AnnoSets.TABLE, FrameNetDispatcher.ANNOSET);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, AnnoSets.TABLE, FrameNetDispatcher.ANNOSETS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Sentences_Layers_X.TABLE, FrameNetDispatcher.SENTENCES_LAYERS_X);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, AnnoSets_Layers_X.TABLE, FrameNetDispatcher.ANNOSETS_LAYERS_X);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Patterns_Layers_X.TABLE, FrameNetDispatcher.PATTERNS_LAYERS_X);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, ValenceUnits_Layers_X.TABLE, FrameNetDispatcher.VALENCEUNITS_LAYERS_X);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Words_LexUnits_Frames.TABLE, FrameNetDispatcher.WORDS_LEXUNITS_FRAMES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Words_LexUnits_Frames.TABLE_FN, FrameNetDispatcher.WORDS_LEXUNITS_FRAMES_FN);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames_FEs.TABLE, FrameNetDispatcher.FRAMES_FES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames_FEs.TABLE_BY_FE, FrameNetDispatcher.FRAMES_FES_BY_FE);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_Sentences.TABLE, FrameNetDispatcher.LEXUNITS_SENTENCES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_Sentences.TABLE_BY_SENTENCE, FrameNetDispatcher.LEXUNITS_SENTENCES_BY_SENTENCE);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_Sentences_AnnoSets_Layers_Labels.TABLE, FrameNetDispatcher.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_Sentences_AnnoSets_Layers_Labels.TABLE_BY_SENTENCE, FrameNetDispatcher.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_Governors.TABLE, FrameNetDispatcher.LEXUNITS_GOVERNORS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_Governors.TABLE_FN, FrameNetDispatcher.LEXUNITS_GOVERNORS_FN);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_FERealizations_ValenceUnits.TABLE, FrameNetDispatcher.LEXUNITS_REALIZATIONS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_FERealizations_ValenceUnits.TABLE_BY_REALIZATION, FrameNetDispatcher.LEXUNITS_REALIZATIONS_BY_REALIZATION);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE, FrameNetDispatcher.LEXUNITS_GROUPREALIZATIONS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE_BY_PATTERN, FrameNetDispatcher.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Patterns_Sentences.TABLE, FrameNetDispatcher.PATTERNS_SENTENCES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, ValenceUnits_Sentences.TABLE, FrameNetDispatcher.VALENCEUNITS_SENTENCES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Governors_AnnoSets_Sentences.TABLE, FrameNetDispatcher.GOVERNORS_ANNOSETS);

		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_FnWords.TABLE + "/*", FrameNetDispatcher.LOOKUP_FTS_WORDS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_FnWords.TABLE + "/", FrameNetDispatcher.LOOKUP_FTS_WORDS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_FnSentences.TABLE + "/", FrameNetDispatcher.LOOKUP_FTS_SENTENCES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_FnSentences_X.TABLE + "/", FrameNetDispatcher.LOOKUP_FTS_SENTENCES_X);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_FnSentences_X.TABLE_BY_SENTENCE + "/", FrameNetDispatcher.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE);

		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FnWords.TABLE + "/*", FrameNetDispatcher.SUGGEST_WORDS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FnWords.TABLE + "/", FrameNetDispatcher.SUGGEST_WORDS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FTS_FnWords.TABLE + "/*", FrameNetDispatcher.SUGGEST_FTS_WORDS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FTS_FnWords.TABLE + "/", FrameNetDispatcher.SUGGEST_FTS_WORDS);
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
	public FrameNetProvider()
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
		switch (FrameNetProvider.uriMatcher.match(uri))
		{
			// TABLES
			case FrameNetDispatcher.LEXUNIT:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits.TABLE;
			case FrameNetDispatcher.LEXUNITS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits.TABLE;
			case FrameNetDispatcher.LEXUNITS_X_BY_LEXUNIT:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_X.TABLE_BY_LEXUNIT;
			case FrameNetDispatcher.LEXUNITS_OR_FRAMES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_or_Frames.TABLE;
			case FrameNetDispatcher.LEXUNITS_OR_FRAMES_FN:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_or_Frames.TABLE_FN;
			case FrameNetDispatcher.FRAME:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Frames.TABLE;
			case FrameNetDispatcher.FRAMES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Frames.TABLE;
			case FrameNetDispatcher.FRAMES_X_BY_FRAME:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Frames_X.TABLE_BY_FRAME;
			case FrameNetDispatcher.FRAMES_RELATED:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Frames_Related.TABLE;
			case FrameNetDispatcher.SENTENCE:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Sentences.TABLE;
			case FrameNetDispatcher.SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Sentences.TABLE;
			case FrameNetDispatcher.ANNOSET:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + AnnoSets.TABLE;
			case FrameNetDispatcher.ANNOSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + AnnoSets.TABLE;
			case FrameNetDispatcher.SENTENCES_LAYERS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Sentences_Layers_X.TABLE;
			case FrameNetDispatcher.ANNOSETS_LAYERS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + AnnoSets_Layers_X.TABLE;
			case FrameNetDispatcher.PATTERNS_LAYERS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Patterns_Layers_X.TABLE;
			case FrameNetDispatcher.VALENCEUNITS_LAYERS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + ValenceUnits_Layers_X.TABLE;
			case FrameNetDispatcher.WORDS_LEXUNITS_FRAMES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_LexUnits_Frames.TABLE;
			case FrameNetDispatcher.WORDS_LEXUNITS_FRAMES_FN:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_LexUnits_Frames.TABLE_FN;
			case FrameNetDispatcher.FRAMES_FES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Frames_FEs.TABLE;
			case FrameNetDispatcher.LEXUNITS_SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_Sentences.TABLE;
			case FrameNetDispatcher.LEXUNITS_GOVERNORS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_Governors.TABLE;
			case FrameNetDispatcher.LEXUNITS_GOVERNORS_FN:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_Governors.TABLE_FN;
			case FrameNetDispatcher.LEXUNITS_REALIZATIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_FERealizations_ValenceUnits.TABLE;
			case FrameNetDispatcher.LEXUNITS_REALIZATIONS_BY_REALIZATION:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_FERealizations_ValenceUnits.TABLE_BY_REALIZATION;
			case FrameNetDispatcher.LEXUNITS_GROUPREALIZATIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE;
			case FrameNetDispatcher.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE_BY_PATTERN;
			case FrameNetDispatcher.PATTERNS_SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Patterns_Sentences.TABLE;
			case FrameNetDispatcher.VALENCEUNITS_SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + ValenceUnits_Sentences.TABLE;
			case FrameNetDispatcher.GOVERNORS_ANNOSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Governors_AnnoSets_Sentences.TABLE;

			// L O O K U P
			case FrameNetDispatcher.LOOKUP_FTS_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words.TABLE;
			case FrameNetDispatcher.LOOKUP_FTS_SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_FnSentences.TABLE;
			case FrameNetDispatcher.LOOKUP_FTS_SENTENCES_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_FnSentences_X.TABLE;
			case FrameNetDispatcher.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_FnSentences_X.TABLE_BY_SENTENCE;

			// S U G G E S T
			case FrameNetDispatcher.SUGGEST_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words.TABLE;
			case FrameNetDispatcher.SUGGEST_FTS_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words.TABLE;

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
		final int code = FrameNetProvider.uriMatcher.match(uri);
		Log.d(FrameNetProvider.TAG + "URI", String.format("%s (code %s)", uri, code));
		if (code == UriMatcher.NO_MATCH)
		{
			throw new RuntimeException("Malformed URI " + uri);
		}

		Result result;
		// MAIN
		result = FrameNetDispatcher.queryMain(code, uri.getLastPathSegment(), projection0, selection0, selectionArgs0);
		if (result == null)
		{
			// TEXTSEARCH
			result = FrameNetDispatcher.querySearch(code, projection0, selection0, selectionArgs0);
		}
		// MAIN || TEXTSEARCH
		if (result != null)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, result.table, result.projection, result.selection, result.groupBy, null, sortOrder0, null);
			logSql(sql, result.selectionArgs);
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
		result = FrameNetDispatcher.querySuggest(code, uri.getLastPathSegment());
		if (result != null)
		{
			return this.db.query(result.table, result.projection, result.selection, result.selectionArgs, result.groupBy, null, null);
		}
		return null;
	}
}
