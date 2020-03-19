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
import org.sqlunet.wordnet.provider.WordNetContract.AdjPositionTypes;
import org.sqlunet.wordnet.provider.WordNetContract.AdjPositions_AdjPositionTypes;
import org.sqlunet.wordnet.provider.WordNetContract.Dict;
import org.sqlunet.wordnet.provider.WordNetContract.LexDomains;
import org.sqlunet.wordnet.provider.WordNetContract.LexLinks;
import org.sqlunet.wordnet.provider.WordNetContract.LexLinks_Senses;
import org.sqlunet.wordnet.provider.WordNetContract.LexLinks_Senses_Words_X;
import org.sqlunet.wordnet.provider.WordNetContract.LexLinks_Senses_X;
import org.sqlunet.wordnet.provider.WordNetContract.LinkTypes;
import org.sqlunet.wordnet.provider.WordNetContract.Links;
import org.sqlunet.wordnet.provider.WordNetContract.Links_Senses_Words_X;
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Definitions;
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Samples;
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Words;
import org.sqlunet.wordnet.provider.WordNetContract.MorphMaps_Morphs;
import org.sqlunet.wordnet.provider.WordNetContract.PosTypes;
import org.sqlunet.wordnet.provider.WordNetContract.Samples;
import org.sqlunet.wordnet.provider.WordNetContract.SemLinks;
import org.sqlunet.wordnet.provider.WordNetContract.SemLinks_Synsets;
import org.sqlunet.wordnet.provider.WordNetContract.SemLinks_Synsets_Words_X;
import org.sqlunet.wordnet.provider.WordNetContract.SemLinks_Synsets_X;
import org.sqlunet.wordnet.provider.WordNetContract.Senses;
import org.sqlunet.wordnet.provider.WordNetContract.Senses_Synsets_PosTypes_LexDomains;
import org.sqlunet.wordnet.provider.WordNetContract.Senses_Words;
import org.sqlunet.wordnet.provider.WordNetContract.Suggest_FTS_Definitions;
import org.sqlunet.wordnet.provider.WordNetContract.Suggest_FTS_Samples;
import org.sqlunet.wordnet.provider.WordNetContract.Suggest_FTS_Words;
import org.sqlunet.wordnet.provider.WordNetContract.Suggest_Words;
import org.sqlunet.wordnet.provider.WordNetContract.Synsets;
import org.sqlunet.wordnet.provider.WordNetContract.Synsets_PosTypes_LexDomains;
import org.sqlunet.wordnet.provider.WordNetContract.VerbFrameMaps_VerbFrames;
import org.sqlunet.wordnet.provider.WordNetContract.VerbFrameSentenceMaps_VerbFrameSentences;
import org.sqlunet.wordnet.provider.WordNetContract.Words;
import org.sqlunet.wordnet.provider.WordNetContract.Words_MorphMaps_Morphs;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains;
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
	static private final int WORDS = 10;
	static private final int WORD = 11;
	static private final int WORD_BY_LEMMA = 12;
	static private final int SENSES = 20;
	static private final int SENSE = 21;
	static private final int SYNSETS = 30;
	static private final int SYNSET = 31;
	static private final int SEMLINKS = 40;
	static private final int LEXLINKS = 50;
	static private final int LINKTYPES = 60;
	static private final int POSTYPES = 70;
	static private final int LEXDOMAINS = 80;
	static private final int ADJPOSITIONTYPES = 90;
	static private final int SAMPLES = 100;

	// view codes
	static private final int DICT = 200;

	// join codes
	static private final int WORDS_SENSES_SYNSETS = 310;
	static private final int WORDS_SENSES_CASEDWORDS_SYNSETS = 311;
	static private final int WORDS_SENSES_CASEDWORDS_SYNSETS_POSTYPES_LEXDOMAINS = 312;
	static private final int SENSES_WORDS = 320;
	static private final int SENSES_WORDS_BY_SYNSET = 321;
	static private final int SENSES_SYNSETS_POSTYPES_LEXDOMAINS = 330;
	static private final int SYNSETS_POSTYPES_LEXDOMAINS = 340;

	static private final int LINKS_SENSES_WORDS_X_BY_SYNSET = 400;
	static private final int SEMLINKS_SYNSETS = 410;
	static private final int SEMLINKS_SYNSETS_X = 411;
	static private final int SEMLINKS_SYNSETS_WORDS_X_BY_SYNSET = 412;
	static private final int LEXLINKS_SENSES = 420;
	static private final int LEXLINKS_SENSES_X = 421;
	static private final int LEXLINKS_SENSES_WORDS_X_BY_SYNSET = 422;

	static private final int VFRAMEMAPS_VFRAMES = 510;
	static private final int VFRAMESENTENCEMAPS_VFRAMESENTENCES = 515;
	static private final int ADJPOSITIONS_ADJPOSITIONTYPES = 520;
	static private final int MORPHMAPS_MORPHS = 530;
	static private final int WORDS_MORPHMAPS_MORPHS = 541;
	static private final int WORDS_MORPHMAPS_MORPHS_BY_WORD = 542;

	// search text codes
	static private final int LOOKUP_FTS_WORDS = 810;
	static private final int LOOKUP_FTS_DEFINITIONS = 820;
	static private final int LOOKUP_FTS_SAMPLES = 830;

	// suggest codes
	static private final int SUGGEST_WORDS = 900;
	static private final int SUGGEST_FTS_WORDS = 910;
	static private final int SUGGEST_FTS_DEFINITIONS = 920;
	static private final int SUGGEST_FTS_SAMPLES = 930;

	static private void matchURIs()
	{
		// table
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Words.TABLE, WordNetProvider.WORDS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Words.TABLE + "/#", WordNetProvider.WORD);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Words.TABLE + "/*", WordNetProvider.WORD_BY_LEMMA);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Senses.TABLE, WordNetProvider.SENSES);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Senses.TABLE + "/#", WordNetProvider.SENSE);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Synsets.TABLE, WordNetProvider.SYNSETS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Synsets.TABLE + "/#", WordNetProvider.SYNSET);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, SemLinks.TABLE, WordNetProvider.SEMLINKS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, LexLinks.TABLE, WordNetProvider.LEXLINKS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, LinkTypes.TABLE, WordNetProvider.LINKTYPES);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, PosTypes.TABLE, WordNetProvider.POSTYPES);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, LexDomains.TABLE, WordNetProvider.LEXDOMAINS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, AdjPositionTypes.TABLE, WordNetProvider.ADJPOSITIONTYPES);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Samples.TABLE, WordNetProvider.SAMPLES);

		// view
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Dict.TABLE, WordNetProvider.DICT);

		// joins
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Words_Senses_Synsets.TABLE, WordNetProvider.WORDS_SENSES_SYNSETS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Words_Senses_CasedWords_Synsets.TABLE, WordNetProvider.WORDS_SENSES_CASEDWORDS_SYNSETS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.TABLE, WordNetProvider.WORDS_SENSES_CASEDWORDS_SYNSETS_POSTYPES_LEXDOMAINS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Senses_Words.TABLE, WordNetProvider.SENSES_WORDS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Senses_Words.TABLE_BY_SYNSET, WordNetProvider.SENSES_WORDS_BY_SYNSET);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Senses_Synsets_PosTypes_LexDomains.TABLE, WordNetProvider.SENSES_SYNSETS_POSTYPES_LEXDOMAINS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Synsets_PosTypes_LexDomains.TABLE, WordNetProvider.SYNSETS_POSTYPES_LEXDOMAINS);

		WordNetProvider.uriMatcher.addURI(AUTHORITY, Links_Senses_Words_X.TABLE_BY_SYNSET, WordNetProvider.LINKS_SENSES_WORDS_X_BY_SYNSET);

		WordNetProvider.uriMatcher.addURI(AUTHORITY, SemLinks_Synsets.TABLE, WordNetProvider.SEMLINKS_SYNSETS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, SemLinks_Synsets_X.TABLE, WordNetProvider.SEMLINKS_SYNSETS_X);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, SemLinks_Synsets_Words_X.TABLE_BY_SYNSET, WordNetProvider.SEMLINKS_SYNSETS_WORDS_X_BY_SYNSET);

		WordNetProvider.uriMatcher.addURI(AUTHORITY, LexLinks_Senses.TABLE, WordNetProvider.LEXLINKS_SENSES);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, LexLinks_Senses_X.TABLE, WordNetProvider.LEXLINKS_SENSES_X);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, LexLinks_Senses_Words_X.TABLE_BY_SYNSET, WordNetProvider.LEXLINKS_SENSES_WORDS_X_BY_SYNSET);

		WordNetProvider.uriMatcher.addURI(AUTHORITY, VerbFrameMaps_VerbFrames.TABLE, WordNetProvider.VFRAMEMAPS_VFRAMES);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, VerbFrameSentenceMaps_VerbFrameSentences.TABLE, WordNetProvider.VFRAMESENTENCEMAPS_VFRAMESENTENCES);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, AdjPositions_AdjPositionTypes.TABLE, WordNetProvider.ADJPOSITIONS_ADJPOSITIONTYPES);

		WordNetProvider.uriMatcher.addURI(AUTHORITY, MorphMaps_Morphs.TABLE, WordNetProvider.MORPHMAPS_MORPHS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Words_MorphMaps_Morphs.TABLE, WordNetProvider.WORDS_MORPHMAPS_MORPHS);
		WordNetProvider.uriMatcher.addURI(AUTHORITY, Words_MorphMaps_Morphs.TABLE_BY_WORD, WordNetProvider.WORDS_MORPHMAPS_MORPHS_BY_WORD);

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

	@SuppressWarnings("DuplicateBranchesInSwitch")
	@Override
	public String getType(@NonNull final Uri uri)
	{
		switch (WordNetProvider.uriMatcher.match(uri))
		{
			// T A B L E S

			case WORDS:
			case WORD:
			case WORD_BY_LEMMA:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words.TABLE;
			case SENSES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses.TABLE;
			case SENSE:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses.TABLE;
			case SYNSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Synsets.TABLE;
			case SYNSET:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Synsets.TABLE;
			case SEMLINKS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + SemLinks.TABLE;
			case LEXLINKS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexLinks.TABLE;
			case LINKTYPES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LinkTypes.TABLE;
			case POSTYPES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PosTypes.TABLE;
			case ADJPOSITIONTYPES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + AdjPositionTypes.TABLE;
			case LEXDOMAINS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexDomains.TABLE;
			case SAMPLES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Samples.TABLE;

			// V I E W S

			case DICT:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Dict.TABLE;
			case WORDS_SENSES_SYNSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_Senses_Synsets.TABLE;
			case WORDS_SENSES_CASEDWORDS_SYNSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_Senses_CasedWords_Synsets.TABLE;
			case WORDS_SENSES_CASEDWORDS_SYNSETS_POSTYPES_LEXDOMAINS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.TABLE;

			// J O I N S

			case SENSES_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses_Words.TABLE;
			case SENSES_WORDS_BY_SYNSET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses_Words.TABLE_BY_SYNSET;
			case SENSES_SYNSETS_POSTYPES_LEXDOMAINS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Senses_Synsets_PosTypes_LexDomains.TABLE;
			case SYNSETS_POSTYPES_LEXDOMAINS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Synsets_PosTypes_LexDomains.TABLE;
			case LINKS_SENSES_WORDS_X_BY_SYNSET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Links_Senses_Words_X.TABLE_BY_SYNSET;
			case SEMLINKS_SYNSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + SemLinks_Synsets.TABLE;
			case SEMLINKS_SYNSETS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + SemLinks_Synsets_X.TABLE;
			case SEMLINKS_SYNSETS_WORDS_X_BY_SYNSET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + SemLinks_Synsets_Words_X.TABLE_BY_SYNSET;
			case LEXLINKS_SENSES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexLinks_Senses.TABLE;
			case LEXLINKS_SENSES_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexLinks_Senses_X.TABLE;
			case LEXLINKS_SENSES_WORDS_X_BY_SYNSET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexLinks_Senses_Words_X.TABLE_BY_SYNSET;
			case VFRAMEMAPS_VFRAMES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + VerbFrameMaps_VerbFrames.TABLE;
			case VFRAMESENTENCEMAPS_VFRAMESENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + VerbFrameSentenceMaps_VerbFrameSentences.TABLE;
			case ADJPOSITIONS_ADJPOSITIONTYPES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + AdjPositions_AdjPositionTypes.TABLE;
			case MORPHMAPS_MORPHS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + MorphMaps_Morphs.TABLE;
			case WORDS_MORPHMAPS_MORPHS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_MorphMaps_Morphs.TABLE;
			case WORDS_MORPHMAPS_MORPHS_BY_WORD:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_MorphMaps_Morphs.TABLE_BY_WORD;

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
	@SuppressWarnings({"boxing", "DuplicateBranchesInSwitch"})
	@Nullable
	@Override
	public Cursor query(@NonNull final Uri uri, final String[] projection, @Nullable final String selection, final String[] selectionArgs, final String sortOrder)
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

		String[] actualProjection = projection;
		String actualSelection = selection;
		final int code = WordNetProvider.uriMatcher.match(uri);
		Log.d(WordNetProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code));
		String groupBy = null;
		String table;
		switch (code)
		{
			// T A B L E
			// table uri : last element is table

			case WORDS:
			case SENSES:
			case SYNSETS:
			case SEMLINKS:
			case LEXLINKS:
			case LINKTYPES:
			case POSTYPES:
			case LEXDOMAINS:
			case ADJPOSITIONTYPES:
			case SAMPLES:
				table = uri.getLastPathSegment();
				break;

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case WORD:
				table = Words.TABLE;
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
				break;

			// J O I N S

			case WORDS_SENSES_SYNSETS:
				table = "words " + //
						"LEFT JOIN senses USING (wordid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS:
				table = "words " + //
						"LEFT JOIN senses USING (wordid) " + //
						"LEFT JOIN casedwords USING (wordid,casedwordid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS_POSTYPES_LEXDOMAINS:
				table = "words " + //
						"LEFT JOIN senses AS " + WordNetContract.SENSE + " USING (wordid) " + //
						"LEFT JOIN casedwords USING (wordid,casedwordid) " + //
						"LEFT JOIN synsets AS " + WordNetContract.SYNSET + " USING (synsetid) " + //
						"LEFT JOIN postypes AS " + WordNetContract.POS + " USING (pos) " + //
						"LEFT JOIN lexdomains USING (lexdomainid)";
				break;

			case SENSES_WORDS:
				table = "senses " + //
						"LEFT JOIN words USING(wordid)";
				break;

			case SENSES_WORDS_BY_SYNSET:
				groupBy = "synsetid";
				table = "senses " + //
						"LEFT JOIN words USING(wordid)";
				actualProjection = BaseProvider.appendProjection(actualProjection, "GROUP_CONCAT(words.lemma, ', ' ) AS " + Senses_Words.MEMBERS);
				break;

			case SENSES_SYNSETS_POSTYPES_LEXDOMAINS:
				table = "senses " + //
						"INNER JOIN synsets USING (synsetid) " + //
						"LEFT JOIN postypes USING(pos) " + //
						"LEFT JOIN lexdomains USING(lexdomainid)";
				break;

			case SYNSETS_POSTYPES_LEXDOMAINS:
				table = "synsets " + //
						"LEFT JOIN postypes USING(pos) " + //
						"LEFT JOIN lexdomains USING(lexdomainid)";
				break;

			case LINKS_SENSES_WORDS_X_BY_SYNSET:
			{
				final String table1 = "semlinks";
				final String table2 = "lexlinks";
				final String[] projection1 = { //
						SemLinks.LINKID, //
						SemLinks.SYNSETID1, //
						SemLinks.SYNSETID2, //
				};
				final String[] projection2 = { //
						LexLinks.LINKID, //
						LexLinks.WORDID1, //
						LexLinks.SYNSETID1, //
						LexLinks.WORDID2, //
						LexLinks.SYNSETID2, //
				};
				final String[] unionProjection = { //
						Links.LINKID, //
						Links.WORDID1, //
						Links.SYNSETID1, //
						Links.WORDID2, //
						Links.SYNSETID2, //
				};
				groupBy = BaseModule.TARGET_SYNSETID + " , " + WordNetContract.TYPE + " , link, linkid, " + BaseModule.TARGET_WORDID + ',' + BaseModule.TARGET_LEMMA;
				assert selection != null;
				final String[] selections = selection.split("/\\*\\*/\\|/\\*\\*/");
				table = "( " + makeQuery(table1, //
						table2, //
						projection1, //
						projection2, //
						unionProjection, //
						WordNetContract.TYPE, //
						"sem", //
						"lex", //
						selections[0], //
						selections[1]) + " ) AS " + WordNetContract.LINK + ' ' + //
						"INNER JOIN linktypes USING (linkid) " + //
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid " + //
						"LEFT JOIN senses ON " + WordNetContract.DEST + ".synsetid = senses.synsetid " + //
						"LEFT JOIN words AS " + WordNetContract.WORD + " USING (wordid) " + //
						"LEFT JOIN words AS " + WordNetContract.WORD2 + " ON " + WordNetContract.LINK + ".word2id = " + WordNetContract.WORD2 + ".wordid";
				actualSelection = null;
			}
			break;

			case SEMLINKS_SYNSETS:
				table = "semlinks AS " + WordNetContract.LINK + ' ' + //
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid";
				break;

			case SEMLINKS_SYNSETS_X:
				table = "semlinks AS " + WordNetContract.LINK + ' ' + //
						"INNER JOIN linktypes USING (linkid)" + //
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid ";
				break;

			case SEMLINKS_SYNSETS_WORDS_X_BY_SYNSET:
				groupBy = WordNetContract.DEST + ".synsetid";
				table = "semlinks AS " + WordNetContract.LINK + ' ' + //
						"INNER JOIN linktypes USING (linkid) " + //
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid " + //
						"LEFT JOIN senses ON " + WordNetContract.DEST + ".synsetid = senses.synsetid " + //
						"LEFT JOIN words USING (wordid)";
				actualProjection = BaseProvider.appendProjection(actualProjection, "GROUP_CONCAT(words.lemma, ', ' ) AS " + SemLinks_Synsets_Words_X.MEMBERS2);
				break;

			case LEXLINKS_SENSES:
				table = "lexlinks AS " + WordNetContract.LINK + ' ' + //
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.LINK + ".word2id = " + WordNetContract.WORD + ".wordid";
				break;

			case LEXLINKS_SENSES_X:
				table = "lexlinks AS " + WordNetContract.LINK + ' ' + //
						"INNER JOIN linktypes USING (linkid)" + //
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.LINK + ".word2id = " + WordNetContract.WORD + ".wordid ";
				break;

			case LEXLINKS_SENSES_WORDS_X_BY_SYNSET:
				groupBy = WordNetContract.DEST + ".synsetid";
				actualProjection = BaseProvider.appendProjection(actualProjection, "GROUP_CONCAT(DISTINCT " + WordNetContract.WORD2 + ".lemma) AS " + LexLinks_Senses_Words_X.MEMBERS2);
				table = "lexlinks AS " + WordNetContract.LINK + ' ' + //
						"INNER JOIN linktypes USING (linkid) " + //
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.LINK + ".word2id = " + WordNetContract.WORD + ".wordid " + //
						"LEFT JOIN senses AS " + WordNetContract.SENSE + " ON " + WordNetContract.DEST + ".synsetid = " + WordNetContract.SENSE + ".synsetid " + //
						"LEFT JOIN words AS " + WordNetContract.WORD2 + " USING (wordid)";
				break;

			case VFRAMEMAPS_VFRAMES:
				table = "vframemaps " + //
						"LEFT JOIN vframes USING (frameid)";
				break;

			case VFRAMESENTENCEMAPS_VFRAMESENTENCES:
				table = "vframesentencemaps " + //
						"LEFT JOIN vframesentences USING (sentenceid)";
				break;

			case ADJPOSITIONS_ADJPOSITIONTYPES:
				table = "adjpositions " + //
						"LEFT JOIN adjpositiontypes USING (position)";
				break;

			case MORPHMAPS_MORPHS:
				table = "morphmaps " + //
						"LEFT JOIN morphs USING (morphid)";
				break;

			case WORDS_MORPHMAPS_MORPHS_BY_WORD:
				groupBy = "wordid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case WORDS_MORPHMAPS_MORPHS:
				table = "words " + //
						"LEFT JOIN morphmaps USING (wordid) " + //
						"LEFT JOIN morphs USING (morphid)";
				break;

			// T E X T S E A R C H

			case LOOKUP_FTS_WORDS:
				table = "words_lemma_fts4";
				break;

			case LOOKUP_FTS_DEFINITIONS:
				table = "synsets_definition_fts4";
				break;

			case LOOKUP_FTS_SAMPLES:
				table = "samples_sample_fts4";
				break;

			// S U G G E S T

			case SUGGEST_WORDS:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}
				table = "words";
				return this.db.query(table, new String[]{"wordid AS _id", //
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
				table = "words_lemma_fts4";
				return this.db.query(table, new String[]{"wordid AS _id", //
								"lemma AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
								"lemma AS " + SearchManager.SUGGEST_COLUMN_QUERY}, //
						"lemma MATCH ?", //
						new String[]{last + '*'}, null, null, null);
			}

			case SUGGEST_FTS_DEFINITIONS:
			{
				final String last = uri.getLastPathSegment();
				table = "synsets_definition_fts4";
				return this.db.query(table, new String[]{"synsetid AS _id", //
								"definition AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
								"definition AS " + SearchManager.SUGGEST_COLUMN_QUERY}, //
						"definition MATCH ?", //
						new String[]{last + '*'}, null, null, null);
			}

			case SUGGEST_FTS_SAMPLES:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}
				table = "samples_sample_fts4";
				return this.db.query(table, new String[]{"sampleid AS _id", //
								"sample AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
								"sample AS " + SearchManager.SUGGEST_COLUMN_QUERY}, //
						"sample MATCH ?", new String[]{last + '*'}, null, null, null);
			}

			case UriMatcher.NO_MATCH:
			default:
				throw new RuntimeException("Malformed URI " + uri);
		}

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
	private String makeQuery(//
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

	private String makeSelection(final String[] projection, final String selection)
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
