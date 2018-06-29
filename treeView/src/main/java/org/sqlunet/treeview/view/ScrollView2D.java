package org.sqlunet.treeview.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.List;

/**
 * Layout container for a view hierarchy that can be scrolled by the user, allowing it to be larger than the physical display. A ScrollView2D is a
 * {@link FrameLayout}, meaning you should place one child in it containing the entire contents to scroll; this child may itself be a layout manager with a
 * complex hierarchy of objects. A child that is often used is a {@link LinearLayout} in a vertical orientation, presenting a vertical array of top-level items
 * that the user can scroll through.
 * <p/>
 * <p>
 * The {@link TextView} class also takes care of its own scrolling, so does not require a ScrollView2D, but using the two together is possible to achieve the
 * effect of a text view within a larger container.
 */
public class ScrollView2D extends FrameLayout
{
	static private final int ANIMATED_SCROLL_GAP = 250;
	static private final float MAX_SCROLL_FACTOR = 0.5f;

	private long mLastScroll;

	private final Rect mTempRect = new Rect();
	private Scroller mScroller;

	/**
	 * Flag to indicate that we are moving focus ourselves. This is so the code that watches for focus changes initiated outside this ScrollView2D knows that
	 * it does not have to do anything.
	 */
	private boolean mTwoDScrollViewMovedFocus;

	/**
	 * Position of the last motion event.
	 */
	private float mLastMotionY;
	private float mLastMotionX;

	/**
	 * True when the layout has changed but the traversal has not come through yet. Ideally the view hierarchy would keep track of this for us.
	 */
	private boolean mIsLayoutDirty = true;

	/**
	 * The child to give focus to in the event that a child has requested focus while the layout is dirty. This prevents the scroll from being wrong if the
	 * child has not been laid out before requesting focus.
	 */
	@Nullable
	private View mChildToScrollTo;

	/**
	 * True if the user is currently dragging this ScrollView2D around. This is not the same as 'is being flung', which can be checked by
	 * mScroller.isFinished() (flinging begins when the user lifts his finger).
	 */
	private boolean mIsBeingDragged = false;

	/**
	 * Determines speed during touch scrolling
	 */
	@Nullable
	private VelocityTracker mVelocityTracker;

	/**
	 * Whether arrow scrolling is animated.
	 */
	private int mTouchSlop;
	private int mMinimumVelocity;
	private int mMaximumVelocity;

	public ScrollView2D(@NonNull Context context)
	{
		super(context);
		initTwoDScrollView();
	}

	public ScrollView2D(@NonNull Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initTwoDScrollView();
	}

	public ScrollView2D(@NonNull Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initTwoDScrollView();
	}

	@Override
	protected float getTopFadingEdgeStrength()
	{
		if (getChildCount() == 0)
		{
			return 0.0f;
		}
		final int length = getVerticalFadingEdgeLength();
		if (getScrollY() < length)
		{
			return getScrollY() / (float) length;
		}
		return 1.0f;
	}

	@Override
	protected float getBottomFadingEdgeStrength()
	{
		if (getChildCount() == 0)
		{
			return 0.0f;
		}
		final int length = getVerticalFadingEdgeLength();
		final int bottomEdge = getHeight() - getPaddingBottom();
		final int span = getChildAt(0).getBottom() - getScrollY() - bottomEdge;
		if (span < length)
		{
			return span / (float) length;
		}
		return 1.0f;
	}

	@Override
	protected float getLeftFadingEdgeStrength()
	{
		if (getChildCount() == 0)
		{
			return 0.0f;
		}
		final int length = getHorizontalFadingEdgeLength();
		if (getScrollX() < length)
		{
			return getScrollX() / (float) length;
		}
		return 1.0f;
	}

	@Override
	protected float getRightFadingEdgeStrength()
	{
		if (getChildCount() == 0)
		{
			return 0.0f;
		}
		final int length = getHorizontalFadingEdgeLength();
		final int rightEdge = getWidth() - getPaddingRight();
		final int span = getChildAt(0).getRight() - getScrollX() - rightEdge;
		if (span < length)
		{
			return span / (float) length;
		}
		return 1.0f;
	}

	/**
	 * @return The maximum amount this scroll view will scroll in response to an arrow event.
	 */
	private int getMaxScrollAmountVertical()
	{
		return (int) (MAX_SCROLL_FACTOR * getHeight());
	}

	private int getMaxScrollAmountHorizontal()
	{
		return (int) (MAX_SCROLL_FACTOR * getWidth());
	}

