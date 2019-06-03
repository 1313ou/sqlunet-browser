/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.app.Activity;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import org.sqlunet.style.Report;
import org.sqlunet.xnet.R;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

/**
 * Info helper
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

public class Info
{
	static public void build(@NonNull final SpannableStringBuilder sb, @NonNull final CharSequence... lines)
	{
		int i = 0;
		for (CharSequence line : lines)
		{
			//noinspection StatementWithEmptyBody
			if (line == null)
			{
				// sb.append("");
			}
			else if ((i++ % 2) == 0)
			{
				Report.appendHeader(sb, line);
			}
			else
			{
				sb.append(line);
			}
			sb.append('\n');
		}
	}

	static public void info(@NonNull final Activity activity, @StringRes final int messageId, final CharSequence... lines)
	{
		final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
		alert.setTitle(R.string.action_info);
		alert.setMessage(messageId);
		alert.setNegativeButton(R.string.action_dismiss, (dialog, whichButton) -> {
			// canceled.
		});
		/*
		final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
		final ListView extra = new ListView(this);
		input.setAdapter(adapter);
		*/
		final SpannableStringBuilder sb = new SpannableStringBuilder();
		build(sb, lines);

		final TextView extra = new TextView(activity);
		extra.setPadding(20, 0, 20, 0);
		extra.setText(sb);
		alert.setView(extra);
		alert.show();
	}
}
