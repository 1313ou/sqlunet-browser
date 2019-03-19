package org.sqlunet.support;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

/**
 * Other applications
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class OtherActivity extends AppCompatActivity
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

	public void onMarketSemantikosVn(@SuppressWarnings("UnusedParameters") View view)
	{
		install(getString(R.string.semantikos_vn_uri));
	}

	public void onMarketSemantikosFn(@SuppressWarnings("UnusedParameters") View view)
	{
		install(getString(R.string.semantikos_fn_uri));
	}

	private void install(final String uri)
	{
		final Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(uri));
		try
		{
			startActivity(goToMarket);
		}
		catch (@NonNull final ActivityNotFoundException e)
		{
			String message = getString(R.string.market_fail);
			message += ' ';
			message += uri;
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}
	}

	static private boolean isAppInstalled(final String uri, @NonNull final Context context)
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

