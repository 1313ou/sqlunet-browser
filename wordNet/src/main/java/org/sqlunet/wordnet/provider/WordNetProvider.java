/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.provider;

import android.app.SearchManager;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;

import org.sqlunet.provider.BaseProvider;
import org.sqlunet.sql.SqlFormatter;
import org.sqlunet.wordnet.loaders.BaseModule;
import org.sqlunet.wordnet.provider.WordNetContract.AdjPositions;
import org.sqlunet.wordnet.provider.WordNetContract.BaseRelations;
import org.sqlunet.wordnet.provider.WordNetContract.BaseRelations_Senses_Words_X;
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

	// table codes
	static final int WORDS = 10;
	static final int WORD = 11;
	static final int SENSES = 20;
	static final int SENSE = 21;
	static final int SYNSETS = 30;
	static final int SYNSET = 31;
	static final int SEMRELATIONS = 40;
	static final int LEXRELATIONS = 50;
	static final int RELATIONS = 60;
	static final int POSES = 70;
	static final int DOMAINS = 80;
	static final int ADJPOSITIONS = 90;
	static final int SAMPLES = 100;

	// view codes
	static final int DICT = 200;

	// join codes
	static final int WORDS_SENSES_SYNSETS = 310;
	static final int WORDS_SENSES_CASEDWORDS_SYNSETS = 311;
	static final int WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS = 312;
	static final int SENSES_WORDS = 320;
	static final int SENSES_WORDS_BY_SYNSET = 321;
	static final int SENSES_SYNSETS_POSES_DOMAINS = 330;
	static final int SYNSETS_POSES_DOMAINS = 340;

	static final int ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET = 400;
	static final int SEMRELATIONS_SYNSETS = 410;
	static final int SEMRELATIONS_SYNSETS_X = 411;
	static final int SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET = 412;
	static final int LEXRELATIONS_SENSES = 420;
	static final int LEXRELATIONS_SENSES_X = 421;
	static final int LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET = 422;

	static final int SENSES_VFRAMES = 510;
	static final int SENSES_VTEMPLATES = 515;
	static final int SENSES_ADJPOSITIONS = 520;
	static final int LEXES_MORPHS = 530;
	static final int WORDS_LEXES_MORPHS = 541;
	static final int WORDS_LEXES_MORPHS_BY_WORD = 542;

	// search text codes
	static final int LOOKUP_FTS_WORDS = 810;
	static final int LOOKUP_FTS_DEFINITIONS = 820;
	static final int LOOKUP_FTS_SAMPLES = 830;

	// suggest codes
	static final int SUGGEST_WORDS = 900;
	static final int SUGGEST_FTS_WORDS = 910;
	static final int SUGGEST_FTS_DEFINITIONS = 920;
	static final int SUGGEST_FTS_SAMPLES = 930;

	static private void matchURIs()
	{
		// table
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Words.TABLE, WordNetProvider.WORDS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Words.TABLE + "/#", WordNetProvider.WORD);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Senses.TABLE, WordNetProvider.SENSES);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Senses.TABLE + "/#", WordNetProvider.SENSE);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Synsets.TABLE, WordNetProvider.SYNSETS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Synsets.TABLE + "/#", WordNetProvider.SYNSET);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, SemRelations.TABLE, WordNetProvider.SEMRELATIONS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, LexRelations.TABLE, WordNetProvider.LEXRELATIONS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Relations.TABLE, WordNetProvider.RELATIONS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Poses.TABLE, WordNetProvider.POSES);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Domains.TABLE, WordNetProvider.DOMAINS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, AdjPositions.TABLE, WordNetProvider.ADJPOSITIONS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Samples.TABLE, WordNetProvider.SAMPLES);

		// view
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Dict.TABLE, WordNetProvider.DICT);

		// joins
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Words_Senses_Synsets.TABLE, WordNetProvider.WORDS_SENSES_SYNSETS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Words_Senses_CasedWords_Synsets.TABLE, WordNetProvider.WORDS_SENSES_CASEDWORDS_SYNSETS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Words_Senses_CasedWords_Synsets_Poses_Domains.TABLE, WordNetProvider.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Senses_Words.TABLE, WordNetProvider.SENSES_WORDS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Senses_Words.TABLE_BY_SYNSET, WordNetProvider.SENSES_WORDS_BY_SYNSET);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Senses_Synsets_Poses_Domains.TABLE, WordNetProvider.SENSES_SYNSETS_POSES_DOMAINS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Synsets_Poses_Domains.TABLE, WordNetProvider.SYNSETS_POSES_DOMAINS);

		WordNetProvider.uriMatcher.addURI(AUTHORITY, BaseRelations_Senses_Words_X.TABLE_BY_SYNSET, WordNetProvider.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET);

		WordNetProvider.uriMatcher.addURI(AUTHORITY, SemRelations_Synsets.TABLE, WordNetProvider.SEMRELATIONS_SYNSETS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, SemRelations_Synsets_X.TABLE, WordNetProvider.SEMRELATIONS_SYNSETS_X);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, SemRelations_Synsets_Words_X.TABLE_BY_SYNSET, WordNetProvider.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET);

		WordNetProvider.uriMatcher.addURI(AUTHORITY, LexRelations_Senses.TABLE, WordNetProvider.LEXRELATIONS_SENSES);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, LexRelations_Senses_X.TABLE, WordNetProvider.LEXRELATIONS_SENSES_X);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, LexRelations_Senses_Words_X.TABLE_BY_SYNSET, WordNetProvider.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET);

		WordNetProvider.uriMatcher.addURI(AUTHORITY, Senses_VerbFrames.TABLE, WordNetProvider.SENSES_VFRAMES);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Senses_VerbTemplates.TABLE, WordNetProvider.SENSES_VTEMPLATES);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Senses_AdjPositions.TABLE, WordNetProvider.SENSES_ADJPOSITIONS);

		WordNetProvider.uriMatcher.addURI(AUTHORITY, Lexes_Morphs.TABLE, WordNetProvider.LEXES_MORPHS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Words_Lexes_Morphs.TABLE, WordNetProvider.WORDS_LEXES_MORPHS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Words_Lexes_Morphs.TABLE_BY_WORD, WordNetProvider.WORDS_LEXES_MORPHS_BY_WORD);

		// search text
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_Words.TABLE, WordNetProvider.LOOKUP_FTS_WORDS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_Definitions.TABLE, WordNetProvider.LOOKUP_FTS_DEFINITIONS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_Samples.TABLE, WordNetProvider.LOOKUP_FTS_SAMPLES);

		// search
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_Words.TABLE + "/*", WordNetProvider.SUGGEST_WORDS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_Words.TABLE + "/", WordNetProvider.SUGGEST_WORDS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FTS_Words.TABLE + "/*", WordNetProvider.SUGGEST_FTS_WORDS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FTS_Words.TABLE + "/", WordNetProvider.SUGGEST_FTS_WORDS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FTS_Definitions.TABLE + "/*", WordNetProvider.SUGGEST_FTS_DEFINITIONS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FTS_Definitions.TABLE + "/", WordNetProvider.SUGGEST_FTS_DEFINITIONS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FTS_Samples.TABLE + "/*", WordNetProvider.SUGGEST_FTS_SAMPLES);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FTS_Samples.TABLE + "/", WordNetProvider.SUGGEST_FTS_SAMPLES);
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

			case WORDS:
			case WORD:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words.TABLE;
			case SENSES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses.TABLE;
			case SENSE:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses.TABLE;
			case SYNSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Synsets.TABLE;
			case SYNSET:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Synsets.TABLE;
			case SEMRELATIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + SemRelations.TABLE;
			case LEXRELATIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexRelations.TABLE;
			case RELATIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Relations.TABLE;
			case POSES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Poses.TABLE;
			case ADJPOSITIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + AdjPositions.TABLE;
			case DOMAINS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Domains.TABLE;
			case SAMPLES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Samples.TABLE;

			// V I E W S

			case DICT:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Dict.TABLE;
			case WORDS_SENSES_SYNSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_Senses_Synsets.TABLE;
			case WORDS_SENSES_CASEDWORDS_SYNSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_Senses_CasedWords_Synsets.TABLE;
			case WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_Senses_CasedWords_Synsets_Poses_Domains.TABLE;

			// J O I N S

			case SENSES_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses_Words.TABLE;
			case SENSES_WORDS_BY_SYNSET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses_Words.TABLE_BY_SYNSET;
			case SENSES_SYNSETS_POSES_DOMAINS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses_Synsets_Poses_Domains.TABLE;
			case SYNSETS_POSES_DOMAINS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Synsets_Poses_Domains.TABLE;
			case ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + BaseRelations_Senses_Words_X.TABLE_BY_SYNSET;
			case SEMRELATIONS_SYNSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + SemRelations_Synsets.TABLE;
			case SEMRELATIONS_SYNSETS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + SemRelations_Synsets_X.TABLE;
			case SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + SemRelations_Synsets_Words_X.TABLE_BY_SYNSET;
			case LEXRELATIONS_SENSES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexRelations_Senses.TABLE;
			case LEXRELATIONS_SENSES_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexRelations_Senses_X.TABLE;
			case LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexRelations_Senses_Words_X.TABLE_BY_SYNSET;
			case SENSES_VFRAMES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses_VerbFrames.TABLE;
			case SENSES_VTEMPLATES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses_VerbTemplates.TABLE;
			case SENSES_ADJPOSITIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses_AdjPositions.TABLE;
			case LEXES_MORPHS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lexes_Morphs.TABLE;
			case WORDS_LEXES_MORPHS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_Lexes_Morphs.TABLE;
			case WORDS_LEXES_MORPHS_BY_WORD:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_Lexes_Morphs.TABLE_BY_WORD;

			// T E X T   L O O K U P S

			case LOOKUP_FTS_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words.TABLE;
			case LOOKUP_FTS_DEFINITIONS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Synsets.TABLE;
			case LOOKUP_FTS_SAMPLES:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Samples.TABLE;

			// S E A R C H

			case SUGGEST_FTS_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words.TABLE;
			case SUGGEST_FTS_DEFINITIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Synsets.TABLE;
			case SUGGEST_FTS_SAMPLES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Samples.TABLE;
			default:
				throw new UnsupportedOperationException("Illegal MIME type");
		}
	}

	// Q U E R Y

	@Nullable
	@Override
	public Cursor query(@NonNull final Uri uri, @Nullable final String[] projection, @Nullable final Bundle queryArgs, @Nullable final CancellationSignal cancellationSignal)
	{
		return super.query(uri, projection, queryArgs, cancellationSignal);
	}

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

		String table;
		String[] projection = projection0;
		String selection = selection0;
		String groupBy = null;
		switch (code)
		{
			// T A B L E
			// table uri : last element is table

			case WORDS:
				table = Queries.WORDS.TABLE;
				break;

			case SENSES:
				table = Queries.SENSES.TABLE;
				break;

			case SYNSETS:
				table = Queries.SYNSETS.TABLE;
				break;

			case SEMRELATIONS:
				table = Queries.SEMRELATIONS.TABLE;
				break;

			case LEXRELATIONS:
				table = Queries.LEXRELATIONS.TABLE;
				break;

			case RELATIONS:
				table = Queries.RELATIONS.TABLE;
				break;

			case POSES:
				table = Queries.POSES.TABLE;
				break;

			case DOMAINS:
				table = Queries.DOMAINS.TABLE;
				break;

			case ADJPOSITIONS:
				table = Queries.ADJPOSITIONS.TABLE;
				break;

			case SAMPLES:
				table = Queries.SAMPLES.TABLE;
				break;

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case WORD:
				table = Queries.WORD.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += Words.WORDID + " = " + uri.getLastPathSegment();
				break;

			case SENSE:
				table = Queries.SENSE.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += Senses.SENSEID + " = " + uri.getLastPathSegment();
				break;

			case SYNSET:
				table = Queries.SYNSET.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += Synsets.SYNSETID + " = " + uri.getLastPathSegment();
				break;

			// V I E W S

			case DICT:
				table = Queries.DICT.TABLE;
				break;

			// J O I N S

			case WORDS_SENSES_SYNSETS:
				table = Queries.WORDS_SENSES_SYNSETS.TABLE;
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS:
				table = Queries.WORDS_SENSES_CASEDWORDS_SYNSETS.TABLE;
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS:
				table = Queries.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case SENSES_WORDS:
				table = Queries.SENSES_WORDS.TABLE;
				break;

			case SENSES_WORDS_BY_SYNSET:
				table = Queries.SENSES_WORDS_BY_SYNSET.TABLE;
				projection = BaseProvider.appendProjection(projection, Queries.SENSES_WORDS_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members\\}", WordNetContract.MEMBERS));
				groupBy = Queries.SENSES_WORDS_BY_SYNSET.GROUPBY;
				break;

			case SENSES_SYNSETS_POSES_DOMAINS:
				table = Queries.SENSES_SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case SYNSETS_POSES_DOMAINS:
				table = Queries.SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				final String subQuery = makeAllRelationsSubQuery(selection0);
				table = Queries.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET.TABLE.replaceFirst("#\\{query\\}", subQuery);
				selection = null;
				groupBy = Queries.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET.GROUPBY.replaceAll("#\\{query_target_synsetid\\}", BaseModule.TARGET_SYNSETID) //
						.replaceAll("#\\{query_target_wordid\\}", BaseModule.TARGET_WORDID) //
						.replaceAll("#\\{query_target_word\\}", BaseModule.TARGET_WORD);
				break;

			case SEMRELATIONS_SYNSETS:
				table = Queries.SEMRELATIONS_SYNSETS.TABLE;
				break;

			case SEMRELATIONS_SYNSETS_X:
				table = Queries.SEMRELATIONS_SYNSETS_X.TABLE;
				break;

			case SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET:
				table = Queries.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.TABLE;
				groupBy = Queries.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.GROUPBY;
				projection = BaseProvider.appendProjection(projection, Queries.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members2\\}", WordNetContract.MEMBERS2));
				break;

			case LEXRELATIONS_SENSES:
				table = Queries.LEXRELATIONS_SENSES.TABLE;
				break;

			case LEXRELATIONS_SENSES_X:
				table = Queries.LEXRELATIONS_SENSES_X.TABLE;
				break;

			case LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				table = Queries.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.TABLE;
				projection = BaseProvider.appendProjection(projection, Queries.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members2\\}", WordNetContract.MEMBERS2));
				groupBy = Queries.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.GROUPBY;
				break;

			case SENSES_VFRAMES:
				table = Queries.SENSES_VFRAMES.TABLE;
				break;

			case SENSES_VTEMPLATES:
				table = Queries.SENSES_VTEMPLATES.TABLE;
				break;

			case SENSES_ADJPOSITIONS:
				table = Queries.SENSES_ADJPOSITIONS.TABLE;
				break;

			case LEXES_MORPHS:
				table = Queries.LEXES_MORPHS.TABLE;
				break;

			case WORDS_LEXES_MORPHS_BY_WORD:
				table = Queries.WORDS_LEXES_MORPHS.TABLE;
				groupBy = Queries.WORDS_LEXES_MORPHS_BY_WORD.GROUPBY;
				break;

			case WORDS_LEXES_MORPHS:
				table = Queries.WORDS_LEXES_MORPHS.TABLE;
				break;

			// T E X T S E A R C H

			case LOOKUP_FTS_WORDS:
				table = Queries.LOOKUP_FTS_WORDS.TABLE;
				break;

			case LOOKUP_FTS_DEFINITIONS:
				table = Queries.LOOKUP_FTS_DEFINITIONS.TABLE;
				break;

			case LOOKUP_FTS_SAMPLES:
				table = Queries.LOOKUP_FTS_SAMPLES.TABLE;
				break;

			// S U G G E S T

			case SUGGEST_WORDS:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}
				table = Queries.SUGGEST_WORDS.TABLE;
				projection = Queries.SUGGEST_WORDS.PROJECTION;
				selection = Queries.SUGGEST_WORDS.SELECTION;
				String[] actualSelectionArgs = {Queries.SUGGEST_WORDS.ARGS[0].replaceAll("#\\{uri_last\\}", last)};
				return this.db.query(table, projection, selection, actualSelectionArgs, null, null, null);
			}

			case SUGGEST_FTS_WORDS:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}
				table = Queries.SUGGEST_FTS_WORDS.TABLE;
				projection = Queries.SUGGEST_FTS_WORDS.PROJECTION;
				selection = Queries.SUGGEST_FTS_WORDS.SELECTION;
				String[] selectionArgs = {Queries.SUGGEST_FTS_WORDS.ARGS[0].replaceAll("#\\{uri_last\\}", last)};
				return this.db.query(table, projection, selection, selectionArgs, null, null, null);
			}

			case SUGGEST_FTS_DEFINITIONS:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}
				table = Queries.SUGGEST_FTS_DEFINITIONS.TABLE;
				projection = Queries.SUGGEST_FTS_DEFINITIONS.PROJECTION;
				selection = Queries.SUGGEST_FTS_DEFINITIONS.SELECTION;
				String[] selectionArgs = {Queries.SUGGEST_FTS_DEFINITIONS.ARGS[0].replaceAll("#\\{uri_last\\}", last)};
				return this.db.query(table, projection, selection, selectionArgs, null, null, null);
			}

			case SUGGEST_FTS_SAMPLES:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}
				projection = Queries.SUGGEST_FTS_SAMPLES.PROJECTION;
				selection = Queries.SUGGEST_FTS_SAMPLES.SELECTION;
				String[] actualSelectionArgs = {Queries.SUGGEST_FTS_SAMPLES.ARGS[0].replaceAll("#\\{uri_last\\}", last)};
				table = Queries.SUGGEST_FTS_SAMPLES.TABLE;
				return this.db.query(table, projection, selection, actualSelectionArgs, null, null, null);
			}

			case UriMatcher.NO_MATCH:
			default:
				throw new RuntimeException("Malformed URI " + uri);
		}

		final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, selection, groupBy, null, sortOrder0, null);
		logSql(sql, selectionArgs0);
		if (BaseProvider.logSql)
		{
			Log.d(WordNetProvider.TAG + "SQL", SqlFormatter.format(sql).toString());
			Log.d(WordNetProvider.TAG + "ARG", BaseProvider.argsToString(selectionArgs0));
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

	/**
	 * All relations subquery
	 *
	 * @param selection0 input selection
	 * @return all relations subquery
	 */
	private String makeAllRelationsSubQuery(final String selection0)
	{
		final String semTable = SemRelations.TABLE;
		final String lexTable = LexRelations.TABLE;
		final String[] projection1 = { //
				SemRelations.RELATIONID, //
				SemRelations.SYNSETID1, //
				SemRelations.SYNSETID2, //
		};
		final String[] projection2 = { //
				LexRelations.RELATIONID, //
				LexRelations.WORDID1, //
				LexRelations.SYNSETID1, //
				LexRelations.WORDID2, //
				LexRelations.SYNSETID2, //
		};
		final String[] unionProjection = { //
				BaseRelations.RELATIONID, //
				BaseRelations.WORDID1, //
				BaseRelations.SYNSETID1, //
				BaseRelations.WORDID2, //
				BaseRelations.SYNSETID2, //
		};
		assert selection0 != null;
		final String[] selections = selection0.split("/\\*\\*/\\|/\\*\\*/");
		final String subQuery = makeQuery(semTable, //
				lexTable, //
				projection1, //
				projection2, //
				unionProjection, //
				WordNetContract.TYPE, //
				"sem", //
				"lex", //
				selections[0], //
				selections[1]);
		return subQuery;
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
	static String makeQuery(//
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
		final String[] actualUnionProjection = BaseProvider.appendProjection(unionProjection, WordNetContract.TYPE);
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
		final String subQuery2 = subQueryBuilder2.buildUnionSubQuery(WordNetContract.TYPE, //
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
