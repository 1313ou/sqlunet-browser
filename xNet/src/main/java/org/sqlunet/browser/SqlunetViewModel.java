/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.concurrency.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SqlunetViewModel extends AndroidViewModel
{
	static private final String TAG = "SqlunetViewModel";

	@FunctionalInterface
	public interface PostProcessor
	{
		void postProcess(@NonNull final Cursor cursor);
	}

	private final MutableLiveData<Cursor> data = new MutableLiveData<>();

	@NonNull
	public LiveData<Cursor> getData()
	{
		return data;
	}

	@NonNull
	public MutableLiveData<Cursor> getMutableData()
	{
		return data;
	}

	public SqlunetViewModel(@NonNull final Application application)
	{
		super(application);
	}

	@SuppressLint("StaticFieldLeak")
	@SuppressWarnings("UnusedReturnValue")
	public void loadData(@NonNull final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder, @Nullable final PostProcessor postProcessor)
	{
		Log.d(TAG, "load data " + uri);
		new Task<Void, Void, Cursor>()
		{
			@Nullable
			@Override
			protected Cursor doInBackground(Void... voids)
			{
				final Cursor cursor = getApplication().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
				Log.d(TAG, "loaded data for " + uri + " yielded cursor " + cursor);
				if (postProcessor != null && cursor != null)
				{
					postProcessor.postProcess(cursor);
				}
				return cursor;
			}

			@Override
			protected void onPostExecute(Cursor cursor)
			{
				SqlunetViewModel.this.data.setValue(cursor);
			}
		}.execute();
	}

	public void loadData(@NonNull final Uri uri, @NonNull final Module.ContentProviderSql sql, @Nullable final PostProcessor postProcessor)
	{
		loadData(uri, sql.projection, sql.selection, sql.selectionArgs, sql.sortBy, postProcessor);
	}
}
