package org.sqlunet.browser.config;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.browser.R;
import org.sqlunet.browser.StatusActivity;
import org.sqlunet.provider.ManagerContract;
import org.sqlunet.provider.ManagerProvider;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.settings.StorageSettings;

/**
 * Manage activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class ManageActivity extends Activity
{
	// static private final String TAG = "ManageActivity";

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_manage);

		// show the Up button in the action bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);

		// fragment
		final Fragment fragment = new ManageFragment();
		fragment.setArguments(getIntent().getExtras());
		getFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_manage, fragment) //
				.commit();
	}
}
