/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.content.Intent
import android.content.res.Resources
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.bbou.concurrency.observe.TaskDialogObserver
import com.bbou.concurrency.observe.TaskObserver
import com.bbou.donate.DonateActivity
import com.bbou.download.workers.utils.FileDataDownloader.Companion.start
import com.bbou.download.workers.utils.ResourcesDownloader.Companion.showResources
import com.bbou.others.OthersActivity
import com.bbou.rate.AppRate.Companion.rate
import org.sqlunet.browser.EntryActivity.Companion.rerun
import org.sqlunet.browser.Providers.listProviders
import org.sqlunet.browser.common.R
import org.sqlunet.browser.config.BaseSettingsActivity
import org.sqlunet.browser.config.DiagnosticsActivity
import org.sqlunet.browser.config.DownloadIntentFactory.makeIntent
import org.sqlunet.browser.config.DownloadIntentFactory.makeUpdateIntent
import org.sqlunet.browser.config.ExecAsyncTask
import org.sqlunet.browser.config.LogsActivity
import org.sqlunet.browser.config.SettingsActivity
import org.sqlunet.browser.config.SetupActivity
import org.sqlunet.browser.config.SetupAsset.deliverAsset
import org.sqlunet.browser.config.SetupAsset.disposeAsset
import org.sqlunet.browser.config.SetupFileActivity
import org.sqlunet.browser.config.SetupFileFragment
import org.sqlunet.browser.config.StorageActivity
import org.sqlunet.browser.history.HistoryActivity
import org.sqlunet.provider.BaseProvider
import org.sqlunet.provider.BaseProvider.Companion.closeProviders
import org.sqlunet.settings.LogUtils
import org.sqlunet.settings.Settings

