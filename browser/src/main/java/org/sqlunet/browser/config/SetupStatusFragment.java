package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.sqlunet.browser.Info;
import org.sqlunet.browser.R;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

/**
 * Status fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupStatusFragment extends Fragment
{
	static private final String TAG = "SetupStatusFragment";

	// codes

	static private final int REQUEST_DOWNLOAD_CODE = 0xDDDD;

	static private final int REQUEST_MANAGE_CODE = 0xAAA0;

	// components

	private ImageView imageDb;

	private ImageView imageIndexes;

	private ImageView imagePm;

	private ImageView imageTextSearchWn;

	private ImageView imageTextSearchVn;

	private ImageView imageTextSearchPb;

	private ImageView imageTextSearchFn;

	private ImageButton buttonDb;

	private ImageButton buttonIndexes;

	private ImageButton buttonPm;

	private ImageButton buttonTextSearchWn;

	private ImageButton buttonTextSearchVn;

	private ImageButton buttonTextSearchPb;

	private ImageButton buttonTextSearchFn;

	private ImageButton infoDatabaseButton;

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
		setHasOptionsMenu(true);

		// view
		final View view = inflater.inflate(R.layout.fragment_status, container, false);

		// images
		this.imageDb = (ImageView) view.findViewById(R.id.status_database);
		this.imageIndexes = (ImageView) view.findViewById(R.id.status_indexes);
		this.imagePm = (ImageView) view.findViewById(R.id.status_predicatematrix);
		this.imageTextSearchWn = (ImageView) view.findViewById(R.id.status_textsearchWn);
		this.imageTextSearchVn = (ImageView) view.findViewById(R.id.status_textsearchVn);
		this.imageTextSearchPb = (ImageView) view.findViewById(R.id.status_textsearchPb);
		this.imageTextSearchFn = (ImageView) view.findViewById(R.id.status_textsearchFn);

		// buttons
		this.buttonDb = (ImageButton) view.findViewById(R.id.databaseButton);
		this.buttonIndexes = (ImageButton) view.findViewById(R.id.indexesButton);
		this.buttonPm = (ImageButton) view.findViewById(R.id.predicatematrixButton);
		this.buttonTextSearchWn = (ImageButton) view.findViewById(R.id.textsearchWnButton);
		this.buttonTextSearchVn = (ImageButton) view.findViewById(R.id.textsearchVnButton);
		this.buttonTextSearchPb = (ImageButton) view.findViewById(R.id.textsearchPbButton);
		this.buttonTextSearchFn = (ImageButton) view.findViewById(R.id.textsearchFnButton);
		this.infoDatabaseButton = (ImageButton) view.findViewById(R.id.info_database);

		// activity
		final Activity activity = getActivity();

		// click listeners
		this.buttonDb.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(activity, DownloadActivity.class);
				startActivityForResult(intent, SetupStatusFragment.REQUEST_DOWNLOAD_CODE);
			}
		});

		this.buttonIndexes.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(activity, SetupDatabaseActivity.class);
				intent.putExtra(SetupDatabaseFragment.ARG, Status.DO_INDEXES);
				startActivityForResult(intent, SetupStatusFragment.REQUEST_MANAGE_CODE + Status.DO_INDEXES);
			}
		});

		this.buttonPm.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(activity, SetupDatabaseActivity.class);
				intent.putExtra(SetupDatabaseFragment.ARG, Status.DO_PM);
				startActivityForResult(intent, SetupStatusFragment.REQUEST_MANAGE_CODE + Status.DO_PM);
			}
		});

		this.buttonTextSearchWn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(activity, SetupDatabaseActivity.class);
				intent.putExtra(SetupDatabaseFragment.ARG, Status.DO_TS_WN);
				startActivityForResult(intent, SetupStatusFragment.REQUEST_MANAGE_CODE + Status.DO_TS_WN);
			}
		});

		this.buttonTextSearchVn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(activity, SetupDatabaseActivity.class);
				intent.putExtra(SetupDatabaseFragment.ARG, Status.DO_TS_VN);
				startActivityForResult(intent, SetupStatusFragment.REQUEST_MANAGE_CODE + Status.DO_TS_WN);
			}
		});

		this.buttonTextSearchPb.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(activity, SetupDatabaseActivity.class);
				intent.putExtra(SetupDatabaseFragment.ARG, Status.DO_TS_PB);
				startActivityForResult(intent, SetupStatusFragment.REQUEST_MANAGE_CODE + Status.DO_TS_PB);
			}
		});

		this.buttonTextSearchFn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(activity, SetupDatabaseActivity.class);
				intent.putExtra(SetupDatabaseFragment.ARG, Status.DO_TS_FN);
				startActivityForResult(intent, SetupStatusFragment.REQUEST_MANAGE_CODE + Status.DO_TS_FN);
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
				Info.info(activity, R.string.title_download, //
						getString(R.string.title_from), source, //
						getString(R.string.title_database), database, //
						getString(R.string.title_free), free);
			}
		});

		// swipe refresh layout
		this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
		this.swipeRefreshLayout.setColorSchemeResources(R.color.swipedown1_color, R.color.swipedown2_color);
		this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				update();

				// stop the refreshing indicator
				SetupStatusFragment.this.swipeRefreshLayout.setRefreshing(false);
			}
		});
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		update();
	}

	// U P D A T E

	/**
	 * Update status
	 */
	private void update()
	{
		// activity
		final Activity activity = getActivity();

		final int status = Status.status(activity);
		Log.d(TAG, "STATUS " + Status.toString(status));
		final boolean existsDb = (status & Status.EXISTS) != 0;
		final boolean existsTables = (status & Status.EXISTS_TABLES) != 0;
		if (existsTables)
		{
			this.imageDb.setImageResource(R.drawable.ic_ok);
			this.buttonDb.setVisibility(View.GONE);

			final boolean existsIndexes = (status & Status.EXISTS_INDEXES) != 0;
			final boolean existsPm = (status & Status.EXISTS_PREDICATEMATRIX) != 0;
			final boolean existsTsWn = (status & Status.EXISTS_TS_WN) != 0;
			final boolean existsTsVn = (status & Status.EXISTS_TS_VN) != 0;
			final boolean existsTsPb = (status & Status.EXISTS_TS_PB) != 0;
			final boolean existsTsFn = (status & Status.EXISTS_TS_FN) != 0;

			this.imageIndexes.setImageResource(existsIndexes ? R.drawable.ic_ok : R.drawable.ic_fail);
			this.imagePm.setImageResource(existsPm ? R.drawable.ic_ok : R.drawable.ic_fail);
			this.imageTextSearchWn.setImageResource(existsTsWn ? R.drawable.ic_ok : R.drawable.ic_fail);
			this.imageTextSearchVn.setImageResource(existsTsVn ? R.drawable.ic_ok : R.drawable.ic_fail);
			this.imageTextSearchPb.setImageResource(existsTsPb ? R.drawable.ic_ok : R.drawable.ic_fail);
			this.imageTextSearchFn.setImageResource(existsTsFn ? R.drawable.ic_ok : R.drawable.ic_fail);

			this.buttonIndexes.setVisibility(existsIndexes ? View.GONE : View.VISIBLE);
			this.buttonPm.setVisibility(existsPm ? View.GONE : View.VISIBLE);
			this.buttonTextSearchWn.setVisibility(existsTsWn ? View.GONE : View.VISIBLE);
			this.buttonTextSearchVn.setVisibility(existsTsVn ? View.GONE : View.VISIBLE);
			this.buttonTextSearchPb.setVisibility(existsTsPb ? View.GONE : View.VISIBLE);
			this.buttonTextSearchFn.setVisibility(existsTsFn ? View.GONE : View.VISIBLE);
		}
		else
		{
			this.imageDb.setImageResource(R.drawable.ic_fail);
			this.buttonDb.setVisibility(View.VISIBLE);

			this.buttonIndexes.setVisibility(View.GONE);
			this.buttonPm.setVisibility(View.GONE);
			this.buttonTextSearchWn.setVisibility(View.GONE);
			this.buttonTextSearchVn.setVisibility(View.GONE);
			this.buttonTextSearchPb.setVisibility(View.GONE);
			this.buttonTextSearchFn.setVisibility(View.GONE);

			this.imageIndexes.setImageResource(R.drawable.ic_unknown);
			this.imagePm.setImageResource(R.drawable.ic_unknown);
			this.imageTextSearchWn.setImageResource(R.drawable.ic_unknown);
			this.imageTextSearchVn.setImageResource(R.drawable.ic_unknown);
			this.imageTextSearchPb.setImageResource(R.drawable.ic_unknown);
			this.imageTextSearchFn.setImageResource(R.drawable.ic_unknown);
		}
	}

	// R E T U R N S

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent returnIntent)
	{
		// handle selection of input by other activity which returns selected input
		switch (requestCode)
		{
			case REQUEST_DOWNLOAD_CODE:
				boolean success = resultCode == Activity.RESULT_OK;
				Log.d(TAG, "Download " + (success ? "succeeded" : "failed")); ////
				Toast.makeText(getActivity(), success ? R.string.title_download_complete : R.string.title_download_failed, Toast.LENGTH_SHORT).show();
				update();
				break;
			default:
				break;
		}
		super.onActivityResult(requestCode, resultCode, returnIntent);
	}

	// M E N U

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		inflater.inflate(R.menu.status, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		final Context context = getActivity();

		// handle item selection
		switch (item.getItemId())
		{
			case R.id.action_refresh:
				/// make sure that the SwipeRefreshLayout is displaying its refreshing indicator
				if (!this.swipeRefreshLayout.isRefreshing())
				{
					this.swipeRefreshLayout.setRefreshing(true);
				}
				update();

				// stop the refreshing indicator
				this.swipeRefreshLayout.setRefreshing(false);
				break;

			default:
				return false;
		}
		return true;
	}
}
