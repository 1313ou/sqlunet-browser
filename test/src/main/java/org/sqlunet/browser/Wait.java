/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.view.View;
import android.widget.TextView;

import org.hamcrest.Matcher;

import java.util.concurrent.TimeoutException;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class Wait
{
	static private final int TIME_UNIT_IN_MS = 1000;

	@NonNull
	private static ViewAction waitId(@IdRes final int viewId, final long millis)
	{
		return new ViewAction()
		{
			@NonNull
			@Override
			public Matcher<View> getConstraints()
			{
				return isRoot();
			}

			@NonNull
			@Override
			public String getDescription()
			{
				return "wait for a specific view with id <" + viewId + "> during " + millis + " millis.";
			}

			@Override
			public void perform(@NonNull final UiController uiController, @NonNull final View view)
			{
				uiController.loopMainThreadUntilIdle();
				final long startTime = System.currentTimeMillis();
				final long endTime = startTime + millis;
				final Matcher<View> viewMatcher = withId(viewId);
				do
				{
					for (View child : TreeIterables.breadthFirstViewTraversal(view))
					{
						// found view with required ID
						if (viewMatcher.matches(child))
						{
							return;
						}
					}
					uiController.loopMainThreadForAtLeast(50);
				}
				while (System.currentTimeMillis() < endTime);

				// timeout happens
				throw new PerformException.Builder().withActionDescription(this.getDescription()).withViewDescription(HumanReadables.describe(view)).withCause(new TimeoutException()).build();
			}
		};
	}

	@NonNull
	private static ViewAction waitIdText(final int viewId, final String target, final boolean not, final long millis)
	{
		return new ViewAction()
		{
			@NonNull
			@Override
			public Matcher<View> getConstraints()
			{
				return isRoot();
			}

			@NonNull
			@Override
			public String getDescription()
			{
				return "wait for a specific view with id <" + viewId + "> during " + millis + " millis.";
			}

			@Override
			public void perform(@NonNull final UiController uiController, @NonNull final View view)
			{
				uiController.loopMainThreadUntilIdle();
				final long startTime = System.currentTimeMillis();
				final long endTime = startTime + millis;
				final Matcher<View> viewMatcher = withId(viewId);
				do
				{
					for (View child : TreeIterables.breadthFirstViewTraversal(view))
					{
						// found view with required ID
						if (viewMatcher.matches(child))
						{
							if (!(child instanceof TextView))
							{
								throw new PerformException.Builder().withActionDescription(this.getDescription()).withViewDescription(HumanReadables.describe(view)).withCause(new ClassCastException()).build();
							}
							final TextView textView = (TextView) child;
							final String text = textView.getText().toString();
							if (not && !text.equals(target))
							{
								return;
							}
							else if (!not && text.equals(target))
							{
								return;
							}
						}
					}
					uiController.loopMainThreadForAtLeast(50);
				}
				while (System.currentTimeMillis() < endTime);

				// timeout happens
				throw new PerformException.Builder().withActionDescription(this.getDescription()).withViewDescription(HumanReadables.describe(view)).withCause(new TimeoutException()).build();
			}
		};
	}

	static public void until(@IdRes int resId, int sec)
	{
		onView(isRoot()).perform(waitId(resId, (long) sec * TIME_UNIT_IN_MS));
	}

	static public void until_not_text(@IdRes int resId, String target, int sec)
	{
		onView(isRoot()).perform(waitIdText(resId, target, true, (long) sec * TIME_UNIT_IN_MS));
	}

	static public void until_text(@IdRes int resId, String target, int sec)
	{
		onView(isRoot()).perform(waitIdText(resId, target, false, (long) sec * TIME_UNIT_IN_MS));
	}

	static public void pause(int sec)
	{
		try
		{
			Thread.sleep((long) sec * TIME_UNIT_IN_MS);
		}
		catch (InterruptedException e)
		{
			//
		}
	}
}
