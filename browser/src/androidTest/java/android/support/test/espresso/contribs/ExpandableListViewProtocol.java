/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.test.espresso.contribs;

import android.database.Cursor;
import android.os.Build;
import android.support.test.espresso.action.AdapterViewProtocol;
import android.support.test.espresso.core.deps.guava.base.Optional;
import android.support.test.espresso.core.deps.guava.collect.Lists;
import android.support.test.espresso.core.deps.guava.collect.Range;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterViewAnimator;
import android.widget.AdapterViewFlipper;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.List;

import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkArgument;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;

/**
 * Implementations of {@link AdapterViewProtocol} for ExpandableListView SDK Widgets.
 *
 * @author Bernard Bou (1313ou@gmail.com)
 */
public final class ExpandableListViewProtocol implements AdapterViewProtocol
{
	private static final String TAG = "ELVProtocol";

	private static final AdapterViewProtocol ELV_PROTOCOL = new ExpandableListViewProtocol();

	/**
	 * Creates an implementation of AdapterViewProtocol that can work with ExpandableListViews.
	 */
	public static AdapterViewProtocol elvProtocol()
	{
		return ELV_PROTOCOL;
	}

	/**
	 * Consider views which have over this percentage of their area visible to the user
	 * to be fully rendered.
	 */
	private static final int FULLY_RENDERED_PERCENTAGE_CUTOFF = 90;

	private static final class ExpandableDataFunction implements DataFunction
	{
		private final Object dataAtPosition;
		private final int group;
		private final int position; // -1 stand for group itself, not group item

		private ExpandableDataFunction(Object dataAtPosition, int group, int position)
		{
			checkArgument(group >= 0, "group must be >= 0");
			checkArgument(position >= -1, "position must be >= -1");
			this.dataAtPosition = dataAtPosition;
			this.group = group;
			this.position = position;
		}

		@Override
		public Object getData()
		{
			if (dataAtPosition instanceof Cursor)
			{
				int p = position == -1 ? group : position;
				if (!((Cursor) dataAtPosition).moveToPosition(p))
				{
					Log.e(TAG, "Cannot move cursor to position: " + p);
				}
			}
			return dataAtPosition;
		}
	}

	/**
	 * Returns all data this AdapterViewProtocol can find within the given AdapterView.
	 * <p>
	 * Any AdaptedData returned by this method can be passed to makeDataRenderedWithinView and the
	 * implementation should make the AdapterView bring that data item onto the screen.
	 *
	 * @param adapterView the AdapterView we want to interrogate the contents of.
	 * @return an {@link Iterable} of AdaptedDatas representing all data the implementation sees in
	 * this view
	 * @throws IllegalArgumentException if the implementation doesn't know how to manipulate the given
	 *                                  adapter view.
	 */
	@Override
	public Iterable<AdaptedData> getDataInAdapterView(AdapterView<? extends Adapter> adapterView)
	{
		ExpandableListView elv = (ExpandableListView) adapterView;
		ExpandableListAdapter ela = elv.getExpandableListAdapter();
		List<AdaptedData> adaptedDatas = Lists.newArrayList();
		for (int g = 0; g < ela.getGroupCount(); g++)
		{
			// group
			Object groupDataAtPosition = ela.getGroup(g);
			adaptedDatas.add(new AdaptedData.Builder().withDataFunction(new ExpandableDataFunction(groupDataAtPosition, g, -1)).withOpaqueToken(new Pair<>(g, -1)).build());

			// group children
			for (int c = 0; c < ela.getChildrenCount(g); c++)
			{
				Object dataAtPosition = ela.getChild(g, c);
				adaptedDatas.add(new AdaptedData.Builder().withDataFunction(new ExpandableDataFunction(dataAtPosition, g, c)).withOpaqueToken(new Pair<>(g, c)).build());
			}
		}
		return adaptedDatas;
	}

	/**
	 * Returns the data object this particular view is rendering if possible.
	 * <p>
	 * Implementations are expected to create a relationship between the data in the AdapterView and
	 * the descendant views of the AdapterView that obeys the following conditions:
	 * <p>
	 * <ul>
	 * <li>For each descendant view there exists either 0 or 1 data objects it is rendering.</li>
	 * <li>For each data object the AdapterView there exists either 0 or 1 descendant views which
	 * claim to be rendering it.</li>
	 * </ul>
	 * <p>
	 * For example - if a PersonObject is rendered into:
	 * <code>
	 * LinearLayout
	 * ImageView picture
	 * TextView firstName
	 * TextView lastName
	 * </code>
	 * <p>
	 * <p>
	 * It would be expected that getDataRenderedByView(adapter, LinearLayout) would return the
	 * PersonObject. If it were called instead with the TextView or ImageView it would return
	 * Object.absent().
	 * </p>
	 *
	 * @param adapterView    the adapterview hosting the data.
	 * @param descendantView a view which is a child, grand-child, or deeper descendant of adapterView
	 * @return an optional data object the descendant view is rendering.
	 * @throws IllegalArgumentException if this protocol cannot interrogate this class of adapterView
	 */
	@Override
	public Optional<AdaptedData> getDataRenderedByView(AdapterView<? extends Adapter> adapterView, View descendantView)
	{
		// if the target view is a child of list view
		if (adapterView == descendantView.getParent())
		{
			ExpandableListView elv = (ExpandableListView) adapterView;
			ExpandableListAdapter ela = elv.getExpandableListAdapter();

			int flatListPosition = adapterView.getPositionForView(descendantView); // CHECK
			if (flatListPosition != AdapterView.INVALID_POSITION)
			{
				long packedPosition = elv.getExpandableListPosition(flatListPosition);
				int groupPosition = elv.getPackedPositionGroup(packedPosition);
				int childPosition = elv.getPackedPositionChild(packedPosition);
				Object dataAtPosition = childPosition == -1 ? ela.getGroup(groupPosition) : ela.getChild(groupPosition, childPosition);
				return Optional.of(new AdaptedData.Builder() //
						.withDataFunction(new ExpandableDataFunction(dataAtPosition, groupPosition, childPosition)) //
						.withOpaqueToken(new Pair<>(groupPosition, childPosition)) //
						.build());
			}
		}
		return Optional.absent();
	}

