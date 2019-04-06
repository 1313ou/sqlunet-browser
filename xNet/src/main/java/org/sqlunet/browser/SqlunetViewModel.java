package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import java.util.AbstractMap.SimpleEntry;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SqlunetViewModel extends AndroidViewModel
{
	private Uri uri;

	private String[] projection;

	private String selection;

	private String[] selectionArgs;

	private String sortOrder;

	private final MutableLiveData<SimpleEntry<String, Cursor>> data = new MutableLiveData<>();

	public LiveData<SimpleEntry<String, Cursor>> getData()
	{
		return data;
	}

	public SqlunetViewModel(final Application application)
	{
		super(application);
	}

	public void set(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder)
	{
		this.uri = uri;
		this.projection = projection;
		this.selection = selection;
		this.selectionArgs = selectionArgs;
		this.sortOrder = sortOrder;
	}

	@SuppressLint("StaticFieldLeak")
	public void loadData(final String tag)
	{
		new AsyncTask<Void, Void, SimpleEntry<String, Cursor>>()
		{
			@Override
			protected SimpleEntry<String, Cursor> doInBackground(Void... voids)
			{
				final Cursor cursor = getApplication().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
				return new SimpleEntry<>(tag, cursor);
			}

			@Override
			protected void onPostExecute(SimpleEntry<String, Cursor> entry)
			{
				data.setValue(entry);
			}

		}.execute();
	}
}
