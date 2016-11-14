package org.sqlunet.browser;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.sqlunet.browser.config.DownloadActivity;
import org.sqlunet.browser.config.ManageActivity;
import org.sqlunet.browser.config.ManageFragment;
import org.sqlunet.browser.config.SettingsActivity;
import org.sqlunet.browser.config.SetupActivity;
import org.sqlunet.browser.config.SetupSqlActivity;
import org.sqlunet.browser.config.Status;
import org.sqlunet.browser.config.StorageActivity;
import org.sqlunet.settings.Settings;

/**
 * Status fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StatusFragment extends Fragment
{
	static private final String TAG = "StatusFragment";

	// codes

	static private final int REQUEST_DOWNLOAD_CODE = 0xDDDD;

	static private final int REQUEST_MANAGE_CODE = 0xAAA0;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public StatusFragment()
	{
		//
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// layout
		return inflater.inflate(R.layout.fragment_status, container, false);
	}


	@Override
	public void onResume()
	{
		super.onResume();

		// view
		final View view = getView();

		// status
		final ImageView db = (ImageView) view.findViewById(R.id.status_database);
		final ImageButton buttonDb = (ImageButton) view.findViewById(R.id.databaseButton);
		final ImageButton buttonIndexes = (ImageButton) view.findViewById(R.id.indexesButton);
		final ImageButton buttonPm = (ImageButton) view.findViewById(R.id.predicatematrixButton);
		final ImageButton buttonTextSearchWn = (ImageButton) view.findViewById(R.id.textsearchWnButton);
		final ImageButton buttonTextSearchVn = (ImageButton) view.findViewById(R.id.textsearchVnButton);
		final ImageButton buttonTextSearchPb = (ImageButton) view.findViewById(R.id.textsearchPbButton);
		final ImageButton buttonTextSearchFn = (ImageButton) view.findViewById(R.id.textsearchFnButton);

		// click listeners
		buttonDb.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusFragment.this.getActivity(), DownloadActivity.class);
				startActivityForResult(intent, StatusFragment.REQUEST_DOWNLOAD_CODE);
			}
		});

		buttonIndexes.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusFragment.this.getActivity(), ManageActivity.class);
				intent.putExtra(ManageFragment.ARG, Status.DO_INDEXES);
				startActivityForResult(intent, StatusFragment.REQUEST_MANAGE_CODE + Status.DO_INDEXES);
			}
		});

		buttonPm.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusFragment.this.getActivity(), ManageActivity.class);
				intent.putExtra(ManageFragment.ARG, Status.DO_PM);
				startActivityForResult(intent, StatusFragment.REQUEST_MANAGE_CODE + Status.DO_PM);
			}
		});

		buttonTextSearchWn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusFragment.this.getActivity(), ManageActivity.class);
				intent.putExtra(ManageFragment.ARG, Status.DO_TS_WN);
				startActivityForResult(intent, StatusFragment.REQUEST_MANAGE_CODE + Status.DO_TS_WN);
			}
		});

		buttonTextSearchVn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusFragment.this.getActivity(), ManageActivity.class);
				intent.putExtra(ManageFragment.ARG, Status.DO_TS_VN);
				startActivityForResult(intent, StatusFragment.REQUEST_MANAGE_CODE + Status.DO_TS_WN);
			}
		});

		buttonTextSearchPb.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusFragment.this.getActivity(), ManageActivity.class);
				intent.putExtra(ManageFragment.ARG, Status.DO_TS_PB);
				startActivityForResult(intent, StatusFragment.REQUEST_MANAGE_CODE + Status.DO_TS_PB);
			}
		});

		buttonTextSearchFn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusFragment.this.getActivity(), ManageActivity.class);
				intent.putExtra(ManageFragment.ARG, Status.DO_TS_FN);
				startActivityForResult(intent, StatusFragment.REQUEST_MANAGE_CODE + Status.DO_TS_FN);
			}
		});

		final int status = Status.status(StatusFragment.this.getActivity());
		if (status != 0)
		{
			db.setImageResource(R.drawable.ic_ok);
			buttonDb.setVisibility(View.GONE);

			final boolean existsIndexes = (status & Status.EXISTS_INDEXES) != 0;
			final boolean existsPm = (status & Status.EXISTS_PM) != 0;
			final boolean existsTsWn = (status & Status.EXISTS_TS_WN) != 0;
			final boolean existsTsVn = (status & Status.EXISTS_TS_VN) != 0;
			final boolean existsTsPb = (status & Status.EXISTS_TS_PB) != 0;
			final boolean existsTsFn = (status & Status.EXISTS_TS_FN) != 0;

			final ImageView imageIndexes = (ImageView) view.findViewById(R.id.status_indexes);
			final ImageView imagePm = (ImageView) view.findViewById(R.id.status_predicatematrix);
			final ImageView imageTextSearchWn = (ImageView) view.findViewById(R.id.status_textsearchWn);
			final ImageView imageTextSearchVn = (ImageView) view.findViewById(R.id.status_textsearchVn);
			final ImageView imageTextSearchPb = (ImageView) view.findViewById(R.id.status_textsearchPb);
			final ImageView imageTextSearchFn = (ImageView) view.findViewById(R.id.status_textsearchFn);
			imageIndexes.setImageResource(existsIndexes ? R.drawable.ic_ok : R.drawable.ic_fail);
			imagePm.setImageResource(existsPm ? R.drawable.ic_ok : R.drawable.ic_fail);
			imageTextSearchWn.setImageResource(existsTsWn ? R.drawable.ic_ok : R.drawable.ic_fail);
			imageTextSearchVn.setImageResource(existsTsVn ? R.drawable.ic_ok : R.drawable.ic_fail);
			imageTextSearchPb.setImageResource(existsTsPb ? R.drawable.ic_ok : R.drawable.ic_fail);
			imageTextSearchFn.setImageResource(existsTsFn ? R.drawable.ic_ok : R.drawable.ic_fail);

			buttonIndexes.setVisibility(existsIndexes ? View.GONE : View.VISIBLE);
			buttonPm.setVisibility(existsPm ? View.GONE : View.VISIBLE);
			buttonTextSearchWn.setVisibility(existsTsWn ? View.GONE : View.VISIBLE);
			buttonTextSearchVn.setVisibility(existsTsVn ? View.GONE : View.VISIBLE);
			buttonTextSearchPb.setVisibility(existsTsPb ? View.GONE : View.VISIBLE);
			buttonTextSearchFn.setVisibility(existsTsFn ? View.GONE : View.VISIBLE);
		}
		else
		{
			db.setImageResource(R.drawable.ic_fail);
			buttonDb.setVisibility(View.VISIBLE);
		}
	}

	// S P E C I F I C R E T U R N S

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent returnIntent)
	{
		// handle selection of input by other activity which returns selected input
		switch (requestCode)
		{
			case REQUEST_DOWNLOAD_CODE:
				Log.d(TAG, "result=" + resultCode);
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
		inflater.inflate(R.menu.status, menu);
	}

	// A C T I O N H A N D L I N G

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		Intent intent;

		// handle item selection
		switch (item.getItemId())
		{
			case android.R.id.home:
				intent = StatusFragment.this.getActivity().getIntent();
				boolean cantRun = intent.getBooleanExtra(Status.CANTRUN, false);
				if (!cantRun)
				{
					return super.onOptionsItemSelected(item);
				}
				break;

			case R.id.action_settings:
				intent = new Intent(StatusFragment.this.getActivity(), SettingsActivity.class);
				break;
			case R.id.action_status:
				intent = new Intent(StatusFragment.this.getActivity(), StatusActivity.class);
				break;
			case R.id.action_setup:
				intent = new Intent(StatusFragment.this.getActivity(), SetupActivity.class);
				break;
			case R.id.action_setup_sql:
				intent = new Intent(StatusFragment.this.getActivity(), SetupSqlActivity.class);
				break;
			case R.id.action_storage:
				intent = new Intent(StatusFragment.this.getActivity(), StorageActivity.class);
				break;
			case R.id.action_manage:
				intent = new Intent(StatusFragment.this.getActivity(), ManageActivity.class);
				break;
			case R.id.action_appsettings:
				Settings.applicationSettings(StatusFragment.this.getActivity(), "org.sqlunet.browser");
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

		// start activity
		startActivity(intent);
		return true;
	}
}
