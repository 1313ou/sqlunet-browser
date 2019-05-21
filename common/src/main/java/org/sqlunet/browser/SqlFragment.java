/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.sqlunet.browser.common.R;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.sql.SqlFormatter;

import androidx.fragment.app.ListFragment;

/**
 * Sql fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SqlFragment extends ListFragment
{
	// static private final String TAG = "SqlF";

	@Override
	public void onResume()
	{
		super.onResume();
		update();
	}

	private void update()
	{
		final Context context = getContext();
		if (context != null)
		{
			CharSequence[] sqls = BaseProvider.buffer.reverseItems();
			for (int i = 0; i < sqls.length; i++)
			{
				sqls[i] = SqlFormatter.styledFormat(sqls[i]);
			}
			final ListAdapter adapter = new ArrayAdapter<>(context, R.layout.item_sql, android.R.id.text1, sqls.length > 0 ? sqls : new CharSequence[]{"empty"});
			setListAdapter(adapter);
		}
	}
}
