/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Logging fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class LoggingFragment : Fragment() {

    init {
        Log.d(TAG, "Constructor (0) ${this::class}")
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach() (1) $this")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate() (2) $this from $savedInstanceState")
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView() (3) $this from $savedInstanceState")
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated() (4) $this from $savedInstanceState")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewStateRestored() (5) $this")
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onStart() {
        Log.d(TAG, "onStart() (6) $this")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "onResume() (7) $this")
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() (-6) $this")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() (-5) $this")
    }

    /**
     * This method may be called at any time before onDestroy.
     * There are many situations where a fragment may be mostly torn down (such as when placed on the back stack with no UI showing),
     * but its state will not be saved until its owning activity actually needs to save its state.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState() (-4) $this")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView() (-3) $this")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() (-2) $this")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach() (-1) $this")
    }

    companion object {

        private const val TAG = "Lifecycle"
    }
}
