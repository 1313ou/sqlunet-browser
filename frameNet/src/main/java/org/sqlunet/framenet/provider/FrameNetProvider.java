package org.sqlunet.framenet.provider;

import org.sqlunet.framenet.provider.FrameNetContract.AnnoSets;
import org.sqlunet.framenet.provider.FrameNetContract.AnnoSets_Layers_X;
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
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_Sentences_Annosets_Layers_Labels;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_X;
import org.sqlunet.framenet.provider.FrameNetContract.Lookup_FnSentences;
import org.sqlunet.framenet.provider.FrameNetContract.Patterns_Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.Patterns_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.Sentences_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.ValenceUnits_Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.ValenceUnits_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.Words_LexUnits_Frames;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.provider.SqlUNetProvider;

import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * WordNet provider
 *
 * @author Bernard Bou
 */
public class FrameNetProvider extends SqlUNetProvider
{
	static private final String TAG = "FrameNetProvider"; //$NON-NLS-1$

	// U R I M A T C H E R

	// table codes
	private static final UriMatcher uriMatcher;
	private static final int LEXUNIT = 10;
	private static final int LEXUNITS = 11;
	private static final int LEXUNITS_X_BY_LEXUNIT = 12;
	private static final int FRAME = 20;
	private static final int FRAMES = 21;
	private static final int FRAMES_X_BY_FRAME = 23;
	private static final int FRAMES_RELATED = 25;
	private static final int SENTENCE = 30;
	private static final int SENTENCES = 31;
	private static final int ANNOSET = 40;
	private static final int ANNOSETS = 41;
	private static final int SENTENCES_LAYERS_X = 50;
	private static final int ANNOSETS_LAYERS_X = 51;
	private static final int PATTERNS_LAYERS_X = 52;
	private static final int VALENCEUNITS_LAYERS_X = 53;
	private static final int PATTERNS_SENTENCES = 61;
	private static final int VALENCEUNITS_SENTENCES = 62;
	private static final int GOVERNORS_ANNOSETS = 70;

	// join codes
	private static final int WORDS_LEXUNITS_FRAMES = 100;
	private static final int FRAMES_FES = 200;
	private static final int FRAMES_FES_BY_FE = 201;
	private static final int LEXUNITS_SENTENCES = 300;
	private static final int LEXUNITS_SENTENCES_BY_SENTENCE = 301;
	private static final int LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS = 310;
	private static final int LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE = 311;
	private static final int LEXUNITS_GOVERNORS = 410;
	private static final int LEXUNITS_REALIZATIONS = 420;
	private static final int LEXUNITS_REALIZATIONS_BY_REALIZATION = 421;
	private static final int LEXUNITS_GROUPREALIZATIONS = 430;
	private static final int LEXUNITS_GROUPREALIZATIONS_BY_PATTERN = 431;

