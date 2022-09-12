/*
 * Copyright (c) 2022. Bernard Bou
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
import org.sqlunet.framenet.provider.FrameNetContract.Lookup_FTS_FnSentences;
import org.sqlunet.framenet.provider.FrameNetContract.Lookup_FTS_FnSentences_X;
import org.sqlunet.framenet.provider.FrameNetContract.Lookup_FTS_FnWords;
import org.sqlunet.framenet.provider.FrameNetContract.Patterns_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.Patterns_Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.Sentences_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.Suggest_FTS_FnWords;
import org.sqlunet.framenet.provider.FrameNetContract.Suggest_FnWords;
import org.sqlunet.framenet.provider.FrameNetContract.ValenceUnits_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.ValenceUnits_Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.Words_LexUnits_Frames;
import org.sqlunet.framenet.provider.FrameNetControl.Result;
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
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits.URI, FrameNetControl.LEXUNIT);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits.URI, FrameNetControl.LEXUNITS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_X.URI_BY_LEXUNIT, FrameNetControl.LEXUNITS_X_BY_LEXUNIT);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_or_Frames.URI, FrameNetControl.LEXUNITS_OR_FRAMES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_or_Frames.URI_FN, FrameNetControl.LEXUNITS_OR_FRAMES_FN);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames.URI, FrameNetControl.FRAME);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames.URI, FrameNetControl.FRAMES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames_X.URI_BY_FRAME, FrameNetControl.FRAMES_X_BY_FRAME);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames_Related.URI, FrameNetControl.FRAMES_RELATED);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Sentences.URI, FrameNetControl.SENTENCE);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Sentences.URI, FrameNetControl.SENTENCES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, AnnoSets.URI, FrameNetControl.ANNOSET);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, AnnoSets.URI, FrameNetControl.ANNOSETS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Sentences_Layers_X.URI, FrameNetControl.SENTENCES_LAYERS_X);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, AnnoSets_Layers_X.URI, FrameNetControl.ANNOSETS_LAYERS_X);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Patterns_Layers_X.URI, FrameNetControl.PATTERNS_LAYERS_X);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, ValenceUnits_Layers_X.URI, FrameNetControl.VALENCEUNITS_LAYERS_X);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Words_LexUnits_Frames.URI, FrameNetControl.WORDS_LEXUNITS_FRAMES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Words_LexUnits_Frames.URI_FN, FrameNetControl.WORDS_LEXUNITS_FRAMES_FN);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames_FEs.URI, FrameNetControl.FRAMES_FES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames_FEs.URI_BY_FE, FrameNetControl.FRAMES_FES_BY_FE);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_Sentences.URI, FrameNetControl.LEXUNITS_SENTENCES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_Sentences.URI_BY_SENTENCE, FrameNetControl.LEXUNITS_SENTENCES_BY_SENTENCE);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_Sentences_AnnoSets_Layers_Labels.URI, FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_Sentences_AnnoSets_Layers_Labels.URI_BY_SENTENCE, FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_Governors.URI, FrameNetControl.LEXUNITS_GOVERNORS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_Governors.URI_FN, FrameNetControl.LEXUNITS_GOVERNORS_FN);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_FERealizations_ValenceUnits.URI, FrameNetControl.LEXUNITS_REALIZATIONS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_FERealizations_ValenceUnits.URI_BY_REALIZATION, FrameNetControl.LEXUNITS_REALIZATIONS_BY_REALIZATION);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_FEGroupRealizations_Patterns_ValenceUnits.URI, FrameNetControl.LEXUNITS_GROUPREALIZATIONS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_FEGroupRealizations_Patterns_ValenceUnits.URI_BY_PATTERN, FrameNetControl.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Patterns_Sentences.URI, FrameNetControl.PATTERNS_SENTENCES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, ValenceUnits_Sentences.URI, FrameNetControl.VALENCEUNITS_SENTENCES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Governors_AnnoSets_Sentences.URI, FrameNetControl.GOVERNORS_ANNOSETS);

		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_FTS_FnWords.URI + "/*", FrameNetControl.LOOKUP_FTS_WORDS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_FTS_FnWords.URI + "/", FrameNetControl.LOOKUP_FTS_WORDS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_FTS_FnSentences.URI + "/", FrameNetControl.LOOKUP_FTS_SENTENCES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_FTS_FnSentences_X.URI + "/", FrameNetControl.LOOKUP_FTS_SENTENCES_X);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_FTS_FnSentences_X.URI_BY_SENTENCE + "/", FrameNetControl.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE);

		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FnWords.URI + "/*", FrameNetControl.SUGGEST_WORDS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FnWords.URI + "/", FrameNetControl.SUGGEST_WORDS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FTS_FnWords.URI + "/*", FrameNetControl.SUGGEST_FTS_WORDS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FTS_FnWords.URI + "/", FrameNetControl.SUGGEST_FTS_WORDS);
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
			case FrameNetControl.LEXUNIT:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits.URI;
			case FrameNetControl.LEXUNITS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits.URI;
			case FrameNetControl.LEXUNITS_X_BY_LEXUNIT:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_X.URI_BY_LEXUNIT;
			case FrameNetControl.LEXUNITS_OR_FRAMES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_or_Frames.URI;
			case FrameNetControl.LEXUNITS_OR_FRAMES_FN:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_or_Frames.URI_FN;
			case FrameNetControl.FRAME:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Frames.URI;
			case FrameNetControl.FRAMES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Frames.URI;
			case FrameNetControl.FRAMES_X_BY_FRAME:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Frames_X.URI_BY_FRAME;
			case FrameNetControl.FRAMES_RELATED:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Frames_Related.URI;
			case FrameNetControl.SENTENCE:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Sentences.URI;
			case FrameNetControl.SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Sentences.URI;
			case FrameNetControl.ANNOSET:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + AnnoSets.URI;
			case FrameNetControl.ANNOSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + AnnoSets.URI;
			case FrameNetControl.SENTENCES_LAYERS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Sentences_Layers_X.URI;
			case FrameNetControl.ANNOSETS_LAYERS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + AnnoSets_Layers_X.URI;
			case FrameNetControl.PATTERNS_LAYERS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Patterns_Layers_X.URI;
			case FrameNetControl.VALENCEUNITS_LAYERS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + ValenceUnits_Layers_X.URI;
			case FrameNetControl.WORDS_LEXUNITS_FRAMES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_LexUnits_Frames.URI;
			case FrameNetControl.WORDS_LEXUNITS_FRAMES_FN:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_LexUnits_Frames.URI_FN;
			case FrameNetControl.FRAMES_FES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Frames_FEs.URI;
			case FrameNetControl.LEXUNITS_SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_Sentences.URI;
			case FrameNetControl.LEXUNITS_GOVERNORS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_Governors.URI;
			case FrameNetControl.LEXUNITS_GOVERNORS_FN:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_Governors.URI_FN;
			case FrameNetControl.LEXUNITS_REALIZATIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_FERealizations_ValenceUnits.URI;
			case FrameNetControl.LEXUNITS_REALIZATIONS_BY_REALIZATION:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_FERealizations_ValenceUnits.URI_BY_REALIZATION;
			case FrameNetControl.LEXUNITS_GROUPREALIZATIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_FEGroupRealizations_Patterns_ValenceUnits.URI;
			case FrameNetControl.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_FEGroupRealizations_Patterns_ValenceUnits.URI_BY_PATTERN;
			case FrameNetControl.PATTERNS_SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Patterns_Sentences.URI;
			case FrameNetControl.VALENCEUNITS_SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + ValenceUnits_Sentences.URI;
			case FrameNetControl.GOVERNORS_ANNOSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Governors_AnnoSets_Sentences.URI;

			// L O O K U P
			case FrameNetControl.LOOKUP_FTS_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words.URI;
			case FrameNetControl.LOOKUP_FTS_SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_FTS_FnSentences.URI;
			case FrameNetControl.LOOKUP_FTS_SENTENCES_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_FTS_FnSentences_X.URI;
			case FrameNetControl.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_FTS_FnSentences_X.URI_BY_SENTENCE;

			// S U G G E S T
			case FrameNetControl.SUGGEST_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words.TABLE;
			case FrameNetControl.SUGGEST_FTS_WORDS:
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

		// MAIN
		Result result = FrameNetControl.queryMain(code, uri.getLastPathSegment(), projection0, selection0, selectionArgs0);
		if (result == null)
		{
			// TEXTSEARCH
			result = FrameNetControl.querySearch(code, projection0, selection0, selectionArgs0);
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
		result = FrameNetControl.querySuggest(code, uri.getLastPathSegment());
		if (result != null)
		{
			return this.db.query(result.table, result.projection, result.selection, result.selectionArgs, result.groupBy, null, null);
		}
		return null;
	}
}
