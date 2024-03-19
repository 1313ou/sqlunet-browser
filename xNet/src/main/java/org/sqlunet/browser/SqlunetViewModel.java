/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.bbou.concurrency.Task;

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
		/**
		 * @noinspection unused
		 */
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
		Log.d(TAG, "Loading data for " + uri);
		new Task<Void, Void, Cursor>()
		{
			@Override
			@Nullable
			public Cursor doJob(@Nullable final Void unused)
			{
				final Cursor cursor = getApplication().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
				Log.d(TAG, "Loaded data for " + uri + " yielded cursor " + cursor);
				if (postProcessor != null && cursor != null)
				{
					postProcessor.postProcess(cursor);
				}
				return cursor;
			}

			@Override
			public void onDone(@Nullable final Cursor cursor)
			{
				SqlunetViewModel.this.data.setValue(cursor);
			}
		}.execute(null);
	}

	public void loadData(@NonNull final Uri uri, @NonNull final Module.ContentProviderSql sql, @Nullable final PostProcessor postProcessor)
	{
		loadData(uri, sql.projection, sql.selection, sql.selectionArgs, sql.sortBy, postProcessor);
	}
}
