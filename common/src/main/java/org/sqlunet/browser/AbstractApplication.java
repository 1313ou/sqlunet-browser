/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;

import org.sqlunet.browser.common.R;
import org.sqlunet.nightmode.NightMode;
import org.sqlunet.settings.Settings;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import static org.sqlunet.nightmode.NightMode.nightModeToString;

abstract public class AbstractApplication extends Application
{
	static private final String LOG = "AApplication";

	@Override
	public void onCreate()
	{
		super.onCreate();
		Settings.initializeDisplayPrefs(this);
	}

	@Override
	public void onConfigurationChanged(@NonNull final Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		Context newContext = NightMode.wrapContext(this, newConfig, R.style.MyTheme);
		Log.d(LOG, "onConfigurationChanged: " + nightModeToString(this) + " -> " + nightModeToString(newContext));
		setAllColorsFromResources(newContext);
	}

	abstract public void setAllColorsFromResources(@NonNull final Context newContext);

	// T A S K S

	@RequiresApi(api = Build.VERSION_CODES.M)
	public void dumpTasks()
	{
		dumpTasks(this.getBaseContext());
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	static public void dumpTasks(@NonNull Context context)
	{
		final ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		final List<ActivityManager.AppTask> tasks = manager.getAppTasks();
		for (ActivityManager.AppTask task : tasks)
		{
			ActivityManager.RecentTaskInfo info = task.getTaskInfo();
			Log.i("task", info.baseActivity.toString());
		}
	}
}
