package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.sqlunet.browser.common.R;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;
import org.sqlunet.style.Report;

import java.io.File;

/**
 * Download activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract class BaseDownloadFragment extends Fragment implements View.OnClickListener
{
	static private final String TAG = "BaseDownloadF";

	/**
	 * Instance state key for download button
	 */
	static private final String DOWNLOAD_BTN_STATE = "download_btn_state";

	/**
	 * Instance state key for download button background resource id
	 */
	static private final String DOWNLOAD_BTN_RES_STATE = "download_res_btn_state";

	/**
	 * Instance state key for cancel button
	 */
	static private final String CANCEL_BTN_STATE = "cancel_btn_state";

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
	 * From argument
	 */
	static public final String DOWNLOAD_FROM_ARG = "download_from";

	/**
	 * To argument
	 */
	static public final String DOWNLOAD_TO_ARG = "download_to";

	/**
	 * Download listener (typically implemented by activity)
	 */
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

	class Progress
	{
		long total;
		long downloaded;
	}

	/**
	 * Download dir uri
	 */
	@Nullable
	String downloadUrl;

	/**
	 * Destination file uri
	 */
	@Nullable
	File destFile;

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
	 * Saved resource id of background applied to download button
	 */
	private int downloadButtonRes;

	/**
	 * Cancel downloads button
	 */
	private Button cancelButton;

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
	 * Cached context for threads that terminate after activity finishes
	 */
	Context context;

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate " + savedInstanceState + " " + this);

		// context for threads that terminate after activity finishes
		final Activity activity = getActivity();
		assert activity != null;
		this.context = activity.getApplicationContext();

		// arguments
		final Bundle arguments = getArguments();
		final String fromArg = arguments == null ? null : arguments.getString(DOWNLOAD_FROM_ARG);
		final String toArg = arguments == null ? null : arguments.getString(DOWNLOAD_TO_ARG);

		// download source data
		this.downloadUrl = fromArg != null ? fromArg : StorageSettings.getDbDownloadSource(this.context);
		if (/*this.downloadUrl == null ||*/ this.downloadUrl.isEmpty())
		{
			final String message = this.context.getString(R.string.status_error_null_download_url);
			warn(message);

			// fire done
			fireDone(false);
		}

		// download dest data
		this.destFile = new File(toArg != null ? toArg : StorageSettings.getDbDownloadTarget(this.context));

		// inits
		this.progress = new Progress();
		this.status = 0;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateView " + savedInstanceState + " " + this);

		// view
		final View view = inflater.inflate(R.layout.fragment_download, container, false);

		// components
		this.progressBar = view.findViewById(R.id.progressBar);
		this.progressStatus = view.findViewById(R.id.progressStatus);
		this.statusView = view.findViewById(R.id.status);

		// default background res
		this.downloadButtonRes = R.drawable.bg_button_action;

		// buttons
		this.downloadButton = view.findViewById(R.id.downloadButton);
		this.downloadButton.setOnClickListener(this);
		this.cancelButton = view.findViewById(R.id.cancelButton);
		this.cancelButton.setOnClickListener(v -> {
			BaseDownloadFragment.this.cancelButton.setVisibility(View.INVISIBLE);
			cancel();
		});
		this.md5Button = view.findViewById(R.id.md5Button);
		this.md5Button.setOnClickListener(v -> md5());

		// source
		final TextView srcView = view.findViewById(R.id.src);
		srcView.setText(this.downloadUrl);
		srcView.setSingleLine(true);
		srcView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		srcView.setSelected(true);

		// destination
		final TextView targetView = view.findViewById(R.id.target);
		targetView.setText(this.destFile != null ? this.destFile.getAbsolutePath() : "");
		targetView.setSingleLine(true);
		targetView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		targetView.setSelected(true);

		// destination
		this.statusView.setText("");

		if (savedInstanceState != null)
		{
			//noinspection WrongConstant
			this.downloadButton.setVisibility(savedInstanceState.getInt(DOWNLOAD_BTN_STATE, View.VISIBLE));
			this.downloadButtonRes = savedInstanceState.getInt(DOWNLOAD_BTN_RES_STATE, R.drawable.bg_button_action);
			this.downloadButton.setBackgroundResource(this.downloadButtonRes);
			//noinspection WrongConstant
			this.progressBar.setVisibility(savedInstanceState.getInt(PROGRESS_STATE, View.INVISIBLE));
			//noinspection WrongConstant
			this.progressStatus.setVisibility(savedInstanceState.getInt(PROGRESS_STATUS_STATE, View.INVISIBLE));
			//noinspection WrongConstant
			this.cancelButton.setVisibility(savedInstanceState.getInt(CANCEL_BTN_STATE, View.INVISIBLE));
			//noinspection WrongConstant
			this.md5Button.setVisibility(savedInstanceState.getInt(MD5_BTN_STATE, View.INVISIBLE));
		}
		else
		{
			this.initialState();
		}

		return view;
	}

	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		super.onSaveInstanceState(outState);

		outState.putInt(DOWNLOAD_BTN_STATE, this.downloadButton.getVisibility());
		outState.putInt(DOWNLOAD_BTN_RES_STATE, this.downloadButtonRes);
		outState.putInt(CANCEL_BTN_STATE, this.cancelButton.getVisibility());
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

	@Override
	public void onResume()
	{
		super.onResume();

		Log.d(TAG, "Start observer");
		startObserver();
	}

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
		this.observer.cancel = true;
	}

	/**
	 * Observer update
	 */
	private void observerUpdate()
	{
		final boolean inProgress = Status.STATUS_RUNNING.test(BaseDownloadFragment.this.status) || Status.STATUS_PAUSED.test(BaseDownloadFragment.this.status);
		final int progress100 = this.progress.total == 0 ? -1 : (int) (this.progress.downloaded * 100L / this.progress.total);
		final String status = makeStatusString(this.status);
		final String reason = getReason();
		final String message = status + (reason == null ? "" : '\n' + reason);
		final String count = StorageUtils.countToStorageString(this.progress.downloaded);
		Log.d(TAG, "Observer update " + message + ", " + progress100 + "% done");

		final Activity activity = getActivity();
		if (activity != null && !this.isDetached())
		{
			activity.runOnUiThread(() -> {
				if (inProgress)
				{
					//BaseDownloadFragment.this.downloadButton.setVisibility(View.INVISIBLE);
					//BaseDownloadFragment.this.cancelButton.setVisibility(View.VISIBLE);
					BaseDownloadFragment.this.progressBar.setVisibility(View.VISIBLE);
					BaseDownloadFragment.this.progressStatus.setVisibility(View.VISIBLE);
					BaseDownloadFragment.this.progressBar.setProgress(progress100);
					BaseDownloadFragment.this.progressStatus.setText(count);
				}
				BaseDownloadFragment.this.statusView.setText(message);
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
		return this.context.getString(resId);
	}

	/**
	 * Warn
	 */
	private void warn(final CharSequence message)
	{
		final Activity activity = getActivity();
		if (activity != null && !this.isDetached())
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
		assert this.destFile != null;
		if (success && this.destFile.equals(new File(StorageSettings.getDatabasePath(this.context))))
		{
			FileData.recordDb(this.context);
		}

		// observer
		stopObserver();

		// UI
		Log.d(TAG, "Update UI " + success);

		// progress
		if (BaseDownloadFragment.this.progressBar != null)
		{
			BaseDownloadFragment.this.progressBar.setVisibility(View.INVISIBLE);
		}
		if (BaseDownloadFragment.this.progressStatus != null)
		{
			BaseDownloadFragment.this.progressStatus.setVisibility(View.INVISIBLE);
		}
		if (BaseDownloadFragment.this.statusView != null)
		{
			final String status = makeStatusString(BaseDownloadFragment.this.status);
			final String reason = getReason();
			final String message = status + (reason == null ? "" : '\n' + reason);

			BaseDownloadFragment.this.statusView.setText(success ? BaseDownloadFragment.this.context.getString(R.string.status_download_successful) : message);
			BaseDownloadFragment.this.statusView.setVisibility(View.VISIBLE);
		}

		// buttons
		if (BaseDownloadFragment.this.downloadButton != null)
		{
			BaseDownloadFragment.this.downloadButtonRes = success ? R.drawable.bg_button_ok : R.drawable.bg_button_err;
			BaseDownloadFragment.this.downloadButton.setBackgroundResource(BaseDownloadFragment.this.downloadButtonRes);
			BaseDownloadFragment.this.downloadButton.setEnabled(false);
			BaseDownloadFragment.this.downloadButton.setVisibility(View.VISIBLE);
		}
		if (BaseDownloadFragment.this.cancelButton != null)
		{
			BaseDownloadFragment.this.cancelButton.setVisibility(View.GONE);
		}
		if (BaseDownloadFragment.this.md5Button != null)
		{
			BaseDownloadFragment.this.md5Button.setVisibility(success ? View.VISIBLE : View.GONE);
		}

		// fire done (broadcast to listener)
		fireDone(success);
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

		final Handler handler = new Handler();
		handler.postDelayed(() -> BaseDownloadFragment.this.listener.onDone(status), 1000);
	}

	// M D 5

	/**
	 * MD5 check
	 */
	private void md5()
	{
		final String from = this.downloadUrl + ".md5";
		final String targetFile = Uri.parse(this.downloadUrl).getLastPathSegment();
		new MD5Downloader(downloadedResult -> {
			if (downloadedResult == null)
			{
				final AlertDialog.Builder alert = new AlertDialog.Builder(BaseDownloadFragment.this.getActivity());
				alert.setTitle(getString(R.string.action_md5) + " of " + targetFile);
				alert.setMessage(R.string.status_task_failed);
				alert.show();
			}
			else
			{
				final Context context = getActivity();
				assert context != null;
				assert BaseDownloadFragment.this.destFile != null;
				final String localPath = BaseDownloadFragment.this.destFile.getAbsolutePath();
				FileAsyncTask.md5(context, localPath, result -> {
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

					final AlertDialog.Builder alert = new AlertDialog.Builder(BaseDownloadFragment.this.getActivity());
					alert.setTitle(getString(R.string.action_md5_of) + ' ' + targetFile);
					alert.setMessage(sb);
					alert.show();
				});
			}
		}).execute(from, targetFile);
	}
}
