/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.concurrency;

import android.util.Log;

import java.text.NumberFormat;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Task observer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("WeakerAccess")
public class TaskObserver
{
	static private final String TAG = "TaskObserver";

	/**
	 * Observer interface
	 */
	@FunctionalInterface
	public interface Observer<Progress extends Number>
	{
		/**
		 * Start event
		 */
		default void taskStart(@NonNull final Task<?, Progress, ?> task)
		{
		}

		/**
		 * Finish event
		 *
		 * @param result result
		 */
		@SuppressWarnings({"EmptyMethod", "UnusedReturnValue"})
		void taskFinish(boolean result);

		/**
		 * Intermediate update event
		 *
		 * @param progress progress value
		 * @param length   length
		 */
		default void taskUpdate(@NonNull Progress progress, @NonNull Progress length)
		{
		}
	}

	/**
	 * Base observer
	 */
	static public class BaseObserver<Progress extends Number> implements Observer<Progress>
	{
		@SuppressWarnings("WeakerAccess")
		@Override
		public void taskStart(@NonNull final Task<?, Progress, ?> task)
		{
			Log.d(TAG, "Task start");
		}

		@SuppressWarnings("WeakerAccess")
		@Override
		public void taskUpdate(@NonNull Progress progress, @NonNull Progress length)
		{
			// TODO progress observation
			// Log.d(TAG, "Task " + progress + '/' + length);
		}

		@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
		@Override
		public void taskFinish(boolean result)
		{
			Log.d(TAG, "Task " + (result ? "succeeded" : "failed"));
		}
	}

	/**
	 * Byte count to string
	 *
	 * @param count byte count
	 * @return string
	 */
	@NonNull
	static String countToString(final long count, @NonNull final CharSequence unit)
	{
		return NumberFormat.getNumberInstance(Locale.US).format(count) + ' ' + unit;
	}

	static final String[] UNITS = {"B", "KB", "MB", "GB"};

	/**
	 * Byte count to string
	 *
	 * @param count byte count
	 * @return string
	 */
	@NonNull
	static String countToStorageString(final long count)
	{
		if (count > 0)
		{
			float unit = 1024F * 1024F * 1024F;
			for (int i = 3; i >= 0; i--)
			{
				if (count >= unit)
				{
					return String.format(Locale.ENGLISH, "%.1f %s", count / unit, UNITS[i]);
				}

				unit /= 1024;
			}
		}
		else if (count == 0)
		{
			return "0 Byte";
		}
		return "[n/a]";
	}
}
