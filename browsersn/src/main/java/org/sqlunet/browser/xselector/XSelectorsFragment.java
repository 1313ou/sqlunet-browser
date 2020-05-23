/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.xselector;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;

import org.sqlunet.browser.R;
import org.sqlunet.browser.SqlunetViewModel;
import org.sqlunet.browser.xn.Settings;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.provider.XSqlUNetContract;
import org.sqlunet.provider.XSqlUNetContract.Words_FnWords_PbWords_VnWords;
import org.sqlunet.provider.XSqlUNetContract.Words_XNet_U;
import org.sqlunet.provider.XSqlUNetProvider;
import org.sqlunet.wordnet.provider.WordNetContract;
import org.sqlunet.wordnet.provider.WordNetProvider;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.app.local.ExpandableListFragment;
import androidx.lifecycle.ViewModelProvider;

/**
 * X selector fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class XSelectorsFragment extends ExpandableListFragment
{
	static private final String TAG = "XSelectorsF";

	/**
	 * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
	 */
	@FunctionalInterface
	public interface Listener
	{
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemSelected(XSelectorPointer pointer, String word, String cased, String pos);
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
	 * id column
	 */
	static private final String GROUPID_COLUMN = "_id";

	/**
	 * Name column
	 */
	static private final String GROUPNAME_COLUMN = "xn";

	/**
	 * Icon column
	 */
	static private final String GROUPICON_COLUMN = "xicon";

	/**
	 * WordNet id
	 */
	static public final int GROUPID_WORDNET = 1;

	/**
	 * SyntagNet id
	 */
	static public final int GROUPID_SYNTAGNET = 2;

	/**
	 * Source fields for groups
	 */
	static private final String[] groupFrom = {GROUPNAME_COLUMN, GROUPICON_COLUMN,};

	/**
	 * Target resource for groups
	 */
	static private final int[] groupTo = {R.id.xn, R.id.xicon,};

	/**
	 * Source fields
	 */
	static private final int[] childTo = { //
			R.id.wordid, //
			R.id.synsetid, //
			R.id.xid, //
			R.id.xmemberid, //
			R.id.xname, //
			R.id.xheader, //
			R.id.xinfo, //
			R.id.xdefinition, //
			R.id.xsourcestext, //
			R.id.xsources, //
			R.id.pm, //
	};

	/**
	 * Target resource
	 */
	static private final String[] childFrom = {Words_XNet_U.WORDID, //
			Words_XNet_U.SYNSETID, //
			Words_XNet_U.XID, //
			Words_XNet_U.XMEMBERID, //
			Words_XNet_U.XNAME, //
			Words_XNet_U.XHEADER, //
			Words_XNet_U.XINFO, //
			Words_XNet_U.XDEFINITION, //
			Words_XNet_U.SOURCES, //
			Words_XNet_U.SOURCES, //
			Words_XNet_U.SOURCES,};

	/**
	 * Xn group cursor
	 */
	@NonNull
	private final MatrixCursor xnCursor;

	/**
	 * WordNet group position
	 */
	private int groupWordNetPosition;

	/**
	 * SyntagNet group position
	 */
	private int groupSyntagNetPosition;

	/**
	 * The current activated item position.
	 */
	private int groupPosition;

	/**
	 * The fragment's current callback object, which is notified of list item clicks.
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
	 * Model
	 */
	private SqlunetViewModel wordIdFromWordModel;

	/**
	 * WordNet model
	 */
	private SqlunetViewModel wnFromWordIdModel;

	/**
	 * SyntagNet model
	 */
	private SqlunetViewModel snFromWordIdModel;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public XSelectorsFragment()
	{
		this.groupWordNetPosition = -1;
		this.groupSyntagNetPosition = -1;
		this.xnCursor = new MatrixCursor(new String[]{GROUPID_COLUMN, GROUPNAME_COLUMN, GROUPICON_COLUMN});
	}

	// C R E A T E

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// retain instance
		setRetainInstance(true);

		// args
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

		// fill groups
		int position = 0;
		int enable = Settings.getAllPref(getContext());

		if (Settings.Source.WORDNET.test(enable))
		{
			this.groupWordNetPosition = position++;
			this.xnCursor.addRow(new Object[]{GROUPID_WORDNET, "wordnet", Integer.toString(R.drawable.wordnet)});
		}
		if (Settings.Source.SYNTAGNET.test(enable))
		{
			this.groupSyntagNetPosition = position++;
			this.xnCursor.addRow(new Object[]{GROUPID_SYNTAGNET, "syntagnet", Integer.toString(R.drawable.syntagnet)});
		}
		this.groupPosition = position >= 0 ? 0 : -1;
		Log.d(XSelectorsFragment.TAG, "init position " + this.groupPosition + " " + this);
	}

	@Override
	public void onAttach(@NonNull final Context context)
	{
		super.onAttach(context);
		makeModels();
	}

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		this.wordIdFromWordModel = new ViewModelProvider(this).get("xselectors.wordid(word)", SqlunetViewModel.class);
		this.wordIdFromWordModel.getData().observe(this, unusedCursor -> initialize());

		this.wnFromWordIdModel = new ViewModelProvider(this).get("xselectors.wn(wordid)", SqlunetViewModel.class);
		this.wnFromWordIdModel.getData().observe(this, cursor -> {

			if (cursor != null)
			{
				// dump(cursor);

				// pass on to list adapter
				final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
				if (adapter != null && this.groupPosition >= 0)
				{
					adapter.setChildrenCursor(this.groupPosition, cursor);
				}
			}
			else
			{
				Log.i(XSelectorsFragment.TAG, "WN none");
			}
		});

		this.snFromWordIdModel = new ViewModelProvider(this).get("xselectors.sn(wordid)", SqlunetViewModel.class);
		this.snFromWordIdModel.getData().observe(this, cursor -> {

			if (cursor != null)
			{
				// dump(cursor);

				// pass on to list adapter
				final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
				if (adapter != null && this.groupPosition >= 0)
				{
					adapter.setChildrenCursor(this.groupPosition, cursor);
				}
			}
			else
			{
				Log.i(XSelectorsFragment.TAG, "VN none");
			}
		});
	}

	// V I E W

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_xselectors, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view0, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view0, savedInstanceState);

		// when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
		final ExpandableListView view = getListView();
		assert view != null;
		view.setChoiceMode(this.activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);

		// restore the previously serialized activated item position, if any
		if (savedInstanceState != null && savedInstanceState.containsKey(XSelectorsFragment.STATE_ACTIVATED_SELECTOR))
		{
			this.groupPosition = savedInstanceState.getInt(XSelectorsFragment.STATE_ACTIVATED_SELECTOR);
			Log.d(XSelectorsFragment.TAG, "restored position " + this.groupPosition + " " + this);
		}
	}

	// L I F E C Y C L E   E V E N T S

	@Override
	public void onActivityCreated(@Nullable final Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// load the contents (once activity is available)
		load();
	}

	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		super.onSaveInstanceState(outState);

		// serialize and persist the activated item position.
		outState.putInt(XSelectorsFragment.STATE_ACTIVATED_SELECTOR, this.groupPosition);
		Log.d(XSelectorsFragment.TAG, "saved position " + this.groupPosition + " " + this);
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
	 * Load id from word
	 */
	private void load()
	{
		// load the contents
		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(Words_FnWords_PbWords_VnWords.CONTENT_URI_TABLE));
		final String[] projection = { //
				Words_FnWords_PbWords_VnWords.SYNSETID + " AS " + GROUPID_COLUMN, //
				Words_FnWords_PbWords_VnWords.WORDID, //
				Words_FnWords_PbWords_VnWords.FNWORDID, //
				Words_FnWords_PbWords_VnWords.VNWORDID, //
				Words_FnWords_PbWords_VnWords.PBWORDID, //
		};
		final String selection = XSqlUNetContract.WORD + '.' + Words_FnWords_PbWords_VnWords.LEMMA + " = ?";
		final String[] selectionArgs = {XSelectorsFragment.this.word};
		final String sortOrder = XSqlUNetContract.POS + '.' + Words_FnWords_PbWords_VnWords.POS + ',' + Words_FnWords_PbWords_VnWords.SENSENUM;
		this.wordIdFromWordModel.loadData(uri, projection, selection, selectionArgs, sortOrder, this::xselectorsPostProcess);
	}

	/**
	 * Read wordId from cursor
	 *
	 * @param cursor cursor
	 */
	private void xselectorsPostProcess(@NonNull final Cursor cursor)
	{
		// store source
		if (cursor.moveToFirst())
		{
			final int idWordId = cursor.getColumnIndex(Words_FnWords_PbWords_VnWords.WORDID);
			XSelectorsFragment.this.wordId = cursor.getLong(idWordId);
		}
	}

	/**
	 * Initialize adapter
	 */
	private void initialize()
	{
		// adapter
		final ExpandableListAdapter adapter = new SimpleCursorTreeAdapter(requireContext(), this.xnCursor, R.layout.item_group_xselector, groupFrom, groupTo, R.layout.item_xselector, childFrom, childTo)
		{
			@Nullable
			@Override
			protected Cursor getChildrenCursor(@NonNull Cursor groupCursor)
			{
				Log.d(XSelectorsFragment.TAG, "getChildrenCursor(cursor)");

				// given the group, return a cursor for all the children within that group
				// int groupPosition = groupCursor.getPosition();
				int groupId = groupCursor.getInt(groupCursor.getColumnIndex(GROUPID_COLUMN));
				// String groupName = groupCursor.getString(groupCursor.getColumnIndex(GROUPNAME_COLUMN));
				// Log.d(XSelectorsFragment.TAG, "group " + groupPosition + ' ' + groupName);

				// load
				startChildLoader(groupId);
				return null; // set later when loader completes
			}

			@Override
			public boolean isChildSelectable(int groupPosition, int childPosition)
			{
				return true;
			}

			@Override
			protected void setViewImage(@NonNull ImageView v, @NonNull String value)
			{
				switch (v.getId())
				{
					case R.id.xsources:
						final String[] fields = value.split(",");
						for (String field : fields)
						{
							switch (field)
							{
								case "wn": //
									v.setImageResource(R.drawable.wordnet);
									return;
								case "sn": //
									v.setImageResource(R.drawable.syntagnet);
									return;
							}
						}
						v.setImageDrawable(null);
						// v.setVisibility(View.GONE);
						break;

					case R.id.pm:
						final String[] fields2 = value.split(",");
						for (String field2 : fields2)
						{
							if (field2.startsWith("pm")) //
							{
								v.setImageResource(R.drawable.predicatematrix);
								v.setVisibility(View.VISIBLE);
								return;
							}
						}
						v.setImageDrawable(null);
						v.setVisibility(View.GONE);
						break;

					default:
						super.setViewImage(v, value);
						break;
				}
			}
		};
		setListAdapter(adapter);

		// expand (triggers data loading)
		Log.d(XSelectorsFragment.TAG, "expand position " + this.groupPosition + " " + this);
		expand(this.groupPosition);
	}

	/**
	 * Start child loader for
	 *
	 * @param groupId group id
	 */
	private void startChildLoader(int groupId)
	{
		Log.d(XSelectorsFragment.TAG, "Invoking startChildLoader() for groupId=" + groupId);
		switch (groupId)
		{
			case GROUPID_WORDNET:
				loadWn(this.wordId);
				break;
			case GROUPID_SYNTAGNET:
				loadSn(this.wordId);
				break;
			default:
				break;
		}
	}

	// L O A D

	/**
	 * Load WordNet data
	 *
	 * @param wordId word id
	 */
	private void loadWn(final long wordId)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CONTENT_URI_TABLE));
		final String[] projection = { //
				"'wn' AS " + Words_XNet_U.SOURCES, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.WORDID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID + " AS " + Words_XNet_U.XID, //
				"NULL AS " + Words_XNet_U.XCLASSID, //
				"NULL AS " + Words_XNet_U.XMEMBERID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEMMA + "|| '.' ||" + WordNetContract.POS + '.' + WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POS + " AS " + Words_XNet_U.XNAME, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXDOMAIN + " AS " + Words_XNet_U.XHEADER, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSEKEY + " AS " + Words_XNet_U.XINFO, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID + " AS _id"};
		final String selection = WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};
		this.wnFromWordIdModel.loadData(uri, projection, selection, selectionArgs, null, null);
	}

	/**
	 * Load SyntagNet data
	 *
	 * @param wordId word id
	 */
	private void loadSn(final long wordId)
	{
//		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(XSqlUNetContract.Words_VnWords_VnClasses_U.CONTENT_URI_TABLE));
//		final String[] projection = { //
//				XSqlUNetContract.Words_VnWords_VnClasses_U.WORDID, //
//				XSqlUNetContract.Words_VnWords_VnClasses_U.SYNSETID, //
//				XSqlUNetContract.Words_VnWords_VnClasses_U.CLASSID + " AS " + Words_XNet_U.XID, //
//				XSqlUNetContract.Words_VnWords_VnClasses_U.CLASSID + " AS " + Words_XNet_U.XCLASSID, //
//				"NULL AS " + Words_XNet_U.XMEMBERID, //
//				"TRIM(" + XSqlUNetContract.Words_VnWords_VnClasses_U.CLASS + ",'-.0123456789')" + " AS " + Words_XNet_U.XNAME, //
//				XSqlUNetContract.Words_VnWords_VnClasses_U.CLASS + " AS " + Words_XNet_U.XHEADER, //
//				XSqlUNetContract.Words_VnWords_VnClasses_U.CLASSTAG + " AS " + Words_XNet_U.XINFO, //
//				XSqlUNetContract.Words_VnWords_VnClasses_U.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, //
//				"rowid AS _id",};
//		final String selection = XSqlUNetContract.Words_VnWords_VnClasses_U.WORDID + " = ?";
//		final String[] selectionArgs = {Long.toString(wordId)};
//		final String sortOrder = XSqlUNetContract.Words_VnWords_VnClasses_U.CLASSID;
//		this.snFromWordIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, null);
	}

	// S E L E C T I O N   L I S T E N E R

	@Override
	public void onGroupExpand(int groupPosition)
	{
		super.onGroupExpand(groupPosition);

		this.groupPosition = groupPosition;
		Log.d(XSelectorsFragment.TAG, "select " + this.groupPosition);
	}

	@Override
	public void onGroupCollapse(final int groupPosition)
	{
		super.onGroupCollapse(groupPosition);

		//final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
		//assert adapter != null;
		//adapter.setChildrenCursor(groupPosition, null);
		Log.d(XSelectorsFragment.TAG, "collapse " + this.groupPosition);
		//this.groupPosition = -1;
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

	@SuppressWarnings({"boxing", "SameReturnValue"})
	@Override
	public boolean onChildClick(@NonNull final ExpandableListView listView, final View view, final int groupPosition, final int childPosition, final long id)
	{
		super.onChildClick(listView, view, groupPosition, childPosition, id);

		if (this.listener != null)
		{
			//Log.d(XSelectorsFragment.TAG, "CLICK on group=" + groupPosition + " child=" + childPosition + " id=" + id);
			int index = listView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
			listView.setItemChecked(index, true);

			final CursorTreeAdapter adapter = (SimpleCursorTreeAdapter) getListAdapter();
			assert adapter != null;
			final Cursor cursor = adapter.getChild(groupPosition, childPosition);
			if (!cursor.isAfterLast())
			{
				// column indices
				final int idSynsetId = cursor.getColumnIndex(Words_XNet_U.SYNSETID);
				final int idXId = cursor.getColumnIndex(Words_XNet_U.XID);
				final int idXClassId = cursor.getColumnIndex(Words_XNet_U.XCLASSID);
				final int idXMemberId = cursor.getColumnIndex(Words_XNet_U.XMEMBERID);
				final int idXSources = cursor.getColumnIndex(Words_XNet_U.SOURCES);
				// final int idWordId = cursor.getColumnIndex(Words_XNet_U.WORDID);

				// data
				final long wordId = this.wordId;
				final String lemma = this.word;
				final String cased = this.word;
				final long synsetId = cursor.isNull(idSynsetId) ? 0 : cursor.getLong(idSynsetId);
				final String pos = synsetIdToPos(synsetId);
				final long xId = cursor.isNull(idXId) ? 0 : cursor.getLong(idXId);
				final long xClassId = cursor.isNull(idXClassId) ? 0 : cursor.getLong(idXClassId);
				final long xMemberId = cursor.isNull(idXMemberId) ? 0 : cursor.getLong(idXMemberId);
				final String xSources = cursor.getString(idXSources);
				final long xMask = XSelectorPointer.getMask(xSources);

				int groupId = -1;
				if (this.groupWordNetPosition != -1 && groupPosition == this.groupWordNetPosition)
				{
					groupId = GROUPID_WORDNET;
				}
				else if (this.groupSyntagNetPosition != -1 && groupPosition == this.groupSyntagNetPosition)
				{
					groupId = GROUPID_SYNTAGNET;
				}

				// pointer
				final XSelectorPointer pointer = new XSelectorPointer(synsetId, wordId, xId, xClassId, xMemberId, xSources, xMask, groupId);
				Log.d(XSelectorsFragment.TAG, "pointer=" + pointer);

				// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
				this.listener.onItemSelected(pointer, lemma, cased, pos);
			}
			// cursor ownership is transferred  to adapter, so do not call
			// cursor.close();
		}
		return true;
	}

	// E X P A N D

	/**
	 * Expand section
	 */
	private void expand(int groupPosition)
	{
		final ExpandableListView view = getExpandableListView();
		assert view != null;
		view.expandGroup(groupPosition);
	}

	/**
	 * Expand all
	 */
	@SuppressWarnings("unused")
	private void expandAll()
	{
		final ExpandableListView view = getExpandableListView();
		assert view != null;
		int count = this.xnCursor.getCount();
		for (int position = 0; position < count; position++)
		{
			view.expandGroup(position);
		}
	}

	// H E L P E R

	/**
	 * Extract pos from synset id number
	 *
	 * @param synsetId synset id
	 * @return pos
	 */
	@Nullable
	private String synsetIdToPos(@Nullable final Long synsetId)
	{
		if (synsetId == null)
		{
			return null;
		}
		int p = (int) Math.floor(synsetId / 100000000F);
		switch (p)
		{
			case 1:
				return "n";
			case 2:
				return "v";
			case 3:
				return "a";
			case 4:
				return "r";
			default:
				return null;
		}
	}

	/**
	 * Dump utility
	 */
	@SuppressWarnings("unused")
	static public void dump(@NonNull final Cursor cursor)
	{
		if (cursor.moveToFirst())
		{
			final int idWordId = cursor.getColumnIndex(Words_XNet_U.WORDID);
			final int idSynsetId = cursor.getColumnIndex(Words_XNet_U.SYNSETID);
			final int idXId = cursor.getColumnIndex(Words_XNet_U.XID);
			final int idXName = cursor.getColumnIndex(Words_XNet_U.XNAME);
			final int idXHeader = cursor.getColumnIndex(Words_XNet_U.XHEADER);
			final int idXInfo = cursor.getColumnIndex(Words_XNet_U.XINFO);
			final int idDefinition = cursor.getColumnIndex(Words_XNet_U.XDEFINITION);
			final int idSources = cursor.getColumnIndex(Words_XNet_U.SOURCES);

			do
			{
				long wordId = cursor.getLong(idWordId);
				long synsetId = cursor.isNull(idSynsetId) ? 0 : cursor.getLong(idSynsetId);
				long xId = cursor.isNull(idXId) ? 0 : cursor.getLong(idXId);
				String xName = cursor.isNull(idXName) ? null : cursor.getString(idXName);
				String xHeader = cursor.isNull(idXHeader) ? null : cursor.getString(idXHeader);
				String xInfo = cursor.isNull(idXInfo) ? null : cursor.getString(idXInfo);
				String definition = cursor.isNull(idXInfo) ? null : cursor.getString(idDefinition);
				String sources = cursor.isNull(idSources) ? "" : //
						cursor.getString(idSources);
				Log.i("xloader", "sources=" + sources +  //
						" wordid=" + wordId +  //
						" synsetid=" + synsetId +  //
						" xid=" + xId +  //
						" name=" + xName +  //
						" header=" + xHeader +  //
						" info=" + xInfo +  //
						" definition=" + definition);
			}
			while (cursor.moveToNext());
			cursor.moveToFirst();
		}
	}
}