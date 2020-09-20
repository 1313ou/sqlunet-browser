/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.sqlunet.browser.ColorUtils;
import org.sqlunet.browser.Info;
import org.sqlunet.browser.common.R;
import org.sqlunet.concurrency.ObservedDelegatingTask;
import org.sqlunet.concurrency.Task;
import org.sqlunet.concurrency.TaskObserver;
import org.sqlunet.download.BaseDownloadFragment;
import org.sqlunet.download.DownloadActivity;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Set up with SQL fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupSqlFragment extends Fragment implements Updatable
{
	static private final String TAG = "SetupSqlF";

	// codes

	static private final int REQUEST_DOWNLOAD_CODE = 0xDDDD;

	// download sql button
	private ImageButton downloadSqlZipButton;

	// create button
	private ImageButton createButton;

	// import button
	private ImageButton importButton;

	// index button
	private ImageButton indexesButton;

	// download sql view
	private ImageView downloadSqlZipStatus;

	// create view
	private ImageView createStatus;

	// import view
	private ImageView importStatus;

	// index view
	private ImageView indexesStatus;

	/**
	 * Swipe refresh layout
	 */
	private SwipeRefreshLayout swipeRefreshLayout;

	/**
	 * Constructor
	 */
	public SetupSqlFragment()
	{
		setHasOptionsMenu(true);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_setup_sql, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// statuses
		this.downloadSqlZipStatus = view.findViewById(R.id.status_sqlzip);
		this.createStatus = view.findViewById(R.id.status_created);
		this.importStatus = view.findViewById(R.id.status_import);
		this.indexesStatus = view.findViewById(R.id.status_indexes);

		// buttons
		this.downloadSqlZipButton = view.findViewById(R.id.download_sqlzip);
		this.createButton = view.findViewById(R.id.create_database);
		this.importButton = view.findViewById(R.id.execute_import);
		this.indexesButton = view.findViewById(R.id.execute_indexes);
		final ImageButton infoSqlZipButton = view.findViewById(R.id.info_sqlzip);
		final ImageButton infoCreateButton = view.findViewById(R.id.info_created);
		final ImageButton infoImportButton = view.findViewById(R.id.info_import);
		final ImageButton infoIndexesButton = view.findViewById(R.id.info_indexes);

		// sql zip
		this.downloadSqlZipButton.setOnClickListener(v -> {

			this.downloadSqlZipButton.setEnabled(false);
			this.importButton.setEnabled(false);
			this.indexesButton.setEnabled(false);

			// starting download
			final Context context = requireContext();
			final String from = StorageSettings.getSqlDownloadSource(context);
			final String to = StorageSettings.getSqlDownloadTarget(context);
			final Intent intent = new Intent(context, DownloadActivity.class);
			intent.putExtra(BaseDownloadFragment.DOWNLOAD_FROM_ARG, from);
			intent.putExtra(BaseDownloadFragment.DOWNLOAD_TO_ARG, to);
			startActivityForResult(intent, REQUEST_DOWNLOAD_CODE);
		});
		infoSqlZipButton.setOnClickListener(v -> {

			final Activity activity = requireActivity();
			final String from = StorageSettings.getSqlDownloadSource(activity);
			final String to = StorageSettings.getSqlDownloadTarget(activity);
			final String free = StorageUtils.getFree(activity, to);
			final boolean exists = new File(to).exists();
			Info.info(activity, R.string.title_sqlzip, //
					getString(R.string.title_operation), getString(R.string.info_op_download_sqlzip), //
					getString(R.string.title_from), from, //
					getString(R.string.title_to), to, //
					getString(R.string.size_expected), getString(R.string.hr_size_sqlunet_sql_zip), //
					getString(R.string.title_free), free, //
					getString(R.string.title_status), getString(exists ? R.string.status_local_exists : R.string.status_local_not_exists));
		});

		// created
		this.createButton.setOnClickListener(v -> {

			try
			{
				SetupDatabaseTasks.createDatabase(requireContext(), StorageSettings.getDatabasePath(requireContext()));
			}
			catch (@NonNull final Exception e)
			{
				Log.e(TAG, "While creating", e);
			}
			update();
		});
		infoCreateButton.setOnClickListener(v -> {

			final Activity activity = requireActivity();
			final String database = StorageSettings.getDatabasePath(activity);
			final String free = StorageUtils.getFree(activity, database);
			final boolean databaseExists = new File(database).exists();
			Info.info(activity, R.string.title_created, //
					getString(R.string.title_operation), getString(R.string.info_op_create_database), //
					getString(R.string.title_database), database, //
					getString(R.string.title_free), free, //
					getString(R.string.title_status), getString(databaseExists ? R.string.status_local_exists : R.string.status_local_not_exists));
		});

		// import
		this.importButton.setOnClickListener(v -> {

			final FragmentActivity activity = requireActivity();
			try
			{
				final String database = StorageSettings.getDatabasePath(activity);
				final String source = StorageSettings.getSqlSource(activity);
				final String entry = StorageSettings.getImportEntry(activity);
				final String unit = activity.getString(R.string.unit_statement);
				final TaskObserver.Listener<Integer> listener = new TaskObserver.BaseListener<>();
				final Task<String, Integer, Boolean> st = new ExecAsyncTask(activity, this::update, listener, 1000).fromArchive();
				// final TaskObserver.Listener<Integer> stListener = new TaskObserver.ProgressDialogListener<>(activity, activity.getString(R.string.status_managing), source + '@' + entry, unit);
				final TaskObserver.Listener<Integer> stListener = new TaskObserver.DialogListener<>(activity.getSupportFragmentManager(), activity.getString(R.string.status_managing), source + '@' + entry, unit);
				final ObservedDelegatingTask<String, Integer, Boolean> oft = new ObservedDelegatingTask<>(st, stListener);
				oft.execute(database, source, entry);
			}
			catch (@NonNull final Exception e)
			{
				Log.e(TAG, "While importing", e);
			}
		});
		infoImportButton.setOnClickListener(v -> {

			final Activity activity = requireActivity();
			final String database = StorageSettings.getDatabasePath(activity);
			final String source = StorageSettings.getSqlSource(activity);
			final String entry = StorageSettings.getImportEntry(activity);
			final String free = StorageUtils.getFree(activity, database);
			final boolean dbExists = new File(database).exists();
			final boolean sqlzipExists = new File(source).exists();
			Info.info(activity, R.string.title_import, //
					getString(R.string.title_operation), getString(R.string.info_op_execute_import), //
					getString(R.string.title_database), database, //
					getString(R.string.title_status), getString(dbExists ? R.string.status_database_exists : R.string.status_database_not_exists), //
					getString(R.string.title_free), free, //
					getString(R.string.title_archive), source, //
					getString(R.string.title_entry), entry, //
					getString(R.string.size_expected) + ' ' + getString(R.string.total), getString(R.string.hr_size_db_working_total), //
					getString(R.string.title_status), getString(sqlzipExists ? R.string.status_local_exists : R.string.status_local_not_exists));
		});

		// index button
		this.indexesButton.setOnClickListener(v -> {

			final FragmentActivity activity = requireActivity();

			// starting indexing task
			try
			{
				final String database = StorageSettings.getDatabasePath(activity);
				final String source = StorageSettings.getSqlSource(activity);
				final String entry = StorageSettings.getIndexEntry(activity);
				final String unit = activity.getString(R.string.unit_statement);
				final TaskObserver.Listener<Integer> listener = new TaskObserver.BaseListener<>();
				final Task<String, Integer, Boolean> st = new ExecAsyncTask(activity, this::update, listener, 1).fromArchive(); //database, source, entry);
				// final TaskObserver.Listener<Integer> stListener = new TaskObserver.ProgressDialogListener<>(activity, activity.getString(R.string.status_managing), source + '@' + entry, unit);
				final TaskObserver.Listener<Integer> stListener = new TaskObserver.DialogListener<>(activity.getSupportFragmentManager(), activity.getString(R.string.status_managing), source + '@' + entry, unit);
				final ObservedDelegatingTask<String, Integer, Boolean> oft = new ObservedDelegatingTask<>(st, stListener);
				oft.execute(database, source, entry);
			}
			catch (@NonNull final Exception e)
			{
				Log.e(TAG, "While indexing", e);
			}
		});
		infoIndexesButton.setOnClickListener(v -> {

			final Activity activity = requireActivity();
			final String database = StorageSettings.getDatabasePath(activity);
			final String source = StorageSettings.getSqlSource(activity);
			final String entry = StorageSettings.getIndexEntry(activity);
			final String free = StorageUtils.getFree(activity, database);
			final boolean dbExists = new File(database).exists();
			final boolean sqlzipExists = new File(source).exists();
			Info.info(activity, R.string.title_indexes, //
					getString(R.string.title_operation), getString(R.string.info_op_execute_indexes), //
					getString(R.string.title_database), database, //
					getString(R.string.title_status), getString(dbExists ? R.string.status_database_exists : R.string.status_database_not_exists), //
					getString(R.string.title_free), free, //
					getString(R.string.title_archive), source, //
					getString(R.string.title_entry), entry, //
					getString(R.string.title_status), getString(sqlzipExists ? R.string.status_local_exists : R.string.status_local_not_exists));
		});

		// swipe refresh layout
		this.swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
		this.swipeRefreshLayout.setColorSchemeResources(R.color.swipe_down_1_color, R.color.swipe_down_2_color);
		this.swipeRefreshLayout.setOnRefreshListener(() -> {

			update();

			// stop the refreshing indicator
			this.swipeRefreshLayout.setRefreshing(false);
		});
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

	// U P D A T E

	@Override
	public void update()
	{
		// context
		final Context context = getContext();
		if (context != null)
		{
			// images
			final Drawable okDrawable = ColorUtils.getDrawable(context, R.drawable.ic_ok);
			ColorUtils.tint(ColorUtils.getColor(context, R.color.tertiaryForeColor), okDrawable);
			final Drawable failDrawable = ColorUtils.getDrawable(context, R.drawable.ic_fail);

			// sql zip file
			final String sqlZip = StorageSettings.getSqlSource(context);
			boolean sqlZipExists = new File(sqlZip).exists();
			this.downloadSqlZipButton.setVisibility(sqlZipExists ? View.GONE : View.VISIBLE);
			this.downloadSqlZipStatus.setImageDrawable(sqlZipExists ? okDrawable : failDrawable);

			// status
			final int status = Status.status(context);
			final boolean existsDatabase = (status & Status.EXISTS) != 0;
			final boolean existsTables = (status & Status.EXISTS_TABLES) != 0;
			final boolean existsIndexes = (status & Status.EXISTS_INDEXES) != 0;

			this.createStatus.setImageDrawable(existsDatabase ? okDrawable : failDrawable);
			this.importStatus.setImageDrawable(existsTables ? okDrawable : failDrawable);
			this.indexesStatus.setImageDrawable(existsIndexes ? okDrawable : failDrawable);

			// actions
			this.createButton.setVisibility(sqlZipExists && !existsDatabase ? View.VISIBLE : View.GONE);
			this.importButton.setVisibility(sqlZipExists && existsDatabase && !existsTables ? View.VISIBLE : View.GONE);
			this.indexesButton.setVisibility(sqlZipExists && existsDatabase && existsTables && !existsIndexes ? View.VISIBLE : View.GONE);
		}
	}

	// R E T U R N S

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent returnIntent)
	{
		// handle selection of input by other activity which returns selected input
		if (requestCode == REQUEST_DOWNLOAD_CODE)
		{
			boolean success = resultCode == Activity.RESULT_OK;
			Log.d(TAG, "Download " + (success ? "succeeded" : "failed")); ////
			Toast.makeText(requireContext(), success ? R.string.title_download_complete : R.string.title_download_failed, Toast.LENGTH_SHORT).show();
			update();
		}
		super.onActivityResult(requestCode, resultCode, returnIntent);
	}

	// M E N U

	@Override
	public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull final MenuInflater inflater)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		inflater.inflate(R.menu.setup_sql, menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
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
}
