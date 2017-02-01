package org.sqlunet.browser;

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
	static private final String TAG = "SqlFragment";

	@Override
	public void onResume()
	{
		super.onResume();

		CharSequence[] sqls = BaseProvider.buffer.reverseItems();
		for (int i = 0; i < sqls.length; i++)
		{
			sqls[i] = SqlFormatter.styledFormat(sqls[i]);
		}
		final ListAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_activated_1, android.R.id.text1, sqls.length > 0 ? sqls : new CharSequence[]{"empty"});
		setListAdapter(adapter);
	}
}
