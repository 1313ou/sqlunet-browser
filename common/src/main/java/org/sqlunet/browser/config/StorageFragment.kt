/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.sqlunet.browser.AppContext
import org.sqlunet.browser.MenuHandler.menuDispatch
import org.sqlunet.browser.common.R
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
     * Swipe refresh layout
     */
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_storage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // swipe refresh
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout!!.setOnRefreshListener {
            update()

            // stop the refreshing indicator
            swipeRefreshLayout!!.isRefreshing = false
        }

        // menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // inflate
                menu.clear()
                menuInflater.inflate(R.menu.main, menu)
                menuInflater.inflate(R.menu.storage, menu)
                // MenuCompat.setGroupDividerEnabled(menu, true)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val context = requireContext()

                // handle item selection
                when (menuItem.itemId) {
                    R.id.action_dirs -> {
                        val message = reportStyledDirs(context)
                        AlertDialog.Builder(context)
                            .setTitle(R.string.action_dirs)
                            .setMessage(message)
                            .setNegativeButton(R.string.action_dismiss) { _: DialogInterface?, _: Int -> }
                            .show()
                        return true
                    }

                    R.id.action_storage_dirs -> {
                        val dirs = getStyledStorageDirectoriesNamesValues(context)
                        val message = namesValuesToReportStyled(dirs)
                        AlertDialog.Builder(context)
                            .setTitle(R.string.action_storage_dirs)
                            .setMessage(message)
                            .setNegativeButton(R.string.action_dismiss) { _: DialogInterface?, _: Int -> }
                            .show()
                        return true
                    }

                    R.id.action_cache_dirs -> {
                        val dirs = getStyledCachesNamesValues(context)
                        val message = namesValuesToReportStyled(dirs)
                        AlertDialog.Builder(context)
                            .setTitle(R.string.action_cache_dirs)
                            .setMessage(message)
                            .setNegativeButton(R.string.action_dismiss) { _: DialogInterface?, _: Int -> }
                            .show()
                        return true
                    }

                    R.id.action_download_dirs -> {
                        val dirs = getStyledDownloadNamesValues(context)
                        val message = namesValuesToReportStyled(dirs)
                        AlertDialog.Builder(context)
                            .setTitle(R.string.action_download_dirs)
                            .setMessage(message)
                            .setNegativeButton(R.string.action_dismiss) { _: DialogInterface?, _: Int -> }
                            .show()
                        return true
                    }

                    R.id.action_copy -> {
                        val sb = StringBuilder()

                        // db
                        sb.append(getString(R.string.title_database))
                            .append('\n')
                            .append(getSqlUNetStorage(context).absolutePath)
                            .append('\n')
                            .append('\n')

                            // storage
                            .append(getString(R.string.title_storage))
                            .append('\n')
                            .append(reportStorageDirectories(context))

                            // storage devices
                            .append(getString(R.string.title_external_storage_devices))
                            .append('\n')
                            .append(reportExternalStorage(context))

                        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Storage", sb)
                        clipboard.setPrimaryClip(clip)
                        return true
                    }

                    R.id.action_refresh -> {
                        // make sure that the SwipeRefreshLayout is displaying its refreshing indicator
                        if (!swipeRefreshLayout!!.isRefreshing) {
                            swipeRefreshLayout!!.isRefreshing = true
                        }
                        update()

                        // stop the refreshing indicator
                        swipeRefreshLayout!!.isRefreshing = false
                        return true
                    }

                    else -> return menuDispatch((requireActivity() as AppCompatActivity), menuItem)
                }
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED)
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

        // context
        val context = requireContext()

        // db
        val db = view.findViewById<TextView>(R.id.database)
        db.text = getSqlUNetStorage(AppContext.context).absolutePath

        // storage
        val storage = view.findViewById<TextView>(R.id.storage)
        storage.text = reportStyledStorageDirectories(context)

        // storage devices
        val storageDevices = view.findViewById<TextView>(R.id.storage_devices)
        storageDevices.text = reportStyledExternalStorage(context)
    }
}
