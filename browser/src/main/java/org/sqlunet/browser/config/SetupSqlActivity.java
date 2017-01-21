package org.sqlunet.browser.config;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.sqlunet.browser.R;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;
import org.sqlunet.style.Report;

import java.io.File;

/**
 * Set up with SQL activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupSqlActivity extends Activity
{
	static private final String TAG = "SetupSqlActivity";

	static private final int DOWNLOAD_CODE = 555;

	// task
	protected AsyncTask<?, Integer, Boolean> task;

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
	private ImageView predicatematrixStatus;

	// index view
	private ImageView indexesStatus;

	@SuppressWarnings("boxing")
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_setup_sql);

		// show the Up button in the type bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);

		// statuses
		this.downloadSqlZipStatus = (ImageView) findViewById(R.id.status_sqlzip);
		this.importStatus = (ImageView) findViewById(R.id.status_import);
		this.indexesStatus = (ImageView) findViewById(R.id.status_indexes);
		this.predicatematrixStatus = (ImageView) findViewById(R.id.status_pm);

		// buttons
		this.downloadSqlZipButton = (ImageButton) findViewById(R.id.download_sqlzip);
		this.importButton = (ImageButton) findViewById(R.id.execute_import);
		this.indexesButton = (ImageButton) findViewById(R.id.execute_indexes);
		this.predicateMatrixButton = (ImageButton) findViewById(R.id.execute_predicatematrix);
		ImageButton infoSqlZipButton = (ImageButton) findViewById(R.id.info_sqlzip);
		ImageButton infoImportButton = (ImageButton) findViewById(R.id.info_import);
		ImageButton infoIndexesButton = (ImageButton) findViewById(R.id.info_indexes);
		ImageButton infoPmButton = (ImageButton) findViewById(R.id.info_pm);

		// sql zip
		this.downloadSqlZipButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				SetupSqlActivity.this.downloadSqlZipButton.setEnabled(false);
				SetupSqlActivity.this.importButton.setEnabled(false);
				SetupSqlActivity.this.indexesButton.setEnabled(false);

				// starting download
				final String from = StorageSettings.getSqlDownloadSource(SetupSqlActivity.this);
				final String to = StorageSettings.getSqlDownloadTarget(SetupSqlActivity.this);
				final Intent intent = new Intent(SetupSqlActivity.this, DownloadActivity.class);
				intent.putExtra(BaseDownloadFragment.DOWNLOAD_FROM_ARG, from);
				intent.putExtra(BaseDownloadFragment.DOWNLOAD_TO_ARG, to);
				startActivityForResult(intent, DOWNLOAD_CODE);

			}
		});
		infoSqlZipButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				final String from = StorageSettings.getSqlDownloadSource(SetupSqlActivity.this);
				final String to = StorageSettings.getSqlDownloadTarget(SetupSqlActivity.this);
				final String free = getFree(to);
				info(R.string.title_sqlzip, getString(R.string.title_from), from, getString(R.string.title_to), to, getString(R.string.title_free), free);
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
					final String database = StorageSettings.getDatabasePath(SetupSqlActivity.this);
					final String source = StorageSettings.getSqlSource(SetupSqlActivity.this);
					final String entry = StorageSettings.getImportEntry(SetupSqlActivity.this);
					final TaskObserver.Listener listener = new TaskObserver.DialogListener(SetupSqlActivity.this, R.string.status_managing, source + '@' + entry);
					SetupSqlActivity.this.task = new ExecAsyncTask(listener, 1000).executeFromArchive(database, source, entry);
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
				final String database = StorageSettings.getDatabasePath(SetupSqlActivity.this);
				final String source = StorageSettings.getSqlSource(SetupSqlActivity.this);
				final String entry = StorageSettings.getImportEntry(SetupSqlActivity.this);
				final String free = getFree(database);
				info(R.string.title_import, getString(R.string.title_database), database, getString(R.string.title_archive), source, getString(R.string.title_entry), entry, getString(R.string.title_free), free);
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
					final String database = StorageSettings.getDatabasePath(SetupSqlActivity.this);
					final String source = StorageSettings.getSqlSource(SetupSqlActivity.this);
					final String entry = StorageSettings.getIndexEntry(SetupSqlActivity.this);
					final TaskObserver.Listener listener = new TaskObserver.DialogListener(SetupSqlActivity.this, R.string.status_managing, source + '@' + entry);
					SetupSqlActivity.this.task = new ExecAsyncTask(listener, 1).executeFromArchive(database, source, entry);
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
				final String database = StorageSettings.getDatabasePath(SetupSqlActivity.this);
				final String source = StorageSettings.getSqlSource(SetupSqlActivity.this);
				final String entry = StorageSettings.getIndexEntry(SetupSqlActivity.this);
				final String free = getFree(database);
				info(R.string.title_indexes, getString(R.string.title_database), database, getString(R.string.title_archive), source, getString(R.string.title_entry), entry, getString(R.string.title_free), free);
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
					final String database = StorageSettings.getDatabasePath(SetupSqlActivity.this);
					final String source = StorageSettings.getSqlSource(SetupSqlActivity.this);
					final String entry = StorageSettings.getPmEntry(SetupSqlActivity.this);
					final TaskObserver.Listener listener = new TaskObserver.DialogListener(SetupSqlActivity.this, R.string.status_managing, source + '@' + entry);
					SetupSqlActivity.this.task = new ExecAsyncTask(listener, 1).executeFromArchive(database, source, entry);
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
				final String database = StorageSettings.getDatabasePath(SetupSqlActivity.this);
				final String source = StorageSettings.getSqlSource(SetupSqlActivity.this);
				final String entry = StorageSettings.getPmEntry(SetupSqlActivity.this);
				final String free = getFree(database);
				info(R.string.title_predicatematrix, getString(R.string.title_database), database, getString(R.string.title_archive), source, getString(R.string.title_entry), entry, getString(R.string.title_free), free);
			}
		});

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		updateStatus();
	}

	void updateStatus()
	{
		// sql zip
		final String sqlZip = StorageSettings.getSqlSource(this);
		boolean sqlZipExists = new File(sqlZip).exists();
		this.downloadSqlZipButton.setVisibility(sqlZipExists ? View.INVISIBLE : View.VISIBLE);
		this.downloadSqlZipStatus.setImageResource(sqlZipExists ? R.drawable.ic_ok : R.drawable.ic_fail);

		this.importButton.setVisibility(sqlZipExists ? View.VISIBLE : View.INVISIBLE);
		this.predicateMatrixButton.setVisibility(sqlZipExists ? View.VISIBLE : View.INVISIBLE);
		this.indexesButton.setVisibility(sqlZipExists ? View.VISIBLE : View.INVISIBLE);

		final int status = Status.status(this);
		final boolean existsData = (status & Status.EXISTS) != 0;
		final boolean existsIndexes = (status & Status.EXISTS_INDEXES) != 0;
		final boolean existsPredicateMatrix = (status & Status.EXISTS_PREDICATEMATRIX) != 0;
		this.importStatus.setImageResource(existsData ? R.drawable.ic_ok : R.drawable.ic_fail);
		this.indexesStatus.setImageResource(existsIndexes ? R.drawable.ic_ok : R.drawable.ic_fail);
		this.predicatematrixStatus.setImageResource(existsPredicateMatrix ? R.drawable.ic_ok : R.drawable.ic_fail);
	}

	private String getFree(final String target)
	{
		final File file = new File(target);
		final String dir = file.isDirectory() ? file.getAbsolutePath() : file.getParent();
		final float[] dataStats = StorageUtils.storageStats(dir);
		final float df = dataStats[StorageUtils.STORAGE_FREE];
		final float dc = dataStats[StorageUtils.STORAGE_CAPACITY];
		final float dp = dataStats[StorageUtils.STORAGE_OCCUPANCY];
		return getString(R.string.format_storage_data, dir, StorageUtils.mbToString(df), StorageUtils.mbToString(dc), dp);
	}

	// I N F O

	private void info(final int messageId, final CharSequence... lines)
	{
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.action_info);
		alert.setMessage(messageId);
		alert.setNegativeButton(R.string.action_dismiss, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				// canceled.
			}
		});
		/*
		final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
		final ListView extra = new ListView(this);
		input.setAdapter(adapter);
		*/
		final SpannableStringBuilder sb = new SpannableStringBuilder();
		int i = 0;
		for (CharSequence line : lines)
		{
			if ((i++ % 2) == 0)
			{
				Report.appendHeader(sb, line);
			}
			else
			{
				sb.append(line);
			}
			sb.append('\n');
		}

		final TextView extra = new TextView(this);
		extra.setPadding(20, 0, 20, 0);
		extra.setText(sb);
		alert.setView(extra);
		alert.show();
	}

	protected void onDownloadFinish(final boolean result)
	{
		this.importButton.setVisibility(result ? View.VISIBLE : View.GONE);
		this.predicateMatrixButton.setVisibility(result ? View.VISIBLE : View.GONE);
		this.indexesButton.setVisibility(result ? View.VISIBLE : View.GONE);

		Log.d(TAG, "Download " + (result ? "succeeded" : "failed")); ////
		Toast.makeText(this, result ? R.string.title_download_complete : R.string.title_download_failed, Toast.LENGTH_SHORT).show();

		// delete file if failed
		if (!result)
		{
			String to = StorageSettings.getSqlDownloadTarget(this);
			//noinspection ResultOfMethodCallIgnored
			new File(to).delete();
		}
	}
}
