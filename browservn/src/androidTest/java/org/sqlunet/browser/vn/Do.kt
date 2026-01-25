/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.vn

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
import org.sqlunet.browser.ContainerUtils
import org.sqlunet.browser.DataUtils
import org.sqlunet.browser.Seq
import org.sqlunet.browser.ToBoolean
import org.sqlunet.browser.WaitUntil
import org.sqlunet.browser.WaitUntilText
import com.bbou.download.common.R as DownloadR
import org.sqlunet.browser.common.R as CommonR

internal object Do {

    fun ensureDownloaded(): Boolean {
        val notMain =
            ToBoolean.testAssertion(ViewMatchers.withId(CommonR.id.drawer_layout), ViewAssertions.doesNotExist()) || !ToBoolean.test(ViewMatchers.withId(CommonR.id.drawer_layout), ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
        if (notMain) {
            download()
            return true
        }
        return false
    }

    private fun download() {
        Seq.doClick(R.id.databaseButton)
        // download activity
        Seq.doClick(DownloadR.id.downloadButton)
        //Wait.until_not_text(R.id.status, Seq.getResourceString(R.string.status_task_running), 10)
        WaitUntilText.changesFrom(CommonR.id.status, Seq.getResourceString(CommonR.string.status_task_running))
    }

    fun ensureTextSearchSetup(@IdRes buttonId: Int) {
        val notSet = ToBoolean.testAssertion(ViewMatchers.withId(buttonId), ViewAssertions.doesNotExist()) || ToBoolean.test(ViewMatchers.withId(buttonId), ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
        if (notSet) {
            textSearchSetup(buttonId)
            Seq.doPressBack()
        }
    }

    private fun textSearchSetup(@IdRes buttonId: Int) {
        Seq.doClick(buttonId)
        Seq.doClick(CommonR.id.task_run)
        //Wait.until_not_text(R.id.task_status, Seq.getResourceString(R.string.status_task_running), 10)
        WaitUntilText.changesFrom(CommonR.id.task_status, Seq.getResourceString(CommonR.string.status_task_running))
    }

    fun searchRunTree() {
        for (word in DataUtils.wordList!!) {
            Seq.doTypeSearch(R.id.search, word)
            // selector list
            // Wait.until(android.R.id.list, 5)
            WaitUntil.shown(android.R.id.list)
            val list = CoreMatchers.allOf(ViewMatchers.withId(android.R.id.list), CoreMatchers.instanceOf(ListView::class.java))
            Espresso.onView(list).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
            // close first
            Espresso.onView(ViewMatchers.withChild(CoreMatchers.allOf(ViewMatchers.withId(R.id.xn), CoreMatchers.instanceOf(TextView::class.java), ViewMatchers.withText("verbnet"))))
                .perform(ViewActions.click())
            // expand all
            Espresso.onView(ViewMatchers.withChild(CoreMatchers.allOf(ViewMatchers.withId(R.id.xn), CoreMatchers.instanceOf(TextView::class.java), ViewMatchers.withText("propbank"))))
                .perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withChild(CoreMatchers.allOf(ViewMatchers.withId(R.id.xn), CoreMatchers.instanceOf(TextView::class.java), ViewMatchers.withText("verbnet"))))
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
                    Seq.doPressBack()
                }
            }
        }
    }

    fun textSearchRun(position: Int) {
        Seq.doChoose(CommonR.id.spinner, position)
        for (word in DataUtils.wordList!!) {
            Seq.doTypeSearch(R.id.search, word)
        }
    }
}