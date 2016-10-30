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
	static public final String AUTHORITY = "org.sqlunet.provider.manager"; //

	/**
	 * QueryData tables and indexes intent factory
	 *
	 * @param context context
	 * @return intent
	 */
	static public Intent makeTablesAndIndexesIntent(final Context context)
	{
		final Intent intent = new Intent(context, TableActivity.class);
		intent.putExtra(SqlUNetContract.ARG_QUERYURI, TablesAndIndices.CONTENT_URI);
		intent.putExtra(SqlUNetContract.ARG_QUERYID, "rowid"); //
		intent.putExtra(SqlUNetContract.ARG_QUERYITEMS, new String[]{"rowid", TablesAndIndices.TYPE, TablesAndIndices.NAME}); //
		final String order = "CASE " //
				+ "WHEN " + TablesAndIndices.TYPE + " = 'table' THEN '1' " //
				+ "WHEN " + TablesAndIndices.TYPE + " = 'view' THEN '2' " //
				+ "WHEN " + TablesAndIndices.TYPE + " = 'index' THEN '3' " //
				+ "ELSE " + TablesAndIndices.TYPE + " END ASC," //
				+ TablesAndIndices.NAME + " ASC"; //
		intent.putExtra(SqlUNetContract.ARG_QUERYSORT, order);
		intent.putExtra(SqlUNetContract.ARG_QUERYFILTER, "name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android_%'"); //
		return intent;
	}

	/**
	 * Table and indices contract
	 */
	static public final class TablesAndIndices
	{
		static public final String TABLE = "sqlite_master"; //
		static public final String CONTENT_URI = "content://" + ManagerContract.AUTHORITY + '/' + TablesAndIndices.TABLE; //
		static public final String NAME = "name"; //
		static public final String TYPE = "type"; //
	}
}
