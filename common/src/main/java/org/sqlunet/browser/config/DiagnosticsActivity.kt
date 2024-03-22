/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.config;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.sqlunet.browser.Sender;
import org.sqlunet.browser.common.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DiagnosticsActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_diagnostics);

		// toolbar
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

		// progress
		final ProgressBar progress = findViewById(R.id.progress);
		progress.setIndeterminate(true);

		// text view
		final TextView textView = findViewById(R.id.report);
		textView.setText(R.string.status_diagnostics_running);

		// action button
		final FloatingActionButton fab = findViewById(R.id.send_fab);
		fab.setVisibility(View.GONE);
		fab.setOnClickListener(view -> Sender.send(this, "Semantikos diagnostics", textView.getText(), "semantikos.org@gmail.com"));

		// diagnostics
		// final String diagnostics = Diagnostics.report(this);
		new Diagnostics.AsyncDiagnostics(text -> {
			progress.setIndeterminate(false);
			progress.setVisibility(View.GONE);
			textView.setText(text);
			fab.setVisibility(View.VISIBLE);
			fab.setEnabled(true);
		}).execute(this);
	}
}
