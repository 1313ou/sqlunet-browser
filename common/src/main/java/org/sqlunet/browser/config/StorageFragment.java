/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sqlunet.browser.common.R;
import org.sqlunet.settings.Storage;
import org.sqlunet.settings.StorageReports;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Storage fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StorageFragment extends Fragment
{
	/**
	 * Swipe refresh layout
	 */
	private SwipeRefreshLayout swipeRefreshLayout;

	/**
	 * Constructor
	 */
	public StorageFragment()
	{
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// inflate
		final View view = inflater.inflate(R.layout.fragment_storage, container, false);

		// swipe refresh layout
		this.swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
		//this.swipeRefreshLayout.setColorSchemeResources(R.color.swipe_down_1_color, R.color.swipe_down_2_color);
		this.swipeRefreshLayout.setOnRefreshListener(() -> {
			update();

			// stop the refreshing indicator
			StorageFragment.this.swipeRefreshLayout.setRefreshing(false);
		});
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		final AppCompatActivity activity = (AppCompatActivity) requireActivity();
		final ActionBar actionBar = activity.getSupportActionBar();
		assert actionBar != null;
		actionBar.setCustomView(null);
		actionBar.setBackgroundDrawable(null);

		update();
	}

	/**
	 * Update status
	 */
	private void update()
	{
		// view
		final View view = getView();
		assert view != null;

		// context
		final Context context = requireContext();

		// db
		final TextView db = view.findViewById(R.id.database);
		db.setText(Storage.getSqlUNetStorage(context).getAbsolutePath());

		// storage
		final TextView storage = view.findViewById(R.id.storage);
		storage.setText(StorageReports.reportStyledStorageDirectories(context));

		// storage devices
		final TextView storageDevices = view.findViewById(R.id.storage_devices);
		storageDevices.setText(StorageReports.reportStyledExternalStorage(context));
	}

	// M E N U

	@Override
	public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull final MenuInflater inflater)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		inflater.inflate(R.menu.storage, menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		final Context context = requireContext();

		// handle item selection
		final int itemId = item.getItemId();
		if (itemId == R.id.action_storage_dirs)
		{
			final CharSequence message = StorageReports.reportStyledDirs(context);
			new AlertDialog.Builder(context) //
					.setTitle(R.string.action_storage_dirs) //
					.setMessage(message) //
					.setNegativeButton(R.string.action_dismiss, (dialog, whichButton) -> { /*canceled*/ }) //
					.show();
		}
		else if (itemId == R.id.action_refresh)
		{
			// make sure that the SwipeRefreshLayout is displaying its refreshing indicator
			if (!this.swipeRefreshLayout.isRefreshing())
			{
				this.swipeRefreshLayout.setRefreshing(true);
			}
			update();

			// stop the refreshing indicator
			this.swipeRefreshLayout.setRefreshing(false);
		}
		else
		{
			return false;
		}
		return true;
	}
}
