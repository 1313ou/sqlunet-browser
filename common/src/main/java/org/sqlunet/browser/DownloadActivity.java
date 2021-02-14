/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import org.sqlunet.browser.EntryActivity;

public class DownloadActivity extends org.sqlunet.download.DownloadActivity
{
	@Override
	public void onBackPressed()
	{
		// super.onBackPressed();
		EntryActivity.rerun(this);
	}

	@Override
	public boolean onNavigateUp()
	{
		EntryActivity.rerun(this);
		finish();
		return true;
	}
}
