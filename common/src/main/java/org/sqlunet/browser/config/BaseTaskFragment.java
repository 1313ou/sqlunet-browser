package org.sqlunet.browser.config;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.sqlunet.browser.common.R;

/**
 * Base task fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class BaseTaskFragment extends Fragment
{
	// static private final String TAG = "TaskFragment";

	/**
	 * Action spinner
	 */
	@SuppressWarnings("WeakerAccess")
	protected Spinner spinner;

	/**
	 * Status view
	 */
	TextView status;

	/**
	 * Run button
	 */
	@SuppressWarnings("WeakerAccess")
	protected ImageButton runButton;

	/**
	 * Layout id set by derived class
	 */
	@SuppressWarnings("WeakerAccess")
	protected int layoutId;

	/**
	 * Make spinner
	 *
	 * @return spinner adapter
	 */
	abstract protected SpinnerAdapter makeAdapter();

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);

		// view
		final View view = inflater.inflate(this.layoutId, container, false);

		// task spinner
		this.spinner = (Spinner) view.findViewById(R.id.task_spinner);

		// adapter
		final SpinnerAdapter adapter = makeAdapter();

		// apply the adapter to the spinner
		this.spinner.setAdapter(adapter);
		this.spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view0, final int position, final long id)
			{
				select(position);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parent)
			{
				select(-1);
			}
		});

		// task status view
		this.status = (TextView) view.findViewById(R.id.task_status);

		// task run button
		this.runButton = (ImageButton) view.findViewById(R.id.task_run);

		return view;
	}

	@SuppressWarnings("WeakerAccess")
	protected void select(final int position)
	{
		BaseTaskFragment.this.status.setText("");
	}
}
