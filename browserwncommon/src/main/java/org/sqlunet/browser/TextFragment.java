/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import org.sqlunet.browser.wn.Settings;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.style.Factories;
import org.sqlunet.style.RegExprSpanner;
import org.sqlunet.style.Spanner.SpanFactory;
import org.sqlunet.wordnet.SynsetPointer;
import org.sqlunet.wordnet.WordPointer;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Text result fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TextFragment extends AbstractTableFragment
{
	static private final String TAG = "TextF";

	/**
	 * Factories
	 */
	static private final SpanFactory[] factories = new SpanFactory[]{Factories.boldFactory,};

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
		// patterns (case-insensitive)
		final String[] patterns = toPatterns(this.query);

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
				return true;
			}
			else if (view instanceof ImageView)
			{
				try
				{
					((ImageView) view).setImageResource(Integer.parseInt(value));
					return true;
				}
				catch (@NonNull final NumberFormatException nfe)
				{
					((ImageView) view).setImageURI(Uri.parse(value));
					return true;
				}
			}
			else
			{
				throw new IllegalStateException(view.getClass().getName() + " is not a view that can be bound by this SimpleCursorAdapter");
			}
		};
	}

	@NonNull
	static private String[] toPatterns(@NonNull final String query)
	{
		String[] tokens = query.split("[\\s()]+");
		List<String> patterns = new ArrayList<>();
		for (String token : tokens)
		{
			token = token.trim();
			token = token.replaceAll("\\*$", "");
			if (token.isEmpty() || "AND".equals(token) || "OR".equals(token) || "NOT".equals(token) || token.startsWith("NEAR"))
			{
				continue;
			}

			// Log.d(TAG, '<' + token + '>');
			patterns.add('(' + "(?i)" + token + ')');
		}
		return patterns.toArray(new String[0]);
	}

	// C L I C K

	@Override
	public void onListItemClick(@NonNull final ListView listView, @NonNull final View view, final int position, final long id)
	{
		super.onListItemClick(listView, view, position, id);

		Log.d(TAG, "CLICK id=" + id + " pos=" + position);

		// cursor
		final ListAdapter adapter = getListAdapter();
		assert adapter != null;
		final Object item = adapter.getItem(position);
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
					// parameters
					final int recurse = org.sqlunet.wordnet.settings.Settings.getRecursePref(requireContext());
					final Bundle parameters = org.sqlunet.wordnet.settings.Settings.getRenderParametersPref(requireContext());

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
					targetIntent.putExtra(ProviderArgs.ARG_RENDERPARAMETERS, parameters);

					// start
					startActivity(targetIntent);
				}
				else if ("word".equals(subtarget))
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
