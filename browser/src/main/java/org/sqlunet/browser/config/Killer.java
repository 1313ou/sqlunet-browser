package org.sqlunet.browser.config;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Killer broadcast receiver
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

public class Killer extends BroadcastReceiver
{
	static public final String KILL_DOWNLOAD = "kill_download";

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		System.out.println("RECEIVED " + action);
		if (action.equals(Killer.KILL_DOWNLOAD))
		{
			SimpleDownloadFragment.kill();
		}
		int id = intent.getIntExtra(SimpleDownloadFragment.NOTIFICATION_ID, 0);
		if (id != 0)
		{
			final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			manager.cancel(id);
		}
	}
}
