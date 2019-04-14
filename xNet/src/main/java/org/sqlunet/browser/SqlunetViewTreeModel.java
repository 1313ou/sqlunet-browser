package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import org.sqlunet.treeview.model.TreeNode;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SqlunetViewTreeModel extends AndroidViewModel
{
	public interface ToTreeNodes
	{
		TreeNode[] cursorToTreeNodes(final Cursor cursor);
	}

	private final MutableLiveData<TreeNode[]> data = new MutableLiveData<>();

	public LiveData<TreeNode[]> getData()
	{
		return data;
	}

	public SqlunetViewTreeModel(final Application application)
	{
		super(application);
	}

	@SuppressLint("StaticFieldLeak")
	public void loadData(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder, final ToTreeNodes treeConverter)
	{
		new AsyncTask<Void, Void, TreeNode[]>()
		{
			@Override
			protected TreeNode[] doInBackground(Void... voids)
			{
				final Cursor cursor = getApplication().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
				return treeConverter.cursorToTreeNodes(cursor);
			}

			@Override
			protected void onPostExecute(TreeNode[] treeNode)
			{
				data.setValue(treeNode);
			}
		}.execute();
	}
}
