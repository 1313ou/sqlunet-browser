package org.sqlunet.browser.vn;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Log;

import java.util.List;

/**
 * Database _status
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class Status extends org.sqlunet.browser.config.Status
{
	static private final String TAG = "Status";

	// _status flags

	static public final int EXISTS_TS_VN = 0x1000;

	static public final int EXISTS_TS_PB = 0x2000;

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
			final String[] requiredTables = res.getStringArray(R.array.required_tables);
			final String[] requiredIndexes = res.getStringArray(R.array.required_indexes);
			final String[] requiredTextsVn = res.getStringArray(R.array.required_texts_vn);
			final String[] requiredTextsPb = res.getStringArray(R.array.required_texts_pb);

			boolean existsTables = contains(existingTablesAndIndexes, requiredTables);
			boolean existsIdx = contains(existingTablesAndIndexes, requiredIndexes);
			boolean existsTsVn = contains(existingTablesAndIndexes, requiredTextsVn);
			boolean existsTsPb = contains(existingTablesAndIndexes, requiredTextsPb);

			if (existsTables)
			{
				status |= EXISTS_TABLES;
			}
			if (existsIdx)
			{
				status |= EXISTS_INDEXES;
			}
			if (existsTsVn)
			{
				status |= EXISTS_TS_VN;
			}
			if (existsTsPb)
			{
				status |= EXISTS_TS_PB;
			}
			return status;
		}
		return 0;
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
		if ((status & EXISTS_TS_VN) != 0)
		{
			sb.append(" tsvn");
		}
		if ((status & EXISTS_TS_PB) != 0)
		{
			sb.append(" tspb");
		}
		return sb;
	}
}
