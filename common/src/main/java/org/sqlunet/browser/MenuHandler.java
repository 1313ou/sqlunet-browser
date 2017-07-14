package org.sqlunet.browser;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import org.sqlunet.browser.common.R;
import org.sqlunet.browser.config.SettingsActivity;
import org.sqlunet.browser.config.SetupActivity;
import org.sqlunet.browser.config.StorageActivity;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.settings.Settings;
import org.sqlunet.support.DonateActivity;
import org.sqlunet.support.OtherActivity;

/**
 * Main activity stub
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class MenuHandler
{
	/**
	 * Dispatch menu item action
	 *
	 * @param activity activity
	 * @param item     menu item
	 * @return true if processed/consumed
	 */
	static public boolean menuDispatch(final Activity activity, final MenuItem item)
	{
		Intent intent;

		// handle item selection
		int i = item.getItemId();
		if (i == R.id.action_settings)
		{
			intent = new Intent(activity, SettingsActivity.class);
		}
		else if (i == R.id.action_storage)
		{
			// database
			intent = new Intent(activity, StorageActivity.class);
		}
		else if (i == R.id.action_status)
		{
			// database
			intent = new Intent(activity, StatusActivity.class);
		}
		else if (i == R.id.action_setup)
		{
			// database
			intent = new Intent(activity, SetupActivity.class);
		}
		else if (i == R.id.action_clear_sql)
		{
			// SQLs
			BaseProvider.buffer.clear();
			return true;
		}
		else if (i == R.id.action_help)
		{
			// guide
			intent = new Intent(activity, HelpActivity.class);
		}
		else if (i == R.id.action_about)
		{
			// about
			intent = new Intent(activity, AboutActivity.class);
		}
		else if (i == R.id.action_donate)
		{
			// support
			intent = new Intent(activity, DonateActivity.class);
		}
		else if (i == R.id.action_other)
		{
			// about
			intent = new Intent(activity, OtherActivity.class);
		}
		else if (i == R.id.action_quit)
		{
			// lifecycle
			activity.finish();
			return true;
		}
		else if (i == R.id.action_appsettings)
		{
			// settings
			final String appId = activity.getPackageName();
			Settings.applicationSettings(activity, appId);
			return true;
		}
		else
		{
			return false;
		}

		// start activity
		activity.startActivity(intent);
		return true;
	}
}
