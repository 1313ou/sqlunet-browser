/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.sqlunet.browser.common.R

/**
 * Base task fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class BaseTaskFragment : Fragment() {

    /**
     * Action spinner
     */
    lateinit var spinner: Spinner

    /**
     * Status view
     */
    lateinit var status: TextView

    /**
     * Run button
     */
    lateinit var runButton: Button

    /**
     * Layout id set by derived class
     */
    var layoutId = 0

    /**
     * Make spinner
     *
     * @return spinner adapter
     */
    protected abstract fun makeAdapter(): SpinnerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // task spinner
        spinner = view.findViewById(R.id.task_spinner)

        // adapter
        val adapter = makeAdapter()

        // apply the adapter to the spinner
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view0: View, position: Int, id: Long) {
                select(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                select(-1)
            }
        }

        // task status view
        status = view.findViewById(R.id.task_status)

        // task run button
        runButton = view.findViewById(R.id.task_run)
    }

    open fun select(position: Int) {
        status.text = ""
    }
}
