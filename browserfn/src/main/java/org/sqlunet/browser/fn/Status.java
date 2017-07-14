package org.sqlunet.browser.fn;

import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Log;

import java.util.List;

/**
 * Database _status
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Status extends org.sqlunet.browser.config.Status
{
	static private final String TAG = "Status";

	// _status flags

	static public final int EXISTS_TS_FN = 0x4000;

	static public final int DO_TS_FN = 7;

	/**
	 * Get _status
	 *
	 * @param context context
	 * @return _status
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
				Log.e(TAG, "While getting _status", e);
				return status;
			}

			final Resources res = context.getResources();
			final String[] requiredTables = res.getStringArray(org.sqlunet.browser.R.array.required_tables);
			final String[] requiredIndexes = res.getStringArray(org.sqlunet.browser.R.array.required_indexes);
			final String[] requiredTextsFn = res.getStringArray(org.sqlunet.browser.fn.R.array.required_texts_fn);

			boolean existsTables = contains(existingTablesAndIndexes, requiredTables);
			boolean existsIdx = contains(existingTablesAndIndexes, requiredIndexes);
			boolean existsTsFn = contains(existingTablesAndIndexes, requiredTextsFn);

			if (existsTables)
			{
				status |= EXISTS_TABLES;
			}
			if (existsIdx)
			{
				status |= EXISTS_INDEXES;
			}
			if (existsTsFn)
			{
				status |= EXISTS_TS_FN;
			}
			return status;
		}
		return 0;
	}

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
		if ((status & EXISTS_TS_FN) != 0)
		{
			sb.append(" tsfn");
		}
		return sb;
	}
}
