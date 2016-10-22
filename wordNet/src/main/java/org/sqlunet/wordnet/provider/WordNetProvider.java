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
	static private final String TAG = "WordNetProvider"; //$NON-NLS-1$

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
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Words.TABLE + "/#", WordNetProvider.WORD); //$NON-NLS-1$
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Words.TABLE + "/*", WordNetProvider.WORD_BY_LEMMA); //$NON-NLS-1$
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Senses.TABLE, WordNetProvider.SENSES);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Senses.TABLE + "/#", WordNetProvider.SENSE); //$NON-NLS-1$
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Synsets.TABLE, WordNetProvider.SYNSETS);
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Synsets.TABLE + "/#", WordNetProvider.SYNSET); //$NON-NLS-1$
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
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Search_Words.TABLE + "/*", WordNetProvider.LOOKUP_WORDS); //$NON-NLS-1$
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Search_Words.TABLE + "/", WordNetProvider.LOOKUP_WORDS); //$NON-NLS-1$
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Search_Definitions.TABLE + "/*", WordNetProvider.LOOKUP_DEFINITIONS); //$NON-NLS-1$
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Search_Definitions.TABLE + "/", WordNetProvider.LOOKUP_DEFINITIONS); //$NON-NLS-1$
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Search_Samples.TABLE + "/*", WordNetProvider.LOOKUP_SAMPLES); //$NON-NLS-1$
		WordNetProvider.uriMatcher.addURI(WordNetContract.AUTHORITY, Search_Samples.TABLE + "/", WordNetProvider.LOOKUP_SAMPLES); //$NON-NLS-1$
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
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Words.TABLE; //$NON-NLS-1$
			case WORD:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Words.TABLE; //$NON-NLS-1$
			case WORD_BY_LEMMA:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Words.TABLE; //$NON-NLS-1$
			case SENSES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Senses.TABLE; //$NON-NLS-1$
			case SENSE:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Senses.TABLE; //$NON-NLS-1$
			case SYNSETS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Synsets.TABLE; //$NON-NLS-1$
			case SYNSET:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Synsets.TABLE; //$NON-NLS-1$
			case SEMLINKS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + SemLinks.TABLE; //$NON-NLS-1$
			case LEXLINKS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + LexLinks.TABLE; //$NON-NLS-1$
			case LINKTYPES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + LinkTypes.TABLE; //$NON-NLS-1$
			case POSTYPES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + PosTypes.TABLE; //$NON-NLS-1$
			case ADJPOSITIONTYPES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + AdjPositionTypes.TABLE; //$NON-NLS-1$
			case LEXDOMAINS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + LexDomains.TABLE; //$NON-NLS-1$
			case SAMPLES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Samples.TABLE; //$NON-NLS-1$

			// V I E W S

			case DICT:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Dict.TABLE; //$NON-NLS-1$

			case WORDS_SENSES_SYNSETS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Words_Senses_Synsets.TABLE; //$NON-NLS-1$
			case WORDS_SENSES_CASEDWORDS_SYNSETS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Words_Senses_CasedWords_Synsets.TABLE; //$NON-NLS-1$
			case WORDS_SENSES_CASEDWORDS_SYNSETS_POSTYPES_LEXDOMAINS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.TABLE; //$NON-NLS-1$

			// J O I N S

			case SENSES_WORDS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Senses_Words.TABLE; //$NON-NLS-1$
			case SENSES_WORDS_BY_SYNSET:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Senses_Words.TABLE_BY_SYNSET; //$NON-NLS-1$
			case SENSES_SYNSETS_POSTYPES_LEXDOMAINS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Senses_Synsets_PosTypes_LexDomains.TABLE; //$NON-NLS-1$
			case SYNSETS_POSTYPES_LEXDOMAINS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Synsets_PosTypes_LexDomains.TABLE; //$NON-NLS-1$

			case SEMLINKS_SYNSETS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + SemLinks_Synsets.TABLE; //$NON-NLS-1$
			case SEMLINKS_SYNSETS_X:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + SemLinks_Synsets_X.TABLE; //$NON-NLS-1$
			case SEMLINKS_SYNSETS_WORDS_X_BY_SYNSET:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + SemLinks_Synsets_Words_X.TABLE_BY_SYNSET; //$NON-NLS-1$
			case LEXLINKS_SENSES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + LexLinks_Senses.TABLE; //$NON-NLS-1$
			case LEXLINKS_SENSES_X:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + LexLinks_Senses_X.TABLE; //$NON-NLS-1$
			case LEXLINKS_SENSES_WORDS_X_BY_SYNSET:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + LexLinks_Senses_Words_X.TABLE_BY_SYNSET; //$NON-NLS-1$

			case VFRAMEMAPS_VFRAMES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + VerbFrameMaps_VerbFrames.TABLE; //$NON-NLS-1$
			case VFRAMESENTENCEMAPS_VFRAMESENTENCES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + VerbFrameSentenceMaps_VerbFrameSentences.TABLE; //$NON-NLS-1$
			case ADJPOSITIONS_ADJPOSITIONTYPES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + AdjPositions_AdjPositionTypes.TABLE; //$NON-NLS-1$

			case MORPHMAPS_MORPHS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + MorphMaps_Morphs.TABLE; //$NON-NLS-1$

			// TEXT LOOKUPS

			case LOOKUP_FTS_WORDS:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Words.TABLE; //$NON-NLS-1$
			case LOOKUP_FTS_DEFINITIONS:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Synsets.TABLE; //$NON-NLS-1$
			case LOOKUP_FTS_SAMPLES:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Samples.TABLE; //$NON-NLS-1$

			// S E A R C H

			case LOOKUP_WORDS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Words.TABLE; //$NON-NLS-1$
			case LOOKUP_DEFINITIONS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Synsets.TABLE; //$NON-NLS-1$
			case LOOKUP_SAMPLES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + WordNetContract.AUTHORITY + '.' + Samples.TABLE; //$NON-NLS-1$

			default:
				throw new UnsupportedOperationException("Illegal MIME type"); //$NON-NLS-1$
		}
	}

	// Q U E R Y

	/**
	 * Query
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
		Log.d(WordNetProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code)); //$NON-NLS-1$ //$NON-NLS-2$

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
					actualSelection += " AND "; //$NON-NLS-1$
				}
				else
				{
					actualSelection = ""; //$NON-NLS-1$
				}
				actualSelection += Words.WORDID + " = " + uri.getLastPathSegment(); //$NON-NLS-1$
				break;

			case SENSE:
				table = Senses.TABLE;
				if (actualSelection != null)
				{
					actualSelection += " AND "; //$NON-NLS-1$
				}
				else
				{
					actualSelection = ""; //$NON-NLS-1$
				}
				actualSelection += Senses.SENSEID + " = " + uri.getLastPathSegment(); //$NON-NLS-1$
				break;

			case SYNSET:
				table = uri.getLastPathSegment();
				if (actualSelection != null)
				{
					actualSelection += " AND "; //$NON-NLS-1$
				}
				else
				{
					actualSelection = ""; //$NON-NLS-1$
				}
				actualSelection += Synsets.SYNSETID + " = " + uri.getLastPathSegment(); //$NON-NLS-1$
				break;

			// V I E W S

			case DICT:
				table = uri.getLastPathSegment();
				break;

			// J O I N S

			case WORDS_SENSES_SYNSETS:
				table = "words " + //$NON-NLS-1$
						"LEFT JOIN senses USING (wordid) " + //$NON-NLS-1$
						"LEFT JOIN synsets USING (synsetid)"; //$NON-NLS-1$
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS:
				table = "words " + //$NON-NLS-1$
						"LEFT JOIN senses USING (wordid) " + //$NON-NLS-1$
						"LEFT JOIN casedwords USING (wordid,casedwordid) " + //$NON-NLS-1$
						"LEFT JOIN synsets USING (synsetid)"; //$NON-NLS-1$
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS_POSTYPES_LEXDOMAINS:
				table = "words " + //$NON-NLS-1$
						"LEFT JOIN senses USING (wordid) " + //$NON-NLS-1$
						"LEFT JOIN casedwords USING (wordid,casedwordid) " + //$NON-NLS-1$
						"LEFT JOIN synsets USING (synsetid) " + //$NON-NLS-1$
						"LEFT JOIN postypes USING (pos) " + //$NON-NLS-1$
						"LEFT JOIN lexdomains USING (lexdomainid)"; //$NON-NLS-1$
				break;

			case SENSES_WORDS:
				table = "senses " + //$NON-NLS-1$
						"LEFT JOIN words USING(wordid)"; //$NON-NLS-1$
				break;

			case SENSES_WORDS_BY_SYNSET:
				groupBy = "synsetid"; //$NON-NLS-1$
				table = "senses " + //$NON-NLS-1$
						"LEFT JOIN words USING(wordid)"; //$NON-NLS-1$
				actualProjection = SqlUNetProvider.appendProjection(actualProjection, "GROUP_CONCAT(words.lemma, ', ' ) AS members"); //$NON-NLS-1$
				break;

			case SENSES_SYNSETS_POSTYPES_LEXDOMAINS:
				table = "senses " + //$NON-NLS-1$
						"INNER JOIN synsets USING (synsetid) " + //$NON-NLS-1$
						"LEFT JOIN postypes USING(pos) " + //$NON-NLS-1$
						"LEFT JOIN lexdomains USING(lexdomainid)"; //$NON-NLS-1$
				break;

			case SYNSETS_POSTYPES_LEXDOMAINS:
				table = "synsets " + //$NON-NLS-1$
						"LEFT JOIN postypes USING(pos) " + //$NON-NLS-1$
						"LEFT JOIN lexdomains USING(lexdomainid)"; //$NON-NLS-1$
				break;

			case SEMLINKS_SYNSETS:
				table = "semlinks AS " + WordNetContract.LINK + ' ' + //$NON-NLS-1$
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid"; //$NON-NLS-1$
				break;

			case SEMLINKS_SYNSETS_X:
				table = "semlinks AS " + WordNetContract.LINK + ' ' + //$NON-NLS-1$
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid " + //$NON-NLS-1$
						"LEFT JOIN linktypes USING (linkid)"; //$NON-NLS-1$
				break;

			case SEMLINKS_SYNSETS_WORDS_X_BY_SYNSET:
				groupBy = WordNetContract.DEST + ".synsetid"; //$NON-NLS-1$
				table = "semlinks AS " + WordNetContract.LINK + ' ' + //$NON-NLS-1$
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid " + //$NON-NLS-1$
						"LEFT JOIN linktypes USING (linkid) " + //$NON-NLS-1$
						"LEFT JOIN senses ON " + WordNetContract.DEST + ".synsetid = senses.synsetid " + //$NON-NLS-1$
						"LEFT JOIN words USING (wordid)"; //$NON-NLS-1$
				actualProjection = SqlUNetProvider.appendProjection(actualProjection, "GROUP_CONCAT(words.lemma, ', ' ) AS members"); //$NON-NLS-1$
				break;

			case LEXLINKS_SENSES:
				table = "lexlinks AS " + WordNetContract.LINK + ' ' + //$NON-NLS-1$
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid " + //$NON-NLS-1$
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.LINK + ".word2id = " + WordNetContract.WORD + ".wordid"; //$NON-NLS-1$
				break;

			case LEXLINKS_SENSES_X:
				table = "lexlinks AS " + WordNetContract.LINK + ' ' + //$NON-NLS-1$
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid " + //$NON-NLS-1$
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.LINK + ".word2id = " + WordNetContract.WORD + ".wordid " + //$NON-NLS-1$
						"LEFT JOIN linktypes USING (linkid)"; //$NON-NLS-1$
				break;

			case LEXLINKS_SENSES_WORDS_X_BY_SYNSET:
				groupBy = WordNetContract.DEST + ".synsetid"; //$NON-NLS-1$
				actualProjection = SqlUNetProvider.appendProjection(actualProjection, "GROUP_CONCAT(DISTINCT " + WordNetContract.WORD2 + ".lemma) AS members"); //$NON-NLS-1$
				table = "lexlinks AS " + WordNetContract.LINK +  ' ' + //$NON-NLS-1$
						"INNER JOIN synsets AS " + WordNetContract.DEST + " ON " + WordNetContract.LINK + ".synset2id = " + WordNetContract.DEST + ".synsetid " + //$NON-NLS-1$
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.LINK + ".word2id = " + WordNetContract.WORD + ".wordid " + //$NON-NLS-1$
						"LEFT JOIN linktypes USING (linkid) " + //$NON-NLS-1$
						"LEFT JOIN senses AS " + WordNetContract.SENSE + " ON " + WordNetContract.DEST + ".synsetid = " + WordNetContract.SENSE + ".synsetid " + //$NON-NLS-1$
						"LEFT JOIN words AS " + WordNetContract.WORD2 + " USING (wordid)"; //$NON-NLS-1$
				break;

			case VFRAMEMAPS_VFRAMES:
				table = "vframemaps " + //$NON-NLS-1$
						"LEFT JOIN vframes USING (frameid)"; //$NON-NLS-1$
				break;

			case VFRAMESENTENCEMAPS_VFRAMESENTENCES:
				table = "vframesentencemaps " + //$NON-NLS-1$
						"LEFT JOIN vframesentences USING (sentenceid)"; //$NON-NLS-1$
				break;

			case ADJPOSITIONS_ADJPOSITIONTYPES:
				table = "adjpositions " + //$NON-NLS-1$
						"LEFT JOIN adjpositiontypes USING (position)"; //$NON-NLS-1$
				break;

			case MORPHMAPS_MORPHS:
				table = "morphmaps " + //$NON-NLS-1$
						"LEFT JOIN morphs USING (morphid)"; //$NON-NLS-1$
				break;

			// T E X T S E A R C H

			case LOOKUP_FTS_WORDS:
				table = "words_lemma_fts4"; //$NON-NLS-1$
				break;

			case LOOKUP_FTS_DEFINITIONS:
				table = "synsets_definition_fts4"; //$NON-NLS-1$
				break;

			case LOOKUP_FTS_SAMPLES:
				table = "samples_sample_fts4"; //$NON-NLS-1$
				break;

			// S U G G E S T

			case LOOKUP_WORDS:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}
				table = "words_lemma_fts4"; //$NON-NLS-1$
				return this.db.query(table, new String[]{"wordid AS _id", //$NON-NLS-1$
								"lemma AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //$NON-NLS-1$
								"lemma AS " + SearchManager.SUGGEST_COLUMN_QUERY}, //$NON-NLS-1$
						"lemma MATCH ?", //$NON-NLS-1$
						new String[]{last}, null, null, null);
			}

			case LOOKUP_DEFINITIONS:
			{
				final String last = uri.getLastPathSegment();
				table = "synsets_definition_fts4"; //$NON-NLS-1$
				return this.db.query(table, new String[]{"synsetid AS _id", //$NON-NLS-1$
								"definition AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //$NON-NLS-1$
								"definition AS " + SearchManager.SUGGEST_COLUMN_QUERY}, //$NON-NLS-1$
						"definition MATCH ?", //$NON-NLS-1$
						new String[]{last}, null, null, null);
			}

			case LOOKUP_SAMPLES:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}
				table = "samples_sample_fts4"; //$NON-NLS-1$
				return this.db.query(table, new String[]{"sampleid AS _id", //$NON-NLS-1$
								"sample AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //$NON-NLS-1$
								"sample AS " + SearchManager.SUGGEST_COLUMN_QUERY}, //$NON-NLS-1$
						"sample MATCH ?", new String[]{last}, null, null, null); //$NON-NLS-1$
			}

			case UriMatcher.NO_MATCH:
			default:
				throw new RuntimeException("Malformed URI " + uri); //$NON-NLS-1$
		}

		if (SqlUNetProvider.debugSql)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, actualProjection, actualSelection, groupBy, null, sortOrder, null);
			Log.d(WordNetProvider.TAG + "SQL", sql); //$NON-NLS-1$
			Log.d(WordNetProvider.TAG + "ARG", SqlUNetProvider.argsToString(selectionArgs)); //$NON-NLS-1$
		}

		// do query
		try
		{
			return this.db.query(table, actualProjection, actualSelection, selectionArgs, groupBy, null, sortOrder);
		}
		catch (final SQLiteException e)
		{
			Log.e(WordNetProvider.TAG, "WordNet provider query failed", e); //$NON-NLS-1$
			return null;
		}
	}
}
