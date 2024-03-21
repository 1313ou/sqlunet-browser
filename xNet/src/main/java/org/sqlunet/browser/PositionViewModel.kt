/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class PositionViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    fun setPosition(position: Int) {
        savedStateHandle["position"] = position
    }

    val positionLiveData: LiveData<Int>
        get() = savedStateHandle.getLiveData("position")
}
