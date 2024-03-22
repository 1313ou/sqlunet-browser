/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bbou.concurrency.observe.TaskDialogObserver;
import com.bbou.concurrency.observe.TaskObserver;
import com.bbou.donate.DonateActivity;
import com.bbou.download.workers.utils.FileDataDownloader;
import com.bbou.download.workers.utils.ResourcesDownloader;
import com.bbou.others.OthersActivity;
import com.bbou.rate.AppRate;

import org.sqlunet.browser.common.R;
import org.sqlunet.browser.config.DiagnosticsActivity;
import org.sqlunet.browser.config.DownloadIntentFactory;
import org.sqlunet.browser.config.LogsActivity;
import org.sqlunet.browser.config.SettingsActivity;
import org.sqlunet.browser.config.SetupActivity;
import org.sqlunet.browser.config.SetupAsset;
import org.sqlunet.browser.config.SetupFileActivity;
import org.sqlunet.browser.config.SetupFileFragment;
import org.sqlunet.browser.config.StorageActivity;
import org.sqlunet.browser.history.HistoryActivity;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.settings.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import kotlin.Pair;

import static org.sqlunet.browser.config.BaseSettingsActivity.INITIAL_ARG;

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

		// main
		if (itemId == R.id.action_main)
		{
			intent = new Intent(activity, MainActivity.class);
			intent.addFlags(0);
		}

		// status
		else if (itemId == R.id.action_status)
		{
			intent = new Intent(activity, StatusActivity.class);
		}
		else if (itemId == R.id.action_provider_info)
		{
			Providers.listProviders(activity);
			return true;
		}

		// change data
		else if (itemId == R.id.action_setup)
		{
			intent = new Intent(activity, SetupActivity.class);
		}
		else if (itemId == R.id.action_download)
		{
			intent = DownloadIntentFactory.makeIntent(activity);
		}
		else if (itemId == R.id.action_update)
		{
			BaseProvider.closeProviders(activity);
			FileDataDownloader.Companion.start(activity, DownloadIntentFactory.makeUpdateIntent(activity));
			return true;
		}
		else if (itemId == R.id.action_resources_directory)
		{
			ResourcesDownloader.Companion.showResources(activity);
			return true;
		}

		// settings
		else if (itemId == R.id.action_settings)
		{
			intent = new Intent(activity, SettingsActivity.class);
			intent.addFlags(0);
		}
		else if (itemId == R.id.action_clear_settings)
		{
			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
			final SharedPreferences.Editor edit = prefs.edit();
			edit.clear().apply();
			intent = new Intent(activity, SettingsActivity.class);
			intent.addFlags(0);
		}

		// sql
		else if (itemId == R.id.action_sql_clear)
		{
			BaseProvider.sqlBuffer.clear();
			return true;
		}

		// others
		else if (itemId == R.id.action_history)
		{
			intent = new Intent(activity, HistoryActivity.class);
		}
		else if (itemId == R.id.action_help)
		{
			intent = new Intent(activity, HelpActivity.class);
		}
		else if (itemId == R.id.action_credits)
		{
			intent = new Intent(activity, AboutActivity.class);
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
		else
		{
			return menuDispatchCommon(activity, itemId);
		}

		// start activity
		activity.startActivity(intent);
		return true;
	}

	/**
	 * Dispatch menu item action when can't run
	 *
	 * @param activity activity
	 * @param item     menu item
	 * @return true if processed/consumed
	 */
	static public boolean menuDispatchWhenCantRun(@NonNull final AppCompatActivity activity, @NonNull final MenuItem item)
	{
		Intent intent;

		// handle item selection
		final int itemId = item.getItemId();

		// status
		if (itemId == R.id.action_status)
		{
			intent = new Intent(activity, StatusActivity.class);
			intent.addFlags(0);
		}

		// change data
		else if (itemId == R.id.action_setup)
		{
			intent = new Intent(activity, SetupActivity.class);
			intent.addFlags(0);
		}
		else if (itemId == R.id.action_download)
		{
			intent = DownloadIntentFactory.makeIntent(activity);
			intent.addFlags(0);
		}

		// settings
		else if (itemId == R.id.action_download_settings)
		{
			intent = new Intent(activity, SettingsActivity.class);
			intent.putExtra(INITIAL_ARG, true);
			intent.addFlags(0);
		}
		else if (itemId == R.id.action_clear_settings)
		{
			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
			final SharedPreferences.Editor edit = prefs.edit();
			edit.clear().apply();
			intent = new Intent(activity, SettingsActivity.class);
			intent.addFlags(0);
		}

		// others
		else
		{
			return menuDispatchCommon(activity, itemId);
		}

		// start activity
		activity.startActivity(intent);
		return true;
	}

	/**
	 * Dispatch menu item action, common
	 *
	 * @param activity activity
	 * @param itemId   menu item id
	 * @return true if processed/consumed
	 */
	static private boolean menuDispatchCommon(@NonNull final AppCompatActivity activity, final int itemId)
	{
		Intent intent;

		if (itemId == R.id.action_storage)
		{
			intent = new Intent(activity, StorageActivity.class);
		}
		else if (itemId == R.id.action_diagnostics)
		{
			intent = new Intent(activity, DiagnosticsActivity.class);
		}
		else if (itemId == R.id.action_logs)
		{
			intent = new Intent(activity, LogsActivity.class);
		}

		// change data
		else if (itemId == R.id.action_drop)
		{
			intent = new Intent(activity, SetupFileActivity.class);
			intent.putExtra(SetupFileFragment.ARG, SetupFileFragment.Operation.DROP.toString());
		}
		else if (itemId == R.id.action_asset_deliver)
		{
			final String asset = Settings.getAssetPack(activity);
			final String assetDir = Settings.getAssetPackDir(activity);
			final String assetZip = Settings.getAssetPackZip(activity);
			final String assetZipEntry = activity.getString(R.string.asset_zip_entry);
			final TaskObserver<Pair<Number, Number>> observer = new TaskDialogObserver<Pair<Number, Number>>(activity.getSupportFragmentManager()) //
					.setTitle(activity.getString(R.string.title_dialog_assetload)) //
					.setMessage(asset + '\n' + activity.getString(R.string.gloss_asset_delivery_message));

			SetupAsset.deliverAsset(asset, assetDir, assetZip, assetZipEntry, activity, observer, null, null);
			return true;
		}
		else if (itemId == R.id.action_asset_dispose)
		{
			SetupAsset.disposeAsset(Settings.getAssetPack(activity), activity);
			return true;
		}
		/*
		else if (itemId == R.id.action_asset_deliver_primary)
		{
			final String asset = activity.getString(R.string.asset_primary);
			final String assetDir = activity.getString(R.string.asset_dir_primary);
			final String assetZip = activity.getString(R.string.asset_zip_primary);
			final String assetZipEntry = activity.getString(R.string.asset_zip_entry);
			final TaskObserver<Pair<Number, Number>> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager()) //
					.setTitle(activity.getString(R.string.title_dialog_assetload)) //
					.setMessage(asset);
			SetupAsset.deliverAsset(asset, assetDir, assetZip, assetZipEntry, activity, observer, null, null);
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
				final TaskObserver<Pair<Number, Number>> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager()) //
						.setTitle(activity.getString(R.string.title_dialog_assetload)) //
						.setMessage(asset);
				SetupAsset.deliverAsset(asset, assetDir, assetZip, assetZipEntry, activity, observer, null, null);
			}
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
		*/

		// others
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
		else if (itemId == R.id.action_quit)
		{
			activity.finish();
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

	/**
	 * Dispatch menu item action, handle home
	 *
	 * @param activity activity
	 * @param itemId   menu item id
	 * @return true if processed/consumed
	 */
	static private boolean menuDispatchHome(@NonNull final AppCompatActivity activity, final int itemId)
	{
		if (itemId == android.R.id.home)
		{
			EntryActivity.rerun(activity);
			return true;
		}
		return false;
	}

	/*
	static public void populateAssets(@NonNull Context context, @NonNull Menu menu)
	{
		MenuItem menuItem = menu.findItem(R.id.action_assets);
		if (menuItem != null)
		{
			Menu subMenu = menuItem.getSubMenu();
			if (subMenu != null)
			{
				final Resources res = context.getResources();
				setAssetActionTitle(res, subMenu.findItem(R.id.action_asset_deliver_primary), R.string.action_asset_deliver_format, R.string.asset_primary_name);
				setAssetActionTitle(res, subMenu.findItem(R.id.action_asset_dispose_primary), R.string.action_asset_dispose_format, R.string.asset_primary_name);
				setAssetActionTitle(res, subMenu.findItem(R.id.action_asset_deliver_alt), R.string.action_asset_deliver_format, R.string.asset_alt_name);
				setAssetActionTitle(res, subMenu.findItem(R.id.action_asset_dispose_alt), R.string.action_asset_dispose_format, R.string.asset_alt_name);
			}
		}
	}
	*/

	static private void setAssetActionTitle(@NonNull final Resources res, @Nullable final MenuItem menuItem, @StringRes int formatId, @StringRes int assetNameId)
	{
		if (menuItem != null)
		{
			String title = String.format(res.getString(formatId), res.getString(assetNameId));
			menuItem.setTitle(title);
		}
	}

	public static void disableDataChange(@NonNull Menu menu)
	{
		// MenuHandler.populateAssets(this, menu);
		MenuItem submenuItem = menu.findItem(R.id.action_data);
		if (submenuItem != null)
		{
			Menu subMenu = submenuItem.getSubMenu();
			if (subMenu != null)
			{
				subMenu.setGroupEnabled(R.id.change_data, false);
			}
		}
	}
}

