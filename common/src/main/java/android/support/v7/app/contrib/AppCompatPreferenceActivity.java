/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package android.support.v7.app.contrib;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.common.R;

/**
 * A {@link android.preference.PreferenceActivity} which implements and proxies the necessary calls to be used with AppCompat.
 */
@SuppressWarnings("unused")
public abstract class AppCompatPreferenceActivity extends PreferenceActivity
{
	// D E L E G A T E

	private AppCompatDelegate mDelegate;

	private AppCompatDelegate getDelegate()
	{
		if (this.mDelegate == null)
		{
			this.mDelegate = AppCompatDelegate.create(this, null);
		}
		return this.mDelegate;
	}

	// L I F E C Y C L E

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		getDelegate().installViewFactory();
		getDelegate().onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		getDelegate().onPostCreate(savedInstanceState);
	}

	@Override
	protected void onPostResume()
	{
		super.onPostResume();
		getDelegate().onPostResume();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		getDelegate().onStop();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		getDelegate().onDestroy();
	}

	// A C T I O N   B A R

	protected void setupToolbar(int toolbarLayout, int toolbarId)
	{
		// TODO
		final ViewGroup rootView = findViewById(R.id.action_bar_root); //id from appcompat
		if (rootView != null)
		{
			final View view = getLayoutInflater().inflate(toolbarLayout, rootView, false);
			rootView.addView(view, 0);

			final Toolbar toolbar = findViewById(toolbarId);
			setSupportActionBar(toolbar);
		}
	}

	public ActionBar getSupportActionBar()
	{
		return getDelegate().getSupportActionBar();
	}

	public void setSupportActionBar(@Nullable Toolbar toolbar)
	{
		getDelegate().setSupportActionBar(toolbar);
	}

	@NonNull
	@Override
	public MenuInflater getMenuInflater()
	{
		return getDelegate().getMenuInflater();
	}

	// C O N T E N T   V I E W

	@Override
	public void setContentView(@LayoutRes int layoutResID)
	{
		getDelegate().setContentView(layoutResID);
	}

	@Override
	public void setContentView(View view)
	{
		getDelegate().setContentView(view);
	}

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params)
	{
		getDelegate().setContentView(view, params);
	}

	@Override
	public void addContentView(View view, ViewGroup.LayoutParams params)
	{
		getDelegate().addContentView(view, params);
	}

	// M I S C   O V E R R I D E S

	@Override
	protected void onTitleChanged(CharSequence title, int color)
	{
		super.onTitleChanged(title, color);
		getDelegate().setTitle(title);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		getDelegate().onConfigurationChanged(newConfig);
	}

	@Override
	public void invalidateOptionsMenu()
	{
		getDelegate().invalidateOptionsMenu();
	}
}