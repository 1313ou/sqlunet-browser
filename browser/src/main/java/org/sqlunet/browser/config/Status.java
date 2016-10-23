package org.sqlunet.browser.config;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.provider.ManagerContract;
import org.sqlunet.provider.ManagerContract.TablesAndIndices;
import org.sqlunet.settings.StorageSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Database status
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Status
{
	static private final String TAG = "SqlUNet Status"; //

	// status flags

	private static final int EXISTS = 0x1;

	static public final int EXISTS_IDX = 0x10;

	static public final int EXISTS_PM = 0x20;

	static public final int EXISTS_TS = 0x100;

	static public final int EXISTS_TSFN = 0x200;

	/**
	 * Get status
	 *
	 * @param context context
	 * @return status
	 */
	static public int status(final Context context)
	{
		if (existsDatabase(context))
		{
			int status = EXISTS;

			List<String> existingTablesAndIndexes;
			try
			{
				existingTablesAndIndexes = tablesAndIndexes(context);
			}
			catch (Exception e)
			{
				Log.e(TAG, "While getting status", e); //
				return status;
			}

			boolean existsIdx = contains(existingTablesAndIndexes, "index_words_lemma", "index_senses_wordid", "index_senses_synsetid", "index_synsets_synsetid", //
					"index_casedwords_wordid_casedwordid", //
					"index_semlinks_synset1id", "index_semlinks_linkid", //
					"index_samples_synsetid", //
					"index_vnwords_wordid", "index_vnrolemaps_classid", "index_vnframemaps_classid", //
					"index_pbwords_wordid", "index_pbrolesets_pbwordid", "index_pbroles_rolesetid", "index_pbexamples_rolesetid", //
					"index_fnwords_wordid", "index_fnlexemes_fnwordid", "index_fnfes_frameid", "index_fnlayers_annosetid", "index_fnlabels_layerid", //
					"index_pm_wordid", "index_pm_synsetid"); //
			boolean existsPm = contains(existingTablesAndIndexes, "pmvn", "pmpb", "pmfn"); //
			boolean existsTs = contains(existingTablesAndIndexes, "words_lemma_fts4", "synsets_definition_fts4", "samples_sample_fts4"); //
			boolean existsTsFn = contains(existingTablesAndIndexes, "fnsentences_text_fts4"); //

			if (existsIdx)
			{
				status |= EXISTS_IDX;
			}
			if (existsPm)
			{
				status |= EXISTS_PM;
			}
			if (existsTs)
			{
				status |= EXISTS_TS;
			}
			if (existsTsFn)
			{
				status |= EXISTS_TSFN;
			}
			return status;
		}
		return 0;
	}

	/**
	 * Can run status
	 *
	 * @param context context
	 * @return true if app is ready to run
	 */
	static public boolean canRun(final Context context)
	{
		final int status = status(context);
		return (status & (EXISTS | EXISTS_IDX | EXISTS_PM)) == (EXISTS | EXISTS_IDX | EXISTS_PM);
	}

	/**
	 * Test existence of database
	 *
	 * @param context context
	 * @return true if database exists
	 */
	private static boolean existsDatabase(final Context context)
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
	private static List<String> tablesAndIndexes(final Context context)
	{
		final String order = "CASE " //
				+ "WHEN " + ManagerContract.TablesAndIndices.TYPE + " = 'table' THEN '1' " //
				+ "WHEN " + ManagerContract.TablesAndIndices.TYPE + " = 'view' THEN '2' " //
				+ "WHEN " + ManagerContract.TablesAndIndices.TYPE + " = 'index' THEN '3' " //
				+ "ELSE " + ManagerContract.TablesAndIndices.TYPE + " END ASC," //
				+ ManagerContract.TablesAndIndices.NAME + " ASC"; //

		final Cursor cursor = context.getContentResolver().query( //
				Uri.parse(TablesAndIndices.CONTENT_URI), //
				new String[]{TablesAndIndices.TYPE, TablesAndIndices.NAME}, // projection
				"name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android_%'", // selection criteria //
				null, //
				order);
		assert cursor != null;
		List<String> result = null;
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
		return result;
	}

	/**
	 * Test if targets are contained in tables and indexes
	 *
	 * @param tablesAndIndexes tables and indexes
	 * @param targets          targets
	 * @return true if targets are contained in tables and indexes
	 */
	static private boolean contains(final Collection<String> tablesAndIndexes, final String... targets)
	{
		return tablesAndIndexes.containsAll(Arrays.asList(targets));
	}
}
