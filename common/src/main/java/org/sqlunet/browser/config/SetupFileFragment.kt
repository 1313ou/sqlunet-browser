/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import com.bbou.download.preference.Settings
import com.bbou.download.preference.Settings.Mode.Companion.getModePref
import com.bbou.download.preference.Settings.unrecordDatapack
import org.sqlunet.browser.EntryActivity.Companion.rerun
import org.sqlunet.browser.Info.build
import org.sqlunet.browser.common.R
import org.sqlunet.browser.config.DownloadIntentFactory.makeIntent
import org.sqlunet.browser.config.DownloadIntentFactory.makeIntentDownloadThenDeploy
import org.sqlunet.browser.config.Operations.copy
import org.sqlunet.browser.config.Operations.md5
import org.sqlunet.browser.config.Operations.unzip
import org.sqlunet.browser.config.Permissions.check
import org.sqlunet.browser.config.SetupDatabaseTasks.createDatabase
import org.sqlunet.browser.config.SetupDatabaseTasks.deleteDatabase
import org.sqlunet.browser.config.SetupDatabaseTasks.update
import org.sqlunet.browser.config.Utils.confirm
import org.sqlunet.browser.config.Utils.hrSize
import org.sqlunet.settings.Settings.Companion.getZipEntry
import org.sqlunet.settings.StorageSettings.getCachedZippedPath
import org.sqlunet.settings.StorageSettings.getDatabasePath
import org.sqlunet.settings.StorageSettings.getDbDownloadName
import org.sqlunet.settings.StorageSettings.getDbDownloadSourcePath
import org.sqlunet.settings.StorageSettings.getDbDownloadZippedSourcePath
import org.sqlunet.settings.StorageUtils.getFree
import java.io.File

