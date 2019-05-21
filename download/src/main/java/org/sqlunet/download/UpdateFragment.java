/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import static org.sqlunet.download.BaseDownloadFragment.DOWNLOAD_FROM_ARG;
import static org.sqlunet.download.BaseDownloadFragment.DOWNLOAD_TO_ARG;

/**
 * Update fragment.
 */
@SuppressWarnings("WeakerAccess")
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

	static public final String TO_DEST_ARG = "to_dest";

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
		final Activity activity = requireActivity();
		final Intent intent = activity.getIntent();

		final String fromArg = intent.getStringExtra(FROM_ARG);
		final String fromDateArg = intent.getStringExtra(FROM_DATE_ARG);
		final String fromSizeArg = intent.getStringExtra(FROM_SIZE_ARG);

		final String toArg = intent.getStringExtra(TO_ARG);
		final String toDateArg = intent.getStringExtra(TO_DATE_ARG);
		final String toSizeArg = intent.getStringExtra(TO_SIZE_ARG);

		final boolean newerArg = intent.getBooleanExtra(NEWER_ARG, false);

		final View view = getView();
		assert view != null;

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

		final TextView newer = view.findViewById(R.id.newer);
		if (newerArg)
		{
			newer.setText(R.string.download_newer);
			final ImageButton button = view.findViewById(R.id.update);
			button.setVisibility(View.VISIBLE);
			button.setOnClickListener(v -> {

				final Context context = requireContext();
				confirm(context, R.string.title_activity_update, R.string.askUpdate, () -> {
					final String downloadFromArg = intent.getStringExtra(DOWNLOAD_FROM_ARG);
					final String downloadToArg = intent.getStringExtra(DOWNLOAD_TO_ARG);
					update(context, downloadFromArg, downloadToArg);
					final Activity activity2 = getActivity();
					if (activity2 != null)
					{
						activity2.finish();
					}
				});
			});
		}
		else
		{
			newer.setText(R.string.download_uptodate);
		}
	}

	static private void update(@NonNull final Context context, final String downloadFromArg, final String downloadToArg)
	{
		final Intent intent = new Intent(context, DownloadActivity.class);
		intent.putExtra(DOWNLOAD_FROM_ARG, downloadFromArg);
		intent.putExtra(DOWNLOAD_TO_ARG, downloadToArg);
		context.startActivity(intent);
	}

	/**
	 * Confirm
	 *
	 * @param context  context
	 * @param titleId  title resource id
	 * @param askId    ask resource id
	 * @param runnable run if confirmed
	 */
	private static void confirm(@NonNull final Context context, final int titleId, final int askId, @NonNull final Runnable runnable)
	{
		new AlertDialog.Builder(context) //
				.setIcon(android.R.drawable.ic_dialog_alert) //
				.setTitle(titleId) //
				.setMessage(askId) //
				.setPositiveButton(R.string.yes, (dialog, which) -> runnable.run()).setNegativeButton(R.string.no, null).show();
	}
}
