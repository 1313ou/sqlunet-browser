/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.provider;

import android.content.Context;
import android.content.Intent;

import org.sqlunet.browser.config.TableActivity;

import androidx.annotation.NonNull;

/**
 * Manager contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class ManagerContract
{
	/**
	 * Query tables and indexes intent factory
	 *
	 * @param context context
	 * @return intent
	 */
	@NonNull
	static public Intent makeTablesAndIndexesIntent(final Context context)
	{
		final Intent intent = new Intent(context, TableActivity.class);
		intent.putExtra(ProviderArgs.ARG_QUERYURI, ManagerProvider.makeUri(TablesAndIndices.CONTENT_URI_TABLE));
		intent.putExtra(ProviderArgs.ARG_QUERYID, "rowid");
		intent.putExtra(ProviderArgs.ARG_QUERYITEMS, new String[]{"rowid", TablesAndIndices.TYPE, TablesAndIndices.NAME});
		final String order = "CASE " //
				+ "WHEN " + TablesAndIndices.TYPE + " = 'table' THEN '1' " //
				+ "WHEN " + TablesAndIndices.TYPE + " = 'view' THEN '2' " //
				+ "WHEN " + TablesAndIndices.TYPE + " = 'index' THEN '3' " //
				+ "ELSE " + TablesAndIndices.TYPE + " END ASC," //
				+ TablesAndIndices.NAME + " ASC";
		intent.putExtra(ProviderArgs.ARG_QUERYSORT, order);
		intent.putExtra(ProviderArgs.ARG_QUERYFILTER, "name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android_%'");
		return intent;
	}

	/**
	 * Table and indices contract
	 */
	static public final class TablesAndIndices
	{
		static public final String TABLE = "sqlite_master";
		static public final String CONTENT_URI_TABLE = TablesAndIndices.TABLE;
		static public final String NAME = "name";
		static public final String TYPE = "type";
	}
}
