/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.fn

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
import com.bbou.download.preference.Settings.Mode
import org.sqlunet.browser.ColorUtils.getDrawable
import org.sqlunet.browser.Info.info
import org.sqlunet.browser.config.SetupDatabaseActivity
import org.sqlunet.browser.config.SetupDatabaseFragment
import org.sqlunet.browser.config.SetupStatusFragment
import org.sqlunet.browser.config.Utils.hrSize
import org.sqlunet.settings.StorageSettings
import org.sqlunet.settings.StorageUtils.countToStorageString
import org.sqlunet.settings.StorageUtils.getFree
import java.io.File

/**
 * Setup Status fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SetupFnStatusFragment : SetupStatusFragment() {

    // components
    private var imageTextSearchFn: ImageView? = null
    private var buttonTextSearchFn: ImageButton? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // images
        imageTextSearchFn = view.findViewById(R.id.status_searchtext_fn)

        // buttons
        buttonTextSearchFn = view.findViewById(R.id.searchtextFnButton)

        // click listeners
        buttonDb!!.setOnClickListener { download() }
        buttonIndexes!!.setOnClickListener { index() }
        infoDatabaseButton!!.setOnClickListener { info() }
        buttonTextSearchFn!!.setOnClickListener {
            val index = resources.getInteger(R.integer.sql_statement_do_ts_fn_position)
            val intent = Intent(requireContext(), SetupDatabaseActivity::class.java)
            intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index)
            startActivity(intent)
        }
        infoDatabaseButton!!.setOnClickListener {
            val activity: Activity = requireActivity()
            val database = StorageSettings.getDatabasePath(activity)
            val free = getFree(requireContext(), database)
            val mode = Mode.getModePref(activity)
            val source = StorageSettings.getDbDownloadSourcePath(activity, mode == Mode.DOWNLOAD_ZIP_THEN_UNZIP || mode == Mode.DOWNLOAD_ZIP)
            val status = FnStatus.status(activity)
            val existsDb = status and org.sqlunet.browser.config.Status.EXISTS != 0
            val existsTables = status and org.sqlunet.browser.config.Status.EXISTS_TABLES != 0
            if (existsDb) {
                val size = File(database).length()
                val hrSize = countToStorageString(size) + " (" + size + ')'
                info(
                    activity, R.string.title_status,
                    getString(R.string.title_database), database,
                    getString(R.string.title_status), getString(R.string.status_database_exists) + '-' + getString(if (existsTables) R.string.status_data_exists else R.string.status_data_not_exists),
                    getString(R.string.title_free), free,
                    getString(R.string.size_expected), hrSize(R.integer.size_sqlunet_db, requireContext()),
                    getString(R.string.size_expected) + ' ' + getString(R.string.text_search), hrSize(R.integer.size_searchtext, requireContext()),
                    getString(R.string.size_expected) + ' ' + getString(R.string.total), hrSize(R.integer.size_db_working_total, requireContext()),
                    getString(R.string.size_current), hrSize
                )
            } else {
                info(
                    activity, R.string.title_dialog_info_download,
                    getString(R.string.title_operation), getString(R.string.info_op_download_database),
                    getString(R.string.title_from), source,
                    getString(R.string.title_database), database,
                    getString(R.string.title_free), free,
                    getString(R.string.size_expected), hrSize(R.integer.size_sqlunet_db, requireContext()),
                    getString(R.string.size_expected) + ' ' + getString(R.string.text_search), hrSize(R.integer.size_searchtext, requireContext()),
                    getString(R.string.size_expected) + ' ' + getString(R.string.total), hrSize(R.integer.size_db_working_total, requireContext()),
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
            val status = FnStatus.status(context)
            Log.d(TAG, "Status: $status")
            val existsDb = status and org.sqlunet.browser.config.Status.EXISTS != 0
            val existsTables = status and org.sqlunet.browser.config.Status.EXISTS_TABLES != 0
            if (existsDb && existsTables) {
                // images
                val okDrawable = getDrawable(context, R.drawable.ic_ok)
                val failDrawable = getDrawable(context, R.drawable.ic_fail)
                val existsTsFn = status and FnStatus.EXISTS_TS_FN != 0
                imageTextSearchFn!!.setImageDrawable(if (existsTsFn) okDrawable else failDrawable)
                ImageViewCompat.setImageTintMode(imageTextSearchFn!!, if (existsTsFn) PorterDuff.Mode.SRC_IN else PorterDuff.Mode.DST)
                buttonTextSearchFn!!.visibility = if (existsTsFn) View.GONE else View.VISIBLE
            } else {
                buttonTextSearchFn!!.visibility = View.GONE
                imageTextSearchFn!!.setImageResource(R.drawable.ic_unknown)
            }
        }
    }

    companion object {

        private const val TAG = "SetupStatusF"
    }
}
