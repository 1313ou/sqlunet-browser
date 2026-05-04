/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.sqlunet.browser.AppContext
import org.sqlunet.browser.common.R
import org.sqlunet.browser.makeDialog
import org.sqlunet.settings.Storage.getSqlUNetStorage
import org.sqlunet.settings.StorageReports.getStyledCachesNamesValues
import org.sqlunet.settings.StorageReports.getStyledDownloadNamesValues
import org.sqlunet.settings.StorageReports.getStyledStorageDirectoriesNamesValues
import org.sqlunet.settings.StorageReports.namesValuesToReportStyled
import org.sqlunet.settings.StorageReports.reportExternalStorage
import org.sqlunet.settings.StorageReports.reportStorageDirectories
import org.sqlunet.settings.StorageReports.reportStyledDirs
import org.sqlunet.settings.StorageReports.reportStyledExternalStorage
import org.sqlunet.settings.StorageReports.reportStyledStorageDirectories

/**
 * Storage fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class StorageFragment : Fragment() {

    /**
     * Fragment menu provider
     */
    private val fragmentMenuProvider = object : MenuProvider {

        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.storage, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            val activityContext = requireContext()
            return when (menuItem.itemId) {
                R.id.action_dirs -> {
                    val message = reportStyledDirs(activityContext)
                    makeDialog(message, activityContext)
                        .setTitle(R.string.action_dirs)
                        .show()
                    true
                }

                R.id.action_storage_dirs -> {
                    val dirs = getStyledStorageDirectoriesNamesValues(activityContext)
                    val message = namesValuesToReportStyled(dirs)
                    makeDialog(message, activityContext)
                        .setTitle(R.string.action_storage_dirs)
                        .show()
                    true
                }

                R.id.action_cache_dirs -> {
                    val dirs = getStyledCachesNamesValues(activityContext)
                    val message = namesValuesToReportStyled(dirs)
                    makeDialog(message,activityContext)
                        .setTitle(R.string.action_cache_dirs)
                        .show()
                    true
                }

                R.id.action_download_dirs -> {
                    val dirs = getStyledDownloadNamesValues(activityContext)
                    val message = namesValuesToReportStyled(dirs)
                    makeDialog(message,activityContext)
                        .setTitle(R.string.action_download_dirs)
                        .show()
                    true
                }

                R.id.action_copy -> {
                    val sb = StringBuilder()

                    // db
                    sb.append(getString(R.string.title_database))
                        .append('\n')
                        .append(getSqlUNetStorage(activityContext).absolutePath)
                        .append('\n')
                        .append('\n')

                        // storage
                        .append(getString(R.string.title_storage))
                        .append('\n')
                        .append(reportStorageDirectories(activityContext))

                        // storage devices
                        .append(getString(R.string.title_external_storage_devices))
                        .append('\n')
                        .append(reportExternalStorage(activityContext))

                    val clipboard = AppContext.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Storage", sb)
                    clipboard.setPrimaryClip(clip)
                    true
                }

                R.id.action_refresh -> {
                    // make sure that the SwipeRefreshLayout is displaying its refreshing indicator
                    if (!swipeRefreshLayout!!.isRefreshing) {
                        swipeRefreshLayout!!.isRefreshing = true
                    }
                    update()

                    // stop the refreshing indicator
                    swipeRefreshLayout!!.isRefreshing = false
                    true
                }

                else -> false
            }
        }
    }

    /**
     * Swipe refresh layout
     */
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_storage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // swipe refresh
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout.setOnRefreshListener {
            update()

            // stop the refreshing indicator
            swipeRefreshLayout.isRefreshing = false
        }

        // menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(fragmentMenuProvider, getViewLifecycleOwner(), Lifecycle.State.RESUMED)
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    /**
     * Update status
     */
    private fun update() {

        // view
        val view = requireView()

        // db
        val db = view.findViewById<TextView>(R.id.database)
        db.text = getSqlUNetStorage(AppContext.context).absolutePath

        // context
        val activityContext = requireContext()

        // storage
        val storage = view.findViewById<TextView>(R.id.storage)
        storage.text = reportStyledStorageDirectories(activityContext)

        // storage devices
        val storageDevices = view.findViewById<TextView>(R.id.storage_devices)
        storageDevices.text = reportStyledExternalStorage(activityContext)
    }
}
