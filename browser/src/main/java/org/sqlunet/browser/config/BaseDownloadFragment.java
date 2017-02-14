package org.sqlunet.browser.config;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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

import org.sqlunet.browser.R;
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

		final public int mask;

		final public int res;

		Status(int mask, int res)
		{
			this.mask = mask;
			this.res = res;
		}

		boolean test(long status)
		{
			return (status & this.mask) != 0;
		}

		static int toResId(final Status status)
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
	@SuppressWarnings("WeakerAccess")
	protected String downloadUrl;

	/**
	 * Destination file uri
	 */
	@SuppressWarnings("WeakerAccess")
	protected File destFile;

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
	 * Cached context
	 */
	protected Context context;

	/**
	 * True if created from a saved instance
	 */
	private boolean fromSavedInstance = false;

	/**
	 * Constructor
	 */
	public BaseDownloadFragment()
	{
		super();
		Log.d(TAG, "Constructor");
	}

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate " + savedInstanceState);

		// context for threads that termitate after activity finishes
		this.context = getActivity().getApplicationContext();

		// arguments
		final Bundle arguments = getArguments();
		final String fromArg = arguments == null ? null : arguments.getString(DOWNLOAD_FROM_ARG);
		final String toArg = arguments == null ? null : arguments.getString(DOWNLOAD_TO_ARG);

		// download source data
		this.downloadUrl = fromArg != null ? fromArg : StorageSettings.getDbDownloadSource(this.context);
		if (this.downloadUrl == null || this.downloadUrl.isEmpty())
		{
			final String message = this.context.getString(R.string.status_error_null_download_url);
			warn(message);

			// fire done
			fireDone(false);
		}

		// download dest data
		this.destFile = new File(toArg != null ? toArg : StorageSettings.getDbDownloadTarget(this.context));

		/*
		final File destDir = new File(StorageSettings.getDataDir(this.context));
		final Uri downloadUri = Uri.parse(this.downloadUrl);
		final String filename = downloadUri.getLastPathSegment(); // Storage.DBFILE
		this.destFile = new File(destDir, filename);
		*/

		// inits
		this.progress = new Progress();
		this.status = 0;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateView " + savedInstanceState);

		// view
		final View view = inflater.inflate(R.layout.fragment_download, container, false);

		// components
		this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		this.progressStatus = (TextView) view.findViewById(R.id.progressStatus);
		this.statusView = (TextView) view.findViewById(R.id.status);

		// default background res
		this.downloadButtonRes = R.drawable.bg_button_action;

		// buttons
		this.downloadButton = (ImageButton) view.findViewById(R.id.downloadButton);
		this.downloadButton.setOnClickListener(this);
		this.cancelButton = (Button) view.findViewById(R.id.cancelButton);
		this.cancelButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				cancel();
			}
		});
		this.md5Button = (Button) view.findViewById(R.id.md5Button);
		this.md5Button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				md5();
			}
		});

		// source
		final TextView srcView = (TextView) view.findViewById(R.id.src);
		srcView.setText(this.downloadUrl);
		//srcView.setSingleLine(true);
		//srcView.setEllipsize(TextUtils.TruncateAt.MARQUEE);

		// destination
		final TextView targetView = (TextView) view.findViewById(R.id.target);
		targetView.setText(this.destFile != null ? this.destFile.getAbsolutePath() : "");
		//targetView.setSingleLine(true);
		//targetView.setEllipsize(TextUtils.TruncateAt.MARQUEE);

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
		return view;
	}

	@Override
	public void onSaveInstanceState(final Bundle outState)
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

	@Override
	public void onActivityCreated(final Bundle savedInstance)
	{
		super.onActivityCreated(savedInstance);
		this.fromSavedInstance = savedInstance != null;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (this.fromSavedInstance)
		{
			startObserver();
		}
	}

	@Override
	public void onClick(final View view)
	{
		final int id = view.getId();
		if (id == R.id.downloadButton)
		{
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

	/**
	 * Start download
	 */
	abstract protected void start();

	/**
	 * Cancel download
	 */
	abstract protected void cancel();

	/*
	 * Get status
	 *
	 * @param result status
	 * @return true if finished
	 */
	abstract int getStatus(final Progress progress);

	/**
	 * Get reason
	 *
	 * @return reason
	 */
	abstract String getReason();

	/**
	 * Start observer thread
	 */
	private void startObserver()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Log.d(TAG, "OBSERVER IS ALIVE");
				while (true)
				{
					// terminate if fragment is not in resumed state
					if (!isResumed())
					{
						break;
					}

					// observerUpdate status
					BaseDownloadFragment.this.status = getStatus(BaseDownloadFragment.this.progress);
					Log.d(TAG, "STATUS " + Long.toHexString(BaseDownloadFragment.this.status));

					// observerUpdate UI if fragment is added to activity
					observerUpdate();

					// exit because task has ended
					if (Status.finished(BaseDownloadFragment.this.status))
					{
						if (Status.STATUS_FAILED.test(BaseDownloadFragment.this.status))
						{
							cancel();
						}
						//noinspection BreakStatement
						break;
					}

					// sleep
					try
					{
						Thread.sleep(2000);
					}
					catch (final InterruptedException e)
					{
						//
					}
				}
				Log.d(TAG, "OBSERVER DIES");
			}
		}).start();
	}

	private String observerUpdate()
	{
		final int progress100 = this.progress.total == 0 ? -1 : (int) (this.progress.downloaded * 100L / this.progress.total);
		final String status = makeStatusString(this.status);
		final String reason = getReason();
		final String message = status + (reason == null ? "" : '\n' + reason);
		final String count = StorageUtils.countToStorageString(this.progress.downloaded);
		Log.d(TAG, "OBSERVER UPDATE " + message + ", " + progress100 + "% done");

		final Activity activity = getActivity();
		if (activity != null && !this.isDetached())
		{
			activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					BaseDownloadFragment.this.progressBar.setProgress(progress100);
					BaseDownloadFragment.this.progressStatus.setText(count);
					BaseDownloadFragment.this.statusView.setText(message);
				}
			});
		}
		return message;
	}

	/**
	 * Warn
	 */
	private void warn(final CharSequence message)
	{
		final Activity activity = getActivity();
		if (activity != null && !this.isDetached())
		{
			activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	/**
	 * Make status string
	 *
	 * @param statusCode status code
	 * @return status string
	 */
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
	protected String makeString(int resId)
	{
		return this.context.getString(resId);
	}

	/**
	 * MD5 check
	 */
	private void md5()
	{
		final String from = this.downloadUrl + ".md5";
		final String targetFile = Uri.parse(this.downloadUrl).getLastPathSegment();
		new MD5Downloader(new MD5Downloader.Listener()
		{
			@Override
			public void onDone(final String downloadedResult)
			{
				if (downloadedResult == null)
				{
					final AlertDialog.Builder alert = new AlertDialog.Builder(BaseDownloadFragment.this.getActivity());
					alert.setTitle(getString(R.string.action_md5) + " of " + targetFile);
					alert.setMessage(R.string.status_task_failed);
					alert.show();
				}
				else
				{
					final String localPath = BaseDownloadFragment.this.destFile.getAbsolutePath();
					FileAsyncTask.md5(getActivity(), localPath, new FileAsyncTask.ResultListener()
					{
						@Override
						public void onResult(final String computedResult)
						{
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
						}
					});
				}
			}
		}).execute(from, targetFile);
	}


	/**
	 * Event sink
	 *
	 * @param success whether download was successful
	 */
	@SuppressWarnings("WeakerAccess")
	protected void onDone(boolean success)
	{
		Log.d(TAG, "OnDone " + success);

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
			final String status = makeStatusString(this.status);
			final String reason = getReason();
			final String message = status + (reason == null ? "" : '\n' + reason);

			BaseDownloadFragment.this.statusView.setText(success ? this.context.getString(R.string.status_download_successful) : message);
			BaseDownloadFragment.this.statusView.setVisibility(View.VISIBLE);
		}

		// buttons
		if (BaseDownloadFragment.this.downloadButton != null)
		{
			this.downloadButtonRes = success ? R.drawable.bg_button_ok : R.drawable.bg_button_err;
			BaseDownloadFragment.this.downloadButton.setBackgroundResource(this.downloadButtonRes);
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

	private void fireDone(final boolean status)
	{
		if (this.listener == null)
		{
			return;
		}

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				BaseDownloadFragment.this.listener.onDone(status);
			}
		}, 1000);
	}
}
