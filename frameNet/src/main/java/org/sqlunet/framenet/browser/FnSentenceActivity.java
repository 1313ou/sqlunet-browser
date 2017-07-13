package org.sqlunet.framenet.browser;

import android.support.v4.app.Fragment;

import org.sqlunet.browser.AbstractActivity;
import org.sqlunet.framenet.R;

/**
 * Sentence activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnSentenceActivity extends AbstractActivity
{
	@Override
	protected int getLayoutId(){return R.layout.activity_fnsentence;}

	@Override
	protected int getContainerId(){return R.id.container_sentence;}

	@Override
	protected Fragment makeFragment(){ return new FnSentenceFragment(); }
}
