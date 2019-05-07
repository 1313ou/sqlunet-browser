package org.sqlunet.browser;

import android.content.Context;
import androidx.appcompat.app.ActionBar;

/**
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

@FunctionalInterface
public interface ActionBarSetter
{
	boolean setActionBar(final ActionBar actionBar, final Context context);
}
