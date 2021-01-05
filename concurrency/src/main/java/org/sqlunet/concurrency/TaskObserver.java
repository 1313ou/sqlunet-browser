/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.concurrency;

import android.util.Log;

import java.text.NumberFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
	public interface Observer<Progress extends Number>
	{
		/**
		 * Start event
		 */
		void taskStart(@NonNull final Cancelable task);

		/**
		 * Finish event
		 *
		 * @param result result
		 */
		@SuppressWarnings({"EmptyMethod", "UnusedReturnValue"})
		void taskFinish(boolean result);

		/**
		 * Intermediate progress event
		 *
		 * @param progress progress value
		 * @param length   length
		 * @param unit     unit
		 */
		void taskProgress(@NonNull Progress progress, @NonNull Progress length, @Nullable String unit);

		/**
		 * Intermediate update event
		 *
		 * @param status status
		 */
		void taskUpdate(@NonNull CharSequence status);

		/**
		 * Set title
		 *
		 * @param title title
		 */
		Observer<Progress> setTitle(@NonNull CharSequence title);

		/**
		 * Set message
		 *
		 * @param message message
		 */
		Observer<Progress> setMessage(@NonNull CharSequence message);
	}

	/**
	 * Base observer
	 */
	static public class BaseObserver<Progress extends Number> implements Observer<Progress>
	{
		@SuppressWarnings("WeakerAccess")
		@Override
		public void taskStart(@NonNull final Cancelable task)
		{
			Log.d(TAG, "Task start");
		}

		@SuppressWarnings("WeakerAccess")
		@Override
		public void taskProgress(@NonNull Progress progress, @NonNull Progress length, @Nullable String unit)
		{
			Log.d(TAG, "Task " + progress + '/' + length + ' ' + unit);
		}

		@SuppressWarnings("WeakerAccess")
		@Override
		public void taskUpdate(@NonNull final CharSequence status)
		{
			Log.d(TAG, "Task " + status);
		}

		@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
		@Override
		public void taskFinish(boolean result)
		{
			Log.d(TAG, "Task " + (result ? "succeeded" : "failed"));
		}

		@Override
		public Observer<Progress> setTitle(@NonNull final CharSequence title)
		{
			return this;
		}

		@Override
		public Observer<Progress> setMessage(@NonNull final CharSequence message)
		{
			return this;
		}
	}

	/**
	 * Byte count to string
	 *
	 * @param count byte count
	 * @param unit  unit
	 * @return string
	 */
	@NonNull
	public static String countToString(final long count, @Nullable final String unit)
	{
		String strValue = NumberFormat.getNumberInstance(Locale.US).format(count);
		if (unit == null)
		{
			return strValue;
		}
		return strValue + ' ' + unit;
	}

	static final String[] UNITS = {"B", "KB", "MB", "GB"};

	/**
	 * Byte count to string
	 *
	 * @param count byte count
	 * @return string
	 */
	@NonNull
	public static String countToStorageString(final long count)
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

	@NonNull
	public static String countToString(final long progress, final long length, @Nullable final String unit)
	{
		String str = (unit != null ? countToString(progress, unit) : countToStorageString(progress));
		if (length != -1L)
		{
			String strLength = (unit != null ? countToString(length, unit) : countToStorageString(length));
			str += " / " + strLength;
		}
		return str;
	}
}
