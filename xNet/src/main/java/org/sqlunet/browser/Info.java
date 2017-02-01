package org.sqlunet.browser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import org.sqlunet.style.Report;
import org.sqlunet.xnet.R;

/**
 * Info helper
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

public class Info
{
	static public void info(final Activity activity, final int messageId, final CharSequence... lines)
	{
		final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
		alert.setTitle(R.string.action_info);
		alert.setMessage(messageId);
		alert.setNegativeButton(R.string.action_dismiss, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				// canceled.
			}
		});
		/*
		final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
		final ListView extra = new ListView(this);
		input.setAdapter(adapter);
		*/
		final SpannableStringBuilder sb = new SpannableStringBuilder();
		int i = 0;
		for (CharSequence line : lines)
		{
			if ((i++ % 2) == 0)
			{
				Report.appendHeader(sb, line);
			}
			else
			{
				sb.append(line);
			}
			sb.append('\n');
		}

		final TextView extra = new TextView(activity);
		extra.setPadding(20, 0, 20, 0);
		extra.setText(sb);
		alert.setView(extra);
		alert.show();
	}
}
