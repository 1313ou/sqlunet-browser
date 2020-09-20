/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.xn;

import android.app.Activity;
import android.content.Context;
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
import org.sqlunet.browser.R;
import org.sqlunet.browser.config.ExecAsyncTask;
import org.sqlunet.concurrency.ObservedDelegatingTask;
import org.sqlunet.concurrency.Task;
import org.sqlunet.concurrency.TaskObserver;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

/**
 * Set up with SQL fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupXSqlFragment extends org.sqlunet.browser.config.SetupSqlFragment
{
	static private final String TAG = "SetupSqlF";

	// pm button
	private ImageButton predicateMatrixButton;

	// pm view
	private ImageView pmStatus;

	/**
	 * Constructor
	 */
	public SetupXSqlFragment()
	{
		// Required empty public constructor
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		assert view != null;
		return view;
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// statuses
		this.pmStatus = view.findViewById(R.id.status_pm);

		// buttons
		this.predicateMatrixButton = view.findViewById(R.id.execute_predicatematrix);
		final ImageButton infoPmButton = view.findViewById(R.id.info_pm);

		// pm exec button
		this.predicateMatrixButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				final FragmentActivity activity = requireActivity();

				// starting pm task
				try
				{
					final String database = StorageSettings.getDatabasePath(activity);
					final String source = StorageSettings.getSqlSource(activity);
					final String entry = Settings.getPmEntry(activity);
					final String unit = activity.getString(R.string.unit_statement);
					final TaskObserver.Listener<Integer> listener = new TaskObserver.BaseListener<>();
					final Task<String, Integer, Boolean> st = new ExecAsyncTask(activity, SetupXSqlFragment.this::update, listener, 1).fromArchive();
					// final TaskObserver.Listener<Integer> stListener = new TaskObserver.ProgressDialogListener<>(activity, activity.getString(R.string.status_managing), source + '@' + entry, unit);
					final TaskObserver.Listener<Integer> stListener = new TaskObserver.DialogListener<>(activity.getSupportFragmentManager(), activity.getString(R.string.status_managing), source + '@' + entry, unit);
					final ObservedDelegatingTask<String, Integer, Boolean> oft = new ObservedDelegatingTask<>(st, stListener);
					oft.execute(database, source, entry);
				}
				catch (@NonNull final Exception e)
				{
					Log.e(TAG, "While preparing predicatematrix", e);
				}
			}
		});
		// pm info button
		infoPmButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				final Activity activity = requireActivity();
				final String database = StorageSettings.getDatabasePath(activity);
				final String source = StorageSettings.getSqlSource(activity);
				final String entry = Settings.getPmEntry(activity);
				final String free = StorageUtils.getFree(activity, database);
				final boolean dbExists = new File(database).exists();
				final boolean sqlzipExists = new File(source).exists();
				Info.info(activity, R.string.title_predicatematrix, //
						getString(R.string.title_operation), getString(R.string.info_op_execute_predicatematrix), //
						getString(R.string.title_database), database, //
						getString(R.string.title_status), getString(dbExists ? R.string.status_database_exists : R.string.status_database_not_exists), //
						getString(R.string.title_free), free, //
						getString(R.string.title_archive), source, //
						getString(R.string.title_entry), entry, //
						getString(R.string.title_status), getString(sqlzipExists ? R.string.status_local_exists : R.string.status_local_not_exists));
			}
		});
	}

	/**
	 * Update status
	 */
	@SuppressWarnings("WeakerAccess")
	@Override
	public void update()
	{
		super.update();

		final Context context = getContext();
		if (context != null)
		{
			// sql zip file
			final String sqlZip = StorageSettings.getSqlSource(context);
			boolean sqlZipExists = new File(sqlZip).exists();

			// images
			final Drawable okDrawable = ColorUtils.getDrawable(context, R.drawable.ic_ok);
			ColorUtils.tint(ColorUtils.getColor(context, R.color.tertiaryForeColor), okDrawable);
			final Drawable failDrawable = ColorUtils.getDrawable(context, R.drawable.ic_fail);

			// status
			final int status = Status.status(context);
			final boolean existsDatabase = (status & Status.EXISTS) != 0;
			final boolean existsTables = (status & Status.EXISTS_TABLES) != 0;
			// final boolean existsIndexes = (status & org.sqlunet.browser.config.Status.EXISTS_INDEXES) != 0;
			final boolean existsPm = (status & Status.EXISTS_PREDICATEMATRIX) != 0;
			this.pmStatus.setImageDrawable(existsPm ? okDrawable : failDrawable);

			// actions
			this.predicateMatrixButton.setVisibility(sqlZipExists && existsDatabase && existsTables && !existsPm ? View.VISIBLE : View.GONE);
		}
	}
}
