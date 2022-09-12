/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.wordnet.provider;

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
import org.sqlunet.wordnet.provider.WordNetContract.AdjPositions;
import org.sqlunet.wordnet.provider.WordNetContract.AnyRelations;
import org.sqlunet.wordnet.provider.WordNetContract.AnyRelations_Senses_Words_X;
import org.sqlunet.wordnet.provider.WordNetContract.Dict;
import org.sqlunet.wordnet.provider.WordNetContract.Domains;
import org.sqlunet.wordnet.provider.WordNetContract.LexRelations;
import org.sqlunet.wordnet.provider.WordNetContract.LexRelations_Senses;
import org.sqlunet.wordnet.provider.WordNetContract.LexRelations_Senses_Words_X;
import org.sqlunet.wordnet.provider.WordNetContract.LexRelations_Senses_X;
import org.sqlunet.wordnet.provider.WordNetContract.Lexes_Morphs;
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Definitions;
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Samples;
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Words;
import org.sqlunet.wordnet.provider.WordNetContract.Poses;
import org.sqlunet.wordnet.provider.WordNetContract.Relations;
import org.sqlunet.wordnet.provider.WordNetContract.Samples;
import org.sqlunet.wordnet.provider.WordNetContract.SemRelations;
import org.sqlunet.wordnet.provider.WordNetContract.SemRelations_Synsets;
import org.sqlunet.wordnet.provider.WordNetContract.SemRelations_Synsets_Words_X;
import org.sqlunet.wordnet.provider.WordNetContract.SemRelations_Synsets_X;
import org.sqlunet.wordnet.provider.WordNetContract.Senses;
import org.sqlunet.wordnet.provider.WordNetContract.Senses_AdjPositions;
import org.sqlunet.wordnet.provider.WordNetContract.Senses_Synsets_Poses_Domains;
import org.sqlunet.wordnet.provider.WordNetContract.Senses_VerbFrames;
import org.sqlunet.wordnet.provider.WordNetContract.Senses_VerbTemplates;
import org.sqlunet.wordnet.provider.WordNetContract.Senses_Words;
import org.sqlunet.wordnet.provider.WordNetContract.Suggest_FTS_Definitions;
import org.sqlunet.wordnet.provider.WordNetContract.Suggest_FTS_Samples;
import org.sqlunet.wordnet.provider.WordNetContract.Suggest_FTS_Words;
import org.sqlunet.wordnet.provider.WordNetContract.Suggest_Words;
import org.sqlunet.wordnet.provider.WordNetContract.Synsets;
import org.sqlunet.wordnet.provider.WordNetContract.Synsets_Poses_Domains;
import org.sqlunet.wordnet.provider.WordNetContract.Words;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Lexes_Morphs;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_Synsets;
import org.sqlunet.wordnet.provider.WordNetControl.Result;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * WordNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WordNetProvider extends BaseProvider
{
	static private final String TAG = "WordNetProvider";

	// C O N T E N T   P R O V I D E R   A U T H O R I T Y

	static private final String AUTHORITY = makeAuthority("wordnet_authority");

	// U R I M A T C H E R

	static private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static
	{
		matchURIs();
	}

	static private void matchURIs()
	{
		// table
		uriMatcher.addURI(AUTHORITY, Words.URI, WordNetControl.WORDS);
		uriMatcher.addURI(AUTHORITY, Words.URI + "/#", WordNetControl.WORD);
		uriMatcher.addURI(AUTHORITY, Senses.URI, WordNetControl.SENSES);
		uriMatcher.addURI(AUTHORITY, Senses.URI + "/#", WordNetControl.SENSE);
		uriMatcher.addURI(AUTHORITY, Synsets.URI, WordNetControl.SYNSETS);
		uriMatcher.addURI(AUTHORITY, Synsets.URI + "/#", WordNetControl.SYNSET);
		uriMatcher.addURI(AUTHORITY, SemRelations.URI, WordNetControl.SEMRELATIONS);
		uriMatcher.addURI(AUTHORITY, LexRelations.URI, WordNetControl.LEXRELATIONS);
		uriMatcher.addURI(AUTHORITY, Relations.URI, WordNetControl.RELATIONS);
		uriMatcher.addURI(AUTHORITY, Poses.URI, WordNetControl.POSES);
		uriMatcher.addURI(AUTHORITY, Domains.URI, WordNetControl.DOMAINS);
		uriMatcher.addURI(AUTHORITY, AdjPositions.URI, WordNetControl.ADJPOSITIONS);
		uriMatcher.addURI(AUTHORITY, Samples.URI, WordNetControl.SAMPLES);

		// view
		uriMatcher.addURI(AUTHORITY, Dict.URI, WordNetControl.DICT);

		// joins
		uriMatcher.addURI(AUTHORITY, Words_Senses_Synsets.URI, WordNetControl.WORDS_SENSES_SYNSETS);
		uriMatcher.addURI(AUTHORITY, Words_Senses_CasedWords_Synsets.URI, WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS);
		uriMatcher.addURI(AUTHORITY, Words_Senses_CasedWords_Synsets_Poses_Domains.URI, WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS);
		uriMatcher.addURI(AUTHORITY, WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.URI, WordNetControl.WORDS_SENSES_CASEDWORDS_PRONUNCIATIONS_SYNSETS_POSES_DOMAINS);
		uriMatcher.addURI(AUTHORITY, Senses_Words.URI, WordNetControl.SENSES_WORDS);
		uriMatcher.addURI(AUTHORITY, Senses_Words.URI_BY_SYNSET, WordNetControl.SENSES_WORDS_BY_SYNSET);
		uriMatcher.addURI(AUTHORITY, Senses_Synsets_Poses_Domains.URI, WordNetControl.SENSES_SYNSETS_POSES_DOMAINS);
		uriMatcher.addURI(AUTHORITY, Synsets_Poses_Domains.URI, WordNetControl.SYNSETS_POSES_DOMAINS);

		uriMatcher.addURI(AUTHORITY, AnyRelations_Senses_Words_X.URI_BY_SYNSET, WordNetControl.ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET);

		uriMatcher.addURI(AUTHORITY, SemRelations_Synsets.URI, WordNetControl.SEMRELATIONS_SYNSETS);
		uriMatcher.addURI(AUTHORITY, SemRelations_Synsets_X.URI, WordNetControl.SEMRELATIONS_SYNSETS_X);
		uriMatcher.addURI(AUTHORITY, SemRelations_Synsets_Words_X.URI_BY_SYNSET, WordNetControl.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET);

		uriMatcher.addURI(AUTHORITY, LexRelations_Senses.URI, WordNetControl.LEXRELATIONS_SENSES);
		uriMatcher.addURI(AUTHORITY, LexRelations_Senses_X.URI, WordNetControl.LEXRELATIONS_SENSES_X);
		uriMatcher.addURI(AUTHORITY, LexRelations_Senses_Words_X.URI_BY_SYNSET, WordNetControl.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET);

		uriMatcher.addURI(AUTHORITY, Senses_VerbFrames.URI, WordNetControl.SENSES_VFRAMES);
		uriMatcher.addURI(AUTHORITY, Senses_VerbTemplates.URI, WordNetControl.SENSES_VTEMPLATES);
		uriMatcher.addURI(AUTHORITY, Senses_AdjPositions.URI, WordNetControl.SENSES_ADJPOSITIONS);

		uriMatcher.addURI(AUTHORITY, Lexes_Morphs.URI, WordNetControl.LEXES_MORPHS);
		uriMatcher.addURI(AUTHORITY, Words_Lexes_Morphs.URI, WordNetControl.WORDS_LEXES_MORPHS);
		uriMatcher.addURI(AUTHORITY, Words_Lexes_Morphs.URI_BY_WORD, WordNetControl.WORDS_LEXES_MORPHS_BY_WORD);

		// search text
		uriMatcher.addURI(AUTHORITY, Lookup_Words.URI, WordNetControl.LOOKUP_FTS_WORDS);
		uriMatcher.addURI(AUTHORITY, Lookup_Definitions.URI, WordNetControl.LOOKUP_FTS_DEFINITIONS);
		uriMatcher.addURI(AUTHORITY, Lookup_Samples.URI, WordNetControl.LOOKUP_FTS_SAMPLES);

		// search
		uriMatcher.addURI(AUTHORITY, Suggest_Words.URI + "/*", WordNetControl.SUGGEST_WORDS);
		uriMatcher.addURI(AUTHORITY, Suggest_Words.URI + "/", WordNetControl.SUGGEST_WORDS);
		uriMatcher.addURI(AUTHORITY, Suggest_FTS_Words.URI + "/*", WordNetControl.SUGGEST_FTS_WORDS);
		uriMatcher.addURI(AUTHORITY, Suggest_FTS_Words.URI + "/", WordNetControl.SUGGEST_FTS_WORDS);
		uriMatcher.addURI(AUTHORITY, Suggest_FTS_Definitions.URI + "/*", WordNetControl.SUGGEST_FTS_DEFINITIONS);
		uriMatcher.addURI(AUTHORITY, Suggest_FTS_Definitions.URI + "/", WordNetControl.SUGGEST_FTS_DEFINITIONS);
		uriMatcher.addURI(AUTHORITY, Suggest_FTS_Samples.URI + "/*", WordNetControl.SUGGEST_FTS_SAMPLES);
		uriMatcher.addURI(AUTHORITY, Suggest_FTS_Samples.URI + "/", WordNetControl.SUGGEST_FTS_SAMPLES);
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
	public WordNetProvider()
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
	@SuppressWarnings("DuplicateBranchesInSwitch")
	@Override
	public String getType(@NonNull final Uri uri)
	{
		switch (WordNetProvider.uriMatcher.match(uri))
		{
			// T A B L E S

			case WordNetControl.WORDS:
			case WordNetControl.WORD:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words.URI;
			case WordNetControl.SENSES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses.URI;
			case WordNetControl.SENSE:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses.URI;
			case WordNetControl.SYNSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Synsets.URI;
			case WordNetControl.SYNSET:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Synsets.URI;
			case WordNetControl.SEMRELATIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + SemRelations.URI;
			case WordNetControl.LEXRELATIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexRelations.URI;
			case WordNetControl.RELATIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Relations.URI;
			case WordNetControl.POSES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Poses.URI;
			case WordNetControl.ADJPOSITIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + AdjPositions.URI;
			case WordNetControl.DOMAINS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Domains.URI;
			case WordNetControl.SAMPLES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Samples.URI;

			// V I E W S

			case WordNetControl.DICT:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Dict.URI;
			case WordNetControl.WORDS_SENSES_SYNSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_Senses_Synsets.URI;
			case WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_Senses_CasedWords_Synsets.URI;
			case WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_Senses_CasedWords_Synsets_Poses_Domains.URI;
			case WordNetControl.WORDS_SENSES_CASEDWORDS_PRONUNCIATIONS_SYNSETS_POSES_DOMAINS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.URI;

			// J O I N S

			case WordNetControl.SENSES_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses_Words.URI;
			case WordNetControl.SENSES_WORDS_BY_SYNSET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses_Words.URI_BY_SYNSET;
			case WordNetControl.SENSES_SYNSETS_POSES_DOMAINS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses_Synsets_Poses_Domains.URI;
			case WordNetControl.SYNSETS_POSES_DOMAINS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Synsets_Poses_Domains.URI;
			case WordNetControl.ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + AnyRelations_Senses_Words_X.URI_BY_SYNSET;
			case WordNetControl.SEMRELATIONS_SYNSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + SemRelations_Synsets.URI;
			case WordNetControl.SEMRELATIONS_SYNSETS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + SemRelations_Synsets_X.URI;
			case WordNetControl.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + SemRelations_Synsets_Words_X.URI_BY_SYNSET;
			case WordNetControl.LEXRELATIONS_SENSES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexRelations_Senses.URI;
			case WordNetControl.LEXRELATIONS_SENSES_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexRelations_Senses_X.URI;
			case WordNetControl.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexRelations_Senses_Words_X.URI_BY_SYNSET;
			case WordNetControl.SENSES_VFRAMES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses_VerbFrames.URI;
			case WordNetControl.SENSES_VTEMPLATES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses_VerbTemplates.URI;
			case WordNetControl.SENSES_ADJPOSITIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses_AdjPositions.URI;
			case WordNetControl.LEXES_MORPHS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lexes_Morphs.URI;
			case WordNetControl.WORDS_LEXES_MORPHS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_Lexes_Morphs.URI;
			case WordNetControl.WORDS_LEXES_MORPHS_BY_WORD:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_Lexes_Morphs.URI_BY_WORD;

			// T E X T   L O O K U P S

			case WordNetControl.LOOKUP_FTS_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words.URI;
			case WordNetControl.LOOKUP_FTS_DEFINITIONS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Synsets.URI;
			case WordNetControl.LOOKUP_FTS_SAMPLES:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Samples.URI;

			// S E A R C H

			case WordNetControl.SUGGEST_FTS_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words.URI;
			case WordNetControl.SUGGEST_FTS_DEFINITIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Synsets.URI;
			case WordNetControl.SUGGEST_FTS_SAMPLES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Samples.URI;
			default:
				throw new UnsupportedOperationException("Illegal MIME type");
		}
	}

	// Q U E R Y

	// @Nullable
	// @Override
	// public Cursor query(@NonNull final Uri uri, @Nullable final String[] projection, @Nullable final Bundle queryArgs, @Nullable final CancellationSignal cancellationSignal)
	// {
	// 	return super.query(uri, projection, queryArgs, cancellationSignal);
	// }

	/**
	 * Query
	 *
	 * @param uri            uri
	 * @param projection0    projection
	 * @param selection0     selection
	 * @param selectionArgs0 selection arguments
	 * @param sortOrder0     sort order
	 * @return cursor
	 */
	@SuppressWarnings("boxing")
	@Nullable
	@Override
	public Cursor query(@NonNull final Uri uri, final String[] projection0, @Nullable final String selection0, final String[] selectionArgs0, final String sortOrder0)
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

		final int code = WordNetProvider.uriMatcher.match(uri);
		Log.d(WordNetProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code));
		if (code == UriMatcher.NO_MATCH)
		{
			throw new RuntimeException("Malformed URI " + uri);
		}

		// MAIN
		Result result = WordNetControl.queryMain(code, uri.getLastPathSegment(), projection0, selection0, selectionArgs0);
		if (result == null)
		{
			// RELATIONS
			result = WordNetControl.queryAnyRelations(code, projection0, selection0, selectionArgs0);
			if (result == null)
			{
				// TEXTSEARCH
				result = WordNetControl.querySearch(code, projection0, selection0, selectionArgs0);
			}
		}
		// MAIN || RELATIONS || TEXTSEARCH
		if (result != null)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, result.table, result.projection, result.selection, result.groupBy, null, sortOrder0, null);
			logSql(sql, selectionArgs0);
			if (BaseProvider.logSql)
			{
				Log.d(WordNetProvider.TAG + "SQL", SqlFormatter.format(sql).toString());
				Log.d(WordNetProvider.TAG + "ARG", BaseProvider.argsToString(result.selectionArgs == null ? selectionArgs0 : result.selectionArgs));
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
		result = WordNetControl.querySuggest(code, uri.getLastPathSegment());
		if (result != null)
		{
			return this.db.query(result.table, result.projection, result.selection, result.selectionArgs, result.groupBy, null, null);
		}
		return null;
	}

	// S U B Q U E R Y

	/**
	 * Any relations subquery
	 *
	 * @param selection0 input selection
	 * @return all relations subquery
	 */
	private String makeAnyRelationsSubQuery(final String selection0)
	{
		final String semTable = SemRelations.TABLE;
		final String lexTable = LexRelations.TABLE;
		final String[] projection1 = { //
				SemRelations.RELATIONID, //
				SemRelations.SYNSET1ID, //
				SemRelations.SYNSET2ID, //
		};
		final String[] projection2 = { //
				LexRelations.RELATIONID, //
				LexRelations.WORD1ID, //
				LexRelations.SYNSET1ID, //
				LexRelations.WORD2ID, //
				LexRelations.SYNSET2ID, //
		};
		final String[] unionProjection = { //
				AnyRelations.RELATIONID, //
				AnyRelations.WORD1ID, //
				AnyRelations.SYNSET1ID, //
				AnyRelations.WORD2ID, //
				AnyRelations.SYNSET2ID, //
		};
		assert selection0 != null;
		final String[] selections = selection0.split("/\\*\\*/\\|/\\*\\*/");
		return makeQuery(semTable, //
				lexTable, //
				projection1, //
				projection2, //
				unionProjection, //
				WordNetContract.RELATIONTYPE, //
				"sem", //
				"lex", //
				selections[0], //
				selections[1]);
	}

	/**
	 * Make union query.
	 * Requirements on selection : expr1 AND expr2
	 * Requirements on selectionArgs : [0] value1, [1] value2
	 *
	 * @param table1          table1
	 * @param table2          table2
	 * @param projection1     table1 projection
	 * @param projection2     table2 projection
	 * @param unionProjection union projection
	 * @param discriminator   discriminator field
	 * @param value1          value1 for discriminator
	 * @param value2          value2 for discriminator
	 * @param selection1      selection
	 * @param selection2      selection
	 * @return union sql
	 */
	private static String makeQuery(//
			@SuppressWarnings("SameParameterValue") @NonNull final String table1, //
			@SuppressWarnings("SameParameterValue") @NonNull final String table2, //
			@NonNull final String[] projection1, //
			@NonNull final String[] projection2, //
			@NonNull final String[] unionProjection, //
			@SuppressWarnings("SameParameterValue") @NonNull final String discriminator, //
			@SuppressWarnings("SameParameterValue") @NonNull final String value1, //
			@SuppressWarnings("SameParameterValue") @NonNull final String value2, //
			final String selection1, //
			final String selection2)
	{
		final String[] actualUnionProjection = BaseProvider.appendProjection(unionProjection, WordNetContract.RELATIONTYPE);
		final List<String> table1ProjectionList = Arrays.asList(projection1);
		final List<String> table2ProjectionList = Arrays.asList(projection2);

		// query 1
		//final String actualSelection1 = makeSelectionAndInstantiateArgs(projection1, selection1, selection1Args);
		final String actualSelection1 = makeSelection(projection1, selection1);
		final SQLiteQueryBuilder subQueryBuilder1 = new SQLiteQueryBuilder();
		subQueryBuilder1.setTables(table1);
		final String subQuery1 = subQueryBuilder1.buildUnionSubQuery(discriminator, //
				actualUnionProjection, //
				new HashSet<>(table1ProjectionList), //
				0, //
				value1, //
				actualSelection1, //
				null, //
				null);

		// query 2
		//final String actualSelection2 = makeSelectionAndInstantiateArgs(projection2, selection2, selection2Args);
		final String actualSelection2 = makeSelection(projection2, selection2);
		final SQLiteQueryBuilder subQueryBuilder2 = new SQLiteQueryBuilder();
		subQueryBuilder2.setTables(table2);
		final String subQuery2 = subQueryBuilder2.buildUnionSubQuery(WordNetContract.RELATIONTYPE, //
				actualUnionProjection, //
				new HashSet<>(table2ProjectionList), //
				0, //
				value2, //
				actualSelection2, //
				null, //
				null);

		// union (equiv to view)
		final SQLiteQueryBuilder uQueryBuilder = new SQLiteQueryBuilder();
		uQueryBuilder.setDistinct(true);
		return uQueryBuilder.buildUnionQuery(new String[]{subQuery1, subQuery2}, null, null);
		//return embed(uQuery, projection, selection, groupBy, sortOrder);
	}

	private static String makeSelection(@SuppressWarnings("UnusedParameters") final String[] projection, final String selection)
	{
		return selection;
	}

	private String embed(@NonNull final String sql, @NonNull final String[] projection, final String selection, final String groupBy, final String sortOrder)
	{
		final SQLiteQueryBuilder embeddingQueryBuilder = new SQLiteQueryBuilder();
		embeddingQueryBuilder.setTables('(' + sql + ')');
		return embeddingQueryBuilder.buildQuery(projection, selection, groupBy, null, sortOrder, null);
	}
}
