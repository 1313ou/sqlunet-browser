package org.sqlunet.verbnet.provider;

import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.provider.BaseProvider;

/**
 * VerbNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VerbNetProvider extends BaseProvider
{
	static private final String TAG = "VerbNetProvider";

	// U R I M A T C H E R

	// uri matcher
	private static final UriMatcher uriMatcher;

	// table codes
	private static final int VNCLASS = 10;
	private static final int VNCLASSES = 11;
	private static final int VNCLASSES_X_BY_VNCLASS = 20;

	// join codes
	private static final int WORDS_VNCLASSES = 100;
	private static final int VNCLASSES_VNMEMBERS_X_BY_WORD = 110;
	private static final int VNCLASSES_VNROLES_X_BY_VNROLE = 120;
	private static final int VNCLASSES_VNFRAMES_X_BY_VNFRAME = 130;

	// text search codes
	private static final int LOOKUP_FTS_EXAMPLES = 501;

	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		VerbNetProvider.uriMatcher.addURI(VerbNetContract.AUTHORITY, VerbNetContract.VnClasses.TABLE, VerbNetProvider.VNCLASS);
		VerbNetProvider.uriMatcher.addURI(VerbNetContract.AUTHORITY, VerbNetContract.VnClasses.TABLE, VerbNetProvider.VNCLASSES);
		VerbNetProvider.uriMatcher.addURI(VerbNetContract.AUTHORITY, VerbNetContract.Words_VnClasses.TABLE, VerbNetProvider.WORDS_VNCLASSES);
		VerbNetProvider.uriMatcher.addURI(VerbNetContract.AUTHORITY, VerbNetContract.VnClasses_VnMembers_X.TABLE_BY_WORD, VerbNetProvider.VNCLASSES_VNMEMBERS_X_BY_WORD);
		VerbNetProvider.uriMatcher.addURI(VerbNetContract.AUTHORITY, VerbNetContract.VnClasses_VnRoles_X.TABLE_BY_ROLE, VerbNetProvider.VNCLASSES_VNROLES_X_BY_VNROLE);
		VerbNetProvider.uriMatcher.addURI(VerbNetContract.AUTHORITY, VerbNetContract.VnClasses_VnFrames_X.TABLE_BY_FRAME, VerbNetProvider.VNCLASSES_VNFRAMES_X_BY_VNFRAME);

		VerbNetProvider.uriMatcher.addURI(VerbNetContract.AUTHORITY, VerbNetContract.Lookup_VnExamples.TABLE + "/", VerbNetProvider.LOOKUP_FTS_EXAMPLES);
	}

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public VerbNetProvider()
	{
	}

	// M I M E

	@Override
	public String getType(final Uri uri)
	{
		switch (VerbNetProvider.uriMatcher.match(uri))
		{

			// I T E M S

			case VNCLASS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + VerbNetContract.AUTHORITY + '.' + VerbNetContract.VnClasses.TABLE;

			// T A B L E S

			case VNCLASSES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + VerbNetContract.AUTHORITY + '.' + VerbNetContract.VnClasses.TABLE;

			// J O I N S

			case WORDS_VNCLASSES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + VerbNetContract.AUTHORITY + '.' + VerbNetContract.Words_VnClasses.TABLE;
			case VNCLASSES_VNMEMBERS_X_BY_WORD:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + VerbNetContract.AUTHORITY + '.' + VerbNetContract.VnClasses_VnMembers_X.TABLE_BY_WORD;
			case VNCLASSES_VNROLES_X_BY_VNROLE:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + VerbNetContract.AUTHORITY + '.' + VerbNetContract.VnClasses_VnRoles_X.TABLE_BY_ROLE;
			case VNCLASSES_VNFRAMES_X_BY_VNFRAME:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + VerbNetContract.AUTHORITY + '.' + VerbNetContract.VnClasses_VnFrames_X.TABLE_BY_FRAME;

			// S E A R C H
			case LOOKUP_FTS_EXAMPLES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + VerbNetContract.AUTHORITY + '.' + VerbNetContract.Lookup_VnExamples.TABLE;

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
				table = VerbNetContract.VnClasses.TABLE;
				if (actualSelection != null)
				{
					actualSelection += " AND ";
				}
				else
				{
					actualSelection = "";
				}
				actualSelection += VerbNetContract.VnClasses.CLASSID + " = " + uri.getLastPathSegment();
				break;

			case VNCLASSES:
				table = VerbNetContract.VnClasses.TABLE;
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

			case LOOKUP_FTS_EXAMPLES:
				table = "vnexamples_example_fts4";
				break;

			default:
			case UriMatcher.NO_MATCH:
				throw new RuntimeException("Malformed URI " + uri);
		}

		if (BaseProvider.debugSql)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, actualSelection, groupBy, null, sortOrder, null);
			Log.d(VerbNetProvider.TAG + "SQL", sql);
			Log.d(VerbNetProvider.TAG + "ARGS", BaseProvider.argsToString(selectionArgs));
		}

		// do query
		try
		{
			return this.db.query(table, projection, actualSelection, selectionArgs, groupBy, null, sortOrder, null);
		}
		catch (final SQLiteException e)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, actualSelection, groupBy, null, sortOrder, null);
			Log.d(TAG + "SQL", sql);
			Log.e(VerbNetProvider.TAG, "VerbNet provider query failed", e);
			return null;
		}
	}
}
