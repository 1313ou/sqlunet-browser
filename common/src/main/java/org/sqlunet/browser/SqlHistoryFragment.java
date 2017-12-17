package org.sqlunet.browser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.util.Log;

import org.sqlunet.browser.common.R;

/**
 * Sql fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SqlHistoryFragment extends SqlFragment implements ActionBarSetter
{
	static private final String TAG = "SqlHistoryFragment";

	@Override
	public boolean setActionBar(@NonNull final ActionBar actionBar, final Context context)
	{
		Log.d(SqlHistoryFragment.TAG, "restore action bar");
		actionBar.setTitle(R.string.title_sql_section);
		actionBar.setSubtitle(R.string.app_subname);
		return false;
	}
}
