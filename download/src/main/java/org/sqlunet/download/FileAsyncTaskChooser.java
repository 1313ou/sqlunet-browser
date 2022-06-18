/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.app.AlertDialog;
import android.util.Pair;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

/**
 * File async task chooser
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FileAsyncTaskChooser
{
	// static private final String TAG = "FileAsyncTaskChooser";

	/**
	 * Copy data base from selected file in container
	 *
	 * @param activity     activity
	 * @param databasePath database path
	 * @param directories  directory names-values array
	 */
	@SafeVarargs
	static public void copyFromFile(@NonNull final FragmentActivity activity, @NonNull final String databasePath, final Pair<CharSequence[], CharSequence[]>... directories)
	{
		// collect sources
		final RadioGroup input = Chooser.collectFiles(activity, directories);
		if (input == null)
		{
			Toast.makeText(activity, R.string.md5_none, Toast.LENGTH_SHORT).show();
			return;
		}

		// display sources
		final AlertDialog.Builder alert = new AlertDialog.Builder(activity); // unguarded, level 1
		alert.setTitle(R.string.action_copy_from_file);
		alert.setMessage(R.string.hint_copy_from_file);
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
					if (!activity.isFinishing() && !activity.isDestroyed())
					{
						FileAsyncTask.launchCopy(activity, sourceFile, databasePath, null);
					}
				}
			}
		});
		alert.setNegativeButton(R.string.action_cancel, (dialog, whichButton) -> {
			// canceled.
		});
		alert.show();
	}

	/**
	 * Unzip data base from selected file in container
	 *
	 * @param activity     activity
	 * @param databasePath database path
	 * @param directories  directory names-values array
	 */
	@SafeVarargs
	static public void unzipFromArchive(@NonNull final FragmentActivity activity, @NonNull final String databasePath, final Pair<CharSequence[], CharSequence[]>... directories)
	{
		// collect sources
		final RadioGroup input = Chooser.collectFiles(activity, directories);
		if (input == null)
		{
			Toast.makeText(activity, R.string.md5_none, Toast.LENGTH_SHORT).show();
			return;
		}

		// display sources
		final AlertDialog.Builder alert = new AlertDialog.Builder(activity); // unguarded, level 1
		alert.setTitle(R.string.action_unzip_from_archive);
		alert.setMessage(R.string.hint_unzip_from_archive);
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
					if (!activity.isFinishing() && !activity.isDestroyed())
					{
						FileAsyncTask.launchUnzip(activity, sourceFile, databasePath, null);
					}
				}
			}
		});
		alert.setNegativeButton(R.string.action_cancel, (dialog, whichButton) -> {
			// canceled.
		});
		alert.show();
	}

	/**
	 * Unzip data base from selected file in container
	 *
	 * @param activity     activity
	 * @param databasePath database path
	 * @param directories  directory names-values array
	 */
	@SafeVarargs
	static public void unzipEntryFromArchive(@NonNull final FragmentActivity activity, @NonNull final String databasePath, final Pair<CharSequence[], CharSequence[]>... directories)
	{
		// collect sources
		final RadioGroup archiveInput1 = Chooser.collectFiles(activity, directories);
		if (archiveInput1 == null)
		{
			Toast.makeText(activity, R.string.md5_none, Toast.LENGTH_SHORT).show();
			return;
		}

		// assemble composite input
		final LinearLayout input = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.zip_input, null);
		final EditText entryInput = input.findViewById(R.id.zip_entry);
		entryInput.setText(Settings.HINT_DB_ZIP_ENTRY);
		entryInput.setSelection(Settings.HINT_DB_ZIP_ENTRY.length());
		input.addView(archiveInput1);

		// display sources
		final AlertDialog.Builder alert = new AlertDialog.Builder(activity); // unguarded, level 1
		alert.setTitle(R.string.action_unzip_from_archive);
		alert.setMessage(R.string.hint_unzip_from_archive);
		alert.setView(input);
		alert.setPositiveButton(R.string.action_ok, (dialog, whichButton) -> {
			dialog.dismiss();
			final String zipEntry = entryInput.getText().toString();
			if (!zipEntry.isEmpty())
			{
				int childCount = input.getChildCount();
				for (int i = 0; i < childCount; i++)
				{
					final RadioButton radioButton = (RadioButton) input.getChildAt(i);
					if (radioButton.getId() == archiveInput1.getCheckedRadioButtonId())
					{
						final String sourceFile = radioButton.getTag().toString();
						if (!activity.isFinishing() && !activity.isDestroyed())
						{
							FileAsyncTask.launchUnzip(activity, sourceFile, zipEntry, databasePath, null);
						}
					}
				}
			}
		});
		alert.setNegativeButton(R.string.action_cancel, (dialog, whichButton) -> {
			// canceled.
		});
		alert.show();
	}

	/**
	 * Copy data base from selected file in container
	 *
	 * @param activity    activity
	 * @param directories directory names-values array
	 */
	@SafeVarargs
	static public void md5FromFile(@NonNull final FragmentActivity activity, final Pair<CharSequence[], CharSequence[]>... directories)
	{
		// collect sources
		final RadioGroup input = Chooser.collectFiles(activity, directories);
		if (input == null)
		{
			Toast.makeText(activity, R.string.md5_none, Toast.LENGTH_SHORT).show();
			return;
		}

		// display sources
		final AlertDialog.Builder alert = new AlertDialog.Builder(activity); // unguarded, level 1
		alert.setTitle(R.string.action_md5_of_file);
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
					if (!activity.isFinishing() && !activity.isDestroyed())
					{
						FileAsyncTask.launchMd5(activity, sourceFile, null);
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
