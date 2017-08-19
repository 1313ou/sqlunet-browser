package org.sqlunet.verbnet.browser;

import android.support.v4.app.Fragment;

import org.sqlunet.browser.AbstractActivity;
import org.sqlunet.verbnet.R;

/**
 * VerbNet class activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VnClassActivity extends AbstractActivity
{
	@Override
	protected int getLayoutId()
	{
		return R.layout.activity_vnclass;
	}

	@Override
	protected int getContainerId()
	{
		return R.id.container_vnclass;
	}

	@Override
	protected Fragment makeFragment()
	{
		return new VnClassFragment();
	}
}
