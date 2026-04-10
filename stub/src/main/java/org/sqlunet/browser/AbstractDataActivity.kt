/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.bbou.capture.Capture.captureAndSave
import com.bbou.capture.Capture.captureAndShare
import com.bbou.capture.Capture.getBackgroundFromTheme
import com.bbou.capture.Captured.capturedView
import org.sqlunet.browser.stub.R
import org.sqlunet.core.R as CoreR

/**
 * Abstract activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class AbstractDataActivity : BaseActivity() {

    protected abstract val layoutId: Int
    protected abstract val containerId: Int
    protected abstract fun makeFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // content
        setContentView(layoutId)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // set up the action bar
        val actionBar = supportActionBar!!
        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE

        // fragment
        // savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity (e.g. when rotating the screen from
        // portrait to landscape). In this case, the fragment will automatically be re-added to its container so we don't need to manually addItem it.
        // @see http://developer.android.com/guide/components/fragments.html
        if (savedInstanceState == null) {
            // create the sense fragment, transmit intent's extras as parameters and addItem it to the activity using a fragment transaction
            val fragment = makeFragment()
            fragment.setArguments(intent.extras)
            supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(containerId, fragment)
                // .addToBackStack(fragment.getTag() == null ? "tagless" : fragment.getTag())
                .commit()
        }
    }

    // M E N U

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(CoreR.menu.activity_theme, menu)
        menuInflater.inflate(R.menu.activity_capture, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            CoreR.id.action_theme_system -> {
                NightMode.switchToMode(this, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                 true
            }

            CoreR.id.action_theme_night -> {
                NightMode.switchToMode(this, AppCompatDelegate.MODE_NIGHT_YES)
                 true
            }

            CoreR.id.action_theme_day -> {
                NightMode.switchToMode(this, AppCompatDelegate.MODE_NIGHT_NO)
                 true
            }

            R.id.action_capture -> {
                val view = capturedView(this)
                if (view != null) {
                    val bg: Int = getBackgroundFromTheme(this)
                    captureAndSave(view, this, backGround = bg)
                }
                 true
            }

            R.id.action_share_capture -> {
                val view = capturedView(this)
                if (view != null) {
                    val bg: Int = getBackgroundFromTheme(this)
                    captureAndShare(view, this, backGround = bg)
                }
                 true
            }

            else ->  false
        }
    }
}