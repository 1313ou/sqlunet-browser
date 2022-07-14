/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.app.Activity;
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

import org.sqlunet.browser.MenuHandler;
import org.sqlunet.browser.common.R;
import org.sqlunet.concurrency.Task;
import org.sqlunet.concurrency.TaskObserver;
import org.sqlunet.concurrency.TaskToastObserver;
import org.sqlunet.provider.ManagerContract;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;

/**
 * Manage fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupDatabaseFragment extends BaseTaskFragment
{
	// static private final String TAG = "SetupDatabaseF";

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
			final long id = spinner.getSelectedItemId();
			if (id == 0)
			{
				return;
			}

			// database path
			final Activity activity = requireActivity();
			final String databasePath = StorageSettings.getDatabasePath(activity.getBaseContext());

			// sqls
			final CharSequence[] sqls = activity.getResources().getTextArray(R.array.sql_statements_values);

			// execute
			final CharSequence sql = sqls[(int) id];
			if (sql == null || "EXEC_URI".equals(sql.toString()))
			{
				final Intent intent2 = new Intent(activity, OperationActivity.class);
				intent2.putExtra(OperationActivity.ARG_OP, OperationActivity.OP_EXEC_SQL);
				intent2.putExtra(OperationActivity.ARG_TYPES, new String[]{"application/sql", "text/plain"});
				activity.startActivity(intent2);
			}
			else if ("EXEC_ZIPPED_URI".equals(sql.toString()))
			{
				final Intent intent2 = new Intent(activity, OperationActivity.class);
				intent2.putExtra(OperationActivity.ARG_OP, OperationActivity.OP_EXEC_ZIPPED_SQL);
				intent2.putExtra(OperationActivity.ARG_ZIP_ENTRY, Settings.getZipEntry(requireContext(), "sql"));
				intent2.putExtra(OperationActivity.ARG_TYPES, new String[]{"application/zip"});
				activity.startActivity(intent2);
			}
			else
			{
				status.setText(R.string.status_task_running);
				final String[] sqlStatements = sql.toString().split(";");
				// Log.d(TAG, Arrays.toString(sqlStatements));
				final TaskObserver.Observer<Number> observer = new TaskToastObserver.WithStatus<>(activity, status);
				final Task<String[], Number, Boolean> task = new ExecAsyncTask(activity, this::update, observer, 1).fromSql(databasePath);
				task.execute(sqlStatements);
			}
		});

		return view;
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// action bar
		final MenuHost host = requireActivity().<Toolbar>findViewById(R.id.toolbar);
		host.addMenuProvider(new MenuProvider()
		{
			@Override
			public void onCreateMenu(@NonNull final Menu menu, @NonNull final MenuInflater menuInflater)
			{
				// inflate
				menu.clear();
				menuInflater.inflate(R.menu.main, menu);
				menuInflater.inflate(R.menu.setup_database, menu);
			}

			@Override
			public boolean onMenuItemSelected(@NonNull final MenuItem menuItem)
			{
				boolean handled = onOptionsItemSelected(menuItem);
				if (handled)
				{
					return true;
				}
				return MenuHandler.menuDispatch((AppCompatActivity) requireActivity(), menuItem);
			}

		}, this.getViewLifecycleOwner(), Lifecycle.State.RESUMED);
		// host.invalidateMenu();
	}

	// U P D A T E

	@SuppressWarnings("EmptyMethod")
	private void update(Boolean result)
	{
	}

	// S P I N N E R

	@NonNull
	@Override
	protected SpinnerAdapter makeAdapter()
	{
		// create an ArrayAdapter using the string array and a default spinner layout
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.sql_statement_titles, R.layout.spinner_item_task);

		// specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(R.layout.spinner_item_task_dropdown);

		return adapter;
	}

	// M E NU

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		Intent intent;

		// handle item selection
		final int itemId = item.getItemId();
		if (itemId == R.id.action_tables_and_indices)
		{
			intent = ManagerContract.makeTablesAndIndexesIntent(requireContext());
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
