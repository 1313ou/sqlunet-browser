/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.sn.selector;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.sqlunet.browser.BaseSelectorsListFragment;
import org.sqlunet.browser.Module;
import org.sqlunet.browser.Selectors;
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

/**
 * Selector Fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("WeakerAccess")
public class SnSelectorsFragment extends BaseSelectorsListFragment
{
	// static private final String TAG = "SnSelectorsF";

	/**
	 * Word id
	 */
	private long wordId = -1;

	// L I F E C Y C L E

	public SnSelectorsFragment()
	{
		this.layoutId = R.layout.fragment_selectors;
		this.viewModelKey = "snx:selectors(word)";
	}

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
	}

	// A D A P T E R

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

	@Override
	@NonNull
	protected CursorAdapter makeAdapter()
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

	// L O A D

	@Override
	protected void load()
	{
		if (this.wordId == -1)
		{
			return;
		}

		final Module.ContentProviderSql sql = Queries.prepareSnSelect(SnSelectorsFragment.this.wordId);
		final Uri uri = Uri.parse(SyntagNetProvider.makeUri(sql.providerUri));
		this.dataModel.loadData(uri, sql, null);
	}

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

	// V I E W M O D E L S

	@Override
	@NonNull
	protected Cursor augmentCursor(@NonNull Cursor cursor)
	{
		return augmentCursor(cursor, this.wordId, this.word);
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
	private Cursor augmentCursor(Cursor cursor, long wordid, String word)
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

	// C L I C K   L I S T E N E R

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
	 * The fragment's current callbacks, which are notified of list item clicks.
	 */
	private Listener[] listeners;

	/**
	 * Set listeners
	 *
	 * @param listeners listeners
	 */
	public void setListeners(final Listener... listeners)
	{
		this.listeners = listeners;
	}

	@Override
	protected void activate(int position)
	{
		this.positionModel.setPosition(position);

		if (this.listeners != null)
		{
			final SimpleCursorAdapter adapter = (SimpleCursorAdapter) this.adapter;
			assert adapter != null;
			final Cursor cursor = adapter.getCursor();
			assert cursor != null;
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
		final ListView listView = this.listView;
		listView.clearChoices();
		listView.requestLayout();
	}
}
