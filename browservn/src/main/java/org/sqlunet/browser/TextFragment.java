/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import org.sqlunet.browser.vn.R;
import org.sqlunet.propbank.PbRoleSetPointer;
import org.sqlunet.propbank.browser.PbRoleSetActivity;
import org.sqlunet.propbank.provider.PropBankContract;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.style.RegExprSpanner;
import org.sqlunet.style.Spanner.SpanFactory;
import org.sqlunet.verbnet.VnClassPointer;
import org.sqlunet.verbnet.browser.VnClassActivity;
import org.sqlunet.verbnet.provider.VerbNetContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

/**
 * TextSearch progressMessage fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TextFragment extends AbstractTableFragment
{
	static private final String TAG = "TextF";

	/**
	 * Bold style factory
	 */
	static private final SpanFactory boldFactory = flags -> new Object[]{/*new BackgroundColorSpan(Colors.dk_red), new ForegroundColorSpan(Color.WHITE), */new StyleSpan(Typeface.BOLD)};

	/**
	 * Factories
	 */
	static private final SpanFactory[] factories = new SpanFactory[]{boldFactory,};

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

	static private String[] toPatterns(final String query)
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
			if ("vn".equals(database)) //
			{
				final int idClasses = cursor.getColumnIndex(VerbNetContract.Lookup_VnExamples_X.CLASSES);
				final String classes = cursor.getString(idClasses);
				Log.d(TAG, "CLICK vn classes=" + classes);

				final Pair<TypedPointer[], CharSequence[]> result = makeData(classes);
				if (result.first.length > 1)
				{
					final DialogInterface.OnClickListener listener = (dialog, which) -> {
						// which argument contains the index position of the selected item
						final TypedPointer typedPointer = result.first[which];
						startVn(typedPointer);
					};

					final AlertDialog dialog = makeDialog(listener, result.second);
					dialog.show();
				}
				else if (result.first.length == 1)
				{
					final TypedPointer typedPointer = result.first[0];
					startVn(typedPointer);
				}
			}
			else if ("pb".equals(database)) //
			{
				final int idRoleSets = cursor.getColumnIndex(PropBankContract.Lookup_PbExamples_X.ROLESETS);
				final String roleSets = cursor.getString(idRoleSets);
				Log.d(TAG, "CLICK pb rolesets=" + roleSets);

				final Pair<TypedPointer[], CharSequence[]> result = makeData(roleSets);
				if (result.first.length > 1)
				{
					final DialogInterface.OnClickListener listener = (dialog, which) -> {
						// which argument contains the index position of the selected item
						final TypedPointer typedPointer = result.first[which];
						startPb(typedPointer);
					};

					final AlertDialog dialog = makeDialog(listener, result.second);
					dialog.show();
				}
				else if (result.first.length == 1)
				{
					final TypedPointer typedPointer = result.first[0];
					startPb(typedPointer);
				}
			}

		}
	}

	/**
	 * Start VerbNet
	 *
	 * @param typedPointer typed pointer
	 */
	private void startVn(@NonNull final TypedPointer typedPointer)
	{
		final long targetId = typedPointer.id;
		Intent targetIntent = null;
		Parcelable pointer = null;

		// intent, type, pointer
		if (typedPointer.type == 0)
		{
			pointer = new VnClassPointer(targetId);
			targetIntent = new Intent(requireContext(), VnClassActivity.class);
			targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_VNCLASS);
		}

		// pass pointer
		assert targetIntent != null;
		targetIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
		targetIntent.setAction(ProviderArgs.ACTION_QUERY);

		// start
		startActivity(targetIntent);
	}

	/**
	 * Start PropBank
	 *
	 * @param typedPointer typed pointer
	 */
	private void startPb(@NonNull final TypedPointer typedPointer)
	{
		final long targetId = typedPointer.id;
		Intent targetIntent = null;
		Parcelable pointer = null;

		// intent, type, pointer
		if (typedPointer.type == 0)
		{
			pointer = new PbRoleSetPointer(targetId);
			targetIntent = new Intent(requireContext(), PbRoleSetActivity.class);
			targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_PBROLESET);
		}

		// pass pointer
		assert targetIntent != null;
		targetIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
		targetIntent.setAction(ProviderArgs.ACTION_QUERY);

		// start
		startActivity(targetIntent);
	}

	/**
	 * Typed pointer
	 */
	private class TypedPointer
	{
		final int type;
		final long id;

		TypedPointer(int type, long id)
		{
			this.type = type;
			this.id = id;
		}
	}

	/**
	 * Make choice data
	 *
	 * @param concatChoices concatenated choices
	 * @return array of typed pointers and array of labels
	 */
	private Pair<TypedPointer[], CharSequence[]> makeData(@NonNull final String... concatChoices)
	{
		final List<TypedPointer> typedPointers = new ArrayList<>();
		final List<CharSequence> labels = new ArrayList<>();
		final Pattern pattern = Pattern.compile('(' + "(?i)" + this.query + ')');

		int type = 0;
		for (String choices : concatChoices)
		{
			if (choices == null)
			{
				type++;
				continue;
			}
			final List<String> choiceList = Arrays.asList(choices.split(","));
			Collections.sort(choiceList);
			for (String choice : choiceList)
			{
				final String[] fields = choice.split("@");
				final String label = fields[0];
				int resId = -1;
				switch (type)
				{
					case 0:
						resId = R.drawable.roles;
						break;
					case 1:
						resId = R.drawable.role;
						break;
					case 2:
						resId = R.drawable.sentence;
						break;
				}
				final SpannableStringBuilder sb = new SpannableStringBuilder();
				appendImage(requireContext(), sb, resId);
				sb.append(' ');
				if (pattern.matcher(label).find())
				{
					append(sb, label, new ForegroundColorSpan(Color.BLACK), new StyleSpan(Typeface.BOLD));
				}
				else
				{
					append(sb, label, new ForegroundColorSpan(Color.GRAY));
				}

				labels.add(sb);
				typedPointers.add(new TypedPointer(type, Long.parseLong(fields[1])));
			}
			type++;
		}
		final TypedPointer[] typedPointersArray = typedPointers.toArray(new TypedPointer[0]);
		final CharSequence[] labelsArray = labels.toArray(new CharSequence[0]);
		return new Pair<>(typedPointersArray, labelsArray);
	}

	/**
	 * Make selection dialog
	 *
	 * @param listener click listener
	 * @param choices  choices
	 * @return dialog
	 */
	private AlertDialog makeDialog(final DialogInterface.OnClickListener listener, final CharSequence... choices)
	{
		return new AlertDialog.Builder(requireContext()) //
				.setTitle(R.string.title_activity_searchtext) //
				.setItems(choices, listener) //
				.create();
	}

	/**
	 * Append text
	 *
	 * @param sb    spannable string builder
	 * @param text  text
	 * @param spans spans to apply
	 */
	static private void append(@NonNull final SpannableStringBuilder sb, @Nullable final CharSequence text, @NonNull final Object... spans)
	{
		if (text == null || text.length() == 0)
		{
			return;
		}

		final int from = sb.length();
		sb.append(text);
		final int to = sb.length();

		for (final Object span : spans)
		{
			sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	/**
	 * Append Image
	 *
	 * @param context context
	 * @param sb      spannable string builder
	 * @param resId   resource id
	 */
	static private void appendImage(@NonNull final Context context, @NonNull final SpannableStringBuilder sb, final int resId)
	{
		append(sb, "\u0000", makeImageSpan(context, resId));
	}

	/**
	 * Make image span
	 *
	 * @param context context
	 * @param resId   res id
	 * @return image span
	 */
	static private Object makeImageSpan(@NonNull final Context context, final int resId)
	{
		Drawable drawable;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
		{
			drawable = context.getResources().getDrawable(resId);
		}
		else
		{
			drawable = context.getResources().getDrawable(resId, null);
		}
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		return new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM);
	}
}
