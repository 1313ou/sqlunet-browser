package org.sqlunet.browser;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import org.sqlunet.browser.common.R;
import org.sqlunet.provider.XSqlUNetContract.Sources;
import org.sqlunet.provider.XSqlUNetProvider;

/**
 * A list fragment representing sources.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SourceFragment extends ListFragment
{
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		final String[] from = {Sources.NAME, Sources.VERSION, Sources.URL, Sources.PROVIDER, Sources.REFERENCE};
		final int[] to = {R.id.name, R.id.version, R.id.url, R.id.provider, R.id.reference};

		// make cursor adapter
		final Context context = getActivity();
		assert context != null;
		final ListAdapter adapter = new SimpleCursorAdapter(context, R.layout.item_source, null, //
				from, //
				to, 0);
		setListAdapter(adapter);

		// load the contents
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderManager.LoaderCallbacks<Cursor>()
		{
			@Nullable
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(Sources.CONTENT_URI_TABLE));
				final String[] projection = {Sources.ID + " AS _id", Sources.NAME, Sources.VERSION, Sources.URL, Sources.PROVIDER, Sources.REFERENCE};
				final String[] selectionArgs = null;
				final String selection = null;
				final String sort = Sources.ID;
				return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sort);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				((CursorAdapter) getListAdapter()).swapCursor(cursor);
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				((CursorAdapter) getListAdapter()).swapCursor(null);
			}
		});
	}
}
