package org.sqlunet.browser.wn;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
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
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

import java.io.File;

/**
 * Setup Status fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupStatusFragment extends org.sqlunet.browser.config.SetupStatusFragment
{
	static private final String TAG = "SetupStatusFragment";

	// components

	private ImageView imageTextSearchWn;

	private ImageButton buttonTextSearchWn;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public SetupStatusFragment()
	{
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		assert view != null;

		// images
		this.imageTextSearchWn = view.findViewById(R.id.status_searchtext_wn);

		// buttons
		this.buttonTextSearchWn = view.findViewById(R.id.searchtextWnButton);

		// activity
		final Activity activity = getActivity();
		assert activity != null;

		// click listeners
		this.buttonTextSearchWn.setOnClickListener(v -> {
			int index = getResources().getInteger(R.integer.sql_statement_do_ts_wn_position);
			final Intent intent = new Intent(activity, SetupDatabaseActivity.class);
			intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index);
			startActivityForResult(intent, SetupStatusFragment.REQUEST_MANAGE_CODE + index);
		});

		this.infoDatabaseButton.setOnClickListener(v -> {
			final String database = StorageSettings.getDatabasePath(activity);
			final String free = StorageUtils.getFree(activity, database);
			final String source = StorageSettings.getDbDownloadSource(activity);
			final int status = Status.status(activity);
			final boolean existsDb = (status & Status.EXISTS) != 0;
			final boolean existsTables = (status & Status.EXISTS_TABLES) != 0;
			if (existsDb)
			{
				final long size = new File(database).length();
				final String hrSize = StorageUtils.countToStorageString(size) + " (" + Long.toString(size) + ')';
				Info.info(activity, R.string.title_status, //
						getString(R.string.title_database), database, //
						getString(R.string.title_status), getString(R.string.status_database_exists) + '-' + getString(existsTables ? R.string.status_data_exists : R.string.status_data_not_exists), //
						getString(R.string.title_free), free, //
						getString(R.string.size_expected), getString(R.string.hr_size_sqlunet_db), //
						getString(R.string.size_expected) + ' ' + getString(R.string.text_search), getString(R.string.hr_size_searchtext), //
						getString(R.string.size_expected) + ' ' + getString(R.string.total), getString(R.string.hr_size_db_working_total), //
						getString(R.string.size_actual), hrSize);
			}
			else
			{
				Info.info(activity, R.string.title_download, //
						getString(R.string.title_operation), getString(R.string.info_op_download_database), //
						getString(R.string.title_from), source, //
						getString(R.string.title_database), database, //
						getString(R.string.title_free), free, //
						getString(R.string.size_expected), getString(R.string.hr_size_sqlunet_db), //
						getString(R.string.size_expected) + ' ' + getString(R.string.text_search), getString(R.string.hr_size_searchtext), //
						getString(R.string.size_expected) + ' ' + getString(R.string.total), getString(R.string.hr_size_db_working_total), //
						getString(R.string.title_status), getString(R.string.status_database_not_exists));
			}
		});
		return view;
	}

	// U P D A T E

	/**
	 * Update status
	 */
	protected void update()
	{
		super.update();

		final Activity activity = getActivity();
		assert activity != null;

		final int status = Status.status(activity);
		Log.d(TAG, "STATUS " + Status.toString(status));

		final boolean existsDb = (status & Status.EXISTS) != 0;
		final boolean existsTables = (status & Status.EXISTS_TABLES) != 0;
		if (existsDb && existsTables)
		{
			// images
			final Drawable okDrawable = ColorUtils.getDrawable(activity, R.drawable.ic_ok);
			ColorUtils.tint(ColorUtils.getColor(activity, R.color.secondaryForeColor), okDrawable);
			final Drawable failDrawable = ColorUtils.getDrawable(activity, org.sqlunet.browser.common.R.drawable.ic_fail);

			final boolean existsTsWn = (status & Status.EXISTS_TS_WN) != 0;
			this.imageTextSearchWn.setImageDrawable(existsTsWn ? okDrawable : failDrawable);
			this.buttonTextSearchWn.setVisibility(existsTsWn ? View.GONE : View.VISIBLE);
		}
		else
		{
			this.buttonTextSearchWn.setVisibility(View.GONE);
			this.imageTextSearchWn.setImageResource(R.drawable.ic_unknown);
		}
	}
}
