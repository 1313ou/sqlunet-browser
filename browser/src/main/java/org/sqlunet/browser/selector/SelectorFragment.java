package org.sqlunet.browser.selector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.R;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.provider.XSqlUNetContract;
import org.sqlunet.provider.XSqlUNetContract.Words_FnWords_PbWords_VnWords;

import java.util.Locale;

/**
 * Selector Fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SelectorFragment extends ListFragment
{
	// protected static final String TAG = "SelectorFragment";
	/**
	 * The serialization (saved instance state) Bundle key representing the activated item position. Only used on tablets.
	 */
	private static final String ACTIVATED_POSITION_NAME = "activated_position";
	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int activatedPosition = AdapterView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
	 */
	public interface Listener
	{
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemSelected(SelectorPointer pointer);
	}

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
	public SelectorFragment()
	{
	}

	// L I F E   C Y C L E

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// arguments
		Bundle args = getArguments();
		if (args == null)
		{
			args = getActivity().getIntent().getExtras();
		}

		// target word
		String query = args.getString(SqlUNetContract.ARG_QUERYSTRING);
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
						Words_FnWords_PbWords_VnWords.FNWORDID, //
						Words_FnWords_PbWords_VnWords.VNWORDID, //
						Words_FnWords_PbWords_VnWords.PBWORDID, //
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
						R.id.fnwordid, //
						R.id.vnwordid, //
						R.id.pbwordid, //
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
				}
				else
				{
					throw new IllegalStateException(view.getClass().getName() + " is not a view that can be bound by this SimpleCursorAdapter");
				}
				return false;
			}
		});
		setListAdapter(adapter);

		// load the contents
		load();
	}

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
				final Uri uri = Uri.parse(Words_FnWords_PbWords_VnWords.CONTENT_URI);
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
				final String[] selectionArgs = {SelectorFragment.this.word};
				final String sortOrder = XSqlUNetContract.SYNSET + '.' + Words_FnWords_PbWords_VnWords.POS + ',' + Words_FnWords_PbWords_VnWords.SENSENUM;
				return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				// store source result
				if (cursor.moveToFirst())
				{
					final int idWordId = cursor.getColumnIndex(Words_FnWords_PbWords_VnWords.WORDID);
					SelectorFragment.this.wordId = cursor.getLong(idWordId);
				}

				// pass on to list adapter
				((CursorAdapter) getListAdapter()).swapCursor(cursor);
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> arg)
			{
				((CursorAdapter) getListAdapter()).swapCursor(null);
			}
		});
	}

	// A T T A C H / D E T A C H

	@TargetApi(Build.VERSION_CODES.M)
	@Override
	public void onAttach(final Context context)
	{
		super.onAttach(context);
		onAttachToContext(context);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onAttach(final Activity activity)
	{
		super.onAttach(activity);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
		{
			onAttachToContext(activity);
		}
	}

	private void onAttachToContext(final Context context)
	{
		// activities containing this fragment must implement its listener
		if (!(context instanceof Listener))
		{
			throw new IllegalStateException("Activity must implement fragment's listener.");
		}
		this.listener = (Listener) context;
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		// reset the active listener interface to the dummy implementation.
		this.listener = null;
	}

	// L A Y O U T

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// layout
		return inflater.inflate(R.layout.fragment_selector, container);
	}

	// R E S T O R E / S A V E A C T I V A T E D S T A T E

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// restore the previously serialized activated item position, if any
		if (savedInstanceState != null && savedInstanceState.containsKey(SelectorFragment.ACTIVATED_POSITION_NAME))
		{
			final int position = savedInstanceState.getInt(SelectorFragment.ACTIVATED_POSITION_NAME);
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
	public void onSaveInstanceState(final Bundle outState)
	{
		super.onSaveInstanceState(outState);

		if (this.activatedPosition != AdapterView.INVALID_POSITION)
		{
			// serialize and persist the activated item position.
			outState.putInt(SelectorFragment.ACTIVATED_POSITION_NAME, this.activatedPosition);
		}
	}

	// C L I C K

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
	 *
	 * @param activateOnItemClick true if activate
	 */
	public void setActivateOnItemClick(final boolean activateOnItemClick)
	{
		// when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
		getListView().setChoiceMode(activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);
	}

	@Override
	public void onListItemClick(final ListView listView, final View view, final int position, final long id)
	{
		super.onListItemClick(listView, view, position, id);
		listView.setItemChecked(position, true);
		// view.setSelected(true);
		// view.setActivated(true);

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
			final SelectorPointer pointer = new SelectorPointer(synsetId, pos, this.wordId, this.word, cased);

			// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
			if (this.listener != null)
			{
				this.listener.onItemSelected(pointer);
			}
		}
	}
}
