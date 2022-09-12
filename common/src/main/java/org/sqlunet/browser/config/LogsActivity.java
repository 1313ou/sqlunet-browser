/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.browser.config;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.sqlunet.browser.common.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LogsActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_logs);

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
		fab.setOnClickListener(view -> {

			final Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL, new String[]{"semantikos.org@gmail.com"});
			email.putExtra(Intent.EXTRA_SUBJECT, "Semantikos log");
			email.putExtra(Intent.EXTRA_TEXT, textView.getText());
			email.setType("message/rfc822"); // prompts email client only

			startActivity(Intent.createChooser(email, getString(R.string.title_dialog_select_email)));
		});

		// logs
		String fileName = null;
		final File storage = getCacheDir();
		//noinspection ConstantConditions
		final File logFile = new File(storage, fileName != null ? fileName : "sqlunet.log");
		final String text = readFile(logFile);
		textView.setText(text);
		progress.setIndeterminate(false);
		progress.setVisibility(View.GONE);
		fab.setVisibility(View.VISIBLE);
		fab.setEnabled(true);
	}

	private static String readFile(File file)
	{
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
				sb.append('\n');
			}
			return sb.toString();
		}
		catch (IOException e)
		{
			return "Error " + e.getMessage();
		}
	}
}
