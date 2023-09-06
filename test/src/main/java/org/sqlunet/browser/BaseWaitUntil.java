/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.view.View;

import org.hamcrest.Matcher;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

	@Nullable
	protected static View getView(@NonNull Matcher<View> viewMatcher)
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
