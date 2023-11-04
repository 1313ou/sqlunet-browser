/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.xn.selector;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.sqlunet.browser.BaseSelectorsListFragment;
import org.sqlunet.browser.Module;
import org.sqlunet.browser.R;
import org.sqlunet.browser.selector.PosSelectorPointer;
import org.sqlunet.browser.selector.SelectorPointer;
import org.sqlunet.loaders.Queries;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.provider.XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords;
import org.sqlunet.provider.XSqlUNetProvider;
import org.sqlunet.speak.Pronunciation;

import java.util.Locale;

import androidx.annotation.NonNull;

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
	 * Word id
	 */
	private long wordId;

	// L I F E C Y C L E

	// --constructor--

	public SelectorsFragment()
	{
		this.layoutId = R.layout.fragment_selectors;
		this.viewModelKey = "selectors(word)";
	}

	// --activate--

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Lifecycle: onCreate (2) " + this + " " + savedInstanceState);

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
	}

	// A D A P T E R

	@Override
	@NonNull
	protected CursorAdapter makeAdapter()
	{
		Log.d(TAG, "Make adapter");
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

	// L O A D

	@Override
	protected void load()
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
		void onItemSelected(SelectorPointer pointer, String word, String cased, String pronunciation, String pos);
	}

	/**
	 * The fragment's current callback, which is notified of list item clicks.
	 */
	private Listener listener;

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

	@Override
	protected void activate(int position)
	{
		this.positionModel.setPosition(position);

		if (this.listener != null)
		{
			final SimpleCursorAdapter adapter = (SimpleCursorAdapter) this.adapter;
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
