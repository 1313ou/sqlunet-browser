/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.selector;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
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

import com.google.android.material.snackbar.Snackbar;

import org.sqlunet.browser.BaseSelectorsListFragment;
import org.sqlunet.browser.Module;
import org.sqlunet.browser.PositionViewModel;
import org.sqlunet.browser.Selectors;
import org.sqlunet.browser.SqlunetViewModel;
import org.sqlunet.browser.sn.R;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.syntagnet.loaders.Queries;
import org.sqlunet.syntagnet.provider.SyntagNetContract;
import org.sqlunet.syntagnet.provider.SyntagNetContract.SnCollocations_X;
import org.sqlunet.syntagnet.provider.SyntagNetProvider;
import org.sqlunet.wordnet.provider.WordNetContract.Words;
import org.sqlunet.wordnet.provider.WordNetProvider;

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
public class SnSelectorsFragment extends BaseSelectorsListFragment
{
	static private final String TAG = "SnSelectorsF";

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
	 * Columns
	 */
	private static final String[] COLUMNS = { //
			"_id", //
			SnCollocations_X.WORD1ID, //
			SnCollocations_X.WORD2ID, //
			SnCollocations_X.SYNSET1ID, //
			SnCollocations_X.SYNSET2ID, //
			SyntagNetContract.WORD1, //
			SyntagNetContract.WORD2, //
			SyntagNetContract.POS1, //
			SyntagNetContract.POS2, //
	};

	/**
	 * Displayed columns
	 */
	private static final String[] DISPLAYED_COLUMNS = { //
			SyntagNetContract.WORD1, //
			SyntagNetContract.WORD2, //
			SyntagNetContract.POS1, //
			SyntagNetContract.POS2, //
			//SnCollocations_X.WORD1ID, //
			//SnCollocations_X.WORD2ID, //
			//SnCollocations_X.SYNSET1ID, //
			//SnCollocations_X.SYNSET2ID, //
			//SyntagNetContract.POS1, //
			//SyntagNetContract.POS2, //
	};

	/**
	 * Column resources
	 */
	private static final int[] DISPLAYED_COLUMN_RES_IDS = {  //
			R.id.word1, //
			R.id.word2, //
			R.id.pos1, //
			R.id.pos2, //
			//R.id.word1id, //
			//R.id.word2id, //
			//R.id.synset1id, //
			//R.id.synset2id, //
			//R.id.pos1, //
			//R.id.pos2, //
	};

	/**
	 * Activate on click flag: in two-pane mode, list items should be given the 'activated' state when touched.
	 */
	private boolean activateOnItemClick = true;

	/**
	 * The fragment's current callbacks, which are notified of list item clicks.
	 */
	private Listener[] listeners;

	/**
	 * Search query
	 */
	@Nullable
	private String word;

	/**
	 * Word id
	 */
	private long wordId = -1;

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

		// activate on click
		this.activateOnItemClick = args.getBoolean(Selectors.IS_TWO_PANE, false);

		// target word
		String query = args.getString(ProviderArgs.ARG_QUERYSTRING);
		if (query != null)
		{
			query = query.trim().toLowerCase(Locale.ENGLISH);
		}
		this.word = query;
		this.wordId = queryId(query);

