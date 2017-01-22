package org.sqlunet.browser.config;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.sqlunet.browser.MainActivity;
import org.sqlunet.browser.R;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;
import org.sqlunet.style.Report;

import java.io.File;

/**
 * Set up with SQL activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupSqlActivity extends Activity
{
	static private final String TAG = "SetupSqlActivity";

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_setup);

		// show the Up button in the type bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);

		// fragment
		final Fragment fragment = new SetupSqlFragment();
		fragment.setArguments(getIntent().getExtras());
		getFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_setup, fragment) //
				.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		return MainActivity.dispatch(this, item);
	}
}
