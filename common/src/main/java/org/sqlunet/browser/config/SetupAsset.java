/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.config;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.assetpacks.AssetPackLocation;
import com.google.android.play.core.assetpacks.AssetPackManager;
import com.google.android.play.core.assetpacks.AssetPackManagerFactory;

import org.sqlunet.assetpack.AssetPackLoader;
import org.sqlunet.browser.common.R;
import com.bbou.concurrency.TaskObserver;
import com.bbou.download.FileAsyncTask;
import com.bbou.download.Settings;
import org.sqlunet.settings.StorageSettings;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class SetupAsset
{
	static public final String PREF_ASSET_PRIMARY_DEFAULT = "pref_asset_primary_default";

	static public final String PREF_ASSET_AUTO_CLEANUP = "pref_asset_auto_cleanup";

	// public static final String ASSET_ARCHIVE_ENTRY = "sqlunet.db";

	// public static final String TARGET_DB = "sqlunet.db";

	/**
	 * Deliver asset
	 *
	 * @param assetPack     asset pack name
	 * @param assetDir      asset pack dir
	 * @param assetZip      asset pack zip
	 * @param assetZipEntry asset pack zip entry
	 * @param activity      activity
	 * @param observer      observer
	 * @param view          view for snackbar
	 * @return path if already installed
	 */
	@Nullable
	@SuppressWarnings("UnusedReturnValue")
	public static String deliverAsset(@NonNull final String assetPack, @NonNull final String assetDir, @NonNull final String assetZip, @NonNull final String assetZipEntry, @NonNull final Activity activity, @NonNull final TaskObserver.Observer<Number> observer, @Nullable Runnable whenComplete, @Nullable final View view)
	{
		if (assetPack.isEmpty())
		{
			throw new RuntimeException("Asset is empty");
		}
		if (assetZip.isEmpty())
		{
			throw new RuntimeException("Asset zip is empty");
		}
		if (assetDir.isEmpty())
		{
			throw new RuntimeException("Asset dir is empty");
		}

		if (view != null)
		{
			Snackbar.make(view, R.string.action_asset_deliver, Snackbar.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(activity, R.string.action_asset_deliver, Toast.LENGTH_SHORT).show();
		}

		// observer title and message
		observer //
				.setTitle(activity.getString(R.string.title_dialog_assetload)) //
				.setMessage(activity.getString(R.string.gloss_asset_delivery_message));

		// deliver asset (returns non null path if already installed)
		final String path0 = new AssetPackLoader(activity, assetPack) //
				.assetPackDelivery(activity, observer, () -> {

					// run when delivery completes
					final AssetPackManager assetPackManager = AssetPackManagerFactory.getInstance(activity);
					final AssetPackLocation packLocation = assetPackManager.getPackLocation(assetPack);
					if (packLocation != null)
					{
						final String path = packLocation.assetsPath();
						final String zipFilePath = new File(new File(path, assetDir), assetZip).getAbsolutePath();

						//final TaskObserver.Observer<Number> observer2 = new TaskDialogObserver<>(activity.getSupportFragmentManager());
						@NonNull final TaskObserver.Observer<Number> observer2 = observer;
						observer2 //
								.setTitle(activity.getString(R.string.action_unzip_from_asset)) //
								.setMessage(zipFilePath);

						FileAsyncTask.launchUnzip(activity, observer2, zipFilePath, assetZipEntry, StorageSettings.getDatabasePath(activity), (result) -> {

							org.sqlunet.assetpack.Settings.recordDbAsset(activity, assetPack);
							Settings.recordDatapackSource(activity, zipFilePath, -1, -1, null, null, null);
							if (whenComplete != null)
							{
								whenComplete.run();
							}
						});
					}
				});

		// if already installed
		if (path0 != null)
		{
			if (view != null)
			{
				Snackbar.make(view, R.string.action_asset_installed, Snackbar.LENGTH_LONG)
						//.setAction(R.string.action_asset_md5, (view2) -> FileAsyncTask.launchMd5(activity, new File(activity.getFilesDir(), TARGET_DB).getAbsolutePath()))
						//.setAction(R.string.action_asset_dispose, (view2) -> disposeAsset(assetPack, activity, view2))
						.show();
			}
			else
			{
				Toast.makeText(activity, R.string.action_asset_installed, Toast.LENGTH_LONG).show();
			}

			/* boolean success = */
			SetupDatabaseTasks.deleteDatabase(activity, StorageSettings.getDatabasePath(activity));

			final File zipFile = new File(new File(path0, assetDir), assetZip);
			final String zipFilePath = zipFile.getAbsolutePath();
			if (zipFile.exists())
			{
				observer //
						.setTitle(activity.getString(R.string.action_unzip_from_asset)) //
						.setMessage(zipFilePath);
				FileAsyncTask.launchUnzip(activity, observer, zipFilePath, assetZipEntry, StorageSettings.getDatabasePath(activity), (result) -> {

					org.sqlunet.assetpack.Settings.recordDbAsset(activity, assetPack);
					Settings.recordDatapackSource(activity, zipFilePath, -1, -1, null, null, null);
					if (whenComplete != null)
					{
						whenComplete.run();
					}
				});
			}
			else
			{
				observer.setTitle(activity.getString(R.string.action_unzip_from_asset));
				observer.setMessage(activity.getString(R.string.status_error_no_file) + ' ' + zipFilePath);
			}
		}
		return path0;
	}

	/**
	 * Dispose of asset
	 *
	 * @param assetPack asset pack name
	 * @param activity  activity
	 */
	public static void disposeAsset(@NonNull final String assetPack, @NonNull final FragmentActivity activity)
	{
		if (assetPack.isEmpty())
		{
			throw new RuntimeException("Asset is empty");
		}
		AssetPackLoader.assetPackRemove(activity, assetPack);
	}

	/**
	 * Dispose of assets
	 *
	 * @param activity activity
	 */
	public static void disposeAllAssets(@NonNull final FragmentActivity activity)
	{
		String assetPack1 = activity.getString(R.string.asset_primary);
		String assetPack2 = activity.getString(R.string.asset_alt);
		AssetPackLoader.assetPackRemove(activity, assetPack1, assetPack2);
	}
}
