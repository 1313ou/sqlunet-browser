package org.sqlunet.browser.config;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Log;

import org.sqlunet.browser.common.R;
import org.sqlunet.provider.ManagerContract;
import org.sqlunet.provider.ManagerContract.TablesAndIndices;
import org.sqlunet.provider.ManagerProvider;
import org.sqlunet.settings.StorageSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Database _status
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class Status
{
	static private final String TAG = "Status";

	static public final String CANTRUN = "_status.cant.run";

	// _status flags

	static public final int EXISTS = 0x1;

	static public final int EXISTS_TABLES = 0x2;

	static public final int EXISTS_INDEXES = 0x10;

	/**
	 * Get _status
	 *
	 * @param context context
	 * @return _status
	 */
	static public int status(@NonNull final Context context)
	{
		if (existsDatabase(context))
		{
			int status = EXISTS;

			final Resources res = context.getResources();
			final String[] requiredTables = res.getStringArray(R.array.required_tables);
			final String[] requiredIndexes = res.getStringArray(R.array.required_indexes);

			List<String> existingTablesAndIndexes;
			try
			{
				existingTablesAndIndexes = tablesAndIndexes(context);
			}
			catch (Exception e)
			{
				Log.e(TAG, "While getting _status", e);
				return status;
			}

			boolean existsTables = contains(existingTablesAndIndexes, requiredTables);
			boolean existsIdx = contains(existingTablesAndIndexes, requiredIndexes);

			if (existsTables)
			{
				status |= EXISTS_TABLES;
			}
			if (existsIdx)
			{
				status |= EXISTS_INDEXES;
			}
			return status;
		}
		return 0;
	}

	/**
	 * Can run _status
	 *
	 * @param context context
	 * @return true if app is ready to run
	 */
	static public boolean canRun(@NonNull final Context context)
	{
		final int status = status(context);
		return (status & (EXISTS | EXISTS_TABLES | EXISTS_INDEXES)) == (EXISTS | EXISTS_TABLES | EXISTS_INDEXES);
	}

	/**
	 * Test existence of database
	 *
	 * @param context context
	 * @return true if database exists
	 */
	static protected boolean existsDatabase(@NonNull final Context context)
	{
		final String databasePath = StorageSettings.getDatabasePath(context);
		final File db = new File(databasePath);
		return db.exists() && db.isFile() && db.canWrite();
	}

	/**
	 * Get tables and indexes
	 *
	 * @param context context
	 * @return list of tables and indexes
	 */
	@Nullable
	static protected List<String> tablesAndIndexes(@NonNull final Context context)
	{
		final String order = "CASE " //
				+ "WHEN " + ManagerContract.TablesAndIndices.TYPE + " = 'table' THEN '1' " //
				+ "WHEN " + ManagerContract.TablesAndIndices.TYPE + " = 'view' THEN '2' " //
				+ "WHEN " + ManagerContract.TablesAndIndices.TYPE + " = 'index' THEN '3' " //
				+ "ELSE " + ManagerContract.TablesAndIndices.TYPE + " END ASC," //
				+ ManagerContract.TablesAndIndices.NAME + " ASC";
		final Cursor cursor = context.getContentResolver().query( //
				Uri.parse(ManagerProvider.makeUri(TablesAndIndices.CONTENT_URI_TABLE)), //
				new String[]{TablesAndIndices.TYPE, TablesAndIndices.NAME}, // projection
				"name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android_%'", // selection criteria //
				null, //
				order);
		List<String> result = null;
		if (cursor != null)
		{
			if (cursor.moveToFirst())
			{
				final int nameId = cursor.getColumnIndex(TablesAndIndices.NAME);
				result = new ArrayList<>();
				do
				{
					final String name = cursor.getString(nameId);
					result.add(name);
				}
				while (cursor.moveToNext());
			}

			cursor.close();
		}
		return result;
	}

	/**
	 * Test if targets are contained in tables and indexes
	 *
	 * @param tablesAndIndexes tables and indexes
	 * @param targets          targets
	 * @return true if targets are contained in tables and indexes
	 */
	static protected boolean contains(@Nullable final Collection<String> tablesAndIndexes, @NonNull final String... targets)
	{
		if (tablesAndIndexes == null)
		{
			return false;
		}
		boolean result = tablesAndIndexes.containsAll(Arrays.asList(targets));
		if (!result)
		{
			for (String target : targets)
			{
				if (!tablesAndIndexes.contains(target))
				{
					Log.d(TAG, "ABSENT " + target);
				}
			}
		}
		return result;
	}

	@NonNull
	static public CharSequence toString(int status)
	{
		final Editable sb = new SpannableStringBuilder();
		sb.append(Integer.toHexString(status));
		if ((status & EXISTS) != 0)
		{
			sb.append(" file");
		}
		if ((status & EXISTS_TABLES) != 0)
		{
			sb.append(" tables");
		}
		if ((status & EXISTS_INDEXES) != 0)
		{
			sb.append(" indexes");
		}
		return sb;
	}
}
