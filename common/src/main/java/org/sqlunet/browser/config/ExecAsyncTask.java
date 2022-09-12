/*
 * Copyright (c) 2022. Bernard Bou
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
import androidx.core.util.Consumer;
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
	final private Consumer<Boolean> whenDone;

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
	public ExecAsyncTask(@NonNull final Activity activity, @Nullable final Consumer<Boolean> whenDone, @Nullable final TaskObserver.Observer<Number> observer, final int publishRate)
	{
		this.activity = activity;
		this.whenDone = whenDone;
		this.observer = observer;
		this.publishRate = publishRate;
		this.unit = activity.getString(R.string.unit_statement);
	}

	// S Q L

	/**
	 * Sql executor
	 */
	static private class AsyncExecuteFromSql extends Task<String[], Number, Boolean>
	{
		/**
		 * Data base path
		 */
		@NonNull
		private final String dataBase;

		/*
		 * Done listener
		 */
		@Nullable
		final Consumer<Boolean> whenDone;

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
		AsyncExecuteFromSql(@NonNull final String dataBase, @Nullable final Consumer<Boolean> whenDone, @Nullable final TaskObserver.Observer<Number> observer, final int publishRate, final String unit)
		{
			this.dataBase = dataBase;
			this.whenDone = whenDone;
			this.observer = observer;
			this.publishRate = publishRate;
			this.unit = unit;
		}

		@NonNull
		@Override
		protected final Boolean doInBackground(final String[]... params)
		{
			final String[] sqlArgs = params[0];

			try (SQLiteDatabase db = SQLiteDatabase.openDatabase(dataBase, null, SQLiteDatabase.OPEN_READWRITE))
			{
				// database

				// execute
				final int total = sqlArgs.length;
				int successCount = 0;
				for (int i = 0; i < total; i++)
				{
					final String sql = sqlArgs[i].trim();
					if (sql.isEmpty())
					{
						continue;
					}

					// exec
					db.execSQL(sql);
					successCount++;
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
				publishProgress(successCount, total);
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
				this.whenDone.accept(result);
			}
		}
	}

	/**
	 * Execute sql statements
	 */
	@NonNull
	@SuppressWarnings({"UnusedReturnValue", "unchecked"})
	public Task<String[], Number, Boolean> fromSql(@NonNull final String dataBase)
	{
		return new AsyncExecuteFromSql(dataBase, this.whenDone, this.observer, this.publishRate, this.unit);
	}

	// A R C H I V E   F I L E

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
		final Consumer<Boolean> whenDone;

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
		AsyncExecuteFromArchiveFile(@NonNull final String dataBase, @NonNull final String entry, @Nullable final Consumer<Boolean> whenDone, @Nullable final TaskObserver.Observer<Number> observer, final int publishRate, final String unit, final PowerManager powerManager, final Window window)
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
			Log.d(ExecAsyncTask.TAG, archiveArg + '!' + entry + " -> " + dataBase);

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
						boolean status = true;
						int count = 0;
						int successCount = 0;
						String sql = null;
						String line;
						while ((line = reader.readLine()) != null)
						{
							if (line.startsWith("-- "))
							{
								continue;
							}

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
								successCount++;
							}
							catch (@NonNull final SQLiteException e)
							{
								Log.e(TAG, "SQL exec failed: " + e.getMessage());
								status = false;
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
						publishProgress(successCount, count);
						return status;
					}
				}
				catch (IOException e1)
				{
					Log.e(TAG, "While executing SQL from archive", e1);
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
				this.whenDone.accept(result);
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

	// U R I

	static private class AsyncExecuteFromUri extends Task<Uri, Number, Boolean>
	{
		/**
		 * Data base path
		 */
		@NonNull
		private final String dataBase;

		/**
		 * Done listener
		 */
		@Nullable
		final Consumer<Boolean> whenDone;

		/**
		 * Content resolver
		 */
		@NonNull
		final ContentResolver resolver;

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
		AsyncExecuteFromUri(@NonNull final String dataBase, @NonNull final ContentResolver resolver, @Nullable final Consumer<Boolean> whenDone, @Nullable final TaskObserver.Observer<Number> observer, final int publishRate, final String unit, final PowerManager powerManager, final Window window)
		{
			this.dataBase = dataBase;
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
			final Uri uri = params[0];
			Log.d(ExecAsyncTask.TAG, uri.toString() + " -> " + dataBase);
			if (!resolver.getType(uri).startsWith("text/plain"))
			{
				Log.e(TAG, "Illegal mime type " + resolver.getType(uri));
				return false;
			}

			// wake lock
			assert this.powerManager != null;
			final PowerManager.WakeLock wakelock = this.powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "org.sqlunet.browser:Executor");
			wakelock.acquire(20 * 60 * 1000L /*20 minutes*/);

			boolean inTransaction = false;
			try (SQLiteDatabase db = SQLiteDatabase.openDatabase(dataBase, null, SQLiteDatabase.CREATE_IF_NECESSARY))
			{
				try ( // open the reader
				      InputStream is = resolver.openInputStream(uri); //
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
					boolean status = true;
					int count = 0;
					int successCount = 0;
					String sql = null;
					String line;
					while ((line = reader.readLine()) != null)
					{
						if (line.startsWith("-- "))
						{
							continue;
						}

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
							successCount++;
						}
						catch (@NonNull final SQLiteException e)
						{
							Log.e(TAG, "SQL update failed: " + e.getMessage());
							status = false;
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
					publishProgress(successCount, count);
					return status;
				}
				catch (IOException e1)
				{
					Log.e(TAG, "While executing SQL from uri", e1);
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
				this.whenDone.accept(result);
			}
		}
	}

	/**
	 * Execute sql statements from zip file
	 */
	@NonNull
	public Task<Uri, Number, Boolean> fromUri(@NonNull final String dataBase, final ContentResolver resolver)
	{
		final PowerManager powerManager = (PowerManager) ExecAsyncTask.this.activity.getSystemService(Context.POWER_SERVICE);
		final Window window = ExecAsyncTask.this.activity.getWindow();
		return new AsyncExecuteFromUri(dataBase, resolver, this.whenDone, this.observer, this.publishRate, this.unit, powerManager, window);
	}

	/**
	 * Launch uri
	 *
	 * @param activity     activity
	 * @param uri          source zip uri
	 * @param databasePath database path
	 * @param whenDone     to run when done
	 */
	public static void launchExecUri(final FragmentActivity activity, final Uri uri, final String databasePath, final Consumer<Boolean> whenDone)
	{
		final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager()) // guarded, level 2
				.setTitle(activity.getString(R.string.action_exec_from_uri)) //
				.setMessage(uri.toString());
		launchExecUri(activity, observer, uri, databasePath, whenDone);
	}

	/**
	 * Launch uri
	 *
	 * @param activity activity
	 * @param observer observer
	 * @param uri      source zip uri
	 * @param dest     database path
	 * @param whenDone to run when done
	 */
	public static void launchExecUri(@NonNull final Activity activity, @NonNull final TaskObserver.Observer<Number> observer, @NonNull final Uri uri, @NonNull final String dest, @Nullable final Consumer<Boolean> whenDone)
	{
		final Consumer<Boolean> whenDone2 = (result) -> {

			if (whenDone != null)
			{
				whenDone.accept(result);
			}
		};

		final Task<Uri, Number, Boolean> task = new ExecAsyncTask(activity, whenDone2, observer, 1000).fromUri(dest, activity.getContentResolver());
		task.execute(uri);
		observer.taskUpdate(activity.getString(R.string.status_executing));
	}

	// A R C H I V E   U R I

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
		final Consumer<Boolean> whenDone;

		/**
		 * Content resolver
		 */
		@NonNull
		final ContentResolver resolver;

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
		AsyncExecuteFromArchiveUri(@NonNull final String dataBase, @NonNull final String entry, @NonNull final ContentResolver resolver, @Nullable final Consumer<Boolean> whenDone, @Nullable final TaskObserver.Observer<Number> observer, final int publishRate, final String unit, final PowerManager powerManager, final Window window)
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
			final Uri uri = params[0];
			Log.d(ExecAsyncTask.TAG, uri.toString() + '!' + entry + " -> " + dataBase);
			if (!resolver.getType(uri).startsWith("application/zip"))
			{
				Log.e(TAG, "Illegal mime type " + resolver.getType(uri));
				return false;
			}

			// wake lock
			assert this.powerManager != null;
			final PowerManager.WakeLock wakelock = this.powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "org.sqlunet.browser:Executor");
			wakelock.acquire(20 * 60 * 1000L /*20 minutes*/);

			boolean inTransaction = false;
			try (SQLiteDatabase db = SQLiteDatabase.openDatabase(dataBase, null, SQLiteDatabase.CREATE_IF_NECESSARY))
			{
				try ( //
				      InputStream is = resolver.openInputStream(uri); //
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
						boolean status = true;
						int count = 0;
						int successCount = 0;
						String sql = null;
						String line;
						while ((line = reader.readLine()) != null)
						{
							if (line.startsWith("-- "))
							{
								continue;
							}

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
								successCount++;
							}
							catch (@NonNull final SQLiteException e)
							{
								Log.e(TAG, "SQL update failed: " + e.getMessage());
								status = false;
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

						publishProgress(successCount, count);
						return status;
					}
					// found none
					return false;
				}
				catch (IOException e1)
				{
					Log.e(TAG, "While executing from archive uri", e1);
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
				this.whenDone.accept(result);
			}
		}
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
	 * Launch exec of archive uri
	 *
	 * @param activity     activity
	 * @param uri          source uri
	 * @param databasePath database path
	 * @param whenDone     to run when done
	 */
	public static void launchExecZippedUri(@NonNull final FragmentActivity activity, @NonNull final Uri uri, @NonNull String entry, @NonNull final String databasePath, @Nullable final Consumer<Boolean> whenDone)
	{
		final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager()) // guarded, level 2
				.setTitle(activity.getString(R.string.action_exec_from_uri)) //
				.setMessage(uri.toString());
		launchExecZippedUri(activity, observer, uri, entry, databasePath, whenDone);
	}

	/**
	 * Launch exec of entry in archive uri
	 *
	 * @param activity  activity
	 * @param observer  observer
	 * @param sourceUri source zip uri
	 * @param zipEntry  zip entry
	 * @param dest      database path
	 * @param whenDone  to run when done
	 */
	public static void launchExecZippedUri(@NonNull final Activity activity, @NonNull final TaskObserver.Observer<Number> observer, @NonNull final Uri sourceUri, @NonNull final String zipEntry, @NonNull final String dest, @Nullable final Consumer<Boolean> whenDone)
	{
		final Consumer<Boolean> whenDone2 = (result) -> {

			if (whenDone != null)
			{
				whenDone.accept(result);
			}
		};

		final Task<Uri, Number, Boolean> task = new ExecAsyncTask(activity, whenDone2, observer, 1000).fromArchiveUri(dest, zipEntry, activity.getContentResolver());
		task.execute(sourceUri);
		observer.taskUpdate(activity.getString(R.string.status_executing) + ' ' + zipEntry);
	}

	// V A C U U M

	static private class AsyncVacuum extends Task<String, Void, Boolean>
	{
		/**
		 * Done listener
		 */
		@Nullable
		final Consumer<Boolean> whenDone;

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
		AsyncVacuum(@Nullable final Consumer<Boolean> whenDone, @Nullable final TaskObserver.Observer<Number> observer)
		{
			this.whenDone = whenDone;
			this.observer = observer;
		}

		@Nullable
		@Override
		protected Boolean doInBackground(String... params)
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
		protected void onPostExecute(Boolean result)
		{
			if (this.observer != null)
			{
				this.observer.taskFinish(true);
			}
			if (this.whenDone != null)
			{
				this.whenDone.accept(result);
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
		final Task<String, Void, Boolean> task = new AsyncVacuum(this.whenDone, this.observer);
		task.execute(database, tempDir);
	}
}
