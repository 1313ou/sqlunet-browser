package org.sqlunet.wordnet.browser;

import org.sqlunet.browser.Module;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.wordnet.R;
import org.sqlunet.wordnet.SensePointer;
import org.sqlunet.wordnet.WordPointer;
import org.sqlunet.wordnet.provider.WordNetContract;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

/**
 * A list fragment representing a list of synsets. This fragment also supports tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a {@link SenseFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Listener} interface.
 *
 * @author Bernard Bou
 */
public class SensesFragment extends ListFragment
{
	/**
	 * The serialization (saved instance state) Bundle key representing the activated item position. Only used on tablets.
	 */
	private static final String ACTIVATED_POSITION_NAME = "activated_position"; //$NON-NLS-1$

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
		void onItemSelected(SensePointer sense);
	}

	/**
	 * The fragment's current callback object, which is notified of list item clicks.
	 */
	private Listener listener = null;

	/**
	 * Search query
	 */
	private String queryWord;

	/**
	 * Search word
	 */
	private WordPointer word;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public SensesFragment()
	{
		//
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// get target passed as parameter
		Bundle args = getArguments();
		if (args == null)
		{
			args = getActivity().getIntent().getExtras();
		}
		this.queryWord = args.getString(SqlUNetContract.ARG_QUERYSTRING);
		this.word = null;

		// list adapter, with no data
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.item_sense, null, //
				new String[] { //
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
				new int[] { //
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

		adapter.setViewBinder(new ViewBinder()
		{
			@Override
			public boolean setViewValue(final View view, final Cursor cursor, final int columnIndex)
			{
				String text = cursor.getString(columnIndex);
				if (text == null)
				{
					text = ""; //$NON-NLS-1$
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
					throw new IllegalStateException(view.getClass().getName() + " is not a view that can be bound by this SimpleCursorAdapter"); //$NON-NLS-1$
				return false;
			}
		});
		setListAdapter(adapter);

		// load the contents
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId0, final Bundle args0)
			{
				final Uri uri = Uri.parse(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CONTENT_URI);
				final String[] projection = new String[] { //
				WordNetContract.Synsets.SYNSETID + " AS _id", // //$NON-NLS-1$
						WordNetContract.Words.WORDID, //
						WordNetContract.Senses.SENSEID, //
						WordNetContract.Senses.SENSENUM, //
						WordNetContract.Senses.SENSEKEY, //
						WordNetContract.Senses.LEXID, //
						WordNetContract.Senses.TAGCOUNT, //
						WordNetContract.Synsets.SYNSETID, //
						WordNetContract.Synsets.DEFINITION, //
						WordNetContract.PosTypes.POSNAME, //
						WordNetContract.LexDomains.LEXDOMAIN, //
						WordNetContract.CasedWords.CASED };
				final String selection = Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEMMA + " = ?"; //$NON-NLS-1$
				final String[] selectionArgs = new String[] { SensesFragment.this.queryWord };
				final String sortOrder = Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POS + ',' + Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSENUM;
				return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@SuppressWarnings("synthetic-access")
			@Override
			public void onLoadFinished(final Loader<Cursor> loader0, final Cursor c)
			{
				// store source result
				if (c.moveToFirst())
				{
					SensesFragment.this.word = new WordPointer();
					SensesFragment.this.word.lemma = SensesFragment.this.queryWord;
					final int wordidId = c.getColumnIndex(WordNetContract.Words.WORDID);
					SensesFragment.this.word.wordid = c.getLong(wordidId);
				}

				// pass on to list adapter
				((SimpleCursorAdapter) getListAdapter()).swapCursor(c);
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> arg0)
			{
				((SimpleCursorAdapter) getListAdapter()).swapCursor(null);
			}
		});
	}

	// L A Y O U T

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// layout
		return inflater.inflate(R.layout.fragment_senses, container);
	}

	// R E S T O R E / S A V E A C T I V A T E D S T A T E

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListFragment#onViewCreated(android.view.View, android.os.Bundle)
	 */
	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// restore the previously serialized activated item position, if any
		if (savedInstanceState != null && savedInstanceState.containsKey(SensesFragment.ACTIVATED_POSITION_NAME))
		{
			final int position = savedInstanceState.getInt(SensesFragment.ACTIVATED_POSITION_NAME);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState)
	{
		super.onSaveInstanceState(outState);

		if (this.activatedPosition != AdapterView.INVALID_POSITION)
		{
			// serialize and persist the activated item position.
			outState.putInt(SensesFragment.ACTIVATED_POSITION_NAME, this.activatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
	 */
	@SuppressWarnings("unused")
	public void setActivateOnItemClick(final boolean activateOnItemClick)
	{
		// when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
		getListView().setChoiceMode(activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);
	}

	// C L I C K E V E N T L I S T E N

	@TargetApi(23)
	@Override
	public void onAttach(final Context context)
	{
		super.onAttach(context);
		onAttachToContext(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onAttach(android.app.Activity)
	 */
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
			throw new IllegalStateException("Activity must implement fragment's listener."); //$NON-NLS-1$
		this.listener = (Listener) context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onDetach()
	 */
	@Override
	public void onDetach()
	{
		super.onDetach();

		// reset the active listener interface to the dummy implementation.
		this.listener = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@SuppressWarnings("boxing")
	@Override
	public void onListItemClick(final ListView listView, final View view, final int position, final long id)
	{
		super.onListItemClick(listView, view, position, id);

		final SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
		final Cursor cursor = adapter.getCursor();
		if (cursor.moveToPosition(position))
		{
			final int synsetId = cursor.getColumnIndex(WordNetContract.Synsets.SYNSETID);
			final int posId = cursor.getColumnIndex(WordNetContract.PosTypes.POSNAME);
			final int casedId = cursor.getColumnIndex(WordNetContract.CasedWords.CASED);

			final SensePointer sense = new SensePointer();
			sense.setSynset(cursor.isNull(synsetId) ? null : cursor.getLong(synsetId), cursor.getString(posId));
			sense.setWord(this.word.wordid, this.word.lemma, cursor.getString(casedId));

			// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
			if (this.listener != null)
			{
				this.listener.onItemSelected(sense);
			}
		}

		cursor.close();
	}
}
