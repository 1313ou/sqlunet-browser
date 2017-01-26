package org.sqlunet.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import org.sqlunet.xnet.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Storage utilities
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StorageUtils
{
	static private final String TAG = "StorageUtils";

	/**
	 * Database size
	 */
	static private final float DATABASEMB = 512F;

	/**
	 * Storage types
	 */
	public enum StorageType
	{
		PRIMARY_EMULATED, PRIMARY_PHYSICAL, SECONDARY
	}

	/**
	 * Directory type
	 *
	 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
	 */
	public enum DirType
	{
		AUTO, APP_EXTERNAL_SECONDARY, APP_EXTERNAL_PRIMARY, PUBLIC_EXTERNAL_SECONDARY, PUBLIC_EXTERNAL_PRIMARY, APP_INTERNAL;

		/**
		 * Compare (sort by preference)
		 *
		 * @param type1 type 1
		 * @param type2 type 2
		 * @return order
		 */
		public static int compare(final DirType type1, final DirType type2)
		{
			int i1 = type1.ordinal();
			int i2 = type2.ordinal();
			return i1 < i2 ? -1 : (i1 == i2 ? 0 : 1);
		}

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
			return null;
		}
	}

	/**
	 * Directory with type
	 *
	 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
	 */
	static public class Directory
	{
		private final File file;

		private final DirType type;

		public Directory(final File file, final DirType type)
		{
			this.file = file;
			this.type = type;
		}

		public final DirType getType()
		{
			return this.type;
		}

		public CharSequence getValue()
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
	static public class StorageDirectory implements Comparable<StorageDirectory>
	{
		/**
		 * Status flag: null dir
		 */
		static public final int NULL_DIR = 0x0001;

		/**
		 * Status flag: storage is not mounted
		 */
		static public final int NOT_MOUNTED = 0x0002;

		/**
		 * Status flag: directory does not exist
		 */
		static public final int NOT_EXISTS = 0x0004;

		/**
		 * Status flag: not a directory
		 */
		static public final int NOT_DIR = 0x0008;

		/**
		 * Status flag: directory is not writable
		 */
		static public final int NOT_WRITABLE = 0x0010;

		/**
		 * Directory
		 */
		public final Directory dir;

		/**
		 * Free megabytes
		 */
		public final float free;

		/**
		 * Occupancy
		 */
		public final float occupancy;

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
		public StorageDirectory(final Directory dir, final float free, final float occupancy, final int status)
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
		public CharSequence toShortString()
		{
			return String.format(Locale.ENGLISH, "%s %s %s free", this.dir.type.toDisplay(), this.dir.file.getAbsolutePath(), mbToString(this.free));
		}

		/**
		 * Long string
		 *
		 * @return long string
		 */
		public String toLongString()
		{
			return String.format(Locale.ENGLISH, "%s\n%s\n%s free %.1f%% occupancy\n%s", this.dir.type.toDisplay(), this.dir.file.getAbsolutePath(), mbToString(this.free), this.occupancy, this.status());
		}

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
		 *
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
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
		 * @return true if database fits in storage
		 */
		public boolean fitsIn()
		{
			return !Float.isNaN(this.free) && this.free >= DATABASEMB;
		}
	}

	// C O L L E C T

	/**
	 * Get list of directories
	 *
	 * @param context context
	 * @return list of storage directories
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	static private List<Directory> getDirectories(final Context context)
	{
		final List<Directory> result = new ArrayList<>();

		// A P P - S P E C I F I C - P O S S I B L Y   A D O P T E D

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) //
		{
			result.add(new Directory(context.getFilesDir(), DirType.AUTO));
		}

		// A P P - S P E C I F I C

		// application-specific secondary external storage or primary external (KITKAT)
		File dir;
		try
		{
			final File[] dirs = context.getExternalFilesDirs(null);
			if (dirs != null && dirs.length > 0)
			{
				// preferably secondary storage
				for (int i = 1; i < dirs.length; i++)
				{
					dir = dirs[i];
					if (dir != null)
					{
						result.add(new Directory(dir, DirType.APP_EXTERNAL_SECONDARY));
					}
				}

				// primary storage
				dir = dirs[0];
				if (dir != null)
				{
					result.add(new Directory(dir, DirType.APP_EXTERNAL_PRIMARY));
				}
			}
		}
		catch (final Throwable e)
		{
			// application-specific primary external storage
			try
			{
				dir = context.getExternalFilesDir(null);
				result.add(new Directory(dir, DirType.APP_EXTERNAL_PRIMARY));
			}
			catch (Exception e2)
			{
				//noinspection ErrorNotRethrown
				try
				{
					dir = context.getExternalFilesDir(Storage.SQLUNETDIR);
					result.add(new Directory(dir, DirType.APP_EXTERNAL_PRIMARY));
				}
				catch (final NoSuchFieldError e3)
				{
					//
				}
			}
		}

		// P U B L I C

		// top-level public external storage directory
		try
		{
			dir = Environment.getExternalStoragePublicDirectory(Storage.SQLUNETDIR);
			result.add(new Directory(dir, DirType.PUBLIC_EXTERNAL_PRIMARY));
		}
		catch (final Throwable e)
		{
			// top-level public in external
			try
			{
				final File storage = Environment.getExternalStorageDirectory();
				if (storage != null)
				{
					dir = new File(storage, Storage.SQLUNETDIR);
					result.add(new Directory(dir, DirType.PUBLIC_EXTERNAL_PRIMARY));
				}
			}
			catch (final Throwable e2)
			{
				//
			}
		}

		// top-level public external secondary storage directory: not accessible to apps

		// @formatter:off
		/*
		final File[] secondaryStorages = discoverSecondaryExternalStorage();
		if (secondaryStorages != null)
		{
			for (File secondaryStorage : secondaryStorages)
			{
				dir = new File(secondaryStorage, Storage.SQLUNETDIR);
				storages.addItem(new Directory(dir, DirType.PUBLIC_EXTERNAL_SECONDARY));
			}
		}
		*/
		// @formatter:on

		// I N T E R N A L

		// internal private storage
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) //
		{
			result.add(new Directory(context.getFilesDir(), DirType.APP_INTERNAL));
		}

		return result;
	}

	/**
	 * Make list of storage directories from list of directories
	 *
	 * @param dirs list of directories
	 * @return list of storage storages
	 */
	static private List<StorageDirectory> directories2StorageDirectories(final Iterable<Directory> dirs)
	{
		final List<StorageDirectory> storages = new ArrayList<>();
		for (final Directory dir : dirs)
		{
			int status = StorageUtils.qualifies(dir.file, dir.type);
			float[] stats = storageStats(dir.file.getAbsolutePath());
			storages.add(new StorageDirectory(dir, stats[STORAGE_FREE], stats[STORAGE_OCCUPANCY], status));
		}
		return storages;
	}

	/**
	 * Get list of storage storages
	 *
	 * @param context context
	 * @return list of storage storages
	 */
	static private List<StorageDirectory> getStorageDirectories(final Context context)
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
	static public List<StorageDirectory> getSortedStorageDirectories(final Context context)
	{
		final List<StorageDirectory> storageDirectories = getStorageDirectories(context);
		Collections.sort(storageDirectories);
		return storageDirectories;
	}

	// D I S C O V E R  S T O R A G E

	/**
	 * Get external storage
	 *
	 * @return map per type of external storage
	 */
	static public Map<StorageType, File[]> getExternalStorages()
	{
		// result set of paths
		final Map<StorageType, File[]> dirs = new EnumMap<>(StorageType.class);

		// P R I M A R Y

		// primary emulated
		final File primaryEmulated = discoverPrimaryEmulatedExternalStorage();
		if (primaryEmulated != null)
		{
			dirs.put(StorageType.PRIMARY_EMULATED, new File[]{primaryEmulated});
		}

		// primary emulated
		final File physicalEmulated = discoverPrimaryPhysicalExternalStorage();
		if (physicalEmulated != null)
		{
			dirs.put(StorageType.PRIMARY_PHYSICAL, new File[]{physicalEmulated});
		}

		// S E C O N D A R Y

		final File[] secondaryStorages = discoverSecondaryExternalStorage();
		if (secondaryStorages != null && secondaryStorages.length > 0)
		{
			dirs.put(StorageType.SECONDARY, secondaryStorages);
		}

		return dirs;
	}

	/**
	 * Select external storage
	 *
	 * @return external storage directory
	 */
	@SuppressWarnings("unused")
	static public String selectExternalStorage()
	{
		// S E C O N D A R Y

		// all secondary sdcards split into array
		final File[] secondaries = discoverSecondaryExternalStorage();
		if (secondaries != null && secondaries.length > 0)
		{
			return secondaries[0].getAbsolutePath();
		}

		// P R I M A R Y

		// primary emulated sdcard
		final File primaryEmulated = discoverPrimaryEmulatedExternalStorage();
		if (primaryEmulated != null)
		{
			return primaryEmulated.getAbsolutePath();
		}

		final File primaryPhysical = discoverPrimaryPhysicalExternalStorage();
		if (primaryPhysical != null)
		{
			return primaryPhysical.getAbsolutePath();
		}

		return null;
	}

	/**
	 * Discover primary emulated external storage directory
	 *
	 * @return primary emulated external storage directory
	 */
	static private File discoverPrimaryEmulatedExternalStorage()
	{
		// primary emulated sdcard
		final String emulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
		if (emulatedStorageTarget != null && !emulatedStorageTarget.isEmpty())
		{
			// device has emulated extStorage
			// external extStorage paths should have userId burned into them
			final String userId = StorageUtils.getUserId();

			// /extStorage/emulated/0[1,2,...]
			if (userId == null || userId.isEmpty())
			{
				return new File(emulatedStorageTarget);
			}
			else
			{
				return new File(emulatedStorageTarget + File.separatorChar + userId);
			}
		}
		return null;
	}

	/**
	 * Discover primary physical external storage directory
	 *
	 * @return primary physical external storage directory
	 */
	static private File discoverPrimaryPhysicalExternalStorage()
	{
		final String externalStorage = System.getenv("EXTERNAL_STORAGE");
		// device has physical external extStorage; use plain paths.
		if (externalStorage != null && !externalStorage.isEmpty())
		{
			return new File(externalStorage);
		}

		return null;
	}

	/**
	 * Discover secondary external storage directories
	 *
	 * @return secondary external storage directories
	 */
	static private File[] discoverSecondaryExternalStorage()
	{
		// all secondary sdcards (all except primary) separated by ":"
		String secondaryStoragesEnv = System.getenv("SECONDARY_STORAGE");
		if ((secondaryStoragesEnv == null) || secondaryStoragesEnv.isEmpty())
		{
			secondaryStoragesEnv = System.getenv("EXTERNAL_SDCARD_STORAGE");
		}

		// addItem all secondary storages
		if (secondaryStoragesEnv != null && !secondaryStoragesEnv.isEmpty())
		{
			// all secondary sdcards split into array
			final String[] paths = secondaryStoragesEnv.split(File.pathSeparator);
			final List<File> dirs = new ArrayList<>();
			for (String path : paths)
			{
				final File dir = new File(path);
				if (dir.exists())
				{
					dirs.add(dir);
				}
			}
			return dirs.toArray(new File[0]);
		}
		return null;
	}

	// Q U A L I F I E S

	/**
	 * Whether the dir qualifies as sqlunet storage
	 *
	 * @param dir directory
	 * @return true if it qualifies
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	static private int qualifies(final File dir, final DirType type)
	{
		int status = 0;

		// dir
		if (dir == null)
		{
			status |= StorageDirectory.NULL_DIR;
			return status;
		}

		// state
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
						Log.d(StorageUtils.TAG, "storage state of " + dir + ": " + state);
						status |= StorageDirectory.NOT_MOUNTED;
					}
				}
				catch (final Throwable e)
				{
					//
				}
				break;
			case APP_INTERNAL:
			case AUTO:
				break;
		}

		// make path
		// true if and only if the directory was created
		final boolean wasCreated = dir.mkdirs();

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

		// restore
		if (wasCreated && dir.exists())
		{
			//noinspection ResultOfMethodCallIgnored
			dir.delete();
		}

		return status;
	}

	// U S E R I D

	/**
	 * User id
	 *
	 * @return user id
	 */
	static private String getUserId()
	{
		final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		final String[] folders = path.split(File.separator);
		final String lastFolder = folders[folders.length - 1];
		boolean isDigit = false;
		try
		{
			//noinspection ResultOfMethodCallIgnored
			Integer.valueOf(lastFolder);
			isDigit = true;
		}
		catch (final NumberFormatException ignored)
		{
			//
		}
		return isDigit ? lastFolder : "";
	}

	// C A P A C I T Y

	/**
	 * Index of free storage value
	 */
	private static final int STORAGE_FREE = 0;

	/**
	 * Index of capacity value
	 */
	private static final int STORAGE_CAPACITY = 1;

	/**
	 * Index of occupancy value
	 */
	private static final int STORAGE_OCCUPANCY = 2;

	/**
	 * Storage data at path
	 *
	 * @param path path
	 * @return data
	 */
	private static float[] storageStats(final String path)
	{
		float[] stats = new float[3];
		stats[STORAGE_FREE] = storageFree(path);
		stats[STORAGE_CAPACITY] = storageCapacity(path);
		stats[STORAGE_OCCUPANCY] = stats[STORAGE_CAPACITY] == 0F ? 0F : 100F * ((stats[STORAGE_CAPACITY] - stats[STORAGE_FREE]) / stats[STORAGE_CAPACITY]);
		return stats;
	}

	static public String getFree(final Context context, final String target)
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
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private static float storageFree(final String path)
	{
		try
		{
			final StatFs stat = new StatFs(path);
			final float bytes = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
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
	static public CharSequence storageFreeAsString(final File dir)
	{
		return storageFreeAsString(dir.getAbsolutePath());
	}

	/**
	 * Free space for dir
	 *
	 * @param dir dir
	 * @return free space as string
	 */
	static public CharSequence storageFreeAsString(final String dir)
	{
		return StorageUtils.mbToString(storageFree(dir));
	}

	/**
	 * Storage capacity at path
	 *
	 * @param path path
	 * @return storage capacity in megabytes
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	static public float storageCapacity(final String path)
	{
		try
		{
			final StatFs stat = new StatFs(path);
			final float bytes = stat.getBlockCountLong() * stat.getBlockSizeLong();
			return bytes / (1024.f * 1024.f);
		}
		catch (Throwable e)
		{
			return Float.NaN;
		}
	}

	/**
	 * Megabytes to string
	 *
	 * @param mb megabytes
	 * @return string
	 */
	static public String mbToString(final float mb)
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

	/**
	 * Get storage dirs names and values
	 *
	 * @param context context
	 * @return pair of names and values
	 */
	@SuppressWarnings("unused")
	static public Pair<CharSequence[], CharSequence[]> getStorageDirectoriesNamesValues(final Context context)
	{
		final List<CharSequence> names = new ArrayList<>();
		final List<CharSequence> values = new ArrayList<>();
		final List<StorageDirectory> dirs = StorageUtils.getSortedStorageDirectories(context);
		for (StorageDirectory dir : dirs)
		{
			if (dir.status != 0)
			{
				continue;
			}
			// name
			names.add(dir.toShortString());

			// value
			values.add(dir.dir.getValue());
		}
		return new Pair<>(names.toArray(new CharSequence[0]), values.toArray(new CharSequence[0]));
	}

	// R E P O R T S

	static public CharSequence reportStorageDirectories(final Context context)
	{
		final StringBuilder sb = new StringBuilder();
		int i = 1;
		final List<StorageDirectory> dirs = StorageUtils.getSortedStorageDirectories(context);
		for (StorageDirectory dir : dirs)
		{
			sb.append(i++);
			sb.append(' ');
			sb.append('-');
			sb.append(' ');
			sb.append(dir.toLongString());
			sb.append(' ');
			sb.append(dir.fitsIn() ? "Fits in" : "Does not fit in");
			sb.append('\n');
			sb.append('\n');
		}
		return sb;
	}

	/**
	 * Report on external storage
	 *
	 * @return report
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	static public CharSequence reportExternalStorage()
	{
		final Map<StorageType, File[]> storages = StorageUtils.getExternalStorages();
		final File[] physical = storages.get(StorageType.PRIMARY_PHYSICAL);
		final File[] emulated = storages.get(StorageType.PRIMARY_EMULATED);
		final File[] secondary = storages.get(StorageType.SECONDARY);

		final StringBuilder sb = new StringBuilder();
		if (physical != null)
		{
			sb.append("primary physical:\n");
			for (File f : physical)
			{
				final String s = f.getAbsolutePath();
				sb.append(s);
				sb.append(' ');
				sb.append(mbToString(StorageUtils.storageCapacity(s)));
				sb.append(' ');
				try
				{
					sb.append(Environment.isExternalStorageEmulated(f) ? "emulated" : "not-emulated");
				}
				catch (Throwable e)
				{ //
				}
				sb.append('\n');
			}
		}
		if (emulated != null)
		{
			sb.append("primary emulated:\n");
			for (File f : emulated)
			{
				final String s = f.getAbsolutePath();
				sb.append(s);
				sb.append(' ');
				sb.append(mbToString(StorageUtils.storageCapacity(s)));
				sb.append(' ');
				try
				{
					sb.append(Environment.isExternalStorageEmulated(f) ? "emulated" : "not-emulated");
				}
				catch (Throwable e)
				{ //
				}
				sb.append('\n');
			}
		}
		if (secondary != null)
		{
			sb.append("secondary:\n");
			for (File f : secondary)
			{
				final String s = f.getAbsolutePath();
				sb.append(s);
				sb.append(' ');
				sb.append(mbToString(StorageUtils.storageCapacity(s)));
				sb.append(' ');
				try
				{
					sb.append(Environment.isExternalStorageEmulated(f) ? "emulated" : "not-emulated");
				}
				catch (Throwable e)
				{ //
				}
				sb.append('\n');
			}
		}
		return sb.toString();
	}
}
