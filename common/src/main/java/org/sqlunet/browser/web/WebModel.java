/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.web;

import com.bbou.concurrency.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

	public void loadData(@NonNull final DocumentStringLoader loader)
	{
		new Task<Void, Void, String>()
		{
			@Nullable
			@Override
			public String doJob(Void ignored)
			{
				return loader.getDoc();
			}

			@Override
			public void onDone(final String treeNode)
			{
				data.setValue(treeNode);
			}

		}.execute(null);
	}
}
