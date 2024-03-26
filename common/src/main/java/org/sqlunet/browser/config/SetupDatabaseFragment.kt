/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import com.bbou.concurrency.observe.TaskObserver
import com.bbou.concurrency.observe.TaskToastObserver
import org.sqlunet.browser.common.R
import org.sqlunet.settings.Settings.Companion.getZipEntry
import org.sqlunet.settings.StorageSettings.getDatabasePath

/**
 * Manage database fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SetupDatabaseFragment : BaseTaskFragment() {

    init {
        layoutId = R.layout.fragment_setup_database
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // args (relies on order of resources matching that of DO_)
        val args = arguments
        if (args != null) {
            val arg = args.getInt(ARG_POSITION)
            if (arg > 0) {
                spinner!!.setSelection(arg)
            }
        }
        runButton!!.setOnClickListener {

            // skip first
            val id = spinner!!.selectedItemId
            if (id == 0L) {
                return@setOnClickListener
            }

            // database path
            val activity: Activity = requireActivity()
            val databasePath = getDatabasePath(activity.baseContext)

            // sqls
            val sqls = activity.resources.getTextArray(R.array.sql_statements_values)

            // execute
            val sql = sqls[id.toInt()]
            if (sql == null || "EXEC_URI" == sql.toString()) {
                val intent2 = Intent(activity, OperationActivity::class.java)
                intent2.putExtra(OperationActivity.ARG_OP, OperationActivity.OP_EXEC_SQL)
                intent2.putExtra(OperationActivity.ARG_TYPES, arrayOf("application/sql", "text/plain"))
                activity.startActivity(intent2)
            } else if ("EXEC_ZIPPED_URI" == sql.toString()) {
                val intent2 = Intent(activity, OperationActivity::class.java)
                intent2.putExtra(OperationActivity.ARG_OP, OperationActivity.OP_EXEC_ZIPPED_SQL)
                intent2.putExtra(OperationActivity.ARG_ZIP_ENTRY, getZipEntry(requireContext(), "sql"))
                intent2.putExtra(OperationActivity.ARG_TYPES, arrayOf("application/zip"))
                activity.startActivity(intent2)
            } else {
                status!!.setText(R.string.status_task_running)
                val sqlStatements = sql.toString().split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                // Log.d(TAG, Arrays.toString(sqlStatements))
                val observer: TaskObserver<Pair<Number, Number>> = TaskToastObserver.WithStatus(activity, status!!)
                val task = ExecAsyncTask(activity, { ignoredResult: Boolean -> update() }, observer, 1).fromSql(databasePath)
                task.execute(sqlStatements)
            }
        }
    }

    // U P D A T E

    private fun update() {
        // empty
    }

    // S P I N N E R

    override fun makeAdapter(): SpinnerAdapter {
        // create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.sql_statement_titles, R.layout.spinner_item_task)

        // specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_item_task_dropdown)
        return adapter
    }

    companion object {
        /**
         * Initial spinner position key
         */
        const val ARG_POSITION = "position"
    }
}
