/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.sn

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat
import com.bbou.download.preference.Settings.Mode
import org.sqlunet.browser.AppContext
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
import org.sqlunet.browser.common.R as CommonR
import org.sqlunet.wordnet.R as WordnetR

/**
 * Setup Status fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SetupSnStatusFragment : SetupStatusFragment() {

    // components
    private var imageTextSearchWn: ImageView? = null

    private var buttonTextSearchWn: ImageButton? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // images
        imageTextSearchWn = view.findViewById(R.id.status_searchtext_wn)

        // button
        buttonTextSearchWn = view.findViewById(R.id.searchtextWnButton)

        // click listeners
        buttonDb!!.setOnClickListener { download() }
        buttonIndexes!!.setOnClickListener { index() }
        buttonTextSearchWn!!.setOnClickListener {
            val index = resources.getInteger(R.integer.sql_statement_do_ts_wn_position)
            val intent = Intent(AppContext.context, SetupDatabaseActivity::class.java)
            intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index)
            startActivity(intent)
        }
        infoDatabaseButton!!.setOnClickListener {
            val activity: Activity = requireActivity()
            val database = StorageSettings.getDatabasePath(activity)
            val free = getFree(activity, database)
            val mode = Mode.getModePref(activity)
            val source = StorageSettings.getDbDownloadSourcePath(activity, mode == Mode.DOWNLOAD_ZIP_THEN_UNZIP || mode == Mode.DOWNLOAD_ZIP)
            val status = SnStatus.status(activity)
            val existsDb = status and org.sqlunet.browser.config.Status.EXISTS != 0
            val existsTables = status and org.sqlunet.browser.config.Status.EXISTS_TABLES != 0
            if (existsDb) {
                val size = File(database).length()
                val hrSize = countToStorageString(size) + " (" + size + ')'
                info(
                    activity, CommonR.string.title_status,
                    getString(CommonR.string.title_database), database,
                    getString(CommonR.string.title_status), getString(CommonR.string.status_database_exists),
                    getString(CommonR.string.title_status), getString(if (existsTables) CommonR.string.status_data_exists else CommonR.string.status_data_not_exists),
                    getString(CommonR.string.title_free), free,
                    getString(CommonR.string.size_expected), hrSize(R.integer.size_sqlunet_db, AppContext.context),
                    getString(CommonR.string.size_expected) + ' ' + getString(CommonR.string.text_search) + ' ' + getString(WordnetR.string.wordnet), hrSize(R.integer.size_searchtext, AppContext.context),
                    getString(CommonR.string.size_expected) + ' ' + getString(CommonR.string.total), hrSize(R.integer.size_db_working_total, AppContext.context),
                    getString(CommonR.string.size_current), hrSize
                )
            } else {
                info(
                    activity, CommonR.string.title_dialog_info_download,
                    getString(CommonR.string.title_operation), getString(CommonR.string.info_op_download_database),
                    getString(CommonR.string.title_from), source,
                    getString(CommonR.string.title_database), database,
                    getString(CommonR.string.title_free), free,
                    getString(CommonR.string.size_expected) + ' ' + getString(CommonR.string.text_search) + ' ' + getString(WordnetR.string.wordnet), hrSize(R.integer.size_searchtext, AppContext.context),
                    getString(CommonR.string.size_expected) + ' ' + getString(CommonR.string.total), hrSize(R.integer.size_db_working_total, AppContext.context),
                    getString(CommonR.string.title_status), getString(CommonR.string.status_database_not_exists)
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
            val status = SnStatus.status(context)
            Log.d(TAG, "Status: $status")
            val existsDb = status and org.sqlunet.browser.config.Status.EXISTS != 0
            val existsTables = status and org.sqlunet.browser.config.Status.EXISTS_TABLES != 0
            if (existsDb && existsTables) {
                val existsTsWn = status and SnStatus.EXISTS_TS_WN != 0

                // images
                val okDrawable = getDrawable(context, CommonR.drawable.ic_ok)
                val failDrawable = getDrawable(context, CommonR.drawable.ic_fail)
                imageTextSearchWn!!.setImageDrawable(if (existsTsWn) okDrawable else failDrawable)
                ImageViewCompat.setImageTintMode(imageTextSearchWn!!, if (existsTsWn) PorterDuff.Mode.SRC_IN else PorterDuff.Mode.DST)
                buttonTextSearchWn!!.visibility = if (existsTsWn) View.GONE else View.VISIBLE
            } else {
                imageTextSearchWn!!.setImageResource(CommonR.drawable.ic_unknown)
                buttonTextSearchWn!!.visibility = View.GONE
            }
        }
    }

    companion object {

        private const val TAG = "SetupStatusF"
    }
}
