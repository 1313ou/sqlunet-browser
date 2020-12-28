package org.sqlunet.assetpack;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.android.play.core.assetpacks.AssetPackLocation;
import com.google.android.play.core.assetpacks.AssetPackManager;
import com.google.android.play.core.assetpacks.AssetPackManagerFactory;
import com.google.android.play.core.assetpacks.AssetPackState;
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener;
import com.google.android.play.core.assetpacks.AssetPackStates;
import com.google.android.play.core.assetpacks.model.AssetPackStatus;
import com.google.android.play.core.tasks.RuntimeExecutionException;

import org.sqlunet.concurrency.Cancelable;
import org.sqlunet.concurrency.TaskObserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AssetPackLoader implements Cancelable
{
	private static final String LOGTAG = "AssetPackLoader";

	@NonNull
	private final String pack;

	private final AssetPackManager assetPackManager;

	@Nullable
	private final TaskObserver.Observer<Number> observer;

	private boolean waitForWifiConfirmationShown = false;

	public AssetPackLoader(@NonNull final Context context, @NonNull final String pack, @Nullable final TaskObserver.Observer<Number> observer)
	{
		this.pack = pack;
		this.assetPackManager = AssetPackManagerFactory.getInstance(context);
		this.observer = observer;
		dumpLocalTesting(context);
	}

	public String packAccessDelivery(@NonNull final Activity activity, @Nullable Runnable whenReady)
	{
		// pack location
		final AssetPackLocation packLocation = this.assetPackManager.getPackLocation(this.pack);
		if (packLocation != null)
		{
			final String location = packLocation.assetsPath();
			Log.d(LOGTAG, location);
			return location;
		}

		// observer
		this.observer.taskStart(this);

		// state
		this.assetPackManager //
				.getPackStates(Collections.singletonList(this.pack)) //
				.addOnCompleteListener(task1 -> {

					try
					{
						AssetPackStates assetPackStates1 = task1.getResult();
						AssetPackState assetPackState1 = assetPackStates1.packStates().get(this.pack);
						assert assetPackState1 != null;
						int status1 = assetPackState1.status();
						Log.d(LOGTAG, String.format("Assetpack %s status %s %d/%d %d%%", assetPackState1.name(), status(status1), assetPackState1.bytesDownloaded(), assetPackState1.totalBytesToDownload(), assetPackState1.transferProgressPercentage()));
						if (AssetPackStatus.NOT_INSTALLED == status1)
						{
							assetPackManager.registerListener(new AssetPackStateUpdateListener()
							{
								@Override
								public void onStateUpdate(@NonNull final AssetPackState assetPackState2)
								{
									int status2 = assetPackState2.status();
									String status2Str = status(status2);
									Log.d(LOGTAG, "Status " + status2Str);
									AssetPackLoader.this.observer.taskUpdate(status2Str);

									switch (status2)
									{
										case AssetPackStatus.NOT_INSTALLED: // Asset pack is not downloaded yet.
											break;

										case AssetPackStatus.PENDING:
											break;

										case AssetPackStatus.DOWNLOADING:
											long downloaded = assetPackState2.bytesDownloaded();
											long totalSize = assetPackState2.totalBytesToDownload();
											Log.i(LOGTAG, "Status downloading progress " + String.format("%d / %d", downloaded, totalSize));
											AssetPackLoader.this.observer.taskProgress(downloaded, totalSize);
											break;

										case AssetPackStatus.TRANSFERRING: // 100% downloaded and assets are being transferred. Notify user to wait until transfer is complete.
											int percent2 = assetPackState2.transferProgressPercentage();
											String percent2Str = String.format("%d %%", percent2);
											Log.i(LOGTAG, "Status transferring progress " + percent2Str);
											AssetPackLoader.this.observer.taskUpdate(percent2Str);
											break;

										case AssetPackStatus.COMPLETED: // Asset pack is ready to use.
											final AssetPackLocation packLocation1 = AssetPackLoader.this.assetPackManager.getPackLocation(AssetPackLoader.this.pack);
											Log.d(LOGTAG, "Status asset path " + (packLocation1 == null ? "null" : packLocation1.assetsPath()));
											AssetPackLoader.this.assetPackManager.unregisterListener(this);
											AssetPackLoader.this.observer.taskFinish(true);
											whenReady.run();
											break;

										case AssetPackStatus.FAILED: // Request failed. Notify user.
											AssetPackLoader.this.assetPackManager.unregisterListener(this);
											AssetPackLoader.this.observer.taskFinish(false);
											int errorCode = assetPackState2.errorCode();
											Log.e(LOGTAG, "Status error " + errorCode);
											AssetPackLoader.this.observer.taskUpdate(Integer.toString(errorCode));
											break;

										case AssetPackStatus.CANCELED:  // Request canceled. Notify user.
											AssetPackLoader.this.assetPackManager.unregisterListener(this);
											AssetPackLoader.this.observer.taskFinish(false);
											Log.e(LOGTAG, "Status canceled" + assetPackState2.errorCode());
											break;

										case AssetPackStatus.WAITING_FOR_WIFI:
											if (!AssetPackLoader.this.waitForWifiConfirmationShown)
											{
												AssetPackLoader.this.assetPackManager.showCellularDataConfirmation(activity).addOnSuccessListener(resultCode -> {

													if (resultCode == Activity.RESULT_OK)
													{
														Log.d(LOGTAG, "Confirmation dialog has been accepted.");
													}
													else if (resultCode == Activity.RESULT_CANCELED)
													{
														Log.d(LOGTAG, "Confirmation dialog has been denied by the user.");
													}
												});
												AssetPackLoader.this.waitForWifiConfirmationShown = true;
											}
											break;

										case AssetPackStatus.UNKNOWN:
											// Asset pack state is not known.
											break;
									}
								}
							});

							assetPackManager.fetch(Collections.singletonList(this.pack)) //
								/*
								.addOnCompleteListener(task2 -> {

									AssetPackStates assetPackStates2 = task2.getResult();
									AssetPackState assetPackState2 = assetPackStates2.packStates().get(this.pack);
									assert assetPackState2 != null;
									int status2 = assetPackState2.status();
									Log.d(LOGTAG, "Status2 completed " + status(status2));
									switch (status2)
									{
										case AssetPackStatus.FAILED:
											// Request failed. Notify user.
											Log.e(LOGTAG, "error " + assetPackState2.errorCode());
											break;

										default:
											break;
									}
								}) //
								.addOnFailureListener(exception -> {

									Log.d(LOGTAG, "Status2 failure " + exception.getMessage());
								}) //
								.addOnSuccessListener(task2 -> {

									Log.d(LOGTAG, "Status2 success ");
									final AssetPackLocation packLocation2 = this.assetPackManager.getPackLocation(this.pack);
									Log.d(LOGTAG, "Status2 path asset " + (packLocation2 == null ? "null" : packLocation2.assetsPath()));
								})
								*/;
							;
						}
					}
					catch (RuntimeExecutionException e)
					{
						Log.e(LOGTAG, e.getMessage());
					}
				});
		return null;
	}

	private static String status(int status)
	{
		switch (status)
		{
			case AssetPackStatus.UNKNOWN:
				return "unknown";
			case AssetPackStatus.PENDING:
				return "pending";
			case AssetPackStatus.DOWNLOADING:
				return "downloading";
			case AssetPackStatus.TRANSFERRING:
				return "transferring";
			case AssetPackStatus.COMPLETED:
				return "completed";
			case AssetPackStatus.FAILED:
				return "failed";
			case AssetPackStatus.CANCELED:
				return "canceled";
			case AssetPackStatus.WAITING_FOR_WIFI:
				return "waiting for wifi";
			case AssetPackStatus.NOT_INSTALLED:
				return "not installed";
		}
		return null;
	}

	public static void packAccessDelete(final Activity activity, final String pack)
	{
		// manager
		final AssetPackManager assetPackManager = AssetPackManagerFactory.getInstance(activity);

		assetPackManager.removePack(pack) //
				.addOnCompleteListener(task3 -> {

					boolean success = task3.isSuccessful();
					Log.d(LOGTAG, "Success3 " + success);
				}) //
				.addOnFailureListener(exception -> {

					Log.e(LOGTAG, exception.getMessage());
				});
	}

	public static InputStream packAccessDeliveryOnInstall(@NonNull Context context0) throws PackageManager.NameNotFoundException, IOException
	{
		final Context context = context0.createPackageContext("org.test.pad", 0);
		final AssetManager assetManager = context.getAssets();
		final InputStream is = assetManager.open("asset-name");
		return is;
	}

	@Override
	public boolean cancel(final boolean mayInterruptIfRunning)
	{
		AssetPackLoader.this.assetPackManager.cancel(Collections.singletonList(this.pack));
		return true;
	}

	static void dumpLocalTesting(@NonNull Context context)
	{
		File[] ds = context.getExternalFilesDirs(null);
		for (File d : ds)
		{
			File dir = new File(d, "local_testing");
			if (dir.exists() && dir.isDirectory())
			{
				String[] content = new File(d, "local_testing").list();
				if (content != null)
				{
					for (String f : content)
					{
						Log.d(LOGTAG, "exists " + f);
					}
				}
			}
		}
	}
}