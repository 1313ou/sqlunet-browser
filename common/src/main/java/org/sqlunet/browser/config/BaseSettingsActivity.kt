/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import org.sqlunet.browser.common.R
import org.sqlunet.provider.ManagerContract
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.settings.LogUtils

abstract class BaseSettingsActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        // super
        super.onCreate(savedInstanceState)

        // content view
        setContentView(R.layout.activity_settings)

        // fragment manager
        val fm = supportFragmentManager

        // fragment
        if (savedInstanceState == null) {
            val initial = intent.getBooleanExtra(INITIAL_ARG, false)
            fm.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.settings, if (initial) Header2Fragment() else HeaderFragment())
                .commit()
            setTitle(R.string.title_settings)
        } else {
            title = savedInstanceState.getCharSequence(TITLE_TAG)
        }
        fm.addOnBackStackChangedListener {
            if (fm.backStackEntryCount == 0) {
                setTitle(R.string.title_settings)
            } else {
                var title: CharSequence? = null
                val fragments = fm.fragments
                if (fragments.isNotEmpty()) {
                    val fragment = fragments[0] // only one at a time
                    val preferenceFragment = fragment as PreferenceFragmentCompat
                    title = preferenceFragment.preferenceScreen.title
                }
                if (title.isNullOrEmpty()) {
                    setTitle(R.string.title_settings)
                } else {
                    setTitle(title)
                }
            }
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.subtitle = title
            }
        }

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // set up the action bar
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name)
            actionBar.subtitle = title
            actionBar.displayOptions = ActionBar.DISPLAY_USE_LOGO or ActionBar.DISPLAY_SHOW_TITLE or ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, title)
    }

    // M E N U

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.settings, menu)
        // MenuCompat.setGroupDividerEnabled(menu, true)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_clear_settings -> {
                resetSettings()
                restart()
                return true
            }

            R.id.action_diagnostics -> {
                val intent = Intent(this, DiagnosticsActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.action_tables_and_indices -> {
                val intent = ManagerContract.makeTablesAndIndexesIntent(this)
                intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_dbobject)
                startActivity(intent)
                return true
            }

            R.id.action_logs -> {
                val intent = Intent(this, LogsActivity::class.java)
                intent.putExtra(LogsActivity.ARG_LOG, LogUtils.SQL_LOG)
                startActivity(intent)
                return true
            }

            R.id.action_logs_doc -> {
                val intent = Intent(this, LogsActivity::class.java)
                intent.putExtra(LogsActivity.ARG_LOG, LogUtils.DOC_LOG)
                startActivity(intent)
                return true
            }

            R.id.action_logs_exec -> {
                val intent = Intent(this, LogsActivity::class.java)
                intent.putExtra(LogsActivity.ARG_LOG, ExecAsyncTask.EXEC_LOG)
                startActivity(intent)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    // U P

    override fun onSupportNavigateUp(): Boolean {
        val fm = supportFragmentManager
        return if (fm.popBackStackImmediate()) {
            true
        } else super.onSupportNavigateUp()
    }

    // U T I L S

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        // Instantiate the new fragment
        val fragmentClassName = pref.fragment!!
        val args = pref.getExtras()
        val fragment = supportFragmentManager.getFragmentFactory().instantiate(classLoader, fragmentClassName)
        fragment.setArguments(args)

        // Replace the existing Fragment with the new Fragment
        supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.settings, fragment)
            .addToBackStack("settings")
            .commit()
        return true
    }

    // H E A D E R   F R A G M E N T

    class HeaderFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.pref_headers, rootKey)
        }
    }

    class Header2Fragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.pref_headers2, rootKey)
        }
    }

    // U T I L S

    /**
     * Reset settings
     */
    private fun resetSettings() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPrefs.edit()
        editor.clear()
        tryCommit(editor)
    }

    /**
     * Try to commit
     *
     * @param editor editor editor
     */
    @SuppressLint("CommitPrefEdits", "ApplySharedPref")
    private fun tryCommit(editor: SharedPreferences.Editor) {
        try {
            editor.apply()
        } catch (ignored: AbstractMethodError) {
            // The app injected its own pre-Gingerbread SharedPreferences.Editor implementation without an apply method.
            editor.commit()
        }
    }

    /**
     * Restart app
     */
    private fun restart() {
        val restartIntent = packageManager.getLaunchIntentForPackage(packageName)!!
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(restartIntent)
    }

    companion object {

        private const val TITLE_TAG = "settingsActivityTitle"
        const val INITIAL_ARG = "settings_header"
    }
}
