package org.sqlunet.provider;

import android.content.Context;
import android.content.Intent;

import org.sqlunet.browser.config.TableActivity;

/**
 * Manager contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class ManagerContract
{
	static public final String AUTHORITY = "org.sqlunet.provider.manager"; //$NON-NLS-1$

	/**
	 * Query tables and indexes intent factory
	 *
	 * @param context context
	 * @return intent
	 */
	static public Intent makeTablesAndIndexesIntent(final Context context)
	{
		final Intent intent = new Intent(context, TableActivity.class);
		intent.putExtra(SqlUNetContract.ARG_QUERYURI, TablesAndIndices.CONTENT_URI);
		intent.putExtra(SqlUNetContract.ARG_QUERYID, "rowid"); //$NON-NLS-1$
		intent.putExtra(SqlUNetContract.ARG_QUERYITEMS, new String[]{"rowid", TablesAndIndices.TYPE, TablesAndIndices.NAME}); //$NON-NLS-1$
		final String order = "CASE " // //$NON-NLS-1$
				+ "WHEN " + TablesAndIndices.TYPE + " = 'table' THEN '1' " // //$NON-NLS-1$ //$NON-NLS-2$
				+ "WHEN " + TablesAndIndices.TYPE + " = 'view' THEN '2' " // //$NON-NLS-1$ //$NON-NLS-2$
				+ "WHEN " + TablesAndIndices.TYPE + " = 'index' THEN '3' " // //$NON-NLS-1$ //$NON-NLS-2$
				+ "ELSE " + TablesAndIndices.TYPE + " END ASC," // //$NON-NLS-1$ //$NON-NLS-2$
				+ TablesAndIndices.NAME + " ASC"; //$NON-NLS-1$ //$NON-NLS-1$
		intent.putExtra(SqlUNetContract.ARG_QUERYSORT, order);
		intent.putExtra(SqlUNetContract.ARG_QUERYFILTER, "name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android_%'"); //$NON-NLS-1$
		return intent;
	}

	/**
	 * Table and indices contract
	 */
	static public final class TablesAndIndices
	{
		static public final String TABLE = "sqlite_master"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + ManagerContract.AUTHORITY + '/' + TablesAndIndices.TABLE; //$NON-NLS-1$
		static public final String NAME = "name"; //$NON-NLS-1$
		static public final String TYPE = "type"; //$NON-NLS-1$
	}
}
