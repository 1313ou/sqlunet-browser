/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.view.View;

import org.hamcrest.Matcher;

import java.lang.reflect.Field;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.ViewFinder;
import androidx.test.espresso.ViewInteraction;

import static androidx.test.espresso.Espresso.onView;

public abstract class BaseWaitUntil implements IdlingResource
{
	protected final Matcher<View> viewMatcher;

	protected ResourceCallback resourceCallback;

	public BaseWaitUntil(final Matcher<View> viewMatcher)
	{
		this.viewMatcher = viewMatcher;
	}

	@Override
	public void registerIdleTransitionCallback(final ResourceCallback resourceCallback)
	{
		this.resourceCallback = resourceCallback;
	}

	protected static View getView(Matcher<View> viewMatcher)
	{
		try
		{
			final ViewInteraction viewInteraction = onView(viewMatcher);
			// private in ViewInteraction
			final Field finderField = viewInteraction.getClass().getDeclaredField("viewFinder");
			finderField.setAccessible(true);
			final ViewFinder finder = (ViewFinder) finderField.get(viewInteraction);
			assert finder != null;
			return finder.getView();
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
