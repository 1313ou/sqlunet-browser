/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.xn

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
import org.sqlunet.browser.R
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
class SetupXnStatusFragment : SetupStatusFragment() {

    // components
    private var imagePm: ImageView? = null
    private var imageTextSearchWn: ImageView? = null
    private var imageTextSearchVn: ImageView? = null
    private var imageTextSearchPb: ImageView? = null
    private var imageTextSearchFn: ImageView? = null
    private var buttonPm: ImageButton? = null
    private var buttonTextSearchWn: ImageButton? = null
    private var buttonTextSearchVn: ImageButton? = null
    private var buttonTextSearchPb: ImageButton? = null
    private var buttonTextSearchFn: ImageButton? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // images
        imageTextSearchWn = view.findViewById(R.id.status_searchtext_wn)
        imageTextSearchVn = view.findViewById(R.id.status_searchtext_vn)
        imageTextSearchPb = view.findViewById(R.id.status_searchtext_pb)
        imageTextSearchFn = view.findViewById(R.id.status_searchtext_fn)
        imagePm = view.findViewById(R.id.status_predicatematrix)

        // buttons
        buttonTextSearchWn = view.findViewById(R.id.searchtextWnButton)
        buttonTextSearchVn = view.findViewById(R.id.searchtextVnButton)
        buttonTextSearchPb = view.findViewById(R.id.searchtextPbButton)
        buttonTextSearchFn = view.findViewById(R.id.searchtextFnButton)
        buttonPm = view.findViewById(R.id.predicatematrixButton)
        infoDatabaseButton = view.findViewById(R.id.info_database)

