package org.sqlunet.browser.xn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.sqlunet.browser.Info;
import org.sqlunet.browser.R;
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

	private ImageView imagePm;

	private ImageView imageTextSearchWn;

	private ImageView imageTextSearchVn;

	private ImageView imageTextSearchPb;

	private ImageView imageTextSearchFn;

	private ImageButton buttonPm;

	private ImageButton buttonTextSearchWn;

	private ImageButton buttonTextSearchVn;

	private ImageButton buttonTextSearchPb;

	private ImageButton buttonTextSearchFn;

	/*
	 * Swipe refresh layout
	 */
	// private SwipeRefreshLayout swipeRefreshLayout;

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
		assert view != null;

		// images
		this.imageTextSearchFn = (ImageView) view.findViewById(R.id.status_textsearchFn);
		this.imagePm = (ImageView) view.findViewById(R.id.status_predicatematrix);
		this.imageTextSearchWn = (ImageView) view.findViewById(R.id.status_textsearchWn);
		this.imageTextSearchVn = (ImageView) view.findViewById(R.id.status_textsearchVn);
		this.imageTextSearchPb = (ImageView) view.findViewById(R.id.status_textsearchPb);
		this.imageTextSearchFn = (ImageView) view.findViewById(R.id.status_textsearchFn);

		// buttons
		this.buttonPm = (ImageButton) view.findViewById(R.id.predicatematrixButton);
		this.buttonTextSearchWn = (ImageButton) view.findViewById(R.id.textsearchWnButton);
		this.buttonTextSearchVn = (ImageButton) view.findViewById(R.id.textsearchVnButton);
		this.buttonTextSearchPb = (ImageButton) view.findViewById(R.id.textsearchPbButton);
		this.buttonTextSearchFn = (ImageButton) view.findViewById(R.id.textsearchFnButton);
		final ImageButton infoDatabaseButton = (ImageButton) view.findViewById(R.id.info_database);

		// activity
		final Activity activity = getActivity();

		// click listeners
		this.buttonPm.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int index = getResources().getInteger(R.integer.sql_statement_do_predicatematrix_position);
				final Intent intent = new Intent(activity, SetupDatabaseActivity.class);
				intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index);
				startActivityForResult(intent, SetupStatusFragment.REQUEST_MANAGE_CODE + index);
			}
		});

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

		this.buttonTextSearchVn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int index = getResources().getInteger(R.integer.sql_statement_do_ts_vn_position);
				final Intent intent = new Intent(activity, SetupDatabaseActivity.class);
				intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index);
				startActivityForResult(intent, SetupStatusFragment.REQUEST_MANAGE_CODE + index);
			}
		});

		this.buttonTextSearchPb.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int index = getResources().getInteger(R.integer.sql_statement_do_ts_pb_position);
				final Intent intent = new Intent(activity, SetupDatabaseActivity.class);
				intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index);
				startActivityForResult(intent, SetupStatusFragment.REQUEST_MANAGE_CODE + index);
			}
		});

		this.buttonTextSearchFn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int index = getResources().getInteger(R.integer.sql_statement_do_ts_fn_position);
				final Intent intent = new Intent(activity, SetupDatabaseActivity.class);
				intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index);
				startActivityForResult(intent, SetupStatusFragment.REQUEST_MANAGE_CODE + index);
			}
		});

		infoDatabaseButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				final String database = StorageSettings.getDatabasePath(activity);
				final String free = StorageUtils.getFree(getActivity(), database);
				final String source = StorageSettings.getDbDownloadSource(activity);
				final int status = org.sqlunet.browser.config.Status.status(activity);
				final boolean existsDb = (status & org.sqlunet.browser.config.Status.EXISTS) != 0;
				final boolean existsTables = (status & org.sqlunet.browser.config.Status.EXISTS_TABLES) != 0;
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
							getString(R.string.size_expected) + ' ' + getString(R.string.text_search) + ' ' + getString(R.string.wordnet) + '/' + getString(R.string.verbnet) + '/' + getString(R.string.propbank) + '/' + getString(R.string.framenet), getString(R.string.hr_size_textsearch) + " (" + getString(R.string.hr_size_textsearch_wn) + '+' + getString(R.string.hr_size_textsearch_vn) + '+' + getString(R.string.hr_size_textsearch_pb) + '+' + getString(R.string.hr_size_textsearch_fn) + '+' + getString(R.string.hr_size_textsearch) + ')', //
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
							getString(R.string.size_expected) + ' ' + getString(R.string.text_search) + ' ' + getString(R.string.wordnet) + '/' + getString(R.string.verbnet) + '/' + getString(R.string.propbank) + '/' + getString(R.string.framenet), getString(R.string.hr_size_textsearch) + " (" + getString(R.string.hr_size_textsearch_wn) + '+' + getString(R.string.hr_size_textsearch_vn) + '+' + getString(R.string.hr_size_textsearch_pb) + '+' + getString(R.string.hr_size_textsearch_fn) + '+' + getString(R.string.hr_size_textsearch) + ')', //
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
			final boolean existsPm = (status & Status.EXISTS_PREDICATEMATRIX) != 0;
			final boolean existsTsWn = (status & Status.EXISTS_TS_WN) != 0;
			final boolean existsTsVn = (status & Status.EXISTS_TS_VN) != 0;
			final boolean existsTsPb = (status & Status.EXISTS_TS_PB) != 0;
			final boolean existsTsFn = (status & Status.EXISTS_TS_FN) != 0;

			this.imageTextSearchFn.setImageResource(existsTsFn ? R.drawable.ic_ok : R.drawable.ic_fail);
			this.imagePm.setImageResource(existsPm ? R.drawable.ic_ok : R.drawable.ic_fail);
			this.imageTextSearchWn.setImageResource(existsTsWn ? R.drawable.ic_ok : R.drawable.ic_fail);
			this.imageTextSearchVn.setImageResource(existsTsVn ? R.drawable.ic_ok : R.drawable.ic_fail);
			this.imageTextSearchPb.setImageResource(existsTsPb ? R.drawable.ic_ok : R.drawable.ic_fail);
			this.imageTextSearchFn.setImageResource(existsTsFn ? R.drawable.ic_ok : R.drawable.ic_fail);

			this.buttonTextSearchFn.setVisibility(existsTsFn ? View.GONE : View.VISIBLE);
			this.buttonPm.setVisibility(existsPm ? View.GONE : View.VISIBLE);
			this.buttonTextSearchWn.setVisibility(existsTsWn ? View.GONE : View.VISIBLE);
			this.buttonTextSearchVn.setVisibility(existsTsVn ? View.GONE : View.VISIBLE);
			this.buttonTextSearchPb.setVisibility(existsTsPb ? View.GONE : View.VISIBLE);
			this.buttonTextSearchFn.setVisibility(existsTsFn ? View.GONE : View.VISIBLE);
		}
		else
		{
			this.buttonTextSearchFn.setVisibility(View.GONE);
			this.buttonPm.setVisibility(View.GONE);
			this.buttonTextSearchWn.setVisibility(View.GONE);
			this.buttonTextSearchVn.setVisibility(View.GONE);
			this.buttonTextSearchPb.setVisibility(View.GONE);
			this.buttonTextSearchFn.setVisibility(View.GONE);

			this.imageTextSearchFn.setImageResource(R.drawable.ic_unknown);
			this.imagePm.setImageResource(R.drawable.ic_unknown);
			this.imageTextSearchWn.setImageResource(R.drawable.ic_unknown);
			this.imageTextSearchVn.setImageResource(R.drawable.ic_unknown);
			this.imageTextSearchPb.setImageResource(R.drawable.ic_unknown);
			this.imageTextSearchFn.setImageResource(R.drawable.ic_unknown);
		}
	}
}
