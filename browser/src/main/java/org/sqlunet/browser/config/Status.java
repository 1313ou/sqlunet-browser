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
	static private final String TAG = "Status";
	// status flags

	static private final int EXISTS = 0x1;

	static public final int EXISTS_INDEXES = 0x10;

	static public final int EXISTS_PM = 0x20;

	static public final int EXISTS_TS_WN = 0x100;

	static public final int EXISTS_TS_VN = 0x1000;

	static public final int EXISTS_TS_PB = 0x2000;

	static public final int EXISTS_TS_FN = 0x4000;

	static public final int DO_INDEXES = 1;

	static public final int DO_PM = 2;

	static public final int DO_TS_WN = 3;

	static public final int DO_TS_VN = 4;

	static public final int DO_TS_PB = 5;

	static public final int DO_TS_FN = 6;

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
				Log.e(TAG, "While getting status", e);
				return status;
			}

			boolean existsIdx = contains(existingTablesAndIndexes, "index_words_lemma", "index_senses_wordid", "index_senses_synsetid", "index_synsets_synsetid", //
					"index_casedwords_wordid_casedwordid", //
					"index_semlinks_synset1id", "index_semlinks_linkid", //
					"index_samples_synsetid", //
					"index_vnwords_wordid", "index_vnrolemaps_classid", "index_vnframemaps_classid", //
					"index_pbwords_wordid", "index_pbrolesets_pbwordid", "index_pbroles_rolesetid", "index_pbexamples_rolesetid", "index_pbrels_exampleid", "index_pbargs_exampleid", //
					"index_fnwords_wordid", "index_fnframes_related_frameid", "index_fnframes_related_frame2id", "index_fnlexemes_fnwordid", "index_fnfes_frameid", //
					"index_fnferealizations_luid", "index_fnvalenceunits_ferid", "index_fnfegrouprealizations_luid", "index_fnpatterns_valenceunits_patternid", "index_fnpatterns_fegrid", //
					"index_fnsubcorpuses_luid", "index_fnannosets_sentenceid", "index_fnlayers_annosetid", "index_fnlabels_layerid", //
					"index_pm_wordid", "index_pm_synsetid");
			boolean existsPm = contains(existingTablesAndIndexes, "pmvn", "pmpb", "pmfn");
			boolean existsTsWn = contains(existingTablesAndIndexes, "words_lemma_fts4", "synsets_definition_fts4", "samples_sample_fts4");
			boolean existsTsVn = contains(existingTablesAndIndexes, "vnexamples_example_fts4");
			boolean existsTsPb = contains(existingTablesAndIndexes, "pbexamples_text_fts4");
			boolean existsTsFn = contains(existingTablesAndIndexes, "fnsentences_text_fts4");
			if (existsIdx)
			{
				status |= EXISTS_INDEXES;
			}
			if (existsPm)
			{
				status |= EXISTS_PM;
			}
			if (existsTsWn)
			{
				status |= EXISTS_TS_WN;
			}
			if (existsTsVn)
			{
				status |= EXISTS_TS_VN;
			}
			if (existsTsPb)
			{
				status |= EXISTS_TS_PB;
			}
			if (existsTsFn)
			{
				status |= EXISTS_TS_FN;
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
		return (status & (EXISTS | EXISTS_INDEXES | EXISTS_PM)) == (EXISTS | EXISTS_INDEXES | EXISTS_PM);
	}

	/**
	 * Test existence of database
	 *
	 * @param context context
	 * @return true if database exists
	 */
	static private boolean existsDatabase(final Context context)
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
	static private List<String> tablesAndIndexes(final Context context)
	{
		final String order = "CASE " //
				+ "WHEN " + ManagerContract.TablesAndIndices.TYPE + " = 'table' THEN '1' " //
				+ "WHEN " + ManagerContract.TablesAndIndices.TYPE + " = 'view' THEN '2' " //
				+ "WHEN " + ManagerContract.TablesAndIndices.TYPE + " = 'index' THEN '3' " //
				+ "ELSE " + ManagerContract.TablesAndIndices.TYPE + " END ASC," //
				+ ManagerContract.TablesAndIndices.NAME + " ASC";
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
