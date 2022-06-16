/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.util.Pair;
import android.view.Window;
import android.view.WindowManager;

import org.sqlunet.browser.common.R;
import org.sqlunet.concurrency.Task;
import org.sqlunet.concurrency.TaskDialogObserver;
import org.sqlunet.concurrency.TaskObserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

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
	 * Done listener
	 */
	@Nullable
	final private Runnable whenDone;

	/**
	 * Observer
	 */
	@Nullable
	final private TaskObserver.Observer<Number> observer;

	/**
	 * Publish rate
	 */
	private final int publishRate;

	/**
	 * Unit
	 */
	@NonNull
	private final String unit;

	/**
	 * Activity
	 */
	@NonNull
	private final Activity activity;

	/**
	 * Constructor
	 *
	 * @param activity    activity
	 * @param whenDone    done listener
	 * @param observer    observer
	 * @param publishRate publish rate
	 */
	public ExecAsyncTask(@NonNull final Activity activity, @Nullable final Runnable whenDone, @Nullable final TaskObserver.Observer<Number> observer, final int publishRate)
	{
		this.activity = activity;
		this.whenDone = whenDone;
		this.observer = observer;
		this.publishRate = publishRate;
		this.unit = activity.getString(R.string.unit_statement);
	}

	/**
	 * Sql executor
	 */
	static private class AsyncExecuteFromSql extends Task<Pair<String, String[]>, Number, Boolean>
	{
		/*
		 * Done listener
		 */
		@Nullable
		final Runnable whenDone;

		/**
		 * Task observer
		 */
		@Nullable
		final private TaskObserver.Observer<Number> observer;

		/**
		 * Unit
		 */
		private final String unit;

		/**
		 * Publish rate
		 */
		private final int publishRate;

		/**
		 * Constructor
		 *
		 * @param observer    observer
		 * @param publishRate publish rate
		 */
		AsyncExecuteFromSql(@Nullable final Runnable whenDone, @Nullable final TaskObserver.Observer<Number> observer, final int publishRate, final String unit)
		{
			this.whenDone = whenDone;
			this.observer = observer;
			this.publishRate = publishRate;
			this.unit = unit;
		}

		@NonNull
		@SafeVarargs
		@Override
		protected final Boolean doInBackground(final Pair<String, String[]>... params)
		{
			final Pair<String, String[]> args = params[0];
			final String databaseArg = args.first;
			final String[] sqlArgs = args.second;

			try (SQLiteDatabase db = SQLiteDatabase.openDatabase(databaseArg, null, SQLiteDatabase.OPEN_READWRITE))
			{
				// database

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
					if (total % this.publishRate == 0)
					{
						publishProgress(i, total);
					}

					// cooperative exit
					if (isCancelled())
					{
						Log.d(TAG, "Cancelled!");
						break;
					}
					if (Thread.interrupted())
					{
						Log.d(TAG, "Interrupted!");
						break;
					}
				}
				publishProgress(total, total);
				return true;
			}
			catch (@NonNull final Exception e)
			{
				Log.e(TAG, "While executing", e);
			}
			return false;
		}

		@Override
		protected void onPreExecute()
		{
			if (this.observer != null)
			{
				this.observer.taskStart(this);
			}
		}

		@Override
		protected void onProgressUpdate(final Number... params)
		{
			if (this.observer != null)
			{
				this.observer.taskProgress(params[0], params[1], this.unit);
			}
		}

		@Override
		protected void onPostExecute(final Boolean result)
		{
			if (this.observer != null)
			{
				this.observer.taskFinish(result);
			}
			if (this.whenDone != null)
			{
				this.whenDone.run();
			}
		}
	}

	/**
	 * Execute sql statements
	 */
	@NonNull
	@SuppressWarnings({"UnusedReturnValue", "unchecked"})
	public Task<Pair<String, String[]>, Number, Boolean> fromSql()
	{
		return new AsyncExecuteFromSql(this.whenDone, this.observer, this.publishRate, this.unit);
	}

	public static void launchExec(final OperationActivity activity, final Uri uri, final String databasePath, final Runnable whenDone)
	{
	}

	// archive sql

	static private class AsyncExecuteFromArchiveFile extends Task<String, Number, Boolean>
	{
		/**
		 * Data base path
		 */
		@NonNull
		private final String dataBase;

		/**
		 * Zip entry
		 */
		@NonNull
		private final String entry;

		/**
		 * Done listener
		 */
		@Nullable
		final Runnable whenDone;

		/**
		 * Task observer
		 */
		@Nullable
		final private TaskObserver.Observer<Number> observer;

		/**
		 * Publish rate
		 */
		private final int publishRate;

		/**
		 * UNit
		 */
		private final String unit;

		/**
		 * Power manager
		 */
		private final PowerManager powerManager;

		/**
		 * Power manager
		 */
		private final Window window;

		/**
		 * Constructor
		 *
		 * @param whenDone     done listener
		 * @param observer     observer
		 * @param publishRate  publish rate
		 * @param powerManager power manager
		 * @param window       window
		 */
		AsyncExecuteFromArchiveFile(@NonNull final String dataBase, @NonNull final String entry, @Nullable final Runnable whenDone, @Nullable final TaskObserver.Observer<Number> observer, final int publishRate, final String unit, final PowerManager powerManager, final Window window)
		{
			this.dataBase = dataBase;
			this.entry = entry;
			this.whenDone = whenDone;
			this.observer = observer;
			this.publishRate = publishRate;
			this.unit = unit;
			this.powerManager = powerManager;
			this.window = window;
		}

		@NonNull
		@Override
		@SuppressWarnings("boxing")
		protected Boolean doInBackground(final String... params)
		{
			final String archiveArg = params[1];
			Log.d(ExecAsyncTask.TAG, archiveArg + '!' + entry + '>' + dataBase);

			// wake lock
			assert this.powerManager != null;
			final PowerManager.WakeLock wakelock = this.powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "org.sqlunet.browser:Executor");
			wakelock.acquire(20 * 60 * 1000L /*20 minutes*/);

			boolean inTransaction = false;
			try (SQLiteDatabase db = SQLiteDatabase.openDatabase(dataBase, null, SQLiteDatabase.CREATE_IF_NECESSARY))
			{
				try (ZipFile zipFile = new ZipFile(archiveArg))
				{

					// zip
					final ZipEntry zipEntry = zipFile.getEntry(entry);
					if (zipEntry == null)
					{
						throw new IOException("Zip entry not found " + entry);
					}

					try ( // open the reader
					      InputStream is = zipFile.getInputStream(zipEntry); //
					      InputStreamReader isr = new InputStreamReader(is); //
					      BufferedReader reader = new BufferedReader(isr) //
					)
					{
						// journal off
						if (SKIP_TRANSACTION)
						{
							db.execSQL("PRAGMA journal_mode = OFF;");
						}
						// temp store
						// db.execSQL("PRAGMA temp_store = FILE;");

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
							if (sql.equals("BEGIN;") || sql.equals("BEGIN TRANSACTION;")) //
							{
								if (SKIP_TRANSACTION)
								{
									continue;
								}
								inTransaction = true;
							}
							if (sql.equals("COMMIT;") || sql.equals("COMMIT TRANSACTION;") || sql.equals("END;") || sql.equals("END TRANSACTION;")) //
							{
								if (SKIP_TRANSACTION)
								{
									continue;
								}
								inTransaction = false;
							}

							// execute
							try
							{
								// exec one sql
								db.execSQL(sql);
							}
							catch (@NonNull final SQLiteException e)
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
								if (count % this.publishRate == 0)
								{
									publishProgress(count, -1);
								}
							}

							// cooperative exit
							if (isCancelled())
							{
								Log.d(TAG, "Cancelled!");
								break;
							}
							if (Thread.interrupted())
							{
								Log.d(TAG, "Interrupted!");
								break;
							}
						}
						publishProgress(count, count);
						return true;
					}
				}
				catch (IOException e1)
				{
					Log.e(TAG, "While executing from archive", e1);
				}
				finally
				{
					if (db != null)
					{
						if (inTransaction)
						{
							db.endTransaction();
						}
					}
				}
			}
			finally
			{
				// wake lock
				wakelock.release();
			}
			return false;
		}

		@Override
		protected void onPreExecute()
		{
			this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			if (this.observer != null)
			{
				this.observer.taskStart(this);
			}
		}

		@Override
		protected void onProgressUpdate(final Number... params)
		{
			if (this.observer != null)
			{
				this.observer.taskProgress(params[0], params[1], this.unit);
			}
		}

		@Override
		protected void onPostExecute(final Boolean result)
		{
			this.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			if (this.observer != null)
			{
				this.observer.taskFinish(result);
			}
			if (this.whenDone != null)
			{
				this.whenDone.run();
			}
		}
	}

	/**
	 * Sql executor
	 */
	static private class AsyncExecuteFromArchiveUri extends Task<Uri, Number, Boolean>
	{
		/**
		 * Data base path
		 */
		@NonNull
		private final String dataBase;

		/**
		 * Zip entry
		 */
		@NonNull
		private final String entry;

		/**
		 * Done listener
		 */
		@Nullable
		final Runnable whenDone;

		/**
		 * Content resolver
		 */
		@NonNull
		ContentResolver resolver;

		/**
		 * Task observer
		 */
		@Nullable
		final private TaskObserver.Observer<Number> observer;

		/**
		 * Publish rate
		 */
		private final int publishRate;

		/**
		 * UNit
		 */
		private final String unit;

		/**
		 * Power manager
		 */
		private final PowerManager powerManager;

		/**
		 * Power manager
		 */
		private final Window window;

		/**
		 * Constructor
		 *
		 * @param whenDone     done listener
		 * @param observer     observer
		 * @param publishRate  publish rate
		 * @param powerManager power manager
		 * @param window       window
		 */
		AsyncExecuteFromArchiveUri(@NonNull final String dataBase, @NonNull final String entry, @NonNull final ContentResolver resolver, @Nullable final Runnable whenDone, @Nullable final TaskObserver.Observer<Number> observer, final int publishRate, final String unit, final PowerManager powerManager, final Window window)
		{
			this.dataBase = dataBase;
			this.entry = entry;
			this.whenDone = whenDone;
			this.resolver = resolver;
			this.observer = observer;
			this.publishRate = publishRate;
			this.unit = unit;
			this.powerManager = powerManager;
			this.window = window;
		}

		@NonNull
		@Override
		@SuppressWarnings("boxing")
		protected Boolean doInBackground(final Uri... params)
		{
			final Uri archiveArg = params[0];
			Log.d(ExecAsyncTask.TAG, archiveArg.toString() + '!' + entry + '>' + dataBase);

			// wake lock
			assert this.powerManager != null;
			final PowerManager.WakeLock wakelock = this.powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "org.sqlunet.browser:Executor");
			wakelock.acquire(20 * 60 * 1000L /*20 minutes*/);

			boolean inTransaction = false;
			try (SQLiteDatabase db = SQLiteDatabase.openDatabase(dataBase, null, SQLiteDatabase.CREATE_IF_NECESSARY))
			{
				try ( //
				      InputStream is = resolver.openInputStream(archiveArg); //
				      ZipInputStream zis = new ZipInputStream(is); //
				      InputStreamReader isr = new InputStreamReader(zis); BufferedReader reader = new BufferedReader(isr) //
				)
				{
					// temp store
					// db.execSQL("PRAGMA temp_store = FILE;");

					// zip
					ZipEntry zipEntry;
					while ((zipEntry = zis.getNextEntry()) != null)
					{
						if (!zipEntry.getName().equals(entry))
						{
							continue;
						}

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
							if (sql.equals("BEGIN;") || sql.equals("BEGIN TRANSACTION;")) //
							{
								if (SKIP_TRANSACTION)
								{
									continue;
								}
								inTransaction = true;
							}
							if (sql.equals("COMMIT;") || sql.equals("COMMIT TRANSACTION;") || sql.equals("END;") || sql.equals("END TRANSACTION;")) //
							{
								if (SKIP_TRANSACTION)
								{
									continue;
								}
								inTransaction = false;
							}

							// execute
							try
							{
								// exec one sql
								db.execSQL(sql);
							}
							catch (@NonNull final SQLiteException e)
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
								if (count % this.publishRate == 0)
								{
									publishProgress(count, -1);
								}
							}

							// cooperative exit
							if (isCancelled())
							{
								Log.d(TAG, "Cancelled!");
								break;
							}
							if (Thread.interrupted())
							{
								Log.d(TAG, "Interrupted!");
								break;
							}
						}

						publishProgress(count, count);
						return true;
					}
				}
				catch (IOException e1)
				{
					Log.e(TAG, "While executing from archive", e1);
					return false;
				}
				finally
				{
					if (db != null)
					{
						if (inTransaction)
						{
							db.endTransaction();
						}
					}
				}
			}
			finally
			{
				// wake lock
				wakelock.release();
			}
			return false;
		}

		@Override
		protected void onPreExecute()
		{
			this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			if (this.observer != null)
			{
				this.observer.taskStart(this);
			}
		}

		@Override
		protected void onProgressUpdate(final Number... params)
		{
			if (this.observer != null)
			{
				this.observer.taskProgress(params[0], params[1], this.unit);
			}
		}

		@Override
		protected void onPostExecute(final Boolean result)
		{
			this.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			if (this.observer != null)
			{
				this.observer.taskFinish(result);
			}
			if (this.whenDone != null)
			{
				this.whenDone.run();
			}
		}
	}

	/**
	 * Execute sql statements from zip file
	 */
	@NonNull
	public Task<String, Number, Boolean> fromArchiveFile(@NonNull final String dataBase, @NonNull final String entry)
	{
		final PowerManager powerManager = (PowerManager) ExecAsyncTask.this.activity.getSystemService(Context.POWER_SERVICE);
		final Window window = ExecAsyncTask.this.activity.getWindow();
		return new AsyncExecuteFromArchiveFile(dataBase, entry, this.whenDone, this.observer, this.publishRate, this.unit, powerManager, window);
	}

	/**
	 * Execute sql statements from zip uri
	 *
	 * @param dataBase database
	 * @param entry    zip entry
	 * @param resolver content resolver
	 */
	@NonNull
	public Task<Uri, Number, Boolean> fromArchiveUri(@NonNull final String dataBase, @NonNull final String entry, final ContentResolver resolver)
	{
		final PowerManager powerManager = (PowerManager) ExecAsyncTask.this.activity.getSystemService(Context.POWER_SERVICE);
		final Window window = ExecAsyncTask.this.activity.getWindow();
		return new AsyncExecuteFromArchiveUri(dataBase, entry, resolver, this.whenDone, this.observer, this.publishRate, this.unit, powerManager, window);
	}

	/**
	 * Launch exec
	 *
	 * @param activity     activity
	 * @param uri          source uri
	 * @param databasePath database path
	 * @param whenDone     to run when done
	 */
	public static void launchExecZipped(@NonNull final FragmentActivity activity, @NonNull final Uri uri, @NonNull String entry, @NonNull final String databasePath, @Nullable final Runnable whenDone)
	{
		final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager()) // guarded, level 2
				.setTitle(activity.getString(R.string.action_exec_from_uri)) //
				.setMessage(uri.toString());
		launchExecZipped(activity, observer, uri, entry, databasePath, whenDone);
	}

	/**
	 * Launch unzipping of entry in archive file
	 *
	 * @param activity  activity
	 * @param observer  observer
	 * @param sourceUri source zip uri
	 * @param zipEntry  zip entry
	 * @param dest      database path
	 * @param whenDone  to run when done
	 */
	public static void launchExecZipped(@NonNull final Activity activity, @NonNull final TaskObserver.Observer<Number> observer, @NonNull final Uri sourceUri, @NonNull final String zipEntry, @NonNull final String dest, @Nullable final Runnable whenDone)
	{
		final Runnable whenDone2 = () -> {

			if (whenDone != null)
			{
				whenDone.run();
			}
		};

		final Task<Uri, Number, Boolean> task = new ExecAsyncTask(activity, whenDone2, observer, 1000).fromArchiveUri(dest, zipEntry, activity.getContentResolver());
		task.execute(sourceUri);
		observer.taskUpdate(activity.getString(R.string.status_executing) + ' ' + zipEntry);
	}

	// vacuum

	static private class AsyncVacuum extends Task<String, Void, Void>
	{
		/**
		 * Done listener
		 */
		@Nullable
		final Runnable whenDone;

		/**
		 * Task observer
		 */
		@Nullable
		final private TaskObserver.Observer<Number> observer;

		/**
		 * Constructor
		 *
		 * @param whenDone whenDone
		 * @param observer observer
		 */
		AsyncVacuum(@Nullable final Runnable whenDone, @Nullable final TaskObserver.Observer<Number> observer)
		{
			this.whenDone = whenDone;
			this.observer = observer;
		}

		@Nullable
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
			if (this.observer != null)
			{
				this.observer.taskFinish(true);
			}
			if (this.whenDone != null)
			{
				this.whenDone.run();
			}
		}
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
		final Task<String, Void, Void> task = new AsyncVacuum(this.whenDone, this.observer);
		task.execute(database, tempDir);
	}
}
