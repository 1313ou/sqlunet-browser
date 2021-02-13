/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
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
import org.sqlunet.concurrency.TaskDialogObserver;
import org.sqlunet.concurrency.TaskObserver;
import org.sqlunet.download.DownloadActivity;
import org.sqlunet.download.FileDataDownloader;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageSettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import static org.sqlunet.download.AbstractDownloadFragment.DOWNLOAD_FROM_ARG;
import static org.sqlunet.download.AbstractDownloadFragment.DOWNLOAD_TO_ARG;

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
		else if (itemId == R.id.action_download)
		{
			intent = new Intent(activity, DownloadActivity.class);
			intent.putExtra(DOWNLOAD_FROM_ARG, StorageSettings.getDbDownloadSource(activity));
			intent.putExtra(DOWNLOAD_TO_ARG, StorageSettings.getDbDownloadTarget(activity));
			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		else if (itemId == R.id.action_update)
		{
			BaseProvider.closeProviders(activity);
			final String name = activity.getResources().getString(R.string.pref_default_download_dbfile);
			final String downloadSourceUrl = StorageSettings.getDbDownloadSource(activity);
			final String downloadDest = StorageSettings.getDatabasePath(activity);
			final String cache = StorageSettings.getCacheDir(activity);
			FileDataDownloader.start(activity, name, downloadSourceUrl, downloadDest, cache);
			return true;
		}
		else if (itemId == R.id.action_asset_deliver)
		{
			final String asset = Settings.getAssetPack(activity);
			final String assetDir = Settings.getAssetPackDir(activity);
			final String assetZip = Settings.getAssetPackZip(activity);
			final String assetZipEntry = activity.getString(R.string.asset_zip_entry);
			final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager()) //
					.setTitle(activity.getString(R.string.asset_delivery)) //
					.setMessage(asset);
			SetupAsset.deliverAsset(asset, assetDir, assetZip, assetZipEntry, activity, observer, null);
			return true;
		}
		else if (itemId == R.id.action_asset_deliver_primary)
		{
			final String asset = activity.getString(R.string.asset_primary);
			final String assetDir = activity.getString(R.string.asset_dir_primary);
			final String assetZip = activity.getString(R.string.asset_zip_primary);
			final String assetZipEntry = activity.getString(R.string.asset_zip_entry);
			final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager()) //
					.setTitle(activity.getString(R.string.asset_delivery)) //
					.setMessage(asset);
			SetupAsset.deliverAsset(asset, assetDir, assetZip, assetZipEntry, activity, observer, null);
			return true;
		}
		else if (itemId == R.id.action_asset_deliver_alt)
		{
			final String asset = activity.getString(R.string.asset_alt);
			final String assetDir = activity.getString(R.string.asset_dir_alt);
			final String assetZip = activity.getString(R.string.asset_zip_alt);
			final String assetZipEntry = activity.getString(R.string.asset_zip_entry);
			if (!asset.isEmpty())
			{
				final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager()) //
						.setTitle(activity.getString(R.string.asset_delivery)) //
						.setMessage(asset);
				SetupAsset.deliverAsset(asset, assetDir, assetZip, assetZipEntry, activity, observer, null);
			}
			return true;
		}
		else if (itemId == R.id.action_asset_dispose)
		{
			SetupAsset.disposeAsset(Settings.getAssetPack(activity), activity);
			return true;
		}
		else if (itemId == R.id.action_asset_dispose_primary)
		{
			SetupAsset.disposeAsset(activity.getString(R.string.asset_primary), activity);
			return true;
		}
		else if (itemId == R.id.action_asset_dispose_alt)
		{
			String asset = activity.getString(R.string.asset_alt);
			if (!asset.isEmpty())
			{
				SetupAsset.disposeAsset(asset, activity);
			}
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
		else if (itemId == R.id.action_theme_system)
		{
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
			return true;
		}
		else if (itemId == R.id.action_theme_night)
		{
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
			Log.d("MenuHandler", "set night mode from " + activity.getComponentName());
			return true;
		}
		else if (itemId == R.id.action_theme_day)
		{
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
			Log.d("MenuHandler", "set day mode from " + activity.getComponentName());
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
