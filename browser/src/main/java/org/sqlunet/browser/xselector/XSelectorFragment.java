package org.sqlunet.browser.xselector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.local.app.ExpandableListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.R;
import org.sqlunet.browser.xselector.XLoader.FnLoaderCallbacks;
import org.sqlunet.browser.xselector.XLoader.PbLoaderCallbacks;
import org.sqlunet.browser.xselector.XLoader.VnLoaderCallbacks;
import org.sqlunet.browser.xselector.XLoader.WnLoaderCallbacks;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.provider.XSqlUNetContract.Words_FnWords_PbWords_VnWords;
import org.sqlunet.provider.XSqlUNetContract.Words_XNet_U;
import org.sqlunet.wordnet.WordPointer;
import org.sqlunet.wordnet.browser.SenseFragment;

/**
 * A list fragment representing a list of synsets. This fragment also supports tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a {@link SenseFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Listener} interface.
 *
 * @author Bernard Bou
 */
public class XSelectorFragment extends ExpandableListFragment
{
	private static final String TAG = "XSelectorFragment"; //$NON-NLS-1$

	private static final int[] groupTo = new int[]{ //
			R.id.xn, //
	};
	private static final String[] groupFrom = new String[]{"xn",}; //$NON-NLS-1$

	private static final int[] childTo = new int[]{ //
			R.id.wordid, //
			R.id.synsetid, //
			R.id.xid, //
			R.id.xname, //
			R.id.xheader, //
			R.id.xinfo, //
			R.id.xdefinition, //
			R.id.xsourcestext, //
			R.id.xsources, //
			R.id.pm, //
	};
	private static final String[] childFrom = new String[]{Words_XNet_U.WORDID, //
			Words_XNet_U.SYNSETID, //
			Words_XNet_U.XID, //
			Words_XNet_U.XNAME, //
			Words_XNet_U.XHEADER, //
			Words_XNet_U.XINFO, //
			Words_XNet_U.XDEFINITION, //
			Words_XNet_U.SOURCES, //
			Words_XNet_U.SOURCES, //
			Words_XNet_U.SOURCES,};

	/**
	 * The serialization (saved instance state) Bundle key representing the activated item position. Only used on tablets.
	 */
	private static final String ACTIVATED_POSITION_NAME = "activated_position"; //$NON-NLS-1$

