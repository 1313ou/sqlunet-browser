/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import org.sqlunet.xnet.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

/**
 * Abstract activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class AbstractActivity extends AppCompatActivity
{
	abstract protected int getLayoutId();

	abstract protected int getContainerId();

	@NonNull
	abstract protected Fragment makeFragment();

	@Override
	protected void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(getLayoutId());

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

		// fragment
		// savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity (e.g. when rotating the screen from
		// portrait to landscape). In this case, the fragment will automatically be re-added to its container so we don't need to manually addItem it.
		// @see http://developer.android.com/guide/components/fragments.html
		if (savedInstanceState == null)
		{
			// create the sense fragment, transmit intent's extras as parameters and addItem it to the activity using a fragment transaction
			final Fragment fragment = makeFragment();
			fragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager() //
					.beginTransaction() //
					.replace(getContainerId(), fragment) //
					.commit();
		}
	}

	@Override
	protected void onNightModeChanged(final int mode)
	{
		super.onNightModeChanged(mode);
		final Configuration overrideConfig = createOverrideConfigurationForDayNight(this, mode);
		getApplication().onConfigurationChanged(overrideConfig);
	}

	@NonNull
	static public Configuration createOverrideConfigurationForDayNight(@NonNull Context context, final int mode)
	{
		int newNightMode;
		switch (mode) {
			case MODE_NIGHT_YES:
				newNightMode = Configuration.UI_MODE_NIGHT_YES;
				break;
			case MODE_NIGHT_NO:
				newNightMode = Configuration.UI_MODE_NIGHT_NO;
				break;
			default:
			case MODE_NIGHT_FOLLOW_SYSTEM:
				// If we're following the system, we just use the system default from the application context
				final Configuration appConfig =	context.getApplicationContext().getResources().getConfiguration();
				newNightMode = appConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
				break;
		}

		// If we're here then we can try and apply an override configuration on the Context.
		final Configuration overrideConf = new Configuration();
		overrideConf.fontScale = 0;
		overrideConf.uiMode = newNightMode | (overrideConf.uiMode & ~Configuration.UI_MODE_NIGHT_MASK);
		return overrideConf;
	}
}
