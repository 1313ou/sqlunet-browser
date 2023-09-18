/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bbou.concurrency.ObservedDelegatingTask;
import com.bbou.concurrency.Task;
import com.bbou.concurrency.TaskDialogObserver;
import com.bbou.concurrency.TaskObserver;
import com.bbou.deploy.Deploy;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

/**
 * Download fragment using DownloadWork.
 * Interface between work and activity.
 * Cancel messages are to be sent to this fragment's receiver.
 * Signals completion through the OnComplete callback in the activity.
 * This fragment uses a file downloader core (file end-to-end downloads
 * with option of md5 checking it and zip expanding it to another
 * location (in settings or files dir by default) if a zip file.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadFragment extends BaseDownloadFragment
{
	/**
	 * Log tag
	 */
	static private final String TAG = "DownloadF";

	/**
	 * To argument
	 */
	static public final String DOWNLOAD_TO_FILE_ARG = "download_to_file";

	/**
	 * Destination file or dir
	 */
	@Nullable
	private File toFile;

	/**
	 * Unzip dir
	 */
	@Nullable
	private File unzipDir;

	// A R G U M E N T S

	protected void unmarshal()
	{
		// arguments
		final Bundle arguments = getArguments();
		final String toFileArg = arguments == null ? null : arguments.getString(DOWNLOAD_TO_FILE_ARG);
		final String unzipToArg = arguments == null ? null : arguments.getString(THEN_UNZIP_TO_ARG);

		// download dest data
		this.toFile = toFileArg != null ? new File(toFileArg) : null;
		this.unzipDir = unzipToArg != null ? new File(unzipToArg) : null;
	}

	// C O N T R O L

	/**
	 * Start download
	 */
	@Override
	protected void start()
	{
		synchronized (this)
		{
			Log.d(TAG, "Starting");
			if (!this.downloading) // prevent recursion
			{
				// reset
				this.success = null;
				this.cancel = false;
				this.exception = null;
				this.cause = null;
				this.progressDownloaded = 0;
				this.progressTotal = 0;

				// args
				final String from = this.downloadUrl;
				assert from != null;
				assert this.toFile != null;
				final String to = this.toFile.getAbsolutePath();

				// start job
				start(from, to);

				// status
				this.downloading = true; // set
				return;
			}
		}
		throw new RuntimeException("Already downloading");
	}

	/**
	 * Start work
	 *
	 * @param fromUrl source zip url
	 * @param toFile  destination file
	 */
	protected void start(@NonNull final String fromUrl, @NonNull final String toFile)
	{
		++notificationId;
		this.uuid = DownloadWork.startWork(requireContext(), fromUrl, toFile, this, observer);
	}

	// L A Y O U T

	/**
	 * Layout
	 */
	@Override
	protected int getResId()
	{
		return R.layout.fragment_download;
	}

	// S E T   D E S T I N A T I O N

	protected void setDestination(@NonNull final View view)
	{
		final TextView targetView = view.findViewById(R.id.target);
		final TextView targetView2 = view.findViewById(R.id.target2);
		final TextView targetView3 = view.findViewById(R.id.target3);
		final TextView targetView4 = view.findViewById(R.id.target4);
		final TextView targetView5 = view.findViewById(R.id.target5);
		if (targetView2 != null && targetView3 != null)
		{
			File parent = this.toFile != null ? this.toFile.getParentFile() : null;
			targetView.setText(parent != null ? parent.getParent() : "");
			targetView2.setText(parent != null ? parent.getName() : "");
			targetView3.setText(this.toFile != null ? this.toFile.getName() : "");
		}
		else
		{
			targetView.setText(this.toFile != null ? this.toFile.getAbsolutePath() : "");
		}
		if (targetView4 != null && targetView5 != null && this.unzipDir != null)
		{
			CharSequence deployTo = new SpannableStringBuilder(getText(R.string.deploy_dest)).append(this.unzipDir.getParent() + '/');
			targetView4.setText(deployTo);
			targetView5.setText(this.unzipDir.getName());
		}
	}

	// A B S T R A C T

	/**
	 * Cleanup download
	 */
	@SuppressWarnings("EmptyMethod")
	@Override
	protected void cleanup()
	{
	}

	// D E P L O Y

	/**
	 * Deploy
	 */
	@Override
	protected void deploy()
	{
		// guard against no downloaded file
		if (this.toFile == null)
		{
			Log.e(TAG, "Deploy failure: no downloaded file");
			return;
		}

		// guard against no unzip dir
		if (this.unzipDir == null)
		{
			String datapackDir = Settings.getDatapackDir(requireContext());
			this.unzipDir = datapackDir == null ? null : new File(datapackDir);
		}
		if (this.unzipDir == null)
		{
			Log.e(TAG, "Null datapack dir, aborting deployment");
			return;
		}

		// log
		Log.d(TAG, "Deploying " + this.toFile + " to " + this.unzipDir);

		// make sure unzip directory is clean
		Deploy.emptyDirectory(this.unzipDir);

		// kill request
		if (this.requestKill != null)
		{
			this.requestKill.run();
		}

		// observer to proceed with record, cleanup and broadcast on successful task termination
		final TaskObserver.BaseObserver<Number> unzipObserver = new TaskObserver.BaseObserver<Number>() // guarded, level 1
		{
			@Override
			public void taskFinish(final boolean success)
			{
				super.taskFinish(success);

				if (success && DownloadFragment.this.toFile != null)
				{
					// record and delete downloaded file
					// downloadedFile might become null within task
					//noinspection ConstantConditions
					if (DownloadFragment.this.toFile != null)
					{
						if (DownloadFragment.this.renameFrom != null && DownloadFragment.this.renameTo != null && !DownloadFragment.this.renameFrom.equals(DownloadFragment.this.renameTo))
						{
							// rename
							final File renameFromFile = new File(DownloadFragment.this.unzipDir, DownloadFragment.this.renameFrom);
							final File renameToFile = new File(DownloadFragment.this.unzipDir, DownloadFragment.this.renameTo);
							boolean result2 = renameFromFile.renameTo(renameToFile);
							Log.d(TAG, "Rename " + renameFromFile + " to " + renameToFile + " : " + result2);
						}

						// record
						FileData.recordDatapack(requireContext(), DownloadFragment.this.toFile);

						// cleanup
						//noinspection ResultOfMethodCallIgnored
						DownloadFragment.this.toFile.delete();
					}

					// new datapack
					if (DownloadFragment.this.requestNew != null)
					{
						DownloadFragment.this.requestNew.run();
					}
				}

				// signal
				onComplete(success);
			}
		};

		// unzip as base task
		assert this.unzipDir != null;
		final Task<String, Number, Boolean> baseTask = new FileAsyncTask(unzipObserver, null, 1000).unzipFromArchiveFile(this.unzipDir.getAbsolutePath());

		// run task
		final Activity activity = getActivity();
		if (activity == null || isDetached() || activity.isFinishing() || activity.isDestroyed())
		{
			// guard against finished/destroyed activity
			assert this.toFile != null;
			assert this.unzipDir != null;
			baseTask.execute(this.toFile.getAbsolutePath(), this.unzipDir.getAbsolutePath());
		}
		else
		{
			// augment with a dialog observer if fragment is live
			assert this.toFile != null;
			final TaskObserver.Observer<Number> fatObserver = new TaskDialogObserver<>(getParentFragmentManager()) // guarded, level 1
					.setTitle(requireContext().getString(R.string.action_unzip_from_archive)) //
					.setMessage(this.toFile.getName());
			final Task<String, Number, Boolean> task = new ObservedDelegatingTask<>(baseTask, fatObserver);
			assert this.unzipDir != null;
			task.execute(this.toFile.getAbsolutePath(), this.unzipDir.getAbsolutePath());
		}
	}

	// M D 5

	/**
	 * MD5 check
	 */
	protected void md5()
	{
		final String from = this.sourceUrl + ".md5";
		final Uri uri = Uri.parse(this.sourceUrl);
		final String sourceFile = uri.getLastPathSegment();
		final String targetFile = this.toFile == null ? "?" : this.toFile.getName();
		new MD5Downloader(downloadedResult -> {

			final FragmentActivity activity = getActivity();
			if (activity == null || isDetached() || activity.isFinishing() || activity.isDestroyed())
			{
				return;
			}

			if (downloadedResult == null)
			{
				new AlertDialog.Builder(activity) // guarded, level 2
						.setTitle(getString(R.string.action_md5) + " of " + targetFile) //
						.setMessage(R.string.status_task_failed) //
						.show();
			}
			else
			{
				assert this.toFile != null;
				final String localPath = this.toFile.getAbsolutePath();

				MD5AsyncTaskChooser.md5(activity, localPath, result -> {

					final Activity activity2 = getActivity();
					if (activity2 != null && !isDetached() && !activity2.isFinishing() && !activity2.isDestroyed())
					{
						boolean success = downloadedResult.equals(result);
						final SpannableStringBuilder sb = new SpannableStringBuilder();
						Report.appendHeader(sb, getString(R.string.md5_downloaded));
						sb.append('\n');
						sb.append(downloadedResult);
						sb.append('\n');
						Report.appendHeader(sb, getString(R.string.md5_computed));
						sb.append('\n');
						sb.append(result == null ? getString(R.string.status_task_failed) : result);
						sb.append('\n');
						Report.appendHeader(sb, getString(R.string.md5_compared));
						sb.append('\n');
						sb.append(getString(success ? R.string.status_task_success : R.string.status_task_failed));

						new AlertDialog.Builder(activity2) // guarded, level 3
								.setTitle(getString(R.string.action_md5_of_file) + ' ' + targetFile) //
								.setMessage(sb) //
								.show();
					}
				});
			}
		}).execute(from, sourceFile);
	}

	// E V E N T S

	/**
	 * Event sink for download events fired by downloader
	 *
	 * @param status download status
	 */
	@Override
	void onDone(final Status status)
	{
		Log.d(TAG, "OnDone " + status);

		super.onDone(status);

		// register if this is the datapack
		if (status == Status.STATUS_SUCCEEDED)
		{
			if (this.toFile != null)
			{
				Settings.recordDatapackFile(requireContext(), this.toFile);
			}
		}
		boolean requiresDeploy = this.unzipDir != null;

		// UI
		requireActivity().runOnUiThread(() -> {

			endUI(status);

			// md5
			this.md5Button.setVisibility(status == Status.STATUS_SUCCEEDED ? View.VISIBLE : View.GONE);

			// deploy button to complete task
			this.deployButton.setVisibility(status == Status.STATUS_SUCCEEDED && requiresDeploy ? View.VISIBLE : View.GONE);
		});

		// invalidate
		if (status != Status.STATUS_SUCCEEDED)
		{
			this.toFile = null;
		}

		// complete
		if (status != Status.STATUS_SUCCEEDED || !requiresDeploy)
		{
			onComplete(status == Status.STATUS_SUCCEEDED);
		}
	}

	// N O T I F I C A T I O N

	/**
	 * Fire UI notification
	 *
	 * @param context        context
	 * @param notificationId notification id
	 * @param type           notification
	 * @param args           arguments
	 */
	protected void fireNotification(@NonNull final Context context, int notificationId, @NonNull final Notifier.NotificationType type, final Object... args)
	{
		final String from = Uri.parse(this.downloadUrl).getHost();
		final String to = this.toFile == null ? context.getString(R.string.result_deleted) : this.toFile.getName();
		String contentText = from + '→' + to;
		Notifier.fireNotification(context, notificationId, type, contentText, args);
	}
}


