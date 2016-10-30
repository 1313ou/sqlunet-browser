package org.sqlunet.browser.config;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import org.sqlunet.provider.ExecuteManager;
import org.sqlunet.provider.ExecuteManager.Listener;
import org.sqlunet.settings.StorageSettings;

/**
 * Management fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class ManagementFragment extends Fragment implements Listener
{
	private static final String TAG = "ManagementFragment"; //

	public static final String ARG = "statement"; //

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
	public ManagementFragment()
	{
		// Required empty public constructor
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// database path
		final String databasePath = StorageSettings.getDatabasePath(getActivity().getBaseContext());

		// sqls
		final CharSequence[] sqls = getActivity().getResources().getTextArray(R.array.management_values);

		// inflate the layout for this fragment
		final View rootView = inflater.inflate(R.layout.fragment_management, container, false);

		// action spinner
		this.spinner = (Spinner) rootView.findViewById(R.id.management_actions);

		// create an ArrayAdapter using the string array and a default spinner layout
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.management_titles, R.layout.spinner_item_simple);

		// specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// apply the adapter to the spinner
		this.spinner.setAdapter(adapter);
		this.spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				ManagementFragment.this.status.setText(""); //
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
				ManagementFragment.this.status.setText(""); //
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

		// action status
		this.status = (TextView) rootView.findViewById(R.id.management_status);

		// run button
		ImageButton run = (ImageButton) rootView.findViewById(R.id.management_run);
		run.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				final long id = ManagementFragment.this.spinner.getSelectedItemId();
				final CharSequence sql = sqls[(int) id];
				final String[] sqls1 = sql.toString().split(";"); //
				ManagementFragment.this.status.setText(R.string.status_op_running);
				new ExecuteManager(databasePath, ManagementFragment.this, 1).executeFromSql(sqls1);
			}
		});
		return rootView;
	}

	@Override
	public void managerStart()
	{
		Log.d(TAG, "Update start"); //
		Toast.makeText(getActivity(), R.string.status_update_start, Toast.LENGTH_SHORT).show();
		ManagementFragment.this.status.setText(R.string.status_op_running);
	}

	@Override
	public void managerFinish(final boolean result)
	{
		Log.d(TAG, "Update " + (result ? "succeeded" : "failed")); //
		Toast.makeText(getActivity(), result ? R.string.status_update_success : R.string.status_update_failure, Toast.LENGTH_SHORT).show();
		ManagementFragment.this.status.setText(result ? R.string.status_op_done : R.string.status_op_failed);
	}

	@Override
	public void managerUpdate(final int progress)
	{
		Log.d(TAG, "Update " + progress); //
		// Toast.makeText(getActivity(), "Update ", Toast.LENGTH_SHORT).show();
	}
}