/**
 * Menu dispatcher/handler
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object MenuHandler {

    /**
     * Dispatch menu item action
     *
     * @param activity activity
     * @param item     menu item
     * @return true if processed/consumed
     */
    @JvmStatic
    fun menuDispatch(activity: AppCompatActivity, item: MenuItem): Boolean {
        val intent: Intent
        // main
        when (val itemId = item.itemId) {
            R.id.action_main -> {
                intent = Intent(activity, MainActivity::class.java)
                intent.addFlags(0)
            }

            R.id.action_status -> {
                intent = Intent(activity, StatusActivity::class.java)
            }

            R.id.action_provider_info -> {
                listProviders(activity)
                return true
            }

            R.id.action_setup -> {
                intent = Intent(activity, SetupActivity::class.java)
            }

            R.id.action_download -> {
                intent = makeIntent(activity)
            }

            R.id.action_update -> {
                closeProviders(activity)
                start(activity, makeUpdateIntent(activity))
                return true
            }

            R.id.action_resources_directory -> {
                showResources(activity)
                return true
            }

            R.id.action_settings -> {
                intent = Intent(activity, SettingsActivity::class.java)
                intent.addFlags(0)
            }

            R.id.action_clear_settings -> {
                val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
                val edit = prefs.edit()
                edit.clear().apply()
                intent = Intent(activity, SettingsActivity::class.java)
                intent.addFlags(0)
            }

            R.id.action_sql_clear -> {
                BaseProvider.sqlBuffer.clear()
                return true
            }

            R.id.action_history -> {
                intent = Intent(activity, HistoryActivity::class.java)
            }

            R.id.action_help -> {
                intent = Intent(activity, HelpActivity::class.java)
            }

            R.id.action_credits -> {
                intent = Intent(activity, AboutActivity::class.java)
            }

            R.id.action_donate -> {
                intent = Intent(activity, DonateActivity::class.java)
            }

            R.id.action_other -> {
                intent = Intent(activity, OthersActivity::class.java)
            }

            R.id.action_rate -> {
                rate(activity)
                return true
            }

            else -> {
                return menuDispatchCommon(activity, itemId)
            }
        }
        // start activity
        activity.startActivity(intent)
        return true
    }

    /**
     * Dispatch menu item action when can't run
     *
     * @param activity activity
     * @param item     menu item
     * @return true if processed/consumed
     */
    fun menuDispatchWhenCantRun(activity: AppCompatActivity, item: MenuItem): Boolean {
        val intent: Intent
        // handle item selection
        // status
        when (val itemId = item.itemId) {
            R.id.action_status -> {
                intent = Intent(activity, StatusActivity::class.java)
                intent.addFlags(0)
            }

            R.id.action_setup -> {
                intent = Intent(activity, SetupActivity::class.java)
                intent.addFlags(0)
            }

            R.id.action_download -> {
                intent = makeIntent(activity)
                intent.addFlags(0)
            }

            R.id.action_download_settings -> {
                intent = Intent(activity, SettingsActivity::class.java)
                intent.putExtra(BaseSettingsActivity.INITIAL_ARG, true)
                intent.addFlags(0)
            }

            R.id.action_clear_settings -> {
                val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
                val edit = prefs.edit()
                edit.clear().apply()
                intent = Intent(activity, SettingsActivity::class.java)
                intent.addFlags(0)
            }

            else -> {
                return menuDispatchCommon(activity, itemId)
            }
        }
        // start activity
        activity.startActivity(intent)
        return true
    }

    /**
     * Dispatch menu item action, common
     *
     * @param activity activity
     * @param itemId   menu item id
     * @return true if processed/consumed
     */
    private fun menuDispatchCommon(activity: AppCompatActivity, itemId: Int): Boolean {
        val intent: Intent
        when (itemId) {
            R.id.action_storage -> {
                intent = Intent(activity, StorageActivity::class.java)
            }

            R.id.action_diagnostics -> {
                intent = Intent(activity, DiagnosticsActivity::class.java)
            }

            R.id.action_logs -> {
                intent = Intent(activity, LogsActivity::class.java)
                intent.putExtra(LogsActivity.ARG_LOG, LogUtils.SQL_LOG)
            }

            R.id.action_logs_doc -> {
                intent = Intent(activity, LogsActivity::class.java)
                intent.putExtra(LogsActivity.ARG_LOG, LogUtils.DOC_LOG)
            }

            R.id.action_logs_exec -> {
                intent = Intent(activity, LogsActivity::class.java)
                intent.putExtra(LogsActivity.ARG_LOG, ExecAsyncTask.EXEC_LOG)
            }

            R.id.action_drop -> {
                intent = Intent(activity, SetupFileActivity::class.java)
                intent.putExtra(SetupFileFragment.ARG, SetupFileFragment.Operation.DROP.toString())
            }

            R.id.action_asset_deliver -> {
                val asset = Settings.getAssetPack(activity)
                val assetDir = Settings.getAssetPackDir(activity)
                val assetZip = Settings.getAssetPackZip(activity)
                val assetZipEntry = activity.getString(R.string.asset_zip_entry)
                val observer: TaskObserver<Pair<Number, Number>> = TaskDialogObserver<Pair<Number, Number>>(activity.supportFragmentManager)
                    .setTitle(activity.getString(R.string.title_dialog_assetload))
                    .setMessage("$asset\n${activity.getString(R.string.gloss_asset_delivery_message)}")
                deliverAsset(asset, assetDir, assetZip, assetZipEntry, activity, observer, null, null)
                return true
            }

            R.id.action_asset_dispose -> {
                disposeAsset(Settings.getAssetPack(activity), activity)
                return true
            }

            R.id.action_appsettings -> {
                val appId = activity.packageName
                Settings.applicationSettings(activity, appId)
                return true
            }

            R.id.action_theme_system -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                return true
            }

            R.id.action_theme_night -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Log.d("MenuHandler", "set night mode from " + activity.componentName)
                return true
            }

            R.id.action_theme_day -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Log.d("MenuHandler", "set day mode from " + activity.componentName)
                return true
            }

            R.id.action_quit -> {
                activity.finish()
                return true
            }

            else -> {
                return false
            }
        }
        // start activity
        activity.startActivity(intent)
        return true
    }

    /**
     * Dispatch menu item action, handle home
     *
     * @param activity activity
     * @param itemId   menu item id
     * @return true if processed/consumed
     */
    private fun menuDispatchHome(activity: AppCompatActivity, itemId: Int): Boolean {
        if (itemId == android.R.id.home) {
            rerun(activity)
            return true
        }
        return false
    }

    /*
	static public void populateAssets(@NonNull Context context, @NonNull Menu menu)
	{
		MenuItem menuItem = menu.findItem(R.id.action_assets)
		if (menuItem != null)
		{
			Menu subMenu = menuItem.getSubMenu()
			if (subMenu != null)
			{
				var res = context.getResources()
				setAssetActionTitle(res, subMenu.findItem(R.id.action_asset_deliver_primary), R.string.action_asset_deliver_format, R.string.asset_primary_name)
				setAssetActionTitle(res, subMenu.findItem(R.id.action_asset_dispose_primary), R.string.action_asset_dispose_format, R.string.asset_primary_name)
				setAssetActionTitle(res, subMenu.findItem(R.id.action_asset_deliver_alt), R.string.action_asset_deliver_format, R.string.asset_alt_name)
				setAssetActionTitle(res, subMenu.findItem(R.id.action_asset_dispose_alt), R.string.action_asset_dispose_format, R.string.asset_alt_name)
			}
		}
	}
	*/
    private fun setAssetActionTitle(res: Resources, menuItem: MenuItem?, @StringRes formatId: Int, @StringRes assetNameId: Int) {
        if (menuItem != null) {
            val title = String.format(res.getString(formatId), res.getString(assetNameId))
            menuItem.setTitle(title)
        }
    }

    fun disableDataChange(menu: Menu) {
        // MenuHandler.populateAssets(this, menu)
        val submenuItem = menu.findItem(R.id.action_data)
        if (submenuItem != null) {
            val subMenu: Menu? = submenuItem.subMenu
            subMenu?.setGroupEnabled(R.id.change_data, false)
        }
    }
}
