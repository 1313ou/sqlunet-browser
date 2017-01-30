package org.sqlunet.browser;

import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
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
	@Override
	public void onResume()
	{
		super.onResume();

		final CharSequence[] sqls = BaseProvider.buffer.reverseItems();
		for (int i = 0; i < sqls.length; i++)
		{
			sqls[i] = SqlFormatter.styledFormat(sqls[i]);
		}

		final ListAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_activated_1, android.R.id.text1, sqls);
		setListAdapter(adapter);

		setActionBar();
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		super.onHiddenChanged(hidden);
		if (!hidden)
		{
			setActionBar();
		}
	}

	private void setActionBar()
	{
		final AppCompatActivity activity = (AppCompatActivity) getActivity();
		NavigableFragment.restoreActionBar(activity, R.string.title_sql_section);
	}
}
