/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.sqlunet.browser.common.R;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.sql.SqlFormatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

/**
 * Sql fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("WeakerAccess")
public class SqlStatementsFragment extends ListFragment
{
	// static private final String TAG = "SqlStatementsF";

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		final ListView listView = this.getListView();
		listView.setOnItemLongClickListener((av, v, pos, id) -> {

			final CharSequence statement = (CharSequence) av.getAdapter().getItem(pos);
			final ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
			final ClipData clipData = ClipData.newPlainText("text", statement);
			assert clipboard != null;
			clipboard.setPrimaryClip(clipData);
			Toast.makeText(requireContext(), R.string.status_clipboard_copied, Toast.LENGTH_SHORT).show();
			return true;
		});
	}

	@Override
	public void onResume()
	{
		super.onResume();
		update();
	}

	void update()
	{
		final Context context = getContext();
		if (context != null)
		{
			CharSequence[] sqls = BaseProvider.sqlBuffer.reverseItems();
			for (int i = 0; i < sqls.length; i++)
			{
				sqls[i] = SqlFormatter.styledFormat(sqls[i]);
			}
			final ListAdapter adapter = new ArrayAdapter<>(context, R.layout.item_sql, android.R.id.text1, sqls.length > 0 ? sqls : new CharSequence[]{"empty"});
			setListAdapter(adapter);
		}
	}
}
