package org.sqlunet.browser.web;

import android.annotation.SuppressLint;
import android.app.Application;
import android.database.Cursor;
import android.os.AsyncTask;

import org.w3c.dom.Document;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WebModel extends ViewModel
{
	/**
	 * Live data
	 */
	private final MutableLiveData<String> data = new MutableLiveData<>();

	public LiveData<String> getData()
	{
		return data;
	}

	public WebModel()
	{
		super();
	}

	@SuppressLint("StaticFieldLeak")
	public void loadData(final DocumentStringLoader loader)
	{
		new AsyncTask<Void, Void, String>()
		{
			@Override
			protected String doInBackground(Void... voids)
			{
				String doc = loader.loadInBackground();
				return doc;
			}

			@Override
			protected void onPostExecute(String treeNode)
			{
				data.setValue(treeNode);
			}
		}.execute();
	}
}
