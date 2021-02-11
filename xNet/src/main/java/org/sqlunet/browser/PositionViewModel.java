/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class PositionViewModel extends ViewModel
{
	private static final String TAG = "PositionViewModel";

	private final SavedStateHandle savedStateHandle;

	public PositionViewModel(SavedStateHandle savedStateHandle)
	{
		this.savedStateHandle = savedStateHandle;
	}

	public void setPosition(int position)
	{
		Log.d(TAG, "setPosition " + position);
		this.savedStateHandle.set("position", position);
	}

	public LiveData<Integer> getPositionLiveData()
	{
		return savedStateHandle.getLiveData("position");
	}
}
