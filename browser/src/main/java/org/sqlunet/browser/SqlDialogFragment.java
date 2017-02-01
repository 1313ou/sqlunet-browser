package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Sql dialog fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SqlDialogFragment extends DialogFragment
{
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		getDialog().setTitle(R.string.title_dialog_sql);

		@SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.fragment_sql, null);

		final Fragment sqlFragment = new SqlHistoryFragment();
		getChildFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_sql, sqlFragment) //
				.commit();

		return view;
	}

	static void show(final FragmentManager manager)
	{
		final SqlDialogFragment dialogFragment = new SqlDialogFragment();
		dialogFragment.show(manager, "sql");
	}
}
