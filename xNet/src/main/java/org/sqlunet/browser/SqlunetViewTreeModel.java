/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import org.sqlunet.view.TreeOp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SqlunetViewTreeModel extends AndroidViewModel
{
	@FunctionalInterface
	public interface ToTreeOps
	{
		@NonNull
		TreeOp[] cursorToTreeOps(@NonNull final Cursor cursor);
	}

	private final MutableLiveData<TreeOp[]> data = new MutableLiveData<>();

	@NonNull
	public LiveData<TreeOp[]> getData()
	{
		return data;
	}

	public SqlunetViewTreeModel(@NonNull final Application application)
	{
		super(application);
	}

	@SuppressLint("StaticFieldLeak")
	public void loadData(@NonNull final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder, @NonNull final ToTreeOps treeConverter)
	{
		new AsyncTask<Void, Void, TreeOp[]>()
		{
			@Nullable
			@Override
			protected TreeOp[] doInBackground(Void... voids)
			{
				final Cursor cursor = getApplication().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
				return cursor == null ? null : treeConverter.cursorToTreeOps(cursor);
			}

			@Override
			protected void onPostExecute(TreeOp[] treeOps)
			{
				if (treeOps != null)
				{
					data.setValue(treeOps);
				}
			}
		}.execute();
	}
}
