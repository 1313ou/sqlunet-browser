package org.sqlunet.browser.config;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sqlunet.browser.common.R;

/**
 * Update fragment.
 */
public class UpdateFragment extends Fragment
{
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_update, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// arguments
		final Intent intent = getActivity().getIntent();

		final String fromArg = intent.getStringExtra(FROM_ARG);
		final String fromDateArg = intent.getStringExtra(FROM_DATE_ARG);
		final String fromSizeArg = intent.getStringExtra(FROM_SIZE_ARG);

		final String toArg = intent.getStringExtra(TO_ARG);
		final String toDateArg = intent.getStringExtra(TO_DATE_ARG);
		final String toSizeArg = intent.getStringExtra(TO_SIZE_ARG);

		final boolean newerArg = intent.getBooleanExtra(NEWER_ARG, false);

		final View view = getView();

		final TextView src = (TextView) view.findViewById(R.id.src);
		final TextView srcDate = (TextView) view.findViewById(R.id.src_date);
		final TextView srcSize = (TextView) view.findViewById(R.id.src_size);

		src.setText(fromArg);
		srcDate.setText(fromDateArg);
		srcSize.setText(fromSizeArg);

		final TextView dest = (TextView) view.findViewById(R.id.dest);
		final TextView destDate = (TextView) view.findViewById(R.id.dest_date);
		final TextView destSize = (TextView) view.findViewById(R.id.dest_size);

		dest.setText(toArg);
		destDate.setText(toDateArg);
		destSize.setText(toSizeArg);

		if(newerArg)
		{
			final TextView newer = (TextView) view.findViewById(R.id.newer);newer.setText(R.string.download_source_newer);
		}
	}
}
