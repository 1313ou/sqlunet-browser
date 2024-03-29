/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import org.sqlunet.browser.MenuHandler
import org.sqlunet.browser.common.R
import org.sqlunet.provider.ManagerContract
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.settings.StorageReports.getStyledCachesNamesValues
import org.sqlunet.settings.StorageReports.getStyledDownloadNamesValues
import org.sqlunet.settings.StorageReports.getStyledStorageDirectoriesNamesValues
import org.sqlunet.settings.StorageReports.namesValuesToReportStyled
import org.sqlunet.settings.StorageReports.reportStyledDirs
import java.lang.reflect.InvocationTargetException

class SetupActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    /**
     * Pager that will host the section contents.
     */
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // set up the action bar
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
        }

        // create the adapter that will return a fragment for each of the three sections of the activity.
        val pagerAdapter = SectionsPagerAdapter(this)

        // set up the pager with the sections adapter.
        viewPager = findViewById(R.id.container)
        viewPager.setAdapter(pagerAdapter)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d("ViewPager2", "Switched to page $position")
                // You can perform actions based on the current page, update UI elements, etc.
            }
        })

        // tab layout
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.addOnTabSelectedListener(this)

        // For each of the sections in the app, add a tab to the action bar.
        for (i in 0 until pagerAdapter.itemCount) {
            // Create a tab with text corresponding to the page titleId defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            tabLayout.addTab(
                tabLayout.newTab()
                    .setTag(pagerAdapter.fragmentClasses[i])
                    .setContentDescription(pagerAdapter.getPageDescriptionId(i))
                    .setText(pagerAdapter.getPageTitleId(i))
            )
        }
    }

    // T A B  L I S T E N E R

    override fun onTabSelected(tab: TabLayout.Tab) {
        val position = tab.position
        viewPager.currentItem = position
        val fragmentClass = tab.tag as String?
        val fragment = makeFragment(fragmentClass)
        if (fragment is Updatable) {
            val updatable = fragment as Updatable
            updatable.update()
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {}

    override fun onTabReselected(tab: TabLayout.Tab) {}

    private fun makeFragment(fragmentClass: String?): Fragment {
        Log.d(TAG, "Page fragment $fragmentClass")
        if (!fragmentClass.isNullOrEmpty()) {
            try {
                val cl = Class.forName(fragmentClass)
                val cons = cl.getConstructor()
                return cons.newInstance() as Fragment
            } catch (e: ClassNotFoundException) {
                Log.e(TAG, "Page fragment", e)
            } catch (e: NoSuchMethodException) {
                Log.e(TAG, "Page fragment", e)
            } catch (e: InstantiationException) {
                Log.e(TAG, "Page fragment", e)
            } catch (e: IllegalAccessException) {
                Log.e(TAG, "Page fragment", e)
            } catch (e: InvocationTargetException) {
                Log.e(TAG, "Page fragment", e)
            }
        }
        throw IllegalArgumentException(fragmentClass)
    }

    /**
     * A Adapter that returns a fragment corresponding to one of the sections/tabs/pages.
     */
    private inner class SectionsPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        val fragmentClasses: Array<String>

        init {
            val res = activity.resources
            fragmentClasses = res.getStringArray(R.array.fragment_class_setup_pages)
        }

        override fun getItemCount(): Int {
            return fragmentClasses.size
        }

        override fun createFragment(position: Int): Fragment {
            val fragmentName = fragmentClasses[position]
            return makeFragment(fragmentName)
        }

        fun getPageTitleId(position: Int): Int {
            when (position) {
                0 -> return R.string.title_page_setup_status
                1 -> return R.string.title_page_setup_file
                2 -> return R.string.title_page_setup_database
            }
            throw IllegalArgumentException()
        }

        fun getPageDescriptionId(position: Int): Int {
            when (position) {
                0 -> return R.string.description_page_setup_status
                1 -> return R.string.description_page_setup_file
                2 -> return R.string.description_page_setup_database
            }
            throw IllegalArgumentException()
        }
    }

    // M E N U

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.setup, menu)
        menuInflater.inflate(R.menu.status, menu)
        menuInflater.inflate(R.menu.setup_file, menu)
        menuInflater.inflate(R.menu.setup_database, menu)
        // MenuCompat.setGroupDividerEnabled(menu, true)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val itemId = item.itemId
        if (MenuHandler.menuDispatch(this, item)) {
            return true
        } else if (itemId == R.id.action_diagnostics) {
            val intent = Intent(this, DiagnosticsActivity::class.java)
            startActivity(intent)
            return true
        } else if (itemId == R.id.action_logs) {
            val intent = Intent(this, LogsActivity::class.java)
            startActivity(intent)
            return true
        } else if (itemId == R.id.action_tables_and_indices) {
            val intent = ManagerContract.makeTablesAndIndexesIntent(this)
            intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_dbobject)
            startActivity(intent)
            return true
        } else if (itemId == R.id.action_dirs) {
            val message = reportStyledDirs(this)
            AlertDialog.Builder(this)
                .setTitle(R.string.action_dirs)
                .setMessage(message)
                .setNegativeButton(R.string.action_dismiss) { _: DialogInterface?, _: Int -> }
                .show()
        } else if (itemId == R.id.action_storage_dirs) {
            val dirs: Pair<Array<CharSequence>, Array<String>> = getStyledStorageDirectoriesNamesValues(this)
            val message = namesValuesToReportStyled(dirs)
            AlertDialog.Builder(this)
                .setTitle(R.string.action_storage_dirs)
                .setMessage(message)
                .setNegativeButton(R.string.action_dismiss) { _: DialogInterface?, _: Int -> }
                .show()
        } else if (itemId == R.id.action_cache_dirs) {
            val dirs: Pair<Array<CharSequence>, Array<String>> = getStyledCachesNamesValues(this)
            val message = namesValuesToReportStyled(dirs)
            AlertDialog.Builder(this)
                .setTitle(R.string.action_cache_dirs)
                .setMessage(message)
                .setNegativeButton(R.string.action_dismiss) { _: DialogInterface?, _: Int -> }
                .show()
        } else if (itemId == R.id.action_download_dirs) {
            val dirs: Pair<Array<CharSequence>, Array<String>> = getStyledDownloadNamesValues(this)
            val message = namesValuesToReportStyled(dirs)
            AlertDialog.Builder(this)
                .setTitle(R.string.action_download_dirs)
                .setMessage(message)
                .setNegativeButton(R.string.action_dismiss) { _: DialogInterface?, _: Int -> }
                .show()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        private const val TAG = "SetupA"
    }
}
