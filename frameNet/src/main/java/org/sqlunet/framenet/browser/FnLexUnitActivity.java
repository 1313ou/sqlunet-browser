package org.sqlunet.framenet.browser;

import android.support.v4.app.Fragment;

import org.sqlunet.browser.AbstractActivity;
import org.sqlunet.framenet.R;

/**
 * Lex unit activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnLexUnitActivity extends AbstractActivity
{
	@Override
	protected int getLayoutId(){return R.layout.activity_fnlexunit;}

	@Override
	protected int getContainerId(){return R.id.container_lexunit;}

	@Override
	protected Fragment makeFragment(){ return new FnLexUnitFragment(); }
}
