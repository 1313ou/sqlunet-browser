package org.sqlunet.wordnet.provider;

import android.app.SearchManager;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.provider.SqlUNetProvider;
import org.sqlunet.wordnet.provider.WordNetContract.AdjPositionTypes;
import org.sqlunet.wordnet.provider.WordNetContract.AdjPositions_AdjPositionTypes;
import org.sqlunet.wordnet.provider.WordNetContract.Dict;
import org.sqlunet.wordnet.provider.WordNetContract.LexDomains;
import org.sqlunet.wordnet.provider.WordNetContract.LexLinks;
import org.sqlunet.wordnet.provider.WordNetContract.LexLinks_Senses;
import org.sqlunet.wordnet.provider.WordNetContract.LexLinks_Senses_Words_X;
import org.sqlunet.wordnet.provider.WordNetContract.LexLinks_Senses_X;
import org.sqlunet.wordnet.provider.WordNetContract.LinkTypes;
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Definitions;
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Samples;
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Words;
import org.sqlunet.wordnet.provider.WordNetContract.MorphMaps_Morphs;
import org.sqlunet.wordnet.provider.WordNetContract.PosTypes;
import org.sqlunet.wordnet.provider.WordNetContract.Samples;
import org.sqlunet.wordnet.provider.WordNetContract.Search_Definitions;
import org.sqlunet.wordnet.provider.WordNetContract.Search_Samples;
import org.sqlunet.wordnet.provider.WordNetContract.Search_Words;
import org.sqlunet.wordnet.provider.WordNetContract.SemLinks;
import org.sqlunet.wordnet.provider.WordNetContract.SemLinks_Synsets;
import org.sqlunet.wordnet.provider.WordNetContract.SemLinks_Synsets_Words_X;
import org.sqlunet.wordnet.provider.WordNetContract.SemLinks_Synsets_X;
import org.sqlunet.wordnet.provider.WordNetContract.Senses;
import org.sqlunet.wordnet.provider.WordNetContract.Senses_Synsets_PosTypes_LexDomains;
import org.sqlunet.wordnet.provider.WordNetContract.Senses_Words;
import org.sqlunet.wordnet.provider.WordNetContract.Synsets;
import org.sqlunet.wordnet.provider.WordNetContract.Synsets_PosTypes_LexDomains;
import org.sqlunet.wordnet.provider.WordNetContract.VerbFrameMaps_VerbFrames;
import org.sqlunet.wordnet.provider.WordNetContract.VerbFrameSentenceMaps_VerbFrameSentences;
import org.sqlunet.wordnet.provider.WordNetContract.Words;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_Synsets;

