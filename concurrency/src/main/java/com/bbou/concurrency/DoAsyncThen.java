/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.concurrency;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Do in background
 */
public class DoAsyncThen
{
	// R U N N A B L E S

	/**
	 * Run main runnable then post runnable
	 */
	static public void doASync(@NonNull final Runnable main, @Nullable final Runnable post)
	{
		final Task<Void, Void, Void> t = new Task<Void, Void, Void>()
		{
			@SuppressWarnings("SameReturnValue")
			@Nullable
			@Override
			protected Void doInBackground(@NonNull final Void[] params)
			{
				main.run();
				return null;
			}

			@Override
			protected void onPostExecute(final Void result)
			{
				if (post != null)
				{
					post.run();
				}
			}
		};
		t.execute();
	}

	// F U N C T I O N S

	@FunctionalInterface
	public interface Function<T, R>
	{
		@SuppressWarnings("unchecked")
		@NonNull
		R apply(T... params);
	}

	@FunctionalInterface
	public interface Function1<T>
	{
		@SuppressWarnings("UnusedReturnValue")
		@NonNull
		R apply(T param);
	}

	/**
	 * Apply main function, the post function on result
	 */
	@SafeVarargs
	static public <T, R> void doASync(@NonNull final Function<T, R> main, @Nullable final Function1<R> post, T... params)
	{
		final Task<T, Void, R> t = new Task<T, Void, R>()
		{
			@NonNull
			@Override
			protected R doInBackground(@NonNull final T[] params)
			{
				return main.apply(params);
			}

			@Override
			protected void onPostExecute(final R result)
			{
				if (post != null)
				{
					post.apply(result);
				}
			}
		};
		t.execute(params);
	}
}