        // click listeners
        buttonDb!!.setOnClickListener { download() }
        buttonIndexes!!.setOnClickListener { index() }
        infoDatabaseButton!!.setOnClickListener { info() }
        buttonPm!!.setOnClickListener {
            val index = resources.getInteger(R.integer.sql_statement_do_predicatematrix_position)
            val intent = Intent(requireContext(), SetupDatabaseActivity::class.java)
            intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index)
            startActivity(intent)
        }
        buttonTextSearchWn!!.setOnClickListener {
            val index = resources.getInteger(R.integer.sql_statement_do_ts_wn_position)
            val intent = Intent(requireContext(), SetupDatabaseActivity::class.java)
            intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index)
            startActivity(intent)
        }
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
        buttonTextSearchFn!!.setOnClickListener {
            val index = resources.getInteger(R.integer.sql_statement_do_ts_fn_position)
            val intent = Intent(requireContext(), SetupDatabaseActivity::class.java)
            intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index)
            startActivity(intent)
        }
        infoDatabaseButton!!.setOnClickListener {
            val activity: Activity = requireActivity()
            val database = StorageSettings.getDatabasePath(activity)
            val free = getFree(activity, database)
            val mode = Mode.getModePref(activity)
            val source = StorageSettings.getDbDownloadSourcePath(activity, mode == Settings.Mode.DOWNLOAD_ZIP_THEN_UNZIP || mode == Settings.Mode.DOWNLOAD_ZIP)
            val status = XnStatus.status(activity)
            val existsDb = status and org.sqlunet.browser.config.Status.EXISTS != 0
            val existsTables = status and org.sqlunet.browser.config.Status.EXISTS_TABLES != 0
            if (existsDb) {
                val size = File(database).length()
                val hrSize = countToStorageString(size) + " (" + size + ')'
                info(
                    activity, R.string.title_status,
                    getString(R.string.title_database), database,
                    getString(R.string.title_status), getString(R.string.status_database_exists),
                    getString(R.string.title_status), getString(if (existsTables) R.string.status_data_exists else R.string.status_data_not_exists),
                    getString(R.string.title_free), free,
                    getString(R.string.size_expected), hrSize(R.integer.size_sqlunet_db, requireContext()), String.format(
                        "%s %s %s/%s/%s/%s",
                        getString(R.string.size_expected),
                        getString(R.string.text_search),
                        getString(R.string.wordnet),
                        getString(R.string.verbnet),
                        getString(R.string.propbank),
                        getString(R.string.framenet)
                    ), String.format(
                        "%s",
                        hrSize(R.integer.size_searchtext, requireContext())
                    ), String.format(
                        "%s %s %s",
                        getString(R.string.size_expected),
                        getString(R.string.total),
                        hrSize(R.integer.size_db_working_total, requireContext())
                    ),
                    getString(R.string.size_current), hrSize
                )
            } else {
                info(
                    activity, R.string.title_dialog_info_download,
                    getString(R.string.title_operation), getString(R.string.info_op_download_database),
                    getString(R.string.title_from), source,
                    getString(R.string.title_database), database,
                    getString(R.string.title_free), free, String.format(
                        "%s %s %s/%s/%s/%s",
                        getString(R.string.size_expected),
                        getString(R.string.text_search),
                        getString(R.string.wordnet),
                        getString(R.string.verbnet),
                        getString(R.string.propbank),
                        getString(R.string.framenet)
                    ), String.format(
                        "%s",
                        hrSize(R.integer.size_searchtext, requireContext())
                    ), String.format(
                        "%s %s",
                        getString(R.string.size_expected),
                        getString(R.string.total)
                    ),
                    hrSize(R.integer.size_db_working_total, requireContext()),
                    getString(R.string.title_status),
                    getString(R.string.status_database_not_exists)
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
            val status = XnStatus.status(context)
            Log.d(TAG, "Status: $status")
            val existsDb = status and org.sqlunet.browser.config.Status.EXISTS != 0
            val existsTables = status and org.sqlunet.browser.config.Status.EXISTS_TABLES != 0
            if (existsDb && existsTables) {
                val existsPm = status and XnStatus.EXISTS_PREDICATEMATRIX != 0
                val existsTsWn = status and XnStatus.EXISTS_TS_WN != 0
                val existsTsVn = status and XnStatus.EXISTS_TS_VN != 0
                val existsTsPb = status and XnStatus.EXISTS_TS_PB != 0
                val existsTsFn = status and XnStatus.EXISTS_TS_FN != 0

                // images
                val okDrawable = getDrawable(context, R.drawable.ic_ok)
                val failDrawable = getDrawable(context, R.drawable.ic_fail)
                imagePm!!.setImageDrawable(if (existsPm) okDrawable else failDrawable)
                ImageViewCompat.setImageTintMode(imagePm!!, if (existsPm) PorterDuff.Mode.SRC_IN else PorterDuff.Mode.DST)
                imageTextSearchFn!!.setImageDrawable(if (existsTsFn) okDrawable else failDrawable)
                ImageViewCompat.setImageTintMode(imageTextSearchFn!!, if (existsTsFn) PorterDuff.Mode.SRC_IN else PorterDuff.Mode.DST)
                imageTextSearchWn!!.setImageDrawable(if (existsTsWn) okDrawable else failDrawable)
                ImageViewCompat.setImageTintMode(imageTextSearchWn!!, if (existsTsWn) PorterDuff.Mode.SRC_IN else PorterDuff.Mode.DST)
                imageTextSearchVn!!.setImageDrawable(if (existsTsVn) okDrawable else failDrawable)
                ImageViewCompat.setImageTintMode(imageTextSearchVn!!, if (existsTsVn) PorterDuff.Mode.SRC_IN else PorterDuff.Mode.DST)
                imageTextSearchPb!!.setImageDrawable(if (existsTsPb) okDrawable else failDrawable)
                ImageViewCompat.setImageTintMode(imageTextSearchPb!!, if (existsTsPb) PorterDuff.Mode.SRC_IN else PorterDuff.Mode.DST)
                buttonPm!!.setVisibility(if (existsPm) View.GONE else View.VISIBLE)
                buttonTextSearchFn!!.setVisibility(if (existsTsFn) View.GONE else View.VISIBLE)
                buttonTextSearchWn!!.setVisibility(if (existsTsWn) View.GONE else View.VISIBLE)
                buttonTextSearchVn!!.setVisibility(if (existsTsVn) View.GONE else View.VISIBLE)
                buttonTextSearchPb!!.setVisibility(if (existsTsPb) View.GONE else View.VISIBLE)
                buttonTextSearchFn!!.setVisibility(if (existsTsFn) View.GONE else View.VISIBLE)
            } else {
                buttonPm!!.setVisibility(View.GONE)
                buttonTextSearchFn!!.setVisibility(View.GONE)
                buttonTextSearchWn!!.setVisibility(View.GONE)
                buttonTextSearchVn!!.setVisibility(View.GONE)
                buttonTextSearchPb!!.setVisibility(View.GONE)
                buttonTextSearchFn!!.setVisibility(View.GONE)
                imagePm!!.setImageResource(R.drawable.ic_unknown)
                imageTextSearchFn!!.setImageResource(R.drawable.ic_unknown)
                imageTextSearchWn!!.setImageResource(R.drawable.ic_unknown)
                imageTextSearchVn!!.setImageResource(R.drawable.ic_unknown)
                imageTextSearchPb!!.setImageResource(R.drawable.ic_unknown)
                imageTextSearchFn!!.setImageResource(R.drawable.ic_unknown)
            }
        }
    }

    companion object {

        private const val TAG = "SetupStatusF"
    }
}
