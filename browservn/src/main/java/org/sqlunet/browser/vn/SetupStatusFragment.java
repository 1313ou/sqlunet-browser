package org.sqlunet.browser.vn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

import java.io.File;

import androidx.annotation.NonNull;

/**
 * Setup Status fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupStatusFragment extends org.sqlunet.browser.config.SetupStatusFragment
{
	static private final String TAG = "SetupStatusF";

	// components

	private ImageView imageTextSearchVn;

	private ImageView imageTextSearchPb;

	private ImageButton buttonTextSearchVn;

	private ImageButton buttonTextSearchPb;

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
		this.imageTextSearchVn = view.findViewById(R.id.status_searchtext_vn);
		this.imageTextSearchPb = view.findViewById(R.id.status_searchtext_pb);

		// buttons
		this.buttonTextSearchVn = view.findViewById(R.id.searchtextVnButton);
		this.buttonTextSearchPb = view.findViewById(R.id.searchtextPbButton);

		// click listeners
		this.buttonDb.setOnClickListener(v -> download());
		this.buttonIndexes.setOnClickListener(v -> index());
		this.infoDatabaseButton.setOnClickListener(v -> info());
		this.buttonTextSearchVn.setOnClickListener(v -> {

			int index = getResources().getInteger(R.integer.sql_statement_do_ts_vn_position);
			final Intent intent = new Intent(requireContext(), SetupDatabaseActivity.class);
			intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index);
			startActivityForResult(intent, SetupStatusFragment.REQUEST_MANAGE_CODE + index);
		});
		this.buttonTextSearchPb.setOnClickListener(v -> {

			int index = getResources().getInteger(R.integer.sql_statement_do_ts_pb_position);
			final Intent intent = new Intent(requireContext(), SetupDatabaseActivity.class);
			intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index);
			startActivityForResult(intent, SetupStatusFragment.REQUEST_MANAGE_CODE + index);
		});

		this.infoDatabaseButton.setOnClickListener(v -> {

			final Activity activity = requireActivity();
			final String database = StorageSettings.getDatabasePath(activity);
			final String free = StorageUtils.getFree(activity, database);
			final String source = StorageSettings.getDbDownloadSource(activity);
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
				ColorUtils.tint(ColorUtils.getColor(context, R.color.secondaryForeColor), okDrawable);
				final Drawable failDrawable = ColorUtils.getDrawable(context, org.sqlunet.browser.common.R.drawable.ic_fail);

				final boolean existsTsVn = (status & Status.EXISTS_TS_VN) != 0;
				final boolean existsTsPb = (status & Status.EXISTS_TS_PB) != 0;
				this.imageTextSearchVn.setImageDrawable(existsTsVn ? okDrawable : failDrawable);
				this.buttonTextSearchVn.setVisibility(existsTsVn ? View.GONE : View.VISIBLE);
				this.imageTextSearchPb.setImageDrawable(existsTsPb ? okDrawable : failDrawable);
				this.buttonTextSearchPb.setVisibility(existsTsPb ? View.GONE : View.VISIBLE);
			}
			else
			{
				this.buttonTextSearchVn.setVisibility(View.GONE);
				this.imageTextSearchVn.setImageResource(R.drawable.ic_unknown);
				this.buttonTextSearchPb.setVisibility(View.GONE);
				this.imageTextSearchPb.setImageResource(R.drawable.ic_unknown);
			}
		}
	}
}