/**
 * Set up fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SetupFileFragment : BaseTaskFragment() {

    /**
     * Operations
     */
    enum class Operation {
        CREATE,
        DROP,
        COPY_URI,
        UNZIP_URI,
        UNZIP_ENTRY_URI,
        MD5_URI,
        COPY_FILE,
        UNZIP_FILE,
        MD5_FILE,
        DOWNLOAD,
        DOWNLOAD_ZIPPED,
        UPDATE;

        companion object {

            lateinit var operations: Array<CharSequence>

            fun fromIndex(index: Int): Operation? {
                val operation = operations[index]
                return if (operation.isEmpty()) {
                    null
                } else valueOf(operation.toString())
            }
        }
    }

    init {
        layoutId = R.layout.fragment_setup_file
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // operations
        Operation.operations = resources.getTextArray(R.array.setup_files_values)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // args (relies on order of resources matching that of DO_)
        val args = arguments
        if (args != null) {
            val arg = args.getString(ARG)
            if (arg != null) {
                val op = Operation.valueOf(arg)
                spinner!!.setSelection(op.ordinal + 1)
            }
        }
        runButton!!.setOnClickListener {

            // skip first
            val id = spinner!!.selectedItemId
            if (id == 0L) {
                return@setOnClickListener
            }

            // execute
            val success: Boolean
            val activity = requireActivity()
            val op = Operation.fromIndex(id.toInt())
            if (op != null) {
                when (op) {
                    Operation.CREATE -> {
                        status!!.setText(R.string.status_task_running)
                        success = createDatabase(activity, getDatabasePath(activity))
                        status!!.setText(if (success) R.string.status_task_done else R.string.status_task_failed)
                        unrecordDatapack(activity)
                    }

                    Operation.DROP -> confirm(activity, R.string.title_setup_drop, R.string.ask_drop) {
                        status!!.setText(R.string.status_task_running)
                        val success1 = deleteDatabase(activity, getDatabasePath(activity))
                        status!!.setText(if (success1) R.string.status_task_done else R.string.status_task_failed)
                        unrecordDatapack(activity)
                        rerun(activity)
                    }

                    Operation.COPY_URI -> if (check(activity)) {
                        val intent2 = Intent(activity, OperationActivity::class.java)
                        intent2.putExtra(OperationActivity.ARG_OP, OperationActivity.OP_COPY)
                        intent2.putExtra(OperationActivity.ARG_TYPES, arrayOf("application/vnd.sqlite3"))
                        activity.startActivity(intent2)
                    }

                    Operation.UNZIP_URI -> if (check(activity)) {
                        val intent2 = Intent(activity, OperationActivity::class.java)
                        intent2.putExtra(OperationActivity.ARG_OP, OperationActivity.OP_UNZIP)
                        intent2.putExtra(OperationActivity.ARG_TYPES, arrayOf("application/zip"))
                        activity.startActivity(intent2)
                    }

                    Operation.UNZIP_ENTRY_URI -> if (check(activity)) {
                        val intent2 = Intent(activity, OperationActivity::class.java)
                        intent2.putExtra(OperationActivity.ARG_OP, OperationActivity.OP_UNZIP_ENTRY)
                        intent2.putExtra(OperationActivity.ARG_TYPES, arrayOf("application/zip"))
                        intent2.putExtra(OperationActivity.ARG_ZIP_ENTRY, getZipEntry(requireContext(), getDbDownloadName(requireContext())))
                        activity.startActivity(intent2)
                    }

                    Operation.MD5_URI -> if (check(activity)) {
                        val intent2 = Intent(activity, OperationActivity::class.java)
                        intent2.putExtra(OperationActivity.ARG_OP, OperationActivity.OP_MD5)
                        intent2.putExtra(OperationActivity.ARG_TYPES, arrayOf("*/*"))
                        activity.startActivity(intent2)
                    }

                    Operation.COPY_FILE -> if (check(activity)) {
                        copy(activity)
                    }

                    Operation.UNZIP_FILE -> if (check(activity)) {
                        unzip(activity)
                    }

                    Operation.MD5_FILE -> if (check(activity)) {
                        md5(activity)
                    }

                    Operation.DOWNLOAD -> {
                        val intent2 = makeIntent(activity)
                        activity.startActivity(intent2)
                    }

                    Operation.DOWNLOAD_ZIPPED -> {
                        val intent3 = makeIntentDownloadThenDeploy(activity)
                        activity.startActivity(intent3)
                    }

                    Operation.UPDATE -> confirm(activity, R.string.title_setup_update, R.string.askUpdate) { update(activity) }
                }
            }
        }
    }

    override fun makeAdapter(): SpinnerAdapter {
        // create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.setup_files_titles, R.layout.spinner_item_task)

        // specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_item_task_dropdown)
        return adapter
    }

    override fun select(position: Int) {
        var message: SpannableStringBuilder? = null
        val op = Operation.fromIndex(position)
        if (op != null) {
            when (op) {
                Operation.CREATE -> message = statusCreate()
                Operation.DROP -> message = statusDrop()
                Operation.COPY_URI -> {
                    message = statusCopy()
                    message.append(requireContext().getString(R.string.from_uri))
                }

                Operation.COPY_FILE -> {
                    message = statusCopy()
                    message.append(requireContext().getString(R.string.from_file))
                }

                Operation.UNZIP_URI, Operation.UNZIP_ENTRY_URI -> {
                    message = statusUnzip()
                    message.append(requireContext().getString(R.string.from_uri))
                }

                Operation.UNZIP_FILE -> {
                    message = statusUnzip()
                    message.append(requireContext().getString(R.string.from_file))
                }

                Operation.MD5_URI -> {
                    message = statusMd5()
                    message.append(' ')
                    message.append(requireContext().getString(R.string.from_uri))
                }

                Operation.MD5_FILE -> {
                    message = statusMd5()
                    message.append(' ')
                    message.append(requireContext().getString(R.string.from_file))
                }

                Operation.DOWNLOAD -> message = statusDownload()
                Operation.DOWNLOAD_ZIPPED -> message = statusDownloadZipped()
                Operation.UPDATE -> message = statusUpdate()
            }
        }
        status!!.text = message
    }

    /**
     * Operation status for create
     *
     * @return status string
     */
    private fun statusCreate(): SpannableStringBuilder {
        val context = requireContext()
        val database = getDatabasePath(context)
        val free = getFree(context, database)
        val databaseExists = File(database).exists()
        val sb = SpannableStringBuilder()
        sb.append(getString(R.string.info_op_create_database))
        sb.append("\n\n")
        build(
            sb,  
            getString(R.string.title_database), database,  
            getString(R.string.title_status), getString(if (databaseExists) R.string.status_database_exists else R.string.status_database_not_exists),  
            getString(R.string.title_free), free
        )
        return sb
    }

    /**
     * Operation status for create
     *
     * @return status string
     */
    private fun statusDrop(): SpannableStringBuilder {
        val context = requireContext()
        val database = getDatabasePath(context)
        val free = getFree(context, database)
        val databaseExists = File(database).exists()
        val sb = SpannableStringBuilder()
        sb.append(getString(R.string.info_op_drop_database))
        sb.append("\n\n")
        build(
            sb,  
            getString(R.string.title_database), database,  
            getString(R.string.title_status), getString(if (databaseExists) R.string.status_database_exists else R.string.status_database_not_exists),  
            getString(R.string.title_free), free
        )
        return sb
    }

    /**
     * Operation status for copy from uri
     *
     * @return status string
     */
    private fun statusCopy(): SpannableStringBuilder {
        val context = requireContext()
        val database = getDatabasePath(context)
        val free = getFree(context, database)
        val databaseExists = File(database).exists()
        /*
		String fromPath = Settings.getCachePref(context)
		boolean sourceExists = false
		if (fromPath != null)
		{
			fromPath += File.separatorChar + Storage.DBFILE
			sourceExists = new File(fromPath).exists()
		}
		 */
        val sb = SpannableStringBuilder()
        sb.append(getString(R.string.info_op_copy_database))
        sb.append("\n\n")
        build(
            sb,  
            getString(R.string.title_database), database,  
            getString(R.string.title_status), getString(if (databaseExists) R.string.status_database_exists else R.string.status_database_not_exists),  
            getString(R.string.size_expected), hrSize(R.integer.size_sqlunet_db, requireContext()),  
            getString(R.string.size_expected) + ' ' + getString(R.string.total), hrSize(R.integer.size_db_working_total, requireContext()),  
            getString(R.string.title_free), free,  
            "\n", "",  
            getString(R.string.title_from),  
            // fromPath, 
            // getString(R.string.title_status), getString(sourceExists ? R.string.status_source_exists : R.string.status_source_not_exists)
            getString(R.string.title_selection)
        )
        return sb
    }

    /**
     * Operation status for unzip
     *
     * @return status string
     */
    private fun statusUnzip(): SpannableStringBuilder {
        val context = requireContext()
        val database = getDatabasePath(context)
        val free = getFree(context, database)
        val databaseExists = File(database).exists()
        /*
		String fromPath = Settings.getCachePref(context)
		boolean sourceExists = false
		if (fromPath != null)
		{
			fromPath += File.separatorChar + Storage.DBFILEZIP
			sourceExists = new File(fromPath).exists()
		}
		*/
        val sb = SpannableStringBuilder()
        sb.append(getString(R.string.info_op_unzip_database))
        sb.append("\n\n")
        build(
            sb,  
            getString(R.string.title_database), database,  
            getString(R.string.title_status), getString(if (databaseExists) R.string.status_database_exists else R.string.status_database_not_exists),  
            getString(R.string.size_expected), hrSize(R.integer.size_sqlunet_db, requireContext()),  
            getString(R.string.size_expected) + ' ' + getString(R.string.total), hrSize(R.integer.size_db_working_total, requireContext()),  
            getString(R.string.title_free), free,  
            "\n", "",  
            getString(R.string.title_from),  //fromPath, 
            //getString(R.string.title_status), getString(sourceExists ? R.string.status_source_exists : R.string.status_source_not_exists)
            getString(R.string.title_selection)
        )
        return sb
    }

    /**
     * Operation status for md5
     *
     * @return status string
     */
    private fun statusMd5(): SpannableStringBuilder {
        val sb = SpannableStringBuilder()
        sb.append(getString(R.string.info_op_md5))
        return sb
    }

    /**
     * Operation status for update database
     *
     * @return status string
     */
    private fun statusUpdate(): SpannableStringBuilder {
        val sb = SpannableStringBuilder()
        sb.append(getString(R.string.info_op_drop_database))
        sb.append('\n')
        sb.append(statusDownload())
        return sb
    }

    /**
     * Operation status for download database
     *
     * @return status string
     */
    private fun statusDownload(): SpannableStringBuilder {
        val context = requireContext()
        val mode = getModePref(context)
        val from = getDbDownloadSourcePath(context, mode == Settings.Mode.DOWNLOAD_ZIP_THEN_UNZIP || mode == Settings.Mode.DOWNLOAD_ZIP)
        val to = getDatabasePath(context)
        val free = getFree(context, to)
        val targetExists = File(to).exists()
        val sb = SpannableStringBuilder()
        sb.append(getString(R.string.info_op_download_database))
        sb.append("\n\n")
        build(
            sb,  
            getString(R.string.title_from), from,  
            "\n", "",  
            getString(R.string.title_to), to,  
            getString(R.string.size_expected), hrSize(R.integer.size_sqlunet_db, requireContext()),  
            getString(R.string.size_expected) + ' ' + getString(R.string.total), hrSize(R.integer.size_db_working_total, requireContext()),  
            getString(R.string.title_free), free,  
            getString(R.string.title_status), getString(if (targetExists) R.string.status_local_exists else R.string.status_local_not_exists)
        )
        return sb
    }

    /**
     * Operation status for download zipped database
     *
     * @return status string
     */
    private fun statusDownloadZipped(): SpannableStringBuilder {
        val context = requireContext()
        val from = getDbDownloadZippedSourcePath(context)
        val to = getCachedZippedPath(context)
        val free = getFree(context, to)
        val targetExists = File(to).exists()
        val sb = SpannableStringBuilder()
        sb.append(getString(R.string.info_op_download_zipped_database))
        sb.append("\n\n")
        build(
            sb,  
            getString(R.string.title_from), from,  
            "\n", "",  
            getString(R.string.title_to), to,  
            getString(R.string.size_expected), hrSize(R.integer.size_sqlunet_db_zip, requireContext()),  
            getString(R.string.title_free), free,  
            getString(R.string.title_status), getString(if (targetExists) R.string.status_local_exists else R.string.status_local_not_exists)
        )
        return sb
    }

    companion object {
        const val ARG = "operation"
    }
}
