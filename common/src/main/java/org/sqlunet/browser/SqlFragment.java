package org.sqlunet.browser;

import android.content.Context;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.sqlunet.provider.BaseProvider;
import org.sqlunet.sql.SqlFormatter;

/**
 * Sql fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SqlFragment extends ListFragment
{
	// static private final String TAG = "SqlFragment";

	@Override
	public void onResume()
	{
		super.onResume();
		update();
	}

	private void update()
	{
		Context context = getActivity();
		if (context == null)
		{
			return;
		}

		CharSequence[] sqls = BaseProvider.buffer.reverseItems();
		for (int i = 0; i < sqls.length; i++)
		{
			sqls[i] = SqlFormatter.styledFormat(sqls[i]);
		}
		final ListAdapter adapter = new ArrayAdapter<>(context, R.layout.item_sql, android.R.id.text1, sqls.length > 0 ? sqls : new CharSequence[]{"empty"});
		setListAdapter(adapter);
	}
}
