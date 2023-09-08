/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.download;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
	 * Up argument (available upstream)
	 */
	static public final String UP_SOURCE_ARG = "up_source";

	static public final String UP_DATE_ARG = "up_date";

	static public final String UP_SIZE_ARG = "up_size";

	static public final String UP_ETAG_ARG = "up_etag";

	static public final String UP_VERSION_ARG = "up_version";

	static public final String UP_STATIC_VERSION_ARG = "up_static_version";

	/**
	 * Down argument (already downloaded)
	 */
	static public final String DOWN_NAME_ARG = "down_name";

	static public final String DOWN_TARGET_ARG = "down_target";

	static public final String DOWN_DATE_ARG = "down_date";

	static public final String DOWN_SIZE_ARG = "down_size";

	static public final String DOWN_SOURCE_ARG = "down_source";

	static public final String DOWN_SOURCE_DATE_ARG = "down_source_date";

	static public final String DOWN_SOURCE_SIZE_ARG = "down_source_size";

	static public final String DOWN_SOURCE_ETAG_ARG = "down_source_etag";

	static public final String DOWN_SOURCE_VERSION_ARG = "down_source_version";

	static public final String DOWN_SOURCE_STATIC_VERSION_ARG = "down_source_static_version";

	static public final String DOWNLOAD_INTENT_ARG = "download_intent";

	/**
	 * Newer argument
	 */
	static public final String NEWER_ARG = "newer";

	public UpdateFragment()
	{
		// required empty public constructor
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_update, container, false);

		// arguments
		final Activity activity = requireActivity();
		final Intent intent = activity.getIntent();

		final String upSrcArg = intent.getStringExtra(UP_SOURCE_ARG);
		final String upDateArg = intent.getStringExtra(UP_DATE_ARG);
		final String upSizeArg = intent.getStringExtra(UP_SIZE_ARG);
		final String upEtagArg = intent.getStringExtra(UP_ETAG_ARG);
		final String upVersionArg = intent.getStringExtra(UP_VERSION_ARG);
		final String upStaticVersionArg = intent.getStringExtra(UP_STATIC_VERSION_ARG);

		final String downNameArg = intent.getStringExtra(DOWN_NAME_ARG);
		final String downDateArg = intent.getStringExtra(DOWN_DATE_ARG);
		final String downSizeArg = intent.getStringExtra(DOWN_SIZE_ARG);
		final String downSourceArg = intent.getStringExtra(DOWN_SOURCE_ARG);
		final String downSourceDateArg = intent.getStringExtra(DOWN_SOURCE_DATE_ARG);
		final String downSourceSizeArg = intent.getStringExtra(DOWN_SOURCE_SIZE_ARG);
		final String downSourceEtagArg = intent.getStringExtra(DOWN_SOURCE_ETAG_ARG);
		final String downSourceVersionArg = intent.getStringExtra(DOWN_SOURCE_VERSION_ARG);
		final String downSourceStaticVersionArg = intent.getStringExtra(DOWN_SOURCE_STATIC_VERSION_ARG);

		final boolean newerArg = intent.getBooleanExtra(NEWER_ARG, false);
		final Intent downloadIntent = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU ? intent.getParcelableExtra(DOWNLOAD_INTENT_ARG, Intent.class) : intent.getParcelableExtra(DOWNLOAD_INTENT_ARG);

		assert view != null;

		final TextView upSrc = view.findViewById(R.id.up_src);
		final TextView upDate = view.findViewById(R.id.up_date);
		final TextView upSize = view.findViewById(R.id.up_size);
		final TextView upEtag = view.findViewById(R.id.up_etag);
		final TextView upVersion = view.findViewById(R.id.up_version);
		final TextView upStaticVersion = view.findViewById(R.id.up_static_version);
		upSrc.setText(upSrcArg);
		upDate.setText(upDateArg);
		upSize.setText(upSizeArg);
		upEtag.setText(upEtagArg);
		upVersion.setText(upVersionArg);
		upStaticVersion.setText(upStaticVersionArg);

		final TextView downDb = view.findViewById(R.id.down_model);
		final TextView downDate = view.findViewById(R.id.down_model_date);
		final TextView downSize = view.findViewById(R.id.down_model_size);
		final TextView downSource = view.findViewById(R.id.down_source);
		final TextView downSourceDate = view.findViewById(R.id.down_source_date);
		final TextView downSourceSize = view.findViewById(R.id.down_source_size);
		final TextView downSourceEtag = view.findViewById(R.id.down_source_etag);
		final TextView downSourceVersion = view.findViewById(R.id.down_source_version);
		final TextView downSourceStaticVersion = view.findViewById(R.id.down_source_static_version);
		downDb.setText(downNameArg);
		downDate.setText(downDateArg);
		downSize.setText(downSizeArg);
		downSource.setText(downSourceArg);
		downSourceDate.setText(downSourceDateArg);
		downSourceSize.setText(downSourceSizeArg);
		downSourceEtag.setText(downSourceEtagArg);
		downSourceVersion.setText(downSourceVersionArg);
		downSourceStaticVersion.setText(downSourceStaticVersionArg);

		final TextView newer = view.findViewById(R.id.newer);
		if (newerArg)
		{
			newer.setTextColor(Color.BLUE);
			newer.setText(R.string.download_newer);
			final ImageButton button = view.findViewById(R.id.update);
			button.setVisibility(View.VISIBLE);
			button.setOnClickListener(v -> {

				final Context context = requireContext();
				confirm(context, R.string.title_activity_update, R.string.askUpdate, () -> {

					final String downloadFromArg = intent.getStringExtra(DOWNLOAD_FROM_ARG);
					final String downloadToArg = intent.getStringExtra(DOWNLOAD_TO_ARG);
					update(context, downloadFromArg, downloadToArg, downloadIntent);
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
			newer.setTextColor(Color.GREEN);
			newer.setText(R.string.download_uptodate);
		}
		return view;
	}

	static private void update(@NonNull final Context context, final String downloadFromArg, final String downloadToArg, @Nullable final Intent downloadIntent)
	{
		final Intent intent = downloadIntent != null ? downloadIntent : new Intent(context, DownloadActivity.class);
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
