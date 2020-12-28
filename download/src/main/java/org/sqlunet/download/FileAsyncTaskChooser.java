/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.app.AlertDialog;
import android.widget.EditText;

import java.io.File;

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
	 * Unzip data base from selected file in container
	 *
	 * @param activity     activity
	 * @param containerDir container dir
	 * @param databasePath database path
	 */
	static public void unzipFromArchive(@NonNull final FragmentActivity activity, final String containerDir, final String databasePath)
	{
		final AlertDialog.Builder alert = new AlertDialog.Builder(activity); // unguarded, level 1
		alert.setTitle(R.string.action_unzip_from_archive);
		alert.setMessage(R.string.hint_unzip_from_archive);
		final EditText input = new EditText(activity);
		String fromPath = containerDir;
		if (fromPath != null)
		{
			fromPath += File.separatorChar + Settings.HINT_DB_ZIP;
			input.setText(fromPath);
			input.setSelection(fromPath.length());
		}
		alert.setView(input);
		alert.setPositiveButton(R.string.action_ok, (dialog, whichButton) -> {

			final String sourceFile = input.getText().toString();
			if (activity.isFinishing() || activity.isDestroyed())
			{
				return;
			}
			FileAsyncTask.launchUnzip(activity, sourceFile, databasePath);
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
	 * @param containerDir container dir
	 * @param zipEntry     zip entry
	 * @param databasePath database path
	 */
	static public void unzipEntryFromArchive(@NonNull final FragmentActivity activity, final String containerDir, final String zipEntry, final String databasePath)
	{
		final AlertDialog.Builder alert = new AlertDialog.Builder(activity); // unguarded, level 1
		alert.setTitle(R.string.action_unzip_from_archive);
		alert.setMessage(R.string.hint_unzip_from_archive);
		final EditText input = new EditText(activity);
		String fromPath = containerDir;
		if (fromPath != null)
		{
			fromPath += File.separatorChar + Settings.HINT_DB_ZIP;
			input.setText(fromPath);
			input.setSelection(fromPath.length());
		}
		alert.setView(input);
		alert.setPositiveButton(R.string.action_ok, (dialog, whichButton) -> {

			final String sourceFile = input.getText().toString();

			if (activity.isFinishing() || activity.isDestroyed())
			{
				return;
			}
			FileAsyncTask.launchUnzip(activity, sourceFile, zipEntry, databasePath);
		});
		alert.setNegativeButton(R.string.action_cancel, (dialog, whichButton) -> {
			// canceled.
		});
		alert.show();
	}

	/**
	 * Copy data base from selected file in container
	 *
	 * @param activity     activity
	 * @param containerDir container dir
	 * @param databasePath database path
	 */
	static public void copyFromFile(@NonNull final FragmentActivity activity, final String containerDir, final String databasePath)
	{
		final AlertDialog.Builder alert = new AlertDialog.Builder(activity); // unguarded, level 1
		alert.setTitle(R.string.action_copy_from_file);
		alert.setMessage(R.string.hint_copy_from_file);
		final EditText input = new EditText(activity);
		String fromPath = containerDir;
		if (fromPath != null)
		{
			fromPath += File.separatorChar + Settings.HINT_DB_ZIP;
			input.setText(fromPath);
			input.setSelection(fromPath.length());
		}
		alert.setView(input);
		alert.setPositiveButton(R.string.action_ok, (dialog, whichButton) -> {

			final String sourceFile = input.getText().toString();
			if (activity.isFinishing() || activity.isDestroyed())
			{
				return;
			}
			FileAsyncTask.launchCopy(activity, sourceFile, databasePath);
		});
		alert.setNegativeButton(R.string.action_cancel, (dialog, whichButton) -> {
			// canceled.
		});
		alert.show();
	}

	/**
	 * Copy data base from selected file in container
	 *
	 * @param activity     activity
	 * @param containerDir container dir
	 */
	static public void md5FromFile(@NonNull final FragmentActivity activity, final String containerDir)
	{
		final AlertDialog.Builder alert = new AlertDialog.Builder(activity); // unguarded, level 1
		alert.setTitle(R.string.action_md5_of_file);
		alert.setMessage(R.string.hint_md5_of_file);
		final EditText input = new EditText(activity);
		String fromPath = containerDir;
		if (fromPath != null)
		{
			// fromPath += File.separatorChar + Settings.HINT_DB_ZIP;
			input.setText(fromPath);
			input.setSelection(fromPath.length());
		}
		alert.setView(input);
		alert.setPositiveButton(R.string.action_ok, (dialog, whichButton) -> {

			final String sourceFile = input.getText().toString();
			if (activity.isFinishing() || activity.isDestroyed())
			{
				return;
			}
			FileAsyncTask.launchMd5(activity, sourceFile);
		});
		alert.setNegativeButton(R.string.action_cancel, (dialog, whichButton) -> {
			// canceled.
		});
		alert.show();
	}
}
