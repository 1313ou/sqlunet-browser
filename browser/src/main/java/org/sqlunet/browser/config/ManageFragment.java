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
 * Manage fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class ManageFragment extends BaseManageFragment
{
	//static private final String TAG = "ManageFragment";

	public static final String ARG = "operation";

	private enum Operation
	{
		CREATE, DROP, COPY, UNZIP, MD5, SETUPSQL
	}

	/**
	 * Constructor
	 */
	public ManageFragment()
	{
		// Required empty public constructor
	}

	@Override
	protected SpinnerAdapter makeAdapter()
	{
		// create an ArrayAdapter using the string array and a default spinner layout
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.database_titles, R.layout.spinner_item_simple);

		// specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		return adapter;
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
				// operations
				final CharSequence[] operations = getActivity().getResources().getTextArray(R.array.database_values);

				// execute
				final Context context = getActivity();
				final long id = ManageFragment.this.spinner.getSelectedItemId();
				final CharSequence operation = operations[(int) id];
				final Operation op = Operation.valueOf(operation.toString());
				switch (op)
				{
					case CREATE:
						ManageFragment.this.status.setText(R.string.status_task_running);
						ManageTasks.createDatabase(context, StorageSettings.getDatabasePath(context));
						ManageFragment.this.status.setText(R.string.status_task_done);
						break;

					case DROP:
						ManageFragment.this.status.setText(R.string.status_task_running);
						ManageTasks.deleteDatabase(context, StorageSettings.getDatabasePath(context));
						ManageFragment.this.status.setText(R.string.status_task_done);
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
							FileTask.md5(context);
						}
						break;

					case SETUPSQL:
						final Intent intent = new Intent(context, SetupSqlActivity.class);
						context.startActivity(intent);
						break;
				}
			}
		});

		return view;
	}
}
