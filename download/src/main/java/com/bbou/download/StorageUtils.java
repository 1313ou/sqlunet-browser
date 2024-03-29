/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * Storage utilities
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StorageUtils
{
	static private final String TAG = "StorageUtils";

	/**
	 * Datapack size
	 */
	static private float DATAPACK_SIZE_MB = Float.NaN;

	/*
	 * Storage types
	 */

	/**
	 * Directory type
	 *
	 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
	 */
	enum DirType
	{
		AUTO, APP_EXTERNAL_SECONDARY, APP_EXTERNAL_PRIMARY, PUBLIC_EXTERNAL_SECONDARY, PUBLIC_EXTERNAL_PRIMARY, APP_INTERNAL;

		/**
		 * Compare (sort by preference)
		 *
		 * @param type1 type 1
		 * @param type2 type 2
		 * @return order
		 */
		static int compare(@NonNull final DirType type1, @NonNull final DirType type2)
		{
			int i1 = type1.ordinal();
			int i2 = type2.ordinal();
			//noinspection UseCompareMethod
			return i1 < i2 ? -1 : (i1 == i2 ? 0 : 1);
		}

		@NonNull
		public String toDisplay()
		{
			switch (this)
			{
				case AUTO:
					return "auto (internal or adopted)";
				case APP_EXTERNAL_SECONDARY:
					return "secondary";
				case APP_EXTERNAL_PRIMARY:
					return "primary";
				case PUBLIC_EXTERNAL_PRIMARY:
					return "public primary";
				case PUBLIC_EXTERNAL_SECONDARY:
					return "public secondary";
				case APP_INTERNAL:
					return "internal";
			}
			throw new IllegalArgumentException(this.toString());
		}
	}

	/**
	 * Directory with type
	 *
	 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
	 */
	static class Directory
	{
		private final File file;

		private final DirType type;

		Directory(final File file, final DirType type)
		{
			this.file = file;
			this.type = type;
		}

		DirType getType()
		{
			return this.type;
		}

		@NonNull
		CharSequence getValue()
		{
			if (DirType.AUTO == this.type)
			{
				return DirType.AUTO.toString();
			}
			return this.file.getAbsolutePath();
		}

		public File getFile()
		{
			return this.file;
		}
	}

	/**
	 * Storage directory
	 *
	 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
	 */
	static class StorageDirectory implements Comparable<StorageDirectory>
	{
		/**
		 * Status flag: null dir
		 */
		static final int NULL_DIR = 0x0001;

		/**
		 * Status flag: storage is not mounted
		 */
		static final int NOT_MOUNTED = 0x0002;

		/**
		 * Status flag: directory does not exist
		 */
		static final int NOT_EXISTS = 0x0004;

		/**
		 * Status flag: not a directory
		 */
		static final int NOT_DIR = 0x0008;

		/**
		 * Status flag: directory is not writable
		 */
		static final int NOT_WRITABLE = 0x0010;

		/**
		 * Directory
		 */
		public final Directory dir;

		/**
		 * Free megabytes
		 */
		final float free;

		/**
		 * Occupancy
		 */
		final float occupancy;

		/**
		 * Status
		 */
		public final int status;

		/**
		 * Constructor
		 *
		 * @param dir       directory
		 * @param free      free megabytes
		 * @param occupancy occupancy percentage
		 * @param status    status
		 */
		StorageDirectory(final Directory dir, final float free, final float occupancy, final int status)
		{
			this.dir = dir;
			this.free = free;
			this.occupancy = occupancy;
			this.status = status;
		}

		/**
		 * Short string
		 *
		 * @return short string
		 */
		@NonNull
		CharSequence toShortString()
		{
			return String.format(Locale.ENGLISH, "%s %s %s free", this.dir.type.toDisplay(), this.dir.file.getAbsolutePath(), mbToString(this.free));
		}

		/**
		 * Long string
		 *
		 * @return long string
		 */
		@NonNull
		String toLongString()
		{
			return String.format(Locale.ENGLISH, "%s\n%s\n%s free %.1f%% occupancy\n%s", this.dir.type.toDisplay(), this.dir.file.getAbsolutePath(), mbToString(this.free), this.occupancy, this.status());
		}

		@NonNull
		@Override
		public String toString()
		{
			return String.format(Locale.ENGLISH, "%s %s %s %.1f%% %s", this.dir.type.toDisplay(), this.dir.file.getAbsolutePath(), mbToString(this.free), this.occupancy, this.status());
		}

		/**
		 * Equals
		 */
		@Override
		public boolean equals(Object another)
		{
			if (!(another instanceof StorageDirectory))
			{
				return false;
			}
			final StorageDirectory storage2 = (StorageDirectory) another;
			return this.dir.equals(storage2.dir);
		}

		/**
		 * Comparison (most suitable first)
		 */
		@Override
		public int compareTo(@NonNull StorageDirectory another)
		{
			if (this.status != another.status)
			{
				return this.status == 0 ? -1 : 1;
			}
			if (this.free != another.free)
			{
				return -Float.compare(this.free, another.free);
			}
			if (this.dir.type != another.dir.type)
			{
				return DirType.compare(this.dir.type, another.dir.type);
			}

			return 0;
		}

		/**
		 * Storage status
		 *
		 * @return status string
		 */
		@SuppressWarnings("WeakerAccess")
		@NonNull
		public CharSequence status()
		{
			if (this.status == 0)
			{
				return "Ok";
			}
			StringBuilder status = new StringBuilder();
			boolean first = true;
			if ((this.status & NULL_DIR) != 0)
			{
				status.append("Is null dir");
				first = false;
			}
			if ((this.status & NOT_MOUNTED) != 0)
			{
				if (!first)
				{
					status.append(" | ");
				}
				status.append("Is not mounted");
				first = false;
			}
			if ((this.status & NOT_EXISTS) != 0)
			{
				if (!first)
				{
					status.append(" | ");
				}
				status.append("Does not exist");
				first = false;
			}
			if ((this.status & NOT_DIR) != 0)
			{
				if (!first)
				{
					status.append(" | ");
				}
				status.append("Is not a dir");
				first = false;
			}
			if ((this.status & NOT_WRITABLE) != 0)
			{
				if (!first)
				{
					status.append(" | ");
				}
				status.append("Is not writable");
			}
			return status;
		}

		/**
		 * Capacity test
		 *
		 * @return true if datapack fits in storage
		 */
		boolean fitsIn(@NonNull final Context context)
		{
			if (Float.isNaN(DATAPACK_SIZE_MB))
			{
				float size = context.getResources().getInteger(R.integer.size_datapack_working_total);
				DATAPACK_SIZE_MB = (size + size / 10F) / (1024F * 1024F);
			}
			return !Float.isNaN(this.free) && this.free >= DATAPACK_SIZE_MB;
		}
	}

	// C O L L E C T

	/**
	 * Get list of directories
	 *
	 * @param context context
	 * @return list of storage directories
	 */
	@SuppressLint("ObsoleteSdkInt")
	@NonNull
	@TargetApi(Build.VERSION_CODES.KITKAT)
	static private List<Directory> getDirectories(@NonNull final Context context)
	{
		final List<Directory> result = new ArrayList<>();
		File dir;

		// A P P - S P E C I F I C - P O S S I B L Y   A D O P T E D

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) //
		{
			dir = context.getFilesDir();
			if (dir != null)
			{
				result.add(new Directory(dir, DirType.AUTO));
			}
		}

		// A P P - S P E C I F I C

		// application-specific secondary external storage or primary external
		final File[] dirs = ContextCompat.getExternalFilesDirs(context, null);
		if (dirs.length > 0)
		{
			// preferably secondary storage (index >= 1)
			for (int i = 1; i < dirs.length; i++)
			{
				dir = dirs[i];
				if (dir != null)
				{
					result.add(new Directory(dir, DirType.APP_EXTERNAL_SECONDARY));
				}
			}

			// primary storage (index == 0)
			dir = dirs[0];
			if (dir != null)
			{
				result.add(new Directory(dir, DirType.APP_EXTERNAL_PRIMARY));
			}
		}

		// P U B L I C

		// top-level public external storage directory
		//		try
		//		{
		//			dir = Environment.getExternalStoragePublicDirectory(Settings.STORAGE_DB_DIR);
		//			result.add(new Directory(dir, DirType.PUBLIC_EXTERNAL_PRIMARY));
		//		}
		//		catch (@NonNull final Throwable e)
		//		{
		//			// top-level public in external
		//			try
		//			{
		//				final File storage = Environment.getExternalStorageDirectory();
		//				if (storage != null)
		//				{
		//					dir = new File(storage, Settings.STORAGE_DB_DIR);
		//					result.add(new Directory(dir, DirType.PUBLIC_EXTERNAL_PRIMARY));
		//				}
		//			}
		//			catch (@NonNull final Throwable e2)
		//			{
		//				//
		//			}
		//		}

		// I N T E R N A L

		// internal private storage
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) //
		{
			dir = context.getFilesDir();
			if (dir != null)
			{
				result.add(new Directory(dir, DirType.APP_INTERNAL));
			}
		}

		return result;
	}

	/**
	 * Make list of storage directories from list of directories
	 *
	 * @param dirs list of directories
	 * @return list of storage storages
	 */
	@NonNull
	static private List<StorageDirectory> directories2StorageDirectories(@NonNull final Iterable<Directory> dirs)
	{
		final List<StorageDirectory> storages = new ArrayList<>();
		for (final Directory dir : dirs)
		{
			// make path
			// true if and only if the directory was created
			final boolean wasCreated = dir.file.mkdirs();

			// status and size
			float[] stats = storageStats(dir.file.getAbsolutePath());
			int status = StorageUtils.qualifies(dir.file, dir.type);
			storages.add(new StorageDirectory(dir, stats[STORAGE_FREE], stats[STORAGE_OCCUPANCY], status));

			// restore
			if (wasCreated && dir.file.exists())
			{
				//noinspection ResultOfMethodCallIgnored
				dir.file.delete();
			}
		}
		return storages;
	}

	/**
	 * Get list of storage storages
	 *
	 * @param context context
	 * @return list of storage storages
	 */
	@NonNull
	static private List<StorageDirectory> getStorageDirectories(@NonNull final Context context)
	{
		final List<Directory> dirs = getDirectories(context);
		return directories2StorageDirectories(dirs);
	}

	/**
	 * Get sorted list of storage directories
	 *
	 * @param context context
	 * @return list of storage directories (desc-) sorted by size and type
	 */
	@NonNull
	static List<StorageDirectory> getSortedStorageDirectories(@NonNull final Context context)
	{
		final List<StorageDirectory> storageDirectories = getStorageDirectories(context);
		Collections.sort(storageDirectories);
		return storageDirectories;
	}

	/**
	 * Whether the dir qualifies as storage
	 *
	 * @param dir directory
	 * @return code if it qualifies
	 */
	@SuppressLint("ObsoleteSdkInt")
	static private int qualifies(@Nullable final File dir, @NonNull final DirType type)
	{
		int status = 0;

		// dir
		if (dir == null)
		{
			status |= StorageDirectory.NULL_DIR;
			return status;
		}

		// state
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			switch (type)
			{
				case APP_EXTERNAL_SECONDARY:
				case APP_EXTERNAL_PRIMARY:
				case PUBLIC_EXTERNAL_PRIMARY:
				case PUBLIC_EXTERNAL_SECONDARY:
					try
					{
						final String state = Environment.getExternalStorageState(dir);
						if (!Environment.MEDIA_MOUNTED.equals(state))
						{
							Log.d(TAG, "Storage state of " + dir + ": " + state);
							status |= StorageDirectory.NOT_MOUNTED;
						}
					}
					catch (@NonNull final Throwable e)
					{
						//
					}
					break;
				case APP_INTERNAL:
				case AUTO:
					break;
			}
		}

		// exists
		if (!dir.exists())
		{
			status |= StorageDirectory.NOT_EXISTS;
		}

		// make sure it is a directory
		if (!dir.isDirectory())
		{
			status |= StorageDirectory.NOT_DIR;
		}

		// make sure it is a directory
		if (!dir.canWrite())
		{
			status |= StorageDirectory.NOT_WRITABLE;
		}

		return status;
	}

	// D I S C O V E R  S T O R A G E

	/*
	 * Get external storage
	 *
	 * @param context context
	 * @return map per type of external storage
	 */

	/*
	 * Select external storage
	 *
	 * @param context context
	 * @return external storage directory
	 */

	/*
	 * Discover primary emulated external storage directory
	 *
	 * @param context context
	 * @return primary emulated external storage directory
	 */

	/*
	 * Discover primary physical external storage directory
	 *
	 * @return primary physical external storage directory
	 */

	/*
	 * Discover secondary external storage directories
	 *
	 * @return secondary external storage directories
	 */
	//	@Nullable
	//	static private File[] discoverSecondaryExternalStorage()
	//	{
	//		// all secondary sdcards (all except primary) separated by ":"
	//		String secondaryStoragesEnv = System.getenv("SECONDARY_STORAGE");
	//		if ((secondaryStoragesEnv == null) || secondaryStoragesEnv.isEmpty())
	//		{
	//			secondaryStoragesEnv = System.getenv("EXTERNAL_SDCARD_STORAGE");
	//		}
	//
	//		// addItem all secondary storages
	//		if (secondaryStoragesEnv != null && !secondaryStoragesEnv.isEmpty())
	//		{
	//			// all secondary sdcards split into array
	//			final String[] paths = secondaryStoragesEnv.split(File.pathSeparator);
	//			final List<File> dirs = new ArrayList<>();
	//			for (String path : paths)
	//			{
	//				final File dir = new File(path);
	//				if (dir.exists())
	//				{
	//					dirs.add(dir);
	//				}
	//			}
	//			return dirs.toArray(new File[0]);
	//		}
	//		return null;
	//	}

	// Q U A L I F I E S

	// U S E R I D

	/*
	 * User id
	 *
	 * @param context context
	 * @return user id
	 */
	//	@NonNull
	//	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	//	static private String getUserId(@NonNull final Context context)
	//	{
	//		final UserManager manager = (UserManager) context.getSystemService(Context.USER_SERVICE);
	//		if (null != manager)
	//		{
	//			UserHandle user = android.os.Process.myUserHandle();
	//			long userSerialNumber = manager.getSerialNumberForUser(user);
	//			Log.d("USER", "userSerialNumber = " + userSerialNumber);
	//			return Long.toString(userSerialNumber);
	//		}
	//		return "";
	//	}

	// C A P A C I T Y

	// C A P A C I T Y

	/**
	 * Index of free storage value
	 */
	static private final int STORAGE_FREE = 0;

	/**
	 * Index of capacity value
	 */
	static private final int STORAGE_CAPACITY = 1;

	/**
	 * Index of occupancy value
	 */
	static private final int STORAGE_OCCUPANCY = 2;

	/**
	 * Storage data at path
	 *
	 * @param path path
	 * @return data
	 */
	@NonNull
	static private float[] storageStats(final String path)
	{
		float[] stats = new float[3];
		stats[STORAGE_FREE] = storageFree(path);
		stats[STORAGE_CAPACITY] = storageCapacity(path);
		stats[STORAGE_OCCUPANCY] = stats[STORAGE_CAPACITY] == 0F ? 0F : 100F * ((stats[STORAGE_CAPACITY] - stats[STORAGE_FREE]) / stats[STORAGE_CAPACITY]);
		return stats;
	}

	@NonNull
	static public String getFree(@NonNull final Context context, @NonNull final String target)
	{
		final File file = new File(target);
		final String dir = file.isDirectory() ? file.getAbsolutePath() : file.getParent();
		final float[] dataStats = StorageUtils.storageStats(dir);
		final float df = dataStats[StorageUtils.STORAGE_FREE];
		final float dc = dataStats[StorageUtils.STORAGE_CAPACITY];
		final float dp = dataStats[StorageUtils.STORAGE_OCCUPANCY];
		return context.getString(R.string.format_storage_data, dir, StorageUtils.mbToString(df), StorageUtils.mbToString(dc), dp);
	}

	/**
	 * Free storage at path
	 *
	 * @param path path
	 * @return free storage in megabytes
	 */
	@SuppressLint("ObsoleteSdkInt")
	static private float storageFree(final String path)
	{
		try
		{
			final StatFs stat = new StatFs(path);
			float bytes;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
			{
				bytes = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
			}
			else
			{
				//noinspection deprecation
				bytes = stat.getAvailableBlocks() * stat.getBlockSize();
			}
			return bytes / (1024.f * 1024.f);
		}
		catch (Throwable e)
		{
			return Float.NaN;
		}
	}

	/**
	 * Free space for dir
	 *
	 * @param dir dir
	 * @return free space as string
	 */
	@NonNull
	static CharSequence storageFreeAsString(@NonNull final File dir)
	{
		return storageFreeAsString(dir.getAbsolutePath());
	}

	/**
	 * Free space for dir
	 *
	 * @param dir dir
	 * @return free space as string
	 */
	@NonNull
	static CharSequence storageFreeAsString(final String dir)
	{
		return StorageUtils.mbToString(storageFree(dir));
	}

	/**
	 * Storage capacity at path
	 *
	 * @param path path
	 * @return storage capacity in megabytes
	 */
	@SuppressLint("ObsoleteSdkInt")
	@SuppressWarnings({"WeakerAccess"})
	static public float storageCapacity(final String path)
	{
		try
		{
			final StatFs stat = new StatFs(path);
			final float bytes;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
			{
				bytes = stat.getBlockCountLong() * stat.getBlockSizeLong();
			}
			else
			{
				//noinspection deprecation
				bytes = stat.getBlockCount() * stat.getBlockSize();
			}
			return bytes / (1024.f * 1024.f);
		}
		catch (Throwable e)
		{
			return Float.NaN;
		}
	}

	// H U M A N - R E A D A B L E   B Y T E   C O U N T

	/**
	 * Megabytes to string
	 *
	 * @param mb megabytes
	 * @return string
	 */
	@NonNull
	private static String mbToString(final float mb)
	{
		if (Float.isNaN(mb))
		{
			return "[N/A size]";
		}
		if (mb > 1000F)
		{
			return String.format(Locale.ENGLISH, "%.1f GB", mb / 1024F);
		}
		else
		{
			return String.format(Locale.ENGLISH, "%.1f MB", mb);
		}
	}

	static private final String[] UNITS = {"B", "KB", "MB", "GB"};

	/**
	 * Byte count to string
	 *
	 * @param count byte count
	 * @return string
	 */
	@NonNull
	static public String countToStorageString(final long count)
	{
		if (count > 0)
		{
			float unit = 1024F * 1024F * 1024F;
			for (int i = 3; i >= 0; i--)
			{
				if (count >= unit)
				{
					return String.format(Locale.ENGLISH, "%.1f %s", count / unit, UNITS[i]);
				}

				unit /= 1024;
			}
		}
		else if (count == 0)
		{
			return "0 Byte";
		}
		return "[n/a]";
	}
}
