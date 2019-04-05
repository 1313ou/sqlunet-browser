package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

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

	private final MutableLiveData<Cursor> data = new MutableLiveData<>();

	public LiveData<Cursor> getData()
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
	public void loadData()
	{
		new AsyncTask<Void, Void, Cursor>()
		{
			@Override
			protected Cursor doInBackground(Void... voids)
			{
				return getApplication().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			protected void onPostExecute(Cursor cursor)
			{
				data.setValue(cursor);
			}

		}.execute();
	}
}
