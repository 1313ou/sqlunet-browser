/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

/**
 * Proto download fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class AbstractDownloadFragment extends Fragment implements View.OnClickListener
{
	static private final String TAG = "AbstractDownloadF";

	// A R G U M E N T   K E Y S

	/**
	 * Download mode argument
	 */
	static public final String DOWNLOAD_MODE_ARG = "download_mode";

	/**
	 * From argument
	 */
	static public final String DOWNLOAD_FROM_ARG = "download_from";

	/**
	 * Rename from argument
	 */
	static public final String RENAME_FROM_ARG = "rename_from";

	/**
	 * Rename to argument
	 */
	static public final String RENAME_TO_ARG = "rename_to";

	/**
	 * Unzip to argument
	 */
	static public final String THEN_UNZIP_TO_ARG = "unzip_to";

	/**
	 * Rename from argument
	 */
	static public final String DOWNLOAD_RENAME_FROM_ARG = "rename_from";

	/**
	 * Rename to argument
	 */
	static public final String DOWNLOAD_RENAME_TO_ARG = "rename_to";

	/**
	 * Target file
	 */
	static public final String DOWNLOAD_TARGET_FILE_ARG = "target_file";

	// S T A T E   K E Y S

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

	// B R O A C A S T

	/**
	 * Broadcast action
	 */
	static public final String BROADCAST_ACTION = "ACTION";

	/**
	 * Broadcast request key
	 */
	static public final String BROADCAST_REQUEST_KEY = "REQUEST";

	/**
	 * Broadcast kill (old datapack) request value
	 */
	static public final String BROADCAST_KILL_REQUEST_VALUE = "KILL";

	/**
	 * Broadcast new (datapack) request value
	 */
	static public final String BROADCAST_NEW_REQUEST_VALUE = "NEW";

	// S T A T U S

	/**
	 * Status, describes download status
	 */
	enum Status
	{
		STATUS_PENDING(R.string.status_download_pending), //
		STATUS_RUNNING(R.string.status_download_running), //
		STATUS_PAUSED(R.string.status_download_paused), //
		STATUS_SUCCEEDED(R.string.status_download_successful), //
		STATUS_FAILED(R.string.status_download_fail), //
		STATUS_CANCELLED(R.string.status_download_cancelled);

		final int res;

		Status(int res)
		{
			this.res = res;
		}

		/**
		 * Make status string
		 *
		 * @param context context
		 * @return status string
		 */
		@NonNull
		public String toString(@NonNull final Context context)
		{
			return context.getString(this.res);
		}
	}

	/**
	 * Pair of downloaded / total
	 */
	static class Progress
	{
		long total;
		long downloaded;
	}

	/**
	 * Whether one download is in progress
	 */
	static private boolean isDownloading = false;

	/**
	 * Progress
	 */
	private Progress progress;

	// D A T A

	/**
	 * Download uri
	 */
	@SuppressWarnings("WeakerAccess")
	@Nullable
	protected String downloadUrl;

	/**
	 * Rename source
	 */
	@Nullable
	protected String renameFrom;

	/**
	 * Rename destination
	 */
	@Nullable
	protected String renameTo;

	// W I D G E T S

	/**
	 * Progress bar
	 */
	protected ProgressBar progressBar;

	/**
	 * Progress status view
	 */
	protected TextView progressStatus;

	/**
	 * Status view
	 */
	protected TextView statusTextView;

	/**
	 * Download button
	 */
	protected ImageButton downloadButton;

	/**
	 * Saved resource id of image applied to download button
	 */
	protected int downloadButtonImageResId;

	/**
	 * Saved resource id of background applied to download button
	 */
	protected int downloadButtonBackgroundResId;

	/**
	 * Cancel downloads button
	 */
	protected Button cancelButton;

	/**
	 * MD5 button
	 */
	protected Button deployButton;

	/**
	 * MD5 button
	 */
	protected Button md5Button;

	// L I F E C Y C L E

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// arguments
		final Bundle arguments = getArguments();
		final String fromArg = arguments == null ? null : arguments.getString(DOWNLOAD_FROM_ARG);
		final String renameFromArg = arguments == null ? null : arguments.getString(RENAME_FROM_ARG);
		final String renameToArg = arguments == null ? null : arguments.getString(RENAME_TO_ARG);

		// download source data
		this.downloadUrl = fromArg;
		if (this.downloadUrl == null || this.downloadUrl.isEmpty())
		{
			final String message = requireContext().getString(R.string.status_download_error_null_download_url);
			warn(message);
		}

		// rename
		this.renameFrom = renameFromArg;
		this.renameTo = renameToArg;

		// inits
		this.progress = new Progress();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(getResId(), container, false);

		// components
		this.progressBar = view.findViewById(R.id.progressBar);
		this.progressStatus = view.findViewById(R.id.progressStatus);
		this.statusTextView = view.findViewById(R.id.status);

		// default button resources
		this.downloadButtonImageResId = R.drawable.bn_download;
		this.downloadButtonBackgroundResId = R.drawable.bg_button_action;

		// buttons
		this.downloadButton = view.findViewById(R.id.downloadButton);
		assert this.downloadButton != null;
		this.downloadButton.setOnClickListener(this);

		this.cancelButton = view.findViewById(R.id.cancelButton);
		assert this.cancelButton != null;
		this.cancelButton.setOnClickListener(v -> {
			this.cancelButton.setVisibility(View.INVISIBLE);

			cancel();

			stopProbeObserver();

			isDownloading = false;
		});

		this.deployButton = view.findViewById(R.id.deployButton);
		assert this.deployButton != null;
		this.deployButton.setOnClickListener(v -> deploy());

		this.md5Button = view.findViewById(R.id.md5Button);
		assert this.md5Button != null;
		this.md5Button.setOnClickListener(v -> md5());

		final Button showButton = view.findViewById(R.id.showButton);
		assert showButton != null;
		showButton.setOnClickListener(v -> show());

		// source
		final TextView srcView = view.findViewById(R.id.src);
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
		setDestination(view);

		// status
		this.statusTextView.setText("");

		if (savedInstanceState != null)
		{
			this.downloadButton.setVisibility(savedInstanceState.getInt(DOWNLOAD_BTN_STATE, View.VISIBLE));
			this.downloadButtonImageResId = savedInstanceState.getInt(DOWNLOAD_BTN_IMAGE_STATE, R.drawable.bn_download);
			Drawable drawable = AppCompatResources.getDrawable(requireContext(), this.downloadButtonImageResId);
			assert drawable != null;
			DrawableCompat.setTint(drawable, Color.WHITE);
			this.downloadButton.setImageDrawable(drawable);
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
			this.initUI();
		}

		return view;
	}

	abstract protected void setDestination(@NonNull final View view);

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

	@SuppressWarnings("WeakerAccess")
	@Override
	public void onResume()
	{
		super.onResume();

		if (isDownloading)
		{
			Log.d(TAG, "Start probe observer");
			startProbeObserver();
		}
	}

	@SuppressWarnings("WeakerAccess")
	@Override
	public void onPause()
	{
		super.onPause();
		Log.d(TAG, "Stop probe observer");
		stopProbeObserver();
	}

	// A B S T R A C T  L A Y O U T

	abstract protected int getResId();

	// C L I C K

	static private final Object clickLock = new Object();

	/**
	 * Download button click
	 */
	@SuppressWarnings("WeakerAccess")
	@Override
	public void onClick(@NonNull final View view)
	{
		final int id = view.getId();
		if (id == R.id.downloadButton)
		{
			boolean wasNotDownloading;
			synchronized (clickLock)
			{
				wasNotDownloading = !isDownloading;
				if (wasNotDownloading)
				{
					isDownloading = true;
				}
			}
			if (wasNotDownloading)
			{
				startUI();

				try
				{
					// start downloading
					start();

					// start progress
					startProbeObserver();
				}
				catch (@NonNull NullPointerException e)
				{
					Log.e(TAG, "While starting", e);
					warn(getString(R.string.status_download_null_exception));
					onDone(Status.STATUS_FAILED);
				}
				catch (@NonNull Exception e)
				{
					Log.e(TAG, "While starting", e);
					String message = e.getMessage();
					warn(message == null ? e.getClass().getName() : message);
					onDone(Status.STATUS_FAILED);
				}
			}
		}
	}

	// O P E R A T I O N S

	/**
	 * Start download
	 */
	abstract protected void start();

	/**
	 * Deploy tail operation
	 * @noinspection EmptyMethod
	 */
	abstract protected void deploy();

	/**
	 * Md5 operation
	 */
	abstract protected void md5();

	/**
	 * Cleanup after download
	 *
	 * @noinspection EmptyMethod
	 */
	abstract protected void cleanup();

	/**
	 * Show
	 *
	 * @noinspection EmptyMethod
	 */
	abstract protected void show(); //TODO

	/**
	 * Cancel download
	 */
	abstract protected void cancel();

	// S T A T U S

	/**
	 * Get status
	 *
	 * @param progress progress result
	 * @return status code
	 */
	abstract Status getStatus(final Progress progress);

	/**
	 * Get reason
	 *
	 * @return reason
	 */
	@Nullable
	abstract String getReason();

	/**
	 * Build status message
	 *
	 * @param status status
	 * @return message
	 */
	@NonNull
	protected String buildStatusString(@NonNull final Status status)
	{
		if (status != Status.STATUS_SUCCEEDED)
		{
			final String reason = getReason();
			if (reason != null)
			{
				return status.toString(requireContext()) + '\n' + reason;
			}
		}
		return status.toString(requireContext());
	}

	// P R O B E   O B S E R V E R

	static private final long TIMELAPSE = 3000L;

	/**
	 * ProbeObserver
	 */
	class ProbeObserver implements Runnable
	{
		boolean stop = false;

		@Override
		public void run()
		{
			Log.d(TAG, "Probe observer is alive");
			while (true)
			{
				// terminate observer if fragment is not in resumed state
				if (!isResumed())
				{
					break;
				}

				// status
				Status status = getStatus(AbstractDownloadFragment.this.progress);
				Log.d(TAG, "Probe observer got status " + status);

				// exit because task has ended
				if (status == Status.STATUS_FAILED)
				{
					cleanup();
					break;
				}
				if (status == Status.STATUS_SUCCEEDED)
				{
					break;
				}

				// exit because task was cancelled
				if (this.stop)
				{
					break;
				}

				// observer update UI
				probeObserverUpdate(status);

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
			Log.d(TAG, "Probe observer dies");
		}
	}

	/**
	 * ProbeObserver
	 */
	private ProbeObserver probeObserver;

	/**
	 * Start probe observer thread
	 */
	private void startProbeObserver()
	{
		this.probeObserver = new ProbeObserver();
		new Thread(this.probeObserver).start();
	}

	/**
	 * Stop probe observer thread
	 */
	private void stopProbeObserver()
	{
		if (this.probeObserver != null)
		{
			this.probeObserver.stop = true;
		}
	}

	/**
	 * Probe observer update
	 *
	 * @param status status
	 */
	private void probeObserverUpdate(@NonNull final Status status)
	{
		final boolean inProgress = status == Status.STATUS_RUNNING || status == Status.STATUS_PAUSED;
		final int progress100 = this.progress.total == 0 ? -1 : (int) (this.progress.downloaded * 100L / this.progress.total);
		final String count = Utils.countToStorageString(this.progress.downloaded);
		final String message = buildStatusString(status);
		Log.d(TAG, "Probe observer update " + message + ", " + progress100 + "% done");

		final Activity activity = getActivity();
		if (activity != null && !isDetached() && !activity.isFinishing() && !activity.isDestroyed())
		{
			activity.runOnUiThread(() -> {
				if (inProgress)
				{
					this.progressBar.setVisibility(View.VISIBLE);
					this.progressStatus.setVisibility(View.VISIBLE);
					this.progressBar.setProgress(progress100);
					this.progressStatus.setText(count);
				}
				this.statusTextView.setText(message);
			});
		}
	}

	// H E L P E R S

	/**
	 * Initial UI state
	 */
	private void initUI()
	{
		if (!isDownloading)
		{
			this.downloadButton.setVisibility(View.VISIBLE);
			this.progressBar.setVisibility(View.INVISIBLE);
			this.progressStatus.setVisibility(View.INVISIBLE);
			this.cancelButton.setVisibility(View.GONE);
		}
		else
		{
			this.downloadButton.setVisibility(View.INVISIBLE);
			this.progressBar.setVisibility(View.VISIBLE);
			this.progressStatus.setVisibility(View.VISIBLE);
			this.cancelButton.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Start UI state
	 */
	private void startUI()
	{
		// buttons
		this.downloadButton.setEnabled(false);
		this.downloadButton.setVisibility(View.INVISIBLE);
		this.cancelButton.setVisibility(View.VISIBLE);

		// progress
		this.progressBar.setVisibility(View.VISIBLE);
		this.progressStatus.setVisibility(View.VISIBLE);

		// status
		this.statusTextView.setText("");
	}

	/**
	 * End UI state
	 *
	 * @param status status
	 */
	protected void endUI(@NonNull final Status status)
	{
		Log.d(TAG, "Update UI " + status);

		// progress
		if (this.progressBar != null)
		{
			this.progressBar.setVisibility(View.INVISIBLE);
		}
		if (this.progressStatus != null)
		{
			this.progressStatus.setVisibility(View.INVISIBLE);
		}

		// status
		if (this.statusTextView != null)
		{
			String message = buildStatusString(status);
			this.statusTextView.setText(message);
			this.statusTextView.setVisibility(View.VISIBLE);
		}

		// buttons
		if (this.downloadButton != null)
		{
			this.downloadButtonImageResId = status == Status.STATUS_SUCCEEDED ? R.drawable.bn_download_ok : R.drawable.bn_download;
			Drawable drawable = AppCompatResources.getDrawable(requireContext(), this.downloadButtonImageResId);
			assert drawable != null;
			DrawableCompat.setTint(drawable, Color.WHITE);
			this.downloadButton.setImageDrawable(drawable);
			this.downloadButtonBackgroundResId = status == Status.STATUS_SUCCEEDED ? R.drawable.bg_button_ok : R.drawable.bg_button_err;
			this.downloadButton.setBackgroundResource(this.downloadButtonBackgroundResId);
			this.downloadButton.setEnabled(false);
			this.downloadButton.setVisibility(View.VISIBLE);
		}
		this.cancelButton.setVisibility(View.GONE);
	}

	/**
	 * Warn
	 */
	private void warn(@NonNull final CharSequence message)
	{
		final Activity activity = getActivity();
		if (activity != null && !isDetached() && !activity.isFinishing() && !activity.isDestroyed())
		{
			activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_LONG).show());
		}
	}

	// R E C O R D

	protected void record()
	{
		Bundle arguments = getArguments();
		if (arguments != null)
		{
			String target = arguments.getString(DOWNLOAD_TARGET_FILE_ARG);
			if (target != null)
			{
				Settings.recordDatapackFile(requireContext(), new File(target));
				Log.d(TAG,"Recorded " + target);
			}
		}
	}

	// E V E N T S

	/**
	 * Event sink for download events fired by downloader
	 *
	 * @param status download status
	 */
	void onDone(final Status status)
	{
		isDownloading = false;
	}

	/**
	 * Event sink for completion events fired by downloader
	 *
	 * @param success whether download and deployment were successful
	 */
	void onComplete(final boolean success)
	{
		Log.d(TAG, "OnComplete succeeded=" + success + " " + this);

		OnComplete listener = (OnComplete) getActivity();
		assert listener != null;
		listener.onComplete(success);
	}
}
