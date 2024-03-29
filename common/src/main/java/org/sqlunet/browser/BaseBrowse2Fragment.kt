/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.sqlunet.browser.common.R
import org.sqlunet.settings.Settings
import org.sqlunet.settings.Settings.DetailViewMode

/**
 * A fragment representing a detail
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class BaseBrowse2Fragment : Fragment() {

    @JvmField
    protected var pointer: Parcelable? = null

    @JvmField
    protected var word: String? = null

    @JvmField
    protected var cased: String? = null

    @JvmField
    protected var pronunciation: String? = null

    @JvmField
    protected var pos: String? = null

    @JvmField
    protected var layoutId = R.layout.fragment_browse2_multi

    @JvmField
    protected var targetView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            Log.d(TAG, "restore instance state $this")
            pointer = getParcelable(savedInstanceState, POINTER_STATE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mode = Settings.getDetailViewModePref(requireContext())
        return when (mode) {
            DetailViewMode.VIEW -> inflater.inflate(layoutId, container, false)
            DetailViewMode.WEB -> inflater.inflate(R.layout.fragment_browse2, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        targetView = view.findViewById(R.id.target)
        requireActivity().invalidateOptionsMenu()
    }

    override fun onStart() {
        super.onStart()
        if (pointer != null) {
            search()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(POINTER_STATE, pointer)
    }

    /**
     * Search
     *
     * @param pointer       pointer
     * @param word          word
     * @param cased         cased
     * @param pronunciation pronunciation
     * @param pos           pos
     */
    fun search(pointer: Parcelable?, word: String?, cased: String?, pronunciation: String?, pos: String?) {
        this.pointer = pointer
        this.word = word
        this.cased = cased
        this.pronunciation = pronunciation
        this.pos = pos
        search()
    }

    /**
     * Search
     */
    protected abstract fun search()

    companion object {

        private const val TAG = "Browse2F"
        const val FRAGMENT_TAG = "browse2"
        private const val POINTER_STATE = "pointer"
    }
}
