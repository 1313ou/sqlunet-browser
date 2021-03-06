/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.sqlunet.browser.EntryActivity;
import org.sqlunet.browser.common.R;
import org.sqlunet.concurrency.Cancelable;
import org.sqlunet.concurrency.TaskObserver;
import org.sqlunet.settings.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * About fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class AssetLoadFragment extends Fragment implements TaskObserver.Observer<Number>
{
	static private final String TAG = "AssetF";

	private TextView titleTextView;

	private TextView messageTextView;

	private ProgressBar progressBar;

	private TextView progressTextView;

	private TextView statusTextView;

	private Button cancelButton;

	@Nullable
	private Cancelable task;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public AssetLoadFragment()
	{
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_assetload, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// find view components to control loading
		this.titleTextView = view.findViewById(R.id.title);
		this.messageTextView = view.findViewById(R.id.message);
		this.statusTextView = view.findViewById(R.id.status);
		this.progressBar = view.findViewById(R.id.progressBar);
		this.progressTextView = view.findViewById(R.id.progressProgress);
		this.cancelButton = view.findViewById(R.id.cancelButton);
		this.cancelButton.setOnClickListener((v) -> {

			boolean result = this.task != null && this.task.cancel(true);
			Log.d(TAG, "Cancel task @" + (this.task == null ? "null" : Integer.toHexString(this.task.hashCode())) + ' ' + result);
		});

		// load assets
		final Context context = requireContext();
		final String asset = Settings.getAssetPack(context);
		final String assetDir = Settings.getAssetPackDir(context);
		final String assetZip = Settings.getAssetPackZip(context);
		final String assetZipEntry = context.getString(R.string.asset_zip_entry);
		SetupAsset.deliverAsset(asset, assetDir, assetZip, assetZipEntry, requireActivity(), this, () -> EntryActivity.rerun(requireContext()), getView());
	}

	@Override
	public void onResume()
	{
		super.onResume();

		final AppCompatActivity activity = (AppCompatActivity) requireActivity();
		final ActionBar actionBar = activity.getSupportActionBar();
		assert actionBar != null;
		actionBar.setCustomView(null);
		actionBar.setBackgroundDrawable(null);
	}

	@Override
	public void taskStart(@NonNull final Cancelable task)
	{
		this.task = task;
		this.cancelButton.setVisibility(View.VISIBLE);
		this.progressBar.setIndeterminate(true);
		this.progressTextView.setText("");
		this.messageTextView.setText("");
	}

	@Override
	public void taskFinish(final boolean success)
	{
		this.task = null;
		this.cancelButton.setVisibility(View.GONE);
		this.progressBar.setIndeterminate(false);
		this.progressBar.setMax(100);
		this.progressBar.setProgress(100);
		this.messageTextView.setText(success ? R.string.result_success : R.string.result_fail);
	}

	@Override
	public void taskProgress(@NonNull final Number progress, @NonNull final Number length, @Nullable String unit)
	{
		final long longProgress = progress.longValue();
		final long longLength = length.longValue();
		final boolean indeterminate = longLength == -1L;
		this.progressTextView.setText(TaskObserver.countToString(longProgress, longLength, unit));
		this.progressBar.setIndeterminate(indeterminate);
		if (!indeterminate)
		{
			final int percent = (int) ((longProgress * 100F) / longLength);
			this.progressBar.setMax(100);
			this.progressBar.setProgress(percent);
		}
	}

	@Override
	public void taskUpdate(@NonNull final CharSequence status)
	{
		this.statusTextView.setText(status);
	}

	@Nullable
	@Override
	public TaskObserver.Observer<Number> setTitle(@NonNull final CharSequence title)
	{
		this.titleTextView.setText(title);
		return null;
	}

	@NonNull
	@Override
	public TaskObserver.Observer<Number> setMessage(@NonNull final CharSequence message)
	{
		this.messageTextView.setText(message);
		return this;
	}
}
