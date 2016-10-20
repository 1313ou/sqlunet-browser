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
	private static final String TAG = "ExecuteManager"; //$NON-NLS-1$

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
		 * @param result result
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
	@SuppressWarnings("UnusedReturnValue")
	public AsyncTask<String, Integer, Boolean> executeFromSql(final String... sqls)
	{
		final AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>()
		{
			@SuppressWarnings("boxing")
			@Override
			protected Boolean doInBackground(final String... sqls0)
			{
				SQLiteDatabase db = null;
				try
				{
					db = SQLiteDatabase.openDatabase(ExecuteManager.this.databasePath, null, SQLiteDatabase.OPEN_READWRITE);
					final int count = sqls0.length;
					for (int i = 0; i < count; i++)
					{
						final String sql = sqls0[i].trim();
						if (sql.isEmpty())
						{
							continue;
						}
						db.execSQL(sql);
						Log.d(ExecuteManager.TAG, "SQL " + sql); //$NON-NLS-1$
						if (count % ExecuteManager.this.publishRate == 0)
						{
							publishProgress((int) (i / (float) count * 1000));
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
			protected void onProgressUpdate(final Integer... progress)
			{
				super.onProgressUpdate(progress);
				ExecuteManager.this.listener.managerUpdate(progress[0]);
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
	 * @param archive0 zip file path with sql statements
	 * @param entry0   entry
	 */
	public AsyncTask<String, Integer, Boolean> executeFromArchive(final String archive0, final String entry0)
	{
		final AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>()
		{
			@Override
			@SuppressWarnings("boxing")
			protected Boolean doInBackground(final String... params)
			{
				String archive = params[0];
				String entry = params[1];
				Log.d(ExecuteManager.TAG, archive + " entry " + entry); //$NON-NLS-1$
				ZipFile zipFile = null;
				SQLiteDatabase db = null;
				BufferedReader reader = null;
				InputStream is = null;
				try
				{
					// zip
					zipFile = new ZipFile(archive);
					final ZipEntry zipEntry = zipFile.getEntry(entry);
					if (zipEntry == null)
					{
						throw new IOException("Zip entry not found " + entry); //$NON-NLS-1$
					}

					is = zipFile.getInputStream(zipEntry);

					db = SQLiteDatabase.openDatabase(ExecuteManager.this.databasePath, null, SQLiteDatabase.CREATE_IF_NECESSARY);

					// open the reader
					InputStreamReader isr = new InputStreamReader(is);
					reader = new BufferedReader(isr);

					// iterate through lines (assuming each insert has its own line and there's no other stuff)
					int count = 0;
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
						if (!line.endsWith(";")) //$NON-NLS-1$
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
							Log.e(TAG, "SQL update failed: " + e.getMessage()); //$NON-NLS-1$
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
			protected void onProgressUpdate(final Integer... progress)
			{
				ExecuteManager.this.listener.managerUpdate(progress[0]);
			}

			@Override
			protected void onPostExecute(final Boolean result)
			{
				ExecuteManager.this.listener.managerFinish(result);
			}
		};
		return task.execute(archive0, entry0);
	}
}
