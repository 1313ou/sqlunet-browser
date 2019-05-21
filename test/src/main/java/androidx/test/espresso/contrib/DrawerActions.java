/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package androidx.test.espresso.contrib;

import android.view.View;

import org.hamcrest.Matcher;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;

/**
 * Espresso actions for using a {@link DrawerLayout}.
 *
 * @see <a href="http://developer.android.com/design/patterns/navigation-drawer.html">Navigation
 * drawer design guide</a>
 */
@SuppressWarnings("ALL")
public final class DrawerActions
{

	private DrawerActions()
	{
		// forbid instantiation
	}

	static private Field listenerField;

	private abstract static class DrawerAction implements ViewAction
	{

		@Override
		public final Matcher<View> getConstraints()
		{
			return ViewMatchers.isAssignableFrom(DrawerLayout.class);
		}

		@Override
		public final void perform(@NonNull UiController uiController, View view)
		{
			DrawerLayout drawer = (DrawerLayout) view;

			if (!checkAction().matches(drawer))
			{
				return;
			}

			DrawerListener listener = getDrawerListener(drawer);
			IdlingDrawerListener idlingListener;
			if (listener instanceof IdlingDrawerListener)
			{
				idlingListener = (IdlingDrawerListener) listener;
			}
			else
			{
				idlingListener = IdlingDrawerListener.getInstance(listener);
				drawer.setDrawerListener(idlingListener);
				Espresso.registerIdlingResources(idlingListener);
			}

			performAction(uiController, drawer);
			uiController.loopMainThreadUntilIdle();

			Espresso.unregisterIdlingResources(idlingListener);
			drawer.setDrawerListener(idlingListener.parentListener);
			idlingListener.parentListener = null;
		}

		@NonNull
		protected abstract Matcher<View> checkAction();

		protected abstract void performAction(UiController uiController, DrawerLayout view);
	}

	/**
	 * @deprecated Use {@link #open()} with {@code perform} after matching a view. This method will
	 * be removed in the next release.
	 */
	@Deprecated
	@SuppressWarnings("deprecation")
	static public void openDrawer(int drawerLayoutId)
	{
		openDrawer(drawerLayoutId, GravityCompat.START);
	}

	/**
	 * @deprecated Use {@link #open(int)} with {@code perform} after matching a view. This method will
	 * be removed in the next release.
	 */
	@Deprecated
	static public void openDrawer(int drawerLayoutId, int gravity)
	{
		Espresso.onView(ViewMatchers.withId(drawerLayoutId)).perform(open(gravity));
	}

	/**
	 * Creates an action which opens the {@link DrawerLayout} drawer with gravity START. This method
	 * blocks until the drawer is fully open. No operation if the drawer is already open.
	 */
	// TODO alias to openDrawer before 3.0 and deprecate this method.
	static public ViewAction open()
	{
		return open(GravityCompat.START);
	}

	/**
	 * Creates an action which opens the {@link DrawerLayout} drawer with the gravity. This method
	 * blocks until the drawer is fully open. No operation if the drawer is already open.
	 */
	// TODO alias to openDrawer before 3.0 and deprecate this method.
	static public ViewAction open(final int gravity)
	{
		return new DrawerAction()
		{
			@NonNull
			@Override
			public String getDescription()
			{
				return "open drawer with gravity " + gravity;
			}

			@NonNull
			@Override
			protected Matcher<View> checkAction()
			{
				return DrawerMatchers.isClosed(gravity);
			}

			@Override
			protected void performAction(UiController uiController, @NonNull DrawerLayout view)
			{
				view.openDrawer(gravity);
			}
		};
	}

	/**
	 * @deprecated Use {@link #close()} with {@code perform} after matching a view. This method will
	 * be removed in the next release.
	 */
	@Deprecated
	@SuppressWarnings("deprecation")
	static public void closeDrawer(int drawerLayoutId)
	{
		closeDrawer(drawerLayoutId, GravityCompat.START);
	}

	/**
	 * @deprecated Use {@link #open(int)} with {@code perform} after matching a view. This method will
	 * be removed in the next release.
	 */
	@Deprecated
	static public void closeDrawer(int drawerLayoutId, int gravity)
	{
		Espresso.onView(ViewMatchers.withId(drawerLayoutId)).perform(close(gravity));
	}

