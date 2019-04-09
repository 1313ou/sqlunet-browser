package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SqlunetViewModel extends AndroidViewModel
{
	public interface PostProcessor
	{
		void postProcess(final Cursor cursor);
	}

	private final MutableLiveData<Cursor> data = new MutableLiveData<>();

	public LiveData<Cursor> getData()
	{
		return data;
	}

	public SqlunetViewModel(final Application application)
	{
		super(application);
	}

	@SuppressLint("StaticFieldLeak")
	public void loadData(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder, final PostProcessor postProcessor)
	{
		new AsyncTask<Void, Void, Cursor>()
		{
			@Override
			protected Cursor doInBackground(Void... voids)
			{
				final Cursor cursor = getApplication().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
				if (postProcessor != null)
				{
					postProcessor.postProcess(cursor);
				}
				return cursor;
			}

			@Override
			protected void onPostExecute(Cursor cursor)
			{
				data.setValue(cursor);
			}
		}.execute();
	}
}
