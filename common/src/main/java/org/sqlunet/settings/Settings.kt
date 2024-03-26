/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.LayoutRes
import androidx.preference.PreferenceManager
import org.sqlunet.browser.common.R
import org.sqlunet.browser.config.SetupAsset
import org.sqlunet.provider.BaseProvider
import org.sqlunet.provider.BaseProvider.Companion.resizeSql
import org.sqlunet.sql.PreparedStatement

/**
 * Settings
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class Settings {

    // D I S P L A Y

    /**
     * Selector view modes
     */
    enum class SelectorViewMode {
        VIEW,
        WEB;

        companion object {

            /**
             * Get selector preferred view mode
             *
             * @param context context
             * @return preferred selector view mode
             */
            fun getPref(context: Context): SelectorViewMode {
                val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
                val modeString = sharedPref.getString(PREF_SELECTOR_MODE, VIEW.name)
                var mode: SelectorViewMode
                try {
                    mode = valueOf(modeString!!)
                } catch (e: Exception) {
                    mode = VIEW
                    sharedPref.edit().putString(PREF_SELECTOR_MODE, mode.name).apply()
                }
                return mode
            }
        }
    }

    /**
     * Detail detail view modes
     */
    enum class DetailViewMode {
        VIEW,
        WEB;

        companion object {
            /**
             * Get preferred view mode
             *
             * @param context context
             * @return preferred selector mode
             */
            fun getPref(context: Context): DetailViewMode {
                val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
                val modeString = sharedPref.getString(PREF_DETAIL_MODE, VIEW.name)
                var mode: DetailViewMode
                try {
                    mode = valueOf(modeString!!)
                } catch (e: Exception) {
                    mode = VIEW
                    sharedPref.edit().putString(PREF_DETAIL_MODE, mode.name).apply()
                }
                return mode
            }
        }
    }

    // S E L E C T O R   T Y P E

    /**
     * Selectors
     */
    enum class Selector {
        SELECTOR;

        /**
         * Set this selector as preferred selector
         *
         * @param context context
         */
        fun setPref(context: Context) {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPref.edit().putString(PREF_SELECTOR, name).apply()
        }

        companion object {

            /**
             * Get preferred mode
             *
             * @param context context
             * @return preferred selector mode
             */
            @JvmStatic
            fun getPref(context: Context): Selector {
                val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
                val name = sharedPref.getString(PREF_SELECTOR, SELECTOR.name)
                var mode: Selector
                try {
                    mode = valueOf(name!!)
                } catch (e: Exception) {
                    mode = SELECTOR
                    sharedPref.edit().putString(PREF_SELECTOR, mode.name).apply()
                }
                return mode
            }
        }
    }

    companion object {

        // preferences
        const val PREF_LAUNCH = "pref_launch"
        const val PREF_SELECTOR = "pref_selector_mode" // selector or xselector or ...
        const val PREF_SELECTOR_MODE = "pref_selector_view_mode" // view or web for selector fragment
        const val PREF_DETAIL_MODE = "pref_detail_view_mode" // view wor web for detail fragment
        private const val PREF_XML = "pref_xml"
        private const val PREF_TEXTSEARCH_MODE = "pref_searchtext_mode"
        private const val PREF_ASSET_PRIMARY_DEFAULT = SetupAsset.PREF_ASSET_PRIMARY_DEFAULT
        private const val PREF_ASSET_AUTO_CLEANUP = SetupAsset.PREF_ASSET_AUTO_CLEANUP
        const val PREF_DB_FILE = "pref_db_file"
        private const val PREF_DB_DATE = "pref_db_date"
        private const val PREF_DB_SIZE = "pref_db_size"
        const val PREF_STORAGE = StorageSettings.PREF_STORAGE
        const val PREF_DOWNLOAD_MODE = StorageSettings.PREF_DOWNLOAD_MODE
        const val PREF_DOWNLOAD_SITE = StorageSettings.PREF_DOWNLOAD_SITE
        const val PREF_DOWNLOAD_DBFILE = StorageSettings.PREF_DOWNLOAD_DBFILE
        private const val PREF_CACHE = StorageSettings.PREF_CACHE
        private const val PREF_ZIP_ENTRY = "pref_zip_entry"
        const val PREF_TWO_PANES = "pref_two_panes"
        private const val PREF_VERSION = "org.sqlunet.browser.version"

        // P A N E  L A Y O U T   H O O K

        var paneMode = 0

        @JvmStatic
        @LayoutRes
        fun getPaneLayout(@LayoutRes defaultPaneLayout: Int, @LayoutRes onePaneLayout: Int, @LayoutRes twoPanesLayout: Int): Int {
            return when (paneMode) {
                0 -> defaultPaneLayout
                1 -> onePaneLayout
                2 -> twoPanesLayout
                else -> defaultPaneLayout
            }
        }

        // P R E F E R E N C E   S H O R T C U T S

        /**
         * Get launch activity class
         *
         * @param context context
         * @return preferred launch activity class
         */
        fun getLaunchPref(context: Context): String {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            var result = sharedPref.getString(PREF_LAUNCH, null)
            if (result == null) {
                result = context.getString(R.string.default_launch)
            }
            return result
        }

        /**
         * Get selector preferred view mode
         *
         * @param context context
         * @return preferred selector view mode
         */
        @JvmStatic
        fun getSelectorViewModePref(context: Context): SelectorViewMode {
            return SelectorViewMode.getPref(context)
        }

        /**
         * Get detail preferred view mode
         *
         * @param context context
         * @return preferred detail view mode
         */
        @JvmStatic
        fun getDetailViewModePref(context: Context): DetailViewMode {
            return DetailViewMode.getPref(context)
        }

        /**
         * Get preferred selector type
         *
         * @param context context
         * @return preferred selector type
         */
        @JvmStatic
        fun getSelectorPref(context: Context): Selector {
            return Selector.getPref(context)
        }

        /**
         * Get preferred XML output flag when view mode is WEB
         *
         * @param context context
         * @return preferred XML output flag when view mode is WEB
         */
        @JvmStatic
        fun getXmlPref(context: Context): Boolean {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPref.getBoolean(PREF_XML, true)
        }

        /**
         * Get preferred search mode
         *
         * @param context context
         * @return preferred search mode
         */
        @JvmStatic
        fun getSearchModePref(context: Context): Int {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPref.getInt(PREF_TEXTSEARCH_MODE, 0)
        }

        /**
         * Set preferred search mode
         *
         * @param context context
         * @param value   preferred search mode
         */
        @JvmStatic
        fun setSearchModePref(context: Context, value: Int) {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPref.edit().putInt(PREF_TEXTSEARCH_MODE, value).apply()
        }

        /**
         * Get preferred asset pack
         *
         * @param context context
         * @return asset pack preference
         */
        @JvmStatic
        fun getAssetPack(context: Context): String {
            val primary = context.getString(R.string.asset_primary)
            val alt = context.getString(R.string.asset_alt)
            if (alt.isEmpty()) {
                return primary
            }
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val isPrimaryDefault = prefs.getBoolean(PREF_ASSET_PRIMARY_DEFAULT, true)
            return if (isPrimaryDefault) primary else alt
        }

        /**
         * Get preferred asset pack dir
         *
         * @param context context
         * @return asset pack dir preference
         */
        @JvmStatic
        fun getAssetPackDir(context: Context): String {
            val primary = context.getString(R.string.asset_dir_primary)
            val alt = context.getString(R.string.asset_dir_alt)
            if (alt.isEmpty()) {
                return primary
            }
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val isPrimaryDefault = prefs.getBoolean(PREF_ASSET_PRIMARY_DEFAULT, true)
            return if (isPrimaryDefault) primary else alt
        }

        /**
         * Get preferred asset pack zip
         *
         * @param context context
         * @return asset pack zip preference
         */
        @JvmStatic
        fun getAssetPackZip(context: Context): String {
            val primary = context.getString(R.string.asset_zip_primary)
            val alt = context.getString(R.string.asset_zip_alt)
            if (alt.isEmpty()) {
                return primary
            }
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val isPrimaryDefault = prefs.getBoolean(PREF_ASSET_PRIMARY_DEFAULT, true)
            return if (isPrimaryDefault) primary else alt
        }

        /**
         * Get preferred asset pack auto cleanup flag
         *
         * @param context context
         * @return asset asset pack auto cleanup preference
         */
        fun getAssetAutoCleanup(context: Context): Boolean {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return prefs.getBoolean(PREF_ASSET_AUTO_CLEANUP, true)
        }

        /**
         * Get preferred download mode
         *
         * @param context context
         * @return preferred download mode
         */
        fun getDownloadModePref(context: Context): String? {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPref.getString(PREF_DOWNLOAD_MODE, null)
        }

        /**
         * Get preferred cache
         *
         * @param context context
         * @return preferred cache
         */
        fun getCachePref(context: Context): String? {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPref.getString(PREF_CACHE, null)
        }

        @JvmStatic
        fun getZipEntry(context: Context, defaultValue: String?): String? {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPref.getString(PREF_ZIP_ENTRY, defaultValue)
        }

        /**
         * Get db date
         *
         * @param context context
         * @return timestamp
         */
        fun getDbDate(context: Context): Long {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPref.getLong(PREF_DB_DATE, -1)
        }

        /**
         * Set db date
         *
         * @param context   context
         * @param timestamp timestamp
         */
        fun setDbDate(context: Context, timestamp: Long) {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPref.edit().putLong(PREF_DB_DATE, timestamp).apply()
        }

        /**
         * Get db size
         *
         * @param context context
         * @return size
         */
        fun getDbSize(context: Context): Long {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPref.getLong(PREF_DB_SIZE, -1)
        }

        /**
         * Set db size
         *
         * @param context context
         * @param size    size
         */
        fun setDbSize(context: Context, size: Long) {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPref.edit().putLong(PREF_DB_SIZE, size).apply()
        }

        /**
         * Initialize display mode preferences
         *
         * @param context context
         */
        @SuppressLint("CommitPrefEdits", "ApplySharedPref")
        fun initializeDisplayPrefs(context: Context) {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPref.edit()
            val viewwebModeString = sharedPref.getString(PREF_SELECTOR_MODE, null)
            if (viewwebModeString == null) {
                editor.putString(PREF_SELECTOR_MODE, SelectorViewMode.VIEW.name)
            }
            val detailModeString = sharedPref.getString(PREF_DETAIL_MODE, null)
            if (detailModeString == null) {
                editor.putString(PREF_DETAIL_MODE, DetailViewMode.VIEW.name)
            }
            editor.commit()
        }

        /**
         * Update globals
         *
         * @param context context
         */
        fun updateGlobals(context: Context) {
            // globals
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val logSql = sharedPref.getBoolean(BaseProvider.CircularBuffer.PREF_SQL_LOG, false)
            PreparedStatement.logSql = logSql
            BaseProvider.logSql = logSql
            val sqlBufferCapacity = sharedPref.getString(BaseProvider.CircularBuffer.PREF_SQL_BUFFER_CAPACITY, null)
            if (sqlBufferCapacity != null) {
                try {
                    val capacity = sqlBufferCapacity.toInt()
                    if (capacity in 1..64) {
                        resizeSql(capacity)
                    } else {
                        sharedPref.edit().remove(BaseProvider.CircularBuffer.PREF_SQL_BUFFER_CAPACITY).apply()
                    }
                } catch (_: Exception) {
                }
            }
            val twoPanes = sharedPref.getBoolean(PREF_TWO_PANES, false)
            paneMode = if (twoPanes) 2 else 0
        }

        /**
         * Is upgrade
         *
         * @param context context
         * @return [old build,new build]
         */
        fun isUpgrade(context: Context): LongArray {
            // recorded version
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val version: Long = try {
                prefs.getLong(PREF_VERSION, -1)
            } catch (e: ClassCastException) {
                prefs.getInt(PREF_VERSION, -1).toLong()
            }

            // build version
            var build: Long = 0 //BuildConfig.VERSION_CODE
            try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                build = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    packageInfo.versionCode.toLong()
                }
            } catch (ignored: PackageManager.NameNotFoundException) {
                
            }

            // result
            return longArrayOf(version, build)
        }

        /**
         * Clear settings on upgrade
         *
         * @param context context
         */
        @SuppressLint("CommitPrefEdits", "ApplySharedPref")
        fun onUpgrade(context: Context, build: Long) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            prefs 
                .edit() 
                // clear all settings
                // .clear()
                // clear some settings
                .remove(PREF_DOWNLOAD_MODE) 
                .remove(PREF_DOWNLOAD_SITE) 
                .remove(PREF_DOWNLOAD_DBFILE) 
                .remove(PREF_LAUNCH) 
                // flag as 'has run'
                .putLong(PREF_VERSION, build) 
                .apply()
        }

        /**
         * Application settings
         *
         * @param context context
         * @param pkgName package name
         */
        @JvmStatic
        fun applicationSettings(context: Context, pkgName: String) {
            val intent = Intent()

            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.setData(Uri.parse("package:$pkgName"))

            // start activity
            context.startActivity(intent)
        }
    }
}
