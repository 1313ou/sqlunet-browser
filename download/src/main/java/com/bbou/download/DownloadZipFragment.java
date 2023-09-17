/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

/**
 * Download fragment using DownloadZipWork
 * Interface between work and activity.
 * Cancel messages are to be sent to this fragment's receiver.
 * Signals completion through the OnComplete callback in the activity.
 * This fragment uses a zipped file zip downloader core (only matched
 * entries or all by default) are written to target directory location.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadZipFragment extends BaseDownloadFragment
{
	static protected final String TAG = "ZipDownloadF";

	/**
	 * To argument
	 */
	static public final String DOWNLOAD_TO_DIR_ARG = "download_to_dir";

	/**
	 * Zip entry argument
	 */
	static public final String DOWNLOAD_ENTRY_ARG = "entry";

	/**
	 * Download source entry
	 */
	@SuppressWarnings("WeakerAccess")
	@Nullable
	private String sourceEntry;

	/**
	 * Destination dir
	 */
	@Nullable
	private File toDir;

	// A R G U M E N T S

	@Override
	protected void unmarshal()
	{
		// arguments
		final Bundle arguments = getArguments();
		final String toDirArg = arguments == null ? null : arguments.getString(DOWNLOAD_TO_DIR_ARG);
		final String sourceEntryArg = arguments == null ? null : arguments.getString(DOWNLOAD_ENTRY_ARG);

		this.toDir = toDirArg != null ? new File(toDirArg) : null;
		this.sourceEntry = sourceEntryArg;

		// adjust source data
		assert this.sourceUrl != null;
		if (this.sourceUrl.endsWith(".zip"))
		{
			this.sourceUrl = this.sourceUrl.substring(0, this.sourceUrl.length() - 4);
		}
		assert this.downloadUrl != null;
		if (!this.downloadUrl.endsWith(".zip"))
		{
			this.downloadUrl += ".zip";
		}
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
				assert this.toDir != null;
				final String to = this.toDir.getAbsolutePath();
				final String entry = this.sourceEntry;

				// start job
				start(from, entry, to, this.renameFrom, this.renameTo);

				// status
				this.downloading = true; // set
				return;
			}
		}
		throw new RuntimeException("Already downloading");
	}

	/**
	 * Start
	 *
	 * @param fromUrl source zip url
	 * @param entry   source zip entry
	 * @param toDir   destination dir
	 */
	protected void start(@NonNull final String fromUrl, @Nullable final String entry, @NonNull final String toDir, @Nullable final String renameFrom, @Nullable final String renameTo)
	{
		this.uuid = DownloadZipWork.startWork(requireContext(), fromUrl, entry, toDir, renameFrom, renameTo, this, this.observer);
	}

	// L A Y O U T

	@Override
	protected int getResId()
	{
		return R.layout.fragment_zip_download;
	}

	// S E T   D E S T I N A T I O N

	protected void setDestination(@NonNull final View view)
	{
		final TextView targetView = view.findViewById(R.id.target);
		final TextView targetView2 = view.findViewById(R.id.target2);
		final TextView targetView3 = view.findViewById(R.id.target3);
		final TextView targetView4 = view.findViewById(R.id.target4);

		targetView.setText(this.toDir != null ? this.toDir.getAbsolutePath() : "");
		if (targetView2 != null)
		{
			String selectEntry = getString(R.string.select_zip_entry, this.sourceEntry == null ? "*" : this.sourceEntry);
			targetView2.setText(selectEntry);
		}
		if (targetView3 != null && this.renameFrom != null)
		{
			String from = getString(R.string.rename_source, this.renameFrom);
			targetView3.setText(from);
		}
		if (targetView4 != null && this.renameTo != null)
		{
			String to = getString(R.string.rename_dest, this.renameTo);
			targetView4.setText(to);
		}
	}

	// A B S T R A C T

	@Override
	protected void deploy()
	{
	}

	@Override
	protected void md5()
	{
	}

	@Override
	protected void cleanup()
	{
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

		// super.onDone(status);

		// UI
		requireActivity().runOnUiThread(() -> {

			endUI(status);
		});

		// complete
		onComplete(status != Status.STATUS_SUCCEEDED);
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
		final String to = this.toDir == null ? context.getString(R.string.result_deleted) : this.toDir.getName();
		String contentText = from + '→' + to;
		Notifier.fireNotification(context, notificationId, type, contentText, args);
	}
}
