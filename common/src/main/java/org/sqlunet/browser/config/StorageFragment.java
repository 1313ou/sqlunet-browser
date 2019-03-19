package org.sqlunet.browser.config;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sqlunet.browser.NavigableFragment;
import org.sqlunet.browser.common.R;
import org.sqlunet.settings.Storage;
import org.sqlunet.settings.StorageReports;

/**
 * Storage activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StorageFragment extends NavigableFragment
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
		this.titleId = R.string.title_storage_section;
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);

		// inflate
		final View view = inflater.inflate(R.layout.fragment_storage, container, false);

		// swipe refresh layout
		this.swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
		this.swipeRefreshLayout.setColorSchemeResources(R.color.swipe_down_1_color, R.color.swipe_down_2_color);
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
		final Context context = getActivity();
		assert context != null;

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
	public void onCreateOptionsMenu(final Menu menu, @NonNull final MenuInflater inflater)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		inflater.inflate(R.menu.storage, menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		final Context context = getActivity();
		assert context != null;

		// handle item selection
		int i = item.getItemId();
		if (i == R.id.action_storage_dirs)
		{
			final CharSequence message = StorageReports.reportStyledDirs(context);
			final AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle(R.string.action_storage_dirs);
			alert.setMessage(message);
			alert.setNegativeButton(R.string.action_dismiss, (dialog, whichButton) -> {
				// canceled.
			});
			alert.show();
		}
		else if (i == R.id.action_refresh)
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
