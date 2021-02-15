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
import org.sqlunet.browser.config.Status;
import org.sqlunet.settings.Settings;

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

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "lifecycle: OnCreate()");

		// clear (some/all) settings on first run of this version
		Settings.clearSettingsOnUpgrade(this);

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
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
		//final Intent intent = new Intent(activity, StatusActivity.class);
		final Intent intent = new Intent(activity, LoadActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
	}

	/**
	 * Rerun entry activity. If a task is running with entry activity running, bring it to the foreground and dispatch onNewIntent(). Otherwise, entry activity is started in new task.
	 *
	 * @param context context
	 */
	static public void reenter(@NonNull final Context context)
	{
		final Intent intent = new Intent(context, EntryActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * Rerun entry activity. Task is cleared before the activity is started. The activity becomes the new root of an otherwise empty task.
	 *
	 * @param context context
	 */
	static public void rerun(@NonNull final Context context)
	{
		final Intent intent = new Intent(context, EntryActivity.class);
		intent.addFlags(/*Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | */Intent.FLAG_ACTIVITY_CLEAR_TASK);
		context.startActivity(intent);
	}
}
