/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.view.View;

import org.hamcrest.Matcher;

import androidx.annotation.NonNull;
import androidx.test.espresso.ViewAssertion;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

public class ToBoolean
{
	static public boolean test(@NonNull final Matcher<View> view, @NonNull final Matcher<View> state)
	{
		try
		{
			onView(view).check(matches(state));
			return true;
		}
		catch (Throwable e)
		{
			return false;
		}
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	static public boolean testAssertion(@NonNull final Matcher<View> view, @NonNull final ViewAssertion assertion)
	{
		try
		{
			onView(view).check(assertion);
			return true;
		}
		catch (Throwable e)
		{
			return false;
		}
	}
}