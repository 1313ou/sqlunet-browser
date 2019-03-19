package org.sqlunet.browser.xn;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import org.sqlunet.browser.config.TaskObserver;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

import java.io.File;

/**
 * Set up with SQL fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupSqlFragment extends org.sqlunet.browser.config.SetupSqlFragment
{
	static private final String TAG = "SetupSqlFragment";

	// pm button
	private ImageButton predicateMatrixButton;

	// pm view
	private ImageView pmStatus;

	/**
	 * Constructor
	 */
	public SetupSqlFragment()
	{
		// Required empty public constructor
	}

	@SuppressWarnings("boxing")
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		assert view != null;

		// activity
		final Activity activity = getActivity();

		// statuses
		this.pmStatus = view.findViewById(R.id.status_pm);

		// buttons
		this.predicateMatrixButton = view.findViewById(R.id.execute_predicatematrix);
		ImageButton infoPmButton = view.findViewById(R.id.info_pm);


		// pm button
		this.predicateMatrixButton.setOnClickListener(v -> {
			assert activity != null;
			// starting pm task
			try
			{
				final String database = StorageSettings.getDatabasePath(activity);
				final String source = StorageSettings.getSqlSource(activity);
				final String entry = Settings.getPmEntry(activity);
				final String unit = activity.getString(R.string.unit_statement);
				final TaskObserver.Listener listener = new TaskObserver.DialogListener(activity, R.string.status_managing, source + '@' + entry, unit);
				SetupSqlFragment.this.task = new ExecAsyncTask(getActivity(), listener, 1).executeFromArchive(database, source, entry);
			}
			catch (@NonNull final Exception e)
			{
				Log.e(TAG, "While preparing predicatematrix", e);
			}
		});
		infoPmButton.setOnClickListener(v -> {
			assert activity != null;
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
		});

		return view;
	}

	/**
	 * Update status
	 */
	protected void update()
	{
		super.update();

		// activity
		final Activity activity = getActivity();
		assert activity != null;

		// sql zip file
		final String sqlZip = StorageSettings.getSqlSource(activity);
		boolean sqlZipExists = new File(sqlZip).exists();

		// images
		final Drawable okDrawable = ColorUtils.getDrawable(activity, R.drawable.ic_ok);
		ColorUtils.tint(ColorUtils.getColor(activity, R.color.secondaryForeColor), okDrawable);
		final Drawable failDrawable = ColorUtils.getDrawable(activity, R.drawable.ic_fail);

		// status
		final int status = Status.status(activity);
		final boolean existsDatabase = (status & Status.EXISTS) != 0;
		final boolean existsTables = (status & Status.EXISTS_TABLES) != 0;
		// final boolean existsIndexes = (status & org.sqlunet.browser.config.Status.EXISTS_INDEXES) != 0;
		final boolean existsPm = (status & Status.EXISTS_PREDICATEMATRIX) != 0;
		this.pmStatus.setImageDrawable(existsPm ? okDrawable : failDrawable);

		// actions
		this.predicateMatrixButton.setVisibility(sqlZipExists && existsDatabase && existsTables && !existsPm ? View.VISIBLE : View.GONE);
	}
}
