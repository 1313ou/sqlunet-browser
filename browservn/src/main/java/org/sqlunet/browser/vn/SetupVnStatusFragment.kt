/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.vn

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
class SetupVnStatusFragment : SetupStatusFragment() {

    // components
    private var imageTextSearchVn: ImageView? = null
    private var imageTextSearchPb: ImageView? = null
    private var buttonTextSearchVn: ImageButton? = null
    private var buttonTextSearchPb: ImageButton? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // images
        imageTextSearchVn = view.findViewById(R.id.status_searchtext_vn)
        imageTextSearchPb = view.findViewById(R.id.status_searchtext_pb)

        // buttons
        buttonTextSearchVn = view.findViewById(R.id.searchtextVnButton)
        buttonTextSearchPb = view.findViewById(R.id.searchtextPbButton)

        // click listeners
        buttonDb!!.setOnClickListener { download() }
        buttonIndexes!!.setOnClickListener { index() }
        infoDatabaseButton!!.setOnClickListener { info() }
        buttonTextSearchVn!!.setOnClickListener {
            val index = resources.getInteger(R.integer.sql_statement_do_ts_vn_position)
            val intent = Intent(requireContext(), SetupDatabaseActivity::class.java)
            intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index)
            startActivity(intent)
        }
        buttonTextSearchPb!!.setOnClickListener {
            val index = resources.getInteger(R.integer.sql_statement_do_ts_pb_position)
            val intent = Intent(requireContext(), SetupDatabaseActivity::class.java)
            intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index)
            startActivity(intent)
        }
        infoDatabaseButton!!.setOnClickListener {
            val activity: Activity = requireActivity()
            val database = StorageSettings.getDatabasePath(activity)
            val free = getFree(activity, database)
            val mode = Mode.getModePref(activity)
            val source = StorageSettings.getDbDownloadSourcePath(activity, mode == Mode.DOWNLOAD_ZIP_THEN_UNZIP || mode == Mode.DOWNLOAD_ZIP)
            val status = VnStatus.status(activity)
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
            val status = VnStatus.status(context)
            Log.d(TAG, "Status: $status")
            val existsDb = status and org.sqlunet.browser.config.Status.EXISTS != 0
            val existsTables = status and org.sqlunet.browser.config.Status.EXISTS_TABLES != 0
            if (existsDb && existsTables) {
                // images
                val okDrawable = getDrawable(context, R.drawable.ic_ok)
                val failDrawable = getDrawable(context, R.drawable.ic_fail)
                val existsTsVn = status and VnStatus.EXISTS_TS_VN != 0
                val existsTsPb = status and VnStatus.EXISTS_TS_PB != 0
                imageTextSearchVn!!.setImageDrawable(if (existsTsVn) okDrawable else failDrawable)
                ImageViewCompat.setImageTintMode(imageTextSearchVn!!, if (existsTsPb) PorterDuff.Mode.SRC_IN else PorterDuff.Mode.DST)
                imageTextSearchPb!!.setImageDrawable(if (existsTsPb) okDrawable else failDrawable)
                ImageViewCompat.setImageTintMode(imageTextSearchPb!!, if (existsTsPb) PorterDuff.Mode.SRC_IN else PorterDuff.Mode.DST)
                buttonTextSearchVn!!.setVisibility(if (existsTsVn) View.GONE else View.VISIBLE)
                buttonTextSearchPb!!.setVisibility(if (existsTsPb) View.GONE else View.VISIBLE)
            } else {
                buttonTextSearchVn!!.setVisibility(View.GONE)
                imageTextSearchVn!!.setImageResource(R.drawable.ic_unknown)
                buttonTextSearchPb!!.setVisibility(View.GONE)
                imageTextSearchPb!!.setImageResource(R.drawable.ic_unknown)
            }
        }
    }

    companion object {

        private const val TAG = "SetupStatusF"
    }
}
