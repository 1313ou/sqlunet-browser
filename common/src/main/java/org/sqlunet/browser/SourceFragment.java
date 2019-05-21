/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.Context;
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
	/**
	 * View model
	 */
	private SqlunetViewModel model;

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
	}

	@Override
	public void onAttach(@NonNull final Context context)
	{
		super.onAttach(context);
		makeModels();
	}

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		this.model = ViewModelProviders.of(this).get("sources", SqlunetViewModel.class);
		this.model.getData().observe(this, cursor -> {

			final CursorAdapter adapter = (CursorAdapter) getListAdapter();
			assert adapter != null;
			adapter.swapCursor(cursor);
		});
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// load the contents
		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(Sources.CONTENT_URI_TABLE));
		final String[] projection = {Sources.ID + " AS _id", Sources.NAME, Sources.VERSION, Sources.URL, Sources.PROVIDER, Sources.REFERENCE};
		final String sortOrder = Sources.ID;
		this.model.loadData(uri, projection, null, null, sortOrder, null);
	}
}
