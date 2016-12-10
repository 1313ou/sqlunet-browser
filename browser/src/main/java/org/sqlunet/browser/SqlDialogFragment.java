package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

		final Fragment sqlFragment = new SqlFragment();
		getChildFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_sql, sqlFragment) //
				.commit();

		return view;
	}

	static void show(final Activity activity)
	{
		final SqlDialogFragment dialogFragment = new SqlDialogFragment();
		dialogFragment.show(activity.getFragmentManager(), "sql");
	}
}
