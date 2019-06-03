/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.web;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

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
		new AsyncTask<Void, Void, String>()
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
