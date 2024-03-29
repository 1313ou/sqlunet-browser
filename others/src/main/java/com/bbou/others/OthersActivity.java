/*
 * Copyright (c) 2023. Bernard Bou
 */

package com.bbou.others;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Other applications
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class OthersActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content view
		setContentView(R.layout.activity_other);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

		// menu listeners
		ImageButton b = findViewById(R.id.on_market_grammarscope);
		if(b!=null)
			b.setOnClickListener(this::onMarketGrammarScope);

		b = findViewById(R.id.on_market_grammarscope_udpipe);
		if(b!=null)
			b.setOnClickListener(this::onMarketGrammarScopeUDPipe);

		b = findViewById(R.id.on_market_treebolic_wordnet);
		if(b!=null)
			b.setOnClickListener(this::onMarketTreebolicWordNet);

		b = findViewById(R.id.on_market_semantikos);
		if(b!=null)
			b.setOnClickListener(this::onMarketSemantikos);

		b = findViewById(R.id.on_market_semantikos_wn);
		if(b!=null)
			b.setOnClickListener(this::onMarketSemantikosWn);

		b = findViewById(R.id.on_market_semantikos_ewn);
		if(b!=null)
			b.setOnClickListener(this::onMarketSemantikosEWn);

		b = findViewById(R.id.on_market_semantikos_vn);
		if(b!=null)
			b.setOnClickListener(this::onMarketSemantikosVn);

		b = findViewById(R.id.on_market_semantikos_fn);
		if(b!=null)
			b.setOnClickListener(this::onMarketSemantikosFn);

		b = findViewById(R.id.on_market_semantikos_sn);
		if(b!=null)
			b.setOnClickListener(this::onMarketSemantikosSn);
	}

	public void onMarketGrammarScope(@SuppressWarnings("UnusedParameters") View view)
	{
		install(getString(R.string.grammarscope_uri));
	}

	public void onMarketGrammarScopeUDPipe(@SuppressWarnings("UnusedParameters") View view)
	{
		install(getString(R.string.grammarscope_udpipe_uri));
	}

	public void onMarketTreebolicWordNet(@SuppressWarnings("UnusedParameters") View view)
	{
		install(getString(R.string.treebolic_wordnet_uri));
	}

	public void onMarketSemantikos(@SuppressWarnings("UnusedParameters") View view)
	{
		install(getString(R.string.semantikos_uri));
	}

	public void onMarketSemantikosWn(@SuppressWarnings("UnusedParameters") View view)
	{
		install(getString(R.string.semantikos_wn_uri));
	}

	public void onMarketSemantikosEWn(@SuppressWarnings("UnusedParameters") View view)
	{
		install(getString(R.string.semantikos_ewn_uri));
	}

	public void onMarketSemantikosVn(@SuppressWarnings("UnusedParameters") View view)
	{
		install(getString(R.string.semantikos_vn_uri));
	}

	public void onMarketSemantikosFn(@SuppressWarnings("UnusedParameters") View view)
	{
		install(getString(R.string.semantikos_fn_uri));
	}

	public void onMarketSemantikosSn(@SuppressWarnings("UnusedParameters") View view)
	{
		install(getString(R.string.semantikos_sn_uri));
	}

	static public void install(final String uri, @NonNull Activity activity)
	{
		final Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(uri));
		try
		{
			activity.startActivity(goToMarket);
		}
		catch (@NonNull final ActivityNotFoundException e)
		{
			String message = activity.getString(R.string.market_fail);
			message += ' ';
			message += uri;
			Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
		}
	}
	private void install(final String uri)
	{
		install(uri, this);
	}

	static private boolean isAppInstalled(@NonNull final String uri, @NonNull final Context context)
	{
		final PackageManager packageManager = context.getPackageManager();
		boolean isInstalled;
		try
		{
			packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			isInstalled = true;
		}
		catch (@NonNull final PackageManager.NameNotFoundException e)
		{
			isInstalled = false;
		}
		return isInstalled;
	}
}

