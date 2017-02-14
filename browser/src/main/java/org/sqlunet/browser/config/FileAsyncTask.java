package org.sqlunet.browser.config;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.util.Pair;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.sqlunet.browser.R;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.Storage;
import org.sqlunet.settings.StorageReports;
import org.sqlunet.style.Report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Execution manager
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FileAsyncTask
{
	static private final String TAG = "FileAsyncTask";

	/**
	 * Buffer size
	 */
	static private final int CHUNK_SIZE = 1024;

	/**
	 * MD5Downloader listener
	 */
	public interface ResultListener
	{
		/**
		 * Done
		 *
		 * @param result md5 digest
		 */
		void onResult(final String result);
	}

	/**
	 * Task listener
	 */
	final private TaskObserver.Listener listener;

	/**
	 * Result listener
	 */
	final private ResultListener resultListener;

	/**
	 * Publish rate
	 */
	private final int publishRate;

	/**
	 * Constructor
	 *
	 * @param listener       listener
	 * @param resultListener result listener
	 * @param publishRate    publish rate
	 */
	private FileAsyncTask(final TaskObserver.Listener listener, final ResultListener resultListener, final int publishRate)
	{
		this.listener = listener;
		this.resultListener = resultListener;
		this.publishRate = publishRate;
	}

	/**
	 * Constructor
	 *
	 * @param listener    listener
	 * @param publishRate publish rate
	 */
	private FileAsyncTask(final TaskObserver.Listener listener, final int publishRate)
	{
		this(listener, null, publishRate);
	}

	// CORE

	/**
	 * Copy from source file
	 *
	 * @param src  source file
	 * @param dest dest file
	 */
	private AsyncTask<String, Integer, Boolean> copyFromFile(final String src, final String dest)
	{
		final AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>()
		{
			@SuppressWarnings("boxing")
			@Override
			protected Boolean doInBackground(final String... params)
			{
				String srcArg = params[0];
				String destArg = params[1];
				Log.d(FileAsyncTask.TAG, "COPY FROM " + srcArg + " TO " + destArg);

				File sourceFile = new File(srcArg);
				int length = (int) sourceFile.length();

				FileInputStream in = null;
				FileOutputStream out = null;
				try
				{
					in = new FileInputStream(srcArg);
					out = new FileOutputStream(destArg);

					final byte[] buffer = new byte[CHUNK_SIZE];
					int byteCount = 0;
					int chunkCount = 0;
					int readCount;
					while ((readCount = in.read(buffer)) != -1)
					{
						// write
						out.write(buffer, 0, readCount);

						// count
						byteCount += readCount;
						chunkCount++;

						// publish
						if ((chunkCount % FileAsyncTask.this.publishRate) == 0)
						{
							publishProgress(byteCount, length);
						}

						// cancel hook
						if (isCancelled())
						{
							//noinspection BreakStatement
							break;
						}
					}
					publishProgress(byteCount, length);
					return true;
				}
				catch (final Exception e)
				{
					Log.e(TAG, "While copying", e);
				}
				finally
				{
					if (out != null)
					{
						try
						{
							out.close();
						}
						catch (IOException e)
						{
							//
						}
					}
					if (in != null)
					{
						try
						{
							in.close();
						}
						catch (IOException e)
						{
							//
						}
					}
				}
				return false;
			}

			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				FileAsyncTask.this.listener.taskStart(this);
			}

			@Override
			protected void onProgressUpdate(final Integer... params)
			{
				super.onProgressUpdate(params);
				FileAsyncTask.this.listener.taskUpdate(params[0], params[1]);
			}

			@Override
			protected void onPostExecute(final Boolean result)
			{
				super.onPostExecute(result);
				FileAsyncTask.this.listener.taskFinish(result);
			}

			@Override
			protected void onCancelled(final Boolean result)
			{
				FileAsyncTask.this.listener.taskFinish(false);
			}
		};
		task.execute(src, dest);
		return task;
	}

	/**
	 * Expand from zipfile
	 *
	 * @param srcArchive zip file path with sql statements
	 * @param srcEntry   entry
	 * @param dest       dest file
	 */
	private AsyncTask<String, Integer, Boolean> unzipFromArchive(final String srcArchive, final String srcEntry, final String dest)
	{
		final AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>()
		{
			@Override
			@SuppressWarnings("boxing")
			protected Boolean doInBackground(final String... params)
			{
				String srcArchiveArg = params[0];
				String srcEntryArg = params[1];
				String destArg = params[2];
				Log.d(FileAsyncTask.TAG, "EXPAND FROM " + srcArchiveArg + " (entry " + srcEntryArg + ") TO " + destArg);

				ZipFile zipFile = null;
				InputStream in = null;
				FileOutputStream out = null;

				try
				{
					// in
					zipFile = new ZipFile(srcArchiveArg);
					final ZipEntry zipEntry = zipFile.getEntry(srcEntryArg);
					if (zipEntry == null)
					{
						throw new IOException("Zip entry not found " + srcEntryArg);
					}
					in = zipFile.getInputStream(zipEntry);
					int length = (int) zipEntry.getSize();

					// out
					out = new FileOutputStream(destArg);

					final byte[] buffer = new byte[CHUNK_SIZE];
					int byteCount = 0;
					int chunkCount = 0;
					int readCount;
					while ((readCount = in.read(buffer)) != -1)
					{
						// write
						out.write(buffer, 0, readCount);

						// count
						byteCount += readCount;
						chunkCount++;

						// publish
						if ((chunkCount % FileAsyncTask.this.publishRate) == 0)
						{
							publishProgress(byteCount, length);
						}

						// cancel hook
						if (isCancelled())
						{
							//noinspection BreakStatement
							break;
						}
					}
					publishProgress(byteCount, length);
					return true;
				}
				catch (IOException e1)
				{
					Log.e(TAG, "While executing from archive", e1);
				}
				finally
				{
					if (out != null)
					{
						try
						{
							out.close();
						}
						catch (IOException e)
						{
							//
						}
					}
					if (zipFile != null)
					{
						try
						{
							zipFile.close();
						}
						catch (IOException e)
						{
							Log.e(TAG, "While closing archive", e);
						}
					}
					if (in != null)
					{
						try
						{
							in.close();
						}
						catch (IOException e)
						{
							//
						}
					}
				}
				return false;
			}

			@Override
			protected void onPreExecute()
			{
				FileAsyncTask.this.listener.taskStart(this);
			}

			@Override
			protected void onProgressUpdate(final Integer... params)
			{
				FileAsyncTask.this.listener.taskUpdate(params[0], params[1]);
			}

			@Override
			protected void onPostExecute(final Boolean result)
			{
				FileAsyncTask.this.listener.taskFinish(result);
			}

			@Override
			protected void onCancelled(final Boolean result)
			{
				FileAsyncTask.this.listener.taskFinish(false);
			}
		};
		return task.execute(srcArchive, srcEntry, dest);
	}

	/**
	 * Md5 check sum of file
	 *
	 * @param targetFile file path
	 */
	private AsyncTask<String, Integer, String> md5FromFile(final String targetFile)
	{
		final AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>()
		{
			@Override
			@SuppressWarnings("boxing")
			protected String doInBackground(final String... params)
			{
				final String pathArg = params[0];
				Log.d(TAG, "MD5 " + pathArg);
				FileInputStream fis = null;
				DigestInputStream dis = null;
				try
				{
					File sourceFile = new File(pathArg);
					int length = (int) sourceFile.length();

					fis = new FileInputStream(pathArg);

					MessageDigest md = MessageDigest.getInstance("MD5");
					dis = new DigestInputStream(fis, md);

					final byte[] buffer = new byte[CHUNK_SIZE];
					int byteCount = 0;
					int chunkCount = 0;
					@SuppressWarnings("UnusedAssignment") int readCount = 0;

					// read decorated stream (dis) to EOF as normal
					while ((readCount = dis.read(buffer)) != -1)
					{
						// count
						byteCount += readCount;
						chunkCount++;

						// publish
						if ((chunkCount % FileAsyncTask.this.publishRate) == 0)
						{
							publishProgress(byteCount, length);
						}
					}
					byte[] digest = md.digest();
					return digestToString(digest);
				}
				catch (FileNotFoundException e)
				{
					return null;
				}
				catch (NoSuchAlgorithmException e)
				{
					return null;
				}
				catch (IOException e)
				{
					return null;
				}
				finally
				{
					if (dis != null)
					{
						try
						{
							dis.close();
						}
						catch (IOException e)
						{
							//
						}
					}
					if (fis != null)
					{
						try
						{
							fis.close();
						}
						catch (IOException e)
						{
							//
						}
					}
				}
			}

			@Override
			protected void onPreExecute()
			{
				FileAsyncTask.this.listener.taskStart(this);
			}

			@Override
			protected void onProgressUpdate(final Integer... params)
			{
				FileAsyncTask.this.listener.taskUpdate(params[0], params[1]);
			}

			@Override
			protected void onCancelled(final String result)
			{
				FileAsyncTask.this.listener.taskFinish(false);
				FileAsyncTask.this.resultListener.onResult(null);
			}

			@Override
			protected void onPostExecute(final String result)
			{
				FileAsyncTask.this.listener.taskFinish(result != null);
				FileAsyncTask.this.resultListener.onResult(result);
			}
		};
		return task.execute(targetFile);
	}

	// DIALOG

	/**
	 * Unzip data base from
	 *
	 * @param context      context
	 * @param databasePath database path
	 */
	static void unzipFromArchive(final Context context, final String databasePath)
	{
		String fromPath = Settings.getCachePref(context);

		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(R.string.action_unzip_from_archive);
		alert.setMessage(R.string.hint_unzip_from_archive);
		final EditText input = new EditText(context);
		if (fromPath != null)
		{
			fromPath += File.separatorChar + Storage.DBFILEZIP;
			input.setText(fromPath);
			input.setSelection(fromPath.length());
		}
		alert.setView(input);
		alert.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				final String sourceFile = input.getText().toString();
				final TaskObserver.Listener listener = new TaskObserver.DialogListener(context, R.string.action_unzip_from_archive, sourceFile, null);
				new FileAsyncTask(listener, 1000).unzipFromArchive(sourceFile, Storage.DBFILE, databasePath);
			}
		});
		alert.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				// canceled.
			}
		});
		alert.show();
	}

	/**
	 * Copy data base from file in cache
	 *
	 * @param context      context
	 * @param databasePath database path
	 */
	static void copyFromFile(final Context context, final String databasePath)
	{
		String fromPath = Settings.getCachePref(context);

		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(R.string.action_copy_from_file);
		alert.setMessage(R.string.hint_copy_from_file);
		final EditText input = new EditText(context);
		if (fromPath != null)
		{
			fromPath += File.separatorChar + Storage.DBFILE;
			input.setText(fromPath);
			input.setSelection(fromPath.length());
		}
		alert.setView(input);
		alert.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				final String sourceFile = input.getText().toString();
				final TaskObserver.Listener listener = new TaskObserver.DialogListener(context, R.string.action_copy_from_file, sourceFile, null);
				new FileAsyncTask(listener, 1000).copyFromFile(sourceFile, databasePath);
			}
		});
		alert.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				// canceled.
			}
		});
		alert.show();
	}

	/**
	 * MD5
	 *
	 * @param context        context
	 * @param path           path
	 * @param resultListener result listener
	 */
	static public void md5(final Context context, final String path, final ResultListener resultListener)
	{
		final TaskObserver.Listener listener = new TaskObserver.DialogListener(context, R.string.action_md5, path, null);
		new FileAsyncTask(listener, resultListener, 1000).md5FromFile(path);
	}

	static public void md5(final Context context)
	{
		final Pair<CharSequence[], CharSequence[]> result = StorageReports.getStyledCachesNamesValues(context);
		final CharSequence[] names = result.first;
		final CharSequence[] values = result.second;
		final Pair<CharSequence[], CharSequence[]> result2 = StorageReports.getStorageDirectoriesNamesValues(context);
		final CharSequence[] names2 = result2.first;
		final CharSequence[] values2 = result2.second;

		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(R.string.action_md5_ask);
		alert.setMessage(R.string.hint_md5);

		/*
		final EditText input0 = new EditText(context);
		input0.setText(Storage.getSqlUNetStorage(context).getAbsolutePath()+ File.separatorChar + Storage.DBFILE);
		input0.setSelection(Storage.getSqlUNetStorage(context).getAbsolutePath()+ File.separatorChar + Storage.DBFILE.length());
		*/
		/*
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, list);
		final ListView input = new ListView(context);
		input.setAdapter(adapter);
		alert.setView(input);
		*/

		final RadioGroup input = new RadioGroup(context);
		for (int i = 0; i < names.length && i < values.length; i++)
		{
			//final CharSequence name = names[i];
			final CharSequence value = values[i];
			final String dir = value.toString();
			for (String fileName : new String[]{Storage.DBFILE, Storage.DBFILEZIP, Storage.DBSQLZIP})
			{
				final File file = new File(dir, fileName);
				if (file.exists())
				{
					//final SpannableStringBuilder sb = new SpannableStringBuilder();
					//Report.appendHeader(sb, name.toString().split("\n")[0]).append('\n').append(file.getAbsolutePath());
					final RadioButton radioButton = new RadioButton(context);
					radioButton.setText(file.getAbsolutePath());
					radioButton.setTag(file.getAbsolutePath());
					input.addView(radioButton);
				}
			}
		}
		for (int i = 0; i < names2.length && i < values2.length; i++)
		{
			//final CharSequence name = names2[i];
			final CharSequence value = values2[i];
			final String dir = value.toString();
			for (String fileName : new String[]{Storage.DBFILE, Storage.DBFILEZIP, Storage.DBSQLZIP})
			{
				final File file = new File(dir, fileName);
				if (file.exists())
				{
					//final SpannableStringBuilder sb = new SpannableStringBuilder();
					//Report.appendHeader(sb, name.toString().split("\n")[0]).append('\n').append(file.getAbsolutePath());
					final RadioButton radioButton = new RadioButton(context);
					radioButton.setText(file.getAbsolutePath());
					radioButton.setTag(file.getAbsolutePath());
					input.addView(radioButton);
				}
			}
		}
		alert.setView(input);
		alert.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				dialog.dismiss();

				//final String sourceFile = input0.getText();
				//final String sourceFile = input.getSelectedItem().toString();
				int childCount = input.getChildCount();
				for (int i = 0; i < childCount; i++)
				{
					final RadioButton radioButton = (RadioButton) input.getChildAt(i);
					if (radioButton.getId() == input.getCheckedRadioButtonId())
					{
						final String sourceFile = radioButton.getTag().toString();
						// String md5;
						if (new File(sourceFile).exists())
						{
							// TODO md5 = md5(sourceFile);
							FileAsyncTask.md5(context, sourceFile, new FileAsyncTask.ResultListener()
							{
								@Override
								public void onResult(final String computedResult)
								{
									final SpannableStringBuilder sb = new SpannableStringBuilder();
									Report.appendHeader(sb, context.getString(R.string.md5_computed));
									sb.append('\n');
									sb.append(computedResult == null ? context.getString(R.string.status_task_failed) : computedResult);

									// selectable
									final TextView result = new TextView(context);
									result.setText(sb);
									result.setPadding(35, 20, 35, 20);
									result.setTextIsSelectable(true);

									final AlertDialog.Builder alert2 = new AlertDialog.Builder(context);
									alert2.setTitle(context.getString(R.string.action_md5_of) + ' ' + sourceFile) //
											.setView(result)
											//.setMessage(sb)
											.show();
								}
							});
						}
						else
						{
							final AlertDialog.Builder alert2 = new AlertDialog.Builder(context);
							alert2.setTitle(sourceFile) //
									.setMessage(context.getString(R.string.status_data_fail)) //
									.show();
						}
					}
				}
			}
		});
		alert.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				// canceled.
			}
		});
		alert.show();
	}

	private static String digestToString(byte... byteArray)
	{
		if (byteArray == null)
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (byte b : byteArray)
		{
			sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}
}
