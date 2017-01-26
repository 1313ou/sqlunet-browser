package org.sqlunet.browser.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import org.sqlunet.browser.R;
import org.sqlunet.settings.StorageSettings;

/**
 * Set up fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupFileFragment extends BaseTaskFragment
{
	//static private final String TAG = "SetupFileFragment";

	private static final String ARG = "operation";

	/**
	 * Operations
	 */
	private enum Operation
	{
		CREATE, DROP, COPY, UNZIP, MD5, SETUPSQL, DOWNLOAD
	}

	/**
	 * Constructor
	 */
	public SetupFileFragment()
	{
		// Required empty public constructor
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

				// operations
				final CharSequence[] operations = getActivity().getResources().getTextArray(R.array.setup_values);

				// execute
				final Context context = getActivity();
				final CharSequence operation = operations[(int) id];
				final Operation op = Operation.valueOf(operation.toString());
				switch (op)
				{
					case CREATE:
						SetupFileFragment.this.status.setText(R.string.status_task_running);
						SetupDatabaseTasks.createDatabase(context, StorageSettings.getDatabasePath(context));
						SetupFileFragment.this.status.setText(R.string.status_task_done);
						break;

					case DROP:
						SetupFileFragment.this.status.setText(R.string.status_task_running);
						SetupDatabaseTasks.deleteDatabase(context, StorageSettings.getDatabasePath(context));
						SetupFileFragment.this.status.setText(R.string.status_task_done);
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
							FileAsyncTask.unzipFromArchive(context, StorageSettings.getDatabasePath(context));
						}
						break;

					case MD5:
						if (Permissions.check(getActivity()))
						{
							FileAsyncTask.md5(context);
						}
						break;

					case SETUPSQL:
						final Intent intent = new Intent(context, SetupSqlActivity.class);
						context.startActivity(intent);
						break;

					case DOWNLOAD:
						final Intent intent2 = new Intent(context, DownloadActivity.class);
						context.startActivity(intent2);
						break;
				}
			}
		});

		return view;
	}

	@Override
	protected SpinnerAdapter makeAdapter()
	{
		// create an ArrayAdapter using the string array and a default spinner layout
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.setup_titles, R.layout.spinner_item_simple);

		// specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		return adapter;
	}
}
