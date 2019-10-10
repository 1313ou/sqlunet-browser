package org.sqlunet.browser;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import org.hamcrest.Matcher;

import androidx.annotation.NonNull;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import static androidx.test.espresso.Espresso.onView;

public class ContainerUtils
{
	/**
	 * Count of children views in the view group/container
	 *
	 * @param matcher matcher for parent
	 * @return the number of children in the group
	 */
	static public int getViewCount(@NonNull final Matcher<View> matcher)
	{
		final int[] count = {0}; // has to be final
		onView(matcher).perform(new ViewAction()
		{
			@NonNull
			@Override
			public Matcher<View> getConstraints()
			{
				return matcher;
			}

			@NonNull
			@Override
			public String getDescription()
			{
				return "getting child view count";
			}

			@Override
			public void perform(UiController uiController, View view)
			{
				AdapterView<?> adapterView = (AdapterView<?>) view;
				count[0] = adapterView.getChildCount();
			}
		});
		return count[0];
	}

	/**
	 * Count of items in the data set represented by this adapter
	 *
	 * @param matcher matcher for adapter view
	 * @return how many items are in the data set represented by this adapter
	 */
	static public int getItemCount(@NonNull final Matcher<View> matcher)
	{
		final int[] count = {0}; // has to be final
		onView(matcher).perform(new ViewAction()
		{
			@NonNull
			@Override
			public Matcher<View> getConstraints()
			{
				return matcher;
			}

			@NonNull
			@Override
			public String getDescription()
			{
				return "getting item count";
			}

			@Override
			public void perform(UiController uiController, View view)
			{
				AdapterView<?> adapterView = (AdapterView<?>) view;
				count[0] = adapterView.getAdapter().getCount();
			}
		});
		return count[0];
	}

	/**
	 * Get counts per groups for ExpandableListView
	 *
	 * @param matcher matcher for ExpandableListView
	 * @return array of counts per group
	 */
	static public int[] getExpandableListViewItemCounts(@NonNull final Matcher<View> matcher)
	{
		final int[][] result = {new int[0]}; // has to be final
		onView(matcher).perform(new ViewAction()
		{
			@NonNull
			@Override
			public Matcher<View> getConstraints()
			{
				return matcher;
			}

			@NonNull
			@Override
			public String getDescription()
			{
				return "getting ELV group item counts";
			}

			@Override
			public void perform(UiController uiController, View view)
			{
				ExpandableListView expandableListView = (ExpandableListView) view;
				ExpandableListAdapter adapter = expandableListView.getExpandableListAdapter();
				int g = adapter.getGroupCount();
				int[] counts = new int[g];
				for (int i = 0; i < g; i++)
				{
					counts[i] = adapter.getChildrenCount(i);
				}
				result[0] = counts;
			}
		});
		return result[0];
	}
}