	/**
	 * Xn group cursor
	 */
	private final MatrixCursor xnCursor;

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
		void onItemSelected(XPointer pointer);
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
	@SuppressWarnings("boxing")
	public XSelectorFragment()
	{
		this.xnCursor = new MatrixCursor(new String[]{"_id", "xn", "loader"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.xnCursor.addRow(new Object[]{0, "wordnet", 1111}); //$NON-NLS-1$
		this.xnCursor.addRow(new Object[]{1, "verbnet", 2222}); //$NON-NLS-1$
		this.xnCursor.addRow(new Object[]{2, "propbank", 3333}); //$NON-NLS-1$
		this.xnCursor.addRow(new Object[]{3, "framenet", 4444}); //$NON-NLS-1$
	}

	private LoaderCallbacks<Cursor> getWnCallbacks(final long wordid, final int groupPosition)
	{
		return new WnLoaderCallbacks(getActivity(), wordid)
		{
			@Override
			public void onLoadFinished(final Loader<Cursor> loader0, final Cursor cursor)
			{
				if (cursor != null)
				{
					XLoader.dump(cursor);

					// pass on to list adapter
					((SimpleCursorTreeAdapter) getListAdapter()).setChildrenCursor(groupPosition, cursor);
				} else
				{
					Log.i(TAG, "WN none"); //$NON-NLS-1$
				}
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> arg0)
			{
				((SimpleCursorTreeAdapter) getListAdapter()).setChildrenCursor(groupPosition, null);
			}
		};
	}

	private LoaderCallbacks<Cursor> getVnCallbacks(final long wordid, final int groupPosition)
	{
		return new VnLoaderCallbacks(getActivity(), wordid)
		{
			@Override
			public void onLoadFinished(final Loader<Cursor> loader0, final Cursor cursor)
			{
				if (cursor != null)
				{
					XLoader.dump(cursor);

					// pass on to list adapter
					((SimpleCursorTreeAdapter) getListAdapter()).setChildrenCursor(groupPosition, cursor);
				} else
				{
					Log.i(TAG, "VN none"); //$NON-NLS-1$
				}
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> arg0)
			{
				((SimpleCursorTreeAdapter) getListAdapter()).setChildrenCursor(groupPosition, null);
			}
		};
	}

	private LoaderCallbacks<Cursor> getPbCallbacks(final long wordid, final int groupPosition)
	{
		return new PbLoaderCallbacks(getActivity(), wordid)
		{
			@Override
			public void onLoadFinished(final Loader<Cursor> loader0, final Cursor cursor)
			{
				if (cursor != null)
				{
					XLoader.dump(cursor);

					// pass on to list adapter
					((SimpleCursorTreeAdapter) getListAdapter()).setChildrenCursor(groupPosition, cursor);
				} else
				{
					Log.i(TAG, "PB none"); //$NON-NLS-1$
				}
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> arg0)
			{
				((SimpleCursorTreeAdapter) getListAdapter()).setChildrenCursor(groupPosition, null);
			}
		};
	}

	private LoaderCallbacks<Cursor> getFnCallbacks(final long wordid, final int groupPosition)
	{
		return new FnLoaderCallbacks(getActivity(), wordid)
		{
			public void onLoadFinished(final Loader<Cursor> loader0, final Cursor cursor)
			{
				if (cursor != null)
				{
					XLoader.dump(cursor);

					// pass on to list adapter
					((SimpleCursorTreeAdapter) getListAdapter()).setChildrenCursor(groupPosition, cursor);
				} else
				{
					Log.i(TAG, "FN none"); //$NON-NLS-1$
				}
			}

			public void onLoaderReset(final Loader<Cursor> arg0)
			{
				((SimpleCursorTreeAdapter) getListAdapter()).setChildrenCursor(groupPosition, null);
			}
		};
	}

	private void expandWordNet()
	{
		ExpandableListView listView = getExpandableListView();
		listView.expandGroup(0);
	}

	@SuppressWarnings("unused")
	private void expandAll()
	{
		ExpandableListView listView = getExpandableListView();
		int count = this.xnCursor.getCount();
		for (int position = 0; position < count; position++)
			listView.expandGroup(position);
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

		// load the contents
		load();
	}

	private void load()
	{
		// load the contents
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public Loader<Cursor> onCreateLoader(final int id, final Bundle args)
			{
				final Uri uri = Uri.parse(Words_FnWords_PbWords_VnWords.CONTENT_URI);
				final String[] projection = new String[]{ //
						Words_FnWords_PbWords_VnWords.SYNSETID + " AS _id", // //$NON-NLS-1$
						Words_FnWords_PbWords_VnWords.WORDID, //
						Words_FnWords_PbWords_VnWords.FNWORDID, //
						Words_FnWords_PbWords_VnWords.VNWORDID, //
						Words_FnWords_PbWords_VnWords.PBWORDID, //
				};
				final String selection = "w." + Words_FnWords_PbWords_VnWords.LEMMA + " = ?"; //$NON-NLS-1$ //$NON-NLS-2$
				final String[] selectionArgs = new String[]{XSelectorFragment.this.queryWord};
				final String sortOrder = "y." + Words_FnWords_PbWords_VnWords.POS + ',' + Words_FnWords_PbWords_VnWords.SENSENUM; //$NON-NLS-1$
				return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@SuppressWarnings("synthetic-access")
			@Override
			public void onLoadFinished(final Loader<Cursor> loader0, final Cursor cursor)
			{
				// store source result
				if (cursor.moveToFirst())
				{
					XSelectorFragment.this.word = new WordPointer();
					XSelectorFragment.this.word.lemma = XSelectorFragment.this.queryWord;
					final int idWordId = cursor.getColumnIndex(Words_FnWords_PbWords_VnWords.WORDID);
					XSelectorFragment.this.word.wordid = cursor.getLong(idWordId);

					load(XSelectorFragment.this.word.wordid);
				}
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> arg0)
			{
				//
			}
		});
	}

	private void load(final long wordid)
	{
		/*
	  Adapter
	 */
		ExpandableListAdapter adapter = new SimpleCursorTreeAdapter(getActivity(), this.xnCursor, R.layout.item_group_xselector, groupFrom, groupTo, R.layout.item_xselector, childFrom, childTo)
		{
			@Override
			protected void setViewImage(ImageView v, String value)
			{
				switch (v.getId())
				{
					case R.id.xsources:
						final String[] fields = value.split(","); //$NON-NLS-1$
						for (String field : fields)
						{
							switch (field)
							{
								case "wn": //$NON-NLS-1$
									v.setImageResource(R.drawable.wordnet);
									return;
								case "vn": //$NON-NLS-1$
									v.setImageResource(R.drawable.verbnet);
									return;
								case "pb": //$NON-NLS-1$
									v.setImageResource(R.drawable.propbank);
									return;
								case "fn": //$NON-NLS-1$
									v.setImageResource(R.drawable.framenet);
									return;
							}
						}
						v.setImageDrawable(null);
						// v.setVisibility(View.GONE);
						break;

					case R.id.pm:
						final String[] fields2 = value.split(","); //$NON-NLS-1$
						for (String field2 : fields2)
						{
							if (field2.startsWith("pm")) //$NON-NLS-1$
							{
								v.setImageResource(R.drawable.predicatematrix);
								v.setVisibility(View.VISIBLE);
								return;
							}
						}
						v.setImageDrawable(null);
						v.setVisibility(View.GONE);
						break;
				}
			}

			@SuppressWarnings("synthetic-access")
			@Override
			protected Cursor getChildrenCursor(Cursor groupCursor)
			{
				Activity activity = getActivity();
				if (activity == null)
					return null;

				// given the group, we return a cursor for all the children within that group
				int groupPos = groupCursor.getPosition();
				String groupName = groupCursor.getString(groupCursor.getColumnIndex("xn")); //$NON-NLS-1$
				int loaderid = groupCursor.getInt(groupCursor.getColumnIndex("loader")); //$NON-NLS-1$
				Log.d(TAG, "group " + groupPos + ' ' + groupName + " loader=" + loaderid); //$NON-NLS-1$ //$NON-NLS-2$

				LoaderCallbacks<Cursor> callbacks = null;
				switch (groupPos)
				{
					case 0:
						callbacks = getWnCallbacks(wordid, groupPos);
						break;
					case 1:
						callbacks = getVnCallbacks(wordid, groupPos);
						break;
					case 2:
						callbacks = getPbCallbacks(wordid, groupPos);
						break;
					case 3:
						callbacks = getFnCallbacks(wordid, groupPos);
						break;
				}

				Loader<Cursor> loader1 = activity.getLoaderManager().getLoader(loaderid);
				if (loader1 != null && !loader1.isReset())
				{
					activity.getLoaderManager().restartLoader(loaderid, null, callbacks);
				} else
				{
					activity.getLoaderManager().initLoader(loaderid, null, callbacks);
				}

				return null;
			}

			@Override
			public boolean isChildSelectable(int groupPosition, int childPosition)
			{
				return true;
			}
		};
		setListAdapter(adapter);

		// expand (triggers data loading)
		expandWordNet();
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
		return inflater.inflate(R.layout.fragment_xselector, container);
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
		if (savedInstanceState != null && savedInstanceState.containsKey(XSelectorFragment.ACTIVATED_POSITION_NAME))
		{
			final int position = savedInstanceState.getInt(XSelectorFragment.ACTIVATED_POSITION_NAME);
			if (position == AdapterView.INVALID_POSITION)
			{
				getListView().setItemChecked(this.activatedPosition, false);
			} else
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
			outState.putInt(XSelectorFragment.ACTIVATED_POSITION_NAME, this.activatedPosition);
		}
	}

	// A T T A C H / D E T A C H

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

	// C L I C K E V E N T L I S T E N

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(@SuppressWarnings("SameParameterValue") final boolean activateOnItemClick)
	{
		// when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
		getListView().setChoiceMode(activateOnItemClick ?
				AbsListView.CHOICE_MODE_SINGLE :
				AbsListView.CHOICE_MODE_NONE);
	}

	@SuppressWarnings("boxing")
	@Override
	public boolean onChildClick(final ExpandableListView listView, final View view, final int groupPosition, final int childPosition, final long id)
	{
		super.onChildClick(listView, view, groupPosition, childPosition, id);
		Log.d(TAG, "click on group=" + groupPosition + " child=" + childPosition + " id=" + id); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		int index = listView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
		listView.setItemChecked(index, true);
		view.setSelected(true);

		final SimpleCursorTreeAdapter adapter1 = (SimpleCursorTreeAdapter) getListAdapter();
		final Cursor cursor = adapter1.getChild(groupPosition, childPosition);
		if (!cursor.isAfterLast())
		{
			// column indices
			final int idSynsetId = cursor.getColumnIndex(Words_XNet_U.SYNSETID);
			final int idXId = cursor.getColumnIndex(Words_XNet_U.XID);
			final int idXClassId = cursor.getColumnIndex(Words_XNet_U.XCLASSID);
			final int idXInstanceId = cursor.getColumnIndex(Words_XNet_U.XINSTANCEID);
			final int idXSources = cursor.getColumnIndex(Words_XNet_U.SOURCES);
			// final int idWordId = cursor.getColumnIndex(Words_XNet_U.WORDID);

			// data
			final long wordid = this.word.wordid;
			final String lemma = this.word.lemma;
			final String cased = this.word.lemma;
			final Long synsetid = cursor.isNull(idSynsetId) ? null : cursor.getLong(idSynsetId);
			final String pos = synsetidToPos(synsetid);
			final Long xid = cursor.isNull(idXId) ? null : cursor.getLong(idXId);
			final Long xclassid = cursor.isNull(idXClassId) ? null : cursor.getLong(idXClassId);
			final Long xinstanceid = cursor.isNull(idXInstanceId) ?
					null :
					cursor.getLong(idXInstanceId);
			final String sources = cursor.getString(idXSources);

			// pointer
			final XPointer pointer = new XPointer();
			pointer.setWord(wordid, lemma, cased);
			pointer.setSynset(synsetid, pos);
			pointer.setXid(xid);
			pointer.setXclassid(xclassid);
			pointer.setXinstanceid(xinstanceid);
			pointer.setXsources(sources);
			Log.d(TAG, "pointer=" + pointer); //$NON-NLS-1$

			// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
			if (this.listener != null)
			{
				this.listener.onItemSelected(pointer);
			}
		}
		// cursor.close();
		return true;
	}

	@SuppressWarnings("boxing")
	private String synsetidToPos(final Long synsetid)
	{
		if (synsetid == null)
			return null;
		int p = (int) Math.floor(synsetid / 100000000F);
		switch (p)
		{
			case 1:
				return "n"; //$NON-NLS-1$
			case 2:
				return "v"; //$NON-NLS-1$
			case 3:
				return "a"; //$NON-NLS-1$
			case 4:
				return "r"; //$NON-NLS-1$
			default:
				return null;
		}
	}
}
