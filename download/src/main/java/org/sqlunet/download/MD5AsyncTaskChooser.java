/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.util.Pair;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.sqlunet.concurrency.Task;
import org.sqlunet.concurrency.TaskDialogObserver;
import org.sqlunet.concurrency.TaskObserver;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.fragment.app.FragmentActivity;

/**
 * Md5 async task
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class MD5AsyncTaskChooser
{
	// static private final String TAG = "Md5AsyncTaskChooser";

	/**
	 * MD5
	 *
	 * @param activity       activity
	 * @param path           path
	 * @param resultListener result listener
	 */
	public static void md5(@NonNull final FragmentActivity activity, @NonNull final String path, final Consumer<String> resultListener)
	{
		if (activity.isFinishing() || activity.isDestroyed())
		{
			return;
		}
		final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager()) // guarded, level 2
				.setTitle(activity.getString(R.string.action_md5)) //
				.setMessage(path);
		final Task<String, Number, String> task = new FileAsyncTask<>(observer, resultListener, 1000).md5FromFile();
		task.execute(path);
	}

	/**
	 * MD5 from selected file in one of storage directories or caches
	 *
	 * @param directories directory names-values array
	 */
	@SafeVarargs
	static public void md5(@NonNull final FragmentActivity activity, final Pair<CharSequence[], CharSequence[]>... directories)
	{
		// collect
		final RadioGroup input = Chooser.collectFiles(activity, directories);
		if (input == null)
		{
			Toast.makeText(activity, R.string.md5_none, Toast.LENGTH_SHORT).show();
			return;
		}

		// display targets
		final AlertDialog.Builder alert = new AlertDialog.Builder(activity); // unguarded, level 1
		alert.setTitle(R.string.action_md5_ask);
		alert.setMessage(R.string.hint_md5_of_file);
		alert.setView(input);
		alert.setPositiveButton(R.string.action_ok, (dialog, whichButton) -> {
			dialog.dismiss();

			int childCount = input.getChildCount();
			for (int i = 0; i < childCount; i++)
			{
				final RadioButton radioButton = (RadioButton) input.getChildAt(i);
				if (radioButton.getId() == input.getCheckedRadioButtonId())
				{
					final String sourceFile = radioButton.getTag().toString();
					if (new File(sourceFile).exists())
					{
						// process target
						MD5AsyncTaskChooser.md5(activity, sourceFile, computedResult -> {

							if (!activity.isFinishing() && !activity.isDestroyed())
							{
								final SpannableStringBuilder sb = new SpannableStringBuilder();
								Report.appendHeader(sb, activity.getString(R.string.md5_computed));
								sb.append('\n');
								sb.append(computedResult == null ? activity.getString(R.string.status_task_failed) : computedResult);

								// selectable
								final TextView resultView = new TextView(activity);
								resultView.setText(sb);
								resultView.setPadding(35, 20, 35, 20);
								resultView.setTextIsSelectable(true);

								new AlertDialog.Builder(activity) // guarded, level 2
										.setTitle(activity.getString(R.string.action_md5_of_file) + ' ' + sourceFile) //
										.setView(resultView)
										//.setMessage(sb)
										.show();
							}
						});
					}
					else
					{
						final AlertDialog.Builder alert2 = new AlertDialog.Builder(activity); // unguarded, level 1
						alert2.setTitle(sourceFile) //
								.setMessage(activity.getString(R.string.status_error_no_file)) //
								.show();
					}
				}
			}
		});
		alert.setNegativeButton(R.string.action_cancel, (dialog, whichButton) -> {
			// canceled.
		});
		alert.show();
	}
}
