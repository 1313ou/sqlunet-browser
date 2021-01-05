/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.util.Pair;
import android.view.Window;
import android.view.WindowManager;

import org.sqlunet.browser.common.R;
import org.sqlunet.concurrency.Task;
import org.sqlunet.concurrency.TaskObserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

	@FunctionalInterface
	public interface DoneListener
	{
		void onDone();
	}

	/**
	 * Result listener
	 */
	@Nullable
	final private DoneListener doneListener;

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
	private final String unit;

	/**
	 * Activity
	 */
	private final Activity activity;

	/**
	 * Constructor
	 *
	 * @param activity     activity
	 * @param doneListener doneListener
	 * @param observer     observer
	 * @param publishRate  publish rate
	 */
	public ExecAsyncTask(final Activity activity, @Nullable final DoneListener doneListener, @Nullable final TaskObserver.Observer<Number> observer, final int publishRate)
	{
		this.activity = activity;
		this.doneListener = doneListener;
		this.observer = observer;
		this.publishRate = publishRate;
		this.unit = activity.getString(R.string.unit_statement);
	}

	static private class AsyncExecuteFromSql extends Task<Pair<String, String[]>, Number, Boolean>
	{
		/*
		 * Done listener
		 */
		@Nullable
		final DoneListener doneListener;

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
		AsyncExecuteFromSql(@Nullable final DoneListener doneListener, @Nullable final TaskObserver.Observer<Number> observer, final int publishRate, final String unit)
		{
			this.doneListener = doneListener;
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
			if (this.doneListener != null)
			{
				this.doneListener.onDone();
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
		return new AsyncExecuteFromSql(this.doneListener, this.observer, this.publishRate, this.unit);
	}

	static private class AsyncExecuteFromArchive extends Task<String, Number, Boolean>
	{
		/**
		 * Done listener
		 */
		@Nullable
		final DoneListener doneListener;

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
		 * @param doneListener doneListener
		 * @param observer     observer
		 * @param publishRate  publish rate
		 * @param powerManager power manager
		 * @param window       window
		 */
		AsyncExecuteFromArchive(@Nullable final DoneListener doneListener, @Nullable final TaskObserver.Observer<Number> observer, final int publishRate, final String unit, final PowerManager powerManager, final Window window)
		{
			this.doneListener = doneListener;
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
			final String databaseArg = params[0];
			final String archiveArg = params[1];
			final String entryArg = params[2];
			Log.d(ExecAsyncTask.TAG, archiveArg + '!' + entryArg + '>' + databaseArg);

			// wake lock
			assert this.powerManager != null;
			final PowerManager.WakeLock wakelock = this.powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "org.sqlunet.browser:Executor");
			wakelock.acquire(20 * 60 * 1000L /*20 minutes*/);

			boolean inTransaction = false;
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
					if (inTransaction)
					{
						db.endTransaction();
					}
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
			if (this.doneListener != null)
			{
				this.doneListener.onDone();
			}
		}
	}

	/**
	 * Execute sql statements from zipfile
	 */
	@Nullable
	public Task<String, Number, Boolean> fromArchive()
	{
		final PowerManager powerManager = (PowerManager) ExecAsyncTask.this.activity.getSystemService(Context.POWER_SERVICE);
		final Window window = ExecAsyncTask.this.activity.getWindow();
		return new AsyncExecuteFromArchive(this.doneListener, this.observer, this.publishRate, this.unit, powerManager, window);
	}

	static private class AsyncVacuum extends Task<String, Void, Void>
	{
		/**
		 * Done listener
		 */
		@Nullable
		final DoneListener doneListener;

		/**
		 * Task observer
		 */
		@Nullable
		final private TaskObserver.Observer<Number> observer;

		/**
		 * Constructor
		 *
		 * @param doneListener doneListener
		 * @param observer     observer
		 */
		AsyncVacuum(@Nullable final DoneListener doneListener, @Nullable final TaskObserver.Observer<Number> observer)
		{
			this.doneListener = doneListener;
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
			if (this.doneListener != null)
			{
				this.doneListener.onDone();
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
		final Task<String, Void, Void> task = new AsyncVacuum(this.doneListener, this.observer);
		task.execute(database, tempDir);
	}
}
