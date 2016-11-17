package org.sqlunet.browser.config;

import android.app.Activity;
import android.os.Bundle;

import org.sqlunet.browser.R;

/**
 * Storage activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StorageActivity extends Activity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_storage);
	}
}
