/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.browser.fn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.sqlunet.browser.ColorUtils;
import org.sqlunet.browser.Info;
import org.sqlunet.browser.config.SetupDatabaseActivity;
import org.sqlunet.browser.config.SetupDatabaseFragment;
import org.sqlunet.browser.config.Utils;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;

/**
 * Setup Status fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupFnStatusFragment extends org.sqlunet.browser.config.SetupStatusFragment
{
	static private final String TAG = "SetupStatusF";

	// components

	private ImageView imageTextSearchFn;

	private ImageButton buttonTextSearchFn;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public SetupFnStatusFragment()
	{
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		assert view != null;

		// images
		this.imageTextSearchFn = view.findViewById(R.id.status_searchtext_fn);

		// buttons
		this.buttonTextSearchFn = view.findViewById(R.id.searchtextFnButton);

		// click listeners
		this.buttonDb.setOnClickListener(v -> download());
		this.buttonIndexes.setOnClickListener(v -> index());
		this.infoDatabaseButton.setOnClickListener(v -> info());
		this.buttonTextSearchFn.setOnClickListener(v -> {

			int index = getResources().getInteger(R.integer.sql_statement_do_ts_fn_position);
			final Intent intent = new Intent(requireContext(), SetupDatabaseActivity.class);
			intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index);
			startActivity(intent);
		});

		this.infoDatabaseButton.setOnClickListener(v -> {

			final Activity activity = requireActivity();
			final String database = StorageSettings.getDatabasePath(activity);
			final String free = StorageUtils.getFree(requireContext(), database);
			final String source = StorageSettings.getDbDownloadSource(activity, org.sqlunet.download.Settings.Downloader.isZipDownloaderPref(activity));
			final int status = Status.status(activity);
			final boolean existsDb = (status & Status.EXISTS) != 0;
			final boolean existsTables = (status & Status.EXISTS_TABLES) != 0;
			if (existsDb)
			{
				final long size = new File(database).length();
				final String hrSize = StorageUtils.countToStorageString(size) + " (" + size + ')';
				Info.info(activity, R.string.title_status, //
						getString(R.string.title_database), database, //
						getString(R.string.title_status), getString(R.string.status_database_exists) + '-' + getString(existsTables ? R.string.status_data_exists : R.string.status_data_not_exists), //
						getString(R.string.title_free), free, //
						getString(R.string.size_expected), Utils.hrSize(R.integer.size_sqlunet_db, requireContext()), //
						getString(R.string.size_expected) + ' ' + getString(R.string.text_search), Utils.hrSize(R.integer.size_searchtext, requireContext()), //
						getString(R.string.size_expected) + ' ' + getString(R.string.total), Utils.hrSize(R.integer.size_db_working_total, requireContext()), //
						getString(R.string.size_current), hrSize);
			}
			else
			{
				Info.info(activity, R.string.title_dialog_info_download, //
						getString(R.string.title_operation), getString(R.string.info_op_download_database), //
						getString(R.string.title_from), source, //
						getString(R.string.title_database), database, //
						getString(R.string.title_free), free, //
						getString(R.string.size_expected), Utils.hrSize(R.integer.size_sqlunet_db, requireContext()), //
						getString(R.string.size_expected) + ' ' + getString(R.string.text_search), Utils.hrSize(R.integer.size_searchtext, requireContext()), //
						getString(R.string.size_expected) + ' ' + getString(R.string.total), Utils.hrSize(R.integer.size_db_working_total, requireContext()), //
						getString(R.string.title_status), getString(R.string.status_database_not_exists));
			}
		});
		return view;
	}

	// U P D A T E

	/**
	 * Update status
	 */
	@Override
	public void update()
	{
		super.update();

		final Context context = getContext();
		if (context != null)
		{
			final int status = Status.status(context);
			Log.d(TAG, "STATUS " + Status.toString(status));

			final boolean existsDb = (status & Status.EXISTS) != 0;
			final boolean existsTables = (status & Status.EXISTS_TABLES) != 0;
			if (existsDb && existsTables)
			{
				// images
				final Drawable okDrawable = ColorUtils.getDrawable(context, R.drawable.ic_ok);
				final Drawable failDrawable = ColorUtils.getDrawable(context, R.drawable.ic_fail);

				final boolean existsTsFn = (status & Status.EXISTS_TS_FN) != 0;
				this.imageTextSearchFn.setImageDrawable(existsTsFn ? okDrawable : failDrawable);
				ImageViewCompat.setImageTintMode(this.imageTextSearchFn, existsTsFn ? PorterDuff.Mode.SRC_IN : PorterDuff.Mode.DST);
				this.buttonTextSearchFn.setVisibility(existsTsFn ? View.GONE : View.VISIBLE);
			}
			else
			{
				this.buttonTextSearchFn.setVisibility(View.GONE);
				this.imageTextSearchFn.setImageResource(R.drawable.ic_unknown);
			}
		}
	}
}