	/**
	 * Creates an action which closes the {@link DrawerLayout} with gravity START. This method
	 * blocks until the drawer is fully closed. No operation if the drawer is already closed.
	 */
	// TODO alias to closeDrawer before 3.0 and deprecate this method.
	static public ViewAction close()
	{
		return close(GravityCompat.START);
	}

	/**
	 * Creates an action which closes the {@link DrawerLayout} with the gravity. This method
	 * blocks until the drawer is fully closed. No operation if the drawer is already closed.
	 */
	// TODO alias to closeDrawer before 3.0 and deprecate this method.
	static public ViewAction close(final int gravity)
	{
		return new DrawerAction()
		{
			@NonNull
			@Override
			public String getDescription()
			{
				return "close drawer with gravity " + gravity;
			}

			@NonNull
			@Override
			protected Matcher<View> checkAction()
			{
				return DrawerMatchers.isOpen(gravity);
			}

			@Override
			protected void performAction(@NonNull UiController uiController, @NonNull DrawerLayout view)
			{
				view.closeDrawer(gravity);
				uiController.loopMainThreadUntilIdle();
				// If still open wait some more...
				if (view.isDrawerVisible(gravity))
				{
					uiController.loopMainThreadForAtLeast(300);
				}
			}
		};
	}

	/**
	 * Pries the current {@link DrawerListener} loose from the cold dead hands of the given
	 * {@link DrawerLayout}. Uses reflection.
	 */
	@Nullable
	static private DrawerListener getDrawerListener(DrawerLayout drawer)
	{
		try
		{
			if (listenerField == null)
			{
				// lazy initialization of reflected field.
				listenerField = DrawerLayout.class.getDeclaredField("mListener");
				listenerField.setAccessible(true);
			}
			return (DrawerListener) listenerField.get(drawer);
		}
		catch (@NonNull IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex)
		{
			// Pity we can't use Java 7 multi-catch for all of these.
			throw new PerformException.Builder().withCause(ex).build();
		}
	}

	/**
	 * Drawer listener that wraps an existing {@link DrawerListener}, and functions as an
	 * {@link IdlingResource} for Espresso.
	 */
	static private class IdlingDrawerListener implements DrawerListener, IdlingResource
	{
		static private IdlingDrawerListener instance;

		static private IdlingDrawerListener getInstance(DrawerListener parentListener)
		{
			if (instance == null)
			{
				instance = new IdlingDrawerListener();
			}
			instance.setParentListener(parentListener);
			return instance;
		}

		@Nullable
		private DrawerListener parentListener;
		private ResourceCallback callback;
		// Idle state is only accessible from main thread.
		private boolean idle = true;

		public void setParentListener(@Nullable DrawerListener parentListener)
		{
			this.parentListener = parentListener;
		}

		@Override
		public void onDrawerClosed(@NonNull View drawer)
		{
			if (this.parentListener != null)
			{
				this.parentListener.onDrawerClosed(drawer);
			}
		}

		@Override
		public void onDrawerOpened(@NonNull View drawer)
		{
			if (this.parentListener != null)
			{
				this.parentListener.onDrawerOpened(drawer);
			}
		}

		@Override
		public void onDrawerSlide(@NonNull View drawer, float slideOffset)
		{
			if (this.parentListener != null)
			{
				this.parentListener.onDrawerSlide(drawer, slideOffset);
			}
		}

		@Override
		public void onDrawerStateChanged(int newState)
		{
			if (newState == DrawerLayout.STATE_IDLE)
			{
				this.idle = true;
				if (this.callback != null)
				{
					this.callback.onTransitionToIdle();
				}
			}
			else
			{
				this.idle = false;
			}
			if (this.parentListener != null)
			{
				this.parentListener.onDrawerStateChanged(newState);
			}
		}

		@NonNull
		@Override
		public String getName()
		{
			return "IdlingDrawerListener";
		}

		@Override
		public boolean isIdleNow()
		{
			return this.idle;
		}

		@Override
		public void registerIdleTransitionCallback(ResourceCallback callback)
		{
			this.callback = callback;
		}
	}
}
