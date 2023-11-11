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
	@Nullable
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

		return inflater.inflate(this.layoutId, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// list view
		this.listView = view.findViewById(android.R.id.list);

		// bind to adapter
		this.listView.setChoiceMode(this.activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);
		this.listView.setOnItemClickListener(this);

		// data view models
		Log.d(TAG, "Make models. Lifecycle: onViewCreated()");
		makeModels();
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// list adapter bound to view
		Log.d(TAG, "Make adapter. Lifecycle: onStart()");
		this.adapter = makeAdapter();
		Log.d(TAG, "Set listview adapter. Lifecycle: onStart()");
		this.listView.setAdapter(this.adapter);

		// load data
		Log.d(TAG, "Load data. Lifecycle: onStart()");
		load();
	}

	@Override
	public void onStop()
	{
		super.onStop();

		Log.d(TAG, "Nullify listview adapter. Lifecycle: onStop()");
		this.listView.setAdapter(null);
		// the cursor will be saved along with fragment state if any
		Log.d(TAG, "Nullify adapter cursor but do not close cursor. Lifecycle: onStop()");
		assert this.adapter != null;
		//noinspection resource
		this.adapter.swapCursor(null);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		if (this.adapter != null)
		{
			Log.d(TAG, "Close cursor.");
			this.adapter.changeCursor(null);
			Log.d(TAG, "Nullify adapter.");
			this.adapter = null;
		}
	}

	// A D A P T E R

	/**
	 * Load data from word
	 */
	abstract protected void load();

	abstract protected CursorAdapter makeAdapter();

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
	protected Cursor augmentCursor(@NonNull Cursor cursor)
	{
		return cursor;
	}

	// O B S E R V E R S

	@NonNull
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

	@NonNull
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
