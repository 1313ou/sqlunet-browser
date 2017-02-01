package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.sqlunet.browser.Info;
import org.sqlunet.browser.R;
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
	private AsyncTask<?, Integer, Boolean> task;

	// download sql button
	private ImageButton downloadSqlZipButton;

	// import button
	private ImageButton importButton;

	// pm button
	private ImageButton predicateMatrixButton;

	// index button
	private ImageButton indexesButton;

	// download sql view
	private ImageView downloadSqlZipStatus;

	// import view
	private ImageView importStatus;

	// pm view
	private ImageView pmStatus;

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
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);

		// view
		final View view = inflater.inflate(R.layout.fragment_setup_sql, container, false);

		// activity
		final Activity activity = getActivity();

		// statuses
		this.downloadSqlZipStatus = (ImageView) view.findViewById(R.id.status_sqlzip);
		this.importStatus = (ImageView) view.findViewById(R.id.status_import);
		this.indexesStatus = (ImageView) view.findViewById(R.id.status_indexes);
		this.pmStatus = (ImageView) view.findViewById(R.id.status_pm);

		// buttons
		this.downloadSqlZipButton = (ImageButton) view.findViewById(R.id.download_sqlzip);
		this.importButton = (ImageButton) view.findViewById(R.id.execute_import);
		this.indexesButton = (ImageButton) view.findViewById(R.id.execute_indexes);
		this.predicateMatrixButton = (ImageButton) view.findViewById(R.id.execute_predicatematrix);
		ImageButton infoSqlZipButton = (ImageButton) view.findViewById(R.id.info_sqlzip);
		ImageButton infoImportButton = (ImageButton) view.findViewById(R.id.info_import);
		ImageButton infoIndexesButton = (ImageButton) view.findViewById(R.id.info_indexes);
		ImageButton infoPmButton = (ImageButton) view.findViewById(R.id.info_pm);

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
				Info.info(activity, R.string.title_sqlzip, getString(R.string.title_from), from, getString(R.string.title_to), to, getString(R.string.title_free), free);
			}
		});

		// import
		this.importButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				try
				{
					final String database = StorageSettings.getDatabasePath(activity);
					final String source = StorageSettings.getSqlSource(activity);
					final String entry = StorageSettings.getImportEntry(activity);
					final TaskObserver.Listener listener = new TaskObserver.DialogListener(activity, R.string.status_managing, source + '@' + entry);
					SetupSqlFragment.this.task = new ExecAsyncTask(listener, 1000).executeFromArchive(database, source, entry);
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
				final String database = StorageSettings.getDatabasePath(activity);
				final String source = StorageSettings.getSqlSource(activity);
				final String entry = StorageSettings.getImportEntry(activity);
				final String free = StorageUtils.getFree(getActivity(), database);
				Info.info(activity, R.string.title_import, getString(R.string.title_database), database, getString(R.string.title_archive), source, getString(R.string.title_entry), entry, getString(R.string.title_free), free);
			}
		});

		// index button
		this.indexesButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				// starting indexing task
				try
				{
					final String database = StorageSettings.getDatabasePath(activity);
					final String source = StorageSettings.getSqlSource(activity);
					final String entry = StorageSettings.getIndexEntry(activity);
					final TaskObserver.Listener listener = new TaskObserver.DialogListener(activity, R.string.status_managing, source + '@' + entry);
					SetupSqlFragment.this.task = new ExecAsyncTask(listener, 1).executeFromArchive(database, source, entry);
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
				final String database = StorageSettings.getDatabasePath(activity);
				final String source = StorageSettings.getSqlSource(activity);
				final String entry = StorageSettings.getIndexEntry(activity);
				final String free = StorageUtils.getFree(getActivity(), database);
				Info.info(activity, R.string.title_indexes, getString(R.string.title_database), database, getString(R.string.title_archive), source, getString(R.string.title_entry), entry, getString(R.string.title_free), free);
			}
		});

		// pm button
		this.predicateMatrixButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				// starting pm task
				try
				{
					final String database = StorageSettings.getDatabasePath(activity);
					final String source = StorageSettings.getSqlSource(activity);
					final String entry = StorageSettings.getPmEntry(activity);
					final TaskObserver.Listener listener = new TaskObserver.DialogListener(activity, R.string.status_managing, source + '@' + entry);
					SetupSqlFragment.this.task = new ExecAsyncTask(listener, 1).executeFromArchive(database, source, entry);
				}
				catch (final Exception e)
				{
					Log.e(TAG, "While preparing predicatematrix", e);
				}
			}
		});
		infoPmButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				final String database = StorageSettings.getDatabasePath(activity);
				final String source = StorageSettings.getSqlSource(activity);
				final String entry = StorageSettings.getPmEntry(activity);
				final String free = StorageUtils.getFree(getActivity(), database);
				Info.info(activity, R.string.title_predicatematrix, getString(R.string.title_database), database, getString(R.string.title_archive), source, getString(R.string.title_entry), entry, getString(R.string.title_free), free);
			}
		});

		// swipe refresh layout
		this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
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

	/**
	 * Update status
	 */
	private void update()
	{
		// activity
		final Activity activity = getActivity();

		// sql zip file
		final String sqlZip = StorageSettings.getSqlSource(activity);
		boolean sqlZipExists = new File(sqlZip).exists();
		this.downloadSqlZipButton.setVisibility(sqlZipExists ? View.GONE : View.VISIBLE);
		this.downloadSqlZipStatus.setImageResource(sqlZipExists ? R.drawable.ic_ok : R.drawable.ic_fail);

		// status
		final int status = Status.status(activity);
		final boolean existsData = (status & Status.EXISTS) != 0;
		final boolean existsIndexes = (status & Status.EXISTS_INDEXES) != 0;
		final boolean existsPm = (status & Status.EXISTS_PREDICATEMATRIX) != 0;
		this.importStatus.setImageResource(existsData ? R.drawable.ic_ok : R.drawable.ic_fail);
		this.indexesStatus.setImageResource(existsIndexes ? R.drawable.ic_ok : R.drawable.ic_fail);
		this.pmStatus.setImageResource(existsPm ? R.drawable.ic_ok : R.drawable.ic_fail);

		// actions
		this.importButton.setVisibility(sqlZipExists && !existsData ? View.VISIBLE : View.GONE);
		this.indexesButton.setVisibility(sqlZipExists && !existsIndexes ? View.VISIBLE : View.GONE);
		this.predicateMatrixButton.setVisibility(sqlZipExists && !existsPm ? View.VISIBLE : View.GONE);
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
	public boolean onOptionsItemSelected(MenuItem item)
	{
		final Context context = getActivity();

		// handle item selection
		switch (item.getItemId())
		{
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
