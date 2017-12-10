package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

import java.io.File;

/**
 * Set up with SQL fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupSqlFragment extends Fragment
{
	static private final String TAG = "SetupSqlFragment";

	// codes

	static private final int REQUEST_DOWNLOAD_CODE = 0xDDDD;

	// task
	@SuppressWarnings("unused")
	protected AsyncTask<?, Integer, Boolean> task;

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
		// Required empty public constructor
	}

	@SuppressWarnings("boxing")
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);

		// view
		final View view = inflater.inflate(R.layout.fragment_setup_sql, container, false);

		// activity
		final Activity activity = getActivity();

		// statuses
		this.downloadSqlZipStatus = view.findViewById(R.id.status_sqlzip);
		this.createStatus = view.findViewById(R.id.status_created);
		this.importStatus = view.findViewById(R.id.status_import);
		this.indexesStatus = view.findViewById(R.id.status_indexes);

		// buttons
		this.createButton = view.findViewById(R.id.create_database);
		this.downloadSqlZipButton = view.findViewById(R.id.download_sqlzip);
		this.importButton = view.findViewById(R.id.execute_import);
		this.indexesButton = view.findViewById(R.id.execute_indexes);
		ImageButton infoSqlZipButton = view.findViewById(R.id.info_sqlzip);
		ImageButton infoCreateButton = view.findViewById(R.id.info_created);
		ImageButton infoImportButton = view.findViewById(R.id.info_import);
		ImageButton infoIndexesButton = view.findViewById(R.id.info_indexes);

		// sql zip
		this.downloadSqlZipButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				SetupSqlFragment.this.downloadSqlZipButton.setEnabled(false);
				SetupSqlFragment.this.importButton.setEnabled(false);
				SetupSqlFragment.this.indexesButton.setEnabled(false);

				// starting download
				final String from = StorageSettings.getSqlDownloadSource(activity);
				final String to = StorageSettings.getSqlDownloadTarget(activity);
				final Intent intent = new Intent(activity, DownloadActivity.class);
				intent.putExtra(BaseDownloadFragment.DOWNLOAD_FROM_ARG, from);
				intent.putExtra(BaseDownloadFragment.DOWNLOAD_TO_ARG, to);
				startActivityForResult(intent, REQUEST_DOWNLOAD_CODE);

			}
		});
		infoSqlZipButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				final String from = StorageSettings.getSqlDownloadSource(activity);
				final String to = StorageSettings.getSqlDownloadTarget(activity);
				final String free = StorageUtils.getFree(getActivity(), to);
				final boolean exists = new File(to).exists();
				Info.info(activity, R.string.title_sqlzip, //
						getString(R.string.title_operation), getString(R.string.info_op_download_sqlzip), //
						getString(R.string.title_from), from, //
						getString(R.string.title_to), to, //
						getString(R.string.size_expected), getString(R.string.hr_size_sqlunet_sql_zip), //
						getString(R.string.title_free), free, //
						getString(R.string.title_status), getString(exists ? R.string.status_local_exists : R.string.status_local_not_exists));
			}
		});

		// created
		this.createButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				try
				{
					SetupDatabaseTasks.createDatabase(activity, StorageSettings.getDatabasePath(activity));
				}
				catch (final Exception e)
				{
					Log.e(TAG, "While creating", e);
				}
				update();
			}
		});
		infoCreateButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				final String database = StorageSettings.getDatabasePath(activity);
				final String free = StorageUtils.getFree(getActivity(), database);
				final boolean databaseExists = new File(database).exists();
				Info.info(activity, R.string.title_created, //
						getString(R.string.title_operation), getString(R.string.info_op_create_database), //
						getString(R.string.title_database), database, //
						getString(R.string.title_free), free, //
						getString(R.string.title_status), getString(databaseExists ? R.string.status_local_exists : R.string.status_local_not_exists));
			}
		});

		// import
		this.importButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				assert activity != null;
				try
				{
					final String database = StorageSettings.getDatabasePath(activity);
					final String source = StorageSettings.getSqlSource(activity);
					final String entry = StorageSettings.getImportEntry(activity);
					final String unit = activity.getString(R.string.unit_statement);
					final TaskObserver.Listener listener = new TaskObserver.DialogListener(activity, R.string.status_managing, source + '@' + entry, unit);
					SetupSqlFragment.this.task = new ExecAsyncTask(getActivity(), listener, 1000).executeFromArchive(database, source, entry);
				}
				catch (final Exception e)
				{
					Log.e(TAG, "While importing", e);
				}
			}
		});
		infoImportButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				assert activity != null;
				final String database = StorageSettings.getDatabasePath(activity);
				final String source = StorageSettings.getSqlSource(activity);
				final String entry = StorageSettings.getImportEntry(activity);
				final String free = StorageUtils.getFree(getActivity(), database);
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
			}
		});

		// index button
		this.indexesButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				assert activity != null;
				// starting indexing task
				try
				{
					final String database = StorageSettings.getDatabasePath(activity);
					final String source = StorageSettings.getSqlSource(activity);
					final String entry = StorageSettings.getIndexEntry(activity);
					final String unit = activity.getString(R.string.unit_statement);
					final TaskObserver.Listener listener = new TaskObserver.DialogListener(activity, R.string.status_managing, source + '@' + entry, unit);
					SetupSqlFragment.this.task = new ExecAsyncTask(getActivity(), listener, 1).executeFromArchive(database, source, entry);
				}
				catch (final Exception e)
				{
					Log.e(TAG, "While indexing", e);
				}
			}
		});
		infoIndexesButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				assert activity != null;
				final String database = StorageSettings.getDatabasePath(activity);
				final String source = StorageSettings.getSqlSource(activity);
				final String entry = StorageSettings.getIndexEntry(activity);
				final String free = StorageUtils.getFree(getActivity(), database);
				final boolean dbExists = new File(database).exists();
				final boolean sqlzipExists = new File(source).exists();
				Info.info(activity, R.string.title_indexes, //
						getString(R.string.title_operation), getString(R.string.info_op_execute_import), //
						getString(R.string.title_database), database, //
						getString(R.string.title_status), getString(dbExists ? R.string.status_database_exists : R.string.status_database_not_exists), //
						getString(R.string.title_free), free, //
						getString(R.string.title_archive), source, //
						getString(R.string.title_entry), entry, //
						getString(R.string.title_status), getString(sqlzipExists ? R.string.status_local_exists : R.string.status_local_not_exists));
			}
		});

		// swipe refresh layout
		this.swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
		this.swipeRefreshLayout.setColorSchemeResources(R.color.swipe_down_1_color, R.color.swipe_down_2_color);
		this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				update();

				// stop the refreshing indicator
				SetupSqlFragment.this.swipeRefreshLayout.setRefreshing(false);
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

	/**
	 * Update status
	 */
	protected void update()
	{
		// activity
		final Activity activity = getActivity();

		// images
		final Drawable okDrawable = ColorUtils.getDrawable(activity, R.drawable.ic_ok);
		ColorUtils.tint(ColorUtils.getColor(activity, R.color.secondaryForeColor), okDrawable);
		final Drawable failDrawable = ColorUtils.getDrawable(activity, R.drawable.ic_fail);

		// sql zip file
		final String sqlZip = StorageSettings.getSqlSource(activity);
		boolean sqlZipExists = new File(sqlZip).exists();
		this.downloadSqlZipButton.setVisibility(sqlZipExists ? View.GONE : View.VISIBLE);
		this.downloadSqlZipStatus.setImageDrawable(sqlZipExists ? okDrawable : failDrawable);

		// status
		final int status = Status.status(activity);
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

	// R E T U R N S

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent returnIntent)
	{
		// handle selection of input by other activity which returns selected input
		switch (requestCode)
		{
			case REQUEST_DOWNLOAD_CODE:
				boolean success = resultCode == Activity.RESULT_OK;
				Log.d(TAG, "Download " + (success ? "succeeded" : "failed")); ////
				Toast.makeText(getActivity(), success ? R.string.title_download_complete : R.string.title_download_failed, Toast.LENGTH_SHORT).show();
				update();
				break;
			default:
				break;
		}
		super.onActivityResult(requestCode, resultCode, returnIntent);
	}

	// M E N U

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		inflater.inflate(R.menu.setup_sql, menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		// handle item selection
		int i = item.getItemId();
		if (i == R.id.action_refresh)
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
