/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.browser;

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

import org.sqlunet.browser.SqlunetViewModel;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.wordnet.R;
import org.sqlunet.wordnet.SensePointer;
import org.sqlunet.wordnet.provider.WordNetContract;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains;
import org.sqlunet.wordnet.provider.WordNetProvider;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

/**
 * A fragment representing senses
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SensesFragment extends ListFragment
{
	/**
	 * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
	 */
	@FunctionalInterface
	@SuppressWarnings("unused")
	public interface Listener
	{
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemSelected(SensePointer sense, String word, String cased, String pos);
	}

	/**
	 * The serialization (saved instance state) Bundle key representing the activated item position. Only used on tablets.
	 */
	static private final String STATE_ACTIVATED_SELECTOR = "activated_selector";

	/**
	 * Activate on click flag
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

	/**
	 * Word id
	 */
	private long wordId;

	// View models

	private SqlunetViewModel sensesModelFromWord;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public SensesFragment()
	{
		//
	}

	// L I F E C Y C L E

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// arguments
		final Bundle args = getArguments();
		assert args != null;

		// target word
		String query = args.getString(ProviderArgs.ARG_QUERYSTRING);
		if (query != null)
		{
			query = query.trim().toLowerCase(Locale.ENGLISH);
		}
		this.word = query;
		this.wordId = 0;

		// list adapter, with no data
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(requireContext(), R.layout.item_sense, null, //
				new String[]{ //
						WordNetContract.PosTypes.POSNAME, //
						WordNetContract.Senses.SENSENUM, //
						WordNetContract.LexDomains.LEXDOMAIN, //
						WordNetContract.Synsets.DEFINITION, //
						WordNetContract.CasedWords.CASED, //
						WordNetContract.Senses.TAGCOUNT, //
						WordNetContract.Senses.LEXID, //
						WordNetContract.Senses.SENSEKEY, //
						WordNetContract.Words.WORDID, //
						WordNetContract.Synsets.SYNSETID, //
						WordNetContract.Senses.SENSEID, //
				}, //
				new int[]{ //
						R.id.pos, //
						R.id.sensenum, //
						R.id.lexdomain, //
						R.id.definition, //
						R.id.cased, //
						R.id.tagcount, //
						R.id.lexid, //
						R.id.sensekey, //
						R.id.wordid, //
						R.id.synsetid, //
						R.id.senseid, //
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
		this.sensesModelFromWord = new ViewModelProvider(this).get("wn.senses(word)", SqlunetViewModel.class);
		this.sensesModelFromWord.getData().observe(this, cursor -> {

			// pass on to list adapter
			final CursorAdapter adapter = (CursorAdapter) getListAdapter();
			assert adapter != null;
			adapter.swapCursor(cursor);
		});
	}

	// L I S T E N E R

	/**
	 * Set listener
	 *
	 * @param listener listener
	 */
	@SuppressWarnings("unused")
	public void setListener(final Listener listener)
	{
		this.listener = listener;
	}

	// V I E W

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_senses, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
		getListView().setChoiceMode(this.activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);

		// restore the previously serialized activated item position, if any
		if (savedInstanceState != null && savedInstanceState.containsKey(SensesFragment.STATE_ACTIVATED_SELECTOR))
		{
			final int position = savedInstanceState.getInt(SensesFragment.STATE_ACTIVATED_SELECTOR);
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

		// load the contents
		load();
	}

	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		super.onSaveInstanceState(outState);

		if (this.activatedPosition != AdapterView.INVALID_POSITION)
		{
			// serialize and persist the activated item position.
			outState.putInt(SensesFragment.STATE_ACTIVATED_SELECTOR, this.activatedPosition);
		}
	}

	// L O A D

	/**
	 * Load data from word
	 */
	private void load()
	{
		// load the contents
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CONTENT_URI_TABLE));
		final String[] projection = { //
				WordNetContract.Synsets.SYNSETID + " AS _id", //
				WordNetContract.Words.WORDID, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSEID, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSENUM, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSEKEY, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXID, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.TAGCOUNT, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.DEFINITION, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POSNAME, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXDOMAIN, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CASED};
		final String selection = WordNetContract.WORD + '.' + Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEMMA + " = ?";
		final String[] selectionArgs = {SensesFragment.this.word};
		final String sortOrder = Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POS + ',' + Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSENUM;
		this.sensesModelFromWord.loadData(uri, projection, selection, selectionArgs, sortOrder, this::sensesPostProcess);
	}

	private void sensesPostProcess(@NonNull final Cursor cursor)
	{
		// store source result
		if (cursor.moveToFirst())
		{
			final int wordId = cursor.getColumnIndex(WordNetContract.Words.WORDID);
			SensesFragment.this.wordId = cursor.getLong(wordId);
		}
	}

	// C L I C K

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
	 */
	@SuppressWarnings("unused")
	public void setActivateOnItemClick(final boolean activateOnItemClick)
	{
		this.activateOnItemClick = activateOnItemClick;
	}

	@Override
	public void onListItemClick(@NonNull final ListView listView, @NonNull final View view, final int position, final long id)
	{
		super.onListItemClick(listView, view, position, id);

		// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
		if (this.listener != null)
		{
			final SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
			assert adapter != null;
			final Cursor cursor = adapter.getCursor();
			assert cursor != null;
			if (cursor.moveToPosition(position))
			{
				final int idSynsetId = cursor.getColumnIndex(WordNetContract.Synsets.SYNSETID);
				final int idPos = cursor.getColumnIndex(WordNetContract.PosTypes.POSNAME);
				final int idCased = cursor.getColumnIndex(WordNetContract.CasedWords.CASED);

				final long synsetId = cursor.isNull(idSynsetId) ? 0 : cursor.getLong(idSynsetId);
				final String pos = cursor.getString(idPos);
				final String cased = cursor.getString(idCased);

				final SensePointer sense = new SensePointer(synsetId, this.wordId);

				this.listener.onItemSelected(sense, this.word, cased, pos);
			}

			// cursor ownership is transferred  to adapter, so do not call
			// cursor.close();
		}
	}
}
