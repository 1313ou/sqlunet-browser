package org.sqlunet.browser;

import android.content.Context;
import android.support.v7.app.ActionBar;

import org.sqlunet.browser.common.R;
import org.sqlunet.browser.config.SetupStatusFragment;

/**
 * Status fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StatusFragment extends SetupStatusFragment implements ActionBarSetter
{
	// static private final String TAG = "StatusFragment";

	private final int titleId;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public StatusFragment()
	{
		this.titleId = R.string.title_status_section;
	}

	@Override
	public boolean setActionBar(final ActionBar actionBar, final Context context)
	{
		actionBar.setTitle(this.titleId);
		actionBar.setSubtitle(null);
		return false;
	}
}
