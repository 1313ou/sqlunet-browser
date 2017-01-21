package org.sqlunet.browser.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import org.sqlunet.browser.R;
import org.sqlunet.provider.ManagerContract;
import org.sqlunet.provider.ManagerProvider;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.settings.StorageSettings;

/**
 * Manage fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class ManageFragment extends BaseTaskFragment
{
	// static private final String TAG = "ManageFragment";

	/**
	 * Initial spinner position
	 */
	public static final String ARG = "position";

	/**
	 * Constructor
	 */
	public ManageFragment()
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
			final int arg = args.getInt(ARG);
			switch (arg)
			{
				default:
					break;
				case Status.DO_INDEXES: // index 1
				case Status.DO_PM:      // pm 2
				case Status.DO_TS_WN:   // ts_wn 3
				case Status.DO_TS_VN:   // ts_vn 4
				case Status.DO_TS_PB:   // ts_pb 5
				case Status.DO_TS_FN:   // ts_fn 6
					this.spinner.setSelection(arg - 1);
					break;
			}
		}

		this.runButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				ManageFragment.this.status.setText(R.string.status_task_running);

				// database path
				final String databasePath = StorageSettings.getDatabasePath(getActivity().getBaseContext());

				// sqls
				final CharSequence[] sqls = getActivity().getResources().getTextArray(R.array.manage_values);

				// execute
				final long id = ManageFragment.this.spinner.getSelectedItemId();
				final CharSequence sql = sqls[(int) id];
				final String[] sqlStatements = sql.toString().split(";");
				new ExecAsyncTask(new TaskObserver.ToastWithStatusListener(getActivity(), ManageFragment.this.status), 1).executeFromSql(databasePath, sqlStatements);
			}
		});

		return view;
	}

	@Override
	protected SpinnerAdapter makeAdapter()
	{
		// create an ArrayAdapter using the string array and a default spinner layout
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.manage_titles, R.layout.spinner_item_simple);

		// specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		return adapter;
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater)
	{
		inflater.inflate(R.menu.manage, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		final Context context = getActivity();
		Intent intent = null;

		// handle item selection
		switch (item.getItemId())
		{
			case R.id.action_tables_and_indices:
				intent = ManagerContract.makeTablesAndIndexesIntent(context);
				intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_dbobject);
				break;

			case R.id.action_drop_tables:
				SetupTasks.dropAll(context, StorageSettings.getDatabasePath(context), ManagerProvider.getTables(context));
				break;

			case R.id.action_flush_tables:
				SetupTasks.flushAll(context, StorageSettings.getDatabasePath(context), ManagerProvider.getTables(context));
				break;

			case R.id.action_vacuum:
				new ExecAsyncTask(new TaskObserver.ToastListener(getActivity()), 1).vacuum(StorageSettings.getDatabasePath(context), StorageSettings.getDataDir(context));
				break;

			case R.id.action_setup:
				intent = new Intent(context, SetupActivity.class);
				break;

			default:
				return false;
		}

		if (intent != null)
		{
			startActivity(intent);
		}
		return true;
	}
}
