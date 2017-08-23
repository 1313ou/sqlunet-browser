package org.sqlunet.browser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.sqlunet.browser.config.Status;
import org.sqlunet.settings.Settings;

/**
 * Entry point activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class EntryActivity extends AppCompatActivity
{
	// static private final String TAG = "EntryActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// check hook
		boolean canRun = Status.canRun(getBaseContext());
		if (!canRun)
		{
			forkOff(this);
			return;
		}

		// switch as per preferred launch mode
		final String clazz = Settings.getLaunchPref(this); // = "org.sqlunet.browser.MainActivity";
		final Intent intent = new Intent();
		intent.setClassName(this, clazz);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(Status.CANTRUN, false);
		startActivity(intent);
		finish();
	}

	static public void forkOffIfCantRun(final Activity activity)
	{
		boolean canRun;
		final Intent currentIntent = activity.getIntent();
		final Bundle extras = currentIntent.getExtras();
		boolean checked = extras != null && extras.containsKey(Status.CANTRUN);
		if (checked)
		{
			canRun = !currentIntent.getBooleanExtra(Status.CANTRUN, true);
		}
		else
		{
			// check now
			canRun = Status.canRun(activity);
		}
		if (!canRun)
		{
			forkOff(activity);
			// return;
		}
	}

	private static void forkOff(final Activity activity)
	{
		final Intent intent = new Intent(activity, StatusActivity.class);
		intent.putExtra(Status.CANTRUN, true);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
		activity.finish();
	}
}
