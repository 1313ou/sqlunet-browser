/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.sqlunet.browser.config.LoadActivity;
import org.sqlunet.browser.config.SetupAsset;
import org.sqlunet.browser.config.SetupDatabaseTasks;
import org.sqlunet.browser.config.Status;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageSettings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Entry point activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class EntryActivity extends AppCompatActivity
{
	static private final String TAG = "EntryA";

	public static final int INITIAL_TASK_FLAGS = 0;

	public static final int POST_INITIAL_TASK_FLAGS = 0;

	private static final int BRANCH_FLAGS = 0;

	public static final int REENTER_FLAGS = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "lifecycle: OnCreate()");

		// clear (some/all) settings on first run of this version
		Long build = Settings.isUpgrade(this);
		if (build != null)
		{
			boolean success = SetupDatabaseTasks.deleteDatabase(this, StorageSettings.getDatabasePath(this));
			if (success)
			{
				Log.d(TAG, "Deleted database");
			}
			else
			{
				Log.e(TAG, "Error deleting database");
			}

			org.sqlunet.download.Settings.unrecordDb(this);
			Settings.onUpgrade(this, build);
		}

		// settings
		Settings.updateGlobals(this);

		// clean up assets
		if (Settings.getAssetAutoCleanup(this))
		{
			SetupAsset.disposeAllAssets(this);
		}
	}

	@Override
	protected void onNewIntent(final Intent intent)
	{
		super.onNewIntent(intent);
		Log.d(TAG, "lifecycle: OnNewIntent()");
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d(TAG, "lifecycle: OnResume()");

		dispatch();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Log.d(TAG, "lifecycle: onDestroy()");
	}

	/**
	 * Dispatch, depending on whether the app can run
	 */
	private void dispatch()
	{
		// check hook
		boolean canRun = Status.canRun(getBaseContext());
		if (!canRun)
		{
			branchOffToLoad(this);
			return;
		}

		// switch as per preferred launch mode
		final String clazz = Settings.getLaunchPref(this); // = "org.sqlunet.browser.MainActivity";
		final Intent intent = new Intent();
		intent.setClassName(this, clazz);
		intent.addFlags(POST_INITIAL_TASK_FLAGS);
		startActivity(intent);
		finish();
	}

	/**
	 * Branch off to load activity
	 *
	 * @param activity activity to branch from
	 */
	static public void branchOffToLoadIfCantRun(@NonNull final AppCompatActivity activity)
	{
		boolean canRun = Status.canRun(activity);
		if (!canRun)
		{
			branchOffToLoad(activity);
		}
	}

	/**
	 * Branch off to load activity
	 *
	 * @param activity activity to branch from
	 */
	private static void branchOffToLoad(@NonNull final AppCompatActivity activity)
	{
		final Intent intent = new Intent(activity, LoadActivity.class);
		intent.addFlags(BRANCH_FLAGS);
		activity.startActivity(intent);
	}

	/**
	 * Rerun entry activity. Task is cleared before the activity is started. The activity becomes the new root of an otherwise empty task.
	 *
	 * @param context context
	 */
	static public void rerun(@NonNull final Context context)
	{
		final Intent intent = new Intent(context, EntryActivity.class);
		intent.addFlags(REENTER_FLAGS);
		context.startActivity(intent);
	}
}
