package org.sqlunet.browser.wn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

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
	 * Swipe refresh layout
	 */
	private SwipeRefreshLayout swipeRefreshLayout;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public SetupStatusFragment()
	{
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		// images
		this.imageTextSearchWn = (ImageView) view.findViewById(R.id.status_textsearchWn);

		// buttons
		this.buttonTextSearchWn = (ImageButton) view.findViewById(R.id.textsearchWnButton);

		// activity
		final Activity activity = getActivity();

		// click listeners
		this.buttonTextSearchWn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int index = getResources().getInteger(R.integer.sql_statement_do_ts_wn_position);
				final Intent intent = new Intent(activity, SetupDatabaseActivity.class);
				intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index);
				startActivityForResult(intent, SetupStatusFragment.REQUEST_MANAGE_CODE + index);
			}
		});

		this.infoDatabaseButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				final String database = StorageSettings.getDatabasePath(activity);
				final String free = StorageUtils.getFree(getActivity(), database);
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
							getString(R.string.title_status), getString(R.string.status_database_exists), //
							getString(R.string.title_status), getString(existsTables ? R.string.status_data_exists : R.string.status_data_not_exists), //
							getString(R.string.title_free), free, //
							getString(R.string.size_expected), getString(R.string.hr_size_sqlunet_db), //
							getString(R.string.size_expected) + ' ' + getString(R.string.text_search) + ' ' + getString(R.string.hr_size_textsearch), //
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
							getString(R.string.size_expected) + ' ' + getString(R.string.text_search) + ' ' + getString(R.string.hr_size_textsearch) + ')', //
							getString(R.string.size_expected) + ' ' + getString(R.string.total), getString(R.string.hr_size_db_working_total), //
							getString(R.string.title_status), getString(R.string.status_database_not_exists));
				}
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
		final int status = Status.status(activity);
		Log.d(TAG, "STATUS " + Status.toString(status));

		final boolean existsDb = (status & Status.EXISTS) != 0;
		final boolean existsTables = (status & Status.EXISTS_TABLES) != 0;
		if (existsDb && existsTables)
		{
			final boolean existsTsWn = (status & Status.EXISTS_TS_WN) != 0;
			this.imageTextSearchWn.setImageResource(existsTsWn ? R.drawable.ic_ok : R.drawable.ic_fail);
			this.buttonTextSearchWn.setVisibility(existsTsWn ? View.GONE : View.VISIBLE);
		}
		else
		{
			this.buttonTextSearchWn.setVisibility(View.GONE);
			this.imageTextSearchWn.setImageResource(R.drawable.ic_unknown);
		}
	}
}