	/**
	 * Requests that a particular piece of data held in this AdapterView is actually rendered by it.
	 * <p>
	 * After calling this method it expected that there will exist some descendant view of adapterView
	 * for which calling getDataRenderedByView(adapterView, descView).get() == data.data is true.
	 * <p>
	 * Note: this need not happen immediately. EG: an implementor handling ListView may call
	 * listView.smoothScrollToPosition(data.opaqueToken) - which kicks off an animated scroll over
	 * the list to the given position. The animation may be in progress after this call returns. The
	 * only guarantee is that eventually - with no further interaction necessary - this data item
	 * will be rendered as a child or deeper descendant of this AdapterView.
	 *
	 * @param adapterView the adapterView hosting the data.
	 * @param data        an AdaptedData instance retrieved by a prior call to getDataInAdapterView
	 * @throws IllegalArgumentException if this protocol cannot manipulate adapterView or if data is
	 *                                  not owned by this AdapterViewProtocol.
	 */
	@Override
	public void makeDataRenderedWithinAdapterView(AdapterView<? extends Adapter> adapterView, AdaptedData data)
	{
		ExpandableListView elv = (ExpandableListView) adapterView;
		ExpandableListAdapter ela = elv.getExpandableListAdapter();
		checkArgument(data.opaqueToken instanceof Pair, "Not my data: %s", data);
		Pair<Integer, Integer> position = (Pair<Integer, Integer>) data.opaqueToken;
		long packedPosition = elv.getPackedPositionForChild(position.first, position.second);
		int flatPosition = elv.getFlatListPosition(packedPosition);

		boolean moved = false;

		// set selection should always work, we can give a little better experience if per subtype though.
		if (Build.VERSION.SDK_INT > 7)
		{
			if (adapterView instanceof AbsListView)
			{
				if (Build.VERSION.SDK_INT > 10)
				{
					((AbsListView) adapterView).smoothScrollToPositionFromTop(flatPosition, adapterView.getPaddingTop(), 0);
				}
				else
				{
					((AbsListView) adapterView).smoothScrollToPosition(flatPosition);
				}
				moved = true;
			}
			if (Build.VERSION.SDK_INT > 10)
			{
				if (adapterView instanceof AdapterViewAnimator)
				{
					if (adapterView instanceof AdapterViewFlipper)
					{
						((AdapterViewFlipper) adapterView).stopFlipping();
					}
					((AdapterViewAnimator) adapterView).setDisplayedChild(flatPosition);
					moved = true;
				}
			}
		}
		if (!moved)
		{
			adapterView.setSelection(flatPosition);
		}
	}

	/**
	 * Indicates whether or not there now exists a descendant view within adapterView that
	 * is rendering this data.
	 *
	 * @param adapterView the AdapterView hosting this data.
	 * @param data        the data we are checking the display state for.
	 * @return true if the data is rendered by a view in the adapterView, false otherwise.
	 */
	@Override
	public boolean isDataRenderedWithinAdapterView(AdapterView<? extends Adapter> adapterView, AdaptedData data)
	{
		ExpandableListView elv = (ExpandableListView) adapterView;
		checkArgument(data.opaqueToken instanceof Pair, "Not my data: %s", data);
		Pair<Integer, Integer> dataPosition = (Pair<Integer, Integer>) data.opaqueToken;
		long packedPosition = elv.getPackedPositionForChild(dataPosition.first, dataPosition.second);
		int flatPosition = elv.getFlatListPosition(packedPosition);

		boolean inView = false;

		if (Range.closed(adapterView.getFirstVisiblePosition(), adapterView.getLastVisiblePosition()).contains(flatPosition))
		{
			if (adapterView.getFirstVisiblePosition() == adapterView.getLastVisiblePosition())
			{
				// thats a huge element.
				inView = true;
			}
			else
			{
				inView = isElementFullyRendered(adapterView, flatPosition - adapterView.getFirstVisiblePosition());
			}
		}
		if (inView)
		{
			// stops animations - locks in our x/y location.
			adapterView.setSelection(flatPosition);
		}

		return inView;
	}

	private boolean isElementFullyRendered(AdapterView<? extends Adapter> adapterView, int childAt)
	{
		View element = adapterView.getChildAt(childAt); // CHECK
		// Occasionally we'll have to fight with smooth scrolling logic on our definition of when
		// there is extra scrolling to be done. In particular if the element is the first or last
		// element of the list, the smooth scroller may decide that no work needs to be done to scroll
		// to the element if a certain percentage of it is on screen. Ugh. Sigh. Yuck.

		return isDisplayingAtLeast(FULLY_RENDERED_PERCENTAGE_CUTOFF).matches(element);
	}
}
