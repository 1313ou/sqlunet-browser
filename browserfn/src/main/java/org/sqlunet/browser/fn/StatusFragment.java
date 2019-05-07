package org.sqlunet.browser.fn;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import org.sqlunet.browser.ActionBarSetter;

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

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean setActionBar(@NonNull final ActionBar actionBar, final Context context)
	{
		actionBar.setTitle(this.titleId);
		actionBar.setSubtitle(R.string.app_subname);
		return false;
	}
}
