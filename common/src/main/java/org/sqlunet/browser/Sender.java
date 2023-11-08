/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser;

import android.content.Context;
import android.content.Intent;

import org.sqlunet.browser.common.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Sender
{
	public static void send(@NonNull final Context context, @NonNull final String title, @NonNull final CharSequence content, @Nullable final String... to)
	{
		final Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
		sendIntent.putExtra(Intent.EXTRA_TEXT, content);
		if (to != null && to.length > 0)
		{
			sendIntent.putExtra(Intent.EXTRA_EMAIL, to);
		}
		sendIntent.setType("message/rfc822"); // prompts email client only
		context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.title_dialog_select_email)));
	}
}
