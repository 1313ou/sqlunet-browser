package org.sqlunet.browser;

import android.view.View;

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

class Wait
{
	private static ViewAction waitId(final int viewId, final long millis)
	{
		return new ViewAction()
		{
			@NonNull
			@Override
			public Matcher<View> getConstraints()
			{
				return isRoot();
			}

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

	static void until(@IdRes int resId, int sec)
	{
		onView(isRoot()).perform(waitId(resId, sec * 1000));
	}

	static private final int PAUSE_UNIT = 1000;

	static void pause(int sec)
	{
		try
		{
			Thread.sleep(PAUSE_UNIT * sec);
		}
		catch (InterruptedException e)
		{
			//
		}
	}
}