	// text search codes
	private static final int LOOKUP_FTS_SENTENCES = 501;

	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits.TABLE, FrameNetProvider.LEXUNIT);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits.TABLE, FrameNetProvider.LEXUNITS);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits_X.TABLE_BY_LEXUNIT, FrameNetProvider.LEXUNITS_X_BY_LEXUNIT);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, Frames.TABLE, FrameNetProvider.FRAME);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, Frames.TABLE, FrameNetProvider.FRAMES);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, Frames_X.TABLE_BY_FRAME, FrameNetProvider.FRAMES_X_BY_FRAME);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, Frames_Related.TABLE, FrameNetProvider.FRAMES_RELATED);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, Sentences.TABLE, FrameNetProvider.SENTENCE);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, Sentences.TABLE, FrameNetProvider.SENTENCES);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, AnnoSets.TABLE, FrameNetProvider.ANNOSET);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, AnnoSets.TABLE, FrameNetProvider.ANNOSETS);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, Sentences_Layers_X.TABLE, FrameNetProvider.SENTENCES_LAYERS_X);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, AnnoSets_Layers_X.TABLE, FrameNetProvider.ANNOSETS_LAYERS_X);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, Patterns_Layers_X.TABLE, FrameNetProvider.PATTERNS_LAYERS_X);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, ValenceUnits_Layers_X.TABLE, FrameNetProvider.VALENCEUNITS_LAYERS_X);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, Words_LexUnits_Frames.TABLE, FrameNetProvider.WORDS_LEXUNITS_FRAMES);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, Frames_FEs.TABLE, FrameNetProvider.FRAMES_FES);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, Frames_FEs.TABLE_BY_FE, FrameNetProvider.FRAMES_FES_BY_FE);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits_Sentences.TABLE, FrameNetProvider.LEXUNITS_SENTENCES);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits_Sentences.TABLE_BY_SENTENCE, FrameNetProvider.LEXUNITS_SENTENCES_BY_SENTENCE);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits_Sentences_Annosets_Layers_Labels.TABLE, FrameNetProvider.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits_Sentences_Annosets_Layers_Labels.TABLE_BY_SENTENCE, FrameNetProvider.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits_Governors.TABLE, FrameNetProvider.LEXUNITS_GOVERNORS);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits_FERealizations_ValenceUnits.TABLE, FrameNetProvider.LEXUNITS_REALIZATIONS);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits_FERealizations_ValenceUnits.TABLE_BY_REALIZATION, FrameNetProvider.LEXUNITS_REALIZATIONS_BY_REALIZATION);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE, FrameNetProvider.LEXUNITS_GROUPREALIZATIONS);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE_BY_PATTERN, FrameNetProvider.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, Patterns_Sentences.TABLE, FrameNetProvider.PATTERNS_SENTENCES);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, ValenceUnits_Sentences.TABLE, FrameNetProvider.VALENCEUNITS_SENTENCES);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, Governors_AnnoSets_Sentences.TABLE, FrameNetProvider.GOVERNORS_ANNOSETS);

		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, Lookup_FnSentences.TABLE + "/", FrameNetProvider.LOOKUP_FTS_SENTENCES); //$NON-NLS-1$
	}

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public FrameNetProvider()
	{
	}

	// M I M E

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(final Uri uri)
	{
		switch (FrameNetProvider.uriMatcher.match(uri))
		{

		// TABLES

		case LEXUNIT:
			return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits.TABLE; //$NON-NLS-1$
		case LEXUNITS:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits.TABLE; //$NON-NLS-1$
		case LEXUNITS_X_BY_LEXUNIT:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits_X.TABLE_BY_LEXUNIT; //$NON-NLS-1$
		case FRAME:
			return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Frames.TABLE; //$NON-NLS-1$
		case FRAMES:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Frames.TABLE; //$NON-NLS-1$
		case FRAMES_X_BY_FRAME:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Frames_X.TABLE_BY_FRAME; //$NON-NLS-1$
		case FRAMES_RELATED:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Frames_Related.TABLE; //$NON-NLS-1$
		case SENTENCE:
			return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Sentences.TABLE; //$NON-NLS-1$
		case SENTENCES:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Sentences.TABLE; //$NON-NLS-1$
		case ANNOSET:
			return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + AnnoSets.TABLE; //$NON-NLS-1$
		case ANNOSETS:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + AnnoSets.TABLE; //$NON-NLS-1$
		case SENTENCES_LAYERS_X:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Sentences_Layers_X.TABLE; //$NON-NLS-1$
		case ANNOSETS_LAYERS_X:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + AnnoSets_Layers_X.TABLE; //$NON-NLS-1$
		case PATTERNS_LAYERS_X:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Patterns_Layers_X.TABLE; //$NON-NLS-1$
		case VALENCEUNITS_LAYERS_X:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + ValenceUnits_Layers_X.TABLE; //$NON-NLS-1$
		case WORDS_LEXUNITS_FRAMES:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Words_LexUnits_Frames.TABLE; //$NON-NLS-1$
		case FRAMES_FES:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Frames_FEs.TABLE; //$NON-NLS-1$
		case LEXUNITS_SENTENCES:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits_Sentences.TABLE; //$NON-NLS-1$
		case LEXUNITS_GOVERNORS:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits_Governors.TABLE; //$NON-NLS-1$
		case LEXUNITS_REALIZATIONS:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits_FERealizations_ValenceUnits.TABLE; //$NON-NLS-1$
		case LEXUNITS_REALIZATIONS_BY_REALIZATION:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits_FERealizations_ValenceUnits.TABLE_BY_REALIZATION; //$NON-NLS-1$
		case LEXUNITS_GROUPREALIZATIONS:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE; //$NON-NLS-1$
		case LEXUNITS_GROUPREALIZATIONS_BY_PATTERN:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE_BY_PATTERN; //$NON-NLS-1$
		case PATTERNS_SENTENCES:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Patterns_Sentences.TABLE; //$NON-NLS-1$
		case VALENCEUNITS_SENTENCES:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + ValenceUnits_Sentences.TABLE; //$NON-NLS-1$
		case GOVERNORS_ANNOSETS:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Governors_AnnoSets_Sentences.TABLE; //$NON-NLS-1$

			// S E A R C H
		case LOOKUP_FTS_SENTENCES:
			return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Lookup_FnSentences.TABLE; //$NON-NLS-1$

		default:
			throw new UnsupportedOperationException("Illegal MIME type"); //$NON-NLS-1$
		}
	}

	// Q U E R Y

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@SuppressWarnings("boxing")
	@Override
	public Cursor query(final Uri uri, final String[] projection, final String selection0, final String[] selectionArgs, String sortOrder)
	{
		if (this.db == null)
			open();

		// choose the table to query and a sort order based on the code returned for the incoming URI
		String table;
		String selection = selection0;
		String groupBy = null;
		final int code = FrameNetProvider.uriMatcher.match(uri);
		Log.d(FrameNetProvider.TAG + "URI", String.format("%s (code %s)", uri, code)); //$NON-NLS-1$ //$NON-NLS-2$

		switch (code)
		{

		// T A B L E
		// table uri : last element is table

		case LEXUNITS:
		case FRAMES:
		case ANNOSETS:
		case SENTENCES:
			table = uri.getLastPathSegment();
			break;

		// I T E M
		// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
		// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

		case LEXUNIT:
			table = LexUnits.TABLE;
			if (selection != null)
			{
				selection += " AND "; //$NON-NLS-1$
			}
			else
			{
				selection = ""; //$NON-NLS-1$
			}
			selection += LexUnits.LUID + " = " + uri.getLastPathSegment(); //$NON-NLS-1$
			break;

		case FRAME:
			table = Frames.TABLE;
			if (selection != null)
			{
				selection += " AND "; //$NON-NLS-1$
			}
			else
			{
				selection = ""; //$NON-NLS-1$
			}
			selection += Frames.FRAMEID + " = " + uri.getLastPathSegment(); //$NON-NLS-1$
			break;

		case SENTENCE:
			table = uri.getLastPathSegment();
			if (selection != null)
			{
				selection += " AND "; //$NON-NLS-1$
			}
			else
			{
				selection = ""; //$NON-NLS-1$
			}
			selection += Sentences.SENTENCEID + " = " + uri.getLastPathSegment(); //$NON-NLS-1$
			break;

		case ANNOSET:
			table = uri.getLastPathSegment();
			if (selection != null)
			{
				selection += " AND "; //$NON-NLS-1$
			}
			else
			{
				selection = ""; //$NON-NLS-1$
			}
			selection += AnnoSets.ANNOSETID + " = " + uri.getLastPathSegment(); //$NON-NLS-1$
			break;

		// J O I N S

		case FRAMES_X_BY_FRAME:
			groupBy = "frameid"; //$NON-NLS-1$
			table = "fnframes " + // //$NON-NLS-1$
					"LEFT JOIN fnframes_semtypes USING (frameid) " + // //$NON-NLS-1$
					"LEFT JOIN fnsemtypes USING (semtypeid)"; //$NON-NLS-1$
			break;

		case FRAMES_RELATED:
			table = "fnframes_related LEFT JOIN fnframes AS s USING (frameid) LEFT JOIN fnframes AS d ON (frame2id = d.frameid) LEFT JOIN fnframerelations USING (relationid)"; //$NON-NLS-1$
			break;

		case LEXUNITS_X_BY_LEXUNIT:
			groupBy = "luid"; //$NON-NLS-1$
			table = "fnlexunits AS lu " + // //$NON-NLS-1$
					"LEFT JOIN fnframes AS f USING (frameid) " + // //$NON-NLS-1$
					"LEFT JOIN fnposes AS p ON (lu.posid = p.posid) " + // //$NON-NLS-1$
					"LEFT JOIN fnfetypes AS it ON (incorporatedfetypeid = it.fetypeid) " + //$NON-NLS-1$
					"LEFT JOIN fnfes AS ie ON (incorporatedfeid = ie.feid)"; //$NON-NLS-1$
			break;

		case SENTENCES_LAYERS_X:
			table = "(SELECT sentenceid,layerid,layertype,rank,GROUP_CONCAT(start||':'||end||':'||labeltype||':'||CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END||':'||CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END||':'||CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END,'|') AS annotations " //$NON-NLS-1$
					+ //
					"FROM fnsentences " + // //$NON-NLS-1$
					"LEFT JOIN fnannosets USING (sentenceid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlayers USING (annosetid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlayertypes USING (layertypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabels USING (layerid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabeltypes USING (labeltypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabelitypes USING (labelitypeid) " + // //$NON-NLS-1$
					"WHERE sentenceid = ? AND labeltypeid IS NOT NULL " + // //$NON-NLS-1$
					"GROUP BY layerid " + // //$NON-NLS-1$
					"ORDER BY rank,layerid,start,end)"; //$NON-NLS-1$
			break;

		case ANNOSETS_LAYERS_X:
			table = "(SELECT sentenceid,text,layerid,layertype,rank,GROUP_CONCAT(start||':'||end||':'||labeltype||':'||CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END||':'||CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END||':'||CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END,'|') AS annotations " //$NON-NLS-1$
					+ //
					"FROM fnannosets " + // //$NON-NLS-1$
					"LEFT JOIN fnsentences USING (sentenceid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlayers USING (annosetid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlayertypes USING (layertypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabels USING (layerid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabeltypes USING (labeltypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabelitypes USING (labelitypeid) " + // //$NON-NLS-1$
					"WHERE annosetid = ? AND labeltypeid IS NOT NULL " + // //$NON-NLS-1$
					"GROUP BY layerid " + // //$NON-NLS-1$
					"ORDER BY rank,layerid,start,end)"; //$NON-NLS-1$
			break;

		case PATTERNS_LAYERS_X:
			table = "(SELECT sentenceid,text,layerid,layertype,rank,GROUP_CONCAT(start||':'||end||':'||labeltype||':'||CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END||':'||CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END||':'||CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END,'|') AS annotations " //$NON-NLS-1$
					+ //
					"FROM fnpatterns_annosets " + // //$NON-NLS-1$
					"LEFT JOIN fnannosets USING (annosetid) " + // //$NON-NLS-1$
					"LEFT JOIN fnsentences USING (sentenceid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlayers USING (annosetid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlayertypes USING (layertypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabels USING (layerid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabeltypes USING (labeltypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabelitypes USING (labelitypeid) " + // //$NON-NLS-1$
					"WHERE patternid = ? AND labeltypeid IS NOT NULL " + // //$NON-NLS-1$
					"GROUP BY layerid " + // //$NON-NLS-1$
					"ORDER BY rank,layerid,start,end)"; //$NON-NLS-1$
			break;

		case VALENCEUNITS_LAYERS_X:
			table = "(SELECT sentenceid,text,layerid,layertype,rank,GROUP_CONCAT(start||':'||end||':'||labeltype||':'||CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END||':'||CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END||':'||CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END,'|') AS annotations " //$NON-NLS-1$
					+ //
					"FROM fnvalenceunits_annosets " + // //$NON-NLS-1$
					"LEFT JOIN fnannosets USING (annosetid) " + // //$NON-NLS-1$
					"LEFT JOIN fnsentences USING (sentenceid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlayers USING (annosetid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlayertypes USING (layertypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabels USING (layerid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabeltypes USING (labeltypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabelitypes USING (labelitypeid) " + // //$NON-NLS-1$
					"WHERE vuid = ? AND labeltypeid IS NOT NULL " + // //$NON-NLS-1$
					"GROUP BY layerid " + // //$NON-NLS-1$
					"ORDER BY rank,layerid,start,end)"; //$NON-NLS-1$
			break;

		case WORDS_LEXUNITS_FRAMES:
			table = "words " + // //$NON-NLS-1$
					"INNER JOIN fnwords USING (wordid) " + // //$NON-NLS-1$
					"INNER JOIN fnlexemes USING (fnwordid) " + // //$NON-NLS-1$
					"INNER JOIN fnlexunits AS lu USING (luid) " + // //$NON-NLS-1$
					"LEFT JOIN fnframes USING (frameid) " + // //$NON-NLS-1$
					"LEFT JOIN fnposes AS p ON (lu.posid = p.posid) " + // //$NON-NLS-1$
					"LEFT JOIN fnfes AS ie ON (incorporatedfeid = feid) " + // //$NON-NLS-1$
					"LEFT JOIN fnfetypes AS it ON (incorporatedfetypeid = it.fetypeid)"; //$NON-NLS-1$
			break;

		case FRAMES_FES_BY_FE:
			groupBy = "feid"; //$NON-NLS-1$
			//$FALL-THROUGH$
		case FRAMES_FES:
			table = "fnframes " + // //$NON-NLS-1$
					"INNER JOIN fnfes USING (frameid) " + // //$NON-NLS-1$
					"LEFT JOIN fnfetypes USING (fetypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fncoretypes USING (coretypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fnfes_semtypes USING (feid) " + // //$NON-NLS-1$
					"LEFT JOIN fnsemtypes USING (semtypeid)"; //$NON-NLS-1$
			break;

		case LEXUNITS_SENTENCES_BY_SENTENCE:
			groupBy = "s.sentenceid"; //$NON-NLS-1$
			//$FALL-THROUGH$
		case LEXUNITS_SENTENCES:
			table = "fnlexunits AS u " + // //$NON-NLS-1$
					"LEFT JOIN fnsubcorpuses USING (luid) " + // //$NON-NLS-1$
					"LEFT JOIN fnsubcorpuses_sentences USING (subcorpusid) " + // //$NON-NLS-1$
					"INNER JOIN fnsentences AS s USING (sentenceid)"; //$NON-NLS-1$
			break;

		case LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE:
			groupBy = "s.sentenceid"; //$NON-NLS-1$
			//$FALL-THROUGH$
		case LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS:
			table = "fnlexunits AS u " + // //$NON-NLS-1$
					"LEFT JOIN fnsubcorpuses USING (luid) " + // //$NON-NLS-1$
					"LEFT JOIN fnsubcorpuses_sentences USING (subcorpusid) " + // //$NON-NLS-1$
					"INNER JOIN fnsentences AS s USING (sentenceid) " + // //$NON-NLS-1$
					"LEFT JOIN fnannosets USING (sentenceid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlayers USING (annosetid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlayertypes USING (layertypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabels USING (layerid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabeltypes USING (labeltypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabelitypes USING (labelitypeid)"; //$NON-NLS-1$
			break;

		case LEXUNITS_GOVERNORS:
			table = "fnlexunits " + // //$NON-NLS-1$
					"INNER JOIN fnlexunits_governors AS s USING (luid) " + // //$NON-NLS-1$
					"INNER JOIN fngovernors USING (governorid) " + // //$NON-NLS-1$
					"LEFT JOIN fnwords USING (fnwordid)"; //$NON-NLS-1$
			break;

		case GOVERNORS_ANNOSETS:
			table = "fngovernors_annosets " + // //$NON-NLS-1$
					"LEFT JOIN fnannosets USING (annosetid) " + // //$NON-NLS-1$
					"LEFT JOIN fnsentences USING (sentenceid)"; //$NON-NLS-1$
			break;

		case LEXUNITS_REALIZATIONS_BY_REALIZATION:
			groupBy = "ferid"; //$NON-NLS-1$
			//$FALL-THROUGH$
		case LEXUNITS_REALIZATIONS:
			table = "fnlexunits " + // //$NON-NLS-1$
					"INNER JOIN fnferealizations AS r USING (luid) " + // //$NON-NLS-1$
					"LEFT JOIN fnvalenceunits USING (ferid) " + // //$NON-NLS-1$
					"LEFT JOIN fnfetypes USING (fetypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fngftypes USING (gfid) " + // //$NON-NLS-1$
					"LEFT JOIN fnpttypes USING (ptid)"; //$NON-NLS-1$
			break;

		case LEXUNITS_GROUPREALIZATIONS_BY_PATTERN:
			groupBy = "patternid"; //$NON-NLS-1$
			//$FALL-THROUGH$
		case LEXUNITS_GROUPREALIZATIONS:
			table = "fnlexunits " + // //$NON-NLS-1$
					"LEFT JOIN fnfegrouprealizations AS r USING (luid) " + // //$NON-NLS-1$
					"LEFT JOIN fnpatterns AS p USING (fegrid) " + // //$NON-NLS-1$
					"LEFT JOIN fnpatterns_valenceunits AS v USING (patternid) " + // //$NON-NLS-1$
					"LEFT JOIN fnvalenceunits USING (vuid) " + // //$NON-NLS-1$
					"LEFT JOIN fnfetypes USING (fetypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fngftypes USING (gfid) " + // //$NON-NLS-1$
					"LEFT JOIN fnpttypes USING (ptid)"; //$NON-NLS-1$
			break;

		case PATTERNS_SENTENCES:
			table = "fnpatterns_annosets " + // //$NON-NLS-1$
					"LEFT JOIN fnannosets AS r USING (annosetid) " + // //$NON-NLS-1$
					"LEFT JOIN fnsentences AS r USING (sentenceid)"; //$NON-NLS-1$
			break;

		case VALENCEUNITS_SENTENCES:
			table = "fnvalenceunits_annosets " + // //$NON-NLS-1$
					"LEFT JOIN fnannosets AS r USING (annosetid) " + // //$NON-NLS-1$
					"LEFT JOIN fnsentences AS r USING (sentenceid)"; //$NON-NLS-1$
			break;

		case LOOKUP_FTS_SENTENCES:
			table = "fnsentences_text_fts4"; //$NON-NLS-1$
			break;

		default:
		case UriMatcher.NO_MATCH:
			throw new RuntimeException("Malformed URI " + uri); //$NON-NLS-1$
		}

		if (SqlUNetProvider.debugSql)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, selection, groupBy, null, sortOrder, null);
			Log.d(FrameNetProvider.TAG + "SQL", sql); //$NON-NLS-1$
			Log.d(FrameNetProvider.TAG + "ARGS", SqlUNetProvider.argsToString(selectionArgs)); //$NON-NLS-1$
		}

		// do query
		try
		{
			return this.db.query(table, projection, selection, selectionArgs, groupBy, null, sortOrder);
		}
		catch (SQLiteException e)
		{
			Log.e(TAG, "FrameNet provider query failed", e); //$NON-NLS-1$
			return null;
		}
	}
}
