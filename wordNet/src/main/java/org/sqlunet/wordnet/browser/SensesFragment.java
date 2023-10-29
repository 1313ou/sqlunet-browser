/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet.browser;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.PositionViewModel;
import org.sqlunet.browser.SqlunetViewModel;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.wordnet.R;
import org.sqlunet.wordnet.SensePointer;
import org.sqlunet.wordnet.loaders.Queries;
import org.sqlunet.wordnet.provider.WordNetContract;
import org.sqlunet.wordnet.provider.WordNetProvider;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

/**
 * Senses selector fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("WeakerAccess")
public class SensesFragment extends ListFragment
{
	static private final String TAG = "SensesF";

	/**
	 * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
	 */
	@FunctionalInterface
	public interface Listener
	{
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemSelected(SensePointer sense, String word, String cased, String pos);
	}

	/**
	 * Activate on click flag: in two-pane mode, list items should be given the 'activated' state when touched.
	 */
	private boolean activateOnItemClick = true;

	/**
	 * The fragment's current callback, which is notified of list item clicks.
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
	 * Data view model
	 */
	private SqlunetViewModel dataModel;

	/**
	 * Position view model
	 */
	private PositionViewModel positionModel;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public SensesFragment()
	{
		Log.d(TAG, "Lifecycle: Constructor (0) " + this);
	}

	// L I F E C Y C L E

	// --activate--

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Lifecycle: onCreate (2) " + this);
		//noinspection deprecation
		this.setRetainInstance(false); // default

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
		ListAdapter adapter = makeAdapter();
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		Log.d(TAG, "Lifecycle: onCreateView (3) " + this);
		return inflater.inflate(R.layout.fragment_senses, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		Log.d(TAG, "Lifecycle: onViewCreated (4) " + this);

		// when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
		getListView().setChoiceMode(this.activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);

		// data view models
		Log.d(TAG, "make models");
		makeModels();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		Log.d(TAG, "Lifecycle: onStart (6) " + this);
		senses();
	}

	// --deactivate--

	// H E L P E R S

	/**
	 * Make adapter
	 *
	 * @return adapter
	 */
	@NonNull
	private ListAdapter makeAdapter()
	{
		Log.d(TAG, "make adapter");
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(requireContext(), R.layout.item_sense, null, //
				new String[]{ //
						WordNetContract.Poses.POS, //
						WordNetContract.Senses.SENSENUM, //
						WordNetContract.Domains.DOMAIN, //
						WordNetContract.Synsets.DEFINITION, //
						WordNetContract.CasedWords.CASEDWORD, //
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
						R.id.domain, //
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
				return true;
			}
			else if (view instanceof ImageView)
			{
				try
				{
					((ImageView) view).setImageResource(Integer.parseInt(text));
					return true;
				}
				catch (@NonNull final NumberFormatException nfe)
				{
					((ImageView) view).setImageURI(Uri.parse(text));
					return true;
				}
			}
			else
			{
				throw new IllegalStateException(view.getClass().getName() + " is not a view that can be bound by this SimpleCursorAdapter");
			}
		});
		return adapter;
	}

	// V I E W M O D E L S

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		// data model
		this.dataModel = new ViewModelProvider(this).get("wn.senses(word)", SqlunetViewModel.class);
		this.dataModel.getData().observe(getViewLifecycleOwner(), cursor -> {

			// pass on to list adapter
			final CursorAdapter adapter = (CursorAdapter) getListAdapter();
			assert adapter != null;
			adapter.swapCursor(cursor);
		});

		// position model
		this.positionModel = new ViewModelProvider(this).get(PositionViewModel.class);
		this.positionModel.getPositionLiveData().observe(getViewLifecycleOwner(), (position) -> {

			Log.d(TAG, "Observed position change " + position);
			getListView().setItemChecked(position, position != AdapterView.INVALID_POSITION);
		});
		this.positionModel.setPosition(AdapterView.INVALID_POSITION);
	}

	// L O A D

	/**
	 * Load data from word
	 */
	private void senses()
	{
		// load the contents
		final Module.ContentProviderSql sql = Queries.prepareSenses(word);
		final Uri uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri));
		this.dataModel.loadData(uri, sql, this::wordIdFromWordPostProcess);
	}

	/**
	 * Post processing, extraction of wordid from cursor
	 *
	 * @param cursor cursor
	 */
	private void wordIdFromWordPostProcess(@NonNull final Cursor cursor)
	{
		if (cursor.moveToFirst())
		{
			final int idWordId = cursor.getColumnIndex(WordNetContract.Words.WORDID);
			this.wordId = cursor.getLong(idWordId);
		}
		// cursor.close();
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

	/**
	 * Activate item at position
	 *
	 * @param position position
	 */
	private void activate(int position)
	{
		this.positionModel.setPosition(position);

		if (this.listener != null)
		{
			final SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
			assert adapter != null;
			final Cursor cursor = adapter.getCursor();
			assert cursor != null;
			if (cursor.moveToPosition(position))
			{
				// column indexes
				final int idSynsetId = cursor.getColumnIndex(WordNetContract.Synsets.SYNSETID);
				final int idPos = cursor.getColumnIndex(WordNetContract.Poses.POS);
				final int idCased = cursor.getColumnIndex(WordNetContract.CasedWords.CASEDWORD);

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
