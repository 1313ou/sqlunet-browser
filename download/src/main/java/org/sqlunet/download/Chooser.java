/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.download;

import android.util.Pair;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

/**
 * Md5 async task
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Chooser
{
	// static private final String TAG = "Chooser";

	/**
	 * Collect files
	 *
	 * @param directories directory names-values array
	 */
	@SafeVarargs
	static public RadioGroup collectFiles(@NonNull final FragmentActivity activity, final Pair<CharSequence[], CharSequence[]>... directories)
	{
		final RadioGroup group = new RadioGroup(activity);

		// collect targets
		for (Pair<CharSequence[], CharSequence[]> namesValues : directories)
		{
			final CharSequence[] names = namesValues.first;
			final CharSequence[] values = namesValues.second;
			for (int i = 0; i < names.length && i < values.length; i++)
			{
				//final CharSequence name = names[i];
				final CharSequence value = values[i];
				final String dirValue = value.toString();
				final File dir = new File(dirValue);
				if (!dir.exists())
				{
					continue;
				}
				//final File[] files = dir.listFiles((dir1, name) -> name.matches(".*\\.zip") || name.matches(".*\\.db"));
				//final File[] files = dir.listFiles((dir1, name) -> !new File(dir1, name).isDirectory());
				final File[] files = dir.listFiles();
				if (files == null)
				{
					continue;
				}
				for (File file : files)
				{
					if (file.isDirectory())
					{
						continue;
					}

					if (file.exists())
					{
						//final SpannableStringBuilder sb = new SpannableStringBuilder();
						//Report.appendHeader(sb, name.toString().split("\n")[0]).append('\n').append(file.getAbsolutePath());
						final RadioButton radioButton = new RadioButton(activity);
						radioButton.setText(file.getAbsolutePath());
						radioButton.setTag(file.getAbsolutePath());
						group.addView(radioButton);
					}
				}
			}
		}
		return group.getChildCount() == 0 ? null : group;
	}
}
