package org.sqlunet.browser.config;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
	Spinner spinner;

	/**
	 * Status view
	 */
	TextView status;

	/**
	 * Run button
	 */
	ImageButton runButton;

	/**
	 * Layout id set by derived class
	 */
	int layoutId;

	/**
	 * Make spinner
	 *
	 * @return spinner adapter
	 */
	@NonNull
	abstract protected SpinnerAdapter makeAdapter();

	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);

		// view
		final View view = inflater.inflate(this.layoutId, container, false);

		// task spinner
		this.spinner = view.findViewById(R.id.task_spinner);

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
		this.status = view.findViewById(R.id.task_status);

		// task run button
		this.runButton = view.findViewById(R.id.task_run);

		return view;
	}

	void select(final int position)
	{
		BaseTaskFragment.this.status.setText("");
	}
}
