package org.sqlunet.browser.xselector;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.local.app.ExpandableListFragment;
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
import androidx.lifecycle.ViewModelProviders;

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
	 * VerbNet id
	 */
	static public final int GROUPID_VERBNET = 2;

	/**
	 * Propbank id
	 */
	static public final int GROUPID_PROPBANK = 3;

	/**
	 * FrameNet id
	 */
	static public final int GROUPID_FRAMENET = 4;

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
	 * VerbNet group position
	 */
	private int groupVerbNetPosition;

	/**
	 * PropBank group position
	 */
	private int groupPropBankPosition;

	/**
	 * FrameNet group position
	 */
	private int groupFrameNetPosition;

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
	 * WordNet model
	 */
	private SqlunetViewModel wnModel;

	/**
	 * VerbNet model
	 */
	private SqlunetViewModel vnModel;

	/**
	 * PropBank model
	 */
	private SqlunetViewModel pbModel;

	/**
	 * FrameNet model
	 */
	private SqlunetViewModel fnModel;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	@SuppressWarnings("boxing")
	public XSelectorsFragment()
	{
		this.groupWordNetPosition = -1;
		this.groupVerbNetPosition = -1;
		this.groupPropBankPosition = -1;
		this.groupFrameNetPosition = -1;
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
		if (Settings.Source.VERBNET.test(enable))
		{
			this.groupVerbNetPosition = position++;
			this.xnCursor.addRow(new Object[]{GROUPID_VERBNET, "verbnet", Integer.toString(R.drawable.verbnet)});
		}
		if (Settings.Source.PROPBANK.test(enable))
		{
			this.groupPropBankPosition = position++;
			this.xnCursor.addRow(new Object[]{GROUPID_PROPBANK, "propbank", Integer.toString(R.drawable.propbank)});
		}
		if (Settings.Source.FRAMENET.test(enable))
		{
			this.groupFrameNetPosition = position++;
			this.xnCursor.addRow(new Object[]{GROUPID_FRAMENET, "framenet", Integer.toString(R.drawable.framenet)});
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
		this.wnModel = ViewModelProviders.of(this).get("xselectors.wn", SqlunetViewModel.class);
		this.wnModel.getData().observe(this, cursor -> {

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

		this.vnModel = ViewModelProviders.of(this).get("xselectors.vn", SqlunetViewModel.class);
		this.vnModel.getData().observe(this, cursor -> {

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

		this.pbModel = ViewModelProviders.of(this).get("xselectors.pb", SqlunetViewModel.class);
		this.pbModel.getData().observe(this, cursor -> {

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
				Log.i(XSelectorsFragment.TAG, "PB none");
			}
		});

		this.fnModel = ViewModelProviders.of(this).get("xselectors.fn", SqlunetViewModel.class);
		this.fnModel.getData().observe(this, cursor -> {

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
				Log.i(XSelectorsFragment.TAG, "FN none");
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

		final SqlunetViewModel model = ViewModelProviders.of(this).get("xselectors.wordid", SqlunetViewModel.class);
		model.getData().observe(this, unusedCursor -> initialize());
		model.loadData(uri, projection, selection, selectionArgs, sortOrder, this::xselectorsPostProcess);
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
								case "vn": //
									v.setImageResource(R.drawable.verbnet);
									return;
								case "pb": //
									v.setImageResource(R.drawable.propbank);
									return;
								case "fn": //
									v.setImageResource(R.drawable.framenet);
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
			case GROUPID_VERBNET:
				loadVn(this.wordId);
				break;
			case GROUPID_PROPBANK:
				loadPb(this.wordId);
				break;
			case GROUPID_FRAMENET:
				loadFn(this.wordId);
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
				XSqlUNetContract.Words_VnWords_VnClasses_U.WORDID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID + " AS " + Words_XNet_U.XID, //
				"NULL AS " + Words_XNet_U.XCLASSID, //
				"NULL AS " + Words_XNet_U.XMEMBERID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEMMA + "|| '.' ||" + WordNetContract.POS + '.' + WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POS + " AS " + Words_XNet_U.XNAME, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXDOMAIN + " AS " + Words_XNet_U.XHEADER, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSEKEY + " AS " + Words_XNet_U.XINFO, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID + " AS _id"};
		final String selection = XSqlUNetContract.Words_VnWords_VnClasses_U.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};

		this.wnModel.loadData(uri, projection, selection, selectionArgs, null, null);
	}

	/**
	 * Load VerbNet data
	 *
	 * @param wordId word id
	 */
	private void loadVn(final long wordId)
	{
		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(XSqlUNetContract.Words_VnWords_VnClasses_U.CONTENT_URI_TABLE));
		final String[] projection = { //
				XSqlUNetContract.Words_VnWords_VnClasses_U.WORDID, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.SYNSETID, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.CLASSID + " AS " + Words_XNet_U.XID, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.CLASSID + " AS " + Words_XNet_U.XCLASSID, //
				"NULL AS " + Words_XNet_U.XMEMBERID, //
				"TRIM(" + XSqlUNetContract.Words_VnWords_VnClasses_U.CLASS + ",'-.0123456789')" + " AS " + Words_XNet_U.XNAME, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.CLASS + " AS " + Words_XNet_U.XHEADER, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.CLASSTAG + " AS " + Words_XNet_U.XINFO, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, //
				"rowid AS _id",};
		final String selection = XSqlUNetContract.Words_VnWords_VnClasses_U.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};
		final String sortOrder = XSqlUNetContract.Words_VnWords_VnClasses_U.CLASSID;

		this.vnModel.loadData(uri, projection, selection, selectionArgs, sortOrder, null);
	}

	/**
	 * Load PropBank data
	 *
	 * @param wordId word id
	 */
	private void loadPb(final long wordId)
	{
		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(XSqlUNetContract.Words_PbWords_PbRolesets_U.CONTENT_URI_TABLE));
		final String[] projection = { //
				XSqlUNetContract.Words_PbWords_PbRolesets_U.WORDID, //
				XSqlUNetContract.Words_PbWords_PbRolesets_U.SYNSETID, //
				XSqlUNetContract.Words_PbWords_PbRolesets_U.ROLESETID + " AS " + Words_XNet_U.XID, //
				XSqlUNetContract.Words_PbWords_PbRolesets_U.ROLESETID + " AS " + Words_XNet_U.XCLASSID, //
				"NULL AS " + Words_XNet_U.XMEMBERID, //
				"TRIM(" + XSqlUNetContract.Words_PbWords_PbRolesets_U.ROLESETNAME + ",'.0123456789')" + " AS " + Words_XNet_U.XNAME, //
				XSqlUNetContract.Words_PbWords_PbRolesets_U.ROLESETNAME + " AS " + Words_XNet_U.XHEADER, //
				//Words_PbWords_PbRolesets_U.ROLESETHEAD + " AS " + Words_XNet_U.XHEADER, //
				XSqlUNetContract.Words_PbWords_PbRolesets_U.ROLESETDESCR + " AS " + Words_XNet_U.XINFO, //
				XSqlUNetContract.Words_PbWords_PbRolesets_U.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, //
				"rowid AS _id",};
		final String selection = XSqlUNetContract.PredicateMatrix_PropBank.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};
		final String sortOrder = XSqlUNetContract.Words_PbWords_PbRolesets_U.ROLESETID;

		this.pbModel.loadData(uri, projection, selection, selectionArgs, sortOrder, null);
	}

	/**
	 * Load FrameNet data
	 *
	 * @param wordId word id
	 */
	private void loadFn(final long wordId)
	{
		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(XSqlUNetContract.Words_FnWords_FnFrames_U.CONTENT_URI_TABLE));
		final String[] projection = { //
				XSqlUNetContract.Words_FnWords_FnFrames_U.WORDID, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.SYNSETID, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.FRAMEID + " AS " + Words_XNet_U.XID, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.FRAMEID + " AS " + Words_XNet_U.XCLASSID, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.LUID + " AS " + Words_XNet_U.XMEMBERID, //
				"GROUP_CONCAT(" + XSqlUNetContract.Words_FnWords_FnFrames_U.LEXUNIT + ",'\n')" + " AS " + Words_XNet_U.XNAME, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.FRAME + " AS " + Words_XNet_U.XHEADER, //
				"GROUP_CONCAT(" + XSqlUNetContract.Words_FnWords_FnFrames_U.LUDEFINITION + ",'\n') AS " + Words_XNet_U.XINFO, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, //
				"rowid AS _id",};
		final String selection = XSqlUNetContract.Words_FnWords_FnFrames_U.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};
		final String sortOrder = XSqlUNetContract.Words_FnWords_FnFrames_U.LUID + ' ' + "IS NULL" + ',' + XSqlUNetContract.Words_FnWords_FnFrames_U.SOURCES + ',' + XSqlUNetContract.Words_FnWords_FnFrames_U.FRAMEID;

		this.fnModel.loadData(uri, projection, selection, selectionArgs, sortOrder, null);
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

	@SuppressWarnings("boxing")
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
				else if (this.groupVerbNetPosition != -1 && groupPosition == this.groupVerbNetPosition)
				{
					groupId = GROUPID_VERBNET;
				}
				else if (this.groupPropBankPosition != -1 && groupPosition == this.groupPropBankPosition)
				{
					groupId = GROUPID_PROPBANK;
				}
				else if (this.groupFrameNetPosition != -1 && groupPosition == this.groupFrameNetPosition)
				{
					groupId = GROUPID_FRAMENET;
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
