/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.wn

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat
import com.bbou.download.preference.Settings
import com.bbou.download.preference.Settings.Mode.Companion.getModePref
import org.sqlunet.browser.ColorUtils.getDrawable
import org.sqlunet.browser.Info.info
import org.sqlunet.browser.config.SetupDatabaseActivity
import org.sqlunet.browser.config.SetupDatabaseFragment
import org.sqlunet.browser.config.SetupStatusFragment
import org.sqlunet.browser.config.Utils.hrSize
import org.sqlunet.browser.config.Status
import org.sqlunet.browser.wn.lib.R
import org.sqlunet.settings.StorageSettings.getDatabasePath
import org.sqlunet.settings.StorageSettings.getDbDownloadSourcePath
import org.sqlunet.settings.StorageUtils.countToStorageString
import org.sqlunet.settings.StorageUtils.getFree
import java.io.File

/**
 * Setup Status fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SetupWnStatusFragment
/**
 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
 */
    : SetupStatusFragment() {
    // components
    private var imageTextSearchWn: ImageView? = null
    private var buttonTextSearchWn: ImageButton? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // images
        imageTextSearchWn = view.findViewById(R.id.status_searchtext_wn)

        // buttons
        buttonTextSearchWn = view.findViewById(R.id.searchtextWnButton)

        // click listeners
        buttonDb!!.setOnClickListener { download() }
        buttonIndexes!!.setOnClickListener { index() }
        buttonTextSearchWn!!.setOnClickListener {
            val index = resources.getInteger(R.integer.sql_statement_do_ts_wn_position)
            val intent = Intent(requireContext(), SetupDatabaseActivity::class.java)
            intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index)
            startActivity(intent)
        }
        infoDatabaseButton!!.setOnClickListener {
            val activity: Activity = requireActivity()
            val database = getDatabasePath(activity)
            val free = getFree(activity, database)
            val mode = getModePref(activity)
            val source = getDbDownloadSourcePath(activity, mode == Settings.Mode.DOWNLOAD_ZIP_THEN_UNZIP || mode == Settings.Mode.DOWNLOAD_ZIP)
            val status = Status.status(activity)
            val existsDb = status and Status.EXISTS != 0
            val existsTables = status and Status.EXISTS_TABLES != 0
            if (existsDb) {
                val size = File(database).length()
                val hrSize = countToStorageString(size) + " (" + size + ')'
                info(
                    activity, R.string.title_status,  //
                    getString(R.string.title_database), database,  //
                    getString(R.string.title_status), getString(R.string.status_database_exists) + '-' + getString(if (existsTables) R.string.status_data_exists else R.string.status_data_not_exists),  //
                    getString(R.string.title_free), free,  //
                    getString(R.string.size_expected), hrSize(R.integer.size_sqlunet_db, requireContext()),  //
                    getString(R.string.size_expected) + ' ' + getString(R.string.text_search), hrSize(R.integer.size_searchtext, requireContext()),  //
                    getString(R.string.size_expected) + ' ' + getString(R.string.total), hrSize(R.integer.size_db_working_total, requireContext()),  //
                    getString(R.string.size_current), hrSize
                )
            } else {
                info(
                    activity, R.string.title_dialog_info_download,  //
                    getString(R.string.title_operation), getString(R.string.info_op_download_database),  //
                    getString(R.string.title_from), source,  //
                    getString(R.string.title_database), database,  //
                    getString(R.string.title_free), free,  //
                    getString(R.string.size_expected), hrSize(R.integer.size_sqlunet_db, requireContext()),  //
                    getString(R.string.size_expected) + ' ' + getString(R.string.text_search), hrSize(R.integer.size_searchtext, requireContext()),  //
                    getString(R.string.size_expected) + ' ' + getString(R.string.total), hrSize(R.integer.size_db_working_total, requireContext()),  //
                    getString(R.string.title_status), getString(R.string.status_database_not_exists)
                )
            }
        }
    }
    // U P D A T E
    /**
     * Update status
     */
    override fun update() {
        super.update()
        val context = context
        if (context != null) {
            val status = Status.status(context)
            Log.d(TAG, "Status: $status")
            val existsDb = status and Status.EXISTS != 0
            val existsTables = status and Status.EXISTS_TABLES != 0
            if (existsDb && existsTables) {
                // images
                val okDrawable = getDrawable(context, R.drawable.ic_ok)
                val failDrawable = getDrawable(context, R.drawable.ic_fail)
                val existsTsWn = status and org.sqlunet.browser.wn.Status.EXISTS_TS_WN != 0
                imageTextSearchWn!!.setImageDrawable(if (existsTsWn) okDrawable else failDrawable)
                ImageViewCompat.setImageTintMode(imageTextSearchWn!!, if (existsTsWn) PorterDuff.Mode.SRC_IN else PorterDuff.Mode.DST)
                buttonTextSearchWn!!.setVisibility(if (existsTsWn) View.GONE else View.VISIBLE)
            } else {
                imageTextSearchWn!!.setImageResource(R.drawable.ic_unknown)
                buttonTextSearchWn!!.setVisibility(View.GONE)
            }
        }
    }

    companion object {
        private const val TAG = "SetupStatusF"
    }
}
