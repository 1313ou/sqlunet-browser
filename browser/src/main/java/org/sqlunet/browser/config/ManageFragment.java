package org.sqlunet.browser.config;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
public class ManageFragment extends Fragment implements TaskObserver.Listener
{
	static private final String TAG = "ManageFragment";

	public static final String ARG = "statement";

	/**
	 * Action spinner
	 */
	private Spinner spinner;

	/**
	 * Status view
	 */
	private TextView status;

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
		setHasOptionsMenu(true);

		// view
		final View view = inflater.inflate(R.layout.fragment_manage, container, false);

		// database path
		final String databasePath = StorageSettings.getDatabasePath(getActivity().getBaseContext());

		// sqls
		final CharSequence[] sqls = getActivity().getResources().getTextArray(R.array.manage_values);

		// type spinner
		this.spinner = (Spinner) view.findViewById(R.id.manage_actions);

		// create an ArrayAdapter using the string array and a default spinner layout
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.manage_titles, R.layout.spinner_item_simple);

		// specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// apply the adapter to the spinner
		this.spinner.setAdapter(adapter);
		this.spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view0, final int position, final long id)
			{
				ManageFragment.this.status.setText("");
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parent)
			{
				ManageFragment.this.status.setText("");
			}
		});

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
				case Status.DO_PM: // pm 2
				case Status.DO_TS_WN:  // ts_wn 3
				case Status.DO_TS_VN: // ts_vn 4
				case Status.DO_TS_PB: // ts_pb 5
				case Status.DO_TS_FN: // ts_fn 6
					this.spinner.setSelection(arg - 1);
					break;
			}
		}

		// type _status
		this.status = (TextView) view.findViewById(R.id.manage_status);

		// run button
		ImageButton run = (ImageButton) view.findViewById(R.id.manage_run);
		run.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				final long id = ManageFragment.this.spinner.getSelectedItemId();
				final CharSequence sql = sqls[(int) id];
				final String[] sqls1 = sql.toString().split(";");
				ManageFragment.this.status.setText(R.string.status_op_running);
				new ManageAsyncTask(ManageFragment.this, 1).executeFromSql(databasePath, sqls1);
			}
		});
		return view;
	}

	@Override
	public void taskStart(final AsyncTask<?, ?, ?> task)
	{
		Log.d(TAG, "Update start");
		Toast.makeText(getActivity(), R.string.status_update_start, Toast.LENGTH_SHORT).show();
		ManageFragment.this.status.setText(R.string.status_op_running);
	}

	@Override
	public void taskFinish(final boolean result)
	{
		Log.d(TAG, "Update " + (result ? "succeeded" : "failed"));
		Toast.makeText(getActivity(), result ? R.string.status_update_success : R.string.status_update_failure, Toast.LENGTH_SHORT).show();
		ManageFragment.this.status.setText(result ? R.string.status_op_done : R.string.status_op_failed);
	}

	@Override
	public void taskUpdate(final int progress)
	{
		Log.d(TAG, "Update " + progress);
		// Toast.makeText(getActivity(), "Update ", Toast.LENGTH_SHORT).show();
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
			case R.id.action_setup:
				intent = new Intent(context, SetupActivity.class);
				break;

			case R.id.action_setup_sql:
				intent = new Intent(context, SetupSqlActivity.class);
				break;

			case R.id.action_tables_and_indices:
				intent = ManagerContract.makeTablesAndIndexesIntent(context);
				intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_dbobject);
				break;

			case R.id.action_create_database:
				ManageTasks.createDatabase(context, StorageSettings.getDatabasePath(context));
				break;

			case R.id.action_drop_database:
				ManageTasks.deleteDatabase(context, StorageSettings.getDatabasePath(context));
				break;

			case R.id.action_vacuum:
				ManageTasks.vacuum(context, StorageSettings.getDatabasePath(context), StorageSettings.getDataDir(context));
				break;

			case R.id.action_flush_tables:
				ManageTasks.flushAll(context, StorageSettings.getDatabasePath(context), ManagerProvider.getTables(context));
				break;

			case R.id.action_drop_tables:
				ManageTasks.dropAll(context, StorageSettings.getDatabasePath(context), ManagerProvider.getTables(context));
				break;

			case R.id.action_copy_from_database:
				if (Permissions.check(getActivity()))
				{
					FileAsyncTask.copyFromFile(context, StorageSettings.getDatabasePath(context));
				}
				break;

			case R.id.action_unzip_from_database:
				if (Permissions.check(getActivity()))
				{
					FileAsyncTask.unzipFromArchive(context, StorageSettings.getDatabasePath(context));
				}
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
