/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.download;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.net.Uri;

import org.sqlunet.concurrency.Task;
import org.sqlunet.concurrency.TaskDialogObserver;
import org.sqlunet.concurrency.TaskObserver;
import org.sqlunet.deploy.ObservedDeploy;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.fragment.app.FragmentActivity;

/**
 * File async tasks
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FileAsyncTask
{
	// static private final String TAG = "FileAsyncTask";

	/**
	 * Task observer
	 */
	final private TaskObserver.Observer<Number> observer;

	/**
	 * Result listener
	 */
	final private Consumer<?> consumer;

	/**
	 * Publish rate
	 */
	private final int publishRate;

	/**
	 * Constructor
	 *
	 * @param observer    observer
	 * @param consumer    result listener
	 * @param publishRate publish rate
	 */
	@SuppressWarnings("WeakerAccess")
	protected FileAsyncTask(final TaskObserver.Observer<Number> observer, final Consumer<?> consumer, @SuppressWarnings("SameParameterValue") final int publishRate)
	{
		this.observer = observer;
		this.consumer = consumer;
		this.publishRate = publishRate;
	}

	@SuppressWarnings("unchecked")
	static private <T> Consumer<T> safeCast(Consumer<?> consumer)
	{
		return (Consumer<T>) consumer;
	}

	// C O R E

	static private class AsyncCopyFromUri extends Task<Uri, Number, Boolean> implements ObservedDeploy.Publisher
	{
		/**
		 * Database path
		 */
		final String dest;

		/**
		 * Content resolver
		 */
		final private ContentResolver resolver;

		/**
		 * Task observer
		 */
		private final TaskObserver.Observer<Number> observer;

		/**
		 * Result listener
		 */
		private final Consumer<Boolean> consumer;

		/**
		 * Publish rate
		 */
		private final int publishRate;

		/**
		 * Constructor
		 *
		 * @param observer    observer
		 * @param consumer    result listener
		 * @param publishRate public rate
		 */
		AsyncCopyFromUri(final String dest, final ContentResolver resolver, final TaskObserver.Observer<Number> observer, final Consumer<Boolean> consumer, final int publishRate)
		{
			this.dest = dest;
			this.observer = observer;
			this.resolver = resolver;
			this.consumer = consumer;
			this.publishRate = publishRate;
		}

		@NonNull
		@SuppressWarnings("boxing")
		@Override
		protected Boolean doInBackground(final Uri... params)
		{
			Uri srcUriArg = params[0];

			// outsource it to deploy
			return ObservedDeploy.copyFromUri(srcUriArg, resolver, this.dest, this, this, this.publishRate);
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			this.observer.taskStart(this);
		}

		@Override
		protected void onProgressUpdate(final Number... params)
		{
			super.onProgressUpdate(params);
			this.observer.taskProgress(params[0], params[1], null);
		}

		@Override
		protected void onPostExecute(final Boolean result)
		{
			this.observer.taskFinish(result);
			if (this.consumer != null)
			{
				this.consumer.accept(result);
			}
		}

		@Override
		protected void onCancelled()
		{
			this.observer.taskFinish(false);
		}

		@Override
		public void publish(long current, long total)
		{
			publishProgress(current, total);
		}
	}

	static private class AsyncCopyFromFile extends Task<String, Number, Boolean> implements ObservedDeploy.Publisher
	{
		/**
		 * Database path
		 */
		final String dest;

		/**
		 * Task observer
		 */
		private final TaskObserver.Observer<Number> observer;

		/**
		 * Result listener
		 */
		private final Consumer<Boolean> consumer;

		/**
		 * Publish rate
		 */
		private final int publishRate;

		/**
		 * Constructor
		 *
		 * @param observer    observer
		 * @param consumer    result listener
		 * @param publishRate public rate
		 */
		AsyncCopyFromFile(final String dest, final TaskObserver.Observer<Number> observer, final Consumer<Boolean> consumer, final int publishRate)
		{
			this.dest = dest;
			this.observer = observer;
			this.consumer = consumer;
			this.publishRate = publishRate;
		}

		@NonNull
		@SuppressWarnings("boxing")
		@Override
		protected Boolean doInBackground(final String... params)
		{
			String srcFileArg = params[0];

			// outsource it to deploy
			return ObservedDeploy.copyFromFile(srcFileArg, this.dest, this, this, this.publishRate);
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			this.observer.taskStart(this);
		}

		@Override
		protected void onProgressUpdate(final Number... params)
		{
			super.onProgressUpdate(params);
			this.observer.taskProgress(params[0], params[1], null);
		}

		@Override
		protected void onPostExecute(final Boolean result)
		{
			this.observer.taskFinish(result);
			if (this.consumer != null)
			{
				this.consumer.accept(result);
			}
		}

		@Override
		protected void onCancelled()
		{
			this.observer.taskFinish(false);
		}

		@Override
		public void publish(long current, long total)
		{
			publishProgress(current, total);
		}
	}

	static private class AsyncUnzipFromArchiveFile extends Task<String, Number, Boolean> implements ObservedDeploy.Publisher
	{
		/**
		 * Destination
		 */
		final String dest;

		/**
		 * Task observer
		 */
		final private TaskObserver.Observer<Number> observer;

		/**
		 * Result listener
		 */
		final private Consumer<Boolean> consumer;

		/**
		 * Publish rate
		 */
		private final int publishRate;

		/**
		 * Constructor
		 *
		 * @param observer    observer
		 * @param consumer    result listener
		 * @param publishRate public rate
		 */
		AsyncUnzipFromArchiveFile(final String dest, final TaskObserver.Observer<Number> observer, final Consumer<Boolean> consumer, final int publishRate)
		{
			this.dest = dest;
			this.observer = observer;
			this.consumer = consumer;
			this.publishRate = publishRate;
		}

		@NonNull
		@Override
		@SuppressWarnings("boxing")
		protected Boolean doInBackground(final String... params)
		{
			String srcArchiveArg = params[0];

			// outsource it to deploy
			return ObservedDeploy.unzipFromArchiveFile(srcArchiveArg, this.dest, this, this, this.publishRate);
		}

		@Override
		protected void onPreExecute()
		{
			this.observer.taskStart(this);
		}

		@Override
		protected void onProgressUpdate(final Number... params)
		{
			this.observer.taskProgress(params[0], params[1], null);
		}

		@Override
		protected void onPostExecute(final Boolean result)
		{
			this.observer.taskFinish(result);
			if (this.consumer != null)
			{
				this.consumer.accept(result);
			}
		}

		@Override
		protected void onCancelled(final Boolean result)
		{
			this.observer.taskFinish(false);
		}

		@Override
		public void publish(long current, long total)
		{
			publishProgress(current, total);
		}
	}

	static private class AsyncUnzipFromArchiveUri extends Task<Uri, Number, Boolean> implements ObservedDeploy.Publisher
	{
		/**
		 * Destination
		 */
		final String dest;

		/**
		 * Content resolver
		 */
		final private ContentResolver resolver;

		/**
		 * Task observer
		 */
		final private TaskObserver.Observer<Number> observer;

		/**
		 * Result listener
		 */
		final private Consumer<Boolean> consumer;

		/**
		 * Publish rate
		 */
		private final int publishRate;

		/**
		 * Constructor
		 *
		 * @param observer    observer
		 * @param consumer    result listener
		 * @param publishRate public rate
		 */
		AsyncUnzipFromArchiveUri(final String dest, final ContentResolver resolver, final TaskObserver.Observer<Number> observer, final Consumer<Boolean> consumer, final int publishRate)
		{
			this.dest = dest;
			this.resolver = resolver;
			this.observer = observer;
			this.consumer = consumer;
			this.publishRate = publishRate;
		}

		@NonNull
		@Override
		@SuppressWarnings("boxing")
		protected Boolean doInBackground(final Uri... params)
		{
			Uri srcArchiveArg = params[0];

			// outsource it to deploy
			return ObservedDeploy.unzipFromArchiveUri(srcArchiveArg, resolver, this.dest, this, this, this.publishRate);
		}

		@Override
		protected void onPreExecute()
		{
			this.observer.taskStart(this);
		}

		@Override
		protected void onProgressUpdate(final Number... params)
		{
			this.observer.taskProgress(params[0], params[1], null);
		}

		@Override
		protected void onPostExecute(final Boolean result)
		{
			this.observer.taskFinish(result);
			if (this.consumer != null)
			{
				this.consumer.accept(result);
			}
		}

		@Override
		protected void onCancelled(final Boolean result)
		{
			this.observer.taskFinish(false);
		}

		@Override
		public void publish(long current, long total)
		{
			publishProgress(current, total);
		}
	}

	static private class AsyncUnzipEntryFromArchiveFile extends Task<String, Number, Boolean> implements ObservedDeploy.Publisher
	{
		/**
		 * Zip entry name
		 */
		final String entry;

		/**
		 * Destination
		 */
		final String dest;

		/**
		 * Task observer
		 */
		final private TaskObserver.Observer<Number> observer;

		/**
		 * Result listener
		 */
		final private Consumer<Boolean> consumer;

		/**
		 * Publish rate
		 */
		private final int publishRate;

		/**
		 * Constructor
		 *
		 * @param entry       entry
		 * @param dest        dest
		 * @param observer    observer
		 * @param consumer    result listener
		 * @param publishRate public rate
		 */
		AsyncUnzipEntryFromArchiveFile(final String entry, final String dest, final TaskObserver.Observer<Number> observer, final Consumer<Boolean> consumer, final int publishRate)
		{
			this.entry = entry;
			this.dest = dest;
			this.observer = observer;
			this.consumer = consumer;
			this.publishRate = publishRate;
		}

		@NonNull
		@Override
		@SuppressWarnings("boxing")
		protected Boolean doInBackground(final String... params)
		{
			String srcArchiveArg = params[0];

			// outsource it to deploy
			return ObservedDeploy.unzipEntryFromArchiveFile(srcArchiveArg, this.entry, this.dest, this, this, this.publishRate);
		}

		@Override
		protected void onPreExecute()
		{
			this.observer.taskStart(this);
		}

		@Override
		protected void onProgressUpdate(final Number... params)
		{
			this.observer.taskProgress(params[0], params[1], null);
		}

		@Override
		protected void onPostExecute(final Boolean result)
		{
			this.observer.taskFinish(result);
			if (this.consumer != null)
			{
				this.consumer.accept(result);
			}
		}

		@Override
		protected void onCancelled(final Boolean result)
		{
			this.observer.taskFinish(false);
		}

		@Override
		public void publish(long current, long total)
		{
			publishProgress(current, total);
		}
	}

	static private class AsyncUnzipEntryFromArchiveUri extends Task<Uri, Number, Boolean> implements ObservedDeploy.Publisher
	{
		/**
		 * Zip entry name
		 */
		final String entry;

		/**
		 * Destination
		 */
		final String dest;

		/**
		 * Content resolver
		 */
		final ContentResolver resolver;

		/**
		 * Task observer
		 */
		final private TaskObserver.Observer<Number> observer;

		/**
		 * Result listener
		 */
		final private Consumer<Boolean> consumer;

		/**
		 * Publish rate
		 */
		private final int publishRate;

		/**
		 * Constructor
		 *
		 * @param entry       entry
		 * @param dest        dest
		 * @param resolver    resolver
		 * @param observer    observer
		 * @param consumer    result listener
		 * @param publishRate public rate
		 */
		AsyncUnzipEntryFromArchiveUri(final String entry, final String dest, final ContentResolver resolver, final TaskObserver.Observer<Number> observer, final Consumer<Boolean> consumer, final int publishRate)
		{
			this.entry = entry;
			this.dest = dest;
			this.resolver = resolver;
			this.observer = observer;
			this.consumer = consumer;
			this.publishRate = publishRate;
		}

		@NonNull
		@Override
		@SuppressWarnings("boxing")
		protected Boolean doInBackground(final Uri... params)
		{
			Uri srcArchiveArg = params[0];

			// outsource it to deploy
			return ObservedDeploy.unzipEntryFromArchiveUri(srcArchiveArg, this.entry, this.resolver, this.dest, this, this, this.publishRate);
		}

		@Override
		protected void onPreExecute()
		{
			this.observer.taskStart(this);
		}

		@Override
		protected void onProgressUpdate(final Number... params)
		{
			this.observer.taskProgress(params[0], params[1], null);
		}

		@Override
		protected void onPostExecute(final Boolean result)
		{
			this.observer.taskFinish(result);
			if (this.consumer != null)
			{
				this.consumer.accept(result);
			}
		}

		@Override
		protected void onCancelled(final Boolean result)
		{
			this.observer.taskFinish(false);
		}

		@Override
		public void publish(long current, long total)
		{
			publishProgress(current, total);
		}
	}

	static private class AsyncMd5FromFile extends Task<String, Number, String> implements ObservedDeploy.Publisher
	{
		/**
		 * Task observer
		 */
		final private TaskObserver.Observer<Number> observer;

		/**
		 * Result listener
		 */
		final private Consumer<String> consumer;

		/**
		 * Publish rate
		 */
		private final int publishRate;

		/**
		 * Constructor
		 *
		 * @param observer    observer
		 * @param consumer    result listener
		 * @param publishRate public rate
		 */
		AsyncMd5FromFile(final TaskObserver.Observer<Number> observer, final Consumer<String> consumer, final int publishRate)
		{
			this.observer = observer;
			this.consumer = consumer;
			this.publishRate = publishRate;
		}

		@Nullable
		@Override
		protected String doInBackground(final String... params)
		{
			String srcFileArg = params[0];

			// outsource it to deploy
			return ObservedDeploy.md5FromFile(srcFileArg, this, this, this.publishRate);
		}

		@Override
		protected void onPreExecute()
		{
			this.observer.taskStart(this);
		}

		@Override
		protected void onProgressUpdate(final Number... params)
		{
			this.observer.taskProgress(params[0], params[1], null);
		}

		@Override
		protected void onCancelled(final String result)
		{
			this.observer.taskFinish(false);
			if (this.consumer != null)
			{
				this.consumer.accept(null);
			}
		}

		@Override
		protected void onPostExecute(@Nullable final String result)
		{
			this.observer.taskFinish(result != null);
			if (this.consumer != null)
			{
				this.consumer.accept(result);
			}
		}

		@Override
		public void publish(long current, long total)
		{
			publishProgress(current, total);
		}
	}

	static private class AsyncMd5FromUri extends Task<Uri, Number, String> implements ObservedDeploy.Publisher
	{
		/**
		 * Content resolver
		 */
		final private ContentResolver resolver;

		/**
		 * Task observer
		 */
		final private TaskObserver.Observer<Number> observer;

		/**
		 * Result listener
		 */
		final private Consumer<String> consumer;

		/**
		 * Publish rate
		 */
		private final int publishRate;

		/**
		 * Constructor
		 *
		 * @param observer    observer
		 * @param consumer    result listener
		 * @param publishRate public rate
		 */
		AsyncMd5FromUri(final ContentResolver resolver, final TaskObserver.Observer<Number> observer, final Consumer<String> consumer, final int publishRate)
		{
			this.resolver = resolver;
			this.observer = observer;
			this.consumer = consumer;
			this.publishRate = publishRate;
		}

		@Nullable
		@Override
		protected String doInBackground(final Uri... params)
		{
			Uri uriArg = params[0];

			// outsource it to deploy
			return ObservedDeploy.md5FromUri(uriArg, resolver, this, this, this.publishRate);
		}

		@Override
		protected void onPreExecute()
		{
			this.observer.taskStart(this);
		}

		@Override
		protected void onProgressUpdate(final Number... params)
		{
			this.observer.taskProgress(params[0], params[1], null);
		}

		@Override
		protected void onCancelled(final String result)
		{
			this.observer.taskFinish(false);
			if (this.consumer != null)
			{
				this.consumer.accept(null);
			}
		}

		@Override
		protected void onPostExecute(@Nullable final String result)
		{
			this.observer.taskFinish(result != null);
			if (this.consumer != null)
			{
				this.consumer.accept(result);
			}
		}

		@Override
		public void publish(long current, long total)
		{
			publishProgress(current, total);
		}
	}

	// H E L P E R S

	// copy

	/**
	 * Copy from uri
	 */
	@NonNull
	private Task<Uri, Number, Boolean> copyFromUri(final ContentResolver resolver, final String dest)
	{
		return new AsyncCopyFromUri(dest, resolver, this.observer, safeCast(this.consumer), this.publishRate);
	}

	/**
	 * Copy from source file
	 */
	@NonNull
	@SuppressWarnings("WeakerAccess")
	public Task<String, Number, Boolean> copyFromFile(final String dest)
	{
		return new AsyncCopyFromFile(dest, this.observer, safeCast(this.consumer), this.publishRate);
	}

	// unzip

	/**
	 * Expand entry from zipfile
	 */
	@NonNull
	@SuppressWarnings("WeakerAccess")
	public Task<String, Number, Boolean> unzipEntryFromArchiveFile(final String entry, final String dest)
	{
		return new AsyncUnzipEntryFromArchiveFile(entry, dest, this.observer, safeCast(this.consumer), this.publishRate);
	}

	/**
	 * Expand all from zip file
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	public Task<String, Number, Boolean> unzipFromArchiveFile(final String dest)
	{
		return new AsyncUnzipFromArchiveFile(dest, this.observer, safeCast(this.consumer), this.publishRate);
	}

	/**
	 * Expand all from zip uri
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	public Task<Uri, Number, Boolean> unzipFromArchiveUri(final ContentResolver resolver, final String dest)
	{
		return new AsyncUnzipFromArchiveUri(dest, resolver, this.observer, safeCast(this.consumer), this.publishRate);
	}

	/**
	 * Expand entry from zip uri
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	public Task<Uri, Number, Boolean> unzipEntryFromArchiveUri(final ContentResolver resolver, final String entry, final String dest)
	{
		return new AsyncUnzipEntryFromArchiveUri(entry, dest, resolver, this.observer, safeCast(this.consumer), this.publishRate);
	}

	// md5

	/**
	 * Md5 check sum of file
	 */
	@NonNull
	@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
	public Task<String, Number, String> md5FromFile()
	{
		return new AsyncMd5FromFile(this.observer, safeCast(this.consumer), this.publishRate);
	}

	/**
	 * Md5 check sum of input stream
	 */
	@NonNull
	@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
	public Task<Uri, Number, String> md5FromUri(@NonNull final ContentResolver resolver)
	{
		return new AsyncMd5FromUri(resolver, this.observer, safeCast(this.consumer), this.publishRate);
	}

	// L A U N C H E R S

	// unzip

	/**
	 * Launch unzipping archive file
	 *
	 * @param activity   activity
	 * @param sourceFile source zip file
	 * @param dest       database path
	 * @param whenDone   to run when done
	 */
	public static void launchUnzip(@NonNull final FragmentActivity activity, @NonNull final String sourceFile, @NonNull final String dest, @Nullable final Consumer<Boolean> whenDone)
	{
		final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager()) // guarded, level 2
				.setTitle(activity.getString(R.string.action_unzip_from_archive)) //
				.setMessage(sourceFile);
		launchUnzip(activity, observer, sourceFile, dest, whenDone);
	}

	/**
	 * Launch unzipping archive file
	 *
	 * @param activity   activity
	 * @param observer   observer
	 * @param sourceFile source zip file
	 * @param dest       database path
	 * @param whenDone   to run when done
	 */
	public static void launchUnzip(@NonNull final Activity activity, @NonNull final TaskObserver.Observer<Number> observer, @NonNull final String sourceFile, @NonNull final String dest, @Nullable final Consumer<Boolean> whenDone)
	{
		final Consumer<Boolean> consumer = success -> {

			if (success)
			{
				Settings.recordDb(activity, new File(sourceFile));
			}
			if (whenDone != null)
			{
				whenDone.accept(success);
			}
		};
		final Task<String, Number, Boolean> task = new FileAsyncTask(observer, consumer, 1000).unzipFromArchiveFile(dest);
		task.execute(sourceFile);
		observer.taskUpdate(activity.getString(org.sqlunet.download.R.string.status_unzipping));
	}

	/**
	 * Launch unzipping archive uri
	 *
	 * @param activity  activity
	 * @param sourceUri source zip uri
	 * @param dest      database path
	 * @param whenDone  to run when done
	 */
	public static void launchUnzip(@NonNull final FragmentActivity activity, @NonNull final Uri sourceUri, @NonNull final String dest, @Nullable final Consumer<Boolean> whenDone)
	{
		final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager()) // guarded, level 2
				.setTitle(activity.getString(R.string.action_unzip_from_archive)) //
				.setMessage(sourceUri.toString());
		launchUnzip(activity, observer, sourceUri, dest, whenDone);
	}

	/**
	 * Launch unzipping archive uri
	 *
	 * @param activity  activity
	 * @param observer  observer
	 * @param sourceUri source zip uri
	 * @param dest      database path
	 * @param whenDone  to run when done
	 */
	public static void launchUnzip(@NonNull final Activity activity, @NonNull final TaskObserver.Observer<Number> observer, @NonNull final Uri sourceUri, @NonNull final String dest, @Nullable final Consumer<Boolean> whenDone)
	{
		final Consumer<Boolean> consumer = success -> {

			if (success)
			{
				Settings.recordDb(activity, sourceUri.toString());
			}
			if (whenDone != null)
			{
				whenDone.accept(success);
			}
		};
		final Task<Uri, Number, Boolean> task = new FileAsyncTask(observer, consumer, 1000).unzipFromArchiveUri(activity.getContentResolver(), dest);
		task.execute(sourceUri);
		observer.taskUpdate(activity.getString(R.string.status_unzipping));
	}

	/**
	 * Launch unzipping of entry in archive file
	 *
	 * @param activity   activity
	 * @param sourceFile source zip file
	 * @param zipEntry   zip entry
	 * @param dest       database path
	 * @param whenDone   to run when done
	 */
	public static void launchUnzip(@NonNull final FragmentActivity activity, @NonNull final String sourceFile, @NonNull final String zipEntry, @NonNull final String dest, @Nullable final Consumer<Boolean> whenDone)
	{
		final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager()) // guarded, level 2
				.setTitle(activity.getString(R.string.action_unzip_from_archive)) //
				.setMessage(sourceFile);
		launchUnzip(activity, observer, sourceFile, zipEntry, dest, whenDone);
	}

	/**
	 * Launch unzipping of entry in archive file
	 *
	 * @param activity   activity
	 * @param observer   observer
	 * @param sourceFile source zip file
	 * @param zipEntry   zip entry
	 * @param dest       database path
	 * @param whenDone   to run when done
	 */
	public static void launchUnzip(@NonNull final Activity activity, @NonNull final TaskObserver.Observer<Number> observer, @NonNull final String sourceFile, @NonNull final String zipEntry, @NonNull final String dest, @Nullable final Consumer<Boolean> whenDone)
	{
		final Consumer<Boolean> consumer = success -> {

			if (success)
			{
				Settings.recordDb(activity, new File(sourceFile));
			}
			if (whenDone != null)
			{
				whenDone.accept(success);
			}
		};
		final Task<String, Number, Boolean> task = new FileAsyncTask(observer, consumer, 1000).unzipEntryFromArchiveFile(zipEntry, dest);
		task.execute(sourceFile);
		observer.taskUpdate(activity.getString(R.string.status_unzipping) + ' ' + zipEntry);
	}

	/**
	 * Launch unzipping of entry in archive uri
	 *
	 * @param activity  activity
	 * @param sourceUri source zip uri
	 * @param zipEntry  zip entry
	 * @param dest      database path
	 * @param whenDone  to run when done
	 */
	public static void launchUnzip(@NonNull final FragmentActivity activity, @NonNull final Uri sourceUri, @NonNull final String zipEntry, @NonNull final String dest, @Nullable final Consumer<Boolean> whenDone)
	{
		final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager()) // guarded, level 2
				.setTitle(activity.getString(R.string.action_unzip_from_archive)) //
				.setMessage(sourceUri.toString());
		launchUnzip(activity, observer, sourceUri, zipEntry, dest, whenDone);
	}

	/**
	 * Launch unzipping of entry in archive uri
	 *
	 * @param activity  activity
	 * @param observer  observer
	 * @param sourceUri source zip uri
	 * @param zipEntry  zip entry
	 * @param dest      database path
	 * @param whenDone  to run when done
	 */
	public static void launchUnzip(@NonNull final Activity activity, @NonNull final TaskObserver.Observer<Number> observer, @NonNull final Uri sourceUri, @NonNull final String zipEntry, @NonNull final String dest, @Nullable final Consumer<Boolean> whenDone)
	{
		final Consumer<Boolean> consumer = success -> {

			if (success)
			{
				Settings.recordDb(activity, sourceUri.toString());
			}
			if (whenDone != null)
			{
				whenDone.accept(success);
			}
		};
		final Task<Uri, Number, Boolean> task = new FileAsyncTask(observer, consumer, 1000).unzipEntryFromArchiveUri(activity.getContentResolver(), zipEntry, dest);
		task.execute(sourceUri);
		observer.taskUpdate(activity.getString(R.string.status_unzipping) + ' ' + zipEntry);
	}

	// copy

	/**
	 * Launch copy
	 *
	 * @param activity   activity
	 * @param sourceFile source file
	 * @param dest       database path
	 * @param whenDone   to run when done
	 */
	public static void launchCopy(@NonNull final FragmentActivity activity, @NonNull final String sourceFile, @NonNull final String dest, @Nullable final Consumer<Boolean> whenDone)
	{
		final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager()) // guarded, level 2
				.setTitle(activity.getString(R.string.action_copy_from_file)) //
				.setMessage(sourceFile);
		launchCopy(activity, observer, sourceFile, dest, whenDone);
	}

	/**
	 * Launch copy
	 *
	 * @param activity   activity
	 * @param observer   observer
	 * @param sourceFile source file
	 * @param dest       database path
	 * @param whenDone   to run when done
	 */
	public static void launchCopy(@NonNull final Activity activity, @NonNull final TaskObserver.Observer<Number> observer, @NonNull final String sourceFile, @NonNull final String dest, @Nullable final Consumer<Boolean> whenDone)
	{
		final Consumer<Boolean> consumer = success -> {

			if (success)
			{
				Settings.recordDb(activity, new File(sourceFile));
			}
			if (whenDone != null)
			{
				whenDone.accept(success);
			}
		};
		final Task<String, Number, Boolean> task = new FileAsyncTask(observer, consumer, 1000).copyFromFile(dest);
		task.execute(sourceFile);
		observer.taskUpdate(activity.getString(R.string.status_copying) + ' ' + sourceFile);
	}

	/**
	 * Launch copy
	 *
	 * @param activity activity
	 * @param uri      source uri
	 * @param dest     database path
	 * @param whenDone to run when done
	 */
	public static void launchCopy(@NonNull final FragmentActivity activity, @NonNull final Uri uri, @NonNull final String dest, @Nullable final Consumer<Boolean> whenDone)
	{
		final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager()) // guarded, level 2
				.setTitle(activity.getString(R.string.action_copy_from_file)) //
				.setMessage(uri.toString());
		launchCopy(activity, observer, uri, dest, whenDone);
	}

	/**
	 * Launch copy
	 *
	 * @param activity activity
	 * @param observer observer
	 * @param uri      source uri
	 * @param dest     database path
	 * @param whenDone to run when done
	 */
	public static void launchCopy(@NonNull final Activity activity, @NonNull final TaskObserver.Observer<Number> observer, @NonNull final Uri uri, @NonNull final String dest, @Nullable final Consumer<Boolean> whenDone)
	{
		final Consumer<Boolean> consumer = success -> {

			if (success)
			{
				Settings.recordDb(activity, uri.toString());
			}
			if (whenDone != null)
			{
				whenDone.accept(success);
			}
		};
		final Task<Uri, Number, Boolean> task = new FileAsyncTask(observer, consumer, 1000).copyFromUri(activity.getContentResolver(), dest);
		task.execute(uri);
		observer.taskUpdate(activity.getString(R.string.status_copying) + ' ' + uri);
	}

	// md5

	/**
	 * Launch computation of MD5
	 *
	 * @param activity   activity
	 * @param sourceFile source file
	 * @param whenDone   to run when done
	 */
	public static void launchMd5(@NonNull final FragmentActivity activity, @NonNull final String sourceFile, @Nullable final Runnable whenDone)
	{
		final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager()) // guarded, level 2
				.setTitle(activity.getString(R.string.action_md5)) //
				.setMessage(sourceFile);
		launchMd5(activity, observer, sourceFile, whenDone);
	}

	/**
	 * Launch computation of MD5
	 *
	 * @param activity activity
	 * @param uri      uri
	 * @param whenDone to run when done
	 */
	public static void launchMd5(@NonNull final FragmentActivity activity, @NonNull final Uri uri, @Nullable final Runnable whenDone)
	{
		final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager()) // guarded, level 2
				.setTitle(activity.getString(R.string.action_md5)) //
				.setMessage("input stream");
		launchMd5(activity, observer, uri, whenDone);
	}

	/**
	 * Launch computation of MD5
	 *
	 * @param activity   activity
	 * @param observer   observer
	 * @param sourceFile source file
	 * @param whenDone   to run when done
	 */
	public static void launchMd5(@NonNull final Activity activity, @NonNull final TaskObserver.Observer<Number> observer, @NonNull final String sourceFile, @Nullable final Runnable whenDone)
	{
		final Consumer<String> consumer = md5 -> {

			final AlertDialog.Builder alert2 = new AlertDialog.Builder(activity); // unguarded, level 1
			if (md5 != null)
			{
				alert2.setMessage(md5);
			}
			else
			{
				alert2.setMessage(R.string.result_fail);
			}
			alert2.setOnDismissListener((d) -> {
				if (whenDone != null)
				{
					whenDone.run();
				}
			});
			alert2.show();
		};
		final Task<String, Number, String> task = new FileAsyncTask(observer, consumer, 1000).md5FromFile();
		task.execute(sourceFile);
		observer.taskUpdate(activity.getString(R.string.status_md5_checking) + ' ' + sourceFile);
	}

	/**
	 * Launch computation of MD5
	 *
	 * @param activity activity
	 * @param observer observer
	 * @param uri      uri
	 * @param whenDone to run when done
	 */
	public static void launchMd5(@NonNull final Activity activity, @NonNull final TaskObserver.Observer<Number> observer, @NonNull final Uri uri, @Nullable final Runnable whenDone)
	{
		final Consumer<String> consumer = md5 -> {

			final AlertDialog.Builder alert2 = new AlertDialog.Builder(activity); // unguarded, level 1
			if (md5 != null)
			{
				alert2.setMessage(md5);
			}
			else
			{
				alert2.setMessage(R.string.result_fail);
			}
			alert2.setOnDismissListener((d) -> {
				if (whenDone != null)
				{
					whenDone.run();
				}
			});
			alert2.show();
		};
		final Task<Uri, Number, String> task = new FileAsyncTask(observer, consumer, 1000).md5FromUri(activity.getContentResolver());
		task.execute(uri);
		observer.taskUpdate(activity.getString(R.string.status_md5_checking) + ' ' + "input stream");
	}
}
