/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.common.R;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.sql.SqlFormatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

/**
 * Sql fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SqlFragment extends Fragment
{
	public SqlFragment()
	{
	}

	@Override
	public void onResume()
	{
		super.onResume();

		// app bar
		final AppCompatActivity activity = (AppCompatActivity) requireActivity();
		final ActionBar actionBar = activity.getSupportActionBar();
		if (actionBar != null)
		{
			actionBar.hide();
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();

		// app bar
		final AppCompatActivity activity = (AppCompatActivity) requireActivity();
		final ActionBar actionBar = activity.getSupportActionBar();
		if (actionBar != null)
		{
			actionBar.show();
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		final View view = inflater.inflate(R.layout.fragment_sql, container, false);

		if (savedInstanceState == null)
		{
			// splash fragment
			final Fragment fragment = new SqlStatementsFragment();
			getChildFragmentManager() //
					.beginTransaction() //
					.setReorderingAllowed(true) //
					.replace(R.id.container_sql, fragment, "fragment_sql") //
					//.addToBackStack("fragment_sql") //
					.commit();
		}

		return view;
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// toolbar bar
		final Toolbar toolbar = view.findViewById(R.id.toolbar_fragment);
		assert toolbar != null;
		toolbar.setTitle("SQL");
		toolbar.addMenuProvider(new MenuProvider()
		{
			@Override
			public void onCreateMenu(@NonNull final Menu menu, @NonNull final MenuInflater menuInflater)
			{
				// inflate
				menu.clear();
				menuInflater.inflate(R.menu.main, menu);
				menuInflater.inflate(R.menu.sql, menu);
			}

			@Override
			public boolean onMenuItemSelected(@NonNull final MenuItem menuItem)
			{
				boolean handled = onOptionsItemSelected(menuItem);
				if (handled)
				{
					return true;
				}
				return MenuHandler.menuDispatch((AppCompatActivity) requireActivity(), menuItem);
			}

		}, this.getViewLifecycleOwner(), Lifecycle.State.RESUMED);

		// nav
		toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
	}

	// M E N U

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		if (item.getItemId() == R.id.action_copy)
		{
			final StringBuilder sb = new StringBuilder();
			final CharSequence[] sqls = BaseProvider.sqlBuffer.reverseItems();
			for (CharSequence sql : sqls)
			{
				sb.append(SqlFormatter.styledFormat(sql));
				sb.append(";\n");
			}
			final ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
			final ClipData clip = ClipData.newPlainText("SQL", sb);
			assert clipboard != null;
			clipboard.setPrimaryClip(clip);
		}
		return false;
	}
}
