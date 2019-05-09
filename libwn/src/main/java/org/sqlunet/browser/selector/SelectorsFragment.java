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

import org.sqlunet.browser.SqlunetViewModel;
import org.sqlunet.browser.wn.lib.R;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.wordnet.SensePointer;
import org.sqlunet.wordnet.provider.WordNetContract;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains;
import org.sqlunet.wordnet.provider.WordNetProvider;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProviders;

/**
 * Selector Fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("WeakerAccess")
public class SelectorsFragment extends ListFragment
{
	// static protected final String TAG = "SelectorsFragment";

	/**
	 * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
	 */
	@FunctionalInterface
	public interface Listener
	{
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemSelected(SensePointer pointer, String word, String cased, String pos);
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

	/**
	 * View model
	 */
	private SqlunetViewModel model;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public SelectorsFragment()
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

		// target word
		String query = args.getString(ProviderArgs.ARG_QUERYSTRING);
		if (query != null)
		{
			query = query.trim().toLowerCase(Locale.ENGLISH);
		}
		this.word = query;
		this.wordId = 0;

		// list adapter, with no data
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(requireContext(), R.layout.item_selector, null, //
				new String[]{ //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POS, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXDOMAIN, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.DEFINITION, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CASED, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSENUM, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSEKEY, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXID, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.TAGCOUNT, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.WORDID, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSEID, //
				}, //
				new int[]{ //
						R.id.pos, //
						R.id.lexdomain, //
						R.id.definition, //
						R.id.cased, //
						R.id.sensenum, //
						R.id.sensekey, //
						R.id.lexid, //
						R.id.tagcount, //
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
		this.model = ViewModelProviders.of(this).get("wn:selectors(word)", SqlunetViewModel.class);
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
		return inflater.inflate(R.layout.fragment_selectors, container, false);
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
			final int position = savedInstanceState.getInt(SelectorsFragment.STATE_ACTIVATED_SELECTOR, AdapterView.INVALID_POSITION);
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

		// load the contents (once activity is available)
		load();
	}

	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		super.onSaveInstanceState(outState);

		if (this.activatedPosition != AdapterView.INVALID_POSITION)
		{
			// serialize and persist the activated item position.
			outState.putInt(SelectorsFragment.STATE_ACTIVATED_SELECTOR, this.activatedPosition);
		}
	}

	// L I S T E N E R

	/**
	 * Set listener
	 *
	 * @param listener listener
	 */
	@SuppressWarnings("WeakerAccess")
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
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CONTENT_URI_TABLE));
		final String[] projection = { //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID + " AS _id", //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.WORDID, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSEID, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSENUM, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSEKEY, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXID, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.TAGCOUNT, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.DEFINITION, //
				WordNetContract.SYNSET + '.' + Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POS, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POSNAME, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXDOMAIN, //
				Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CASED, //
		};
		final String selection = Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEMMA + " = ?"; ////
		final String[] selectionArgs = {SelectorsFragment.this.word};
		final String sortOrder = WordNetContract.SYNSET + '.' + Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POS + ',' + Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSENUM;
		this.model.loadData(uri, projection, selection, selectionArgs, sortOrder, this::selectorsPostProcess);
	}

	private void selectorsPostProcess(@NonNull final Cursor cursor)
	{
		if (cursor.moveToFirst())
		{
			final int idWordId = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.WORDID);
			SelectorsFragment.this.wordId = cursor.getLong(idWordId);
		}
	}

	// C L I C K

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
			assert cursor != null;
			if (cursor.moveToPosition(position))
			{
				// column indexes
				final int idSynsetId = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID);
				final int idPos = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POSNAME);
				final int idCased = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CASED);

				// retrieve
				final long synsetId = cursor.isNull(idSynsetId) ? 0 : cursor.getLong(idSynsetId);
				final String pos = cursor.getString(idPos);
				final String cased = cursor.getString(idCased);

				// pointer
				final SensePointer pointer = new SensePointer(synsetId, this.wordId);

				// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
				this.listener.onItemSelected(pointer, this.word, cased, pos);
			}
		}
	}
}
