/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import org.sqlunet.browser.EntryActivity;
import org.sqlunet.browser.Info;
import org.sqlunet.browser.common.R;
import org.sqlunet.download.FileAsyncTaskChooser;
import org.sqlunet.download.MD5AsyncTaskChooser;
import org.sqlunet.settings.StorageReports;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import static org.sqlunet.download.BaseDownloadFragment.DOWNLOAD_DOWNLOADER_ARG;
import static org.sqlunet.download.BaseDownloadFragment.DOWNLOAD_FROM_ARG;
import static org.sqlunet.download.BaseDownloadFragment.DOWNLOAD_TO_ARG;
import static org.sqlunet.download.BaseDownloadFragment.RENAME_FROM_ARG;
import static org.sqlunet.download.BaseDownloadFragment.RENAME_TO_ARG;
import static org.sqlunet.download.BaseDownloadFragment.UNZIP_TO_ARG;

/**
 * Set up fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupFileFragment extends BaseTaskFragment
{
	//static private final String TAG = "SetupFileF";

	static public final String ARG = "operation";

	/**
	 * Operations
	 */
	public enum Operation
	{
		CREATE, DROP, COPY_URI, UNZIP_URI, MD5_URI, COPY_FILE, UNZIP_FILE, MD5_FILE, DOWNLOAD, DOWNLOAD_ZIPPED, UPDATE;

		/**
		 * Spinner operations
		 */
		static private CharSequence[] operations;

		@Nullable
		static Operation fromIndex(int index)
		{
			final CharSequence operation = Operation.operations[index];
			if (operation.length() == 0)
			{
				return null;
			}
			return Operation.valueOf(operation.toString());
		}
	}

	/**
	 * Constructor
	 */
	public SetupFileFragment()
	{
		setHasOptionsMenu(true);
		this.layoutId = R.layout.fragment_setup_file;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// operations
		Operation.operations = getResources().getTextArray(R.array.setup_files_values);
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		// args (relies on order of resources matching that of DO_)
		Bundle args = getArguments();
		if (args != null)
		{
			final String arg = args.getString(ARG);
			if (arg != null)
			{
				final Operation op = Operation.valueOf(arg);
				this.spinner.setSelection(op.ordinal() + 1);
			}
		}

		this.runButton.setOnClickListener(v -> {
			// skip first
			final long id = SetupFileFragment.this.spinner.getSelectedItemId();
			if (id == 0)
			{
				return;
			}

			// execute
			boolean success;
			final FragmentActivity activity = requireActivity();
			final Operation op = Operation.fromIndex((int) id);
			if (op != null)
			{
				switch (op)
				{
					case CREATE:
						SetupFileFragment.this.status.setText(R.string.status_task_running);
						success = SetupDatabaseTasks.createDatabase(activity, StorageSettings.getDatabasePath(activity));
						SetupFileFragment.this.status.setText(success ? R.string.status_task_done : R.string.status_task_failed);
						org.sqlunet.download.Settings.unrecordDb(activity);
						break;

					case DROP:
						Utils.confirm(activity, R.string.title_setup_drop, R.string.ask_drop, () -> {
							SetupFileFragment.this.status.setText(R.string.status_task_running);
							boolean success1 = SetupDatabaseTasks.deleteDatabase(activity, StorageSettings.getDatabasePath(activity));
							SetupFileFragment.this.status.setText(success1 ? R.string.status_task_done : R.string.status_task_failed);
							org.sqlunet.download.Settings.unrecordDb(activity);
							EntryActivity.rerun(activity);
						});
						break;

					case COPY_URI:
						if (Permissions.check(activity))
						{
							final Intent intent2 = new Intent(activity, OperationActivity.class);
							intent2.putExtra(OperationActivity.ARG_OP, OperationActivity.OP_COPY);
							activity.startActivity(intent2);
						}
						break;

					case UNZIP_URI:
						if (Permissions.check(activity))
						{
							final Intent intent2 = new Intent(activity, OperationActivity.class);
							intent2.putExtra(OperationActivity.ARG_OP, OperationActivity.OP_UNZIP);
							activity.startActivity(intent2);
						}
						break;

					case MD5_URI:
						if (Permissions.check(activity))
						{
							final Intent intent2 = new Intent(activity, OperationActivity.class);
							intent2.putExtra(OperationActivity.ARG_OP, OperationActivity.OP_MD5);
							activity.startActivity(intent2);
						}
						break;

					case COPY_FILE:
						if (Permissions.check(activity))
						{
							Operations.copy(activity);
						}
						break;

					case UNZIP_FILE:
						if (Permissions.check(activity))
						{
							Operations.unzip(activity);
						}
						break;

					case MD5_FILE:
						if (Permissions.check(activity))
						{
							Operations.md5(activity);
						}
						break;

					case DOWNLOAD:
						final Intent intent2 = new Intent(activity, DownloadActivity.class);
						intent2.putExtra(DOWNLOAD_FROM_ARG, StorageSettings.getDbDownloadSource(activity));
						intent2.putExtra(DOWNLOAD_TO_ARG, StorageSettings.getDbDownloadTarget(activity));
						activity.startActivity(intent2);
						break;

					case DOWNLOAD_ZIPPED:
						final Intent intent3 = new Intent(activity, DownloadActivity.class);
						intent3.putExtra(DOWNLOAD_DOWNLOADER_ARG, org.sqlunet.download.Settings.Downloader.DOWNLOAD_SERVICE.toString());
						intent3.putExtra(DOWNLOAD_FROM_ARG, StorageSettings.getDbDownloadZippedSource(activity));
						intent3.putExtra(DOWNLOAD_TO_ARG, StorageSettings.getDbDownloadZippedTarget(activity));
						intent3.putExtra(UNZIP_TO_ARG, StorageSettings.getDataDir(activity));
						intent3.putExtra(RENAME_FROM_ARG, StorageSettings.getDbDownloadFile(activity));
						intent3.putExtra(RENAME_TO_ARG, StorageSettings.getDatabaseName(activity));
						activity.startActivity(intent3);
						break;

					case UPDATE:
						Utils.confirm(activity, R.string.title_setup_update, R.string.askUpdate, () -> SetupDatabaseTasks.update(activity));
						break;
				}
			}
		});

		return view;
	}

	@NonNull
	@Override
	protected SpinnerAdapter makeAdapter()
	{
		// create an ArrayAdapter using the string array and a default spinner layout
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.setup_files_titles, R.layout.spinner_item_task);

		// specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(R.layout.spinner_item_task_dropdown);

		return adapter;
	}

	@Override
	protected void select(final int position)
	{
		CharSequence message = "";
		final Operation op = Operation.fromIndex(position);
		if (op != null)
		{
			switch (op)
			{
				case CREATE:
					message = statusCreate();
					break;

				case DROP:
					message = statusDrop();
					break;

				case COPY_URI:
					message = statusCopy();
					break;

				case UNZIP_URI:
					message = statusUnzip();
					break;

				case MD5_URI:
					message = statusMd5();
					break;

				case DOWNLOAD:
					message = statusDownload();
					break;

				case DOWNLOAD_ZIPPED:
					message = statusDownloadZipped();
					break;

				case UPDATE:
					message = statusUpdate();
					break;
			}
		}

		SetupFileFragment.this.status.setText(message);
	}

	/**
	 * Operation status for create
	 *
	 * @return status string
	 */
	@NonNull
	private CharSequence statusCreate()
	{
		final Context context = requireContext();
		final String database = StorageSettings.getDatabasePath(context);
		final String free = StorageUtils.getFree(context, database);
		final boolean databaseExists = new File(database).exists();

		final SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(getString(R.string.info_op_create_database));
		sb.append("\n\n");
		Info.build(sb, //
				getString(R.string.title_database), database, //
				getString(R.string.title_status), getString(databaseExists ? R.string.status_database_exists : R.string.status_database_not_exists), //
				getString(R.string.title_free), free);
		return sb;
	}

	/**
	 * Operation status for create
	 *
	 * @return status string
	 */
	@NonNull
	private CharSequence statusDrop()
	{
		final Context context = requireContext();
		final String database = StorageSettings.getDatabasePath(context);
		final String free = StorageUtils.getFree(context, database);
		final boolean databaseExists = new File(database).exists();

		final SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(getString(R.string.info_op_drop_database));
		sb.append("\n\n");
		Info.build(sb, //
				getString(R.string.title_database), database, //
				getString(R.string.title_status), getString(databaseExists ? R.string.status_database_exists : R.string.status_database_not_exists), //
				getString(R.string.title_free), free);
		return sb;
	}

	/**
	 * Operation status for copy
	 *
	 * @return status string
	 */
	@NonNull
	private CharSequence statusCopy()
	{
		final Context context = requireContext();
		final String database = StorageSettings.getDatabasePath(context);
		final String free = StorageUtils.getFree(context, database);
		final boolean databaseExists = new File(database).exists();
		/*
		String fromPath = Settings.getCachePref(context);
		boolean sourceExists = false;
		if (fromPath != null)
		{
			fromPath += File.separatorChar + Storage.DBFILE;
			sourceExists = new File(fromPath).exists();
		}
		 */

		final SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(getString(R.string.info_op_copy_database));
		sb.append("\n\n");
		Info.build(sb, //
				getString(R.string.title_database), database, //
				getString(R.string.title_status), getString(databaseExists ? R.string.status_database_exists : R.string.status_database_not_exists), //
				getString(R.string.size_expected), Utils.hrSize(R.integer.size_sqlunet_db, requireContext()), //
				getString(R.string.size_expected) + ' ' + getString(R.string.total), Utils.hrSize(R.integer.size_db_working_total, requireContext()), //
				getString(R.string.title_free), free, //
				"\n", "", //
				getString(R.string.title_from), //
				// fromPath, //
				// getString(R.string.title_status), getString(sourceExists ? R.string.status_source_exists : R.string.status_source_not_exists)
				getString(R.string.title_selection));
		return sb;
	}

	/**
	 * Operation status for unzip
	 *
	 * @return status string
	 */
	@NonNull
	private CharSequence statusUnzip()
	{
		final Context context = requireContext();
		final String database = StorageSettings.getDatabasePath(context);
		final String free = StorageUtils.getFree(context, database);
		final boolean databaseExists = new File(database).exists();
		/*
		String fromPath = Settings.getCachePref(context);
		boolean sourceExists = false;
		if (fromPath != null)
		{
			fromPath += File.separatorChar + Storage.DBFILEZIP;
			sourceExists = new File(fromPath).exists();
		}
		*/

		final SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(getString(R.string.info_op_unzip_database));
		sb.append("\n\n");
		Info.build(sb, //
				getString(R.string.title_database), database, //
				getString(R.string.title_status), getString(databaseExists ? R.string.status_database_exists : R.string.status_database_not_exists), //
				getString(R.string.size_expected), Utils.hrSize(R.integer.size_sqlunet_db, requireContext()), //
				getString(R.string.size_expected) + ' ' + getString(R.string.total), Utils.hrSize(R.integer.size_db_working_total, requireContext()), //
				getString(R.string.title_free), free, //
				"\n", "", //
				getString(R.string.title_from),
				//fromPath, //
				//getString(R.string.title_status), getString(sourceExists ? R.string.status_source_exists : R.string.status_source_not_exists)
				getString(R.string.title_selection));
		return sb;
	}

	/**
	 * Operation status for md5
	 *
	 * @return status string
	 */
	@NonNull
	private CharSequence statusMd5()
	{
		final SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(getString(R.string.info_op_md5));
		return sb;
	}

	/**
	 * Operation status for update database
	 *
	 * @return status string
	 */
	@NonNull
	private CharSequence statusUpdate()
	{
		final SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(getString(R.string.info_op_drop_database));
		sb.append('\n');
		sb.append(statusDownload());
		return sb;
	}

	/**
	 * Operation status for download database
	 *
	 * @return status string
	 */
	@NonNull
	private CharSequence statusDownload()
	{
		final Context context = requireContext();
		final String from = StorageSettings.getDbDownloadSource(context, org.sqlunet.download.Settings.Downloader.isZipDownloaderPref(context));
		final String to = StorageSettings.getDbDownloadTarget(context);
		final String free = StorageUtils.getFree(context, to);
		final boolean targetExists = new File(to).exists();

		final SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(getString(R.string.info_op_download_database));
		sb.append("\n\n");
		Info.build(sb, //
				getString(R.string.title_from), from, //
				"\n", "", //
				getString(R.string.title_to), to, //
				getString(R.string.size_expected), Utils.hrSize(R.integer.size_sqlunet_db, requireContext()), //
				getString(R.string.size_expected) + ' ' + getString(R.string.total), Utils.hrSize(R.integer.size_db_working_total, requireContext()), //
				getString(R.string.title_free), free, //
				getString(R.string.title_status), getString(targetExists ? R.string.status_local_exists : R.string.status_local_not_exists));
		return sb;
	}

	/**
	 * Operation status for download zipped database
	 *
	 * @return status string
	 */
	@NonNull
	private CharSequence statusDownloadZipped()
	{
		final Context context = requireContext();
		final String from = StorageSettings.getDbDownloadZippedSource(context);
		final String to = StorageSettings.getDbDownloadZippedTarget(context);
		final String free = StorageUtils.getFree(context, to);
		final boolean targetExists = new File(to).exists();

		final SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(getString(R.string.info_op_download_zipped_database));
		sb.append("\n\n");
		Info.build(sb, //
				getString(R.string.title_from), from, //
				"\n", "", //
				getString(R.string.title_to), to, //
				getString(R.string.size_expected), Utils.hrSize(R.integer.size_sqlunet_db_zip, requireContext()), //
				getString(R.string.title_free), free, //
				getString(R.string.title_status), getString(targetExists ? R.string.status_local_exists : R.string.status_local_not_exists));
		return sb;
	}

	// M E NU

	@Override
	public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull final MenuInflater inflater)
	{
		inflater.inflate(R.menu.setup_file, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		final Context context = requireContext();

		// handle item selection
		final int itemId = item.getItemId();
		if (itemId == R.id.action_dirs)
		{
			final CharSequence message = StorageReports.reportStyledDirs(context);
			new AlertDialog.Builder(context) //
					.setTitle(R.string.action_dirs) //
					.setMessage(message) //
					.setNegativeButton(R.string.action_dismiss, (dialog, whichButton) -> { /*canceled*/ }) //
					.show();
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
		}
		return false;
	}
}
