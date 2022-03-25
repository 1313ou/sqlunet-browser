/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.xselector;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;

import org.sqlunet.browser.SqlunetViewModel;
import org.sqlunet.browser.vn.R;
import org.sqlunet.browser.vn.Settings;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.provider.XSqlUNetContract;
import org.sqlunet.provider.XSqlUNetContract.Words_PbWords_VnWords;
import org.sqlunet.provider.XSqlUNetContract.Words_XNet;
import org.sqlunet.provider.XSqlUNetProvider;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.app.local.ExpandableListFragment;
import androidx.lifecycle.LifecycleOwner;
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
	 * The (saved instance state) key representing the groups state.
	 */
	static private final String STATE_GROUPS = "groups_state";

	/**
	 * Activate on click flag
	 */
	private boolean activateOnItemClick = true;

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
	 * First expanded group
	 */
	static public final int GROUP_POSITION_INITIAL = 0;

	/**
	 * VerbNet position index
	 */
	static public final int GROUPINDEX_VERBNET = 0;

	/**
	 * Propbank position index
	 */
	static public final int GROUPINDEX_PROPBANK = 1;

	/**
	 * VerbNet id for loader
	 */
	static public final int GROUPID_VERBNET = 2;

	/**
	 * Propbank id for loader
	 */
	static public final int GROUPID_PROPBANK = 3;

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
	};

	/**
	 * Target resource
	 */
	static private final String[] childFrom = {Words_XNet.WORDID, //
			Words_XNet.SYNSETID, //
			Words_XNet.XID, //
			Words_XNet.XMEMBERID, //
			Words_XNet.XNAME, //
			Words_XNet.XHEADER, //
			Words_XNet.XINFO, //
			Words_XNet.XDEFINITION,};

	/**
	 * Xn group cursor
	 */
	@NonNull
	private final MatrixCursor xnCursor;

	/**
	 * Group positions
	 */
	private int[] groupPositions;

	/**
	 * The current restored group state.
	 */
	@Nullable
	private Integer restoredGroupState;

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
	 * Id view model
	 */
	private SqlunetViewModel wordIdFromWordModel;

	/**
	 * VerbNet model
	 */
	private SqlunetViewModel vnFromWordIdModel;

	/**
	 * PropBank model
	 */
	private SqlunetViewModel pbFromWordIdModel;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public XSelectorsFragment()
	{
		Log.d(TAG, "lifecycle: Constructor (0) " + this);
		this.xnCursor = new MatrixCursor(new String[]{GROUPID_COLUMN, GROUPNAME_COLUMN, GROUPICON_COLUMN});
	}

	// L I F E C Y C L E

	// --activate--

	//	@Override
	//	public void onAttach(@NonNull final Context context)
	//	{
	//		super.onAttach(context);
	//		Log.d(TAG, "lifecycle: onAttach (1) " + this);
	//	}

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "lifecycle: onCreate (2) " + this);
		//noinspection deprecation
		this.setRetainInstance(false); // default

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

		// group cursor populated
		Log.d(TAG, "make groupCursor");
		this.groupPositions = populateGroupCursor(requireContext(), this.xnCursor);
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		Log.d(TAG, "lifecycle: onCreateView (3) " + this);
		return inflater.inflate(R.layout.fragment_xselectors, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view0, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view0, savedInstanceState);
		Log.d(TAG, "lifecycle: onViewCreated (4) " + this);

		// when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
		final ExpandableListView view = getListView();
		assert view != null;
		view.setChoiceMode(this.activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);

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
		// final MutableLiveData<Cursor> idLiveData = wordIdFromWordModel.getMutableData();
		// final Cursor idCursor = idLiveData.getValue();
		// if (idCursor != null && !idCursor.isClosed())
		// {
		//		idLiveData.setValue(idCursor);
		// }
		// else
		load();
	}

	// --deactivate--

	//	@Override
	//	public void onStop()
	//	{
	//		super.onStop();
	//		Log.d(TAG, "lifecycle: onStop(-4) " + this);
	//	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		Log.d(TAG, "lifecycle: onDestroyView (-3) " + this);
		resetAdapter();
	}

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

	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		Log.d(TAG, "lifecycle: onSaveInstanceState (2) " + this);
		super.onSaveInstanceState(outState);

		// serialize and persist the activated group state
		int groupCount = this.xnCursor.getCount();
		int groupState = 0;
		final ExpandableListView expandableListView = getExpandableListView();
		if (expandableListView != null)
		{
			for (int i = 0; i < groupCount; i++)
			{
				if (expandableListView.isGroupExpanded(i))
				{
					groupState |= (1 << i);
				}
			}
		}
		outState.putInt(STATE_GROUPS, groupState);
		Log.d(TAG, "save group states " + Integer.toHexString(groupState) + " " + this);
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState)
	{
		super.onViewStateRestored(savedInstanceState);
		Log.d(TAG, "lifecycle: onViewStateRestored " + this);
		this.restoredGroupState = savedInstanceState != null ? savedInstanceState.getInt(STATE_GROUPS) : null;
	}

	// H E L P E R S

	/**
	 * Populate group cursor. Requires context to be available
	 */
	@NonNull
	private static int[] populateGroupCursor(@NonNull final Context context, @NonNull final MatrixCursor cursor)
	{
		// fill groups
		int position = 0;
		int enable = Settings.getAllPref(context);

		int[] groupPositions = new int[]{AdapterView.INVALID_POSITION, AdapterView.INVALID_POSITION};
		if (Settings.Source.VERBNET.test(enable))
		{
			groupPositions[GROUPINDEX_VERBNET] = position++;
			cursor.addRow(new Object[]{GROUPID_VERBNET, "verbnet", Integer.toString(R.drawable.verbnet)});
		}
		if (Settings.Source.PROPBANK.test(enable))
		{
			//noinspection UnusedAssignment
			groupPositions[GROUPINDEX_PROPBANK] = position++;
			cursor.addRow(new Object[]{GROUPID_PROPBANK, "propbank", Integer.toString(R.drawable.propbank)});
		}
		return groupPositions;
	}

	/**
	 * Make adapter
	 */
	@NonNull
	private ExpandableListAdapter makeAdapter()
	{
		Log.d(TAG, "make adapter");

		// adapter
		this.xnCursor.moveToFirst();
		return new SimpleCursorTreeAdapter(requireContext(), this.xnCursor, R.layout.item_group_xselector, groupFrom, groupTo, R.layout.item_xselector, childFrom, childTo)
		{
			@Nullable
			@Override
			protected Cursor getChildrenCursor(@NonNull Cursor groupCursor)
			{
				Log.d(TAG, "getChildrenCursor(cursor) query groupId");

				// given the group, return a cursor for all the children within that group
				int groupId = groupCursor.getInt(groupCursor.getColumnIndex(GROUPID_COLUMN));
				// int groupPosition = groupCursor.getPosition();
				// String groupName = groupCursor.getString(groupCursor.getColumnIndex(GROUPNAME_COLUMN));
				// Log.d(TAG, "getChildrenCursor(cursor) group " + groupPosition + ' ' + groupName);

				// load
				startChildLoader(groupId);
				return null; // set later when loader completes
			}

			@Override
			public boolean isChildSelectable(int groupPosition, int childPosition)
			{
				return true;
			}

			@Nullable
			@Override
			public View getGroupView(final int groupPosition, final boolean isExpanded, final View convertView, final ViewGroup parent)
			{
				Cursor cursor = this.getCursor();
				if (cursor == null)
				{
					return null;
				}
				return super.getGroupView(groupPosition, isExpanded, convertView, parent);
			}
		};
	}

	/**
	 * Reset adapter. (Otherwise stale cursors)
	 */
	public void resetAdapter()
	{
		CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
		if (adapter != null)
		{
			for (int i = 0; i < adapter.getGroupCount(); i++)
			{
				adapter.setChildrenCursor(i, null);
			}
			//adapter.setGroupCursor(null);
		}
		//setListAdapter(null);
	}

	/**
	 * Initialize groups
	 */
	private void initializeGroups()
	{
		// expand (triggers data loading)
		Log.d(TAG, "expand group " + GROUP_POSITION_INITIAL + " " + this);
		expand(GROUP_POSITION_INITIAL);
	}

	/**
	 * Restore groups
	 */
	public void restoreGroups(@Nullable Integer groupState)
	{
		if (groupState == null)
		{
			return;
		}
		final Handler handler = new Handler(Looper.getMainLooper());
		Log.d(TAG, "saved position " + Integer.toHexString(groupState) + " " + this);
		int groupCount = this.xnCursor.getCount();
		for (int i = 0; i < groupCount; i++)
		{
			if ((groupState & (1 << i)) != 0)
			{
				// expand(i);

				int finalI = i;
				//requireActivity().runOnUiThread(() -> expand(finalI));
				handler.postDelayed(() -> expand(finalI), 1500);
			}
		}
	}

	// V I E W M O D E L S

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		final LifecycleOwner owner = getViewLifecycleOwner();
		this.wordIdFromWordModel = new ViewModelProvider(this).get("vn:xselectors.wordid(word)", SqlunetViewModel.class);
		this.wordIdFromWordModel.getData().observe(owner, unusedCursor -> {

			unusedCursor.close();
			this.wordIdFromWordModel.getData().removeObservers(this);

			final ExpandableListAdapter adapter = makeAdapter();
			setListAdapter(adapter);

			if (this.restoredGroupState != null)
			{
				restoreGroups(this.restoredGroupState);
			}
			else
			{
				initializeGroups();
			}
		});

		this.vnFromWordIdModel = new ViewModelProvider(this).get("vn:xselectors.vn(wordid)", SqlunetViewModel.class);
		this.vnFromWordIdModel.getData().observe(owner, cursor -> {

			if (cursor != null && this.vnFromWordIdModel.getData().hasActiveObservers())
			{
				// dumpXCursor(cursor);

				// pass on to list adapter
				final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
				if (adapter != null && this.groupPositions[GROUPINDEX_VERBNET] != AdapterView.INVALID_POSITION)
				{
					adapter.setChildrenCursor(this.groupPositions[GROUPINDEX_VERBNET], cursor);
				}
			}
			else
			{
				Log.i(TAG, "VN none");
			}
		});

		this.pbFromWordIdModel = new ViewModelProvider(this).get("vn:xselectors.pb(wordid)", SqlunetViewModel.class);
		this.pbFromWordIdModel.getData().observe(owner, cursor -> {

			if (cursor != null && this.pbFromWordIdModel.getData().hasActiveObservers())
			{
				// dumpXCursor(cursor);

				// pass on to list adapter
				final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
				if (adapter != null && this.groupPositions[GROUPINDEX_PROPBANK] != AdapterView.INVALID_POSITION)
				{
					adapter.setChildrenCursor(this.groupPositions[GROUPINDEX_PROPBANK], cursor);
				}
			}
			else
			{
				Log.i(TAG, "PB none");
			}
		});
	}

	// L O A D

	/**
	 * Load id from word
	 */
	private void load()
	{
		// load the contents
		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(XSqlUNetContract.Words_PbWords_VnWords.CONTENT_URI_TABLE));
		final String[] projection = { //
				Words_PbWords_VnWords.SYNSETID + " AS " + GROUPID_COLUMN, //
				Words_PbWords_VnWords.WORDID, //
				Words_PbWords_VnWords.VNWORDID, //
				Words_PbWords_VnWords.PBWORDID, //
		};
		final String selection = XSqlUNetContract.WORD + '.' + Words_PbWords_VnWords.WORD + " = ?";
		final String[] selectionArgs = {XSelectorsFragment.this.word};
		final String sortOrder = XSqlUNetContract.POSID + '.' + Words_PbWords_VnWords.POS + ',' + Words_PbWords_VnWords.SENSENUM;
		this.wordIdFromWordModel.loadData(uri, projection, selection, selectionArgs, sortOrder, this::wordIdFromWordPostProcess);
	}

	/**
	 * Post processing, extraction of wordid from cursor
	 * Closes cursor because it's no longer needed.
	 *
	 * @param cursor cursor
	 */
	private void wordIdFromWordPostProcess(@NonNull final Cursor cursor)
	{
		if (cursor.moveToFirst())
		{
			final int idWordId = cursor.getColumnIndex(Words_PbWords_VnWords.WORDID);
			this.wordId = cursor.getLong(idWordId);
		}
		// cursor.close();
	}


	/**
	 * Start child loader for
	 *
	 * @param groupId group id
	 */
	private void startChildLoader(int groupId)
	{
		Log.d(TAG, "Invoking startChildLoader() for groupId=" + groupId);
		switch (groupId)
		{
			case GROUPID_VERBNET:
			{
				//				final MutableLiveData<Cursor> vnLiveData = this.vnFromWordIdModel.getMutableData();
				//				final Cursor vnCursor = vnLiveData.getValue();
				//				if (vnCursor != null && !vnCursor.isClosed())
				//				{
				//					vnLiveData.setValue(vnCursor);
				//				}
				//				else
				loadVn(this.wordId);
			}
			break;

			case GROUPID_PROPBANK:
			{
				//				final MutableLiveData<Cursor> pbLiveData = this.pbFromWordIdModel.getMutableData();
				//				final Cursor pbCursor = pbLiveData.getValue();
				//				if (pbCursor != null && !pbCursor.isClosed())
				//				{
				//					pbLiveData.setValue(pbCursor);
				//				}
				//				else
				loadPb(this.wordId);
			}
			break;

			default:
				break;
		}
	}

	/**
	 * Load VerbNet data
	 *
	 * @param wordId word id
	 */
	private void loadVn(final long wordId)
	{
		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(XSqlUNetContract.Words_VnWords_VnClasses.CONTENT_URI_TABLE));
		final String[] projection = { //
				XSqlUNetContract.Words_VnWords_VnClasses.WORDID, //
				XSqlUNetContract.Words_VnWords_VnClasses.SYNSETID, //
				XSqlUNetContract.Words_VnWords_VnClasses.CLASSID + " AS " + Words_XNet.XID, //
				XSqlUNetContract.Words_VnWords_VnClasses.CLASSID + " AS " + Words_XNet.XCLASSID, //
				"NULL AS " + Words_XNet.XMEMBERID, //
				"TRIM(" + XSqlUNetContract.Words_VnWords_VnClasses.CLASS + ",'-.0123456789')" + " AS " + Words_XNet.XNAME, //
				XSqlUNetContract.Words_VnWords_VnClasses.CLASS + " AS " + Words_XNet.XHEADER, //
				XSqlUNetContract.Words_VnWords_VnClasses.CLASSTAG + " AS " + Words_XNet.XINFO, //
				XSqlUNetContract.Words_VnWords_VnClasses.DEFINITION + " AS " + Words_XNet.XDEFINITION, //
				"'vn' AS " + Words_XNet.SOURCES, //
				XSqlUNetContract.CLASS + ".rowid AS _id",};
		final String selection = XSqlUNetContract.Words_VnWords_VnClasses.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};
		final String sortOrder = XSqlUNetContract.Words_VnWords_VnClasses.CLASSID;
		this.vnFromWordIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, null);
	}

	/**
	 * Load PropBank data
	 *
	 * @param wordId word id
	 */
	private void loadPb(final long wordId)
	{
		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(XSqlUNetContract.Words_PbWords_PbRoleSets.CONTENT_URI_TABLE));
		final String[] projection = { //
				XSqlUNetContract.Words_PbWords_PbRoleSets.WORDID, //
				"NULL AS " + XSqlUNetContract.Words_PbWords_PbRoleSets.SYNSETID, //
				XSqlUNetContract.Words_PbWords_PbRoleSets.ROLESETID + " AS " + Words_XNet.XID, //
				XSqlUNetContract.Words_PbWords_PbRoleSets.ROLESETID + " AS " + Words_XNet.XCLASSID, //
				"NULL AS " + Words_XNet.XMEMBERID, //
				"TRIM(" + XSqlUNetContract.Words_PbWords_PbRoleSets.ROLESETNAME + ",'.0123456789')" + " AS " + Words_XNet.XNAME, //
				XSqlUNetContract.Words_PbWords_PbRoleSets.ROLESETNAME + " AS " + Words_XNet.XHEADER, //
				//Words_PbWords_PbRoleSets.ROLESETHEAD + " AS " + Words_XNet.XHEADER, //
				XSqlUNetContract.Words_PbWords_PbRoleSets.ROLESETDESCR + " AS " + Words_XNet.XINFO, //
				"NULL AS " + Words_XNet.XDEFINITION, //
				"'pb' AS " + Words_XNet.SOURCES, //
				XSqlUNetContract.CLASS + ".rowid AS _id",};
		final String selection = XSqlUNetContract.Words_PbWords_PbRoleSets.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};
		final String sortOrder = XSqlUNetContract.Words_PbWords_PbRoleSets.ROLESETID;
		this.pbFromWordIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, null);
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

	// S E L E C T I O N   L I S T E N E R

	/*
	@Override
	public void onGroupExpand(final int groupPosition)
	{
		super.onGroupExpand(groupPosition);
		Log.d(TAG, "expand " + groupPosition);
	}
	*/

	/*
	@Override
	public void onGroupCollapse(final int groupPosition)
	{
		super.onGroupCollapse(groupPosition);
		Log.d(TAG, "collapse " + groupPosition);
	}
	*/

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
			//Log.d(TAG, "CLICK on group=" + groupPosition + " child=" + childPosition + " id=" + id);
			int index = listView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
			listView.setItemChecked(index, true);

			final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
			assert adapter != null;
			final Cursor cursor = adapter.getChild(groupPosition, childPosition);
			if (!cursor.isAfterLast())
			{
				// column indices
				final int idSynsetId = cursor.getColumnIndex(XSqlUNetContract.Words_XNet.SYNSETID);
				final int idXId = cursor.getColumnIndex(Words_XNet.XID);
				final int idXClassId = cursor.getColumnIndex(Words_XNet.XCLASSID);
				final int idXMemberId = cursor.getColumnIndex(Words_XNet.XMEMBERID);
				final int idXSources = cursor.getColumnIndex(Words_XNet.SOURCES);
				// final int idWordId = cursor.getColumnIndex(Words_XNet.WORDID);

				// data
				final long wordId = this.wordId;
				final String word = this.word;
				final String cased = this.word;
				final long synsetId = cursor.isNull(idSynsetId) ? 0 : cursor.getLong(idSynsetId);
				final String pos = synsetIdToPos(synsetId);
				final long xId = cursor.isNull(idXId) ? 0 : cursor.getLong(idXId);
				final long xClassId = cursor.isNull(idXClassId) ? 0 : cursor.getLong(idXClassId);
				final long xMemberId = cursor.isNull(idXMemberId) ? 0 : cursor.getLong(idXMemberId);
				final String xSources = cursor.getString(idXSources);
				final long xMask = XSelectorPointer.getMask(xSources);

				if (groupPosition == AdapterView.INVALID_POSITION)
				{
					return false;
				}

				int groupId = -1;
				if (groupPosition == this.groupPositions[GROUPINDEX_VERBNET])
				{
					groupId = GROUPID_VERBNET;
				}
				else if (groupPosition == this.groupPositions[GROUPINDEX_PROPBANK])
				{
					groupId = GROUPID_PROPBANK;
				}

				// pointer
				final XSelectorPointer pointer = new XSelectorPointer(synsetId, wordId, xId, xClassId, xMemberId, xSources, xMask, groupId);
				Log.d(TAG, "pointer=" + pointer);

				// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
				this.listener.onItemSelected(pointer, word, cased, pos);
			}
			// cursor ownership is transferred  to adapter, so do not call
			// cursor.close();
		}
		return true;
	}

	// E X P A N D

	/**
	 * Expand all
	 */
	private void expandAll()
	{
		if (getExpandableListAdapter() == null)
		{
			return;
		}

		final ExpandableListView expandableListView = getExpandableListView();
		assert expandableListView != null;
		int count = this.xnCursor.getCount();
		for (int position = 0; position < count; position++)
		{
			expandableListView.expandGroup(position);
		}
	}

	/*
	 * Expand or Collapse section
	 */
	/*
	private void expandOrCollapse(int groupPosition, boolean expand)
	{
		assert getExpandableListAdapter() != null;
		final ExpandableListView expandableListView = getExpandableListView();
		assert expandableListView != null;
		boolean expanded = expandableListView.isGroupExpanded(groupPosition);
		Log.d(TAG, "Expanded " + expanded);
		if (expand && !expanded)
		{
			Log.d(TAG, "Expand");
			expand(groupPosition);
		}
		else if (!expand && expanded)
		{
			Log.d(TAG, "Collapse");
			collapse(groupPosition);
		}
	}
	*/

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
}
