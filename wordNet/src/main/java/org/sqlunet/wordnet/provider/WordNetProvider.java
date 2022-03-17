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
	 * @param uri           uri
	 * @param projection    projection
	 * @param selection     selection
	 * @param selectionArgs selection arguments
	 * @param sortOrder     sort order
	 * @return cursor
	 */
	@SuppressWarnings("boxing")
	@Nullable
	@Override
	public Cursor query(@NonNull final Uri uri, final String[] projection, @Nullable final String selection, final String[] selectionArgs, final String sortOrder)
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

		String[] actualProjection = projection;
		String actualSelection = selection;
		final int code = WordNetProvider.uriMatcher.match(uri);
		Log.d(WordNetProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code));
		String groupBy = null;
		String table;

		String[] actualProjection2 = projection;
		String actualSelection2 = selection;
		String groupBy2 = null;
		String table2;
		switch (code)
		{
			// T A B L E
			// table uri : last element is table

			case WORDS:
				table = uri.getLastPathSegment();
				table2 = Queries.WORDS.TABLE;
				break;

			case SENSES:
				table = uri.getLastPathSegment();
				table2 = Queries.SENSES.TABLE;
				break;

			case SYNSETS:
				table = uri.getLastPathSegment();
				table2 = Queries.SYNSETS.TABLE;
				break;

			case SEMRELATIONS:
				table = uri.getLastPathSegment();
				table2 = Queries.SEMRELATIONS.TABLE;
				break;

			case LEXRELATIONS:
				table = uri.getLastPathSegment();
				table2 = Queries.LEXRELATIONS.TABLE;
				break;

			case RELATIONS:
				table = uri.getLastPathSegment();
				table2 = Queries.RELATIONS.TABLE;
				break;

			case POSES:
				table = uri.getLastPathSegment();
				table2 = Queries.POSES.TABLE;
				break;

			case DOMAINS:
				table = uri.getLastPathSegment();
				table2 = Queries.DOMAINS.TABLE;
				break;

			case ADJPOSITIONS:
				table = uri.getLastPathSegment();
				table2 = Queries.ADJPOSITIONS.TABLE;
				break;

			case SAMPLES:
				table = uri.getLastPathSegment();
				table2 = Queries.SAMPLES.TABLE;
				break;

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case WORD:
				table = Words.TABLE;
				table2 = Queries.WORD.TABLE;
				if (actualSelection != null)
				{
					actualSelection += " AND ";
				}
				else
				{
					actualSelection = "";
				}
				actualSelection += Words.WORDID + " = " + uri.getLastPathSegment();
				break;

			case SENSE:
				table = Senses.TABLE;
				table2 = Queries.SENSE.TABLE;
				if (actualSelection != null)
				{
					actualSelection += " AND ";
				}
				else
				{
					actualSelection = "";
				}
				actualSelection += Senses.SENSEID + " = " + uri.getLastPathSegment();
				break;

			case SYNSET:
				table = uri.getLastPathSegment();
				table2 = Queries.SYNSET.TABLE;
				if (actualSelection != null)
				{
					actualSelection += " AND ";
				}
				else
				{
					actualSelection = "";
				}
				actualSelection += Synsets.SYNSETID + " = " + uri.getLastPathSegment();
				break;

			// V I E W S

			case DICT:
				table = uri.getLastPathSegment();
				table2 = Queries.DICT.TABLE;
				break;

			// J O I N S

			case WORDS_SENSES_SYNSETS:
				table = "words AS " + WordNetContract.WORD + " " + //
						"LEFT JOIN senses AS " + WordNetContract.SENSE + " USING (wordid) " + //
						"LEFT JOIN synsets AS " + WordNetContract.SYNSET + " USING (synsetid)";
				table2 = Queries.WORDS_SENSES_SYNSETS.TABLE;
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS:
				table = "words AS " + WordNetContract.WORD + " " + //
						"LEFT JOIN senses AS " + WordNetContract.SENSE + " USING (wordid) " + //
						"LEFT JOIN casedwords AS " + WordNetContract.CASED + " USING (wordid,casedwordid) " + //
						"LEFT JOIN synsets AS " + WordNetContract.SYNSET + " USING (synsetid)";
				table2 = Queries.WORDS_SENSES_CASEDWORDS_SYNSETS.TABLE;
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS:
				table = "words AS " + WordNetContract.WORD + " " + //
						"LEFT JOIN senses AS " + WordNetContract.SENSE + " USING (wordid) " + //
						"LEFT JOIN casedwords AS " + WordNetContract.CASED + " USING (wordid,casedwordid) " + //
						"LEFT JOIN synsets AS " + WordNetContract.SYNSET + " USING (synsetid) " + //
						"LEFT JOIN poses AS " + WordNetContract.POS + " USING (posid) " + //
						"LEFT JOIN domains AS " + WordNetContract.DOMAIN + " USING (domainid)";
				table2 = Queries.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case SENSES_WORDS:
				table = "senses AS " + WordNetContract.SENSE + " " + //
						"LEFT JOIN words AS " + WordNetContract.WORD + " USING (wordid)";
				table2 = Queries.SENSES_WORDS.TABLE;
				break;

			case SENSES_WORDS_BY_SYNSET:
				groupBy = "synsetid";
				groupBy2 = Queries.SENSES_WORDS_BY_SYNSET.GROUPBY;
				table = "senses AS " + WordNetContract.SENSE + " " + //
						"LEFT JOIN words AS " + WordNetContract.WORD + " USING (wordid)";
				table2 = Queries.SENSES_WORDS_BY_SYNSET.TABLE;
				actualProjection = BaseProvider.appendProjection(actualProjection, "GROUP_CONCAT(words.word, ', ' ) AS " + Senses_Words.MEMBERS);
				actualProjection2 = BaseProvider.appendProjection(actualProjection2, Queries.SENSES_WORDS_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members\\}", WordNetContract.MEMBERS));
				break;

			case SENSES_SYNSETS_POSES_DOMAINS:
				table = "senses AS " + WordNetContract.SENSE + " " + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET + " USING (synsetid) " + //
						"LEFT JOIN poses AS " + WordNetContract.POS + " USING (posid) " + //
						"LEFT JOIN domains AS " + WordNetContract.DOMAIN + " USING (domainid)";
				table2 = Queries.SENSES_SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case SYNSETS_POSES_DOMAINS:
				table = "synsets AS " + WordNetContract.SYNSET + " " + //
						"LEFT JOIN poses AS " + WordNetContract.POS + " USING (posid) " + //
						"LEFT JOIN domains AS " + WordNetContract.DOMAIN + " USING (domainid)";
				table2 = Queries.SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET:
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
				groupBy = BaseModule.TARGET_SYNSETID + "," + WordNetContract.TYPE + ",relation,relationid," + BaseModule.TARGET_WORDID + ',' + BaseModule.TARGET_WORD;
				groupBy2 = Queries.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET.GROUPBY
						.replaceAll("#\\{query_target_synsetid\\}", BaseModule.TARGET_SYNSETID) //
						.replaceAll("#\\{query_target_wordid\\}", BaseModule.TARGET_WORDID) //
						.replaceAll("#\\{query_target_word\\}", BaseModule.TARGET_WORD);
				assert selection != null;
				final String[] selections = selection.split("/\\*\\*/\\|/\\*\\*/");
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
				table = "( " + subQuery + " ) AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid " + //
						"LEFT JOIN senses ON " + WordNetContract.SYNSET2 + ".synsetid = senses.synsetid " + //
						"LEFT JOIN words AS " + WordNetContract.WORD + " USING (wordid) " + //
						"LEFT JOIN words AS " + WordNetContract.WORD2 + " ON " + WordNetContract.RELATION + ".word2id = " + WordNetContract.WORD2 + ".wordid";
				table2 = Queries.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET.TABLE.replaceFirst("#\\{query\\}", subQuery);
				actualSelection = null;
				actualSelection2 = null;
			}
			break;

			case SEMRELATIONS_SYNSETS:
				table = "semrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid";
				table2 = Queries.SEMRELATIONS_SYNSETS.TABLE;
				break;

			case SEMRELATIONS_SYNSETS_X:
				table = "semrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid ";
				table2 = Queries.SEMRELATIONS_SYNSETS_X.TABLE;
				break;

			case SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET:
				groupBy = WordNetContract.SYNSET2 + ".synsetid";
				groupBy2 = Queries.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.GROUPBY;
				table = "semrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid " + //
						"LEFT JOIN senses ON " + WordNetContract.SYNSET2 + ".synsetid = senses.synsetid " + //
						"LEFT JOIN words USING (wordid)";
				table2 = Queries.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.TABLE;
				actualProjection = BaseProvider.appendProjection(actualProjection, "GROUP_CONCAT(words.word, ', ' ) AS " + SemRelations_Synsets_Words_X.MEMBERS2);
				actualProjection2 = BaseProvider.appendProjection(actualProjection2, Queries.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members2\\}", WordNetContract.MEMBERS2));
				break;

			case LEXRELATIONS_SENSES:
				table = "lexrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.RELATION + ".word2id = " + WordNetContract.WORD + ".wordid";
				table2 = Queries.LEXRELATIONS_SENSES.TABLE;
				break;

			case LEXRELATIONS_SENSES_X:
				table = "lexrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN relations USING (relationid)" + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.RELATION + ".word2id = " + WordNetContract.WORD + ".wordid ";
				table2 = Queries.LEXRELATIONS_SENSES_X.TABLE;
				break;

			case LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				groupBy = WordNetContract.SYNSET2 + ".synsetid";
				groupBy2 = Queries.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.GROUPBY;
				table = "lexrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.RELATION + ".word2id = " + WordNetContract.WORD + ".wordid " + //
						"LEFT JOIN senses AS " + WordNetContract.SENSE + " ON " + WordNetContract.SYNSET2 + ".synsetid = " + WordNetContract.SENSE + ".synsetid " + //
						"LEFT JOIN words AS " + WordNetContract.WORD2 + " USING (wordid)";
				table2 = Queries.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.TABLE;
				actualProjection = BaseProvider.appendProjection(actualProjection, "GROUP_CONCAT(DISTINCT " + WordNetContract.WORD2 + ".word) AS " + LexRelations_Senses_Words_X.MEMBERS2);
				actualProjection2 = BaseProvider.appendProjection(actualProjection2, Queries.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members2\\}", WordNetContract.MEMBERS2));
				break;

			case SENSES_VFRAMES:
				table = "senses_vframes " + //
						"LEFT JOIN vframes USING (frameid)";
				table2 = Queries.SENSES_VFRAMES.TABLE;
				break;

			case SENSES_VTEMPLATES:
				table = "senses_vtemplates " + //
						"LEFT JOIN vtemplates USING (templateid)";
				table2 = Queries.SENSES_VTEMPLATES.TABLE;
				break;

			case SENSES_ADJPOSITIONS:
				table = "senses_adjpositions " + //
						"LEFT JOIN adjpositions USING (positionid)";
				table2 = Queries.SENSES_ADJPOSITIONS.TABLE;
				break;

			case LEXES_MORPHS:
				table = "lexes_morphs " + //
						"LEFT JOIN morphs USING (morphid)";
				table2 = Queries.LEXES_MORPHS.TABLE;
				break;

			case WORDS_LEXES_MORPHS_BY_WORD:
				groupBy = "wordid";
				groupBy2 = Queries.WORDS_LEXES_MORPHS_BY_WORD.GROUPBY;
				//$FALL-THROUGH$
				//noinspection fallthrough

			case WORDS_LEXES_MORPHS:
				table = "words " + //
						"LEFT JOIN lexes_morphs USING (wordid) " + //
						"LEFT JOIN morphs USING (morphid)";
				table2 = Queries.WORDS_LEXES_MORPHS.TABLE;
				break;

			// T E X T S E A R C H

			case LOOKUP_FTS_WORDS:
				table = "words_word_fts4";
				table2 = Queries.LOOKUP_FTS_WORDS.TABLE;
				break;

			case LOOKUP_FTS_DEFINITIONS:
				table = "synsets_definition_fts4";
				table2 = Queries.LOOKUP_FTS_DEFINITIONS.TABLE;
				break;

			case LOOKUP_FTS_SAMPLES:
				table = "samples_sample_fts4";
				table2 = Queries.LOOKUP_FTS_SAMPLES.TABLE;
				break;

			// S U G G E S T

			case SUGGEST_WORDS:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}
				actualProjection = new String[]{"wordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				actualProjection2 = Queries.SUGGEST_WORDS.PROJECTION;
				actualSelection = "word LIKE ? || '%'";
				actualSelection2 = Queries.SUGGEST_WORDS.SELECTION;
				String[] actualSelectionArgs = {last};
				String[] actualSelectionArgs2 = Queries.SUGGEST_WORDS.ARGS;
				table = "words";
				table2 = Queries.SUGGEST_WORDS.TABLE;
//				assert equals(table, table2) : table + " != " + table2;
//				assert equals(actualSelection, actualSelection2) : actualSelection + " != " + actualSelection2;
//				assert equals(groupBy, groupBy2) : actualSelection + " != " + actualSelection2;
//				assert Arrays.equals(actualProjection, actualProjection2) : Arrays.toString(actualProjection) + " != " + Arrays.toString(actualProjection2);
//				assert Arrays.equals(actualSelectionArgs, actualSelectionArgs2) : Arrays.toString(actualSelectionArgs) + " != " + Arrays.toString(actualSelectionArgs2);
				return this.db.query(table, actualProjection, actualSelection, actualSelectionArgs, null, null, null);
			}

			case SUGGEST_FTS_WORDS:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}

				actualProjection = new String[]{"wordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				actualProjection2 = Queries.SUGGEST_FTS_WORDS.PROJECTION;
				actualSelection = "word MATCH ?";
				actualSelection2 = Queries.SUGGEST_FTS_WORDS.SELECTION;
				String[] actualSelectionArgs = {last + '*'};
				String[] actualSelectionArgs2 = Queries.SUGGEST_FTS_WORDS.ARGS;
				table = "words_word_fts4";
				table2 = Queries.SUGGEST_FTS_WORDS.TABLE;
//				assert equals(table, table2);
//				assert equals(actualSelection, actualSelection2);
//				assert equals(groupBy, groupBy2);
//				assert Arrays.equals(actualProjection, actualProjection2);
//				assert Arrays.equals(actualSelectionArgs, actualSelectionArgs2);
				return this.db.query(table, actualProjection, actualSelection, actualSelectionArgs, null, null, null);
			}

			case SUGGEST_FTS_DEFINITIONS:
			{
				final String last = uri.getLastPathSegment();
				actualProjection = new String[]{"synsetid AS _id", //
						"definition AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"definition AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				actualProjection2 = Queries.SUGGEST_FTS_DEFINITIONS.PROJECTION;
				actualSelection = "definition MATCH ?";
				actualSelection2 = Queries.SUGGEST_FTS_DEFINITIONS.SELECTION;
				String[] actualSelectionArgs = new String[]{last + '*'};
				String[] actualSelectionArgs2 = Queries.SUGGEST_FTS_DEFINITIONS.ARGS;
				table = "synsets_definition_fts4";
				table2 = Queries.SUGGEST_FTS_DEFINITIONS.TABLE;
//				assert equals(table, table2);
//				assert equals(actualSelection, actualSelection2);
//				assert equals(groupBy, groupBy2);
//				assert Arrays.equals(actualProjection, actualProjection2);
//				assert Arrays.equals(actualSelectionArgs, actualSelectionArgs2);
				return this.db.query(table, actualProjection, actualSelection, actualSelectionArgs, null, null, null);
			}

			case SUGGEST_FTS_SAMPLES:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}
				actualProjection = new String[]{"sampleid AS _id", //
						"sample AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"sample AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				actualProjection2 = Queries.SUGGEST_FTS_SAMPLES.PROJECTION;
				actualSelection = "sample MATCH ?";
				actualSelection2 = Queries.SUGGEST_FTS_SAMPLES.SELECTION;
				String[] actualSelectionArgs = new String[]{last + '*'};
				String[] actualSelectionArgs2 = Queries.SUGGEST_FTS_SAMPLES.ARGS;
				table = "samples_sample_fts4";
				table2 = Queries.SUGGEST_FTS_SAMPLES.TABLE;
//				assert equals(table, table2);
//				assert equals(actualSelection, actualSelection2);
//				assert equals(groupBy, groupBy2);
//				assert Arrays.equals(actualProjection, actualProjection2);
//				assert Arrays.equals(actualSelectionArgs, actualSelectionArgs2);
				return this.db.query(table, actualProjection, actualSelection, actualSelectionArgs, null, null, null);
			}

			case UriMatcher.NO_MATCH:
			default:
				throw new RuntimeException("Malformed URI " + uri);
		}

		assert equals(table, table2) : table +"!="+ table2;
		assert equals(actualSelection, actualSelection2) : actualSelection +"!="+ actualSelection2;
		assert equals(groupBy, groupBy2) : groupBy +"!="+ groupBy2;
		assert Arrays.equals(actualProjection, actualProjection2) : Arrays.toString(actualProjection) +"!="+ Arrays.toString(actualProjection2);
		//assert Arrays.equals(actualSelectionArgs, actualSelectionArgs2);

		final String sql = SQLiteQueryBuilder.buildQueryString(false, table, actualProjection, actualSelection, groupBy, null, sortOrder, null);
		logSql(sql, selectionArgs);
		if (BaseProvider.logSql)
		{
			Log.d(WordNetProvider.TAG + "SQL", SqlFormatter.format(sql).toString());
			Log.d(WordNetProvider.TAG + "ARG", BaseProvider.argsToString(selectionArgs));
		}

		// do query
		try
		{
			final Cursor cursor = this.db.rawQuery(sql, selectionArgs);
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

	private static boolean equals(Object a, Object b)
	{
		return (a == b) || (a != null && a.equals(b));
	}
}
