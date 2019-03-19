package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import org.sqlunet.browser.common.R;
import org.sqlunet.provider.ManagerContract;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.settings.StorageSettings;

/**
 * Manage fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupDatabaseFragment extends BaseTaskFragment
{
	// static private final String TAG = "SetupDatabaseFragment";

	/**
	 * Initial spinner position
	 */
	static public final String ARG_POSITION = "position";

	/**
	 * Constructor
	 */
	public SetupDatabaseFragment()
	{
		this.layoutId = R.layout.fragment_setup_database;
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		// args (relies on order of resources matching that of DO_)
		Bundle args = getArguments();
		if (args != null)
		{
			final int arg = args.getInt(ARG_POSITION);
			if (arg > 0)
			{
				this.spinner.setSelection(arg);
			}
		}

		this.runButton.setOnClickListener(v -> {
			// skip first
			final long id = SetupDatabaseFragment.this.spinner.getSelectedItemId();
			if (id == 0)
			{
				return;
			}
			SetupDatabaseFragment.this.status.setText(R.string.status_task_running);

			// database path
			final Activity activity = getActivity();
			assert activity != null;
			final String databasePath = StorageSettings.getDatabasePath(activity.getBaseContext());

			// sqls
			final CharSequence[] sqls = getActivity().getResources().getTextArray(R.array.sql_statements_values);

			// execute
			final CharSequence sql = sqls[(int) id];
			final String[] sqlStatements = sql.toString().split(";");
			new ExecAsyncTask(getActivity(), new TaskObserver.ToastWithStatusListener(getActivity(), SetupDatabaseFragment.this.status), 1).executeFromSql(databasePath, sqlStatements);
		});

		return view;
	}

	@NonNull
	@Override
	protected SpinnerAdapter makeAdapter()
	{
		// create an ArrayAdapter using the string array and a default spinner layout
		final Context context = getActivity();
		assert context != null;
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.sql_statement_titles, R.layout.spinner_item_task);

		// specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(R.layout.spinner_item_task_dropdown);

		return adapter;
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, @NonNull final MenuInflater inflater)
	{
		inflater.inflate(R.menu.setup_database, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		final Context context = getActivity();
		Intent intent;

		// handle item selection
		int i = item.getItemId();
		if (i == R.id.action_tables_and_indices)
		{
			intent = ManagerContract.makeTablesAndIndexesIntent(context);
			intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_dbobject);
		}
		else
		{
			return false;
		}

		startActivity(intent);
		return true;
	}
}
