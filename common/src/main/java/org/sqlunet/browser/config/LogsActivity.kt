/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.sqlunet.browser.AppCompatCommonActivity
import org.sqlunet.browser.Sender
import org.sqlunet.browser.common.R
import org.sqlunet.settings.LogUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class LogsActivity : AppCompatCommonActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // set up the action bar
        val actionBar = supportActionBar!!
        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE

        // progress
        val progress = findViewById<ProgressBar>(R.id.progress)
        progress.isIndeterminate = true

        // text view
        val textView = findViewById<TextView>(R.id.report)
        textView.setText(R.string.status_diagnostics_running)

        // action button
        val fab = findViewById<FloatingActionButton>(R.id.send_fab)
        fab.visibility = View.GONE
        fab.setOnClickListener { Sender.send(this, "Semantikos log", textView.text, "semantikos.org@gmail.com") }

        // logs
        val logArg = intent.getStringExtra(ARG_LOG)
        val fileName = logArg ?: LogUtils.SQL_LOG
        val logFile = File(cacheDir, fileName)
        if (logFile.exists()) {
            val text = readFile(logFile)
            textView.text = text
            progress.isIndeterminate = false
            progress.visibility = View.GONE
            fab.visibility = View.VISIBLE
            fab.isEnabled = true
        } else {
            val resId = when (logArg) {
                LogUtils.SQL_LOG -> R.string.status_log_not_exists
                LogUtils.DOC_LOG -> R.string.status_log_not_exists_doc
                ExecAsyncTask.EXEC_LOG -> R.string.status_log_not_exists_exec
                else -> R.string.status_log_not_exists
            }
            textView.text = resources.getText(resId)
            progress.isIndeterminate = false
            progress.visibility = View.GONE
        }
    }

    companion object {

        const val ARG_LOG = "which"

        private fun readFile(file: File): String {
            val sb = StringBuilder()
            try {
                BufferedReader(FileReader(file)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        sb.append(line)
                        sb.append('\n')
                    }
                    return sb.toString()
                }
            } catch (e: IOException) {
                return "Error " + e.message
            }
        }
    }
}
