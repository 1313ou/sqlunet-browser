package org.sqlunet.verbnet.provider;

import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.provider.SqlUNetProvider;

/**
 * WordNet provider
 *
 * @author Bernard Bou
 */
public class VerbNetProvider extends SqlUNetProvider
{
	static private final String TAG = "VerbNetProvider"; //$NON-NLS-1$

	// U R I M A T C H E R

	// uri matcher
	private static final UriMatcher uriMatcher;

	// table codes
	private static final int VNCLASS = 10;
	private static final int VNCLASSES = 11;
	private static final int VNCLASSES_X_BY_VNCLASS = 20;

	// join codes
	private static final int WORDS_VNCLASSES = 100;
	private static final int WORDS_VNCLASSES_VNGROUPING_BY_VNCLASS = 101;
	private static final int VNCLASSES_VNROLES_X_BY_VNROLE = 110;
	private static final int VNCLASSES_VNFRAMES_X_BY_VNFRAME = 120;

	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		VerbNetProvider.uriMatcher.addURI(VerbNetContract.AUTHORITY, VerbNetContract.VnClasses.TABLE, VerbNetProvider.VNCLASS);
		VerbNetProvider.uriMatcher.addURI(VerbNetContract.AUTHORITY, VerbNetContract.VnClasses.TABLE, VerbNetProvider.VNCLASSES);
		VerbNetProvider.uriMatcher.addURI(VerbNetContract.AUTHORITY, VerbNetContract.VnClasses_X.TABLE_BY_VNCLASS, VerbNetProvider.VNCLASSES_X_BY_VNCLASS);
		VerbNetProvider.uriMatcher.addURI(VerbNetContract.AUTHORITY, VerbNetContract.Words_VnClasses.TABLE, VerbNetProvider.WORDS_VNCLASSES);
		VerbNetProvider.uriMatcher.addURI(VerbNetContract.AUTHORITY, VerbNetContract.Words_VnClasses_VnGroupings.TABLE_BY_CLASS, VerbNetProvider.WORDS_VNCLASSES_VNGROUPING_BY_VNCLASS);
		VerbNetProvider.uriMatcher.addURI(VerbNetContract.AUTHORITY, VerbNetContract.VnClasses_VnRoles_X.TABLE_BY_ROLE, VerbNetProvider.VNCLASSES_VNROLES_X_BY_VNROLE);
		VerbNetProvider.uriMatcher.addURI(VerbNetContract.AUTHORITY, VerbNetContract.VnClasses_VnFrames_X.TABLE_BY_FRAME, VerbNetProvider.VNCLASSES_VNFRAMES_X_BY_VNFRAME);
	}

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public VerbNetProvider()
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
		switch (VerbNetProvider.uriMatcher.match(uri))
		{

			// I T E M S

			case VNCLASS:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + VerbNetContract.AUTHORITY + '.' + VerbNetContract.VnClasses.TABLE; //$NON-NLS-1$

			// T A B L E S

			case VNCLASSES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + VerbNetContract.AUTHORITY + '.' + VerbNetContract.VnClasses.TABLE; //$NON-NLS-1$

			// J O I N S

			case VNCLASSES_X_BY_VNCLASS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + VerbNetContract.AUTHORITY + '.' + VerbNetContract.VnClasses_X.TABLE_BY_VNCLASS; //$NON-NLS-1$
			case WORDS_VNCLASSES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + VerbNetContract.AUTHORITY + '.' + VerbNetContract.Words_VnClasses.TABLE; //$NON-NLS-1$
			case WORDS_VNCLASSES_VNGROUPING_BY_VNCLASS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + VerbNetContract.AUTHORITY + '.' + VerbNetContract.Words_VnClasses_VnGroupings.TABLE_BY_CLASS; //$NON-NLS-1$
			case VNCLASSES_VNROLES_X_BY_VNROLE:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + VerbNetContract.AUTHORITY + '.' + VerbNetContract.VnClasses_VnRoles_X.TABLE_BY_ROLE; //$NON-NLS-1$
			case VNCLASSES_VNFRAMES_X_BY_VNFRAME:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + VerbNetContract.AUTHORITY + '.' + VerbNetContract.VnClasses_VnFrames_X.TABLE_BY_FRAME; //$NON-NLS-1$

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
	public Cursor query(final Uri uri, final String[] projection, String selection0, final String[] selectionArgs, String sortOrder)
	{
		if (this.db == null)
		{
			open();
		}

		// choose the table to query and a sort order based on the code returned for the incoming URI
		String table;
		String selection = selection0;
		String groupBy = null;
		final int code = VerbNetProvider.uriMatcher.match(uri);
		Log.d(VerbNetProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code)); //$NON-NLS-1$ //$NON-NLS-2$
		switch (code)
		{

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case VNCLASS:
				table = VerbNetContract.VnClasses.TABLE;
				if (selection != null)
				{
					selection += " AND "; //$NON-NLS-1$
				}
				else
				{
					selection = ""; //$NON-NLS-1$
				}
				selection += VerbNetContract.VnClasses.CLASSID + " = " + uri.getLastPathSegment(); //$NON-NLS-1$
				break;

			case VNCLASSES:
				table = VerbNetContract.VnClasses.TABLE;
				break;

			case VNCLASSES_X_BY_VNCLASS:
				groupBy = "classid"; //$NON-NLS-1$
				table = "vnclasses " + // //$NON-NLS-1$
						"LEFT JOIN vngroupingmaps USING (classid) " + // //$NON-NLS-1$
						"LEFT JOIN vngroupings USING (groupingid)"; //$NON-NLS-1$
				break;

			// J O I N S

			case WORDS_VNCLASSES:
				table = "words " + // //$NON-NLS-1$
						"INNER JOIN vnwords USING (wordid) " + // //$NON-NLS-1$
						"INNER JOIN vnclassmembersenses USING (vnwordid) " + // //$NON-NLS-1$
						"LEFT JOIN vnclasses USING (classid)"; //$NON-NLS-1$
				break;

			case WORDS_VNCLASSES_VNGROUPING_BY_VNCLASS:
				groupBy = "classid"; //$NON-NLS-1$
				table = "words " + // //$NON-NLS-1$
						"INNER JOIN vnwords USING (wordid) " + // //$NON-NLS-1$
						"INNER JOIN vnclassmembersenses USING (vnwordid) " + // //$NON-NLS-1$
						"LEFT JOIN vnclasses USING (classid) " + // //$NON-NLS-1$
						"LEFT JOIN vngroupingmaps USING (classid, vnwordid) " + // //$NON-NLS-1$
						"LEFT JOIN vngroupings USING (groupingid)"; //$NON-NLS-1$
				break;

			case VNCLASSES_VNROLES_X_BY_VNROLE:
				groupBy = "roleid"; //$NON-NLS-1$
				table = "vnrolemaps " + // //$NON-NLS-1$
						"INNER JOIN vnroles USING (roleid) " + // //$NON-NLS-1$
						"INNER JOIN vnroletypes USING (roletypeid) " + // //$NON-NLS-1$
						"LEFT JOIN vnrestrs USING (restrsid)"; //$NON-NLS-1$
				break;

			case VNCLASSES_VNFRAMES_X_BY_VNFRAME:
				groupBy = "frameid"; //$NON-NLS-1$
				table = "vnframemaps " + // //$NON-NLS-1$
						"INNER JOIN vnframes USING (frameid) " + // //$NON-NLS-1$
						"LEFT JOIN vnframenames USING (nameid) " + // //$NON-NLS-1$
						"LEFT JOIN vnframesubnames USING (subnameid) " + // //$NON-NLS-1$
						"LEFT JOIN vnsyntaxes USING (syntaxid) " + // //$NON-NLS-1$
						"LEFT JOIN vnsemantics USING (semanticsid) " + // //$NON-NLS-1$
						"LEFT JOIN vnexamplemaps USING (frameid) " + // //$NON-NLS-1$
						"LEFT JOIN vnexamples USING (exampleid)"; //$NON-NLS-1$
				break;

			default:
			case UriMatcher.NO_MATCH:
				throw new RuntimeException("Malformed URI " + uri); //$NON-NLS-1$
		}

		if (SqlUNetProvider.debugSql)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, selection, groupBy, null, sortOrder, null);
			Log.d(VerbNetProvider.TAG + "SQL", sql); //$NON-NLS-1$
			Log.d(VerbNetProvider.TAG + "ARGS", SqlUNetProvider.argsToString(selectionArgs)); //$NON-NLS-1$
		}

		// do query
		try
		{
			return this.db.query(table, projection, selection, selectionArgs, groupBy, null, sortOrder);
		}
		catch (final SQLiteException e)
		{
			Log.e(VerbNetProvider.TAG, "VerbNet provider query failed", e); //$NON-NLS-1$
			return null;
		}
	}
}
