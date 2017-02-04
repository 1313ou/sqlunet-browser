package org.sqlunet.browser;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.sql.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A list fragment representing a table.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public abstract class AbstractTableFragment extends ListFragment
{
	static private final String TAG = "AbstractTableFrag";

	static private final boolean VERBOSE = false;

	/**
	 * View binder factory
	 *
	 * @return view binder
	 */
	abstract protected ViewBinder makeViewBinder();

	@SuppressWarnings("boxing")
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// args
		final Bundle args = getArguments();

		// query params
		final String uriString = args.getString(ProviderArgs.ARG_QUERYURI);
		final Uri uri = Uri.parse(uriString);
		final String id = args.getString(ProviderArgs.ARG_QUERYID);
		final String[] items = args.getStringArray(ProviderArgs.ARG_QUERYITEMS);
		final String[] hiddenItems = args.getStringArray(ProviderArgs.ARG_QUERYHIDDENITEMS);
		final String sort = args.getString(ProviderArgs.ARG_QUERYSORT);
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
		Log.d(AbstractTableFragment.TAG + "From", Utils.join(from));

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
		Log.d(AbstractTableFragment.TAG + "To", Utils.join(to));

		// make cursor adapter
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), layoutId, null, //
				from, //
				to, 0);
		adapter.setViewBinder(makeViewBinder());
		setListAdapter(adapter);

		// load the contents
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				// make projection
				final List<String> cols = new ArrayList<>();

				// addItem _id alias for first column
				cols.add(id + " AS _id");

				// addItem items
				if (items != null)
				{
					Collections.addAll(cols, items);
				}

				// addItem hidden items
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
				return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sort);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				// dump(cursor);

				if (cursor == null)
				{
					Toast.makeText(getActivity(), R.string.status_provider_query_failed, Toast.LENGTH_LONG).show();
				}

				((CursorAdapter) getListAdapter()).swapCursor(cursor);
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				((CursorAdapter) getListAdapter()).swapCursor(null);
			}
		});
	}

	// L A Y O U T

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(R.layout.fragment_table, container, false);

		// query view
		final TextView queryView = (TextView) view.findViewById(R.id.targetView);

		// args
		final Bundle args = getArguments();

		// query
		final String queryArg = args.getString(ProviderArgs.ARG_QUERYARG);
		if (VERBOSE)
		{
			final String uriString = args.getString(ProviderArgs.ARG_QUERYURI);
			final String selection = args.getString(ProviderArgs.ARG_QUERYFILTER);
			queryView.setText(String.format("%s (filter: %s)(arg=%s)", uriString, selection, queryArg));
		}
		else
		{
			queryView.setText(queryArg);
		}

		return view;
	}

	/**
	 * Dump cursor
	 *
	 * @param cursor cursor
	 */
	@SuppressWarnings("unused")
	void dump(final Cursor cursor)
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