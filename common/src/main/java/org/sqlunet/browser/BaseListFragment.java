/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;

import org.sqlunet.browser.common.R;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.sql.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

/**
 * A list fragment representing a table.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public abstract class BaseListFragment extends ListFragment
{
	static private final String TAG = "BaseListF";

	static private final boolean VERBOSE = true;

	/**
	 * View model
	 */
	private SqlunetViewModel model;

	/**
	 * View binder factory
	 *
	 * @return view binder
	 */
	@Nullable
	abstract protected ViewBinder makeViewBinder();

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// query
		if (VERBOSE)
		{
			// args
			final Bundle args = getArguments();
			assert args != null;

			final String queryArg = args.getString(ProviderArgs.ARG_QUERYARG);
			final String uriString = args.getString(ProviderArgs.ARG_QUERYURI);
			final String selection = args.getString(ProviderArgs.ARG_QUERYFILTER);
			Log.d(TAG, String.format("%s (filter: %s)(arg=%s)", uriString, selection, queryArg));
		}
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_table, container, false);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// models
		makeModels(); // sets cursor

		// args
		final Bundle args = getArguments();
		assert args != null;

		// query params
		final String uriString = args.getString(ProviderArgs.ARG_QUERYURI);
		final Uri uri = Uri.parse(uriString);
		final String id = args.getString(ProviderArgs.ARG_QUERYID);
		final String[] items = args.getStringArray(ProviderArgs.ARG_QUERYITEMS);
		final String[] hiddenItems = args.getStringArray(ProviderArgs.ARG_QUERYHIDDENITEMS);
		final String sortOrder = args.getString(ProviderArgs.ARG_QUERYSORT);
		final String selection = args.getString(ProviderArgs.ARG_QUERYFILTER);
		final String queryArg = args.getString(ProviderArgs.ARG_QUERYARG);
		final int layoutId = args.getInt(ProviderArgs.ARG_QUERYLAYOUT);
		//final String database = args.getString(ProviderArgs.ARG_QUERYDATABASE);

		// adapter set up
		// from (database column names)
		final List<String> fromList = new ArrayList<>();
		if (items != null)
		{
			for (final String item : items)
			{
				String col = item;

				// remove alias
				final int asIndex = col.lastIndexOf(" AS ");
				if (asIndex != -1)
				{
					col = col.substring(asIndex + 4);
				}
				fromList.add(col);
			}
		}

		final String[] from = fromList.toArray(new String[0]);
		Log.d(TAG + " From", Utils.join(from));

		// to (view ids)
		final Collection<Integer> toList = new ArrayList<>();
		int nItems = items == null ? 0 : items.length;
		int nXItems = hiddenItems == null ? 0 : hiddenItems.length;
		final int[] resIds = {R.id.item0, R.id.item1, R.id.item2};
		for (int i = 0; i < (nItems + nXItems) && i < resIds.length; i++)
		{
			toList.add(resIds[i]);
		}

		final int[] to = new int[toList.size()];
		int i = 0;
		for (final Integer n : toList)
		{
			to[i++] = n;
		}
		Log.d(TAG + " To", "" + Utils.join(to));

		// make cursor adapter
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(requireContext(), layoutId, null, //
				from, //
				to, 0);
		adapter.setViewBinder(makeViewBinder());
		setListAdapter(adapter);

		// load the contents

		// make projection
		final List<String> cols = new ArrayList<>();

		// add _id alias for first column
		cols.add(id + " AS _id");

		// add items
		if (items != null)
		{
			Collections.addAll(cols, items);
		}

		// add hidden items
		if (hiddenItems != null)
		{
			Collections.addAll(cols, hiddenItems);
		}

		final String[] projection = cols.toArray(new String[0]);
		// for (String p : projection)
		// {
		//  Log.d(TAG, p);
		// }
		final String[] selectionArgs = queryArg == null ? null : new String[]{queryArg};

		this.model.loadData(uri, projection, selection, selectionArgs, sortOrder, null);
	}

	@Override
	public void onStop()
	{
		super.onStop();

		final ListView listView = getListView();
		final CursorAdapter adapter = (CursorAdapter) getListAdapter();

		Log.d(TAG, "Nullify listview adapter. Lifecycle: onStop()");
		listView.setAdapter(null);
		// the cursor will be saved along with fragment state if any
		Log.d(TAG, "Nullify adapter cursor but do not close cursor. Lifecycle: onStop()");
		assert adapter != null;
		//noinspection resource
		adapter.swapCursor(null);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		final CursorAdapter adapter = (CursorAdapter) getListAdapter();
		if (adapter != null)
		{
			adapter.changeCursor(null);
		}
	}

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		this.model = new ViewModelProvider(this).get("elements", SqlunetViewModel.class);
		this.model.getData().observe(getViewLifecycleOwner(), cursor -> {

			final CursorAdapter adapter = (CursorAdapter) getListAdapter();
			assert adapter != null;
			adapter.changeCursor(cursor);
		});
	}

	/**
	 * Dump cursor
	 *
	 * @param cursor cursor
	 */
	void dump(@Nullable final Cursor cursor)
	{
		if (cursor == null)
		{
			Log.d(TAG, "null cursor");
			return;
		}

		// column names
		int n = cursor.getColumnCount();
		String[] cols = cursor.getColumnNames();
		int position = cursor.getPosition();
		if (cursor.moveToFirst())
		{
			do
			{
				// all columns in row
				for (int i = 0; i < n; i++)
				{
					String val;
					switch (cursor.getType(i))
					{
						case Cursor.FIELD_TYPE_NULL:
							val = "null";
							break;
						case Cursor.FIELD_TYPE_INTEGER:
							val = Integer.toString(cursor.getInt(i));
							break;
						case Cursor.FIELD_TYPE_FLOAT:
							val = Float.toString(cursor.getFloat(i));
							break;
						case Cursor.FIELD_TYPE_STRING:
							val = cursor.getString(i);
							break;
						case Cursor.FIELD_TYPE_BLOB:
							val = "blob";
							break;
						default:
							val = "NA";
							break;
					}
					Log.d(TAG, "column " + i + " " + cols[i] + "=" + val);
				}
			}
			while (cursor.moveToNext());

			// reset
			cursor.moveToPosition(position);
		}
	}
}