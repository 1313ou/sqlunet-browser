/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;

import org.sqlunet.browser.common.R;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

abstract public class BaseSelectorsListFragment extends LoggingFragment implements AdapterView.OnItemClickListener
{
	static private final String TAG = "SelectorsListF";

	/**
	 * Search query
	 */
	@Nullable
	protected String word;

	/**
	 * List view
	 */
	protected ListView listView;

	/**
	 * Cursor adapter
	 */
	protected CursorAdapter adapter;

	/**
	 * Cursor loader id
	 */
	@LayoutRes
	protected int layoutId;

	/**
	 * Activate on click flag
	 */
	protected boolean activateOnItemClick = true;

	// L I F E C Y C L E

	// --constructor--

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public BaseSelectorsListFragment()
	{
		super();
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		final View view = inflater.inflate(this.layoutId, container, false);

		// list adapter bound to the cursor
		Log.d(TAG, "Make adapter. Lifecycle: onCreateView()");
		this.adapter = makeAdapter();

		// list view
		this.listView = view.findViewById(android.R.id.list);

		// bind to adapter
		this.listView.setAdapter(this.adapter);
		this.listView.setChoiceMode(this.activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);
		this.listView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// data view models
		Log.d(TAG, "Make models. Lifecycle: onViewCreated()");
		makeModels();
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// load data
		Log.d(TAG, "Load data. Lifecycle: onStart()");
		load();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		CursorAdapter adapter = this.adapter;
		if (adapter != null)
		{
			Log.d(TAG, "Close cursor. Lifecycle: onDestroy()");
			adapter.changeCursor(null);
			Log.d(TAG, "Clear adapter. Lifecycle: onDestroy()");
			this.adapter = null;
		}
	}

	// A D A P T E R

	abstract protected CursorAdapter makeAdapter();

	/**
	 * Load data from word
	 */
	abstract protected void load();

	// V I E W M O D E L S

	/**
	 * Data view model
	 */
	protected SqlunetViewModel dataModel;

	/**
	 * Position view model
	 */
	protected PositionViewModel positionModel;

	/**
	 * View Model key
	 */
	protected String viewModelKey;

	/**
	 * Make view models
	 */
	protected void makeModels()
	{
		// data model
		this.dataModel = new ViewModelProvider(this).get(this.viewModelKey, SqlunetViewModel.class);
		this.dataModel.getData().observe(getViewLifecycleOwner(), getCursorObserver());

		// position model
		this.positionModel = new ViewModelProvider(this).get(PositionViewModel.class);
		this.positionModel.getPositionLiveData().observe(getViewLifecycleOwner(), getPositionObserver());
		this.positionModel.setPosition(AdapterView.INVALID_POSITION);
	}
	@NonNull
	protected Cursor augmentCursor(Cursor cursor) { return cursor; }

	// O B S E R V E R S

	protected Observer<Cursor> getCursorObserver()
	{
		return cursor -> {

			if (cursor == null || cursor.getCount() <= 0)
			{
				final String html = getString(R.string.error_entry_not_found, "<b>" + this.word + "</b>");
				final CharSequence message = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N ? Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY) : Html.fromHtml(html);
				// Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
				final View view = getView();
				assert view != null;
				Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
			}
			else
			{
				// pass on to list adapter
				assert this.adapter != null;
				this.adapter.changeCursor(augmentCursor(cursor));
			}
		};
	}

	protected Observer<Integer> getPositionObserver()
	{
		return position -> {

			Log.d(TAG, "Observed position change " + position);
			this.listView.setItemChecked(position, position != AdapterView.INVALID_POSITION);
		};
	}

	// C L I C K   L I S T E N E R

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
	 *
	 * @param activateOnItemClick true if activate
	 */
	@SuppressWarnings("WeakerAccess")
	public void setActivateOnItemClick(@SuppressWarnings("SameParameterValue") final boolean activateOnItemClick)
	{
		this.activateOnItemClick = activateOnItemClick;
	}

	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id)
	{
		Log.d(TAG, "Select " + position);
		activate(position);
	}

	abstract protected void activate(int position);
}
