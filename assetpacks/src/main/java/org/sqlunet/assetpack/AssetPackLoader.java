package org.sqlunet.assetpack;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.play.core.assetpacks.AssetPackLocation;
import com.google.android.play.core.assetpacks.AssetPackManager;
import com.google.android.play.core.assetpacks.AssetPackManagerFactory;
import com.google.android.play.core.assetpacks.AssetPackState;
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener;
import com.google.android.play.core.assetpacks.AssetPackStates;
import com.google.android.play.core.assetpacks.model.AssetPackErrorCode;
import com.google.android.play.core.assetpacks.model.AssetPackStatus;
import com.google.android.play.core.tasks.RuntimeExecutionException;

import org.sqlunet.concurrency.Cancelable;
import org.sqlunet.concurrency.TaskObserver;

import java.io.File;
import java.util.Collections;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AssetPackLoader implements Cancelable
{
	private static final String TAG = "AssetPackLoader";

	@NonNull
	private final String pack;

	@NonNull
	private final AssetPackManager assetPackManager;

	private boolean waitForWifiConfirmationShown = false;

	/**
	 * Constructor
	 *
	 * @param context context
	 * @param pack    asset pack name
	 */
	public AssetPackLoader(@NonNull final Context context, @NonNull final String pack)
	{
		this.assetPackManager = AssetPackManagerFactory.getInstance(context);
		this.pack = pack;
		dumpLocalTesting(context);
	}

	/**
	 * Asset pack path
	 *
	 * @return asset pack path if installed, null other wise
	 */
	@Nullable
	public String assetPackPathIfInstalled()
	{
		// pack location
		final AssetPackLocation packLocation = this.assetPackManager.getPackLocation(this.pack);
		if (packLocation != null)
		{
			final String path = packLocation.assetsPath();
			Log.d(TAG, "Asset path " + path);
			return path;
		}
		return null;
	}

	/**
	 * Asset pack delivery
	 *
	 * @param activity  activity
	 * @param observer  observer
	 * @param whenReady to run when ready
	 * @return asset pack path if pack was installed
	 */
	@Nullable
	public String assetPackDelivery(@NonNull final Activity activity, @NonNull final TaskObserver.Observer<Number> observer, @Nullable Runnable whenReady)
	{
		// pack location
		final AssetPackLocation packLocation = this.assetPackManager.getPackLocation(this.pack);
		if (packLocation != null)
		{
			final String path = packLocation.assetsPath();
			Log.d(TAG, "Asset path " + path);
			return path;
		}

		// observer
		observer.taskStart(this);

		// listener
		this.assetPackManager.registerListener(new Listener(activity, observer, whenReady));

		// fetch if uninstalled
		this.assetPackManager //
				.getPackStates(Collections.singletonList(this.pack)) //
				.addOnCompleteListener(getStateTask -> {

					try
					{
						// state
						final AssetPackStates states = getStateTask.getResult();
						final AssetPackState state = states.packStates().get(this.pack);
						assert state != null;
						final int status = state.status();
						Log.i(TAG, String.format("AssetPack %s status %s %d/%d %d%%", state.name(), statusToString(status), state.bytesDownloaded(), state.totalBytesToDownload(), state.transferProgressPercentage()));

						if (AssetPackStatus.NOT_INSTALLED == status || AssetPackStatus.CANCELED == status || AssetPackStatus.FAILED == status)
						{
							// do not eat error
							if (AssetPackStatus.FAILED == status)
							{
								int errorCode = state.errorCode();
								observer.taskUpdate(statusToString(status) + ' ' + errorToString(errorCode));
							}

							// fetch
							// returns an AssetPackStates object containing a list of packs and their initial download states and sizes.
							// if an asset pack requested via fetch() is already downloading, the download status is returned and no additional download is started.
							/* Task<AssetPackStates> fetchTask0 = */
							this.assetPackManager.fetch(Collections.singletonList(this.pack)) //

									.addOnCompleteListener(fetchTask -> {

										final AssetPackStates fetchAssetPackStates = fetchTask.getResult();
										final AssetPackState fetchAssetPackState = fetchAssetPackStates.packStates().get(this.pack);
										assert fetchAssetPackState != null;
										final int fetchStatus = fetchAssetPackState.status();
										Log.i(TAG, "OnFetchCompleted " + statusToString(fetchStatus));
										if (fetchStatus == AssetPackStatus.FAILED)
										{
											int errorCode = fetchAssetPackState.errorCode();
											Log.e(TAG, "OnFetchCompleted with error " + errorCode + ' ' + errorToString(errorCode));
										}
									}) //
									.addOnFailureListener(exception -> Log.i(TAG, "OnFetchFailure " + exception.getMessage())) //
									.addOnSuccessListener(task2 -> {

										Log.i(TAG, "OnFetchSuccess ");
										final AssetPackLocation packLocation2 = this.assetPackManager.getPackLocation(this.pack);
										Log.i(TAG, "OnFetchSuccess, Path asset " + (packLocation2 == null ? "null" : packLocation2.assetsPath()));
									});
						}
						else if (AssetPackStatus.COMPLETED == status)
						{
							final AssetPackLocation packLocation1 = AssetPackLoader.this.assetPackManager.getPackLocation(AssetPackLoader.this.pack);
							Log.i(TAG, "Status asset path " + (packLocation1 == null ? "null" : packLocation1.assetsPath()));
							observer.taskUpdate(statusToString(status));
							observer.taskFinish(true);
							if (whenReady != null)
							{
								whenReady.run();
							}
						}
					}
					catch (RuntimeExecutionException e)
					{
						Log.e(TAG, "Failure " + e.getMessage());
					}
				});
		return null;
	}

	class Listener implements AssetPackStateUpdateListener
	{
		@NonNull
		final Activity activity;

		@NonNull
		final TaskObserver.Observer<Number> observer;

		@Nullable
		final Runnable whenReady;

		Listener(@NonNull final Activity activity, @NonNull final TaskObserver.Observer<Number> observer, @Nullable Runnable whenReady)
		{
			this.activity = activity;
			this.observer = observer;
			this.whenReady = whenReady;
		}

		@Override
		public void onStateUpdate(@NonNull final AssetPackState state)
		{
			int status = state.status();
			String statusStr = statusToString(status);
			Log.d(TAG, "Status " + statusStr);

			switch (status)
			{
				case AssetPackStatus.UNKNOWN: // The pack has never been requested before.
				case AssetPackStatus.NOT_INSTALLED: // Asset pack is not downloaded yet.
				case AssetPackStatus.PENDING: // The asset pack download is pending and will be processed soon.
					// Asset pack state is not known.
					this.observer.taskUpdate(statusStr);
					break;

				case AssetPackStatus.DOWNLOADING:
					long downloaded = state.bytesDownloaded();
					long totalSize = state.totalBytesToDownload();
					Log.i(TAG, "Status downloading progress " + String.format("%d / %d", downloaded, totalSize));
					this.observer.taskUpdate(statusStr);
					this.observer.taskProgress(downloaded, totalSize, null);
					break;

				case AssetPackStatus.TRANSFERRING: // 100% downloaded and assets are being transferred. Notify user to wait until transfer is complete.
					int percent2 = state.transferProgressPercentage();
					String percent2Str = String.format(Locale.getDefault(), "%d %%", percent2);
					Log.i(TAG, "Status transferring progress " + percent2Str);
					this.observer.taskUpdate(statusStr + ' ' + percent2Str);
					this.observer.taskProgress(percent2, -1, "%");
					break;

				case AssetPackStatus.WAITING_FOR_WIFI: // The asset pack download is waiting for Wi-Fi to become available before proceeding.
					this.observer.taskUpdate(statusStr);
					if (!AssetPackLoader.this.waitForWifiConfirmationShown)
					{
						AssetPackLoader.this.assetPackManager.showCellularDataConfirmation(this.activity).addOnSuccessListener(resultCode -> {

							if (resultCode == Activity.RESULT_OK)
							{
								Log.d(TAG, "Confirmation dialog has been accepted.");
							}
							else if (resultCode == Activity.RESULT_CANCELED)
							{
								Log.d(TAG, "Confirmation dialog has been denied by the user.");
							}
						});
						AssetPackLoader.this.waitForWifiConfirmationShown = true;
					}
					break;

				case AssetPackStatus.COMPLETED: // Asset pack is ready to use.
					AssetPackLoader.this.assetPackManager.unregisterListener(this);
					final AssetPackLocation packLocation1 = AssetPackLoader.this.assetPackManager.getPackLocation(AssetPackLoader.this.pack);
					Log.i(TAG, "Status asset path " + (packLocation1 == null ? "null" : packLocation1.assetsPath()));
					this.observer.taskUpdate(statusStr);
					this.observer.taskFinish(true);
					if (this.whenReady != null)
					{
						this.whenReady.run();
					}
					break;

				case AssetPackStatus.FAILED: // Request failed. Notify user.
					AssetPackLoader.this.assetPackManager.unregisterListener(this);
					int errorCode = state.errorCode();
					Log.e(TAG, "Status error " + errorCode + ' ' + errorToString(errorCode));
					this.observer.taskUpdate(statusStr);
					this.observer.taskFinish(false);
					this.observer.taskUpdate("Error " + errorToString(errorCode));
					break;

				case AssetPackStatus.CANCELED:  // Request canceled. Notify user.
					AssetPackLoader.this.assetPackManager.unregisterListener(this);
					int errorCode2 = state.errorCode();
					Log.i(TAG, "Status canceled " + errorCode2 + ' ' + errorToString(errorCode2));
					this.observer.taskUpdate(statusStr);
					this.observer.taskFinish(false);
					break;
			}
		}
	}

	/**
	 * Status to string
	 *
	 * @param status numeric status
	 * @return status string
	 */
	@NonNull
	private static String statusToString(int status)
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
		return "illegal";
	}

	/**
	 * Error to string
	 *
	 * @param error numeric static
	 * @return error string
	 */
	@NonNull
	private static String errorToString(int error)
	{
		switch (error)
		{
			case AssetPackErrorCode.NO_ERROR:
				return "no error";
			case AssetPackErrorCode.APP_UNAVAILABLE:
				return "app unavailable";
			case AssetPackErrorCode.PACK_UNAVAILABLE:
				return "pack unavailable";
			case AssetPackErrorCode.INVALID_REQUEST:
				return "invalid request";
			case AssetPackErrorCode.DOWNLOAD_NOT_FOUND:
				return "download not found";
			case AssetPackErrorCode.API_NOT_AVAILABLE:
				return "api not available";
			case AssetPackErrorCode.NETWORK_ERROR:
				return "network error";
			case AssetPackErrorCode.ACCESS_DENIED:
				return "access denied";
			case AssetPackErrorCode.INSUFFICIENT_STORAGE:
				return "insufficient storage";
			case AssetPackErrorCode.APP_NOT_OWNED:
				return "app not owned (not acquired from Play)";
			case AssetPackErrorCode.INTERNAL_ERROR:
				return "internal error";
		}
		return "unknown " + error;
	}

	/**
	 * Dispose of asset pack
	 *
	 * @param activity activity
	 * @param packs    packs to delete
	 */
	public static void assetPackRemove(@NonNull final Activity activity, @NonNull final String... packs)
	{
		final AssetPackManager assetPackManager = AssetPackManagerFactory.getInstance(activity);
		for (String pack : packs)
		{
			if (pack != null && !pack.isEmpty())
			{
				assetPackManager.removePack(pack) //
						.addOnCompleteListener(task3 -> Log.d(TAG, "Remove success " + task3.isSuccessful())) //
						.addOnFailureListener(exception -> Log.e(TAG, "Remove failure " + exception.getMessage()));
			}
		}
	}

	/**
	 * Cancel operation
	 *
	 * @param mayInterruptIfRunning may interrupt if running
	 * @return true
	 */
	@Override
	public boolean cancel(final boolean mayInterruptIfRunning)
	{
		Log.d(TAG, "User cancel");
		AssetPackLoader.this.assetPackManager.cancel(Collections.singletonList(this.pack));
		return true;
	}

	/**
	 * Local testing dump
	 *
	 * @param context context
	 */
	static void dumpLocalTesting(@NonNull Context context)
	{
		File[] ds = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT ? context.getExternalFilesDirs(null) : new File[]{context.getExternalFilesDir(null)};
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
						Log.d(TAG, "exists " + f);
					}
				}
			}
		}
	}
}