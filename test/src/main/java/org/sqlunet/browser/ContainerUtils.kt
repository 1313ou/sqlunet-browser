/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.view.View
import android.widget.AdapterView
import android.widget.ExpandableListView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import org.hamcrest.Matcher

object ContainerUtils {

    /**
     * Count of children views in the view group/container
     *
     * @param matcher matcher for parent
     * @return the number of children in the group
     */
    fun getViewCount(matcher: Matcher<View>): Int {
        val count = intArrayOf(0) // has to be final
        Espresso.onView(matcher).perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return matcher
            }

            override fun getDescription(): String {
                return "getting child view count"
            }

            override fun perform(uiController: UiController, view: View) {
                val adapterView = view as AdapterView<*>
                count[0] = adapterView.childCount
            }
        })
        return count[0]
    }

    /**
     * Count of items in the data set represented by this adapter
     *
     * @param matcher matcher for adapter view
     * @return how many items are in the data set represented by this adapter
     */
    fun getItemCount(matcher: Matcher<View>): Int {
        val count = intArrayOf(0) // has to be final
        Espresso.onView(matcher).perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return matcher
            }

            override fun getDescription(): String {
                return "getting item count"
            }

            override fun perform(uiController: UiController, view: View) {
                val adapterView = view as AdapterView<*>
                count[0] = adapterView.adapter.count
            }
        })
        return count[0]
    }

    /**
     * Get counts per groups for ExpandableListView
     *
     * @param matcher matcher for ExpandableListView
     * @return array of counts per group
     */
    fun getExpandableListViewItemCounts(matcher: Matcher<View>): IntArray {
        val result = arrayOf(IntArray(0)) // has to be final
        Espresso.onView(matcher).perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return matcher
            }

            override fun getDescription(): String {
                return "getting ELV group item counts"
            }

            override fun perform(uiController: UiController, view: View) {
                val expandableListView = view as ExpandableListView
                val adapter = expandableListView.expandableListAdapter
                val g = adapter.groupCount
                val counts = IntArray(g)
                for (i in 0 until g) {
                    counts[i] = adapter.getChildrenCount(i)
                }
                result[0] = counts
            }
        })
        return result[0]
    }
}