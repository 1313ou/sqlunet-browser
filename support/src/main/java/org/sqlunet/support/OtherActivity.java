package org.sqlunet.support;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
		setContentView(R.layout.activity_other);
	}

	public void onMarketTreebolicWordNet(View view)
	{
		install(getString(R.string.treebolic_wordnet_uri));
	}

	public void onMarketSemantikos(View view)
	{
		install(getString(R.string.semantikos_uri));
	}

	public void onMarketSemantikosWn(View view)
	{
		install(getString(R.string.semantikos_wn_uri));
	}

	public void onMarketSemantikosFn(View view)
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
		catch (final ActivityNotFoundException e)
		{
			String message = getString(R.string.market_fail);
			message += ' ';
			message += uri;
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}
	}

	static private boolean isAppInstalled(final String uri, final Context context)
	{
		final PackageManager packageManager = context.getPackageManager();
		boolean isInstalled;
		try
		{
			packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			isInstalled = true;
		}
		catch (final PackageManager.NameNotFoundException e)
		{
			isInstalled = false;
		}
		return isInstalled;
	}
}