	private void initTwoDScrollView()
	{
		this.mScroller = new Scroller(getContext());
		setFocusable(true);
		setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
		setWillNotDraw(false);
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		this.mTouchSlop = configuration.getScaledTouchSlop();
		this.mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
		this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	@Override
	public void addView(View child)
	{
		if (getChildCount() > 0)
		{
			throw new IllegalStateException("ScrollView2D can host only one direct child");
		}
		super.addView(child);
	}

	@Override
	public void addView(View child, int index)
	{
		if (getChildCount() > 0)
		{
			throw new IllegalStateException("ScrollView2D can host only one direct child");
		}
		super.addView(child, index);
	}

	@Override
	public void addView(View child, ViewGroup.LayoutParams params)
	{
		if (getChildCount() > 0)
		{
			throw new IllegalStateException("ScrollView2D can host only one direct child");
		}
		super.addView(child, params);
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params)
	{
		if (getChildCount() > 0)
		{
			throw new IllegalStateException("ScrollView2D can host only one direct child");
		}
		super.addView(child, index, params);
	}

	/**
	 * @return Returns true this ScrollView2D can be scrolled
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean canScroll()
	{
		View child = getChildAt(0);
		if (child != null)
		{
			int childHeight = child.getHeight();
			int childWidth = child.getWidth();
			return (getHeight() < childHeight + getPaddingTop() + getPaddingBottom()) || (getWidth() < childWidth + getPaddingLeft() + getPaddingRight());
		}
		return false;
	}

	@Override
	public boolean dispatchKeyEvent(@NonNull KeyEvent event)
	{
		// Let the focused view and/or our descendants get the key first
		boolean handled = super.dispatchKeyEvent(event);
		return handled || executeKeyEvent(event);
	}

	/**
	 * You can call this function yourself to have the scroll view perform scrolling from a key event, just as if the event had been dispatched to it by the
	 * view hierarchy.
	 *
	 * @param event The key event to execute.
	 * @return Return true if the event was handled, else false.
	 */
	private boolean executeKeyEvent(@NonNull KeyEvent event)
	{
		this.mTempRect.setEmpty();
		if (!canScroll())
		{
			if (isFocused())
			{
				View currentFocused = findFocus();
				if (currentFocused == this)
				{
					currentFocused = null;
				}
				View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, View.FOCUS_DOWN);
				return nextFocused != null && nextFocused != this && nextFocused.requestFocus(View.FOCUS_DOWN);
			}
			return false;
		}
		boolean handled = false;
		if (event.getAction() == KeyEvent.ACTION_DOWN)
		{
			switch (event.getKeyCode())
			{
				case KeyEvent.KEYCODE_DPAD_UP:
					if (!event.isAltPressed())
					{
						handled = arrowScroll(View.FOCUS_UP, false);
					}
					else
					{
						handled = fullScroll(View.FOCUS_UP, false);
					}
					break;
				case KeyEvent.KEYCODE_DPAD_DOWN:
					if (!event.isAltPressed())
					{
						handled = arrowScroll(View.FOCUS_DOWN, false);
					}
					else
					{
						handled = fullScroll(View.FOCUS_DOWN, false);
					}
					break;
				case KeyEvent.KEYCODE_DPAD_LEFT:
					if (!event.isAltPressed())
					{
						handled = arrowScroll(View.FOCUS_LEFT, true);
					}
					else
					{
						handled = fullScroll(View.FOCUS_LEFT, true);
					}
					break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					if (!event.isAltPressed())
					{
						handled = arrowScroll(View.FOCUS_RIGHT, true);
					}
					else
					{
						handled = fullScroll(View.FOCUS_RIGHT, true);
					}
					break;
			}
		}
		return handled;
	}

	@Override
	public boolean onInterceptTouchEvent(@NonNull MotionEvent ev)
	{
		/*
		 * This method JUST determines whether we want to intercept the motion. If we return true, onMotionEvent will be called and we do the actual scrolling
		 * there.
		 *
		 * Shortcut the most recurring case: the user is in the dragging state and he is moving his finger. We want to intercept this motion.
		 */
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (this.mIsBeingDragged))
		{
			return true;
		}
		if (!canScroll())
		{
			this.mIsBeingDragged = false;
			return false;
		}
		final float y = ev.getY();
		final float x = ev.getX();
		switch (action)
		{
			case MotionEvent.ACTION_MOVE:
				/*
				 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check whether the user has moved far enough from his original down touch.
				 */
				/*
				 * Locally do absolute value. mLastMotionY is set to the y value of the down event.
				 */
				final int yDiff = (int) Math.abs(y - this.mLastMotionY);
				final int xDiff = (int) Math.abs(x - this.mLastMotionX);
				if (yDiff > this.mTouchSlop || xDiff > this.mTouchSlop)
				{
					this.mIsBeingDragged = true;
				}
				break;

			case MotionEvent.ACTION_DOWN:
				/* Remember location of down touch */
				this.mLastMotionY = y;
				this.mLastMotionX = x;

				/*
				 * If being flung and user touches the screen, initiate drag; otherwise don't. mScroller.isFinished should be false when being flung.
				 */
				this.mIsBeingDragged = !this.mScroller.isFinished();
				break;

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				/* Release the drag */
				this.mIsBeingDragged = false;
				break;
		}

