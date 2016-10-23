package org.sqlunet.browser.config;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.R;
import org.sqlunet.framenet.FnSentencePointer;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.wordnet.SynsetPointer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A list fragment representing a table.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TableFragment extends ListFragment
{
	private static final String TAG = "TableFragment"; //

	private Intent targetIntent;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public TableFragment()
	{
		//
	}


	@SuppressWarnings("boxing")
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// args
		Bundle args = getArguments();
		if (args == null)
		{
			args = getActivity().getIntent().getExtras();
		}

		// query params
		final String uriString = args.getString(SqlUNetContract.ARG_QUERYURI);
		final Uri uri = Uri.parse(uriString);
		final String id = args.getString(SqlUNetContract.ARG_QUERYID);
		final String[] items = args.getStringArray(SqlUNetContract.ARG_QUERYITEMS);
		final String[] xitems = args.getStringArray(SqlUNetContract.ARG_QUERYXITEMS);
		final String sort = args.getString(SqlUNetContract.ARG_QUERYSORT);
		final String selection = args.getString(SqlUNetContract.ARG_QUERYFILTER);
		final String queryArg = args.getString(SqlUNetContract.ARG_QUERYARG);
		final int layoutId = args.getInt(SqlUNetContract.ARG_QUERYLAYOUT);
		this.targetIntent = args.getParcelable(SqlUNetContract.ARG_QUERYINTENT);

		// view binder
		ViewBinder viewBinder = new ViewBinder()
		{
			@Override
			public boolean setViewValue(final View view, final Cursor cursor, final int columnIndex)
			{
				String text = cursor.getString(columnIndex);
				if (text == null)
				{
					text = ""; //
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
					throw new IllegalStateException(view.getClass().getName() + " is not a view that can be bound by this SimpleCursorAdapter"); //
				}
				return false;
			}
		};

		// adapter
		// from (database fields)
		final List<String> fromList = new ArrayList<>();
		if (items != null)
		{
			for (final String item2 : items)
			{
				String field = item2;

				// remove alias
				final int asIndex = field.lastIndexOf(" AS "); //
				if (asIndex != -1)
				{
					field = field.substring(asIndex + 4);
				}
				fromList.add(field);
			}
		}
		final String[] from = fromList.toArray(new String[0]);
		// Log.d(TableFragment.TAG + "From", TableFragment.toString(from));

		// to (view ids)
		final Collection<Integer> toList = new ArrayList<>();
		if (items != null)
		{
			for (int i = 0; i < items.length; i++)
			{
				toList.add(R.id.item0 + i);
			}
		}
		final int[] to = new int[toList.size()];
		int i = 0;
		for (final Integer n : toList)
		{
			to[i++] = n;
		}
		// Log.d(TableFragment.TAG + "To", TableFragment.toString(to));

		// make
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), layoutId, null, //
				from, //
				to, 0); //
		adapter.setViewBinder(viewBinder);
		setListAdapter(adapter);

		// load the contents
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				// make projection
				final List<String> fields = new ArrayList<>();

				// add _id alias for first column
				fields.add(id + " AS _id"); //

				// add items
				if (items != null)
				{
					Collections.addAll(fields, items);
				}

				// add xitems
				if (xitems != null)
				{
					Collections.addAll(fields, xitems);
				}

				final String[] projection = fields.toArray(new String[0]);
				// for (String p : projection)
				// {
				//	System.out.println(p);
				//}
				final String[] selectionArgs = queryArg == null ? null : new String[]{queryArg};
				return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sort);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor == null)
				{
					Toast.makeText(getActivity(), R.string.status_provider_query_failed, Toast.LENGTH_LONG).show();
				}

				((CursorAdapter) getListAdapter()).swapCursor(cursor);
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				((CursorAdapter) getListAdapter()).swapCursor(null);
			}
		});
	}

	// L A Y O U T


	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// layout
		return inflater.inflate(R.layout.fragment_table, container, false);
	}

	// C L I C K

	@SuppressWarnings("boxing")
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		// System.out.println("id=" + id + " pos=" + position);

		final Object o = getListAdapter().getItem(position);
		final Cursor cursor = (Cursor) o;
		dump(cursor);

		if (this.targetIntent != null)
		{
			// target
			long targetId = cursor.getLong(0);
			Log.d(TAG, "targetid=" + targetId); //

			// intent's classname
			ComponentName componentName = this.targetIntent.getComponent();
			String className = componentName.getClassName();
			if ("org.sqlunet.framenet.browser.SentenceActivity".equals(className)) //
			{
				// build pointer
				@SuppressWarnings("TypeMayBeWeakened") final FnSentencePointer sentencePointer = new FnSentencePointer(targetId);

				// pass pointer
				this.targetIntent.putExtra(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_FNSENTENCE);
				this.targetIntent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, sentencePointer);

				// start
				startActivity(this.targetIntent);
			}
			else if ("org.sqlunet.wordnet.browser.SynsetActivity".equals(className)) //
			{
				// build pointer
				final SynsetPointer synsetPointer = new SynsetPointer();
				synsetPointer.setSynset(targetId, null);

				// pass pointer
				this.targetIntent.putExtra(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_FNSENTENCE);
				this.targetIntent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, synsetPointer);

				// start
				startActivity(this.targetIntent);
			}
		}
	}

	private void dump(final Cursor cursor)
	{
		int n = cursor.getColumnCount();
		String[] cols = cursor.getColumnNames();
		for (int i = 0; i < n; i++)
		{
			String val;
			switch (cursor.getType(i))
			{
				case Cursor.FIELD_TYPE_NULL:
					val = "null"; //
					break;
				case Cursor.FIELD_TYPE_INTEGER:
					val = Integer.toString(cursor.getInt(i));
					break;
				case Cursor.FIELD_TYPE_FLOAT:
					val = Float.toString(cursor.getFloat(i));
					break;
				case Cursor.FIELD_TYPE_STRING:
					val = cursor.getString(i);
					break;
				case Cursor.FIELD_TYPE_BLOB:
					val = "blob"; //
					break;
				default:
					val = "NA"; //
					break;
			}
			Log.d(TAG, "column " + i + " " + cols[i] + "=" + val); //
		}
	}
}
