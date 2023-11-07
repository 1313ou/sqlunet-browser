/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import org.sqlunet.browser.common.R;
import org.sqlunet.provider.XNetContract.Sources;
import org.sqlunet.provider.XSqlUNetProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

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
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// make cursor adapter
		final String[] from = {Sources.NAME, Sources.VERSION, Sources.URL, Sources.PROVIDER, Sources.REFERENCE};
		final int[] to = {R.id.name, R.id.version, R.id.url, R.id.provider, R.id.reference};
		final ListAdapter adapter = new SimpleCursorAdapter(requireContext(), R.layout.item_source, null, //
				from, //
				to, 0);
		setListAdapter(adapter);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// models
		makeModels(); // sets cursor

		// load the contents
		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(Sources.URI));
		final String[] projection = {Sources.ID + " AS _id", Sources.NAME, Sources.VERSION, Sources.URL, Sources.PROVIDER, Sources.REFERENCE};
		final String sortOrder = Sources.ID;
		this.model.loadData(uri, projection, null, null, sortOrder, null);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		CursorAdapter adapter = (CursorAdapter) getListAdapter();
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
		this.model = new ViewModelProvider(this).get("sources", SqlunetViewModel.class);
		this.model.getData().observe(getViewLifecycleOwner(), cursor -> {

			final CursorAdapter adapter = (CursorAdapter) getListAdapter();
			assert adapter != null;
			adapter.changeCursor(cursor);
		});
	}
}
