package org.sqlunet.provider;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Execution manager
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("RedundantSuppression")
public class ExecuteManager
{
	static private final String TAG = "ExecuteManager";

	/**
	 * Manager listener
	 */
	public interface Listener
	{
		/**
		 * Start event
		 */
		void managerStart();

		/**
		 * Finish event
		 *
		 * @param result progressMessage
		 */
		void managerFinish(boolean result);

		/**
		 * Intermediate update event
		 *
		 * @param progress progress value
		 */
		void managerUpdate(int progress);
	}

	/**
	 * Listener
	 */
	final private Listener listener;

	/**
	 * Publish rate
	 */
	private final int publishRate;

	/**
	 * Database path
	 */
	private final String databasePath;

	/**
	 * Constructor
	 *
	 * @param databasePath database file path
	 * @param listener     listener
	 * @param publishRate  publish rate
	 */
	public ExecuteManager(final String databasePath, final Listener listener, final int publishRate)
	{
		this.listener = listener;
		this.databasePath = databasePath;
		this.publishRate = publishRate;
	}

	/**
	 * Execute sql statements
	 *
	 * @param sqls sql statements
	 */
	public AsyncTask<String, Long, Boolean> executeFromSql(final String... sqls)
	{
		final AsyncTask<String, Long, Boolean> task = new AsyncTask<String, Long, Boolean>()
		{
			@SuppressWarnings("boxing")
			@Override
			protected Boolean doInBackground(final String... sqlsToExecute)
			{
				SQLiteDatabase db = null;
				try
				{
					db = SQLiteDatabase.openDatabase(ExecuteManager.this.databasePath, null, SQLiteDatabase.OPEN_READWRITE);
					final int count = sqlsToExecute.length;
					for (int i = 0; i < count; i++)
					{
						final String sql = sqlsToExecute[i].trim();
						if (sql.isEmpty())
						{
							continue;
						}
						db.execSQL(sql);
						Log.d(ExecuteManager.TAG, "SQL " + sql);
						if (count % ExecuteManager.this.publishRate == 0)
						{
							publishProgress((long) (i / (float) count * 1000));
						}
						if (isCancelled())
						{
							//noinspection BreakStatement
							break;
						}
					}
					return true;
				}
				catch (final Exception e)
				{
					Log.e(TAG, "While executing", e);
				}
				finally
				{
					if (db != null)
					{
						db.close();
					}
				}
				return false;
			}

			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				ExecuteManager.this.listener.managerStart();
			}

			@Override
			protected void onProgressUpdate(final Long... progress)
			{
				super.onProgressUpdate(progress);
				ExecuteManager.this.listener.managerUpdate(progress[0].intValue());
			}

			@Override
			protected void onPostExecute(final Boolean result)
			{
				super.onPostExecute(result);
				ExecuteManager.this.listener.managerFinish(result);
			}
		};
		task.execute(sqls);
		return task;
	}

	/**
	 * Execute sql statements from zipfile
	 *
	 * @param archive zip file path with sql statements
	 * @param entry   entry
	 */
	public AsyncTask<String, Long, Boolean> executeFromArchive(final String archive, final String entry)
	{
		final AsyncTask<String, Long, Boolean> task = new AsyncTask<String, Long, Boolean>()
		{
			@Override
			@SuppressWarnings("boxing")
			protected Boolean doInBackground(final String... params)
			{
				String archiveArg = params[0];
				String entryArg = params[1];
				Log.d(ExecuteManager.TAG, archiveArg + " entry " + entryArg);
				ZipFile zipFile = null;
				SQLiteDatabase db = null;
				BufferedReader reader = null;
				InputStream is = null;
				try
				{
					// zip
					zipFile = new ZipFile(archiveArg);
					final ZipEntry zipEntry = zipFile.getEntry(entryArg);
					if (zipEntry == null)
					{
						throw new IOException("Zip entry not found " + entryArg);
					}

					is = zipFile.getInputStream(zipEntry);

					db = SQLiteDatabase.openDatabase(ExecuteManager.this.databasePath, null, SQLiteDatabase.CREATE_IF_NECESSARY);

					// open the reader
					InputStreamReader isr = new InputStreamReader(is);
					reader = new BufferedReader(isr);

					// iterate through lines (assuming each insert has its own line and there's no other stuff)
					long count = 0;
					String sql = null;
					String line;
					while ((line = reader.readLine()) != null)
					{
						// accumulator
						if (sql == null)
						{
							sql = line;
						}
						else
						{
							sql += '\n' + line;
						}

						// dispatch to execution
						if (!line.endsWith(";")) //
						{
							continue;
						}

						try
						{
							// exec one sql
							db.execSQL(sql);
						}
						catch (final SQLiteException e)
						{
							Log.e(TAG, "SQL update failed: " + e.getMessage());
						}

						// accounting
						count++;
						sql = null;

						// progress
						if (count % ExecuteManager.this.publishRate == 0)
						{
							publishProgress(count);
						}
						if (isCancelled())
						{
							//noinspection BreakStatement
							break;
						}
					}
					publishProgress(count);
					return true;
				}
				catch (IOException e1)
				{
					Log.e(TAG, "While executing from archive", e1);
				}
				finally
				{
					if (db != null)
					{
						db.close();
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
					if (reader != null)
					{
						try
						{
							reader.close();
						}
						catch (IOException e)
						{
							//
						}
					}
					if (is != null)
					{
						try
						{
							is.close();
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
				ExecuteManager.this.listener.managerStart();
			}

			@Override
			protected void onProgressUpdate(final Long... progress)
			{
				ExecuteManager.this.listener.managerUpdate(progress[0].intValue());
			}

			@Override
			protected void onPostExecute(final Boolean result)
			{
				ExecuteManager.this.listener.managerFinish(result);
			}
		};
		return task.execute(archive, entry);
	}
}
