package org.sqlunet.verbnet.browser;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

	@NonNull
	@Override
	protected Fragment makeFragment()
	{
		return new VnClassFragment();
	}
}
