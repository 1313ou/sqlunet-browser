package org.sqlunet.framenet.provider;

import android.app.SearchManager;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import org.sqlunet.framenet.provider.FrameNetContract.AnnoSets;
import org.sqlunet.framenet.provider.FrameNetContract.AnnoSets_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.FnWords;
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
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.sql.SqlFormatter;

import java.io.InputStream;
import java.util.Properties;

/**
 * FrameNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameNetProvider extends BaseProvider
{
	static private final String TAG = "AFrameNetProvider";

	static private String AUTHORITY;

	static
	{
		try
		{
			final InputStream is = FrameNetProvider.class.getResourceAsStream("/org/sqlunet/config.properties");
			final Properties properties = new Properties();
			properties.load(is);

			AUTHORITY = properties.getProperty("framenetprovider");
			if (AUTHORITY == null || AUTHORITY.isEmpty())
			{
				throw new RuntimeException("Null framenet provider");
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	// U R I M A T C H E R

	static private UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static
	{
		matchURIs();
	}

	// table codes
	static private final int LEXUNIT = 10;
	static private final int LEXUNITS = 11;
	static private final int LEXUNITS_X_BY_LEXUNIT = 12;
	static private final int FRAME = 20;
	static private final int FRAMES = 21;
	static private final int FRAMES_X_BY_FRAME = 23;
	static private final int FRAMES_RELATED = 25;
	static private final int SENTENCE = 30;
	static private final int SENTENCES = 31;
	static private final int ANNOSET = 40;
	static private final int ANNOSETS = 41;
	static private final int SENTENCES_LAYERS_X = 50;
	static private final int ANNOSETS_LAYERS_X = 51;
	static private final int PATTERNS_LAYERS_X = 52;
	static private final int VALENCEUNITS_LAYERS_X = 53;
	static private final int PATTERNS_SENTENCES = 61;
	static private final int VALENCEUNITS_SENTENCES = 62;
	static private final int GOVERNORS_ANNOSETS = 70;

	// join codes
	static private final int WORDS_LEXUNITS_FRAMES = 100;
	static private final int LEXUNITS_OR_FRAMES = 101;
	static private final int FRAMES_FES = 200;
	static private final int FRAMES_FES_BY_FE = 201;
	static private final int LEXUNITS_SENTENCES = 300;
	static private final int LEXUNITS_SENTENCES_BY_SENTENCE = 301;
	static private final int LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS = 310;
	static private final int LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE = 311;
	static private final int LEXUNITS_GOVERNORS = 410;
	static private final int LEXUNITS_REALIZATIONS = 420;
	static private final int LEXUNITS_REALIZATIONS_BY_REALIZATION = 421;
	static private final int LEXUNITS_GROUPREALIZATIONS = 430;
	static private final int LEXUNITS_GROUPREALIZATIONS_BY_PATTERN = 431;

	// text search codes
	static private final int LOOKUP_FTS_WORDS = 510;
	static private final int LOOKUP_FTS_SENTENCES = 511;
	static private final int LOOKUP_FTS_SENTENCES_X = 512;
	static private final int LOOKUP_FTS_SENTENCES_X_BY_SENTENCE = 513;

	static private final int SUGGEST_WORDS = 601;
	static private final int SUGGEST_FTS_WORDS = 602;

	static private void matchURIs()
	{
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits.TABLE, FrameNetProvider.LEXUNIT);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits.TABLE, FrameNetProvider.LEXUNITS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_X.TABLE_BY_LEXUNIT, FrameNetProvider.LEXUNITS_X_BY_LEXUNIT);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_or_Frames.TABLE, FrameNetProvider.LEXUNITS_OR_FRAMES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames.TABLE, FrameNetProvider.FRAME);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames.TABLE, FrameNetProvider.FRAMES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames_X.TABLE_BY_FRAME, FrameNetProvider.FRAMES_X_BY_FRAME);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames_Related.TABLE, FrameNetProvider.FRAMES_RELATED);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Sentences.TABLE, FrameNetProvider.SENTENCE);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Sentences.TABLE, FrameNetProvider.SENTENCES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, AnnoSets.TABLE, FrameNetProvider.ANNOSET);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, AnnoSets.TABLE, FrameNetProvider.ANNOSETS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Sentences_Layers_X.TABLE, FrameNetProvider.SENTENCES_LAYERS_X);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, AnnoSets_Layers_X.TABLE, FrameNetProvider.ANNOSETS_LAYERS_X);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Patterns_Layers_X.TABLE, FrameNetProvider.PATTERNS_LAYERS_X);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, ValenceUnits_Layers_X.TABLE, FrameNetProvider.VALENCEUNITS_LAYERS_X);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Words_LexUnits_Frames.TABLE, FrameNetProvider.WORDS_LEXUNITS_FRAMES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames_FEs.TABLE, FrameNetProvider.FRAMES_FES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Frames_FEs.TABLE_BY_FE, FrameNetProvider.FRAMES_FES_BY_FE);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_Sentences.TABLE, FrameNetProvider.LEXUNITS_SENTENCES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_Sentences.TABLE_BY_SENTENCE, FrameNetProvider.LEXUNITS_SENTENCES_BY_SENTENCE);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_Sentences_AnnoSets_Layers_Labels.TABLE, FrameNetProvider.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_Sentences_AnnoSets_Layers_Labels.TABLE_BY_SENTENCE, FrameNetProvider.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_Governors.TABLE, FrameNetProvider.LEXUNITS_GOVERNORS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_FERealizations_ValenceUnits.TABLE, FrameNetProvider.LEXUNITS_REALIZATIONS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_FERealizations_ValenceUnits.TABLE_BY_REALIZATION, FrameNetProvider.LEXUNITS_REALIZATIONS_BY_REALIZATION);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE, FrameNetProvider.LEXUNITS_GROUPREALIZATIONS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE_BY_PATTERN, FrameNetProvider.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Patterns_Sentences.TABLE, FrameNetProvider.PATTERNS_SENTENCES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, ValenceUnits_Sentences.TABLE, FrameNetProvider.VALENCEUNITS_SENTENCES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Governors_AnnoSets_Sentences.TABLE, FrameNetProvider.GOVERNORS_ANNOSETS);

		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_FnWords.TABLE + "/*", FrameNetProvider.LOOKUP_FTS_WORDS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_FnWords.TABLE + "/", FrameNetProvider.LOOKUP_FTS_WORDS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_FnSentences.TABLE + "/", FrameNetProvider.LOOKUP_FTS_SENTENCES);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_FnSentences_X.TABLE + "/", FrameNetProvider.LOOKUP_FTS_SENTENCES_X);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_FnSentences_X.TABLE_BY_SENTENCE + "/", FrameNetProvider.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE);

		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FnWords.TABLE + "/*", FrameNetProvider.SUGGEST_WORDS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FnWords.TABLE + "/", FrameNetProvider.SUGGEST_WORDS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FTS_FnWords.TABLE + "/*", FrameNetProvider.SUGGEST_FTS_WORDS);
		FrameNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FTS_FnWords.TABLE + "/", FrameNetProvider.SUGGEST_FTS_WORDS);
	}

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

	// M I M E

	@Override
	public String getType(@NonNull final Uri uri)
	{
		switch (FrameNetProvider.uriMatcher.match(uri))
		{
			// TABLES

			case LEXUNIT:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits.TABLE;
			case LEXUNITS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits.TABLE;
			case LEXUNITS_X_BY_LEXUNIT:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_X.TABLE_BY_LEXUNIT;
			case LEXUNITS_OR_FRAMES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_or_Frames.TABLE;
			case FRAME:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Frames.TABLE;
			case FRAMES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Frames.TABLE;
			case FRAMES_X_BY_FRAME:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Frames_X.TABLE_BY_FRAME;
			case FRAMES_RELATED:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Frames_Related.TABLE;
			case SENTENCE:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Sentences.TABLE;
			case SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Sentences.TABLE;
			case ANNOSET:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + AnnoSets.TABLE;
			case ANNOSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + AnnoSets.TABLE;
			case SENTENCES_LAYERS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Sentences_Layers_X.TABLE;
			case ANNOSETS_LAYERS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + AnnoSets_Layers_X.TABLE;
			case PATTERNS_LAYERS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Patterns_Layers_X.TABLE;
			case VALENCEUNITS_LAYERS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + ValenceUnits_Layers_X.TABLE;
			case WORDS_LEXUNITS_FRAMES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_LexUnits_Frames.TABLE;
			case FRAMES_FES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Frames_FEs.TABLE;
			case LEXUNITS_SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_Sentences.TABLE;
			case LEXUNITS_GOVERNORS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_Governors.TABLE;
			case LEXUNITS_REALIZATIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_FERealizations_ValenceUnits.TABLE;
			case LEXUNITS_REALIZATIONS_BY_REALIZATION:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_FERealizations_ValenceUnits.TABLE_BY_REALIZATION;
			case LEXUNITS_GROUPREALIZATIONS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE;
			case LEXUNITS_GROUPREALIZATIONS_BY_PATTERN:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE_BY_PATTERN;
			case PATTERNS_SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Patterns_Sentences.TABLE;
			case VALENCEUNITS_SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + ValenceUnits_Sentences.TABLE;
			case GOVERNORS_ANNOSETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Governors_AnnoSets_Sentences.TABLE;

			// L O O K U P
			case LOOKUP_FTS_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + FnWords.TABLE;
			case LOOKUP_FTS_SENTENCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_FnSentences.TABLE;
			case LOOKUP_FTS_SENTENCES_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_FnSentences_X.TABLE;
			case LOOKUP_FTS_SENTENCES_X_BY_SENTENCE:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_FnSentences_X.TABLE_BY_SENTENCE;

			// S U G G E S T
			case SUGGEST_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + FnWords.TABLE;
			case SUGGEST_FTS_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + FnWords.TABLE;

			default:
				throw new UnsupportedOperationException("Illegal MIME type");
		}
	}

	// Q U E R Y

	@SuppressWarnings("boxing")
	@Override
	public Cursor query(@NonNull final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, String sortOrder)
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

			case LEXUNITS_OR_FRAMES:
				table = "(" + //
						"SELECT fnwordid + 10000 AS " + LexUnits_or_Frames.ID + ", luid AS " + LexUnits_or_Frames.FNID + ", fnwordid AS " + LexUnits_or_Frames.FNWORDID + ", wordid AS " + LexUnits_or_Frames.WORDID + ", word AS " + LexUnits_or_Frames.WORD + ", lexunit AS " + LexUnits_or_Frames.NAME + ", 0 AS " + LexUnits_or_Frames.ISFRAME + " " + //
						"FROM fnwords " + //
						"INNER JOIN fnlexemes USING (fnwordid) " + //
						"INNER JOIN fnlexunits AS " + FrameNetContract.LU + " USING (luid) " + //
						"UNION " + //
						"SELECT frameid AS " + LexUnits_or_Frames.ID + ", frameid AS " + LexUnits_or_Frames.FNID + ", 0 AS " + LexUnits_or_Frames.FNWORDID + ", 0 AS " + LexUnits_or_Frames.WORDID + ", frame AS " + LexUnits_or_Frames.WORD + ", frame AS " + LexUnits_or_Frames.NAME + ", 1 AS " + LexUnits_or_Frames.ISFRAME + " " + //
						"FROM fnframes " + //
						")";
				break;

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
				table = "(SELECT annosetid,sentenceid,layerid,layertype,rank," + //
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
				table = "(SELECT annosetid,sentenceid,text,layerid,layertype,rank," + //
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
				table = "(SELECT annosetid,sentenceid,text,layerid,layertype,rank," + //
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
						"INNER JOIN fnfegrouprealizations USING (luid) " + //
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

			// L O O K U P

			case LOOKUP_FTS_WORDS:
				table = "fnwords_word_fts4";
				break;
			case LOOKUP_FTS_SENTENCES:
				table = "fnsentences_text_fts4";
				break;
			case LOOKUP_FTS_SENTENCES_X_BY_SENTENCE:
				groupBy = "sentenceid";
				//addProjection(projection, "GROUP_CONCAT(DISTINCT  frame || '@' || frameid)", "GROUP_CONCAT(DISTINCT  lexunit || '@' || luid)");
				//$FALL-THROUGH$
				//noinspection fallthrough
			case LOOKUP_FTS_SENTENCES_X:
				table = "fnsentences_text_fts4 " + //
						"LEFT JOIN fnframes USING (frameid) " + //
						"LEFT JOIN fnlexunits USING (frameid,luid)";
				break;

			// S U G G E S T

			case SUGGEST_WORDS:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}
				table = "fnwords";
				return this.db.query(table, new String[]{"fnwordid AS _id", //
								"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
								"word AS " + SearchManager.SUGGEST_COLUMN_QUERY}, //
						"word LIKE ? || '%'", //
						new String[]{last}, null, null, null);
			}
			case SUGGEST_FTS_WORDS:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}
				table = "fnwords_word_fts4";
				return this.db.query(table, new String[]{"fnwordid AS _id", //
								"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
								"word AS " + SearchManager.SUGGEST_COLUMN_QUERY}, //
						"word MATCH ?", //
						new String[]{last}, null, null, null);
			}

			default:
			case UriMatcher.NO_MATCH:
				throw new RuntimeException("Malformed URI " + uri);
		}

		final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, actualSelection, groupBy, null, sortOrder, null);
		logSql(sql, selectionArgs);
		if (BaseProvider.logSql)
		{
			Log.d(FrameNetProvider.TAG + "SQL", SqlFormatter.format(sql).toString());
			Log.d(FrameNetProvider.TAG + "ARGS", BaseProvider.argsToString(selectionArgs));
		}

		// do query
		try

		{
			return this.db.query(table, projection, actualSelection, selectionArgs, groupBy, null, sortOrder);
		}

		catch (SQLiteException e)

		{
			Log.d(TAG + "SQL", sql);
			Log.e(TAG, "FrameNet provider query failed", e);
			return null;
		}
	}
}
