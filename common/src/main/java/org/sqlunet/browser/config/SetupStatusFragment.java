/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.sqlunet.browser.ColorUtils;
import org.sqlunet.browser.EntryActivity;
import org.sqlunet.browser.Info;
import org.sqlunet.browser.common.R;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

import java.io.File;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static org.sqlunet.download.BaseDownloadFragment.DOWNLOAD_FROM_ARG;
import static org.sqlunet.download.BaseDownloadFragment.DOWNLOAD_TO_ARG;

/**
 * Base Status fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupStatusFragment extends Fragment implements Updatable
{
	static private final String TAG = "SetupStatusF";

	// components

	private ImageView imageDb;

	private ImageView imageIndexes;

	protected ImageButton buttonDb;

	protected ImageButton buttonIndexes;

	protected ImageButton infoDatabaseButton;

	/**
	 * Activity result launcher
	 */
	protected ActivityResultLauncher<Intent> activityResultLauncher;

	/**
	 * Swipe refresh layout
	 */
	private SwipeRefreshLayout swipeRefreshLayout;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public SetupStatusFragment()
	{
	}

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
			boolean success = result.getResultCode() == Activity.RESULT_OK;
			Log.d(TAG, "Download " + (success ? "succeeded" : "failed"));
			update();
			if (success)
			{
				final Context context2 = requireContext();
				Toast.makeText(context2, R.string.title_download_complete, Toast.LENGTH_SHORT).show();
				EntryActivity.rerun(context2);
			}
		});
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(R.layout.fragment_status, container, false);

		// images
		this.imageDb = view.findViewById(R.id.status_database);
		this.imageIndexes = view.findViewById(R.id.status_indexes);

		// buttons
		this.buttonDb = view.findViewById(R.id.databaseButton);
		this.buttonIndexes = view.findViewById(R.id.indexesButton);
		this.infoDatabaseButton = view.findViewById(R.id.info_database);

		// swipe refresh layout
		this.swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
		// this.swipeRefreshLayout.setColorSchemeResources(R.color.swipe_down_1_color, R.color.swipe_down_2_color);
		this.swipeRefreshLayout.setOnRefreshListener(() -> {
			update();

			// stop the refreshing indicator
			SetupStatusFragment.this.swipeRefreshLayout.setRefreshing(false);
		});
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		update();
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		super.onHiddenChanged(hidden);

		// If we are becoming visible
		if (!hidden)
		{
			update();
		}
	}

	protected void download()
	{
		final Context context = requireContext();
		final Intent intent = new Intent(context, DownloadActivity.class);
		intent.putExtra(DOWNLOAD_FROM_ARG, StorageSettings.getDbDownloadSource(context, org.sqlunet.download.Settings.Downloader.isZipDownloaderPref(context)));
		intent.putExtra(DOWNLOAD_TO_ARG, StorageSettings.getDbDownloadTarget(context));
		this.activityResultLauncher.launch(intent);
	}

	protected void index()
	{
		int index = getResources().getInteger(R.integer.sql_statement_do_indexes_position);
		final Intent intent = new Intent(requireContext(), SetupDatabaseActivity.class);
		intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index);
		startActivity(intent);
	}

	protected void info()
	{
		final Activity activity = requireActivity();
		final String database = StorageSettings.getDatabasePath(activity);
		final String free = StorageUtils.getFree(activity, database);
		final String source = StorageSettings.getDbDownloadSource(activity, org.sqlunet.download.Settings.Downloader.isZipDownloaderPref(activity));
		final int status = Status.status(activity);
		final boolean existsDb = (status & Status.EXISTS) != 0;
		final boolean existsTables = (status & Status.EXISTS_TABLES) != 0;
		if (existsDb)
		{
			final long size = new File(database).length();
			final String hrSize = StorageUtils.countToStorageString(size) + " (" + size + ')';
			Info.info(activity, R.string.title_status, //
					getString(R.string.title_database), database, //
					getString(R.string.title_status), getString(R.string.status_database_exists), //
					getString(R.string.title_status), getString(existsTables ? R.string.status_data_exists : R.string.status_data_not_exists), //
					getString(R.string.title_free), free, //
					getString(R.string.size_expected), Utils.hrSize(R.integer.size_sqlunet_db, requireContext()), //
					getString(R.string.size_expected) + ' ' + getString(R.string.text_search), Utils.hrSize(R.integer.size_searchtext, requireContext()), //
					getString(R.string.size_expected) + ' ' + getString(R.string.total), Utils.hrSize(R.integer.size_db_working_total, requireContext()), //
					getString(R.string.size_current), hrSize);
		}
		else
		{
			Info.info(activity, R.string.title_dialog_info_download, //
					getString(R.string.title_operation), getString(R.string.info_op_download_database), //
					getString(R.string.title_from), source, //
					getString(R.string.title_database), database, //
					getString(R.string.title_free), free, //
					getString(R.string.size_expected) + ' ' + getString(R.string.text_search), Utils.hrSize(R.integer.size_searchtext, requireContext()), //
					getString(R.string.size_expected) + ' ' + getString(R.string.total), Utils.hrSize(R.integer.size_db_working_total, requireContext()), //
					getString(R.string.title_status), getString(R.string.status_database_not_exists));
		}
	}

	// U P D A T E

	@Override
	public void update()
	{
		final Context context = getContext();
		if (context != null)
		{
			final int status = Status.status(context);
			Log.d(TAG, "STATUS " + Status.toString(status));

			// images
			final Drawable okDrawable = ColorUtils.getDrawable(context, R.drawable.ic_ok);
			final Drawable failDrawable = ColorUtils.getDrawable(context, R.drawable.ic_fail);

			final boolean existsDb = (status & Status.EXISTS) != 0;
			final boolean existsTables = (status & Status.EXISTS_TABLES) != 0;
			if (existsDb && existsTables)
			{
				this.imageDb.setImageDrawable(okDrawable);
				ImageViewCompat.setImageTintMode(this.imageDb, PorterDuff.Mode.SRC_IN);
				this.buttonDb.setVisibility(View.GONE);

				final boolean existsIndexes = (status & Status.EXISTS_INDEXES) != 0;

				this.imageIndexes.setImageDrawable(existsIndexes ? okDrawable : failDrawable);
				ImageViewCompat.setImageTintMode(this.imageIndexes, existsIndexes ? PorterDuff.Mode.SRC_IN : PorterDuff.Mode.DST);
				this.buttonIndexes.setVisibility(existsIndexes ? View.GONE : View.VISIBLE);
			}
			else
			{
				this.imageDb.setImageDrawable(failDrawable);
				ImageViewCompat.setImageTintMode(this.imageDb, PorterDuff.Mode.DST);
				this.buttonDb.setVisibility(View.VISIBLE);

				this.buttonIndexes.setVisibility(View.GONE);
				this.imageIndexes.setImageResource(R.drawable.ic_unknown);
			}
		}
	}

	// M E N U

	/*
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		// handle item selection
		final int itemId = item.getItemId();
		if (itemId == R.id.action_refresh)
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
	*/
}
