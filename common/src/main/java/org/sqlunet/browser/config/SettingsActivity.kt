/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Pair
import android.widget.EditText
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.bbou.download.workers.utils.ResourcesDownloader.Companion.populateLists
import org.sqlunet.browser.EntryActivity.Companion.rerun
import org.sqlunet.browser.common.R
import org.sqlunet.preference.OpenEditTextPreference
import org.sqlunet.provider.BaseProvider
import org.sqlunet.provider.BaseProvider.Companion.resizeSql
import org.sqlunet.settings.Settings
import org.sqlunet.settings.Storage
import org.sqlunet.settings.StorageReports.getStyledCachesNamesValues
import org.sqlunet.settings.StorageReports.getStyledStoragesNamesValues
import org.sqlunet.settings.StorageSettings.getDatabasePath
import org.sqlunet.settings.StorageUtils
import org.sqlunet.settings.StorageUtils.isAuto
import org.sqlunet.sql.PreparedStatement
import java.io.File
import java.util.function.BiConsumer

/**
 * A PreferenceActivity that presents a set of application settings. On handset devices, settings are presented as a single list. On tablets, settings
 * are split by category, with category headers shown to the left of the list of settings.
 *
 *
 * See [ Android Design: Settings](http://developer.android.com/design/patterns/settings.html) for design guidelines and the
 * [Settings API Guide](http://developer.android.com/guide/topics/ui/settings.html) for more information on developing a Settings UI.
 */
object SettingsActivity : BaseSettingsActivity() {

