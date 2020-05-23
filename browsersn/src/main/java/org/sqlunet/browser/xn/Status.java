/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.xn;

import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Log;

import org.sqlunet.browser.R;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * Database _status
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("WeakerAccess")
public class Status extends org.sqlunet.browser.config.Status
{
	static private final String TAG = "Status";

	// _status flags

	static public final int EXISTS_PREDICATEMATRIX = 0x20;

	static public final int EXISTS_TS_WN = 0x100;

	static public final int EXISTS_TS_VN = 0x1000;

	static public final int EXISTS_TS_PB = 0x2000;

	static public final int EXISTS_TS_FN = 0x4000;

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
			final String[] requiredTextsWn = res.getStringArray(R.array.required_texts_wn);

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
			boolean existsTsWn = contains(existingTablesAndIndexes, requiredTextsWn);

			if (existsTables)
			{
				status |= EXISTS_TABLES;
			}
			if (existsIdx)
			{
				status |= EXISTS_INDEXES;
			}
			if (existsTsWn)
			{
				status |= EXISTS_TS_WN;
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
		if ((status & EXISTS_PREDICATEMATRIX) != 0)
		{
			sb.append(" pm");
		}
		if ((status & EXISTS_TS_WN) != 0)
		{
			sb.append(" tswn");
		}
		return sb;
	}
}