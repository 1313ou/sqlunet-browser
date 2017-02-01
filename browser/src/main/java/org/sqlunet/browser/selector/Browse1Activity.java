package org.sqlunet.browser.selector;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.sqlunet.browser.R;

/**
 * Selector activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Browse1Activity extends AppCompatActivity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_browse1);

		// show the Up button in the type bar.
		//TODO actionbar
		//final ActionBar actionBar = getSupportActionBar();
		//assert actionBar != null;
		//actionBar.setDisplayHomeAsUpEnabled(true);

		// fragment
		// savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity (e.g. when rotating the screen from
		// portrait to landscape). In this case, the fragment will automatically be re-added to its container so we don't need to manually addItem it.
		// @see http://developer.android.com/guide/components/fragments.html
		if (savedInstanceState == null)
		{
			final Fragment fragment = new Browse1Fragment();
			fragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container_browse, fragment) //
					.commit();
		}
	}
}
