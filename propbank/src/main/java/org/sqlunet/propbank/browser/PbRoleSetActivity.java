package org.sqlunet.propbank.browser;

import android.support.v4.app.Fragment;

import org.sqlunet.browser.AbstractActivity;
import org.sqlunet.propbank.R;

/**
 * PropBank role set activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PbRoleSetActivity extends AbstractActivity
{
	@Override
	protected int getLayoutId()
	{
		return R.layout.activity_pbroleset;
	}

	@Override
	protected int getContainerId()
	{
		return R.id.container_pbroleset;
	}

	@Override
	protected Fragment makeFragment()
	{
		return new PbRoleSetFragment();
	}
}

