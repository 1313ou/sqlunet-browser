/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package androidx.test.espresso.contrib;

import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.test.espresso.matcher.BoundedMatcher;

/**
 * Hamcrest matchers for a {@link DrawerLayout}.
 */
@SuppressWarnings("ALL")
public final class DrawerMatchers
{

	private DrawerMatchers()
	{
		// forbid instantiation
	}

	/**
	 * Returns a matcher that verifies that the drawer with the specified gravity is open. Matches
	 * only when the drawer is fully open. Use {@link #isClosed(int)} instead of {@code
	 * not(isOpen())} when you wish to check that the drawer is fully closed.
	 */
	static public Matcher<View> isOpen(final int gravity)
	{
		return new BoundedMatcher<View, DrawerLayout>(DrawerLayout.class)
		{
			@Override
			public void describeTo(@NonNull Description description)
			{
				description.appendText("is drawer open");
			}

			@Override
			public boolean matchesSafely(@NonNull DrawerLayout drawer)
			{
				return drawer.isDrawerOpen(gravity);
			}
		};
	}

	/**
	 * Returns a matcher that verifies that the drawer (with gravity START) is open. Matches only
	 * when the drawer is fully open. Use {@link #isClosed()} instead of {@code not(isOpen())} when
	 * you wish to check that the drawer is fully closed.
	 */
	static public Matcher<View> isOpen()
	{
		return isOpen(GravityCompat.START);
	}

	/**
	 * Returns a matcher that verifies that the drawer with the specified gravity is closed. Matches
	 * only when the drawer is fully closed. Use {@link #isOpen(int)} instead of {@code
	 * not(isClosed()))} when you wish to check that the drawer is fully open.
	 */
	static public Matcher<View> isClosed(final int gravity)
	{
		return new BoundedMatcher<View, DrawerLayout>(DrawerLayout.class)
		{
			@Override
			public void describeTo(@NonNull Description description)
			{
				description.appendText("is drawer closed");
			}

			@Override
			public boolean matchesSafely(@NonNull DrawerLayout drawer)
			{
				return !drawer.isDrawerVisible(gravity);
			}
		};
	}

	/**
	 * Returns a matcher that verifies that the drawer (with gravity START) is closed. Matches only
	 * when the drawer is fully closed. Use {@link #isOpen()} instead of {@code not(isClosed()))}
	 * when you wish to check that the drawer is fully open.
	 */
	static public Matcher<View> isClosed()
	{
		return isClosed(GravityCompat.START);
	}
}