    // P O P U L A T E    L I S T S
    /**
     * Set storage preference
     *
     * @param context context
     * @param pref    preference
     */
    private fun populateStoragePreference(context: Context, pref: Preference?) {
        val listPreference = (pref as ListPreference?)!!
        populateStorageListPreference(context, listPreference)
        listPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            populateStorageListPreference(context, listPreference)
            false
        }
    }

    /**
     * Set cache preference
     *
     * @param context context
     * @param pref    preference
     */
    private fun populateCachePreference(context: Context, pref: Preference) {
        val listPreference = pref as ListPreference
        populateCacheListPreference(context, listPreference)
        listPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            populateCacheListPreference(context, listPreference)
            false
        }
    }

    /**
     * Set storage preference data
     *
     * @param context  context
     * @param listPref pref
     */
    private fun populateStorageListPreference(context: Context, listPref: ListPreference) {
        val namesValues: Pair<Array<CharSequence>, Array<CharSequence>> = getStyledStoragesNamesValues(context)
        var entries = namesValues.first
        var entryValues = namesValues.second
        val defaultValue: CharSequence?
        if (entries == null || entries.isEmpty() || entryValues == null || entryValues.isEmpty()) {
            defaultValue = StorageUtils.AUTO
            entryValues = arrayOf(defaultValue)
            entries = arrayOf(StorageUtils.AUTO_LABEL)
        } else {
            defaultValue = entryValues[0]
        }
        listPref.entries = entries
        listPref.setDefaultValue(defaultValue)
        listPref.entryValues = entryValues
    }

    /**
     * Set cache preference data
     *
     * @param context  context
     * @param listPref pref
     */
    private fun populateCacheListPreference(context: Context, listPref: ListPreference) {
        val result = getStyledCachesNamesValues(context)
        val names = result.first
        val values: Array<String> = result.second
        listPref.entries = names
        listPref.entryValues = values
        listPref.setDefaultValue(values[0])
    }

    // F R A G M E N T S

    /**
     * This fragment shows general preferences only.
     */
    class GeneralPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            // inflate
            addPreferencesFromResource(R.xml.pref_general)

            // bind the summaries to their values.
            val launchPreference = findPreference<Preference>(Settings.PREF_LAUNCH)!!
            launchPreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance())
            val selectorPreference = findPreference<Preference>(Settings.PREF_SELECTOR_MODE)!!
            selectorPreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance())
            selectorPreference.onPreferenceChangeListener = listener
            val detailPreference = findPreference<Preference>(Settings.PREF_DETAIL_MODE)!!
            detailPreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance())
            detailPreference.onPreferenceChangeListener = listener
            val sqlBufferCapacityPreference = preferenceManager.findPreference<EditTextPreference>(BaseProvider.CircularBuffer.PREF_SQL_BUFFER_CAPACITY)!!
            sqlBufferCapacityPreference.setOnBindEditTextListener { editText: EditText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED) }
            sqlBufferCapacityPreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance())
            sqlBufferCapacityPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener setOnPreferenceChangeListener@{ _: Preference?, value: Any? ->
                val sqlBufferCapacity = value as String?
                if (sqlBufferCapacity != null) {
                    try {
                        val capacity = sqlBufferCapacity.toInt()
                        if (capacity in 1..64) {
                            resizeSql(capacity)
                            return@setOnPreferenceChangeListener true
                        }
                    } catch (e: Exception) {
                        //
                    }
                }
                false
            }
            val sqlLogPreference = findPreference<Preference>(BaseProvider.CircularBuffer.PREF_SQL_LOG)!!
            sqlLogPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, value: Any ->
                val flag = value as Boolean
                PreparedStatement.logSql = flag
                BaseProvider.logSql = flag
                true
            }
            val twoPanesPreference = findPreference<Preference>(Settings.PREF_TWO_PANES)!!
            twoPanesPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, value: Any ->
                val flag = value as Boolean
                Settings.paneMode = if (flag) 2 else 0
                true
            }
        }

        companion object {
            private val listener = Preference.OnPreferenceChangeListener { preference: Preference, value: Any? ->
                val sharedPrefs = preference.getSharedPreferences()!!
                val key = preference.key
                val prevValue = sharedPrefs.getString(key, null)
                if (if (value == null) prevValue != null else value != prevValue) {
                    val context = preference.context
                    rerun(context)
                }
                true
            }
        }
    }

    /**
     * This fragment shows general preferences only.
     */
    class FilterPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            // inflate
            addPreferencesFromResource(R.xml.pref_filter)
        }
    }

    /**
     * This fragment shows database preferences only.
     */
    class DatabasePreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            // inflate
            addPreferencesFromResource(R.xml.pref_database)

            // db file
            val dbFilePreference = findPreference<Preference>(Settings.PREF_DB_FILE)!!
            val storage = getDatabasePath(requireContext())
            dbFilePreference.setSummary(storage)
            dbFilePreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any? ->
                var storage2 = newValue as String?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isAuto(storage2!!)) //
                {
                    storage2 = requireContext().filesDir.absolutePath
                }
                storage2 += File.separatorChar.toString() + Storage.DBFILE
                dbFilePreference.setSummary(storage2)
                false
            }

            // storage
            val storagePreference = findPreference<Preference>(Settings.PREF_STORAGE)!!
            // required if no 'entries' and 'entryValues' in XML
            populateStoragePreference(requireContext(), storagePreference)
            storagePreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance())
            val listener1 = storagePreference.onPreferenceChangeListener
            storagePreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference: Preference?, newValue: Any? ->
                listener1?.onPreferenceChange(preference!!, newValue)
                dbFilePreference.callChangeListener(newValue)
                true
            }
        }
    }

    /**
     * This fragment shows database preferences only.
     */
    class DatabasePreference2Fragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            // inflate
            addPreferencesFromResource(R.xml.pref_database2)

            // db file
            val dbFilePreference = findPreference<Preference>(Settings.PREF_DB_FILE)!!
            val storage = getDatabasePath(requireContext())
            dbFilePreference.setSummary(storage)
            dbFilePreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any? ->
                var storage2 = newValue as String?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isAuto(storage2!!)) //
                {
                    storage2 = requireContext().filesDir.absolutePath
                }
                storage2 += File.separatorChar.toString() + Storage.DBFILE
                dbFilePreference.setSummary(storage2)
                false
            }

            // storage
            val storagePreference = findPreference<Preference>(Settings.PREF_STORAGE)!!
            // required if no 'entries' and 'entryValues' in XML
            populateStoragePreference(requireContext(), storagePreference)
            storagePreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance())
            val listener1 = storagePreference.onPreferenceChangeListener
            storagePreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference: Preference?, newValue: Any? ->
                listener1?.onPreferenceChange(preference!!, newValue)
                dbFilePreference.callChangeListener(newValue)
                true
            }
        }
    }

    /**
     * This fragment shows asset pack preferences only.
     */
    class AssetsPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            // inflate
            addPreferencesFromResource(R.xml.pref_assets)

            // required if no 'entries' and 'entryValues' in XML
            val dbAssetPreference = findPreference<Preference>(org.sqlunet.assetpack.Settings.PREF_DB_ASSET)!!

            // bind the summaries to their values.
            dbAssetPreference.setSummaryProvider { preference: Preference -> PreferenceManager.getDefaultSharedPreferences(preference.context).getString(preference.key, "-") }
        }
    }

    /**
     * This fragment shows download preferences only.
     */
    class DownloadPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            // inflate
            addPreferencesFromResource(R.xml.pref_download)

            // bind the summaries to their values.
            val downloadModePreference = findPreference<Preference>(Settings.PREF_DOWNLOAD_MODE)!!
            downloadModePreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance())
            val sitePreference = findPreference<OpenEditTextPreference>(Settings.PREF_DOWNLOAD_SITE)!!
            populateLists(requireContext(), BiConsumer<List<String>, List<String>> { xValues: List<String?>?, xLabels: List<String?>? -> sitePreference.addOptions(xValues, xLabels) })
            sitePreference.setSummaryProvider(OpenEditTextPreference.SUMMARY_PROVIDER)
            val dbFilePreference = findPreference<Preference>(Settings.PREF_DOWNLOAD_DBFILE)!!
            dbFilePreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance())
        }

        override fun onDisplayPreferenceDialog(preference: Preference) {
            if (!OpenEditTextPreference.onDisplayPreferenceDialog(this, preference)) {
                super.onDisplayPreferenceDialog(preference)
            }
        }
    }
}
