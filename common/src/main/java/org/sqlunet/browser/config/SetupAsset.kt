/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.config;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.bbou.concurrency.observe.TaskObserver;
import com.bbou.deploy.workers.FileTasks;
import com.bbou.download.DownloadData;
import com.bbou.download.preference.Settings;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.assetpacks.AssetPackLocation;
import com.google.android.play.core.assetpacks.AssetPackManager;
import com.google.android.play.core.assetpacks.AssetPackManagerFactory;

import org.sqlunet.assetpack.AssetPackLoader;
import org.sqlunet.browser.common.R;
import org.sqlunet.settings.StorageSettings;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import kotlin.Pair;

public class SetupAsset
{
	static public final String PREF_ASSET_PRIMARY_DEFAULT = "pref_asset_primary_default";

	static public final String PREF_ASSET_AUTO_CLEANUP = "pref_asset_auto_cleanup";

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
	public static String deliverAsset(@NonNull final String assetPack, @NonNull final String assetDir, @NonNull final String assetZip, @NonNull final String assetZipEntry, @NonNull final Activity activity, @NonNull final TaskObserver<Pair<Number, Number>> observer, @Nullable Runnable whenComplete, @Nullable final View view)
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

						FileTasks.launchUnzip(activity, observer, zipFilePath, assetZipEntry, StorageSettings.getDatabasePath(activity), (result) -> {

							org.sqlunet.assetpack.Settings.recordDbAsset(activity, assetPack);
							Settings.recordDatapackSource(activity, new DownloadData(zipFilePath, null, null, null, null, null, null), "asset");
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
				FileTasks.launchUnzip(activity, observer, zipFilePath, assetZipEntry, StorageSettings.getDatabasePath(activity), (result) -> {

					org.sqlunet.assetpack.Settings.recordDbAsset(activity, assetPack);
					Settings.recordDatapackSource(activity, new DownloadData(zipFilePath, null, null, null, null, null, null), "asset");
					if (whenComplete != null)
					{
						whenComplete.run();
					}
				});
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
