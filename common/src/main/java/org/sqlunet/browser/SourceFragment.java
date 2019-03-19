package org.sqlunet.browser;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
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
			@NonNull
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(Sources.CONTENT_URI_TABLE));
				final String[] projection = {Sources.ID + " AS _id", Sources.NAME, Sources.VERSION, Sources.URL, Sources.PROVIDER, Sources.REFERENCE};
				final String[] selectionArgs = null;
				final String selection = null;
				final String sort = Sources.ID;
				final Context context = getContext();
				assert context != null;
				return new CursorLoader(context, uri, projection, selection, selectionArgs, sort);
			}

			@Override
			public void onLoadFinished(@NonNull final Loader<Cursor> loader, final Cursor cursor)
			{
				((CursorAdapter) getListAdapter()).swapCursor(cursor);
			}

			@Override
			public void onLoaderReset(@NonNull final Loader<Cursor> loader)
			{
				((CursorAdapter) getListAdapter()).swapCursor(null);
			}
		});
	}
}
