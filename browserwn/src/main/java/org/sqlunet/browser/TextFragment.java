package org.sqlunet.browser;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import org.sqlunet.browser.wn.Settings;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.style.RegExprSpanner;
import org.sqlunet.style.Spanner.SpanFactory;
import org.sqlunet.wordnet.SynsetPointer;
import org.sqlunet.wordnet.WordPointer;

/**
 * TextSearch progressMessage fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TextFragment extends AbstractTableFragment
{
	static private final String TAG = "TSResultFragment";

	/**
	 * Bold style factory
	 */
	static private final SpanFactory boldFactory = flags -> new Object[]{/*new BackgroundColorSpan(Colors.dk_red), new ForegroundColorSpan(Color.WHITE), */new StyleSpan(Typeface.BOLD)};

	/**
	 * Factories
	 */
	static private final SpanFactory[][] factories = {new SpanFactory[]{boldFactory,}};

	/**
	 * Query argument
	 */
	private String query;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public TextFragment()
	{
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// args
		Bundle args = getArguments();
		assert args != null;

		// search target
		// final String database = args.getString(ProviderArgs.ARG_QUERYDATABASE);
		String queryArg = args.getString(ProviderArgs.ARG_QUERYARG);
		this.query = queryArg != null ? queryArg.trim() : "";

		super.onCreate(savedInstanceState);
	}

	/**
	 * Make view binder
	 *
	 * @return ViewBinder
	 */
	@Nullable
	@Override
	protected ViewBinder makeViewBinder()
	{
		// pattern (case-insensitive)
		final String[] patterns = {'(' + "(?i)" + this.query + ')',};

		// spanner
		final RegExprSpanner spanner = new RegExprSpanner(patterns, factories);

		// view binder
		return (view, cursor, columnIndex) -> {
			String value = cursor.getString(columnIndex);
			if (value == null)
			{
				value = "";
			}

			if (view instanceof TextView)
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder(value);
				spanner.setSpan(sb, 0, 0);
				((TextView) view).setText(sb);
			}
			else if (view instanceof ImageView)
			{
				try
				{
					((ImageView) view).setImageResource(Integer.parseInt(value));
				}
				catch (@NonNull final NumberFormatException nfe)
				{
					((ImageView) view).setImageURI(Uri.parse(value));
				}
			}
			else
			{
				throw new IllegalStateException(view.getClass().getName() + " is not a view that can be bound by this SimpleCursorAdapter");
			}
			return true;
		};
	}

	// C L I C K

	@Override
	public void onListItemClick(final ListView listView, final View view, final int position, final long id)
	{
		super.onListItemClick(listView, view, position, id);

		Log.d(TAG, "CLICK id=" + id + " pos=" + position);

		// cursor
		final Object item = getListAdapter().getItem(position);
		final Cursor cursor = (Cursor) item;

		// args
		Bundle args = getArguments();
		assert args != null;

		// search target
		final String database = args.getString(ProviderArgs.ARG_QUERYDATABASE);
		if (database != null)
		{
			// wordnet
			if ("wn".equals(database))
			{
				String subtarget = args.getString(ProviderArgs.ARG_QUERYIDTYPE);

				if ("synset".equals(subtarget))
				{
					// recursion
					final int recurse = Settings.getRecursePref(getContext());

					// target
					final int colIdx = cursor.getColumnIndex("synsetid");
					final long targetId = cursor.getLong(colIdx);
					Log.d(TAG, "CLICK wn synset=" + targetId);

					// build pointer
					final Parcelable synsetPointer = new SynsetPointer(targetId);

					// intent
					final Intent targetIntent = new Intent(requireContext(), org.sqlunet.wordnet.browser.SynsetActivity.class);
					targetIntent.setAction(ProviderArgs.ACTION_QUERY);
					targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET);
					targetIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, synsetPointer);
					targetIntent.putExtra(ProviderArgs.ARG_QUERYRECURSE, recurse);

					// start
					startActivity(targetIntent);
				}
				else if ("lemma".equals(subtarget))
				{
					// target
					final int colIdx = cursor.getColumnIndex("wordid");
					final long targetId = cursor.getLong(colIdx);
					Log.d(TAG, "CLICK wn word=" + targetId);

					// build pointer
					final Parcelable wordPointer = new WordPointer(targetId);

					// intent
					final Intent targetIntent = new Intent(requireContext(), org.sqlunet.wordnet.browser.WordActivity.class);
					targetIntent.setAction(ProviderArgs.ACTION_QUERY);
					targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_WORD);
					targetIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, wordPointer);

					// start
					startActivity(targetIntent);
				}
			}
		}
	}
}
