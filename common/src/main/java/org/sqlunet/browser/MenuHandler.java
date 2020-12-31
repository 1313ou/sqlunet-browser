/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;

import com.bbou.donate.DonateActivity;
import com.bbou.others.OthersActivity;
import com.bbou.rate.AppRate;

import org.sqlunet.browser.common.R;
import org.sqlunet.browser.config.DiagnosticsActivity;
import org.sqlunet.browser.config.SettingsActivity;
import org.sqlunet.browser.config.SetupActivity;
import org.sqlunet.browser.config.SetupAsset;
import org.sqlunet.browser.config.SetupFileActivity;
import org.sqlunet.browser.config.SetupFileFragment;
import org.sqlunet.browser.config.StorageActivity;
import org.sqlunet.download.FileDataDownloader;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageSettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

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
		final int itemId = item.getItemId();
		if (itemId == R.id.action_main)
		{
			intent = new Intent(activity, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		else if (itemId == R.id.action_settings)
		{
			intent = new Intent(activity, SettingsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		else if (itemId == R.id.action_clear_settings)
		{
			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
			final SharedPreferences.Editor edit = prefs.edit();
			edit.clear().apply();
			intent = new Intent(activity, SettingsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		else if (itemId == R.id.action_storage)
		{
			intent = new Intent(activity, StorageActivity.class);
		}
		else if (itemId == R.id.action_status)
		{
			intent = new Intent(activity, StatusActivity.class);
		}
		else if (itemId == R.id.action_diagnostics)
		{
			intent = new Intent(activity, DiagnosticsActivity.class);
		}
		else if (itemId == R.id.action_drop)
		{
			intent = new Intent(activity, SetupFileActivity.class);
			intent.putExtra(SetupFileFragment.ARG, SetupFileFragment.Operation.DROP.toString());
		}
		else if (itemId == R.id.action_update)
		{
			BaseProvider.closeProviders(activity);
			FileDataDownloader.start(activity, activity.getResources().getString(R.string.pref_default_download_dbfile), StorageSettings.getDbDownloadSource(activity), StorageSettings.getDatabasePath(activity), StorageSettings.getCacheDir(activity));
			return true;
		}
		else if (itemId == R.id.action_asset_deliver)
		{
			SetupAsset.deliverAsset(activity.getString(R.string.asset_default), activity, null);
			return true;
		}
		else if (itemId == R.id.action_asset_dispose)
		{
			SetupAsset.disposeAsset(activity.getString(R.string.asset_default), activity, null);
			return true;
		}
		else if (itemId == R.id.action_asset_alt_deliver)
		{
			SetupAsset.deliverAsset(activity.getString(R.string.asset_alt), activity, null);
			return true;
		}
		else if (itemId == R.id.action_asset_alt_dispose)
		{
			SetupAsset.disposeAsset(activity.getString(R.string.asset_alt), activity, null);
			return true;
		}
		else if (itemId == R.id.action_setup)
		{
			intent = new Intent(activity, SetupActivity.class);
		}
		else if (itemId == R.id.action_clear_sql)
		{
			BaseProvider.sqlBuffer.clear();
			return true;
		}
		else if (itemId == R.id.action_help)
		{
			intent = new Intent(activity, HelpActivity.class);
		}
		else if (itemId == R.id.action_credits)
		{
			intent = new Intent(activity, AboutActivity.class);
		}
		else if (itemId == R.id.action_provider_info)
		{
			Providers.listProviders(activity);
			return true;
		}
		else if (itemId == R.id.action_donate)
		{
			intent = new Intent(activity, DonateActivity.class);
		}
		else if (itemId == R.id.action_other)
		{
			intent = new Intent(activity, OthersActivity.class);
		}
		else if (itemId == R.id.action_rate)
		{
			AppRate.rate(activity);
			return true;
		}
		else if (itemId == R.id.action_quit)
		{
			activity.finish();
			return true;
		}
		else if (itemId == R.id.action_appsettings)
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
