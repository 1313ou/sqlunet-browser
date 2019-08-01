/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;

import org.sqlunet.browser.common.R;

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

		// text view
		final TextView textView = findViewById(R.id.report);

		// diagnostics
		// final String diagnostics = Diagnostics.report(this);
		textView.setText(R.string.title_wait);
		new Diagnostics.AsyncDiagnostics(textView::setText).execute(this);

		// action button
		FloatingActionButton fab = findViewById(R.id.send_fab);
		fab.setOnClickListener(view -> {

			final Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL, new String[]{"semantikos.org@gmail.com"});
			email.putExtra(Intent.EXTRA_SUBJECT, "Semantikos diagnostics");
			email.putExtra(Intent.EXTRA_TEXT, textView.getText());
			email.setType("message/rfc822"); // prompts email client only

			startActivity(Intent.createChooser(email, getString(R.string.title_email_intent_selector)));
		});
	}
}
