package org.sqlunet.browser.config;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import org.sqlunet.browser.common.R;

/**
 * Utils
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class Utils
{
	/**
	 * Confirm
	 *
	 * @param context  context
	 * @param titleId  title resource id
	 * @param askId    ask resource id
	 * @param runnable run if confirmed
	 */
	static public void confirm(@NonNull final Context context, final int titleId, final int askId, @NonNull final Runnable runnable)
	{
		new AlertDialog.Builder(context) //
				.setIcon(android.R.drawable.ic_dialog_alert) //
				.setTitle(titleId) //
				.setMessage(askId) //
				.setPositiveButton(R.string.yes, (dialog, which) -> runnable.run()).setNegativeButton(R.string.no, null).show();
	}
}
