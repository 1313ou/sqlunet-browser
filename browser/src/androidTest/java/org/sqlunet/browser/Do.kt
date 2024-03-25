/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.util.Log
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.AdapterViewProtocols
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.CoreMatchers
import java.util.Objects

internal object Do {

    @JvmStatic
    fun ensureDownloaded(): Boolean {
        val notMain = ToBoolean.testAssertion(ViewMatchers.withId(R.id.drawer_layout), ViewAssertions.doesNotExist()) || !ToBoolean.test(ViewMatchers.withId(R.id.drawer_layout), ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
        if (notMain) {
            download()
            return true
        }
        return false
    }

    private fun download() {
        Seq.do_click(R.id.databaseButton)
        // download activity
        Seq.do_click(R.id.downloadButton)
        //Wait.until_not_text(R.id.status, Seq.getResourceString(R.string.status_task_running), 10);
        WaitUntilText.changesFrom(R.id.status, Seq.getResourceString(R.string.status_task_running))
    }

    @JvmStatic
    fun ensureTextSearchSetup(@IdRes buttonId: Int) {
        val notSet = ToBoolean.testAssertion(ViewMatchers.withId(buttonId), ViewAssertions.doesNotExist()) || ToBoolean.test(ViewMatchers.withId(buttonId), ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
        if (notSet) {
            textSearchSetup(buttonId)
            Seq.do_pressBack()
        }
    }

    private fun textSearchSetup(@IdRes buttonId: Int) {
        Seq.do_click(buttonId)
        Seq.do_click(R.id.task_run)
        //Wait.until_not_text(R.id.task_status, Seq.getResourceString(R.string.status_task_running), 10);
        WaitUntilText.changesFrom(R.id.task_status, Seq.getResourceString(R.string.status_task_running))
    }

    @JvmStatic
    fun searchRunFlat() {
        for (word in Objects.requireNonNull(DataUtils.getWordList())) {
            Seq.do_typeSearch(R.id.search, word)

            // selector list
            //Wait.until(android.R.id.list, 5);
            WaitUntil.shown(android.R.id.list)
            val list = CoreMatchers.allOf(ViewMatchers.withId(android.R.id.list), CoreMatchers.instanceOf(ListView::class.java))
            Espresso.onView(list).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

            // for all selectors
            val n = ContainerUtils.getItemCount(list)
            Log.d("Searching ", "$word has $n results")
            for (i in 0 until n) {
                Espresso.onData(CoreMatchers.anything()) 
                    .inAdapterView(list) 
                    .atPosition(i) 
                    .perform( 
                        ViewActions.click() 
                    )
                Seq.do_pressBack()
            }
        }
    }

    @JvmStatic
    fun searchRunTree() {
        for (word in Objects.requireNonNull(DataUtils.getWordList())) {
            Seq.do_typeSearch(R.id.search, word)

            // selector list
            //Wait.until(android.R.id.list, 5);
            WaitUntil.shown(android.R.id.list)
            val list = CoreMatchers.allOf(ViewMatchers.withId(android.R.id.list), CoreMatchers.instanceOf(ListView::class.java))
            Espresso.onView(list).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

            // close first
            Espresso.onView(ViewMatchers.withChild(CoreMatchers.allOf(ViewMatchers.withId(R.id.xn), CoreMatchers.instanceOf(TextView::class.java), ViewMatchers.withText("wordnet")))) 
                .perform(ViewActions.click())
            // expand all
            Espresso.onView(ViewMatchers.withChild(CoreMatchers.allOf(ViewMatchers.withId(R.id.xn), CoreMatchers.instanceOf(TextView::class.java), ViewMatchers.withText("framenet")))) 
                .perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withChild(CoreMatchers.allOf(ViewMatchers.withId(R.id.xn), CoreMatchers.instanceOf(TextView::class.java), ViewMatchers.withText("propbank")))) 
                .perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withChild(CoreMatchers.allOf(ViewMatchers.withId(R.id.xn), CoreMatchers.instanceOf(TextView::class.java), ViewMatchers.withText("verbnet")))) 
                .perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withChild(CoreMatchers.allOf(ViewMatchers.withId(R.id.xn), CoreMatchers.instanceOf(TextView::class.java), ViewMatchers.withText("wordnet")))) 
                .perform(ViewActions.click())

            // for all selectors
            val counts = ContainerUtils.getExpandableListViewItemCounts(list)
            Log.d("Searching ", word + ' ' + DataUtils.arrayToString(*counts))
            var k = 0
            for (g in counts.indices) {
                k++ // for group header
                for (c in 0 until counts[g]) {
                    Log.d("xselector (", (g + ' '.code + c).toString() + ") = " + k)
                    Espresso.onData(CoreMatchers.anything()) 
                        .inAdapterView(list) 
                        .atPosition(k++) 
                        .usingAdapterViewProtocol(AdapterViewProtocols.standardProtocol()).perform(
                            ViewActions.click() 
                        )
                    Seq.do_pressBack()
                }
            }
        }
    }

    @JvmStatic
    fun xselectorsRunTree() {
        for (word in Objects.requireNonNull(DataUtils.getWordList())) {
            Seq.do_typeSearch(R.id.search, word!!)

            // selector list
            //Wait.until(android.R.id.list, 5);
            WaitUntil.shown(android.R.id.list)
            val list = CoreMatchers.allOf(ViewMatchers.withId(android.R.id.list), CoreMatchers.instanceOf(ListView::class.java))
            Espresso.onView(list).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
            Espresso.onView(ViewMatchers.withChild(CoreMatchers.allOf(ViewMatchers.withId(R.id.xn), CoreMatchers.instanceOf(TextView::class.java), ViewMatchers.withText("wordnet")))) 
                .perform(ViewActions.click())
            for (i in 0..49) {
                for (section in arrayOf("framenet", "propbank", "verbnet", "wordnet")) {
                    Espresso.onView(ViewMatchers.withChild(CoreMatchers.allOf(ViewMatchers.withId(R.id.xn), CoreMatchers.instanceOf(TextView::class.java), ViewMatchers.withText(section)))) 
                        .perform(ViewActions.click())
                    Espresso.onView(ViewMatchers.withChild(CoreMatchers.allOf(ViewMatchers.withId(R.id.xn), CoreMatchers.instanceOf(TextView::class.java), ViewMatchers.withText(section)))) 
                        .perform(ViewActions.click())
                }
            }
        }
    }

    @JvmStatic
    fun textSearchRun(position: Int) {
        Seq.do_choose(R.id.spinner, position)
        for (word in Objects.requireNonNull(DataUtils.getWordList())) {
            Seq.do_typeSearch(R.id.search, word!!)
        }
    }
}