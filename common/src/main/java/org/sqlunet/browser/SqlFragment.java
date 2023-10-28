/*
 * Copyright (c) 2023. Bernard Bou
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Sql fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SqlFragment extends Fragment
{
	static public final String FRAGMENT_TAG = "sql";

	public SqlFragment()
	{
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		final View view = inflater.inflate(R.layout.fragment_sql, container, false);

		// swipe refresh layout
		final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
		swipeRefreshLayout.setOnRefreshListener(() -> {

			if (!isAdded())
			{
				return;
			}
			Fragment fragment = getChildFragmentManager().findFragmentByTag(FRAGMENT_TAG);
			if (fragment instanceof SqlStatementsFragment)
			{
				SqlStatementsFragment sqlStatementsFragment = (SqlStatementsFragment) fragment;
				sqlStatementsFragment.update();
			}
			// stop the refreshing indicator
			swipeRefreshLayout.setRefreshing(false);
		});

		// sub fragment
		if (savedInstanceState == null)
		{
			// splash fragment
			final Fragment fragment = new SqlStatementsFragment();
			assert isAdded();
			getChildFragmentManager() //
					.beginTransaction() //
					.setReorderingAllowed(true) //
					.replace(R.id.container_sql, fragment, FRAGMENT_TAG) //
					.addToBackStack(FRAGMENT_TAG) //
					.commit();
		}

		return view;
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// menu
		final MenuHost menuHost = requireActivity();
		menuHost.addMenuProvider(new MenuProvider()
		{
			@Override
			public void onCreateMenu(@NonNull final Menu menu, @NonNull final MenuInflater menuInflater)
			{
				// inflate
				menu.clear();
				menuInflater.inflate(R.menu.main, menu);
				menuInflater.inflate(R.menu.sql, menu);
				MenuCompat.setGroupDividerEnabled(menu, true);
			}

			@Override
			public boolean onMenuItemSelected(@NonNull final MenuItem menuItem)
			{
				if (menuItem.getItemId() == R.id.action_copy)
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
					return true;
				}
				return MenuHandler.menuDispatch((AppCompatActivity) requireActivity(), menuItem);
			}

		}, this.getViewLifecycleOwner(), Lifecycle.State.RESUMED);
	}
}
