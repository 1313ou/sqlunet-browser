package org.sqlunet.browser.selector;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.R;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.provider.XSqlUNetContract;
import org.sqlunet.provider.XSqlUNetContract.Words_FnWords_PbWords_VnWords;
import org.sqlunet.provider.XSqlUNetProvider;

import java.util.Locale;

/**
 * Selector Fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SelectorsFragment extends ListFragment
{
	// static protected final String TAG = "SelectorsFragment";

	/**
	 * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
	 */
	public interface Listener
	{
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemSelected(SelectorPointer pointer, String word, String cased, String pos);
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
	private String word;

	/**
	 * Word id
	 */
	private long wordId;

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

		//TODO setRetainInstance
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
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.item_selector, null, //
				new String[]{ //
						Words_FnWords_PbWords_VnWords.POS, //
						Words_FnWords_PbWords_VnWords.SENSENUM, //
						Words_FnWords_PbWords_VnWords.LEXDOMAIN, //
						Words_FnWords_PbWords_VnWords.DEFINITION, //
						Words_FnWords_PbWords_VnWords.CASED, //
						Words_FnWords_PbWords_VnWords.TAGCOUNT, //
						Words_FnWords_PbWords_VnWords.LEXID, //
						Words_FnWords_PbWords_VnWords.SENSEKEY, //
						Words_FnWords_PbWords_VnWords.WORDID, //
						Words_FnWords_PbWords_VnWords.SYNSETID, //
						Words_FnWords_PbWords_VnWords.SENSEID, //
						Words_FnWords_PbWords_VnWords.VNWORDID, //
						Words_FnWords_PbWords_VnWords.PBWORDID, //
						Words_FnWords_PbWords_VnWords.FNWORDID, //
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
						R.id.vnwordid, //
						R.id.pbwordid, //
						R.id.fnwordid, //
				}, 0);

		adapter.setViewBinder(new ViewBinder()
		{
			@Override
			public boolean setViewValue(final View view, final Cursor cursor, final int columnIndex)
			{
				String text = cursor.getString(columnIndex);
				if (text == null)
				{
					text = "";
				}

				if (view instanceof TextView)
				{
					((TextView) view).setText(text);
					// TODO return true; // if handled
				}
				else if (view instanceof ImageView)
				{
					try
					{
						((ImageView) view).setImageResource(Integer.parseInt(text));
					}
					catch (final NumberFormatException nfe)
					{
						((ImageView) view).setImageURI(Uri.parse(text));
					}
					// TODO return true; // if handled
				}
				else
				{
					throw new IllegalStateException(view.getClass().getName() + " is not a view that can be bound by this SimpleCursorAdapter");
				}
				return false;
			}
		});
		setListAdapter(adapter);
	}

	// V I E W

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_selectors, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState)
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
	public void onSaveInstanceState(final Bundle outState)
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
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(Words_FnWords_PbWords_VnWords.CONTENT_URI_TABLE));
				final String[] projection = { //
						Words_FnWords_PbWords_VnWords.SYNSETID + " AS _id", //
						Words_FnWords_PbWords_VnWords.WORDID, //
						Words_FnWords_PbWords_VnWords.SENSEID, //
						Words_FnWords_PbWords_VnWords.SENSENUM, //
						Words_FnWords_PbWords_VnWords.SENSEKEY, //
						Words_FnWords_PbWords_VnWords.LEXID, //
						Words_FnWords_PbWords_VnWords.TAGCOUNT, //
						Words_FnWords_PbWords_VnWords.SYNSETID, //
						Words_FnWords_PbWords_VnWords.DEFINITION, //
						XSqlUNetContract.SYNSET + '.' + Words_FnWords_PbWords_VnWords.POS, //
						Words_FnWords_PbWords_VnWords.POSNAME, //
						Words_FnWords_PbWords_VnWords.LEXDOMAIN, //
						Words_FnWords_PbWords_VnWords.CASED, //
						Words_FnWords_PbWords_VnWords.FNWORDID, //
						Words_FnWords_PbWords_VnWords.VNWORDID, //
						Words_FnWords_PbWords_VnWords.PBWORDID, //
				};
				final String selection = XSqlUNetContract.WORD + '.' + Words_FnWords_PbWords_VnWords.LEMMA + " = ?"; ////
				final String[] selectionArgs = {SelectorsFragment.this.word};
				final String sortOrder = XSqlUNetContract.SYNSET + '.' + Words_FnWords_PbWords_VnWords.POS + ',' + Words_FnWords_PbWords_VnWords.SENSENUM;
				return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				// store source progressMessage
				if (cursor.moveToFirst())
				{
					final int idWordId = cursor.getColumnIndex(Words_FnWords_PbWords_VnWords.WORDID);
					SelectorsFragment.this.wordId = cursor.getLong(idWordId);
				}

				// pass on to list adapter
				((CursorAdapter) getListAdapter()).swapCursor(cursor);

				// check
				/*
				if (SelectorsFragment.this.activatedPosition != AdapterView.INVALID_POSITION)
				{
					final ListView listView = getListView();
					listView.setItemChecked(SelectorsFragment.this.activatedPosition, true);
				}
				*/
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> arg)
			{
				((CursorAdapter) getListAdapter()).swapCursor(null);
			}
		});
	}

	// C L I C K

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
	 *
	 * @param activateOnItemClick true if activate
	 */
	public void setActivateOnItemClick(final boolean activateOnItemClick)
	{
		this.activateOnItemClick = activateOnItemClick;
	}

	@Override
	public void onListItemClick(final ListView listView, final View view, final int position, final long id)
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
			final Cursor cursor = adapter.getCursor();
			if (cursor.moveToPosition(position))
			{
				// column indexes
				final int idSynsetId = cursor.getColumnIndex(Words_FnWords_PbWords_VnWords.SYNSETID);
				final int idPos = cursor.getColumnIndex(Words_FnWords_PbWords_VnWords.POSNAME);
				final int idCased = cursor.getColumnIndex(Words_FnWords_PbWords_VnWords.CASED);

				// retrieve
				final long synsetId = cursor.isNull(idSynsetId) ? 0 : cursor.getLong(idSynsetId);
				final String pos = cursor.getString(idPos);
				final String cased = cursor.getString(idCased);

				// pointer
				final SelectorPointer pointer = new SelectorPointer(synsetId, this.wordId);

				// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
				this.listener.onItemSelected(pointer, this.word, cased, pos);
			}
		}
	}
}
