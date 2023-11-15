/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.sn.selector;

import android.database.Cursor;
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
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains;
import org.sqlunet.wordnet.provider.WordNetProvider;

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
	// static private final String TAG = "SelectorsF";

	/**
	 * Word id
	 */
	private long wordId = -1;

	// L I F E C Y C L E

	public SelectorsFragment()
	{
		this.layoutId = R.layout.fragment_selectors;
		this.viewModelKey = "sn:selectors(word)";

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
		this.wordId = 0;
	}

	// A D A P T E R

	@Override
	@NonNull
	protected CursorAdapter makeAdapter()
	{
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(requireContext(), R.layout.item_selector, null, //
				new String[]{ //
						Words_Senses_CasedWords_Synsets_Poses_Domains.POSID, //
						Words_Senses_CasedWords_Synsets_Poses_Domains.DOMAIN, //
						Words_Senses_CasedWords_Synsets_Poses_Domains.DEFINITION, //
						Words_Senses_CasedWords_Synsets_Poses_Domains.CASEDWORD, //
						Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM, //
						Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEKEY, //
						Words_Senses_CasedWords_Synsets_Poses_Domains.LEXID, //
						Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT, //
						Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID, //
						Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID, //
						Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEID, //
				}, //
				new int[]{ //
						R.id.pos, //
						R.id.domain, //
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
		final Module.ContentProviderSql sql = org.sqlunet.wordnet.loaders.Queries.prepareSelectSn(SelectorsFragment.this.word);
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
			final int idWordId = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID);
			this.wordId = cursor.getLong(idWordId);
		}
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
				final int idSynsetId = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID);
				final int idPos = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.POS);
				final int idCased = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.CASEDWORD);

				// retrieve
				final long synsetId = cursor.isNull(idSynsetId) ? 0 : cursor.getLong(idSynsetId);
				final String pos = cursor.getString(idPos);
				final String cased = cursor.getString(idCased);
				final String pronunciation = null;

				// pointer
				final SelectorPointer pointer = new PosSelectorPointer(synsetId, this.wordId, pos.charAt(0));

				// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
				for (Listener listener : this.listeners)
				{
					//noinspection ConstantValue
					listener.onItemSelected(pointer, this.word, cased, pronunciation, pos);
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
