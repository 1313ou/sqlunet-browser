package org.sqlunet.browser.xn;

import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Log;

import org.sqlunet.browser.R;

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
	static public int status(final Context context)
	{
		if (existsDatabase(context))
		{
			int status = EXISTS;

			final Resources res = context.getResources();
			final String[] requiredTables = res.getStringArray(R.array.required_tables);
			final String[] requiredIndexes = res.getStringArray(R.array.required_indexes);
			final String[] requiredTextsPm = res.getStringArray(R.array.required_pm);
			final String[] requiredTextsWn = res.getStringArray(R.array.required_texts_wn);
			final String[] requiredTextsVn = res.getStringArray(R.array.required_texts_vn);
			final String[] requiredTextsPb = res.getStringArray(R.array.required_texts_pb);
			final String[] requiredTextsFn = res.getStringArray(R.array.required_texts_fn);

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
			boolean existsPm = contains(existingTablesAndIndexes, requiredTextsPm);
			boolean existsTsWn = contains(existingTablesAndIndexes, requiredTextsWn);
			boolean existsTsVn = contains(existingTablesAndIndexes, requiredTextsVn);
			boolean existsTsPb = contains(existingTablesAndIndexes, requiredTextsPb);
			boolean existsTsFn = contains(existingTablesAndIndexes, requiredTextsFn);

			if (existsTables)
			{
				status |= EXISTS_TABLES;
			}
			if (existsIdx)
			{
				status |= EXISTS_INDEXES;
			}
			if (existsPm)
			{
				status |= EXISTS_PREDICATEMATRIX;
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
	 * Can run _status
	 *
	 * @param context context
	 * @return true if app is ready to run
	 */
	static public boolean canRun(final Context context)
	{
		final int status = status(context);
		return (status & (EXISTS | EXISTS_TABLES | EXISTS_INDEXES | EXISTS_PREDICATEMATRIX)) == (EXISTS | EXISTS_TABLES | EXISTS_INDEXES | EXISTS_PREDICATEMATRIX);
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
		if ((status & EXISTS_PREDICATEMATRIX) != 0)
		{
			sb.append(" pm");
		}
		if ((status & EXISTS_TS_WN) != 0)
		{
			sb.append(" tswn");
		}
		if ((status & EXISTS_TS_VN) != 0)
		{
			sb.append(" tsvn");
		}
		if ((status & EXISTS_TS_PB) != 0)
		{
			sb.append(" tspb");
		}
		if ((status & EXISTS_TS_FN) != 0)
		{
			sb.append(" tsfn");
		}
		return sb;
	}
}