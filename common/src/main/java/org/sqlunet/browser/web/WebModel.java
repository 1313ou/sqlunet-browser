/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.web;

import android.annotation.SuppressLint;

import org.sqlunet.concurrency.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

@SuppressWarnings("WeakerAccess")
public class WebModel extends ViewModel
{
	/**
	 * Live data
	 */
	private final MutableLiveData<String> data = new MutableLiveData<>();

	@NonNull
	public LiveData<String> getData()
	{
		return data;
	}

	public WebModel()
	{
		super();
	}

	@SuppressLint("StaticFieldLeak")
	public void loadData(@NonNull final DocumentStringLoader loader)
	{
		new Task<Void, Void, String>()
		{
			@Nullable
			@Override
			protected String job(Void... voids)
			{
				return loader.getDoc();
			}

			@Override
			protected void onJobComplete(String treeNode)
			{
				data.setValue(treeNode);
			}
		}.run();
	}
}
