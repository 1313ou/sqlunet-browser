/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.sqlunet.concurrency.ObservedDelegatingTask;
import org.sqlunet.concurrency.Task;
import org.sqlunet.concurrency.TaskDialogObserver;
import org.sqlunet.concurrency.TaskObserver;
import org.sqlunet.deploy.Deploy;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * Download activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class BaseDownloadFragment extends Fragment implements View.OnClickListener
{
	static private final String TAG = "BaseDownloadF";

	/**
	 * Instance state key for download button
	 */
	static private final String DOWNLOAD_BTN_STATE = "download_btn_state";

	/**
	 * Instance state key for download button image resource id
	 */
	static private final String DOWNLOAD_BTN_IMAGE_STATE = "download_image_btn_state";

	/**
	 * Instance state key for download button background resource id
	 */
	static private final String DOWNLOAD_BTN_BACKGROUND_STATE = "download_background_btn_state";

	/**
	 * Instance state key for cancel button
	 */
	static private final String CANCEL_BTN_STATE = "cancel_btn_state";

	/**
	 * Instance state key for deploy button
	 */
	static private final String DEPLOY_BTN_STATE = "deploy_btn_state";

	/**
	 * Instance state key for md5 button
	 */
	static private final String MD5_BTN_STATE = "md5_btn_state";

	/**
	 * Instance state key for progress
	 */
	static private final String PROGRESS_STATE = "progress_state";

	/**
	 * Instance state key for progress status text view
	 */
	static private final String PROGRESS_STATUS_STATE = "progress_status_state";

	/**
	 * Downloader argument
	 */
	static public final String DOWNLOAD_DOWNLOADER_ARG = "download_downloader";

	/**
	 * From argument
	 */
	static public final String DOWNLOAD_FROM_ARG = "download_from";

	/**
	 * To argument
	 */
	static public final String DOWNLOAD_TO_ARG = "download_to";

	/**
	 * Unzip to argument
	 */
	static public final String UNZIP_TO_ARG = "unzip_to";

	/**
	 * Rename from argument
	 */
	static public final String RENAME_FROM_ARG = "rename_from";

	/**
	 * Rename to argument
	 */
	static public final String RENAME_TO_ARG = "rename_to";

	/**
	 * Download listener (typically implemented by activity)
	 */
	@FunctionalInterface
	interface DownloadListener
	{
		void onDone(boolean result);
	}

	/**
	 * Status
	 */
	enum Status
	{
		STATUS_PENDING(0x01, R.string.status_download_pending), // 1
		STATUS_RUNNING(0x02, R.string.status_download_running), // 2
		STATUS_PAUSED(0x04, R.string.status_download_paused), // 4
		STATUS_SUCCESSFUL(0x08, R.string.status_download_successful), // 8
		STATUS_FAILED(0x10, R.string.status_download_fail); // 16

		final int mask;

		final int res;

		Status(int mask, int res)
		{
			this.mask = mask;
			this.res = res;
		}

		boolean test(long status)
		{
			return (status & this.mask) != 0;
		}

		static int toResId(@Nullable final Status status)
		{
			if (status == null)
			{
				return R.string.status_state_unknown;
			}
			return status.res;
		}

		@Nullable
		static Status valueOf(int code)
		{
			for (Status status : values())
			{
				if (status.mask == code)
				{
					return status;
				}
			}
			return null;
		}

		static boolean finished(long status)
		{
			return status == 0 || STATUS_SUCCESSFUL.test(status) || STATUS_FAILED.test(status);
		}

		static boolean isSuccess(long status)
		{
			return STATUS_SUCCESSFUL.test(status);
		}
	}

	static class Progress
	{
		long total;
		long downloaded;
	}

	/**
	 * Download uri
	 */
	@SuppressWarnings("WeakerAccess")
	@Nullable
	protected String downloadUrl;

	/**
	 * Download source uri (may differ, entry if zip stream)
	 */
	@SuppressWarnings("WeakerAccess")
	@Nullable
	protected String sourceUrl;

	/**
	 * Destination file
	 */
	@SuppressWarnings("WeakerAccess")
	@Nullable
	protected File downloadedFile;

	/**
	 * Unzip dir
	 */
	@Nullable
	private File unzipDir;

	/*
	 * Rename source
	 */
	@Nullable
	private String renameFrom;

	/*
	 * Rename dest
	 */
	@Nullable
	private String renameTo;

	/**
	 * Progress bar
	 */
	private ProgressBar progressBar;

	/**
	 * Progress status view
	 */
	private TextView progressStatus;

	/**
	 * Status view
	 */
	private TextView statusView;

	/**
	 * Download button
	 */
	private ImageButton downloadButton;

	/**
	 * Saved resource id of image applied to download button
	 */
	private int downloadButtonImageResId;

	/**
	 * Saved resource id of background applied to download button
	 */
	private int downloadButtonBackgroundResId;

	/**
	 * Cancel downloads button
	 */
	private Button cancelButton;

	/**
	 * MD5 button
	 */
	private Button deployButton;

	/**
	 * MD5 button
	 */
	private Button md5Button;

	/**
	 * Done listener
	 */
	private DownloadListener listener;

	/**
	 * Result status
	 */
	private Progress progress;

	/**
	 * Result status
	 */
	private int status;

	/**
	 * Whether one download is in progress
	 */
	static private boolean isDownloading = false;

	/**
	 * Cached context
	 * <p>
	 * Uses:
	 * context.getString(R.string.x)
	 * PreferenceManager.getDefaultSharedPreferences(context)
	 * new NotificationCompat.Builder(context, CHANNEL_ID)
	 * context.getSystemService(Context.NOTIFICATION_SERVICE)
	 * context.getPackageName()
	 * LocalBroadcastManager.getInstance(context)
	 * PendingIntent.getBroadcast(this.appContext,...)
	 */
	@SuppressWarnings("WeakerAccess")
	protected Context appContext;

	@SuppressWarnings("WeakerAccess")
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate " + savedInstanceState + " " + this);

		// context for threads that terminate after activity finishes
		final Activity activity = requireActivity();
		this.appContext = activity.getApplicationContext();

		// arguments
		final Bundle arguments = getArguments();
		final String fromArg = arguments == null ? null : arguments.getString(DOWNLOAD_FROM_ARG);
		final String toArg = arguments == null ? null : arguments.getString(DOWNLOAD_TO_ARG);
		final String unzipToArg = arguments == null ? null : arguments.getString(UNZIP_TO_ARG);

		// download source data
		this.downloadUrl = this.sourceUrl = fromArg;
		if (this.downloadUrl == null || this.downloadUrl.isEmpty())
		{
			final String message = this.appContext.getString(R.string.status_download_error_null_download_url);
			warn(message);

			// fire done result to listener
			fireDone(false);
		}

		// download dest data
		this.downloadedFile = toArg != null ? new File(toArg) : null;

		// unzip
		this.unzipDir = unzipToArg != null ? new File(unzipToArg) : null;

		// rename
		this.renameFrom = arguments == null ? null : arguments.getString(RENAME_FROM_ARG);
		this.renameTo = arguments == null ? null : arguments.getString(RENAME_TO_ARG);

		// inits
		this.progress = new Progress();
		this.status = 0;
	}

	abstract protected int getResId();

	@SuppressWarnings("WeakerAccess")
	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateView " + savedInstanceState + " " + this);

		// view
		final View view = inflater.inflate(getResId(), container, false);

		// components
		this.progressBar = view.findViewById(R.id.progressBar);
		this.progressStatus = view.findViewById(R.id.progressStatus);
		this.statusView = view.findViewById(R.id.status);

		// default button resources
		this.downloadButtonImageResId = R.drawable.bn_download;
		this.downloadButtonBackgroundResId = R.drawable.bg_button_action;

		// buttons
		this.downloadButton = view.findViewById(R.id.downloadButton);
		this.downloadButton.setOnClickListener(this);
		this.cancelButton = view.findViewById(R.id.cancelButton);
		this.cancelButton.setOnClickListener(v -> {
			this.cancelButton.setVisibility(View.INVISIBLE);
			cancel();
			stopObserver();
			isDownloading = false;
			this.statusView.setText(R.string.status_download_canceled);
		});
		this.deployButton = view.findViewById(R.id.deployButton);
		this.deployButton.setOnClickListener(v -> {
			assert this.unzipDir != null;
			deploy(this.unzipDir.getAbsolutePath(), this.renameFrom, this.renameTo);
		});
		this.md5Button = view.findViewById(R.id.md5Button);
		this.md5Button.setOnClickListener(v -> md5());

		// source
		final TextView srcView = view.findViewById(R.id.up_src);
		final TextView srcView2 = view.findViewById(R.id.src2);
		final TextView srcView3 = view.findViewById(R.id.src3);
		if (srcView2 != null && srcView3 != null)
		{
			try
			{
				Uri uri = Uri.parse(this.downloadUrl);
				String host = uri.getHost();
				int port = uri.getPort();
				if (port != -1)
				{
					host += ':' + Integer.toString(port);
				}
				String file = uri.getLastPathSegment();
				String path = uri.getPath();
				if (path != null && file != null)
				{
					path = path.substring(0, path.lastIndexOf(file));
				}
				srcView3.setText(host);
				srcView2.setText(path);
				srcView.setText(file);
			}
			catch (Exception e)
			{
				srcView3.setText(R.string.status_error_invalid);
				srcView2.setText(R.string.status_error_invalid);
				srcView.setText(R.string.status_error_invalid);
			}
		}
		else
		{
			srcView.setText(this.downloadUrl);
		}
		// destination
		final TextView targetView = view.findViewById(R.id.target);
		final TextView targetView2 = view.findViewById(R.id.target2);
		final TextView targetView3 = view.findViewById(R.id.target3);
		if (targetView2 != null && targetView3 != null)
		{
			targetView3.setText(this.downloadedFile != null ? this.downloadedFile.getName() : "");
			File parent = this.downloadedFile != null ? this.downloadedFile.getParentFile() : null;
			targetView2.setText(parent != null ? parent.getName() : "");
			targetView.setText(parent != null ? parent.getParent() : "");
		}
		else
		{
			targetView.setText(this.downloadedFile != null ? this.downloadedFile.getAbsolutePath() : "");
		}

		// destination
		this.statusView.setText("");

		if (savedInstanceState != null)
		{
			this.downloadButton.setVisibility(savedInstanceState.getInt(DOWNLOAD_BTN_STATE, View.VISIBLE));
			this.downloadButtonImageResId = savedInstanceState.getInt(DOWNLOAD_BTN_IMAGE_STATE, R.drawable.bn_download);
			this.downloadButton.setImageResource(this.downloadButtonImageResId);
			this.downloadButtonBackgroundResId = savedInstanceState.getInt(DOWNLOAD_BTN_BACKGROUND_STATE, R.drawable.bg_button_action);
			this.downloadButton.setBackgroundResource(this.downloadButtonBackgroundResId);
			this.progressBar.setVisibility(savedInstanceState.getInt(PROGRESS_STATE, View.INVISIBLE));
			this.progressStatus.setVisibility(savedInstanceState.getInt(PROGRESS_STATUS_STATE, View.INVISIBLE));
			this.cancelButton.setVisibility(savedInstanceState.getInt(CANCEL_BTN_STATE, View.INVISIBLE));
			this.deployButton.setVisibility(savedInstanceState.getInt(DEPLOY_BTN_STATE, View.INVISIBLE));
			this.md5Button.setVisibility(savedInstanceState.getInt(MD5_BTN_STATE, View.INVISIBLE));
		}
		else
		{
			this.initialState();
		}

		return view;
	}

	@SuppressWarnings("WeakerAccess")
	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		super.onSaveInstanceState(outState);

		outState.putInt(DOWNLOAD_BTN_STATE, this.downloadButton.getVisibility());
		outState.putInt(DOWNLOAD_BTN_IMAGE_STATE, this.downloadButtonImageResId);
		outState.putInt(DOWNLOAD_BTN_BACKGROUND_STATE, this.downloadButtonBackgroundResId);
		outState.putInt(CANCEL_BTN_STATE, this.cancelButton.getVisibility());
		outState.putInt(DEPLOY_BTN_STATE, this.deployButton.getVisibility());
		outState.putInt(MD5_BTN_STATE, this.md5Button.getVisibility());
		outState.putInt(PROGRESS_STATE, this.progressBar.getVisibility());
		outState.putInt(PROGRESS_STATUS_STATE, this.progressStatus.getVisibility());
	}

	/**
	 * Set listener
	 *
	 * @param listener listener
	 */
	public void setListener(final DownloadListener listener)
	{
		this.listener = listener;
	}

	static private final Object lock = new Object();

	@SuppressWarnings("WeakerAccess")
	@Override
	public void onClick(@NonNull final View view)
	{
		final int id = view.getId();
		if (id == R.id.downloadButton)
		{
			@SuppressWarnings("UnusedAssignment") boolean download = false;
			synchronized (lock)
			{
				download = !BaseDownloadFragment.isDownloading;
				if (download)
				{
					BaseDownloadFragment.isDownloading = true;
				}
			}
			if (download)
			{
				this.downloadButton.setEnabled(false);
				this.downloadButton.setVisibility(View.INVISIBLE);
				this.progressBar.setVisibility(View.VISIBLE);
				this.progressStatus.setVisibility(View.VISIBLE);
				this.cancelButton.setVisibility(View.VISIBLE);
				this.statusView.setText("");

				try
				{
					// start downloading
					start();

					// start progress
					startObserver();
				}
				catch (NullPointerException e)
				{
					Log.e(TAG, "While starting", e);
					warn(getString(R.string.status_download_null_exception));
					this.status = Status.STATUS_FAILED.mask;
					onDone(false);
				}
				catch (Exception e)
				{
					Log.e(TAG, "While starting", e);
					warn(e.getMessage());
					this.status = getStatus(null);
					onDone(false);
				}
			}
		}
	}

	/**
	 * Initial state
	 */
	private void initialState()
	{
		if (!BaseDownloadFragment.isDownloading)
		{
			this.downloadButton.setVisibility(View.VISIBLE);
			this.progressBar.setVisibility(View.INVISIBLE);
			this.progressStatus.setVisibility(View.INVISIBLE);
			this.cancelButton.setVisibility(View.GONE);
		}
	}

	/**
	 * Start download
	 */
	abstract protected void start();

	/**
	 * Cancel download
	 */
	abstract protected void cancel();

	/**
	 * Cleanup after download
	 */
	abstract protected void cleanup();

	// O B S E R V E R

	@SuppressWarnings("WeakerAccess")
	@Override
	public void onResume()
	{
		super.onResume();

		if (BaseDownloadFragment.isDownloading)
		{
			Log.d(TAG, "Start observer");
			startObserver();
		}
	}

	@SuppressWarnings("WeakerAccess")
	@Override
	public void onPause()
	{
		super.onPause();
		Log.d(TAG, "Stop  observer");
		stopObserver();
	}

	static private final long TIMELAPSE = 3000L;

	/**
	 * Observer
	 */
	class Observer implements Runnable
	{
		boolean cancel = false;

		@Override
		public void run()
		{
			Log.d(TAG, "Observer is alive");
			while (true)
			{
				// terminate observer if fragment is not in resumed state
				if (!isResumed())
				{
					break;
				}

				// status
				BaseDownloadFragment.this.status = getStatus(BaseDownloadFragment.this.progress);
				Log.d(TAG, "Status " + Long.toHexString(BaseDownloadFragment.this.status));

				// exit because task has ended
				if (Status.finished(BaseDownloadFragment.this.status))
				{
					if (Status.STATUS_FAILED.test(BaseDownloadFragment.this.status))
					{
						cleanup();
					}

					//noinspection BreakStatement
					break;
				}

				// exit because task was cancelled
				if (this.cancel)
				{
					break;
				}

				// observer update UI
				observerUpdate();

				// sleep
				try
				{
					//noinspection BusyWait
					Thread.sleep(TIMELAPSE);
				}
				catch (@NonNull final InterruptedException e)
				{
					//
				}
			}
			Log.d(TAG, "Observer dies");
		}
	}

	/**
	 * Observer
	 */
	private Observer observer;

	/**
	 * Start observer thread
	 */
	private void startObserver()
	{
		this.observer = new Observer();
		new Thread(this.observer).start();
	}

	/**
	 * Stop observer thread
	 */
	private void stopObserver()
	{
		if (this.observer != null)
		{
			this.observer.cancel = true;
		}
	}

	/**
	 * Observer update
	 */
	private void observerUpdate()
	{
		final boolean inProgress = Status.STATUS_RUNNING.test(this.status) || Status.STATUS_PAUSED.test(this.status);
		final int progress100 = this.progress.total == 0 ? -1 : (int) (this.progress.downloaded * 100L / this.progress.total);
		final String status = makeStatusString(this.status);
		final String reason = getReason();
		final String message = status + (reason == null ? "" : '\n' + reason);
		final String count = Utils.countToStorageString(this.progress.downloaded);
		Log.d(TAG, "Observer update " + message + ", " + progress100 + "% done");

		final Activity activity = getActivity();
		if (activity != null && !isDetached() && !activity.isFinishing() && !activity.isDestroyed())
		{
			activity.runOnUiThread(() -> {
				if (inProgress)
				{
					//this.downloadButton.setVisibility(View.INVISIBLE);
					//this.cancelButton.setVisibility(View.VISIBLE);
					this.progressBar.setVisibility(View.VISIBLE);
					this.progressStatus.setVisibility(View.VISIBLE);
					this.progressBar.setProgress(progress100);
					this.progressStatus.setText(count);
				}
				this.statusView.setText(message);
			});
		}
	}

	// S T A T U S

	/**
	 * Get status
	 *
	 * @param progress progress result
	 * @return true if finished
	 */
	abstract int getStatus(final Progress progress);

	/**
	 * Get reason
	 *
	 * @return reason
	 */
	@Nullable
	abstract String getReason();

	// H E L P E R S

	/**
	 * Make status string
	 *
	 * @param statusCode status code
	 * @return status string
	 */
	@NonNull
	private String makeStatusString(int statusCode)
	{
		final Status status = Status.valueOf(statusCode);
		final int statusResId = Status.toResId(status);
		return makeString(statusResId);
	}

	/**
	 * Make string
	 *
	 * @param resId res id
	 * @return string
	 */
	@NonNull
	String makeString(int resId)
	{
		return this.appContext.getString(resId);
	}

	/**
	 * Warn
	 */
	private void warn(final CharSequence message)
	{
		final Activity activity = getActivity();
		if (activity != null && !isDetached() && !activity.isFinishing() && !activity.isDestroyed())
		{
			activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_LONG).show());
		}
	}

	// E V E N T S

	/**
	 * Event sink for events fired by downloader
	 *
	 * @param success whether download was successful
	 */
	void onDone(final boolean success)
	{
		Log.d(TAG, "OnDone " + success + " " + this);

		BaseDownloadFragment.isDownloading = false;

		// register if this is the database
		assert this.downloadedFile != null;
		if (success)
		{
			Settings.recordDb(requireContext(), this.downloadedFile);
		}

		// observer
		stopObserver();

		// UI
		Log.d(TAG, "Update UI " + success);

		// progress
		if (this.progressBar != null)
		{
			this.progressBar.setVisibility(View.INVISIBLE);
		}
		if (this.progressStatus != null)
		{
			this.progressStatus.setVisibility(View.INVISIBLE);
		}
		if (this.statusView != null)
		{
			final String status = makeStatusString(this.status);
			final String reason = getReason();
			final String message = status + (reason == null ? "" : '\n' + reason);

			this.statusView.setText(success ? this.appContext.getString(R.string.status_download_successful) : message);
			this.statusView.setVisibility(View.VISIBLE);
		}

		// buttons
		if (this.downloadButton != null)
		{
			this.downloadButtonImageResId = success ? R.drawable.bn_download_ok : R.drawable.bn_download;
			this.downloadButton.setImageResource(downloadButtonImageResId);
			this.downloadButtonBackgroundResId = success ? R.drawable.bg_button_ok : R.drawable.bg_button_err;
			this.downloadButton.setBackgroundResource(this.downloadButtonBackgroundResId);
			this.downloadButton.setEnabled(false);
			this.downloadButton.setVisibility(View.VISIBLE);
		}
		if (this.cancelButton != null)
		{
			this.cancelButton.setVisibility(View.GONE);
		}
		if (this.md5Button != null)
		{
			this.md5Button.setVisibility(success ? View.VISIBLE : View.GONE);
		}
		if (this.deployButton != null)
		{
			this.deployButton.setVisibility(success && this.unzipDir != null ? View.VISIBLE : View.GONE);
		}

		// fire done (broadcast to listener)
		fireDone(success);

		// invalidate
		if (!success)
		{
			this.downloadedFile = null;
		}
	}

	/**
	 * Fire done event to fragment listener
	 *
	 * @param status result status
	 */
	private void fireDone(final boolean status)
	{
		if (this.listener == null)
		{
			return;
		}

		final Handler handler = new Handler(Looper.getMainLooper());
		handler.postDelayed(() -> this.listener.onDone(status), 1000);
	}

	// D E P L O Y

	/**
	 * Deploy
	 */
	private void deploy(@NonNull final String destDir, @Nullable final String renameFrom, @Nullable final String renameTo)
	{
		if (this.downloadedFile == null)
		{
			return;
		}

		Log.d(TAG, "Deploy " + this.downloadedFile + " to " + destDir);

		Deploy.emptyDirectory(new File(destDir));

		final Activity activity = getActivity();
		if (activity == null || isDetached() || activity.isFinishing() || activity.isDestroyed())
		{
			return;
		}
		final TaskObserver.BaseObserver<Number> taskListener = new TaskObserver.BaseObserver<Number>()
		{
			@Override
			public void taskFinish(final boolean result)
			{
				super.taskFinish(result);

				final Activity activity = getActivity();
				if (activity != null)
				{
					if (result && BaseDownloadFragment.this.downloadedFile != null)
					{
						if (renameFrom != null && renameTo != null && !renameFrom.equals(renameTo))
						{
							final File renameFromFile = new File(destDir, renameFrom);
							final File renameToFile = new File(destDir, renameTo);
							boolean result2 = renameFromFile.renameTo(renameToFile);
							Log.d(TAG, "Rename " + renameFromFile + " to " + renameToFile + " : " + result2);
							Settings.recordDb(activity, renameToFile);
						}

						//noinspection ResultOfMethodCallIgnored
						BaseDownloadFragment.this.downloadedFile.delete();
					}
					//broadcastRequest(this.appContext, NEW);
					activity.finish();
				}
			}
		};
		//broadcastRequest(this.appContext, KILL);

		final TaskObserver.Observer<Number> fatObserver = new TaskDialogObserver<>(getParentFragmentManager()) // guarded, level 1
				.setTitle(activity.getString(R.string.action_unzip_from_archive)) //
				.setMessage(this.downloadedFile.getName());
		final Task<String, Number, Boolean> ft = new FileAsyncTask(taskListener, null, 1000).unzipFromArchive();
		final Task<String, Number, Boolean> oft = new ObservedDelegatingTask<>(ft, fatObserver);
		oft.execute(this.downloadedFile.getAbsolutePath(), destDir);
	}

	/*
	static private void broadcastRequest(Context context, final Broadcast.RequestType request)
	{
		final Intent intent = new Intent();
		intent.setPackage(context.getPackageName());
		//final ComponentName name = new ComponentName(appContext.getPackageName(), "org.sqlunet.ProviderManager");
		//intent.setComponent(name);
		intent.setAction(BROADCAST_ACTION);
		intent.putExtra(BROADCAST_ACTION_REQUEST, request.name());
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
	*/

	// M D 5

	/**
	 * MD5 check
	 */
	private void md5()
	{
		final String from = this.sourceUrl + ".md5";
		assert this.downloadedFile != null;
		final String targetFile = this.downloadedFile.getName();
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
				assert this.downloadedFile != null;
				final String localPath = this.downloadedFile.getAbsolutePath();

				MD5AsyncTaskChooser.md5(activity, localPath, result -> {

					final Activity activity2 = getActivity();
					if (activity2 != null && !isDetached() && !activity2.isFinishing() && !activity2.isDestroyed())
					{
						final String computedResult = (String) result;
						boolean success = downloadedResult.equals(computedResult);
						final SpannableStringBuilder sb = new SpannableStringBuilder();
						Report.appendHeader(sb, getString(R.string.md5_downloaded));
						sb.append('\n');
						sb.append(downloadedResult);
						sb.append('\n');
						Report.appendHeader(sb, getString(R.string.md5_computed));
						sb.append('\n');
						sb.append(computedResult == null ? getString(R.string.status_task_failed) : computedResult);
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
		}).execute(from, targetFile);
	}
}
