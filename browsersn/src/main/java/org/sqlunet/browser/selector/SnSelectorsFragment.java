/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.selector;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.sqlunet.browser.R;
import org.sqlunet.browser.SqlunetViewModel;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.syntagnet.provider.SyntagNetContract;
import org.sqlunet.syntagnet.provider.SyntagNetContract.SnCollocations_X;
import org.sqlunet.syntagnet.provider.SyntagNetProvider;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

/**
 * Selector Fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SnSelectorsFragment extends ListFragment
{
	// static protected final String TAG = "SnSelectorsF";

	/**
	 * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
	 */
	@FunctionalInterface
	public interface Listener
	{
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemSelected(CollocationSelectorPointer pointer);
	}

	/**
	 * The serialization (saved instance state) Bundle key representing the activated item position. Only used on tablets.
	 */
	static private final String STATE_ACTIVATED_SELECTOR = "activated_selector";

	/**
	 * Activate on click flag: in two-pane mode, list items should be given the 'activated' state when touched.
	 */
	private boolean activateOnItemClick = false;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int activatedPosition = AdapterView.INVALID_POSITION;

	/**
	 * The fragment's current callback object, which is notified of list item clicks.
	 */
	private Listener listener;

	/**
	 * Search query
	 */
	@Nullable
	private String word;

	// View model

	private SqlunetViewModel model;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public SnSelectorsFragment()
	{
	}

	// C R E A T E

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// retain instance
		setRetainInstance(true);

		// arguments
		Bundle args = getArguments();
		assert args != null;

		// activate on click
		this.activateOnItemClick = args.getBoolean(Browse1Fragment.IS_TWO_PANE, false);

		// target word
		String query = args.getString(ProviderArgs.ARG_QUERYSTRING);
		if (query != null)
		{
			query = query.trim().toLowerCase(Locale.ENGLISH);
		}
		this.word = query;

		// list adapter, with no data
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(requireContext(), R.layout.item_snselector, null, //
				new String[]{ //
						SyntagNetContract.WORD1, //
						SyntagNetContract.WORD2, //
						//SnCollocations_X.WORD1ID, //
						//SnCollocations_X.WORD2ID, //
						SnCollocations_X.SYNSET1ID, //
						SnCollocations_X.SYNSET2ID, //
						SyntagNetContract.POS1, //
						SyntagNetContract.POS2, //
				}, //
				new int[]{ //
						R.id.word1, //
						R.id.word2, //
						//R.id.word1id, //
						//R.id.word2id, //
						R.id.synset1id, //
						R.id.synset2id, //
						R.id.pos1, //
						R.id.pos2, //
				}, 0);

		adapter.setViewBinder((view, cursor, columnIndex) -> {

			String text = cursor.getString(columnIndex);
			if (text == null)
			{
				view.setVisibility(View.GONE);
				return false;
			}
			else
			{
				view.setVisibility(View.VISIBLE);
			}

			if (view instanceof TextView)
			{
				((TextView) view).setText(text);
			}
			else if (view instanceof ImageView)
			{
				try
				{
					((ImageView) view).setImageResource(Integer.parseInt(text));
				}
				catch (@NonNull final NumberFormatException nfe)
				{
					((ImageView) view).setImageURI(Uri.parse(text));
				}
			}
			else
			{
				throw new IllegalStateException(view.getClass().getName() + " is not a view that can be bound by this SimpleCursorAdapter");
			}
			return false;
		});
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
		this.model = new ViewModelProvider(this).get("snselectors(word)", SqlunetViewModel.class);
		this.model.getData().observe(this, cursor -> {

			// pass on to list adapter
			final CursorAdapter adapter = (CursorAdapter) getListAdapter();
			assert adapter != null;
			adapter.swapCursor(cursor);

			// check
			/*
			if (SelectorsFragment.this.activatedPosition != AdapterView.INVALID_POSITION)
			{
				final ListView listView = getListView();
				listView.setItemChecked(SelectorsFragment.this.activatedPosition, true);
			}
			*/
		});
	}

	// V I E W

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_snselectors, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
		getListView().setChoiceMode(this.activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);

		// restore the previously serialized activated item position, if any
		if (savedInstanceState != null)
		{
			final int position = savedInstanceState.getInt(SnSelectorsFragment.STATE_ACTIVATED_SELECTOR, AdapterView.INVALID_POSITION);
			if (position == AdapterView.INVALID_POSITION)
			{
				getListView().setItemChecked(this.activatedPosition, false);
			}
			else
			{
				getListView().setItemChecked(position, true);
			}
			this.activatedPosition = position;
		}
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// run the contents (once activity is available)
		load();
	}

	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		super.onSaveInstanceState(outState);

		if (this.activatedPosition != AdapterView.INVALID_POSITION)
		{
			// serialize and persist the activated item position.
			outState.putInt(SnSelectorsFragment.STATE_ACTIVATED_SELECTOR, this.activatedPosition);
		}
	}

	// L I S T E N E R

	/**
	 * Set listener
	 *
	 * @param listener listener
	 */
	public void setListener(final Listener listener)
	{
		this.listener = listener;
	}

	// L O A D

	/**
	 * Load data from word
	 */
	private void load()
	{
		// load the contents
		final Uri uri = Uri.parse(SyntagNetProvider.makeUri(SnCollocations_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				SnCollocations_X.COLLOCATIONID + " AS _id", //
				SnCollocations_X.WORD1ID, //
				SnCollocations_X.WORD2ID, //
				SnCollocations_X.SYNSET1ID, //
				SnCollocations_X.SYNSET2ID, //
				SyntagNetContract.W1 + '.' + SnCollocations_X.LEMMA + " AS " + SyntagNetContract.WORD1, //
				SyntagNetContract.W2 + '.' + SnCollocations_X.LEMMA + " AS " + SyntagNetContract.WORD2, //
				SyntagNetContract.S1 + '.' + SnCollocations_X.POS + " AS " + SyntagNetContract.POS1, //
				SyntagNetContract.S2 + '.' + SnCollocations_X.POS + " AS " + SyntagNetContract.POS2, //
		};
		final String selection = SyntagNetContract.W1 + '.' + SnCollocations_X.LEMMA + " = ? OR " + SyntagNetContract.W2 + '.' + SnCollocations_X.LEMMA + " = ?"; ////
		final String[] selectionArgs = {SnSelectorsFragment.this.word, SnSelectorsFragment.this.word};
		final String sortOrder = SyntagNetContract.W1 + '.' + SnCollocations_X.LEMMA + ',' + SyntagNetContract.W2 + '.' + SnCollocations_X.LEMMA;
		this.model.loadData(uri, projection, selection, selectionArgs, sortOrder, null);
	}

	// C L I C K

	@Override
	public void onListItemClick(@NonNull final ListView listView, @NonNull final View view, final int position, final long id)
	{
		super.onListItemClick(listView, view, position, id);
		activate(position);
	}

	private void activate(int position)
	{
		final ListView listView = getListView();
		listView.setItemChecked(position, true);
		this.activatedPosition = position;

		if (this.listener != null)
		{
			final SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
			assert adapter != null;
			final Cursor cursor = adapter.getCursor();
			if (cursor.moveToPosition(position))
			{
				// column indexes
				final int idSynset1Id = cursor.getColumnIndex(SnCollocations_X.SYNSET1ID);
				final int idPos1 = cursor.getColumnIndex(SyntagNetContract.POS1);
				final int idWord1Id = cursor.getColumnIndex(SnCollocations_X.WORD1ID);
				final int idSynset2Id = cursor.getColumnIndex(SnCollocations_X.SYNSET2ID);
				final int idPos2 = cursor.getColumnIndex(SyntagNetContract.POS2);
				final int idWord2Id = cursor.getColumnIndex(SnCollocations_X.WORD2ID);

				// retrieve
				final long synset1Id = cursor.isNull(idSynset1Id) ? 0 : cursor.getLong(idSynset1Id);
				final char pos1 = cursor.getString(idPos1).charAt(0);
				final long word1Id = cursor.getLong(idWord1Id);
				final long synset2Id = cursor.isNull(idSynset2Id) ? 0 : cursor.getLong(idSynset2Id);
				final char pos2 = cursor.getString(idPos2).charAt(0);
				final long word2Id = cursor.getLong(idWord2Id);

				// pointer
				final CollocationSelectorPointer pointer = new CollocationSelectorPointer(synset1Id, word1Id, pos1, synset2Id, word2Id, pos2);

				// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
				this.listener.onItemSelected(pointer);
			}
		}
	}
}