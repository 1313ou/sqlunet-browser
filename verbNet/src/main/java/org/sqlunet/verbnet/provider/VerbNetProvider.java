/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.verbnet.provider;

import android.app.SearchManager;
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
import org.sqlunet.verbnet.provider.VerbNetContract.Lookup_VnExamples;
import org.sqlunet.verbnet.provider.VerbNetContract.Lookup_VnExamples_X;
import org.sqlunet.verbnet.provider.VerbNetContract.Suggest_FTS_VnWords;
import org.sqlunet.verbnet.provider.VerbNetContract.Suggest_VnWords;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnFrames_X;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnMembers_X;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnRoles_X;
import org.sqlunet.verbnet.provider.VerbNetContract.VnWords;
import org.sqlunet.verbnet.provider.VerbNetContract.Words_VnClasses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * VerbNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VerbNetProvider extends BaseProvider
{
	static private final String TAG = "VerbNetProvider";

	// C O N T E N T   P R O V I D E R   A U T H O R I T Y

	static private final String AUTHORITY = makeAuthority("verbnet_authority");

	// U R I M A T C H E R

	static private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static
	{
		matchURIs();
	}

	// table codes
	static private final int VNCLASS = 10;
	static private final int VNCLASSES = 11;
	static private final int VNCLASSES_X_BY_VNCLASS = 20;

	// join codes
	static private final int WORDS_VNCLASSES = 100;
	static private final int VNCLASSES_VNMEMBERS_X_BY_WORD = 110;
	static private final int VNCLASSES_VNROLES_X_BY_VNROLE = 120;
	static private final int VNCLASSES_VNFRAMES_X_BY_VNFRAME = 130;

	// search text codes
	static private final int LOOKUP_FTS_EXAMPLES = 501;
	static private final int LOOKUP_FTS_EXAMPLES_X = 511;
	static private final int LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE = 512;

	// suggest
	static private final int SUGGEST_WORDS = 601;
	static private final int SUGGEST_FTS_WORDS = 602;

	static private void matchURIs()
	{
		VerbNetProvider.uriMatcher.addURI(AUTHORITY, VnClasses.TABLE, VerbNetProvider.VNCLASS);
		VerbNetProvider.uriMatcher.addURI(AUTHORITY, VnClasses.TABLE, VerbNetProvider.VNCLASSES);
		VerbNetProvider.uriMatcher.addURI(AUTHORITY, Words_VnClasses.TABLE, VerbNetProvider.WORDS_VNCLASSES);
		VerbNetProvider.uriMatcher.addURI(AUTHORITY, VnClasses_VnMembers_X.TABLE_BY_WORD, VerbNetProvider.VNCLASSES_VNMEMBERS_X_BY_WORD);
		VerbNetProvider.uriMatcher.addURI(AUTHORITY, VnClasses_VnRoles_X.TABLE_BY_ROLE, VerbNetProvider.VNCLASSES_VNROLES_X_BY_VNROLE);
		VerbNetProvider.uriMatcher.addURI(AUTHORITY, VnClasses_VnFrames_X.TABLE_BY_FRAME, VerbNetProvider.VNCLASSES_VNFRAMES_X_BY_VNFRAME);

		VerbNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_VnExamples.TABLE + "/", VerbNetProvider.LOOKUP_FTS_EXAMPLES);
		VerbNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_VnExamples_X.TABLE + "/", VerbNetProvider.LOOKUP_FTS_EXAMPLES_X);
		VerbNetProvider.uriMatcher.addURI(AUTHORITY, Lookup_VnExamples_X.TABLE_BY_EXAMPLE + "/", VerbNetProvider.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE);

		VerbNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_VnWords.TABLE + "/*", VerbNetProvider.SUGGEST_WORDS);
		VerbNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_VnWords.TABLE + "/", VerbNetProvider.SUGGEST_WORDS);
		VerbNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FTS_VnWords.TABLE + "/*", VerbNetProvider.SUGGEST_FTS_WORDS);
		VerbNetProvider.uriMatcher.addURI(AUTHORITY, Suggest_FTS_VnWords.TABLE + "/", VerbNetProvider.SUGGEST_FTS_WORDS);
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
	public VerbNetProvider()
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
		switch (VerbNetProvider.uriMatcher.match(uri))
		{
			// I T E M S
			case VNCLASS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + VnClasses.TABLE;

			// T A B L E S
			case VNCLASSES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + VnClasses.TABLE;

			// J O I N S
			case WORDS_VNCLASSES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Words_VnClasses.TABLE;
			case VNCLASSES_VNMEMBERS_X_BY_WORD:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + VnClasses_VnMembers_X.TABLE_BY_WORD;
			case VNCLASSES_VNROLES_X_BY_VNROLE:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + VnClasses_VnRoles_X.TABLE_BY_ROLE;
			case VNCLASSES_VNFRAMES_X_BY_VNFRAME:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + VnClasses_VnFrames_X.TABLE_BY_FRAME;

			// L O O K U P
			case LOOKUP_FTS_EXAMPLES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_VnExamples.TABLE;
			case LOOKUP_FTS_EXAMPLES_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_VnExamples_X.TABLE;
			case LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + Lookup_VnExamples_X.TABLE_BY_EXAMPLE;

			// S U G G E S T
			case SUGGEST_WORDS:
			case SUGGEST_FTS_WORDS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + VnWords.TABLE;

			default:
				throw new UnsupportedOperationException("Illegal MIME type");
		}
	}

	// Q U E R Y

	@Nullable
	@SuppressWarnings("boxing")
	@Override
	public Cursor query(@NonNull final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, String sortOrder)
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
		String actualSelection = selection;
		final int code = VerbNetProvider.uriMatcher.match(uri);
		Log.d(VerbNetProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code));
		String groupBy = null;
		String table;
		switch (code)
		{
			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case VNCLASS:
				table = VnClasses.TABLE;
				if (actualSelection != null)
				{
					actualSelection += " AND ";
				}
				else
				{
					actualSelection = "";
				}
				actualSelection += VnClasses.CLASSID + " = " + uri.getLastPathSegment();
				break;

			case VNCLASSES:
				table = VnClasses.TABLE;
				break;

			case VNCLASSES_X_BY_VNCLASS:
				groupBy = "classid";
				table = "vnclasses " + //
						"LEFT JOIN vngroupingmaps USING (classid) " + //
						"LEFT JOIN vngroupings USING (groupingid)";
				break;

			// J O I N S

			case WORDS_VNCLASSES:
				table = "words " + //
						"INNER JOIN vnwords USING (wordid) " + //
						"INNER JOIN vnclassmembersenses USING (vnwordid) " + //
						"LEFT JOIN vnclasses USING (classid)";
				break;

			case VNCLASSES_VNMEMBERS_X_BY_WORD:
				groupBy = "vnwordid";
				table = "vnclassmembersenses " + //
						"LEFT JOIN vnwords USING (vnwordid) " + //
						"LEFT JOIN vngroupingmaps USING (classid, vnwordid) " + //
						"LEFT JOIN vngroupings USING (groupingid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				break;

			case VNCLASSES_VNROLES_X_BY_VNROLE:
				groupBy = "roleid";
				table = "vnrolemaps " + //
						"INNER JOIN vnroles USING (roleid) " + //
						"INNER JOIN vnroletypes USING (roletypeid) " + //
						"LEFT JOIN vnrestrs USING (restrsid)";
				break;

			case VNCLASSES_VNFRAMES_X_BY_VNFRAME:
				groupBy = "frameid";
				table = "vnframemaps " + //
						"INNER JOIN vnframes USING (frameid) " + //
						"LEFT JOIN vnframenames USING (nameid) " + //
						"LEFT JOIN vnframesubnames USING (subnameid) " + //
						"LEFT JOIN vnsyntaxes USING (syntaxid) " + //
						"LEFT JOIN vnsemantics USING (semanticsid) " + //
						"LEFT JOIN vnexamplemaps USING (frameid) " + //
						"LEFT JOIN vnexamples USING (exampleid)";
				break;

			// L O O K U P

			case LOOKUP_FTS_EXAMPLES:
				table = "vnexamples_example_fts4";
				break;
			case LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE:
				groupBy = "exampleid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case LOOKUP_FTS_EXAMPLES_X:
				table = "vnexamples_example_fts4 " + //
						"LEFT JOIN vnclasses USING (classid)";
				break;

			// S U G G E S T

			case SUGGEST_WORDS:
			{
				final String last = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				{
					return null;
				}
				table = "vnwords";
				return this.db.query(table, new String[]{"vnwordid AS _id", //
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
				table = "vnwords_word_fts4";
				return this.db.query(table, new String[]{"vnwordid AS _id", //
								"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
								"word AS " + SearchManager.SUGGEST_COLUMN_QUERY}, //
						"word MATCH ?", //
						new String[]{last + '*'}, null, null, null);
			}

			default:
			case UriMatcher.NO_MATCH:
				throw new RuntimeException("Malformed URI " + uri);
		}

		final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, actualSelection, groupBy, null, sortOrder, null);
		logSql(sql, selectionArgs);
		if (BaseProvider.logSql)
		{
			Log.d(VerbNetProvider.TAG + "SQL", SqlFormatter.format(sql).toString());
			Log.d(VerbNetProvider.TAG + "ARGS", BaseProvider.argsToString(selectionArgs));
		}

		// do query
		try
		{
			return this.db.rawQuery(sql, selectionArgs);
			//return this.db.query(table, projection, actualSelection, selectionArgs, groupBy, null, sortOrder, null);
		}
		catch (@NonNull final SQLiteException e)
		{
			Log.d(TAG + "SQL", sql);
			Log.e(VerbNetProvider.TAG, "VerbNet provider query failed", e);
			return null;
		}
	}
}
