package org.sqlunet.framenet.provider;

import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

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
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_X;
import org.sqlunet.framenet.provider.FrameNetContract.Lookup_FnSentences;
import org.sqlunet.framenet.provider.FrameNetContract.Patterns_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.Patterns_Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.Sentences_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.ValenceUnits_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.ValenceUnits_Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.Words_LexUnits_Frames;
import org.sqlunet.provider.BaseProvider;

/**
 * FrameNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameNetProvider extends BaseProvider
{
	static private final String TAG = "FrameNetProvider";
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
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits_Sentences_AnnoSets_Layers_Labels.TABLE, FrameNetProvider.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits_Sentences_AnnoSets_Layers_Labels.TABLE_BY_SENTENCE, FrameNetProvider.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits_Governors.TABLE, FrameNetProvider.LEXUNITS_GOVERNORS);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits_FERealizations_ValenceUnits.TABLE, FrameNetProvider.LEXUNITS_REALIZATIONS);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits_FERealizations_ValenceUnits.TABLE_BY_REALIZATION, FrameNetProvider.LEXUNITS_REALIZATIONS_BY_REALIZATION);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE, FrameNetProvider.LEXUNITS_GROUPREALIZATIONS);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE_BY_PATTERN, FrameNetProvider.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, Patterns_Sentences.TABLE, FrameNetProvider.PATTERNS_SENTENCES);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, ValenceUnits_Sentences.TABLE, FrameNetProvider.VALENCEUNITS_SENTENCES);
		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, Governors_AnnoSets_Sentences.TABLE, FrameNetProvider.GOVERNORS_ANNOSETS);

		FrameNetProvider.uriMatcher.addURI(FrameNetContract.AUTHORITY, Lookup_FnSentences.TABLE + "/", FrameNetProvider.LOOKUP_FTS_SENTENCES);
	}

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public FrameNetProvider()
	{
	}

	// M I M E

	@Override
	public String getType(final Uri uri)
	{
		switch (FrameNetProvider.uriMatcher.match(uri))
		{
			// TABLES

			case LEXUNIT:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits.TABLE;
			case LEXUNITS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits.TABLE;
			case LEXUNITS_X_BY_LEXUNIT:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits_X.TABLE_BY_LEXUNIT;
			case FRAME:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Frames.TABLE;
			case FRAMES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Frames.TABLE;
			case FRAMES_X_BY_FRAME:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Frames_X.TABLE_BY_FRAME;
			case FRAMES_RELATED:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Frames_Related.TABLE;
			case SENTENCE:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Sentences.TABLE;
			case SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Sentences.TABLE;
			case ANNOSET:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + AnnoSets.TABLE;
			case ANNOSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + AnnoSets.TABLE;
			case SENTENCES_LAYERS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Sentences_Layers_X.TABLE;
			case ANNOSETS_LAYERS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + AnnoSets_Layers_X.TABLE;
			case PATTERNS_LAYERS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Patterns_Layers_X.TABLE;
			case VALENCEUNITS_LAYERS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + ValenceUnits_Layers_X.TABLE;
			case WORDS_LEXUNITS_FRAMES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Words_LexUnits_Frames.TABLE;
			case FRAMES_FES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Frames_FEs.TABLE;
			case LEXUNITS_SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits_Sentences.TABLE;
			case LEXUNITS_GOVERNORS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits_Governors.TABLE;
			case LEXUNITS_REALIZATIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits_FERealizations_ValenceUnits.TABLE;
			case LEXUNITS_REALIZATIONS_BY_REALIZATION:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits_FERealizations_ValenceUnits.TABLE_BY_REALIZATION;
			case LEXUNITS_GROUPREALIZATIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE;
			case LEXUNITS_GROUPREALIZATIONS_BY_PATTERN:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE_BY_PATTERN;
			case PATTERNS_SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Patterns_Sentences.TABLE;
			case VALENCEUNITS_SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + ValenceUnits_Sentences.TABLE;
			case GOVERNORS_ANNOSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Governors_AnnoSets_Sentences.TABLE;
			// S E A R C H
			case LOOKUP_FTS_SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + FrameNetContract.AUTHORITY + '.' + Lookup_FnSentences.TABLE;
			default:
				throw new UnsupportedOperationException("Illegal MIME type");
		}
	}

	// Q U E R Y

	@SuppressWarnings("boxing")
	@Override
	public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, String sortOrder)
	{
		if (this.db == null)
		{
			open();
		}

		// choose the table to query and a sort order based on the code returned for the incoming URI
		String actualSelection = selection;
		final int code = FrameNetProvider.uriMatcher.match(uri);
		Log.d(FrameNetProvider.TAG + "URI", String.format("%s (code %s)", uri, code));
		String groupBy = null;
		String table;
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
				if (actualSelection != null)
				{
					actualSelection += " AND ";
				}
				else
				{
					actualSelection = "";
				}
				actualSelection += LexUnits.LUID + " = " + uri.getLastPathSegment();
				break;

			case FRAME:
				table = Frames.TABLE;
				if (actualSelection != null)
				{
					actualSelection += " AND ";
				}
				else
				{
					actualSelection = "";
				}
				actualSelection += Frames.FRAMEID + " = " + uri.getLastPathSegment();
				break;

			case SENTENCE:
				table = uri.getLastPathSegment();
				if (actualSelection != null)
				{
					actualSelection += " AND ";
				}
				else
				{
					actualSelection = "";
				}
				actualSelection += Sentences.SENTENCEID + " = " + uri.getLastPathSegment();
				break;

			case ANNOSET:
				table = uri.getLastPathSegment();
				if (actualSelection != null)
				{
					actualSelection += " AND ";
				}
				else
				{
					actualSelection = "";
				}
				actualSelection += AnnoSets.ANNOSETID + " = " + uri.getLastPathSegment();
				break;

			// J O I N S

			case FRAMES_X_BY_FRAME:
				groupBy = "frameid";
				table = "fnframes " + //
						"LEFT JOIN fnframes_semtypes USING (frameid) " + //
						"LEFT JOIN fnsemtypes USING (semtypeid)";
				break;

			case FRAMES_RELATED:
				table = "fnframes_related AS " + FrameNetContract.RELATED + ' ' + //
						"LEFT JOIN fnframes AS " + FrameNetContract.SRC + " USING (frameid) " + //
						"LEFT JOIN fnframes AS " + FrameNetContract.DEST + " ON (frame2id = " + FrameNetContract.DEST + ".frameid) " + //  //  //
						"LEFT JOIN fnframerelations USING (relationid)";
				break;

			case LEXUNITS_X_BY_LEXUNIT:
				groupBy = "luid";
				table = "fnlexunits AS " + FrameNetContract.LU + ' ' + //
						"LEFT JOIN fnframes AS " + FrameNetContract.FRAME + " USING (frameid) " + //
						"LEFT JOIN fnposes AS " + FrameNetContract.POS + " ON (" + FrameNetContract.LU + ".posid = " + FrameNetContract.POS + ".posid) " + //
						"LEFT JOIN fnfetypes AS " + FrameNetContract.FETYPE + " ON (incorporatedfetypeid = " + FrameNetContract.FETYPE + ".fetypeid) " + //
						"LEFT JOIN fnfes AS " + FrameNetContract.FE + " ON (incorporatedfeid = " + FrameNetContract.FE + ".feid)";
				break;

			case SENTENCES_LAYERS_X:
				table = "(SELECT sentenceid,layerid,layertype,rank," + //
						"GROUP_CONCAT(start||':'||" + //
						"end||':'||" + //
						"labeltype||':'||" + //
						"CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END||':'||" + //
						"CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END||':'||" + //
						"CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END,'|') AS " + Sentences_Layers_X.LAYERANNOTATIONS + ' ' + //
						"FROM fnsentences " + //
						"LEFT JOIN fnannosets USING (sentenceid) " + //
						"LEFT JOIN fnlayers USING (annosetid) " + //
						"LEFT JOIN fnlayertypes USING (layertypeid) " + //
						"LEFT JOIN fnlabels USING (layerid) " + //
						"LEFT JOIN fnlabeltypes USING (labeltypeid) " + //
						"LEFT JOIN fnlabelitypes USING (labelitypeid) " + //
						"WHERE sentenceid = ? AND labeltypeid IS NOT NULL " + //
						"GROUP BY layerid " + //
						"ORDER BY rank,layerid,start,end)";
				break;

			case ANNOSETS_LAYERS_X:
				table = "(SELECT sentenceid,text,layerid,layertype,rank," + //
						"GROUP_CONCAT(start||':'||" + //
						"end||':'||" + //
						"labeltype||':'||" + //
						"CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END||':'||" + //
						"CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END||':'||" + //
						"CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END,'|') AS " + AnnoSets_Layers_X.LAYERANNOTATIONS + ' ' + //
						"FROM fnannosets " + //
						"LEFT JOIN fnsentences USING (sentenceid) " + //
						"LEFT JOIN fnlayers USING (annosetid) " + //
						"LEFT JOIN fnlayertypes USING (layertypeid) " + //
						"LEFT JOIN fnlabels USING (layerid) " + //
						"LEFT JOIN fnlabeltypes USING (labeltypeid) " + //
						"LEFT JOIN fnlabelitypes USING (labelitypeid) " + //
						"WHERE annosetid = ? AND labeltypeid IS NOT NULL " + //
						"GROUP BY layerid " + //
						"ORDER BY rank,layerid,start,end)";
				break;

			case PATTERNS_LAYERS_X:
				table = "(SELECT sentenceid,text,layerid,layertype,rank," + //
						"GROUP_CONCAT(start||':'||" + //
						"end||':'||" + //
						"labeltype||':'||" + //
						"CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END||':'||" + //
						"CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END||':'||" + //
						"CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END,'|') AS " + Patterns_Layers_X.LAYERANNOTATIONS + ' ' + //
						"FROM fnpatterns_annosets " + //
						"LEFT JOIN fnannosets USING (annosetid) " + //
						"LEFT JOIN fnsentences USING (sentenceid) " + //
						"LEFT JOIN fnlayers USING (annosetid) " + //
						"LEFT JOIN fnlayertypes USING (layertypeid) " + //
						"LEFT JOIN fnlabels USING (layerid) " + //
						"LEFT JOIN fnlabeltypes USING (labeltypeid) " + //
						"LEFT JOIN fnlabelitypes USING (labelitypeid) " + //
						"WHERE patternid = ? AND labeltypeid IS NOT NULL " + //
						"GROUP BY layerid " + //
						"ORDER BY rank,layerid,start,end)";
				break;

			case VALENCEUNITS_LAYERS_X:
				table = "(SELECT sentenceid,text,layerid,layertype,rank," + //
						"GROUP_CONCAT(start||':'||" + //
						"end||':'||" + //
						"labeltype||':'||" + //
						"CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END||':'||" + //
						"CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END||':'||" + //
						"CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END,'|') AS " + ValenceUnits_Layers_X.LAYERANNOTATIONS + ' ' + //
						"FROM fnvalenceunits_annosets " + //
						"LEFT JOIN fnannosets USING (annosetid) " + //
						"LEFT JOIN fnsentences USING (sentenceid) " + //
						"LEFT JOIN fnlayers USING (annosetid) " + //
						"LEFT JOIN fnlayertypes USING (layertypeid) " + //
						"LEFT JOIN fnlabels USING (layerid) " + //
						"LEFT JOIN fnlabeltypes USING (labeltypeid) " + //
						"LEFT JOIN fnlabelitypes USING (labelitypeid) " + //
						"WHERE vuid = ? AND labeltypeid IS NOT NULL " + //
						"GROUP BY layerid " + //
						"ORDER BY rank,layerid,start,end)";
				break;

			case WORDS_LEXUNITS_FRAMES:
				table = "words " + //
						"INNER JOIN fnwords USING (wordid) " + //
						"INNER JOIN fnlexemes USING (fnwordid) " + //
						"INNER JOIN fnlexunits AS " + FrameNetContract.LU + " USING (luid) " + //
						"LEFT JOIN fnframes USING (frameid) " + //
						"LEFT JOIN fnposes AS " + FrameNetContract.POS + " ON (" + FrameNetContract.LU + ".posid = " + FrameNetContract.POS + ".posid) " + //
						"LEFT JOIN fnfes AS " + FrameNetContract.FE + " ON (incorporatedfeid = feid) " + //
						"LEFT JOIN fnfetypes AS " + FrameNetContract.FETYPE + " ON (incorporatedfetypeid = " + FrameNetContract.FE + ".fetypeid)";
				break;

			case FRAMES_FES_BY_FE:
				groupBy = "feid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case FRAMES_FES:
				table = "fnframes " + //
						"INNER JOIN fnfes USING (frameid) " + //
						"LEFT JOIN fnfetypes USING (fetypeid) " + //
						"LEFT JOIN fncoretypes USING (coretypeid) " + //
						"LEFT JOIN fnfes_semtypes USING (feid) " + //
						"LEFT JOIN fnsemtypes USING (semtypeid)";
				break;

			case LEXUNITS_SENTENCES_BY_SENTENCE:
				groupBy = FrameNetContract.SENTENCE + ".sentenceid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case LEXUNITS_SENTENCES:
				table = "fnlexunits AS " + FrameNetContract.LU + ' ' + //
						"LEFT JOIN fnsubcorpuses USING (luid) " + //
						"LEFT JOIN fnsubcorpuses_sentences USING (subcorpusid) " + //
						"INNER JOIN fnsentences AS " + FrameNetContract.SENTENCE + " USING (sentenceid)";
				break;

			case LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE:
				groupBy = FrameNetContract.SENTENCE + ".sentenceid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS:
				table = "fnlexunits AS " + FrameNetContract.LU + ' ' + //
						"LEFT JOIN fnsubcorpuses USING (luid) " + //
						"LEFT JOIN fnsubcorpuses_sentences USING (subcorpusid) " + //
						"INNER JOIN fnsentences AS " + FrameNetContract.SENTENCE + " USING (sentenceid) " + //
						"LEFT JOIN fnannosets USING (sentenceid) " + //
						"LEFT JOIN fnlayers USING (annosetid) " + //
						"LEFT JOIN fnlayertypes USING (layertypeid) " + //
						"LEFT JOIN fnlabels USING (layerid) " + //
						"LEFT JOIN fnlabeltypes USING (labeltypeid) " + //
						"LEFT JOIN fnlabelitypes USING (labelitypeid)";
				break;

			case LEXUNITS_GOVERNORS:
				table = "fnlexunits " + //
						"INNER JOIN fnlexunits_governors USING (luid) " + //
						"INNER JOIN fngovernors USING (governorid) " + //
						"LEFT JOIN fnwords USING (fnwordid)";
				break;

			case GOVERNORS_ANNOSETS:
				table = "fngovernors_annosets " + //
						"LEFT JOIN fnannosets USING (annosetid) " + //
						"LEFT JOIN fnsentences USING (sentenceid)";
				break;

			case LEXUNITS_REALIZATIONS_BY_REALIZATION:
				groupBy = "ferid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case LEXUNITS_REALIZATIONS:
				table = "fnlexunits " + //
						"INNER JOIN fnferealizations USING (luid) " + //
						"LEFT JOIN fnvalenceunits USING (ferid) " + //
						"LEFT JOIN fnfetypes USING (fetypeid) " + //
						"LEFT JOIN fngftypes USING (gfid) " + //
						"LEFT JOIN fnpttypes USING (ptid)";
				break;

			case LEXUNITS_GROUPREALIZATIONS_BY_PATTERN:
				groupBy = "patternid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case LEXUNITS_GROUPREALIZATIONS:
				table = "fnlexunits " + //
						"LEFT JOIN fnfegrouprealizations USING (luid) " + //
						"LEFT JOIN fnpatterns USING (fegrid) " + //
						"LEFT JOIN fnpatterns_valenceunits USING (patternid) " + //
						"LEFT JOIN fnvalenceunits USING (vuid) " + //
						"LEFT JOIN fnfetypes USING (fetypeid) " + //
						"LEFT JOIN fngftypes USING (gfid) " + //
						"LEFT JOIN fnpttypes USING (ptid)";
				break;

			case PATTERNS_SENTENCES:
				table = "fnpatterns_annosets " + //
						"LEFT JOIN fnannosets AS " + FrameNetContract.ANNOSET + " USING (annosetid) " + //
						"LEFT JOIN fnsentences AS " + FrameNetContract.SENTENCE + " USING (sentenceid)";
				break;

			case VALENCEUNITS_SENTENCES:
				table = "fnvalenceunits_annosets " + //
						"LEFT JOIN fnannosets AS " + FrameNetContract.ANNOSET + " USING (annosetid) " + //
						"LEFT JOIN fnsentences AS " + FrameNetContract.SENTENCE + " USING (sentenceid)";
				break;

			case LOOKUP_FTS_SENTENCES:
				table = "fnsentences_text_fts4";
				break;

			default:
			case UriMatcher.NO_MATCH:
				throw new RuntimeException("Malformed URI " + uri);
		}

		if (BaseProvider.debugSql)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, actualSelection, groupBy, null, sortOrder, null);
			Log.d(FrameNetProvider.TAG + "SQL", sql);
			Log.d(FrameNetProvider.TAG + "ARGS", BaseProvider.argsToString(selectionArgs));
		}

		// do query
		try
		{
			return this.db.query(table, projection, actualSelection, selectionArgs, groupBy, null, sortOrder);
		}
		catch (SQLiteException e)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, actualSelection, groupBy, null, sortOrder, null);
			Log.d(TAG + "SQL", sql);
			Log.e(TAG, "FrameNet provider query failed", e);
			return null;
		}
	}
}
