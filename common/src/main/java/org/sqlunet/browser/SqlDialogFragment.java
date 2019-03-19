package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.common.R;

/**
 * Sql dialog fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SqlDialogFragment extends DialogFragment
{
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		getDialog().setTitle(R.string.title_dialog_sql);

		@SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.fragment_sql, container);

		final Fragment sqlFragment = new SqlHistoryFragment();
		getChildFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_sql, sqlFragment) //
				.commit();

		return view;
	}

	static void show(@NonNull final FragmentManager manager)
	{
		final SqlDialogFragment dialogFragment = new SqlDialogFragment();
		dialogFragment.show(manager, "sql");
	}
}
