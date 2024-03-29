/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import org.sqlunet.browser.EntryActivity;
import org.sqlunet.browser.Info;
import org.sqlunet.browser.common.R;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

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
		CREATE, DROP, COPY_URI, UNZIP_URI, UNZIP_ENTRY_URI, MD5_URI, COPY_FILE, UNZIP_FILE, MD5_FILE, DOWNLOAD, DOWNLOAD_ZIPPED, UPDATE;

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
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

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
						com.bbou.download.Settings.unrecordDatapack(activity);
						break;

					case DROP:
						Utils.confirm(activity, R.string.title_setup_drop, R.string.ask_drop, () -> {
							SetupFileFragment.this.status.setText(R.string.status_task_running);
							boolean success1 = SetupDatabaseTasks.deleteDatabase(activity, StorageSettings.getDatabasePath(activity));
							SetupFileFragment.this.status.setText(success1 ? R.string.status_task_done : R.string.status_task_failed);
							com.bbou.download.Settings.unrecordDatapack(activity);
							EntryActivity.rerun(activity);
						});
						break;

					case COPY_URI:
						if (Permissions.check(activity))
						{
							final Intent intent2 = new Intent(activity, OperationActivity.class);
							intent2.putExtra(OperationActivity.ARG_OP, OperationActivity.OP_COPY);
							intent2.putExtra(OperationActivity.ARG_TYPES, new String[]{"application/vnd.sqlite3"});
							activity.startActivity(intent2);
						}
						break;

					case UNZIP_URI:
						if (Permissions.check(activity))
						{
							final Intent intent2 = new Intent(activity, OperationActivity.class);
							intent2.putExtra(OperationActivity.ARG_OP, OperationActivity.OP_UNZIP);
							intent2.putExtra(OperationActivity.ARG_TYPES, new String[]{"application/zip"});
							activity.startActivity(intent2);
						}
						break;

					case UNZIP_ENTRY_URI:
						if (Permissions.check(activity))
						{
							final Intent intent2 = new Intent(activity, OperationActivity.class);
							intent2.putExtra(OperationActivity.ARG_OP, OperationActivity.OP_UNZIP_ENTRY);
							intent2.putExtra(OperationActivity.ARG_TYPES, new String[]{"application/zip"});
							intent2.putExtra(OperationActivity.ARG_ZIP_ENTRY, Settings.getZipEntry(requireContext(), StorageSettings.getDbDownloadName(requireContext())));
							activity.startActivity(intent2);
						}
						break;

					case MD5_URI:
						if (Permissions.check(activity))
						{
							final Intent intent2 = new Intent(activity, OperationActivity.class);
							intent2.putExtra(OperationActivity.ARG_OP, OperationActivity.OP_MD5);
							intent2.putExtra(OperationActivity.ARG_TYPES, new String[]{"*/*"});
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
						final Intent intent2 = DownloadIntentFactory.makeIntent(activity);
						activity.startActivity(intent2);
						break;

					case DOWNLOAD_ZIPPED:
						final Intent intent3 = DownloadIntentFactory.makeIntentDownloadThenDeploy(activity);
						activity.startActivity(intent3);
						break;

					case UPDATE:
						Utils.confirm(activity, R.string.title_setup_update, R.string.askUpdate, () -> SetupDatabaseTasks.update(activity));
						break;
				}
			}
		});
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
		SpannableStringBuilder message = null;
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
					message.append(requireContext().getString(R.string.from_uri));
					break;
				case COPY_FILE:
					message = statusCopy();
					message.append(requireContext().getString(R.string.from_file));
					break;

				case UNZIP_URI:
				case UNZIP_ENTRY_URI:
					message = statusUnzip();
					message.append(requireContext().getString(R.string.from_uri));
					break;
				case UNZIP_FILE:
					message = statusUnzip();
					message.append(requireContext().getString(R.string.from_file));
					break;

				case MD5_URI:
					message = statusMd5();
					message.append(' ');
					message.append(requireContext().getString(R.string.from_uri));
					break;
				case MD5_FILE:
					message = statusMd5();
					message.append(' ');
					message.append(requireContext().getString(R.string.from_file));
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

				default:
					return;
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
	private SpannableStringBuilder statusCreate()
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
	private SpannableStringBuilder statusDrop()
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
	 * Operation status for copy from uri
	 *
	 * @return status string
	 */
	@NonNull
	private SpannableStringBuilder statusCopy()
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
	private SpannableStringBuilder statusUnzip()
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
	private SpannableStringBuilder statusMd5()
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
	private SpannableStringBuilder statusUpdate()
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
	private SpannableStringBuilder statusDownload()
	{
		final Context context = requireContext();
		final com.bbou.download.Settings.Mode mode = com.bbou.download.Settings.Mode.getModePref(context);
		final String from = StorageSettings.getDbDownloadSourcePath(context, mode == com.bbou.download.Settings.Mode.DOWNLOAD_ZIP_THEN_UNZIP || mode == com.bbou.download.Settings.Mode.DOWNLOAD_ZIP);
		final String to = StorageSettings.getDatabasePath(context);
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
	private SpannableStringBuilder statusDownloadZipped()
	{
		final Context context = requireContext();
		final String from = StorageSettings.getDbDownloadZippedSourcePath(context);
		final String to = StorageSettings.getCachedZippedPath(context);
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
}
