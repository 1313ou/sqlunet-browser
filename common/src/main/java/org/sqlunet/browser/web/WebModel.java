/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.web;

import android.annotation.SuppressLint;

import com.bbou.concurrency.Task;

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
	@SuppressWarnings("UnusedReturnValue")
	public void loadData(@NonNull final DocumentStringLoader loader)
	{
		new Task<Void, Void, String>()
		{
			@Nullable
			@Override
			protected String doInBackground(Void... voids)
			{
				return loader.getDoc();
			}

			@Override
			protected void onPostExecute(String treeNode)
			{
				data.setValue(treeNode);
			}
		}.execute();
	}
}
