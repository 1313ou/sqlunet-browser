package org.sqlunet.framenet.browser;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

	@NonNull
	@Override
	protected Fragment makeFragment(){ return new FnSentenceFragment(); }
}