		/*
		 * The only time we want to intercept motion events is if we are in the drag mode.
		 */
		return this.mIsBeingDragged;
	}

	@Override
	public boolean performClick()
	{
		// Calls the super implementation, which generates an AccessibilityEvent and calls the onClick() listener on the view, if any
		super.performClick();
		// Handle the action for the custom click here
		return true;
	}

	@Override
	public boolean onTouchEvent(@NonNull MotionEvent event)
	{
		// accessibility
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				performClick();
				break;
			default:
				break;
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0)
		{
			// Don't handle edge touches immediately -- they may actually belong to one of our descendants.
			return false;
		}

		if (!canScroll())
		{
			return false;
		}

		if (this.mVelocityTracker == null)
		{
			this.mVelocityTracker = VelocityTracker.obtain();
		}
		this.mVelocityTracker.addMovement(event);

		final int action = event.getAction();
		final float y = event.getY();
		final float x = event.getX();

		switch (action)
		{
			case MotionEvent.ACTION_DOWN:
				/*
				 * If being flung and user touches, stop the fling. isFinished will be false if being flung.
				 */
				if (!this.mScroller.isFinished())
				{
					this.mScroller.abortAnimation();
				}

				// Remember where the motion event started
				this.mLastMotionY = y;
				this.mLastMotionX = x;
				break;
			case MotionEvent.ACTION_MOVE:
				// Scroll to follow the motion event
				int deltaX = (int) (this.mLastMotionX - x);
				int deltaY = (int) (this.mLastMotionY - y);
				this.mLastMotionX = x;
				this.mLastMotionY = y;

				if (deltaX < 0)
				{
					if (getScrollX() < 0)
					{
						deltaX = 0;
					}
				}
				else if (deltaX > 0)
				{
					final int rightEdge = getWidth() - getPaddingRight();
					final int availableToScroll = getChildAt(0).getRight() - getScrollX() - rightEdge;
					if (availableToScroll > 0)
					{
						deltaX = Math.min(availableToScroll, deltaX);
					}
					else
					{
						deltaX = 0;
					}
				}
				if (deltaY < 0)
				{
					if (getScrollY() < 0)
					{
						deltaY = 0;
					}
				}
				else if (deltaY > 0)
				{
					final int bottomEdge = getHeight() - getPaddingBottom();
					final int availableToScroll = getChildAt(0).getBottom() - getScrollY() - bottomEdge;
					if (availableToScroll > 0)
					{
						deltaY = Math.min(availableToScroll, deltaY);
					}
					else
					{
						deltaY = 0;
					}
				}
				if (deltaY != 0 || deltaX != 0)
				{
					scrollBy(deltaX, deltaY);
				}
				break;
			case MotionEvent.ACTION_UP:
				final VelocityTracker velocityTracker = this.mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
				int initialXVelocity = (int) velocityTracker.getXVelocity();
				int initialYVelocity = (int) velocityTracker.getYVelocity();
				if ((Math.abs(initialXVelocity) + Math.abs(initialYVelocity) > this.mMinimumVelocity) && getChildCount() > 0)
				{
					fling(-initialXVelocity, -initialYVelocity);
				}
				if (this.mVelocityTracker != null)
				{
					this.mVelocityTracker.recycle();
					this.mVelocityTracker = null;
				}
		}
		return true;
	}

	/**
	 * Finds the next focusable component that fits in this View's bounds (excluding fading edges) pretending that this View's top is located at the parameter
	 * top.
	 *
	 * @param leftFocus          look for a candidate as the one at the left of the bounds if leftFocus is true, or at the right of the bounds if leftFocus is false
	 * @param left               the left offset of the bounds in which a focusable must be found (the fading edge is assumed to start at this position)
	 * @param topFocus           look for a candidate as the one at the top of the bounds if topFocus is true, or at the bottom of the bounds if topFocus is false
	 * @param top                the top offset of the bounds in which a focusable must be found (the fading edge is assumed to start at this position)
	 * @param preferredFocusable the View that has highest priority and will be returned if it is within my bounds (null is valid)
	 * @return the next focusable component in the bounds or null if none can be found
	 */
	@Nullable
	private View findFocusableViewInMyBounds(final boolean leftFocus, final int left, final boolean topFocus, final int top, @Nullable View preferredFocusable)
	{
		/*
		 * The fading edge's transparent side should be considered for focus since it's mostly visible, so we divide the actual fading edge length by 2.
		 */
		final int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength() / 2;
		final int leftWithoutFadingEdge = left + horizontalFadingEdgeLength;
		final int rightWithoutFadingEdge = left + getWidth() - horizontalFadingEdgeLength;

		final int verticalFadingEdgeLength = getVerticalFadingEdgeLength() / 2;
		final int topWithoutFadingEdge = top + verticalFadingEdgeLength;
		final int bottomWithoutFadingEdge = top + getHeight() - verticalFadingEdgeLength;

		if ((preferredFocusable != null) &&  //
				(preferredFocusable.getTop() < bottomWithoutFadingEdge) &&  //
				(preferredFocusable.getBottom() > topWithoutFadingEdge) &&  //
				(preferredFocusable.getLeft() < rightWithoutFadingEdge) &&  //
				(preferredFocusable.getRight() > leftWithoutFadingEdge))
		{
			return preferredFocusable;
		}
		return findFocusableViewInBounds(leftFocus, leftWithoutFadingEdge, rightWithoutFadingEdge, topFocus, topWithoutFadingEdge, bottomWithoutFadingEdge);
	}

	/**
	 * Finds the next focusable component that fits in the specified bounds. </p>
	 *
	 * @param topFocus  look for a candidate as the one at the top of the bounds if topFocus is true, or at the bottom of the bounds if topFocus is false
	 * @param top       the top offset of the bounds in which a focusable must be found
	 * @param bottom    the bottom offset of the bounds in which a focusable must be found
	 * @param leftFocus look for a candidate as the one at the left of the bounds if leftFocus is true, or at the right of the bounds if leftFocus is false
	 * @param left      the left offset of the bounds in which a focusable must be found
	 * @param right     the right offset of the bounds in which a focusable must be found
	 * @return the next focusable component in the bounds or null if none can be found
	 */
	@Nullable
	private View findFocusableViewInBounds(boolean leftFocus, int left, int right, boolean topFocus, int top, int bottom)
	{
		List<View> focusables = getFocusables(View.FOCUS_FORWARD);
		View focusCandidate = null;

		/*
		 * A fully contained focusable is one where its top is below the bound's top, and its bottom is above the bound's bottom. A partially contained
		 * focusable is one where some part of it is within the bounds, but it also has some part that is not within bounds. A fully contained focusable is
		 * preferred to a partially contained focusable.
		 */
		boolean foundFullyContainedFocusable = false;

		int count = focusables.size();
		for (int i = 0; i < count; i++)
		{
			View view = focusables.get(i);
			int viewTop = view.getTop();
			int viewBottom = view.getBottom();
			int viewLeft = view.getLeft();
			int viewRight = view.getRight();

			if (top < viewBottom && viewTop < bottom && left < viewRight && viewLeft < right)
			{
				/*
				 * the focusable is in the target area, it is a candidate for focusing
				 */
				final boolean viewIsFullyContained = (top < viewTop) && (viewBottom < bottom) && (left < viewLeft) && (viewRight < right);
				if (focusCandidate == null)
				{
					/* No candidate, take this one */
					focusCandidate = view;
					foundFullyContainedFocusable = viewIsFullyContained;
				}
				else
				{
					final boolean viewIsCloserToVerticalBoundary = topFocus ? viewTop < focusCandidate.getTop() : viewBottom > focusCandidate.getBottom();
					final boolean viewIsCloserToHorizontalBoundary = leftFocus ? viewLeft < focusCandidate.getLeft() : viewRight > focusCandidate.getRight();
					if (foundFullyContainedFocusable)
					{
						if (viewIsFullyContained && viewIsCloserToVerticalBoundary && viewIsCloserToHorizontalBoundary)
						{
							/*
							 * We're dealing with only fully contained views, so it has to be closer to the boundary to beat our candidate
							 */
							focusCandidate = view;
						}
					}
					else
					{
						if (viewIsFullyContained)
						{
							/* Any fully contained view beats a partially contained view */
							focusCandidate = view;
							foundFullyContainedFocusable = true;
						}
						else if (viewIsCloserToVerticalBoundary && viewIsCloserToHorizontalBoundary)
						{
							/*
							 * Partially contained view beats another partially contained view if it's closer
							 */
							focusCandidate = view;
						}
					}
				}
			}
		}
		return focusCandidate;
	}

	/**
	 * <p>
	 * Handles scrolling in response to a "home/end" shortcut press. This method will scroll the view to the top or bottom and give the focus to the
	 * topmost/bottommost component in the new visible area. If no component is a good candidate for focus, this scrollview reclaims the focus.
	 * </p>
	 *
	 * @param direction the scroll direction: {@link android.view.View#FOCUS_UP} to go the top of the view or {@link android.view.View#FOCUS_DOWN} to go the bottom
	 * @return true if the key event is consumed by this method, false otherwise
	 */
	private boolean fullScroll(int direction, boolean horizontal)
	{
		if (!horizontal)
		{
			boolean down = direction == View.FOCUS_DOWN;
			int height = getHeight();
			this.mTempRect.top = 0;
			this.mTempRect.bottom = height;
			if (down)
			{
				int count = getChildCount();
				if (count > 0)
				{
					View view = getChildAt(count - 1);
					this.mTempRect.bottom = view.getBottom();
					this.mTempRect.top = this.mTempRect.bottom - height;
				}
			}
			return scrollAndFocus(direction, this.mTempRect.top, this.mTempRect.bottom, 0, 0, 0);
		}
		else
		{
			boolean right = direction == View.FOCUS_DOWN;
			int width = getWidth();
			this.mTempRect.left = 0;
			this.mTempRect.right = width;
			if (right)
			{
				int count = getChildCount();
				if (count > 0)
				{
					View view = getChildAt(count - 1);
					this.mTempRect.right = view.getRight();
					this.mTempRect.left = this.mTempRect.right - width;
				}
			}
			return scrollAndFocus(0, 0, 0, direction, this.mTempRect.left, this.mTempRect.right);
		}
	}

	/**
	 * <p>
	 * Scrolls the view to make the area defined by <code>top</code> and <code>bottom</code> visible. This method attempts to give the focus to a component
	 * visible in this area. If no component can be focused in the new visible area, the focus is reclaimed by this scrollview.
	 * </p>
	 *
	 * @param directionY the scroll direction: {@link android.view.View#FOCUS_UP} to go upward {@link android.view.View#FOCUS_DOWN} to downward
	 * @param top        the top offset of the new area to be made visible
	 * @param bottom     the bottom offset of the new area to be made visible
	 * @param directionX the scroll direction: {@link android.view.View#FOCUS_UP} to go upward {@link android.view.View#FOCUS_DOWN} to downward
	 * @param left       the left offset of the new area to be made visible
	 * @param right      the right offset of the new area to be made visible
	 * @return true if the key event is consumed by this method, false otherwise
	 */
	private boolean scrollAndFocus(int directionY, int top, int bottom, int directionX, int left, int right)
	{
		int height = getHeight();
		int containerTop = getScrollY();
		int containerBottom = containerTop + height;
		boolean up = directionY == View.FOCUS_UP;
		int width = getWidth();
		int containerLeft = getScrollX();
		int containerRight = containerLeft + width;
		boolean leftwards = directionX == View.FOCUS_UP;
		View newFocused = findFocusableViewInBounds(leftwards, left, right, up, top, bottom);
		if (newFocused == null)
		{
			newFocused = this;
		}
		boolean handled = true;
		if ((top >= containerTop && bottom <= containerBottom) || (left >= containerLeft && right <= containerRight))
		{
			handled = false;
		}
		else
		{
			int deltaY = up ? (top - containerTop) : (bottom - containerBottom);
			int deltaX = leftwards ? (left - containerLeft) : (right - containerRight);
			doScroll(deltaX, deltaY);
		}
		if (newFocused != findFocus() && newFocused.requestFocus(directionY))
		{
			this.mTwoDScrollViewMovedFocus = true;
			this.mTwoDScrollViewMovedFocus = false;
		}
		return handled;
	}

	/**
	 * Handle scrolling in response to an up or down arrow click.
	 *
	 * @param direction The direction corresponding to the arrow key that was pressed
	 * @return True if we consumed the event, false otherwise
	 */
	private boolean arrowScroll(int direction, boolean horizontal)
	{
		View currentFocused = findFocus();
		if (currentFocused == this)
		{
			currentFocused = null;
		}
		View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, direction);
		final int maxJump = horizontal ? getMaxScrollAmountHorizontal() : getMaxScrollAmountVertical();

		if (!horizontal)
		{
			if (nextFocused != null)
			{
				nextFocused.getDrawingRect(this.mTempRect);
				offsetDescendantRectToMyCoords(nextFocused, this.mTempRect);
				int scrollDelta = computeScrollDeltaToGetChildRectOnScreen(this.mTempRect);
				doScroll(0, scrollDelta);
				nextFocused.requestFocus(direction);
			}
			else
			{
				// no new focus
				int scrollDelta = maxJump;
				if (direction == View.FOCUS_UP && getScrollY() < scrollDelta)
				{
					scrollDelta = getScrollY();
				}
				else if (direction == View.FOCUS_DOWN)
				{
					if (getChildCount() > 0)
					{
						int daBottom = getChildAt(0).getBottom();
						int screenBottom = getScrollY() + getHeight();
						if (daBottom - screenBottom < maxJump)
						{
							scrollDelta = daBottom - screenBottom;
						}
					}
				}
				if (scrollDelta == 0)
				{
					return false;
				}
				doScroll(0, direction == View.FOCUS_DOWN ? scrollDelta : -scrollDelta);
			}
		}
		else
		{
			if (nextFocused != null)
			{
				nextFocused.getDrawingRect(this.mTempRect);
				offsetDescendantRectToMyCoords(nextFocused, this.mTempRect);
				int scrollDelta = computeScrollDeltaToGetChildRectOnScreen(this.mTempRect);
				doScroll(scrollDelta, 0);
				nextFocused.requestFocus(direction);
			}
			else
			{
				// no new focus
				int scrollDelta = maxJump;
				if (direction == View.FOCUS_UP && getScrollY() < scrollDelta)
				{
					scrollDelta = getScrollY();
				}
				else if (direction == View.FOCUS_DOWN)
				{
					if (getChildCount() > 0)
					{
						int daBottom = getChildAt(0).getBottom();
						int screenBottom = getScrollY() + getHeight();
						if (daBottom - screenBottom < maxJump)
						{
							scrollDelta = daBottom - screenBottom;
						}
					}
				}
				if (scrollDelta == 0)
				{
					return false;
				}
				doScroll(direction == View.FOCUS_DOWN ? scrollDelta : -scrollDelta, 0);
			}
		}
		return true;
	}

	/**
	 * Smooth scroll by a Y delta
	 *
	 * @param deltaX the number of pixels to scroll by on the X axis
	 * @param deltaY the number of pixels to scroll by on the Y axis
	 */
	private void doScroll(int deltaX, int deltaY)
	{
		if (deltaX != 0 || deltaY != 0)
		{
			smoothScrollBy(deltaX, deltaY);
		}
	}

	/**
	 * Like {@link View#scrollBy}, but scroll smoothly instead of immediately.
	 *
	 * @param dx the number of pixels to scroll by on the X axis
	 * @param dy the number of pixels to scroll by on the Y axis
	 */
	private void smoothScrollBy(int dx, int dy)
	{
		long duration = AnimationUtils.currentAnimationTimeMillis() - this.mLastScroll;
		if (duration > ANIMATED_SCROLL_GAP)
		{
			this.mScroller.startScroll(getScrollX(), getScrollY(), dx, dy);
			awakenScrollBars(this.mScroller.getDuration());
			invalidate();
		}
		else
		{
			if (!this.mScroller.isFinished())
			{
				this.mScroller.abortAnimation();
			}
			scrollBy(dx, dy);
		}
		this.mLastScroll = AnimationUtils.currentAnimationTimeMillis();
	}

	/**
	 * Like {@link #scrollTo}, but scroll smoothly instead of immediately.
	 *
	 * @param x the position where to scroll on the X axis
	 * @param y the position where to scroll on the Y axis
	 */
	@SuppressWarnings("unused")
	public final void smoothScrollTo(int x, int y)
	{
		smoothScrollBy(x - getScrollX(), y - getScrollY());
	}

	/**
	 * <p>
	 * The scroll range of a scroll view is the overall height of all of its children.
	 * </p>
	 */
	@Override
	protected int computeVerticalScrollRange()
	{
		int count = getChildCount();
		return count == 0 ? getHeight() : (getChildAt(0)).getBottom();
	}

	@Override
	protected int computeHorizontalScrollRange()
	{
		int count = getChildCount();
		return count == 0 ? getWidth() : (getChildAt(0)).getRight();
	}

	@Override
	protected void measureChild(@NonNull View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec)
	{
		ViewGroup.LayoutParams lp = child.getLayoutParams();

		int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, getPaddingLeft() + getPaddingRight(), lp.width);
		int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

		child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
	}

	@Override
	protected void measureChildWithMargins(@NonNull View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed)
	{
		final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
		final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(lp.leftMargin + lp.rightMargin, MeasureSpec.UNSPECIFIED);
		final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.topMargin + lp.bottomMargin, MeasureSpec.UNSPECIFIED);

		child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
	}

	@Override
	public void computeScroll()
	{
		if (this.mScroller.computeScrollOffset())
		{
			// This is called at drawing time by ViewGroup. We don't want to
			// re-show the scrollbars at this point, which scrollTo will do,
			// so we replicate most of scrollTo here.
			//
			// It's a little odd to call onScrollChanged from inside the drawing.
			//
			// It is, except when you remember that computeScroll() is used to
			// animate scrolling. So unless we want to defer the onScrollChanged()
			// until the end of the animated scrolling, we don't really have a
			// choice here.
			//
			// I agree. The alternative, which I think would be worse, is to post
			// something and tell the subclasses later. This is bad because there
			// will be a window where mScrollX/Y is different from what the app
			// thinks it is.
			//
			int oldX = getScrollX();
			int oldY = getScrollY();
			int x = this.mScroller.getCurrX();
			int y = this.mScroller.getCurrY();
			if (getChildCount() > 0)
			{
				View child = getChildAt(0);
				scrollTo(clamp(x, getWidth() - getPaddingRight() - getPaddingLeft(), child.getWidth()), clamp(y, getHeight() - getPaddingBottom() - getPaddingTop(), child.getHeight()));
			}
			else
			{
				scrollTo(x, y);
			}
			if (oldX != getScrollX() || oldY != getScrollY())
			{
				onScrollChanged(getScrollX(), getScrollY(), oldX, oldY);
			}

			// Keep on drawing until the animation has finished.
			postInvalidate();
		}
	}

	/**
	 * Scrolls the view to the given child.
	 *
	 * @param child the View to scroll to
	 */
	private void scrollToChild(@NonNull View child)
	{
		child.getDrawingRect(this.mTempRect);
		/* Offset from child's local coordinates to ScrollView2D coordinates */
		offsetDescendantRectToMyCoords(child, this.mTempRect);
		int scrollDelta = computeScrollDeltaToGetChildRectOnScreen(this.mTempRect);
		if (scrollDelta != 0)
		{
			scrollBy(0, scrollDelta);
		}
	}

	/**
	 * If rect is off screen, scroll just enough to get it (or at least the first screen size chunk of it) on screen.
	 *
	 * @param rect      The rectangle.
	 * @param immediate True to scroll immediately without animation
	 * @return true if scrolling was performed
	 */
	private boolean scrollToChildRect(@NonNull Rect rect, boolean immediate)
	{
		final int delta = computeScrollDeltaToGetChildRectOnScreen(rect);
		final boolean scroll = delta != 0;
		if (scroll)
		{
			if (immediate)
			{
				scrollBy(0, delta);
			}
			else
			{
				smoothScrollBy(0, delta);
			}
		}
		return scroll;
	}

	/**
	 * Compute the amount to scroll in the Y direction in order to get a rectangle completely on the screen (or, if taller than the screen, at least the first
	 * screen size chunk of it).
	 *
	 * @param rect The rect.
	 * @return The scroll delta.
	 */
	private int computeScrollDeltaToGetChildRectOnScreen(@NonNull Rect rect)
	{
		if (getChildCount() == 0)
		{
			return 0;
		}
		int height = getHeight();
		int screenTop = getScrollY();
		int screenBottom = screenTop + height;
		int fadingEdge = getVerticalFadingEdgeLength();
		// leave room for top fading edge as long as rect isn't at very top
		if (rect.top > 0)
		{
			screenTop += fadingEdge;
		}

		// leave room for bottom fading edge as long as rect isn't at very bottom
		if (rect.bottom < getChildAt(0).getHeight())
		{
			screenBottom -= fadingEdge;
		}
		int scrollYDelta = 0;
		if (rect.bottom > screenBottom && rect.top > screenTop)
		{
			// need to move down to get it in view: move down just enough so
			// that the entire rectangle is in view (or at least the first
			// screen size chunk).
			if (rect.height() > height)
			{
				// just enough to get screen size chunk on
				scrollYDelta += (rect.top - screenTop);
			}
			else
			{
				// get entire rect at bottom of screen
				scrollYDelta += (rect.bottom - screenBottom);
			}

			// make sure we aren't scrolling beyond the end of our content
			int bottom = getChildAt(0).getBottom();
			int distanceToBottom = bottom - screenBottom;
			scrollYDelta = Math.min(scrollYDelta, distanceToBottom);

		}
		else if (rect.top < screenTop && rect.bottom < screenBottom)
		{
			// need to move up to get it in view: move up just enough so that
			// entire rectangle is in view (or at least the first screen
			// size chunk of it).

			if (rect.height() > height)
			{
				// screen size chunk
				scrollYDelta -= (screenBottom - rect.bottom);
			}
			else
			{
				// entire rect at top
				scrollYDelta -= (screenTop - rect.top);
			}

			// make sure we aren't scrolling any further than the top our content
			scrollYDelta = Math.max(scrollYDelta, -getScrollY());
		}
		return scrollYDelta;
	}

	@Override
	public void requestChildFocus(View child, @NonNull View focused)
	{
		if (!this.mTwoDScrollViewMovedFocus)
		{
			if (!this.mIsLayoutDirty)
			{
				scrollToChild(focused);
			}
			else
			{
				// The child may not be laid out yet, we can't compute the scroll yet
				this.mChildToScrollTo = focused;
			}
		}
		super.requestChildFocus(child, focused);
	}

	/**
	 * When looking for focus in children of a scroll view, need to be a little more careful not to give focus to something that is scrolled off screen.
	 * <p/>
	 * This is more expensive than the default {@link android.view.ViewGroup} implementation, otherwise this behavior might have been made the default.
	 */
	@Override
	protected boolean onRequestFocusInDescendants(int direction, @Nullable Rect previouslyFocusedRect)
	{
		int actualDirection = direction;

		// convert from forward / backward notation to up / down / left / right
		// (ugh).
		if (actualDirection == View.FOCUS_FORWARD)
		{
			actualDirection = View.FOCUS_DOWN;
		}
		else if (actualDirection == View.FOCUS_BACKWARD)
		{
			actualDirection = View.FOCUS_UP;
		}

		final View nextFocus = previouslyFocusedRect == null ? FocusFinder.getInstance().findNextFocus(this, null, actualDirection) : FocusFinder.getInstance().findNextFocusFromRect(this, previouslyFocusedRect, actualDirection);

		return nextFocus != null && nextFocus.requestFocus(actualDirection, previouslyFocusedRect);
	}

	@Override
	public boolean requestChildRectangleOnScreen(@NonNull View child, @NonNull Rect rectangle, boolean immediate)
	{
		// offset into coordinate space of this scroll view
		rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
		return scrollToChildRect(rectangle, immediate);
	}

	@Override
	public void requestLayout()
	{
		this.mIsLayoutDirty = true;
		super.requestLayout();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
		this.mIsLayoutDirty = false;
		// Give a child focus if it needs it
		if (this.mChildToScrollTo != null && isViewDescendantOf(this.mChildToScrollTo, this))
		{
			scrollToChild(this.mChildToScrollTo);
		}
		this.mChildToScrollTo = null;

		// Calling this with the present values causes it to re-clam them
		scrollTo(getScrollX(), getScrollY());
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		View currentFocused = findFocus();
		if (null == currentFocused || this == currentFocused)
		{
			return;
		}

		// If the currently-focused view was visible on the screen when the
		// screen was at the old height, then scroll the screen to make that
		// view visible with the new screen height.
		currentFocused.getDrawingRect(this.mTempRect);
		offsetDescendantRectToMyCoords(currentFocused, this.mTempRect);
		int scrollDeltaX = computeScrollDeltaToGetChildRectOnScreen(this.mTempRect);
		int scrollDeltaY = computeScrollDeltaToGetChildRectOnScreen(this.mTempRect);
		doScroll(scrollDeltaX, scrollDeltaY);
	}

	/**
	 * Return true if child is an descendant of parent, (or equal to the parent).
	 */
	private boolean isViewDescendantOf(@NonNull View child, View parent)
	{
		if (child == parent)
		{
			return true;
		}

		final ViewParent childParent = child.getParent();
		return (childParent instanceof ViewGroup) && isViewDescendantOf((View) childParent, parent);
	}

	/**
	 * Fling the scroll view
	 *
	 * @param velocityY The initial velocity in the Y direction. Positive numbers mean that the finger/cursor is moving down the screen, which means we want to scroll
	 *                  towards the top.
	 */
	private void fling(int velocityX, int velocityY)
	{
		if (getChildCount() > 0)
		{
			int width = getWidth() - getPaddingRight() - getPaddingLeft();
			int right = getChildAt(0).getWidth();
			int height = getHeight() - getPaddingBottom() - getPaddingTop();
			int bottom = getChildAt(0).getHeight();

			this.mScroller.fling(getScrollX(), getScrollY(), velocityX, velocityY, 0, right - width, 0, bottom - height);

			final boolean movingDown = velocityY > 0;
			final boolean movingRight = velocityX > 0;

			View newFocused = findFocusableViewInMyBounds(movingRight, this.mScroller.getFinalX(), movingDown, this.mScroller.getFinalY(), findFocus());
			if (newFocused == null)
			{
				newFocused = this;
			}

			if (newFocused != findFocus() && newFocused.requestFocus(movingDown ? View.FOCUS_DOWN : View.FOCUS_UP))
			{
				this.mTwoDScrollViewMovedFocus = true;
				this.mTwoDScrollViewMovedFocus = false;
			}

			awakenScrollBars(this.mScroller.getDuration());
			invalidate();
		}
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * <p>
	 * This version also clamps the scrolling to the bounds of our child.
	 */
	@Override
	public void scrollTo(int destX, int destY)
	{
		int x = destX;
		int y = destY;

		// we rely on the fact the View.scrollBy calls scrollTo.
		if (getChildCount() > 0)
		{
			View child = getChildAt(0);
			x = clamp(x, getWidth() - getPaddingRight() - getPaddingLeft(), child.getWidth());
			y = clamp(y, getHeight() - getPaddingBottom() - getPaddingTop(), child.getHeight());
			if (x != getScrollX() || y != getScrollY())
			{
				super.scrollTo(x, y);
			}
		}
	}

	private int clamp(int n, int my, int child)
	{
		if (my >= child || n < 0)
		{
			/*
			 * my >= child is this case: |--------------- me ---------------| |------ child ------| or |--------------- me ---------------| |------ child
			 * ------| or |--------------- me ---------------| |------ child ------|
			 *
			 * n < 0 is this case: |------ me ------| |-------- child --------| |-- mScrollX --|
			 */
			return 0;
		}
		if ((my + n) > child)
		{
			/*
			 * this case: |------ me ------| |------ child ------| |-- mScrollX --|
			 */
			return child - my;
		}
		return n;
	}
}
