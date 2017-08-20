package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.util.Pair;
import android.view.WindowManager;

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
public class ExecAsyncTask
{
	static private final String TAG = "ExecAsyncTask";

	/**
	 * Execute transaction
	 */
	static final private boolean SKIP_TRANSACTION = false;

	/**
	 * ResultListener
	 */
	final private TaskObserver.Listener listener;

	/**
	 * Publish rate
	 */
	private final int publishRate;

	/**
	 * Activity
	 */
	private final Activity activity;

	/**
	 * Constructor
	 *
	 * @param activity    activity
	 * @param listener    listener
	 * @param publishRate publish rate
	 */
	public ExecAsyncTask(final Activity activity, final TaskObserver.Listener listener, final int publishRate)
	{
		this.activity = activity;
		this.listener = listener;
		this.publishRate = publishRate;
	}

	/**
	 * Execute sql statements
	 *
	 * @param database database path
	 * @param sqls     sql statements
	 */
	@SuppressWarnings("UnusedReturnValue")
	public AsyncTask<Pair<String, String[]>, Integer, Boolean> executeFromSql(final String database, final String... sqls)
	{
		final AsyncTask<Pair<String, String[]>, Integer, Boolean> task = new AsyncTask<Pair<String, String[]>, Integer, Boolean>()
		{
			@SafeVarargs
			@Override
			protected final Boolean doInBackground(final Pair<String, String[]>... params)
			{
				final Pair<String, String[]> args = params[0];
				final String databaseArg = args.first;
				final String[] sqlArgs = args.second;

				SQLiteDatabase db = null;
				try
				{
					// database
					db = SQLiteDatabase.openDatabase(databaseArg, null, SQLiteDatabase.OPEN_READWRITE);

					// execute
					final int total = sqlArgs.length;
					for (int i = 0; i < total; i++)
					{
						final String sql = sqlArgs[i].trim();
						if (sql.isEmpty())
						{
							continue;
						}

						// exec
						db.execSQL(sql);
						Log.d(ExecAsyncTask.TAG, "SQL " + sql);

						// publish
						if (total % ExecAsyncTask.this.publishRate == 0)
						{
							publishProgress(i, total);
						}

						// cancel hook
						if (isCancelled())
						{
							//noinspection BreakStatement
							break;
						}
					}
					publishProgress(total, total);
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
				ExecAsyncTask.this.listener.taskStart(this);
			}

			@Override
			protected void onProgressUpdate(final Integer... params)
			{
				super.onProgressUpdate(params);
				ExecAsyncTask.this.listener.taskUpdate(params[0], params[1]);
			}

			@Override
			protected void onPostExecute(final Boolean result)
			{
				super.onPostExecute(result);
				ExecAsyncTask.this.listener.taskFinish(result);
			}
		};
		//noinspection unchecked
		task.execute(new Pair<>(database, sqls));
		return task;
	}

	/**
	 * Execute sql statements from zipfile
	 *
	 * @param database database path
	 * @param archive  zip file path with sql statements
	 * @param entry    entry
	 */
	public AsyncTask<String, Integer, Boolean> executeFromArchive(final String database, final String archive, final String entry)
	{
		final AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>()
		{
			@Override
			@SuppressWarnings("boxing")
			protected Boolean doInBackground(final String... params)
			{
				final String archiveArg = params[0];
				final String entryArg = params[1];
				final String databaseArg = params[2];
				Log.d(ExecAsyncTask.TAG, archiveArg + '!' + entryArg + '>' + databaseArg);

				// wake lock
				final PowerManager powerManager = (PowerManager) ExecAsyncTask.this.activity.getSystemService(Context.POWER_SERVICE);
				final PowerManager.WakeLock wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DownloaderService");
				wakelock.acquire();

				SQLiteDatabase db = null;
				ZipFile zipFile = null;
				BufferedReader reader = null;
				InputStream is = null;
				try
				{
					// database
					db = SQLiteDatabase.openDatabase(databaseArg, null, SQLiteDatabase.CREATE_IF_NECESSARY);

					// temp store
					// db.execSQL("PRAGMA temp_store = FILE;");

					// zip
					zipFile = new ZipFile(archiveArg);
					final ZipEntry zipEntry = zipFile.getEntry(entryArg);
					if (zipEntry == null)
					{
						throw new IOException("Zip entry not found " + entryArg);
					}

					is = zipFile.getInputStream(zipEntry);

					// open the reader
					InputStreamReader isr = new InputStreamReader(is);
					reader = new BufferedReader(isr);

					// journal off
					if (SKIP_TRANSACTION)
					{
						db.execSQL("PRAGMA journal_mode = OFF;");
					}

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

						// wrap
						if (!line.endsWith(";")) //
						{
							continue;
						}

						// filter
						if (SKIP_TRANSACTION && (sql.equals("BEGIN TRANSACTION;") || sql.equals("COMMIT;"))) //
						{
							continue;
						}

						// execute
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
						@SuppressWarnings("deprecation") boolean isInteractive = Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT ? powerManager.isInteractive() : powerManager.isScreenOn();
						if (isInteractive)
						{
							if (count % ExecAsyncTask.this.publishRate == 0)
							{
								publishProgress(count, -1);
							}
						}

						// cancel hook
						if (isCancelled())
						{
							//noinspection BreakStatement
							break;
						}
					}
					publishProgress(count, count);
					return true;
				}
				catch (IOException e1)
				{
					Log.e(TAG, "While executing from archive", e1);
				}
				finally
				{
					// wake lock
					wakelock.release();

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
				ExecAsyncTask.this.activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				ExecAsyncTask.this.listener.taskStart(this);
			}

			@Override
			protected void onProgressUpdate(final Integer... params)
			{
				ExecAsyncTask.this.listener.taskUpdate(params[0], params[1]);
			}

			@Override
			protected void onPostExecute(final Boolean result)
			{
				ExecAsyncTask.this.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				ExecAsyncTask.this.listener.taskFinish(result);
			}
		};
		return task.execute(archive, entry, database);
	}

	/**
	 * Vacuum database
	 *
	 * @param database database path
	 * @param tempDir  temp directory
	 */
	@SuppressWarnings("unused")
	void vacuum(final String database, final String tempDir)
	{
		final AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>()
		{
			@Override
			protected Void doInBackground(String... params)
			{
				final String databasePathArg = params[0];
				final String tempDirArg = params[1];

				// database
				final SQLiteDatabase db = SQLiteDatabase.openDatabase(databasePathArg, null, SQLiteDatabase.OPEN_READWRITE);

				// set parameters
				// db.execSQL("PRAGMA journal_mode = PERSIST;");
				db.execSQL("PRAGMA temp_store = FILE;");
				db.execSQL("PRAGMA temp_store_directory = '" + tempDirArg + "';");
				Log.d(TAG, "vacuuming in " + tempDirArg);
				db.execSQL("VACUUM");
				Log.d(TAG, "vacuumed in " + tempDirArg);
				db.close();
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				ExecAsyncTask.this.listener.taskFinish(true);
			}

			@Override
			protected void onPreExecute()
			{
			}

			@Override
			protected void onProgressUpdate(Void... values)
			{
			}
		};
		task.execute(database, tempDir);
	}
}
