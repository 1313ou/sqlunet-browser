/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.app.Application;
import android.database.Cursor;
import android.net.Uri;

import com.bbou.concurrency.Task;

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

	@SuppressWarnings("UnusedReturnValue")
	@SuppressLint("StaticFieldLeak")
	public void loadData(@NonNull final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder, @NonNull final ToTreeOps treeConverter)
	{
		new Task<Void, Void, TreeOp[]>()
		{
			@Nullable
			@Override
			public TreeOp[] doJob(Void ingored)
			{
				final Cursor cursor = getApplication().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
				return cursor == null ? null : treeConverter.cursorToTreeOps(cursor);
			}

			@Override
			public void onDone(@Nullable TreeOp[] treeOps)
			{
				if (treeOps != null)
				{
					data.setValue(treeOps);
				}
			}
		}.execute(null);
	}

	public void loadData(@NonNull final Uri uri, @NonNull final Module.ContentProviderSql sql, @NonNull final ToTreeOps treeConverter)
	{
		loadData(uri, sql.projection, sql.selection, sql.selectionArgs, sql.sortBy, treeConverter);
	}
}
