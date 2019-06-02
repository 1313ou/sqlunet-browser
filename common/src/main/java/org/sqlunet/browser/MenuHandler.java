/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.SearchView;

import org.sqlunet.browser.common.R;
import org.sqlunet.browser.config.SettingsActivity;
import org.sqlunet.browser.config.SetupActivity;
import org.sqlunet.browser.config.StorageActivity;
import org.sqlunet.download.FileDataDownloader;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.support.DonateActivity;
import org.sqlunet.support.OtherActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
	static public boolean menuDispatch(@NonNull final AppCompatActivity activity, @NonNull final MenuItem item)
	{
		Intent intent;

		// handle item selection
		int i = item.getItemId();
		if (i == R.id.action_main)
		{
			intent = new Intent(activity, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		else if (i == R.id.action_settings)
		{
			intent = new Intent(activity, SettingsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		else if (i == R.id.action_storage)
		{
			intent = new Intent(activity, StorageActivity.class);
		}
		else if (i == R.id.action_status)
		{
			intent = new Intent(activity, StatusActivity.class);
		}
		else if (i == R.id.action_update)
		{
			FileDataDownloader.start(activity, activity.getResources().getString(R.string.pref_default_download_dbfile), StorageSettings.getDbDownloadSource(activity), StorageSettings.getDatabasePath(activity), StorageSettings.getCacheDir(activity));
			return true;
		}
		else if (i == R.id.action_setup)
		{
			intent = new Intent(activity, SetupActivity.class);
		}
		else if (i == R.id.action_clear_sql)
		{
			BaseProvider.buffer.clear();
			return true;
		}
		else if (i == R.id.action_help)
		{
			intent = new Intent(activity, HelpActivity.class);
		}
		else if (i == R.id.action_about)
		{
			intent = new Intent(activity, AboutActivity.class);
		}
		else if (i == R.id.action_searchinfo)
		{
			final SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
			final SearchableInfo info = searchManager.getSearchableInfo(activity.getComponentName());
			final String authority = info.getSuggestAuthority();
			final String path = info.getSuggestPath();
			final String pack = info.getSuggestPackage();
			final String message = pack + '\n' + authority + '/' + path;

			final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
			alert.setTitle(R.string.action_searchinfo);
			alert.setMessage(message);
			alert.setNegativeButton(R.string.action_dismiss, (dialog, whichButton) -> {
				// canceled.
			});
			alert.show();
			return true;
		}
		else if (i == R.id.action_donate)
		{
			intent = new Intent(activity, DonateActivity.class);
		}
		else if (i == R.id.action_other)
		{
			intent = new Intent(activity, OtherActivity.class);
		}
		else if (i == R.id.action_quit)
		{
			activity.finish();
			return true;
		}
		else if (i == R.id.action_appsettings)
		{
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