/**
 * WordNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WordNetProvider extends SqlUNetProvider
{
	static private final String TAG = "WordNetProvider"; //

	// U R I M A T C H E R

	// uri matcher
	private static final UriMatcher uriMatcher;

	// table codes
	private static final int WORDS = 10;
	private static final int WORD = 11;
	private static final int WORD_BY_LEMMA = 12;
	private static final int SENSES = 20;
	private static final int SENSE = 21;
	private static final int SYNSETS = 30;
	private static final int SYNSET = 31;
	private static final int SEMLINKS = 40;
	private static final int LEXLINKS = 50;
	private static final int LINKTYPES = 60;
	private static final int POSTYPES = 70;
	private static final int LEXDOMAINS = 80;
	private static final int ADJPOSITIONTYPES = 90;
	private static final int SAMPLES = 100;

	// view codes
	private static final int DICT = 300;

	// join codes
	private static final int WORDS_SENSES_SYNSETS = 310;
	private static final int WORDS_SENSES_CASEDWORDS_SYNSETS = 311;
	private static final int WORDS_SENSES_CASEDWORDS_SYNSETS_POSTYPES_LEXDOMAINS = 312;
	private static final int SENSES_WORDS = 320;
	private static final int SENSES_WORDS_BY_SYNSET = 321;
	private static final int SENSES_SYNSETS_POSTYPES_LEXDOMAINS = 330;
	private static final int SYNSETS_POSTYPES_LEXDOMAINS = 340;
	private static final int SEMLINKS_SYNSETS = 350;
	private static final int SEMLINKS_SYNSETS_X = 351;
	private static final int SEMLINKS_SYNSETS_WORDS_X_BY_SYNSET = 352;
	private static final int LEXLINKS_SENSES = 360;
	private static final int LEXLINKS_SENSES_X = 361;
	private static final int LEXLINKS_SENSES_WORDS_X_BY_SYNSET = 362;
	private static final int VFRAMEMAPS_VFRAMES = 370;
	private static final int VFRAMESENTENCEMAPS_VFRAMESENTENCES = 371;
	private static final int ADJPOSITIONS_ADJPOSITIONTYPES = 380;
	private static final int MORPHMAPS_MORPHS = 390;

	// text search codes
	private static final int LOOKUP_WORDS = 510;
	private static final int LOOKUP_FTS_WORDS = 511;
	private static final int LOOKUP_DEFINITIONS = 520;
	private static final int LOOKUP_FTS_DEFINITIONS = 521;
	private static final int LOOKUP_SAMPLES = 530;
	private static final int LOOKUP_FTS_SAMPLES = 531;

	static
	{
		// table
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Words.TABLE, WordNetProvider.WORDS);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Words.TABLE + "/#", WordNetProvider.WORD); //
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Words.TABLE + "/*", WordNetProvider.WORD_BY_LEMMA); //
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Senses.TABLE, WordNetProvider.SENSES);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Senses.TABLE + "/#", WordNetProvider.SENSE); //
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Synsets.TABLE, WordNetProvider.SYNSETS);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Synsets.TABLE + "/#", WordNetProvider.SYNSET); //
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, SemLinks.TABLE, WordNetProvider.SEMLINKS);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, LexLinks.TABLE, WordNetProvider.LEXLINKS);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, LinkTypes.TABLE, WordNetProvider.LINKTYPES);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, PosTypes.TABLE, WordNetProvider.POSTYPES);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, LexDomains.TABLE, WordNetProvider.LEXDOMAINS);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, AdjPositionTypes.TABLE, WordNetProvider.ADJPOSITIONTYPES);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Samples.TABLE, WordNetProvider.SAMPLES);

		// view
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Dict.TABLE, WordNetProvider.DICT);

		// joins
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Words_Senses_Synsets.TABLE, WordNetProvider.WORDS_SENSES_SYNSETS);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Words_Senses_CasedWords_Synsets.TABLE, WordNetProvider.WORDS_SENSES_CASEDWORDS_SYNSETS);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.TABLE, WordNetProvider.WORDS_SENSES_CASEDWORDS_SYNSETS_POSTYPES_LEXDOMAINS);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Senses_Words.TABLE, WordNetProvider.SENSES_WORDS);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Senses_Words.TABLE_BY_SYNSET, WordNetProvider.SENSES_WORDS_BY_SYNSET);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Senses_Synsets_PosTypes_LexDomains.TABLE, WordNetProvider.SENSES_SYNSETS_POSTYPES_LEXDOMAINS);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Synsets_PosTypes_LexDomains.TABLE, WordNetProvider.SYNSETS_POSTYPES_LEXDOMAINS);

		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, SemLinks_Synsets.TABLE, WordNetProvider.SEMLINKS_SYNSETS);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, SemLinks_Synsets_X.TABLE, WordNetProvider.SEMLINKS_SYNSETS_X);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, SemLinks_Synsets_Words_X.TABLE_BY_SYNSET, WordNetProvider.SEMLINKS_SYNSETS_WORDS_X_BY_SYNSET);

		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, LexLinks_Senses.TABLE, WordNetProvider.LEXLINKS_SENSES);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, LexLinks_Senses_X.TABLE, WordNetProvider.LEXLINKS_SENSES_X);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, LexLinks_Senses_Words_X.TABLE_BY_SYNSET, WordNetProvider.LEXLINKS_SENSES_WORDS_X_BY_SYNSET);

		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, VerbFrameMaps_VerbFrames.TABLE, WordNetProvider.VFRAMEMAPS_VFRAMES);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, VerbFrameSentenceMaps_VerbFrameSentences.TABLE, WordNetProvider.VFRAMESENTENCEMAPS_VFRAMESENTENCES);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, AdjPositions_AdjPositionTypes.TABLE, WordNetProvider.ADJPOSITIONS_ADJPOSITIONTYPES);

		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, MorphMaps_Morphs.TABLE, WordNetProvider.MORPHMAPS_MORPHS);

		// text search
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Lookup_Words.TABLE, WordNetProvider.LOOKUP_FTS_WORDS);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Lookup_Definitions.TABLE, WordNetProvider.LOOKUP_FTS_DEFINITIONS);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Lookup_Samples.TABLE, WordNetProvider.LOOKUP_FTS_SAMPLES);

		// search
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Search_Words.TABLE + "/*", WordNetProvider.LOOKUP_WORDS); //
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Search_Words.TABLE + "/", WordNetProvider.LOOKUP_WORDS); //
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Search_Definitions.TABLE + "/*", WordNetProvider.LOOKUP_DEFINITIONS); //
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Search_Definitions.TABLE + "/", WordNetProvider.LOOKUP_DEFINITIONS); //
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Search_Samples.TABLE + "/*", WordNetProvider.LOOKUP_SAMPLES); //
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Search_Samples.TABLE + "/", WordNetProvider.LOOKUP_SAMPLES); //
	}

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public WordNetProvider()
	{
	}

	// M I M E

	@Override
	public String getType(final Uri uri)
	{
		switch (WordNetProvider.uriMatcher.match(uri))
		{
			// T A B L E S

			case WORDS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Words.TABLE; //
			case WORD:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Words.TABLE; //
			case WORD_BY_LEMMA:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Words.TABLE; //
			case SENSES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Senses.TABLE; //
			case SENSE:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Senses.TABLE; //
			case SYNSETS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Synsets.TABLE; //
			case SYNSET:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Synsets.TABLE; //
			case SEMLINKS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + SemLinks.TABLE; //
			case LEXLINKS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + LexLinks.TABLE; //
			case LINKTYPES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + LinkTypes.TABLE; //
			case POSTYPES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + PosTypes.TABLE; //
			case ADJPOSITIONTYPES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + AdjPositionTypes.TABLE; //
			case LEXDOMAINS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + LexDomains.TABLE; //
			case SAMPLES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Samples.TABLE; //

			// V I E W S

			case DICT:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Dict.TABLE; //

			case WORDS_SENSES_SYNSETS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Words_Senses_Synsets.TABLE; //
			case WORDS_SENSES_CASEDWORDS_SYNSETS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Words_Senses_CasedWords_Synsets.TABLE; //
			case WORDS_SENSES_CASEDWORDS_SYNSETS_POSTYPES_LEXDOMAINS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.TABLE; //

			// J O I N S

			case SENSES_WORDS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Senses_Words.TABLE; //
			case SENSES_WORDS_BY_SYNSET:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Senses_Words.TABLE_BY_SYNSET; //
			case SENSES_SYNSETS_POSTYPES_LEXDOMAINS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Senses_Synsets_PosTypes_LexDomains.TABLE; //
			case SYNSETS_POSTYPES_LEXDOMAINS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Synsets_PosTypes_LexDomains.TABLE; //

			case SEMLINKS_SYNSETS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + SemLinks_Synsets.TABLE; //
			case SEMLINKS_SYNSETS_X:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + SemLinks_Synsets_X.TABLE; //
			case SEMLINKS_SYNSETS_WORDS_X_BY_SYNSET:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + SemLinks_Synsets_Words_X.TABLE_BY_SYNSET; //
			case LEXLINKS_SENSES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + LexLinks_Senses.TABLE; //
			case LEXLINKS_SENSES_X:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + LexLinks_Senses_X.TABLE; //
			case LEXLINKS_SENSES_WORDS_X_BY_SYNSET:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + LexLinks_Senses_Words_X.TABLE_BY_SYNSET; //

			case VFRAMEMAPS_VFRAMES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + VerbFrameMaps_VerbFrames.TABLE; //
			case VFRAMESENTENCEMAPS_VFRAMESENTENCES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + VerbFrameSentenceMaps_VerbFrameSentences.TABLE; //
			case ADJPOSITIONS_ADJPOSITIONTYPES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + AdjPositions_AdjPositionTypes.TABLE; //

			case MORPHMAPS_MORPHS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + MorphMaps_Morphs.TABLE; //

			// TEXT LOOKUPS

			case LOOKUP_FTS_WORDS:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Words.TABLE; //
			case LOOKUP_FTS_DEFINITIONS:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Synsets.TABLE; //
			case LOOKUP_FTS_SAMPLES:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Samples.TABLE; //

			// S E A R C H

			case LOOKUP_WORDS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Words.TABLE; //
			case LOOKUP_DEFINITIONS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Synsets.TABLE; //
			case LOOKUP_SAMPLES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Samples.TABLE; //

			default:
				throw new UnsupportedOperationException("Illegal MIME type"); //
		}
	}

	// Q U E R Y

	/**
	 * QueryData
	 *
	 * @param uri uri
	 * @param projection projection
	 * @param selection selection
	 * @param selectionArgs selection arguments
	 * @param sortOrder sort order
	 * @return cursor
	 */
	@SuppressWarnings("boxing")
	@Override
	public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder)
	{
		if (this.db == null)
		{
			open();
		}

		String[] actualProjection = projection;
		String actualSelection = selection;
		final int code = WordNetProvider.uriMatcher.match(uri);
		Log.d(WordNetProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code)); //

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
					actualSelection += " AND "; //
				}
				else
				{
					actualSelection = ""; //
				}
				actualSelection += Words.WORDID + " = " + uri.getLastPathSegment(); //
				break;

			case SENSE:
				table = Senses.TABLE;
				if (actualSelection != null)
				{
					actualSelection += " AND "; //
				}
				else
				{
					actualSelection = ""; //
				}
				actualSelection += Senses.SENSEID + " = " + uri.getLastPathSegment(); //
				break;

			case SYNSET:
				table = uri.getLastPathSegment();
				if (actualSelection != null)
				{
					actualSelection += " AND "; //
				}
				else
				{
					actualSelection = ""; //
				}
				actualSelection += Synsets.SYNSETID + " = " + uri.getLastPathSegment(); //
				break;

			// V I E W S

			case DICT:
				table = uri.getLastPathSegment();
				break;

			// J O I N S

			case WORDS_SENSES_SYNSETS:
				table = "words " + //
						"LEFT JOIN senses USING (wordid) " + //
						"LEFT JOIN synsets USING (synsetid)"; //
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS:
				table = "words " + //
						"LEFT JOIN senses USING (wordid) " + //
						"LEFT JOIN casedwords USING (wordid,casedwordid) " + //
						"LEFT JOIN synsets USING (synsetid)"; //
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS_POSTYPES_LEXDOMAINS:
				table = "words " + //
						"LEFT JOIN senses USING (wordid) " + //
						"LEFT JOIN casedwords USING (wordid,casedwordid) " + //
						"LEFT JOIN synsets USING (synsetid) " + //
						"LEFT JOIN postypes AS " + WordNetContract.POS + " USING (pos) " + //
						"LEFT JOIN lexdomains USING (lexdomainid)"; //
				break;

			case SENSES_WORDS:
				table = "senses " + //
						"LEFT JOIN words USING(wordid)"; //
				break;

			case SENSES_WORDS_BY_SYNSET:
				groupBy = "synsetid"; //
				table = "senses " + //
						"LEFT JOIN words USING(wordid)"; //
				actualProjection = SqlUNetProvider.appendProjection(actualProjection, "GROUP_CONCAT(words.lemma, ', ' ) AS " + Senses_Words.MEMBERS); //
				break;

			case SENSES_SYNSETS_POSTYPES_LEXDOMAINS:
				table = "senses " + //
						"INNER JOIN synsets USING (synsetid) " + //
						"LEFT JOIN postypes USING(pos) " + //
						"LEFT JOIN lexdomains USING(lexdomainid)"; //
				break;

			case SYNSETS_POSTYPES_LEXDOMAINS:
				table = "synsets " + //
						"LEFT JOIN postypes USING(pos) " + //
						"LEFT JOIN lexdomains USING(lexdomainid)"; //
				break;

			case SEMLINKS_SYNSETS:
				table = "semlinks AS " + WordNetContract.LINK + ' ' + //
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid"; //
				break;

			case SEMLINKS_SYNSETS_X:
				table = "semlinks AS " + WordNetContract.LINK + ' ' + //
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid " + //
						"LEFT JOIN linktypes USING (linkid)"; //
				break;

			case SEMLINKS_SYNSETS_WORDS_X_BY_SYNSET:
				groupBy = WordNetContract.DEST + ".synsetid"; //
				table = "semlinks AS " + WordNetContract.LINK + ' ' + //
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid " + //
						"LEFT JOIN linktypes USING (linkid) " + //
						"LEFT JOIN senses ON " + WordNetContract.DEST + ".synsetid = senses.synsetid " + //
						"LEFT JOIN words USING (wordid)"; //
				actualProjection = SqlUNetProvider.appendProjection(actualProjection, "GROUP_CONCAT(words.lemma, ', ' ) AS " + SemLinks_Synsets_Words_X.MEMBERS2); //
				break;

			case LEXLINKS_SENSES:
				table = "lexlinks AS " + WordNetContract.LINK + ' ' + //
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.LINK + ".word2id = " + WordNetContract.WORD + ".wordid"; //
				break;

			case LEXLINKS_SENSES_X:
				table = "lexlinks AS " + WordNetContract.LINK + ' ' + //
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.LINK + ".word2id = " + WordNetContract.WORD + ".wordid " + //
						"LEFT JOIN linktypes USING (linkid)"; //
				break;

			case LEXLINKS_SENSES_WORDS_X_BY_SYNSET:
				groupBy = WordNetContract.DEST + ".synsetid"; //
				actualProjection = SqlUNetProvider.appendProjection(actualProjection, "GROUP_CONCAT(DISTINCT " + WordNetContract.WORD2 + ".lemma) AS " + LexLinks_Senses_Words_X.MEMBERS2); //
				table = "lexlinks AS " + WordNetContract.LINK +  ' ' + //
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.LINK + ".word2id = " + WordNetContract.WORD + ".wordid " + //
						"LEFT JOIN linktypes USING (linkid) " + //
						"LEFT JOIN senses AS " + WordNetContract.SENSE + " ON " + WordNetContract.DEST + ".synsetid = " + WordNetContract.SENSE + ".synsetid " + //
						"LEFT JOIN words AS " + WordNetContract.WORD2 + " USING (wordid)"; //
				break;

			case VFRAMEMAPS_VFRAMES:
				table = "vframemaps " + //
						"LEFT JOIN vframes USING (frameid)"; //
				break;

			case VFRAMESENTENCEMAPS_VFRAMESENTENCES:
				table = "vframesentencemaps " + //
						"LEFT JOIN vframesentences USING (sentenceid)"; //
				break;

			case ADJPOSITIONS_ADJPOSITIONTYPES:
				table = "adjpositions " + //
						"LEFT JOIN adjpositiontypes USING (position)"; //
				break;

			case MORPHMAPS_MORPHS:
				table = "morphmaps " + //
						"LEFT JOIN morphs USING (morphid)"; //
				break;

			// T E X T S E A R C H

			case LOOKUP_FTS_WORDS:
				table = "words_lemma_fts4"; //
				break;

			case LOOKUP_FTS_DEFINITIONS:
				table = "synsets_definition_fts4"; //
				break;

			case LOOKUP_FTS_SAMPLES:
				table = "samples_sample_fts4"; //
				break;

			// S U G G E S T

			case LOOKUP_WORDS:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}
				table = "words_lemma_fts4"; //
				return this.db.query(table, new String[]{"wordid AS _id", //
								"lemma AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
								"lemma AS " + SearchManager.SUGGEST_COLUMN_QUERY}, //
						"lemma MATCH ?", //
						new String[]{last}, null, null, null);
			}

			case LOOKUP_DEFINITIONS:
			{
				final String last = uri.getLastPathSegment();
				table = "synsets_definition_fts4"; //
				return this.db.query(table, new String[]{"synsetid AS _id", //
								"definition AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
								"definition AS " + SearchManager.SUGGEST_COLUMN_QUERY}, //
						"definition MATCH ?", //
						new String[]{last}, null, null, null);
			}

			case LOOKUP_SAMPLES:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}
				table = "samples_sample_fts4"; //
				return this.db.query(table, new String[]{"sampleid AS _id", //
								"sample AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
								"sample AS " + SearchManager.SUGGEST_COLUMN_QUERY}, //
						"sample MATCH ?", new String[]{last}, null, null, null); //
			}

			case UriMatcher.NO_MATCH:
			default:
				throw new RuntimeException("Malformed URI " + uri); //
		}

		if (SqlUNetProvider.debugSql)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, actualProjection, actualSelection, groupBy, null, sortOrder, null);
			Log.d(WordNetProvider.TAG + "SQL", sql); //
			Log.d(WordNetProvider.TAG + "ARG", SqlUNetProvider.argsToString(selectionArgs)); //
		}

		// do query
		try
		{
			return this.db.query(table, actualProjection, actualSelection, selectionArgs, groupBy, null, sortOrder, null);
		}
		catch (final SQLiteException e)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, actualProjection, actualSelection, groupBy, null, sortOrder, null);
			Log.d(TAG + "SQL", sql); //
			Log.e(TAG, "WordNet provider query failed", e); //
			return null;
		}
	}
}
