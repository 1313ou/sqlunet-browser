/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.assetpacks.AssetPackLocation;
import com.google.android.play.core.assetpacks.AssetPackManager;
import com.google.android.play.core.assetpacks.AssetPackManagerFactory;

import org.sqlunet.assetpack.AssetPackLoader;
import org.sqlunet.browser.EntryActivity;
import org.sqlunet.browser.common.R;
import org.sqlunet.concurrency.TaskDialogObserver;
import org.sqlunet.concurrency.TaskObserver;
import org.sqlunet.download.FileAsyncTask;
import org.sqlunet.download.Settings;
import org.sqlunet.settings.StorageSettings;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class SetupAsset
{
	static public final String PREF_ASSET_PRIMARY_DEFAULT = "pref_asset_primary_default";

	static public final String PREF_ASSET_AUTO_CLEANUP = "pref_asset_auto_cleanup";

	/**
	 * Entry name in archive
	 */
	public static final String ASSET_ARCHIVE_ENTRY = "sqlunet.db";

	// public static final String TARGET_DB = "sqlunet.db";

	/**
	 * Deliver asset
	 *
	 * @param assetPack asset pack name
	 * @param assetDir  asset pack dir
	 * @param assetZip  asset pack zip
	 * @param activity  activity
	 * @param view      view for snackbar
	 * @return path if already installed
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static String deliverAsset(@NonNull final String assetPack, @NonNull final String assetDir, @NonNull final String assetZip, @NonNull final FragmentActivity activity, @Nullable final View view)
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
		final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager(), activity.getString(R.string.asset_delivery), assetPack, "MB");
		final String path0 = new AssetPackLoader(activity, assetPack).assetPackDelivery(activity, observer, () -> {

			// run when delivery completes
			final AssetPackManager assetPackManager = AssetPackManagerFactory.getInstance(activity);
			final AssetPackLocation packLocation = assetPackManager.getPackLocation(assetPack);
			assert packLocation != null;
			final String path = packLocation.assetsPath();
			FileAsyncTask.launchUnzip(activity, new File(new File(path, assetDir), assetZip).getAbsolutePath(), ASSET_ARCHIVE_ENTRY, StorageSettings.getDatabasePath(activity), () -> {

				org.sqlunet.assetpack.Settings.recordDbAsset(activity, assetPack);
				Settings.recordDbSource(activity, new File(new File(path, assetDir), assetZip).getAbsolutePath(), -1, -1);
				EntryActivity.reenter(activity);
			});
		});

		// if already installed
		if (path0 != null)
		{
			if (view != null)
			{
				Snackbar.make(view, R.string.action_asset_installed, Snackbar.LENGTH_LONG)
						//.setAction(R.string.action_asset_md5, (view2) -> FileAsyncTask.launchMd5(activity, new File(activity.getFilesDir(), TARGET_DB).getAbsolutePath()))
						//.setAction(R.string.action_asset_dispose, (view2) -> disposeAsset(assetPack, activity, view2))
						.setAction(R.string.action_asset_deploy, (view2) -> FileAsyncTask.launchUnzip(activity, new File(new File(path0, assetDir), assetZip).getAbsolutePath(), ASSET_ARCHIVE_ENTRY, StorageSettings.getDatabasePath(activity), () -> {

							org.sqlunet.assetpack.Settings.recordDbAsset(activity, assetPack);
							Settings.recordDbSource(activity, new File(new File(path0, assetDir), assetZip).getAbsolutePath(), -1, -1);
							EntryActivity.reenter(activity);
						})) //
						.show();
			}
			else
			{
				Toast.makeText(activity, R.string.action_asset_installed, Toast.LENGTH_LONG).show();
				FileAsyncTask.launchUnzip(activity, new File(new File(path0, assetDir), assetZip).getAbsolutePath(), ASSET_ARCHIVE_ENTRY, StorageSettings.getDatabasePath(activity), () -> {

					org.sqlunet.assetpack.Settings.recordDbAsset(activity, assetPack);
					Settings.recordDbSource(activity, new File(new File(path0, assetDir), assetZip).getAbsolutePath(), -1, -1);
					EntryActivity.reenter(activity);
				});
			}
			return path0;
		}
		return null;
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
	 * @param activity  activity
	 */
	public static void disposeAllAssets(@NonNull final FragmentActivity activity)
	{
		String assetPack1 = activity.getString(R.string.asset_primary);
		String assetPack2 = activity.getString(R.string.asset_alt);
		AssetPackLoader.assetPackRemove(activity, assetPack1, assetPack2);
	}
}
