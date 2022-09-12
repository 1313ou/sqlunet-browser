/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.browser;

import android.view.View;

import org.hamcrest.Matcher;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.matcher.ViewMatchers;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class WaitUntil extends BaseWaitUntil
{
	// private static final String TAG = WaitUntil.class.getSimpleName();

	public WaitUntil(final Matcher<View> viewMatcher)
	{
		super(viewMatcher);
	}

	@Override
	public boolean isIdleNow()
	{
		View view = getView(this.viewMatcher);
		boolean idle = view == null || view.isShown();
		if (idle && this.resourceCallback != null)
		{
			this.resourceCallback.onTransitionToIdle();
		}
		return idle;
	}

	@NonNull
	@Override
	public String getName()
	{
		return this + this.viewMatcher.toString();
	}

	public static void waitViewShown(@NonNull final Matcher<View> matcher)
	{
		final IdlingResource idlingResource = new WaitUntil(matcher);
		try
		{
			IdlingRegistry.getInstance().register(idlingResource);
			//onView(matcher).check(matches(isDisplayed()));
			onView(matcher).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
		}
		finally
		{
			IdlingRegistry.getInstance().unregister(idlingResource);
		}
	}

	public static void shown(@IdRes final int viewId)
	{
		waitViewShown(withId(viewId));
	}
}
