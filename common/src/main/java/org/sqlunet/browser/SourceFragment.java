package org.sqlunet.browser;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import org.sqlunet.browser.common.R;
import org.sqlunet.provider.XSqlUNetContract.Sources;
import org.sqlunet.provider.XSqlUNetProvider;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProviders;

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
		final ListAdapter adapter = new SimpleCursorAdapter(requireContext(), R.layout.item_source, null, //
				from, //
				to, 0);
		setListAdapter(adapter);

		// load the contents
		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(Sources.CONTENT_URI_TABLE));
		final String[] projection = {Sources.ID + " AS _id", Sources.NAME, Sources.VERSION, Sources.URL, Sources.PROVIDER, Sources.REFERENCE};
		final String[] selectionArgs = null;
		final String selection = null;
		final String sortOrder = Sources.ID;

		final String tag = "sources";
		final SqlunetViewModel model = ViewModelProviders.of(this).get(tag, SqlunetViewModel.class);
		model.getData().observe(this, cursor -> {

			((CursorAdapter) getListAdapter()).swapCursor(cursor);
		});
		model.loadData(uri, projection, selection, selectionArgs, sortOrder, null);
	}

	private void sourcesToView(@NonNull final Cursor cursor)
	{

	}
}
