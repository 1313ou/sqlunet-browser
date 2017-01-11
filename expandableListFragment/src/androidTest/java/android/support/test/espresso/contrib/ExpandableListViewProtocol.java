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

package android.support.test.espresso.contrib;

import android.database.Cursor;
import android.os.Build;
import android.support.test.espresso.action.AdapterViewProtocol;
import android.support.test.espresso.core.deps.guava.base.Optional;
import android.support.test.espresso.core.deps.guava.base.Preconditions;
import android.support.test.espresso.core.deps.guava.collect.Lists;
import android.support.test.espresso.core.deps.guava.collect.Range;
import android.support.test.espresso.matcher.ViewMatchers;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.List;

/**
 * Implementations of {@link AdapterViewProtocol} for ExpandableListView SDK Widgets.
 *
 * @author Bernard Bou (1313ou@gmail.com)
 */
@SuppressWarnings("WeakerAccess")
public final class ExpandableListViewProtocol implements AdapterViewProtocol
{
	private static final String TAG = "ELVProtocol";

	private static final AdapterViewProtocol EXPANDABLELISTVIEW_PROTOCOL = new ExpandableListViewProtocol();

	/**
	 * Creates an implementation of AdapterViewProtocol that can work with ExpandableListViews.
	 */
	public static AdapterViewProtocol expandableListViewProtocol()
	{
		return EXPANDABLELISTVIEW_PROTOCOL;
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

		private final int child; // -1 stand for group itself, not group item

		private ExpandableDataFunction(Object dataAtPosition, int group, int child)
		{
			Preconditions.checkArgument(group >= 0, "group must be >= 0");
			Preconditions.checkArgument(child >= -1, "child must be >= -1");
			this.dataAtPosition = dataAtPosition;
			this.group = group;
			this.child = child;
		}

		@Override
		public Object getData()
		{
			if (this.dataAtPosition instanceof Cursor)
			{
				int p = this.child == -1 ? this.group : this.child;
				if (!((Cursor) this.dataAtPosition).moveToPosition(p))
				{
					Log.e(TAG, "Cannot move cursor to child: " + p);
				}
			}
			return this.dataAtPosition;
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
		final ExpandableListView expandableListView = (ExpandableListView) adapterView;
		final ExpandableListAdapter adapter = expandableListView.getExpandableListAdapter();
		final List<AdaptedData> adaptedDatas = Lists.newArrayList();
		for (int g = 0; g < adapter.getGroupCount(); g++)
		{
			// group
			final Object groupDataAtPosition = adapter.getGroup(g);
			adaptedDatas.add(new AdaptedData.Builder() //
					.withDataFunction(new ExpandableDataFunction(groupDataAtPosition, g, -1)) //
					.withOpaqueToken(new Pair<>(g, -1)) //
					.build());

			// group children
			for (int c = 0; c < adapter.getChildrenCount(g); c++)
			{
				final Object dataAtPosition = adapter.getChild(g, c);
				adaptedDatas.add(new AdaptedData.Builder() //
						.withDataFunction(new ExpandableDataFunction(dataAtPosition, g, c)) //
						.withOpaqueToken(new Pair<>(g, c)) //
						.build());
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
	 * @param adapterView    the adapter view hosting the data.
	 * @param descendantView a view which is a child, grand-child, or deeper descendant of adapterView
	 * @return an optional data object the descendant view is rendering.
	 * @throws IllegalArgumentException if this protocol cannot interrogate this class of adapterView
	 */
	@Override
	public Optional<AdaptedData> getDataRenderedByView(AdapterView<? extends Adapter> adapterView, View descendantView)
	{
		// if the target view is a child of list view (both groups and group children are)
		if (adapterView == descendantView.getParent())
		{
			final ExpandableListView expandableListView = (ExpandableListView) adapterView;
			final ExpandableListAdapter adapter = expandableListView.getExpandableListAdapter();

			int flatListPosition = adapterView.getPositionForView(descendantView);
			if (flatListPosition != AdapterView.INVALID_POSITION)
			{
				long packedPosition = expandableListView.getExpandableListPosition(flatListPosition);
				int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
				int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);
				final Object dataAtPosition = childPosition == -1 ? adapter.getGroup(groupPosition) : adapter.getChild(groupPosition, childPosition);
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
	 * the list to the given child. The animation may be in progress after this call returns. The
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
		Preconditions.checkArgument(data.opaqueToken instanceof Pair, "Not my data: %s", data);
		@SuppressWarnings({"unchecked", "ConstantConditions"}) final Pair<Integer, Integer> position = (Pair<Integer, Integer>) data.opaqueToken;

		final ExpandableListView expandableListView = (ExpandableListView) adapterView;
		long packedPosition = ExpandableListView.getPackedPositionForChild(position.first, position.second);
		int flatPosition = expandableListView.getFlatListPosition(packedPosition);

		boolean moved = false;

		// set selection should always work, we can give a little better experience if per subtype though.
		if (Build.VERSION.SDK_INT > 7)
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
		Preconditions.checkArgument(data.opaqueToken instanceof Pair, "Not my data: %s", data);
		@SuppressWarnings({"unchecked", "ConstantConditions"}) final Pair<Integer, Integer> dataPosition = (Pair<Integer, Integer>) data.opaqueToken;

		final ExpandableListView expandableListView = (ExpandableListView) adapterView;
		long packedPosition = ExpandableListView.getPackedPositionForChild(dataPosition.first, dataPosition.second);
		int flatPosition = expandableListView.getFlatListPosition(packedPosition);

		boolean inView = false;

		if (Range.closed(adapterView.getFirstVisiblePosition(), adapterView.getLastVisiblePosition()).contains(flatPosition))
		{
			if (adapterView.getFirstVisiblePosition() == adapterView.getLastVisiblePosition())
			{
				// that's a huge element.
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
		final View view = adapterView.getChildAt(childAt);

		// Occasionally we'll have to fight with smooth scrolling logic on our definition of when
		// there is extra scrolling to be done. In particular if the element is the first or last
		// element of the list, the smooth scroller may decide that no work needs to be done to scroll
		// to the element if a certain percentage of it is on screen. Ugh. Sigh. Yuck.

		return ViewMatchers.isDisplayingAtLeast(FULLY_RENDERED_PERCENTAGE_CUTOFF).matches(view);
	}
}
