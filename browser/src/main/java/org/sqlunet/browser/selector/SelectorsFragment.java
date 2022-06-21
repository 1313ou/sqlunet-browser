/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.selector;

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

import org.sqlunet.browser.BaseSelectorsListFragment;
import org.sqlunet.browser.Module;
import org.sqlunet.browser.PositionViewModel;
import org.sqlunet.browser.R;
import org.sqlunet.browser.SqlunetViewModel;
import org.sqlunet.loaders.Queries;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.provider.XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords;
import org.sqlunet.provider.XSqlUNetProvider;
import org.sqlunet.speak.Pronunciation;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

/**
 * Selector Fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("WeakerAccess")
public class SelectorsFragment extends BaseSelectorsListFragment
{
	static private final String TAG = "SelectorsF";

	/**
	 * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
	 */
	@FunctionalInterface
	public interface Listener
	{
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemSelected(SelectorPointer pointer, String word, String cased, String pronunciation, String pos);
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

	// L I F E C Y C L E

	// --activate--

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

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
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		Log.d(TAG, "lifecycle: onCreateView (3) " + this);
		return inflater.inflate(R.layout.fragment_selectors, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		Log.d(TAG, "lifecycle: onViewCreated (4) " + this);

		// when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
		getListView().setChoiceMode(this.activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);

		// data view models
		Log.d(TAG, "make models");
		makeModels();
	}

	//	@Override
	//	public void onActivityCreated(@Nullable final Bundle savedInstanceState)
	//	{
	//		super.onActivityCreated(savedInstanceState);
	//		Log.d(TAG, "lifecycle: onActivityCreated (5) " + this);
	//	}

	@Override
	public void onStart()
	{
		super.onStart();
		Log.d(TAG, "lifecycle: onStart (6) " + this);

		// load the contents
		// final MutableLiveData<Cursor> idLiveData = dataModel.getMutableData();
		//  final Cursor idCursor = idLiveData.getValue();
		//  if (idCursor != null && !idCursor.isClosed())
		//  {
		//   	idLiveData.setValue(idCursor);
		//  }
		//  else
		load();
	}

	// --deactivate--

	//	@Override
	//	public void onStop()
	//	{
	//		super.onStop();
	//		Log.d(TAG, "lifecycle: onStop(-4) " + this);
	//	}

	//	@Override
	//	public void onDestroyView()
	//	{
	//		super.onDestroyView();
	//		Log.d(TAG, "lifecycle: onDestroyView (-3) " + this);
	//	}

	//	@Override
	//	public void onDestroy()
	//	{
	//		super.onDestroy();
	//		Log.d(TAG, "lifecycle: onDestroy (-2) " + this);
	//	}

	//	@Override
	//	public void onDetach()
	//	{
	//		super.onDetach();
	//		Log.d(TAG, "lifecycle: onDetach (-1) " + this);
	//	}

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
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(requireContext(), R.layout.item_selector, null, //
				new String[]{ //
						Words_Pronunciations_FnWords_PbWords_VnWords.POS, //
						Words_Pronunciations_FnWords_PbWords_VnWords.SENSENUM, //
						Words_Pronunciations_FnWords_PbWords_VnWords.DOMAIN, //
						Words_Pronunciations_FnWords_PbWords_VnWords.DEFINITION, //
						Words_Pronunciations_FnWords_PbWords_VnWords.CASED, //
						Words_Pronunciations_FnWords_PbWords_VnWords.PRONUNCIATIONS, //
						Words_Pronunciations_FnWords_PbWords_VnWords.TAGCOUNT, //
						Words_Pronunciations_FnWords_PbWords_VnWords.LUID, //
						Words_Pronunciations_FnWords_PbWords_VnWords.SENSEKEY, //
						Words_Pronunciations_FnWords_PbWords_VnWords.WORDID, //
						Words_Pronunciations_FnWords_PbWords_VnWords.SYNSETID, //
						Words_Pronunciations_FnWords_PbWords_VnWords.SENSEID, //
						Words_Pronunciations_FnWords_PbWords_VnWords.VNWORDID, //
						Words_Pronunciations_FnWords_PbWords_VnWords.PBWORDID, //
						Words_Pronunciations_FnWords_PbWords_VnWords.FNWORDID, //
				}, //
				new int[]{ //
						R.id.pos, //
						R.id.sensenum, //
						R.id.domain, //
						R.id.definition, //
						R.id.cased, //
						R.id.pronunciation, //
						R.id.tagcount, //
						R.id.lexid, //
						R.id.sensekey, //
						R.id.wordid, //
						R.id.synsetid, //
						R.id.senseid, //
						R.id.vnwordid, //
						R.id.pbwordid, //
						R.id.fnwordid, //
				}, 0);

		adapter.setViewBinder((view, cursor, columnIndex) -> {

			String text = cursor.getString(columnIndex);

			// pronunciation
			if (view.getId() == R.id.pronunciation)
			{
				text = Pronunciation.sortedPronunciations(text);
			}

			// visibility
			if (text == null)
			{
				view.setVisibility(View.GONE);
				return false;
			}
			else
			{
				view.setVisibility(View.VISIBLE);
			}

			// type of view
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
		this.dataModel = new ViewModelProvider(this).get("selectors(word)", SqlunetViewModel.class);
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
	private void load()
	{
		// load the contents
		final Module.ContentProviderSql sql = Queries.prepareWordPronunciationSelect(this.word);
		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(sql.providerUri));
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
			final int idWordId = cursor.getColumnIndex(Words_Pronunciations_FnWords_PbWords_VnWords.WORDID);
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
				final int idSynsetId = cursor.getColumnIndex(Words_Pronunciations_FnWords_PbWords_VnWords.SYNSETID);
				final int idPos = cursor.getColumnIndex(Words_Pronunciations_FnWords_PbWords_VnWords.POS);
				final int idCased = cursor.getColumnIndex(Words_Pronunciations_FnWords_PbWords_VnWords.CASED);
				final int idPronunciation = cursor.getColumnIndex(Words_Pronunciations_FnWords_PbWords_VnWords.PRONUNCIATIONS);

				// retrieve
				final long synsetId = cursor.isNull(idSynsetId) ? 0 : cursor.getLong(idSynsetId);
				final String pos = cursor.getString(idPos);
				final String cased = cursor.getString(idCased);
				final String pronunciation = cursor.getString(idPronunciation);

				// pointer
				final SelectorPointer pointer = new PosSelectorPointer(synsetId, this.wordId, pos.charAt(0));

				// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
				this.listener.onItemSelected(pointer, this.word, cased, pronunciation, pos);
			}
		}
	}
}
