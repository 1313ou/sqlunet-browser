package org.sqlunet.browser.config;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sqlunet.browser.R;
import org.sqlunet.settings.Storage;
import org.sqlunet.settings.StorageReports;

/**
 * Storage activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StorageFragment extends Fragment
{
	/**
	 * Swipe refresh layout
	 */
	private SwipeRefreshLayout swipeRefreshLayout;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);

		// inflate
		final View view = inflater.inflate(R.layout.fragment_storage, container, false);

		// swipe refresh layout
		this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
		this.swipeRefreshLayout.setColorSchemeResources(R.color.swipedown1_color, R.color.swipedown2_color);
		this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				update();

				// stop the refreshing indicator
				StorageFragment.this.swipeRefreshLayout.setRefreshing(false);
			}
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

		// db
		assert view != null;
		final TextView db = (TextView) view.findViewById(R.id.database);
		db.setText(Storage.getSqlUNetStorage(getActivity()).getAbsolutePath());

		// storage
		final TextView storage = (TextView) view.findViewById(R.id.storage);
		storage.setText(StorageReports.reportStorageDirectories(getActivity()));

		// storage devices
		final TextView storageDevices = (TextView) view.findViewById(R.id.storage_devices);
		storageDevices.setText(StorageReports.reportExternalStorage(getActivity()));
	}

	// M E N U

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		inflater.inflate(R.menu.storage, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		final Context context = getActivity();

		// handle item selection
		switch (item.getItemId())
		{
			case R.id.action_storage_dirs:
				final CharSequence message = StorageReports.reportDirs(getActivity());
				final AlertDialog.Builder alert = new AlertDialog.Builder(context);
				alert.setTitle(R.string.action_storage_dirs);
				alert.setMessage(message);
				alert.setNegativeButton(R.string.action_dismiss, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int whichButton)
					{
						// canceled.
					}
				});
				alert.show();
				break;

			case R.id.action_refresh:
				/// make sure that the SwipeRefreshLayout is displaying its refreshing indicator
				if (!this.swipeRefreshLayout.isRefreshing())
				{
					this.swipeRefreshLayout.setRefreshing(true);
				}
				update();

				// stop the refreshing indicator
				this.swipeRefreshLayout.setRefreshing(false);
				break;

			default:
				return false;
		}
		return true;
	}
}
