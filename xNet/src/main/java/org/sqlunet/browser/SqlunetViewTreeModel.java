package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import org.sqlunet.view.TreeOp;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SqlunetViewTreeModel extends AndroidViewModel
{
	@FunctionalInterface
	public interface ToTreeOps
	{
		TreeOp[] cursorToTreeOps(final Cursor cursor);
	}

	private final MutableLiveData<TreeOp[]> data = new MutableLiveData<>();

	public LiveData<TreeOp[]> getData()
	{
		return data;
	}

	public SqlunetViewTreeModel(final Application application)
	{
		super(application);
	}

	@SuppressLint("StaticFieldLeak")
	public void loadData(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder, final ToTreeOps treeConverter)
	{
		new AsyncTask<Void, Void, TreeOp[]>()
		{
			@Override
			protected TreeOp[] doInBackground(Void... voids)
			{
				final Cursor cursor = getApplication().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
				return treeConverter.cursorToTreeOps(cursor);
			}

			@Override
			protected void onPostExecute(TreeOp[] treeOps)
			{
				data.setValue(treeOps);
			}
		}.execute();
	}
}
