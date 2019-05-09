package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import org.sqlunet.browser.ColorUtils;
import org.sqlunet.browser.Info;
import org.sqlunet.browser.MainActivity;
import org.sqlunet.browser.common.R;
import org.sqlunet.download.DownloadActivity;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static org.sqlunet.download.BaseDownloadFragment.DOWNLOAD_FROM_ARG;
import static org.sqlunet.download.BaseDownloadFragment.DOWNLOAD_TO_ARG;

/**
 * Status fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupStatusFragment extends Fragment implements Updatable
{
	static private final String TAG = "SetupStatusFragment";

	// codes

	private static final int REQUEST_DOWNLOAD_CODE = 0xDDDD;

	static protected final int REQUEST_MANAGE_CODE = 0xAAA0;

	// components

	private ImageView imageDb;

	private ImageView imageIndexes;

	protected ImageButton buttonDb;

	protected ImageButton buttonIndexes;

	protected ImageButton infoDatabaseButton;

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

	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);

		// view
		final View view = inflater.inflate(R.layout.fragment_status, container, false);

		// images
		this.imageDb = view.findViewById(R.id.status_database);
		this.imageIndexes = view.findViewById(R.id.status_indexes);

		// buttons
		this.buttonDb = view.findViewById(R.id.databaseButton);
		this.buttonIndexes = view.findViewById(R.id.indexesButton);
		this.infoDatabaseButton = view.findViewById(R.id.info_database);

		// swipe refresh layout
		this.swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
		this.swipeRefreshLayout.setColorSchemeResources(R.color.swipe_down_1_color, R.color.swipe_down_2_color);
		this.swipeRefreshLayout.setOnRefreshListener(() -> {
			update();

			// stop the refreshing indicator
			SetupStatusFragment.this.swipeRefreshLayout.setRefreshing(false);
		});
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		update();
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		super.onHiddenChanged(hidden);

		// If we are becoming visible
		if (!hidden)
		{
			update();
		}
	}

	protected void download()
	{
		final Context context = requireContext();
		final Intent intent = new Intent(context, DownloadActivity.class);
		intent.putExtra(DOWNLOAD_FROM_ARG, StorageSettings.getDbDownloadSource(context));
		intent.putExtra(DOWNLOAD_TO_ARG, StorageSettings.getDbDownloadTarget(context));
		startActivityForResult(intent, SetupStatusFragment.REQUEST_DOWNLOAD_CODE);
	}

	protected void index()
	{
		int index = getResources().getInteger(R.integer.sql_statement_do_indexes_position);
		final Intent intent = new Intent(requireContext(), SetupDatabaseActivity.class);
		intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index);
		startActivityForResult(intent, SetupStatusFragment.REQUEST_MANAGE_CODE + index);
	}

	protected void info()
	{
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
					getString(R.string.title_status), getString(R.string.status_database_exists), //
					getString(R.string.title_status), getString(existsTables ? R.string.status_data_exists : R.string.status_data_not_exists), //
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
					getString(R.string.size_expected) + ' ' + getString(R.string.text_search), getString(R.string.hr_size_searchtext), //
					getString(R.string.size_expected) + ' ' + getString(R.string.total), getString(R.string.hr_size_db_working_total), //
					getString(R.string.title_status), getString(R.string.status_database_not_exists));
		}
	}

	// U P D A T E

	/**
	 * Update status
	 */
	@Override
	public void update()
	{
		final Context context = getContext();
		if (context != null)
		{
			final int status = Status.status(context);
			Log.d(TAG, "STATUS " + Status.toString(status));

			// images
			final Drawable okDrawable = ColorUtils.getDrawable(context, R.drawable.ic_ok);
			ColorUtils.tint(ColorUtils.getColor(context, R.color.secondaryForeColor), okDrawable);
			final Drawable failDrawable = ColorUtils.getDrawable(context, R.drawable.ic_fail);

			final boolean existsDb = (status & Status.EXISTS) != 0;
			final boolean existsTables = (status & Status.EXISTS_TABLES) != 0;
			if (existsDb && existsTables)
			{
				this.imageDb.setImageDrawable(okDrawable);
				this.buttonDb.setVisibility(View.GONE);

				final boolean existsIndexes = (status & Status.EXISTS_INDEXES) != 0;

				this.imageIndexes.setImageDrawable(existsIndexes ? okDrawable : failDrawable);
				this.buttonIndexes.setVisibility(existsIndexes ? View.GONE : View.VISIBLE);
			}
			else
			{
				this.imageDb.setImageDrawable(failDrawable);
				this.buttonDb.setVisibility(View.VISIBLE);

				this.buttonIndexes.setVisibility(View.GONE);
				this.imageIndexes.setImageResource(R.drawable.ic_unknown);
			}
		}
	}

	// R E T U R N S

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent returnIntent)
	{
		// handle selection of input by other activity which returns selected input
		if (requestCode == REQUEST_DOWNLOAD_CODE)
		{
			boolean success = resultCode == Activity.RESULT_OK;
			Log.d(TAG, "Download " + (success ? "succeeded" : "failed")); ////
			update();
			if (success)
			{
				final Context context = requireContext();

				Toast.makeText(context, R.string.title_download_complete, Toast.LENGTH_SHORT).show();

				final Intent intent = new Intent(context, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				final Activity activity = getActivity();
				if (activity != null)
				{
					activity.finish();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, returnIntent);
	}

	// M E N U

	@Override
	public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull final MenuInflater inflater)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		inflater.inflate(R.menu.status, menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		// handle item selection
		int i = item.getItemId();
		if (i == R.id.action_refresh)
		{
			// make sure that the SwipeRefreshLayout is displaying its refreshing indicator
			if (!this.swipeRefreshLayout.isRefreshing())
			{
				this.swipeRefreshLayout.setRefreshing(true);
			}
			update();

			// stop the refreshing indicator
			this.swipeRefreshLayout.setRefreshing(false);
		}
		else
		{
			return false;
		}
		return true;
	}
}
