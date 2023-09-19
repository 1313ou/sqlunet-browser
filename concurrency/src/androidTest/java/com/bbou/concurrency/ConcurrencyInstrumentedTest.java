/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.concurrency;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ConcurrencyInstrumentedTest
{
	@Test
	public void useTask()
	{
		// Context of the app under test.
		// Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

		final Task<Integer, String, Long> task = new Task<Integer, String, Long>()
		{
			@Override
			protected Long doInBackground(@NonNull final Integer... params)
			{
				long s = 0;
				for (Integer param : params)
				{
					s += param;
					publishProgress(Integer.toString(param));
				}
				return s;
			}

			@Override
			protected void onPostExecute(final Long result)
			{
				super.onPostExecute(result);
				System.out.println(result);
			}

			protected void onProgressUpdate(@NonNull String... progresses)
			{
				for (String progress : progresses)
				{
					System.out.println(progress);
				}
			}
		};
		task.execute(1, 2, 3);
	}
}