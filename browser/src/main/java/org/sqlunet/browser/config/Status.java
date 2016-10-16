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
import java.util.List;

/**
 * Database status
 *
 * @author Bernard Bou
 */
public class Status
{
	static private final String TAG = "SqlUNet Status"; //$NON-NLS-1$

	private static final int EXISTS = 0x1;

	static public final int EXISTS_IDX = 0x10;

	static public final int EXISTS_PM = 0x20;

	static public final int EXISTS_TS = 0x100;

	static public final int EXISTS_TSFN = 0x200;

	static public int status(final Context context)
	{
		if (existsDatabase(context))
		{
			int status = EXISTS;

			List<String> existingTablesAndIndexes;
			try
			{
				existingTablesAndIndexes = tablesAndIndexes(context);
			} catch (Exception e)
			{
				Log.e(TAG, "While getting status", e); //$NON-NLS-1$
				return status;
			}

			boolean existsIdx = contains(existingTablesAndIndexes, "index_words_lemma", "index_senses_wordid", "index_senses_synsetid", "index_synsets_synsetid", "index_casedwords_wordid_casedwordid", "index_semlinks_synset1id", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
					"index_semlinks_linkid", //$NON-NLS-1$
					"index_samples_synsetid", "index_vnwords_wordid", "index_vnrolemaps_classid", "index_vnframemaps_classid", "index_pbwords_wordid", "index_pbrolesets_pbwordid", "index_pbroles_rolesetid", "index_pbexamples_rolesetid", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
					"index_fnwords_wordid", "index_fnlexemes_fnwordid", "index_fnfes_frameid", "index_fnlayers_annosetid", "index_fnlabels_layerid", "index_pm_wordid", "index_pm_synsetid"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
			boolean existsPm = contains(existingTablesAndIndexes, "pmvn", "pmpb", "pmfn"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			boolean existsTs = contains(existingTablesAndIndexes, "words_lemma_fts4", "synsets_definition_fts4", "samples_sample_fts4"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			boolean existsTsFn = contains(existingTablesAndIndexes, "fnsentences_text_fts4"); //$NON-NLS-1$

			if (existsIdx)
				status |= EXISTS_IDX;
			if (existsPm)
				status |= EXISTS_PM;
			if (existsTs)
				status |= EXISTS_TS;
			if (existsTsFn)
				status |= EXISTS_TSFN;
			return status;
		}
		return 0;
	}

	static public boolean canRun(final Context context)
	{
		final int status = status(context);
		return (status & (EXISTS | EXISTS_IDX | EXISTS_PM)) == (EXISTS | EXISTS_IDX | EXISTS_PM);
	}

	private static boolean existsDatabase(final Context context)
	{
		final String databasePath = StorageSettings.getDatabasePath(context);
		final File db = new File(databasePath);
		return db.exists() && db.isFile() && db.canWrite();
	}

	private static List<String> tablesAndIndexes(final Context context)
	{
		final String order = "CASE " // //$NON-NLS-1$
				+ "WHEN " + ManagerContract.TablesAndIndices.TYPE + " = 'table' THEN '1' " // //$NON-NLS-1$ //$NON-NLS-2$
				+ "WHEN " + ManagerContract.TablesAndIndices.TYPE + " = 'view' THEN '2' " // //$NON-NLS-1$ //$NON-NLS-2$
				+ "WHEN " + ManagerContract.TablesAndIndices.TYPE + " = 'index' THEN '3' " // //$NON-NLS-1$ //$NON-NLS-2$
				+ "ELSE " + ManagerContract.TablesAndIndices.TYPE + " END ASC," // //$NON-NLS-1$ //$NON-NLS-2$
				+ ManagerContract.TablesAndIndices.NAME + " ASC"; //$NON-NLS-1$

		final Cursor cursor = context.getContentResolver().query( //
				Uri.parse(TablesAndIndices.CONTENT_URI), //
				new String[]{TablesAndIndices.TYPE, TablesAndIndices.NAME}, // projection
				"name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android_%'", // selection criteria //$NON-NLS-1$
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
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	static private boolean contains(final List<String> tablesAndIndexes, final String... targets)
	{
		return tablesAndIndexes.containsAll(Arrays.asList(targets));
	}
}
