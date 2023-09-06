/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.view.View;
import android.widget.TextView;

import org.hamcrest.Matcher;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.util.HumanReadables;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;

public class WaitUntilText extends BaseWaitUntil
{
	// private static final String TAG = WaitUntilText.class.getSimpleName();

	private final String target;

	private final boolean not;

	public WaitUntilText(final Matcher<View> viewMatcher, final String target, final boolean not)
	{
		super(allOf(viewMatcher, instanceOf(TextView.class)));
		this.target = target;
		this.not = not;
	}

	@Override
	public boolean isIdleNow()
	{
		View view = getView(this.viewMatcher);
		boolean idle;
		if (view == null)
		{
			idle = true;
		}
		else
		{
			if (!(view instanceof TextView))
			{
				throw new PerformException.Builder().withActionDescription(this.getName()).withViewDescription(HumanReadables.describe(view)).withCause(new ClassCastException()).build();
			}

			TextView textView = (TextView) view;
			CharSequence content = textView.getText();
			final String text = content.toString();
			boolean condition = this.not != text.equals(this.target);
			idle = view.isShown() && condition;
		}
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

	public static void waitTextView(@NonNull final Matcher<View> matcher, final String target, final boolean not)
	{
		final IdlingResource idlingResource = new WaitUntilText(matcher, target, not);
		try
		{
			IdlingRegistry.getInstance().register(idlingResource);
			onView(matcher).check(matches(isDisplayed()));
		}
		finally
		{
			IdlingRegistry.getInstance().unregister(idlingResource);
		}
	}

	public static void changesTo(@IdRes final int viewId, final String target)
	{
		waitTextView(withId(viewId), target, false);
	}

	public static void changesFrom(@IdRes final int viewId, final String target)
	{
		waitTextView(withId(viewId), target, true);
	}
}
