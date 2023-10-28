/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.common.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Sql dialog fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SqlDialogFragment extends DialogFragment
{
	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		final Dialog dialog = getDialog();
		assert dialog != null;
		dialog.setTitle(R.string.title_dialog_sql);

		@SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.fragment_sql_statements, container);

		final Fragment sqlFragment = new SqlStatementsFragment();
		assert isAdded();
		getChildFragmentManager() //
				.beginTransaction() //
				.setReorderingAllowed(true) //
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
