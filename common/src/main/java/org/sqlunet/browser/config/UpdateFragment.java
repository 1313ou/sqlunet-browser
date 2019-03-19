package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.sqlunet.browser.common.R;

/**
 * Update fragment.
 */
public class UpdateFragment extends Fragment
{
	static public final String CURRENT_DATE_ARG = "current_date";

	static public final String CURRENT_SIZE_ARG = "current_size";

	/**
	 * From argument
	 */
	static public final String FROM_ARG = "from";

	static public final String FROM_DATE_ARG = "from_date";

	static public final String FROM_SIZE_ARG = "from_size";

	/**
	 * To argument
	 */
	static public final String TO_ARG = "to";

	static public final String TO_DATE_ARG = "to_date";

	static public final String TO_SIZE_ARG = "to_size";

	static public final String NEWER_ARG = "newer";

	public UpdateFragment()
	{
		// required empty public constructor
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_update, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// arguments
		final Activity activity = getActivity();
		assert activity != null;
		final Intent intent = activity.getIntent();

		final String currentDateArg = intent.getStringExtra(CURRENT_DATE_ARG);
		final String currentSizeArg = intent.getStringExtra(CURRENT_SIZE_ARG);

		final String fromArg = intent.getStringExtra(FROM_ARG);
		final String fromDateArg = intent.getStringExtra(FROM_DATE_ARG);
		final String fromSizeArg = intent.getStringExtra(FROM_SIZE_ARG);

		final String toArg = intent.getStringExtra(TO_ARG);
		final String toDateArg = intent.getStringExtra(TO_DATE_ARG);
		final String toSizeArg = intent.getStringExtra(TO_SIZE_ARG);

		final boolean newerArg = intent.getBooleanExtra(NEWER_ARG, false);

		final View view = getView();
		assert view != null;

		final TextView currentDate = view.findViewById(R.id.current_date);
		final TextView currentSize = view.findViewById(R.id.current_size);
		currentDate.setText(currentDateArg);
		currentSize.setText(currentSizeArg);

		final TextView src = view.findViewById(R.id.src);
		final TextView srcDate = view.findViewById(R.id.src_date);
		final TextView srcSize = view.findViewById(R.id.src_size);
		src.setText(fromArg);
		srcDate.setText(fromDateArg);
		srcSize.setText(fromSizeArg);

		final TextView dest = view.findViewById(R.id.dest);
		final TextView destDate = view.findViewById(R.id.dest_date);
		final TextView destSize = view.findViewById(R.id.dest_size);
		dest.setText(toArg);
		destDate.setText(toDateArg);
		destSize.setText(toSizeArg);

		if (newerArg)
		{
			final TextView newer = view.findViewById(R.id.newer);
			newer.setText(R.string.download_newer);
			final ImageButton button = view.findViewById(R.id.update);
			button.setVisibility(View.VISIBLE);
			button.setOnClickListener(v -> {
				final Context context = getActivity();
				assert context != null;
				Utils.confirm(context, R.string.title_activity_update, R.string.askUpdate, () -> SetupDatabaseTasks.update(context));
			});
		}
	}
}
