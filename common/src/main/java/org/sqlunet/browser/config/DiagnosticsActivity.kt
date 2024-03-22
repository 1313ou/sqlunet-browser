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
import org.sqlunet.browser.Sender
import org.sqlunet.browser.common.R
import org.sqlunet.browser.config.Diagnostics.AsyncDiagnostics

class DiagnosticsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnostics)

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
        fab.setOnClickListener { Sender.send(this, "Semantikos diagnostics", textView.getText(), "semantikos.org@gmail.com") }

        // diagnostics
        // final String diagnostics = Diagnostics.report(this);
        AsyncDiagnostics { text: CharSequence? ->
            progress.isIndeterminate = false
            progress.visibility = View.GONE
            textView.text = text
            fab.visibility = View.VISIBLE
            fab.setEnabled(true)
        }.execute(this)
    }
}
