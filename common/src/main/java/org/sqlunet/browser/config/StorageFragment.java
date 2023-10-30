/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.config;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sqlunet.browser.MenuHandler;
import org.sqlunet.browser.common.R;
import org.sqlunet.settings.Storage;
import org.sqlunet.settings.StorageReports;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
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
	}

	@Override
	public void onResume()
	{
		super.onResume();
		update();
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
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
				menuInflater.inflate(R.menu.storage, menu);
				// MenuCompat.setGroupDividerEnabled(menu, true);
			}

			@Override
			public boolean onMenuItemSelected(@NonNull final MenuItem menuItem)
			{
				final Context context = requireContext();

				// handle item selection
				final int itemId = menuItem.getItemId();
				if (itemId == R.id.action_dirs)
				{
					final CharSequence message = StorageReports.reportStyledDirs(context);
					new AlertDialog.Builder(context) //
							.setTitle(R.string.action_dirs) //
							.setMessage(message) //
							.setNegativeButton(R.string.action_dismiss, (dialog, whichButton) -> { /*canceled*/ }) //
							.show();
					return true;
				}
				else if (itemId == R.id.action_storage_dirs)
				{
					final Pair<CharSequence[], CharSequence[]> dirs = StorageReports.getStyledStorageDirectoriesNamesValues(context);
					final CharSequence message = StorageReports.namesValuesToReportStyled(dirs);
					new AlertDialog.Builder(context) //
							.setTitle(R.string.action_storage_dirs) //
							.setMessage(message) //
							.setNegativeButton(R.string.action_dismiss, (dialog, whichButton) -> { /*canceled*/ }) //
							.show();
					return true;
				}
				else if (itemId == R.id.action_cache_dirs)
				{
					final Pair<CharSequence[], CharSequence[]> dirs = StorageReports.getStyledCachesNamesValues(context);
					final CharSequence message = StorageReports.namesValuesToReportStyled(dirs);
					new AlertDialog.Builder(context) //
							.setTitle(R.string.action_cache_dirs) //
							.setMessage(message) //
							.setNegativeButton(R.string.action_dismiss, (dialog, whichButton) -> { /*canceled*/ }) //
							.show();
					return true;
				}
				else if (itemId == R.id.action_download_dirs)
				{
					final Pair<CharSequence[], CharSequence[]> dirs = StorageReports.getStyledDownloadNamesValues(context);
					final CharSequence message = StorageReports.namesValuesToReportStyled(dirs);
					new AlertDialog.Builder(context) //
							.setTitle(R.string.action_download_dirs) //
							.setMessage(message) //
							.setNegativeButton(R.string.action_dismiss, (dialog, whichButton) -> { /*canceled*/ }) //
							.show();
					return true;
				}
				else if (itemId == R.id.action_copy)
				{
					final StringBuilder sb = new StringBuilder();

					// view
					final View view = getView();
					assert view != null;

					// db
					sb.append(getString(R.string.title_database));
					sb.append('\n');
					sb.append(Storage.getSqlUNetStorage(context).getAbsolutePath());
					sb.append('\n');
					sb.append('\n');

					// storage
					sb.append(getString(R.string.title_storage));
					sb.append('\n');
					sb.append(StorageReports.reportStorageDirectories(context));
					//sb.append('\n');

					// storage devices
					sb.append(getString(R.string.title_external_storage_devices));
					sb.append('\n');
					sb.append(StorageReports.reportExternalStorage(context));
					//sb.append('\n');

					final ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
					final ClipData clip = ClipData.newPlainText("Storage", sb);
					assert clipboard != null;
					clipboard.setPrimaryClip(clip);
					return true;
				}
				else if (itemId == R.id.action_refresh)
				{
					// make sure that the SwipeRefreshLayout is displaying its refreshing indicator
					if (!swipeRefreshLayout.isRefreshing())
					{
						swipeRefreshLayout.setRefreshing(true);
					}
					update();

					// stop the refreshing indicator
					swipeRefreshLayout.setRefreshing(false);
					return true;
				}
				return MenuHandler.menuDispatch((AppCompatActivity) requireActivity(), menuItem);
			}

		}, this.getViewLifecycleOwner(), Lifecycle.State.RESUMED);
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
}
