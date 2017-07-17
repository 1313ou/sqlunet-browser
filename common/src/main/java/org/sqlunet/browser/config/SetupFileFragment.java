package org.sqlunet.browser.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import org.sqlunet.browser.Info;
import org.sqlunet.browser.common.R;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.Storage;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

import java.io.File;

import static org.sqlunet.browser.config.BaseDownloadFragment.DOWNLOAD_FROM_ARG;
import static org.sqlunet.browser.config.BaseDownloadFragment.DOWNLOAD_TO_ARG;

/**
 * Set up fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupFileFragment extends BaseTaskFragment
{
	//static private final String TAG = "SetupFileFragment";

	static private final String ARG = "operation";

	/**
	 * Operations
	 */
	private enum Operation
	{
		CREATE, DROP, COPY, UNZIP, MD5, DOWNLOAD, DOWNLOADZIPPED, UPDATE;

		/**
		 * Spinner operations
		 */
		static private CharSequence[] operations;

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
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
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
				this.spinner.setSelection(op.ordinal());
			}
		}

		this.runButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				// skip first
				final long id = SetupFileFragment.this.spinner.getSelectedItemId();
				if (id == 0)
				{
					return;
				}

				// execute
				boolean success;
				final Context context = getActivity();
				final Operation op = Operation.fromIndex((int) id);
				if (op != null)
				{
					switch (op)
					{
						case CREATE:
							SetupFileFragment.this.status.setText(R.string.status_task_running);
							success = SetupDatabaseTasks.createDatabase(context, StorageSettings.getDatabasePath(context));
							SetupFileFragment.this.status.setText(success ? R.string.status_task_done : R.string.status_task_failed);
							break;

						case DROP:
							Utils.confirm(context, R.string.title_setup_drop, R.string.askDrop, new Runnable()
							{
								@Override
								public void run()
								{
									SetupFileFragment.this.status.setText(R.string.status_task_running);
									boolean success = SetupDatabaseTasks.deleteDatabase(context, StorageSettings.getDatabasePath(context));
									SetupFileFragment.this.status.setText(success ? R.string.status_task_done : R.string.status_task_failed);
								}
							});
							break;

						case COPY:
							if (Permissions.check(getActivity()))
							{
								FileAsyncTask.copyFromFile(context, StorageSettings.getDatabasePath(context));
							}
							break;

						case UNZIP:
							if (Permissions.check(getActivity()))
							{
								String zipEntry = StorageSettings.getImportEntry(context);
								if (zipEntry == null || zipEntry.isEmpty())
								{
									zipEntry = Storage.DBFILE;
								}
								FileAsyncTask.unzipFromArchive(context, zipEntry, StorageSettings.getDatabasePath(context));
							}
							break;

						case MD5:
							if (Permissions.check(getActivity()))
							{
								FileAsyncTask.md5(context);
							}
							break;

						case DOWNLOAD:
							final Intent intent2 = new Intent(context, DownloadActivity.class);
							context.startActivity(intent2);
							break;

						case DOWNLOADZIPPED:
							final Intent intent3 = new Intent(context, DownloadActivity.class);
							intent3.putExtra(DOWNLOAD_FROM_ARG, StorageSettings.getDbDownloadZippedSource(context));
							intent3.putExtra(DOWNLOAD_TO_ARG, StorageSettings.getDbDownloadZippedTarget(context));
							context.startActivity(intent3);
							break;

						case UPDATE:
							Utils.confirm(context, R.string.title_setup_update, R.string.askUpdate, new Runnable()
							{
								@Override
								public void run()
								{
									SetupDatabaseTasks.update(context);
								}
							});
							break;
					}
				}
			}
		});

		return view;
	}

	@Override
	protected SpinnerAdapter makeAdapter()
	{
		// create an ArrayAdapter using the string array and a default spinner layout
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.setup_files_titles, R.layout.spinner_item_task);

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

				case COPY:
					message = statusCopy();
					break;

				case UNZIP:
					message = statusUnzip();
					break;

				case MD5:
					message = statusMd5();
					break;

				case DOWNLOAD:
					message = statusDownload();
					break;

				case DOWNLOADZIPPED:
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
	private CharSequence statusCreate()
	{
		final Context context = getActivity();
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
	private CharSequence statusDrop()
	{
		final Context context = getActivity();
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
	private CharSequence statusCopy()
	{
		final Context context = getActivity();
		final String database = StorageSettings.getDatabasePath(context);
		final String free = StorageUtils.getFree(context, database);
		final boolean databaseExists = new File(database).exists();
		String fromPath = Settings.getCachePref(context);
		boolean sourceExists = false;
		if (fromPath != null)
		{
			fromPath += File.separatorChar + Storage.DBFILE;
			sourceExists = new File(fromPath).exists();
		}

		final SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(getString(R.string.info_op_copy_database));
		sb.append("\n\n");
		Info.build(sb, //
				getString(R.string.title_database), database, //
				getString(R.string.title_status), getString(databaseExists ? R.string.status_database_exists : R.string.status_database_not_exists), //
				getString(R.string.size_expected), getString(R.string.hr_size_sqlunet_db), //
				getString(R.string.size_expected) + ' ' + getString(R.string.total), getString(R.string.hr_size_db_working_total), //
				getString(R.string.title_free), free, //
				getString(R.string.title_from), fromPath, //
				getString(R.string.title_status), getString(sourceExists ? R.string.status_source_exists : R.string.status_source_not_exists));
		return sb;
	}

	/**
	 * Operation status for unzip
	 *
	 * @return status string
	 */
	private CharSequence statusUnzip()
	{
		final Context context = getActivity();
		final String database = StorageSettings.getDatabasePath(context);
		final String free = StorageUtils.getFree(context, database);
		final boolean databaseExists = new File(database).exists();
		String fromPath = Settings.getCachePref(context);
		boolean sourceExists = false;
		if (fromPath != null)
		{
			fromPath += File.separatorChar + Storage.DBFILEZIP;
			sourceExists = new File(fromPath).exists();
		}

		final SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(getString(R.string.info_op_unzip_database));
		sb.append("\n\n");
		Info.build(sb, //
				getString(R.string.title_database), database, //
				getString(R.string.title_status), getString(databaseExists ? R.string.status_database_exists : R.string.status_database_not_exists), //
				getString(R.string.size_expected), getString(R.string.hr_size_sqlunet_db), //
				getString(R.string.size_expected) + ' ' + getString(R.string.total), getString(R.string.hr_size_db_working_total), //
				getString(R.string.title_free), free, //
				getString(R.string.title_from), fromPath, //
				getString(R.string.title_status), getString(sourceExists ? R.string.status_source_exists : R.string.status_source_not_exists));
		return sb;
	}

	/**
	 * Operation status for md5
	 *
	 * @return status string
	 */
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
	private CharSequence statusUpdate()
	{
		return getString(R.string.info_op_drop_database) + '\n' + statusDownload();
	}

	/**
	 * Operation status for download database
	 *
	 * @return status string
	 */
	private CharSequence statusDownload()
	{
		final Context context = getActivity();
		final String from = StorageSettings.getDbDownloadSource(context);
		final String to = StorageSettings.getDbDownloadTarget(context);
		final String free = StorageUtils.getFree(context, to);
		final boolean targetExists = new File(to).exists();

		final SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(getString(R.string.info_op_download_database));
		sb.append("\n\n");
		Info.build(sb, //
				getString(R.string.title_from), from, //
				getString(R.string.title_to), to, //
				getString(R.string.size_expected), getString(R.string.hr_size_sqlunet_db), //
				getString(R.string.size_expected) + ' ' + getString(R.string.total), getString(R.string.hr_size_db_working_total), //
				getString(R.string.title_free), free, //
				getString(R.string.title_status), getString(targetExists ? R.string.status_local_exists : R.string.status_local_not_exists));
		return sb;
	}

	/**
	 * Operation status for download zipped database
	 *
	 * @return status string
	 */
	private CharSequence statusDownloadZipped()
	{
		final Context context = getActivity();
		final String from = StorageSettings.getDbDownloadZippedSource(context);
		final String to = StorageSettings.getDbDownloadZippedTarget(context);
		final String free = StorageUtils.getFree(context, to);
		final boolean targetExists = new File(to).exists();

		final SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(getString(R.string.info_op_download_zipped_database));
		sb.append("\n\n");
		Info.build(sb, //
				getString(R.string.title_from), from, //
				getString(R.string.title_to), to, //
				getString(R.string.size_expected), getString(R.string.hr_size_sqlunet_db_zip), //
				getString(R.string.title_free), free, //
				getString(R.string.title_status), getString(targetExists ? R.string.status_local_exists : R.string.status_local_not_exists));
		return sb;
	}
}
