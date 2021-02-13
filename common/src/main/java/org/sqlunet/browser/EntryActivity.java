/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

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
	// static private final String TAG = "EntryA";

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// clear (some/all) settings on first run of this version
		Settings.clearSettingsOnUpgrade(this);

		// settings
		Settings.updateGlobals(this);

		// clean up assets
		if (Settings.getAssetAutoCleanup(this))
		{
			SetupAsset.disposeAllAssets(this);
		}

		// dispatch
		dispatch();
	}

	@Override
	protected void onNewIntent(final Intent intent)
	{
		super.onNewIntent(intent);

		// common
		dispatch();
	}

	private void dispatch()
	{
		// check hook
		boolean canRun = Status.canRun(getBaseContext());
		if (!canRun)
		{
			forkOff(this);
			return;
		}

		// switch as per preferred launch mode
		final String clazz = Settings.getLaunchPref(this); // = "org.sqlunet.browser.MainActivity";
		final Intent intent = new Intent();
		intent.setClassName(this, clazz);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(Status.CANTRUN, false);
		startActivity(intent);
		finish();
	}

	static public void forkOffIfCantRun(@NonNull final AppCompatActivity activity)
	{
		boolean canRun;
		final Intent currentIntent = activity.getIntent();
		final Bundle extras = currentIntent.getExtras();
		boolean checked = extras != null && extras.containsKey(Status.CANTRUN);
		if (checked)
		{
			canRun = !currentIntent.getBooleanExtra(Status.CANTRUN, true);
		}
		else
		{
			// check now
			canRun = Status.canRun(activity);
		}
		if (!canRun)
		{
			forkOff(activity);
			// return;
		}
	}

	private static void forkOff(@NonNull final AppCompatActivity activity)
	{
		//final Intent intent = new Intent(activity, StatusActivity.class);
		final Intent intent = new Intent(activity, AssetActivity.class);
		intent.putExtra(Status.CANTRUN, true);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
		activity.finish();
	}

	static public void reenter(@NonNull final Context context)
	{
		final Intent intent = new Intent(context, EntryActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	static public void rerun(@NonNull final Context context)
	{
		final Intent intent = new Intent(context, EntryActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
