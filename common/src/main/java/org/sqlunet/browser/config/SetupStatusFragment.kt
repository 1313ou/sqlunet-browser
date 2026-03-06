/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bbou.download.preference.Settings.Mode
import org.sqlunet.browser.AppContext
import org.sqlunet.browser.ColorUtils.getDrawable
import org.sqlunet.browser.EntryActivity.Companion.rerun
import org.sqlunet.browser.Info.info
import org.sqlunet.browser.common.R
import org.sqlunet.browser.config.DownloadIntentFactory.makeIntent
import org.sqlunet.browser.config.Utils.hrSize
import org.sqlunet.settings.StorageSettings
import org.sqlunet.settings.StorageUtils.countToStorageString
import org.sqlunet.settings.StorageUtils.getFree
import java.io.File
import org.sqlunet.xnet.R as XNetR

/**
 * Base Status fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class SetupStatusFragment : Fragment(), Updatable {

    private lateinit var imageDb: ImageView

    private lateinit var imageIndexes: ImageView

    protected lateinit var buttonDb: Button

    protected lateinit var buttonIndexes: Button

    protected lateinit var infoDatabaseButton: Button

    /**
     * Activity result launcher
     */
    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null

    /**
     * Swipe refresh layout
     */
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val success = result.resultCode == Activity.RESULT_OK
            Log.d(TAG, "Download " + if (success) "succeeded" else "failed")
            update()
            if (success) {
                val activityContext = requireContext()
                Toast.makeText(activityContext, R.string.title_download_complete, Toast.LENGTH_SHORT).show()
                rerun(activityContext)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // images
        imageDb = view.findViewById(R.id.status_database)
        imageIndexes = view.findViewById(R.id.status_indexes)

        // buttons
        buttonDb = view.findViewById(R.id.databaseButton)
        buttonIndexes = view.findViewById(R.id.indexesButton)
        infoDatabaseButton = view.findViewById(R.id.info_database)

        // swipe refresh layout
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout!!.setOnRefreshListener {
            update()
            swipeRefreshLayout!!.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            update()
        }
    }

    protected fun download() {
        val intent = makeIntent(AppContext.context)
        activityResultLauncher!!.launch(intent)
    }

    protected fun index() {
        val index = resources.getInteger(R.integer.sql_statement_do_indexes_position)
        val intent = Intent(AppContext.context, SetupDatabaseActivity::class.java)
        intent.putExtra(SetupDatabaseFragment.ARG_POSITION, index)
        startActivity(intent)
    }

    protected fun info() {
        val activity: Activity = requireActivity()
        val database = StorageSettings.getDatabasePath(activity)
        val free = getFree(activity, database)
        val mode = Mode.getModePref(activity)
        val source = StorageSettings.getDbDownloadSourcePath(activity, mode == Mode.DOWNLOAD_ZIP_THEN_UNZIP || mode == Mode.DOWNLOAD_ZIP)
        val status = Status.status(activity)
        val existsDb = status and Status.EXISTS != 0
        val existsTables = status and Status.EXISTS_TABLES != 0
        if (existsDb) {
            val size = File(database).length()
            val hrSize = countToStorageString(size) + " (" + size + ')'
            info(
                activity, R.string.title_status,
                getString(R.string.title_database), database,
                getString(R.string.title_status), getString(R.string.status_database_exists),
                getString(R.string.title_status), getString(if (existsTables) R.string.status_data_exists else R.string.status_data_not_exists),
                getString(R.string.title_free), free,
                getString(R.string.size_expected), hrSize(XNetR.integer.size_sqlunet_db, AppContext.context),
                getString(R.string.size_expected) + ' ' + getString(R.string.text_search), hrSize(XNetR.integer.size_searchtext, AppContext.context),
                getString(R.string.size_expected) + ' ' + getString(R.string.total), hrSize(XNetR.integer.size_db_working_total, AppContext.context),
                getString(R.string.size_current), hrSize
            )
        } else {
            info(
                activity, R.string.title_dialog_info_download,
                getString(R.string.title_operation), getString(R.string.info_op_download_database),
                getString(R.string.title_from), source,
                getString(R.string.title_database), database,
                getString(R.string.title_free), free,
                getString(R.string.size_expected) + ' ' + getString(R.string.text_search), hrSize(XNetR.integer.size_searchtext, AppContext.context),
                getString(R.string.size_expected) + ' ' + getString(R.string.total), hrSize(XNetR.integer.size_db_working_total, AppContext.context),
                getString(R.string.title_status), getString(R.string.status_database_not_exists)
            )
        }
    }

    // U P D A T E

    override fun update() {
        val context = context
        if (context != null) {
            val status = Status.status(context)
            Log.d(TAG, "Status: $status")

            // images.
            val okDrawable = getDrawable(context, R.drawable.ic_ok)
            val failDrawable = getDrawable(context, R.drawable.ic_fail)
            val existsDb = status and Status.EXISTS != 0
            val existsTables = status and Status.EXISTS_TABLES != 0
            if (existsDb && existsTables) {
                imageDb.setImageDrawable(okDrawable)
                ImageViewCompat.setImageTintMode(imageDb, PorterDuff.Mode.SRC_IN)
                buttonDb.visibility = View.GONE
                val existsIndexes = status and Status.EXISTS_INDEXES != 0
                imageIndexes.setImageDrawable(if (existsIndexes) okDrawable else failDrawable)
                ImageViewCompat.setImageTintMode(imageIndexes, if (existsIndexes) PorterDuff.Mode.SRC_IN else PorterDuff.Mode.DST)
                buttonIndexes.visibility = if (existsIndexes) View.GONE else View.VISIBLE
            } else {
                imageDb.setImageDrawable(failDrawable)
                ImageViewCompat.setImageTintMode(imageDb, PorterDuff.Mode.DST)
                buttonDb.visibility = View.VISIBLE
                buttonIndexes.visibility = View.GONE
                imageIndexes.setImageResource(R.drawable.ic_unknown)
            }
        }
    }

    companion object {

        private const val TAG = "SetupStatusF"
    }
}