		// list adapter, with no data
		ListAdapter adapter = makeAdapter();
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		Log.d(TAG, "lifecycle: onCreateView (3) " + this);
		return inflater.inflate(R.layout.fragment_snselectors, container, false);
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
		if (this.wordId != -1)
		{
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
	 * Query word
	 *
	 * @param query query word
	 * @return word id
	 */
	private long queryId(final String query)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Words.URI));
		final String[] projection = {Words.WORDID,};
		final String selection = Words.WORD + " = ?"; //
		final String[] selectionArgs = {query};
		try (Cursor cursor = requireContext().getContentResolver().query(uri, projection, selection, selectionArgs, null))
		{
			if (cursor != null)
			{
				if (cursor.moveToFirst())
				{
					final int idWordId = cursor.getColumnIndex(Words.WORDID);
					return cursor.getLong(idWordId);
				}
			}
		}
		return -1;
	}

	/**
	 * Make adapter
	 *
	 * @return adapter
	 */
	@NonNull
	private ListAdapter makeAdapter()
	{
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(requireContext(), R.layout.item_snselector, null, //
				DISPLAYED_COLUMNS, DISPLAYED_COLUMN_RES_IDS, 0);
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
		this.dataModel = new ViewModelProvider(this).get("snselectors(word)", SqlunetViewModel.class);
		this.dataModel.getData().observe(getViewLifecycleOwner(), cursor -> {

			if (cursor == null || cursor.getCount() <= 0)
			{
				final String html = getString(R.string.error_entry_not_found, "<b>" + this.word + "</b>");
				final CharSequence message = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N ? Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY) : Html.fromHtml(html);
				// Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
				Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
			}
			else
			{
				Cursor cursor2 = SnSelectorsFragment.augmentCursor(cursor, wordId, word);

				// pass on to list adapter
				final CursorAdapter adapter = (CursorAdapter) getListAdapter();
				assert adapter != null;
				adapter.swapCursor(cursor2);
			}
		});

		// position model
		this.positionModel = new ViewModelProvider(this).get(PositionViewModel.class);
		this.positionModel.getPositionLiveData().observe(getViewLifecycleOwner(), (position) -> {

			Log.d(TAG, "Observed position change " + position);
			getListView().setItemChecked(position, position != AdapterView.INVALID_POSITION);
		});
		this.positionModel.setPosition(AdapterView.INVALID_POSITION);
	}

	/**
	 * Augment cursor with special values
	 *
	 * @param cursor cursor
	 * @param wordid word id
	 * @param word   word
	 * @return augmented cursor
	 */
	@NonNull
	private static Cursor augmentCursor(Cursor cursor, long wordid, String word)
	{
		// Create a MatrixCursor filled with the special rows to add.
		@SuppressWarnings("resource") MatrixCursor matrixCursor = new MatrixCursor(COLUMNS);

		//	"_id",  WORD1ID,  WORD2ID,  SYNSET1ID,  SYNSET2ID,  WORD1,  WORD2,  POS1,  POS2
		matrixCursor.addRow(new Object[]{Integer.MAX_VALUE, wordid, wordid, null, null, "* " + word + " *", "", null, null});
		matrixCursor.addRow(new Object[]{Integer.MAX_VALUE - 1, wordid, null, null, null, word, "*", null, null});
		matrixCursor.addRow(new Object[]{Integer.MAX_VALUE - 2, null, wordid, null, null, "*", word, null, null});

		// Merge your existing cursor with the matrixCursor you created.
		return new MergeCursor(new Cursor[]{matrixCursor, cursor});
	}

	// L O A D

	/**
	 * Load data from word
	 */
	private void load()
	{
		// load the contents
		final Module.ContentProviderSql sql = Queries.prepareSnSelect(SnSelectorsFragment.this.wordId);
		final Uri uri = Uri.parse(SyntagNetProvider.makeUri(sql.providerUri));
		this.dataModel.loadData(uri, sql, null);
	}

	// L I S T E N E R

	/**
	 * Set listeners
	 *
	 * @param listeners listeners
	 */
	public void setListeners(final Listener... listeners)
	{
		this.listeners = listeners;
	}

	// C L I C K

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

		if (this.listeners != null)
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
				final long synset1Id = cursor.isNull(idSynset1Id) ? -1 : cursor.getLong(idSynset1Id);
				final long word1Id = cursor.isNull(idWord1Id) ? -1 : cursor.getLong(idWord1Id);
				final char pos1 = cursor.isNull(idPos1) ? 0 : cursor.getString(idPos1).charAt(0);
				final long synset2Id = cursor.isNull(idSynset2Id) ? -1 : cursor.getLong(idSynset2Id);
				final long word2Id = cursor.isNull(idWord2Id) ? -1 : cursor.getLong(idWord2Id);
				final char pos2 = cursor.isNull(idPos2) ? 0 : cursor.getString(idPos2).charAt(0);

				int target = this.wordId != -1 && word1Id != word2Id ? (word1Id == this.wordId ? 1 : word2Id == this.wordId ? 2 : 0) : 0;

				// pointer
				final CollocationSelectorPointer pointer = new CollocationSelectorPointer(synset1Id, word1Id, pos1, synset2Id, word2Id, pos2, target);

				// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
				for (Listener listener : this.listeners)
				{
					listener.onItemSelected(pointer);
				}
			}
		}
	}

	/**
	 * Deactivate all
	 */
	public void deactivate()
	{
		final ListView listView = getListView();
		listView.clearChoices();
		listView.requestLayout();
	}
}
