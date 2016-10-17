package org.sqlunet.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.util.Pair;

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
	private static final String TAG = "StorageUtils"; //$NON-NLS-1$

	/**
	 * Database size
	 */
	private static final float DATABASEMB = 512F;

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
		APP_INTERNAL_POSSIBLY_ADOPTED, APP_EXTERNAL_SECONDARY, APP_EXTERNAL_PRIMARY, PUBLIC_EXTERNAL_SECONDARY, PUBLIC_EXTERNAL_PRIMARY, APP_INTERNAL;

		/**
		 * Compare (sort by preference)
		 *
		 * @param type1 type 1
		 * @param type2 type 2
		 * @return order
		 */
		public static int compare(DirType type1, DirType type2)
		{
			int i1 = type1.ordinal();
			int i2 = type2.ordinal();
			return i1 < i2 ? -1 : (i1 == i2 ? 0 : 1);
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		public String toString()
		{
			switch (this)
			{
				case APP_INTERNAL_POSSIBLY_ADOPTED:
					return "auto"; //$NON-NLS-1$
				case APP_EXTERNAL_SECONDARY:
					return "secondary"; //$NON-NLS-1$
				case APP_EXTERNAL_PRIMARY:
					return "primary"; //$NON-NLS-1$
				case PUBLIC_EXTERNAL_PRIMARY:
					return "public primary"; //$NON-NLS-1$
				case PUBLIC_EXTERNAL_SECONDARY:
					return "public secondary"; //$NON-NLS-1$
				case APP_INTERNAL:
					return "internal"; //$NON-NLS-1$
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
		public final File file;

		public final DirType type;

		public Directory(final File file, final DirType type)
		{
			this.file = file;
			this.type = type;
		}
	}

	/**
	 * Candidate storage
	 *
	 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
	 */
	static public class CandidateStorage implements Comparable<CandidateStorage>
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
		public CandidateStorage(final Directory dir, final float free, final float occupancy, final int status)
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
			return String.format(Locale.ENGLISH, "%s %s %s free", this.dir.type.toString(), this.dir.file.getAbsolutePath(), mbToString(this.free)); //$NON-NLS-1$
		}

		/**
		 * Long string
		 *
		 * @return long string
		 */
		public String toLongString()
		{
			return String.format(Locale.ENGLISH, "%s\n%s\n%s free %.1f%% occupancy\n%s", this.dir.type.toString(), this.dir.file.getAbsolutePath(), mbToString(this.free), this.occupancy, this.status()); //$NON-NLS-1$
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return String.format(Locale.ENGLISH, "%s %s %s %.1f%% %s", this.dir.type.toString(), this.dir.file.getAbsolutePath(), mbToString(this.free), this.occupancy, this.status()); //$NON-NLS-1$
		}

		/**
		 * Equals
		 */
		@Override
		public boolean equals(Object another)
		{
			if(!(another instanceof CandidateStorage))
				return false;
			final CandidateStorage storage2 = (CandidateStorage) another;
			return this.dir.equals(storage2.dir);
		}

		/**
		 * Comparison (most suitable first)
		 *
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(CandidateStorage another)
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
				return "Ok"; //$NON-NLS-1$
			}
			StringBuilder status = new StringBuilder();
			boolean first = true;
			if ((this.status & NULL_DIR) != 0)
			{
				status.append("Is null dir"); //$NON-NLS-1$
				first = false;
			}
			if ((this.status & NOT_MOUNTED) != 0)
			{
				if (!first)
				{
					status.append(" | "); //$NON-NLS-1$
				}
				status.append("Is not mounted"); //$NON-NLS-1$
				first = false;
			}
			if ((this.status & NOT_EXISTS) != 0)
			{
				if (!first)
				{
					status.append(" | "); //$NON-NLS-1$
				}
				status.append("Does not exist"); //$NON-NLS-1$
				first = false;
			}
			if ((this.status & NOT_DIR) != 0)
			{
				if (!first)
				{
					status.append(" | "); //$NON-NLS-1$
				}
				status.append("Is not a dir"); //$NON-NLS-1$
				first = false;
			}
			if ((this.status & NOT_WRITABLE) != 0)
			{
				if (!first)
				{
					status.append(" | "); //$NON-NLS-1$
				}
				status.append("Is not writable"); //$NON-NLS-1$
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

	/**
	 * Whether the dir qualifies as sqlunet storage
	 *
	 * @param dir candidate dir
	 * @return true if it qualifies
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private static int qualifies(final File dir, final DirType type)
	{
		int status = 0;

		// dir
		if (dir == null)
		{
			status |= CandidateStorage.NULL_DIR;
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
						Log.d(StorageUtils.TAG, "storage state of " + dir + ": " + state); //$NON-NLS-1$ //$NON-NLS-2$
						status |= CandidateStorage.NOT_MOUNTED;
					}
				}
				catch (final Throwable e)
				{
					//
				}
				break;
			case APP_INTERNAL:
			case APP_INTERNAL_POSSIBLY_ADOPTED:
				break;
		}

		// exists
		//noinspection ResultOfMethodCallIgnored
		dir.mkdirs();
		if (!dir.exists())
		{
			status |= CandidateStorage.NOT_EXISTS;
		}

		// make sure it is a directory
		if (!dir.isDirectory())
		{
			status |= CandidateStorage.NOT_DIR;
		}

		// make sure it is a directory
		if (!dir.canWrite())
		{
			status |= CandidateStorage.NOT_WRITABLE;
		}

		return status;
	}

	/**
	 * Make list of candidate storages
	 *
	 * @param dirs list of directories
	 * @return list of candidate storages
	 */
	private static List<CandidateStorage> makeCandidateStorages(final Iterable<Directory> dirs)
	{
		final List<CandidateStorage> storages = new ArrayList<>();
		for (final Directory dir : dirs)
		{
			int status = StorageUtils.qualifies(dir.file, dir.type);
			float[] stats = storageStats(dir.file.getAbsolutePath());
			storages.add(new CandidateStorage(dir, stats[STORAGE_FREE], stats[STORAGE_OCCUPANCY], status));
		}
		return storages;
	}

	/**
	 * Get list of candidate storages
	 *
	 * @param context context
	 * @return list of candidate storages
	 */
	private static List<CandidateStorage> getCandidateStorages(final Context context)
	{
		final List<Directory> dirs = getDirectories(context);
		return makeCandidateStorages(dirs);
	}

	/**
	 * Get sorted list of directories
	 *
	 * @param context context
	 * @return list of storage directories (desc-) sorted by size and type
	 */
	public static List<CandidateStorage> getSortedCandidateStorages(final Context context)
	{
		final List<CandidateStorage> candidateStorages = getCandidateStorages(context);
		Collections.sort(candidateStorages);
		return candidateStorages;
	}

	/**
	 * Get list of directories
	 *
	 * @param context context
	 * @return list of storage directories
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private static List<Directory> getDirectories(final Context context)
	{
		final List<Directory> storages = new ArrayList<>();

		// A P P - S P E C I F I C - P O S S I B L Y A D O P T E D

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) //
		{
			storages.add(new Directory(context.getFilesDir(), DirType.APP_INTERNAL_POSSIBLY_ADOPTED));
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
					storages.add(new Directory(dir, DirType.APP_EXTERNAL_SECONDARY));
				}

				// primary storage
				storages.add(new Directory(dirs[0], DirType.APP_EXTERNAL_PRIMARY));
			}
		}
		catch (final Throwable e)
		{
			// application-specific primary external storage
			try
			{
				dir = context.getExternalFilesDir(null);
				storages.add(new Directory(dir, DirType.APP_EXTERNAL_PRIMARY));
			}
			catch (Exception e2)
			{
				//noinspection ErrorNotRethrown
				try
				{
					dir = context.getExternalFilesDir(Storage.SQLUNETDIR);
					storages.add(new Directory(dir, DirType.APP_EXTERNAL_PRIMARY));
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
			storages.add(new Directory(dir, DirType.PUBLIC_EXTERNAL_PRIMARY));
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
					storages.add(new Directory(dir, DirType.PUBLIC_EXTERNAL_PRIMARY));
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
				storages.add(new Directory(dir, DirType.PUBLIC_EXTERNAL_SECONDARY));
			}
		}
		*/
		// @formatter:on

		// I N T E R N A L

		// internal private storage
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) //
		{
			storages.add(new Directory(context.getFilesDir(), DirType.APP_INTERNAL));
		}

		return storages;
	}

	/**
	 * Get external storage directories
	 *
	 * @return map per type of external storage directories
	 */
	static public Map<StorageType, File[]> getStorageDirectories()
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
	private static File discoverPrimaryEmulatedExternalStorage()
	{
		// primary emulated sdcard
		final String emulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET"); //$NON-NLS-1$
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
	private static File discoverPrimaryPhysicalExternalStorage()
	{
		final String externalStorage = System.getenv("EXTERNAL_STORAGE"); //$NON-NLS-1$

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
	private static File[] discoverSecondaryExternalStorage()
	{
		// all secondary sdcards (all except primary) separated by ":"
		String secondaryStoragesEnv = System.getenv("SECONDARY_STORAGE"); //$NON-NLS-1$
		if ((secondaryStoragesEnv == null) || secondaryStoragesEnv.isEmpty())
		{
			secondaryStoragesEnv = System.getenv("EXTERNAL_SDCARD_STORAGE"); //$NON-NLS-1$
		}

		// add all secondary storages
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

	// U S E R I D

	/**
	 * User id
	 *
	 * @return user id
	 */
	private static String getUserId()
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
		{
			return ""; //$NON-NLS-1$
		}

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
		return isDigit ? lastFolder : ""; //$NON-NLS-1$
	}

	// C A P A C I T Y

	/**
	 * Index of free storage value
	 */
	static public final int STORAGE_FREE = 0;

	/**
	 * Index of capacity value
	 */
	static public final int STORAGE_CAPACITY = 1;

	/**
	 * Index of occupancy value
	 */
	static public final int STORAGE_OCCUPANCY = 2;

	/**
	 * Storage data at path
	 *
	 * @param path path
	 * @return data
	 */
	static public float[] storageStats(final String path)
	{
		float[] stats = new float[3];
		stats[STORAGE_FREE] = storageFree(path);
		stats[STORAGE_CAPACITY] = storageCapacity(path);
		stats[STORAGE_OCCUPANCY] = stats[STORAGE_CAPACITY] == 0F ? 0F : 100F * ((stats[STORAGE_CAPACITY] - stats[STORAGE_FREE]) / stats[STORAGE_CAPACITY]);
		return stats;
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
			return "[N/A size]"; //$NON-NLS-1$
		}
		if (mb > 1000F)
		{
			return String.format(Locale.ENGLISH, "%.1f GB", mb / 1024F); //$NON-NLS-1$
		}
		else
		{
			return String.format(Locale.ENGLISH, "%.1f MB", mb); //$NON-NLS-1$
		}
	}

	/**
	 * Get candidate names and values
	 *
	 * @param context context
	 * @return pair of names and values
	 */
	@SuppressWarnings("unused")
	static public Pair<CharSequence[], CharSequence[]> getCandidateNamesValues0(final Context context)
	{
		final List<CharSequence> names = new ArrayList<>();
		final List<CharSequence> values = new ArrayList<>();
		final List<CandidateStorage> candidates = StorageUtils.getSortedCandidateStorages(context);
		for (CandidateStorage candidate : candidates)
		{
			if (candidate.status != 0)
			{
				continue;
			}
			names.add(candidate.toShortString());
			if (candidate.dir.type == DirType.APP_INTERNAL_POSSIBLY_ADOPTED)
			{
				values.add("auto"); //$NON-NLS-1$
			}
			else
			{
				values.add(candidate.dir.file.getAbsolutePath());
			}
		}
		return new Pair<>(names.toArray(new CharSequence[0]), values.toArray(new CharSequence[0]));
	}

	// R E P O R T S

	@SuppressWarnings("unused")
	static public CharSequence reportCandidateStorage(final Context context)
	{
		final StringBuilder sb = new StringBuilder();
		int i = 1;
		final List<CandidateStorage> candidates = StorageUtils.getSortedCandidateStorages(context);
		for (CandidateStorage candidate : candidates)
		{
			sb.append(i++);
			sb.append(' ');
			sb.append('-');
			sb.append(' ');
			sb.append(candidate.toLongString());
			sb.append(' ');
			sb.append(candidate.fitsIn() ? "Fits in" : "Does not fit in"); //$NON-NLS-1$ //$NON-NLS-2$
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
		final Map<StorageType, File[]> storages = StorageUtils.getStorageDirectories();
		final File[] physical = storages.get(StorageType.PRIMARY_PHYSICAL);
		final File[] emulated = storages.get(StorageType.PRIMARY_EMULATED);
		final File[] secondary = storages.get(StorageType.SECONDARY);

		final StringBuilder sb = new StringBuilder();
		if (physical != null)
		{
			sb.append("primary physical:\n"); //$NON-NLS-1$
			for (File f : physical)
			{
				final String s = f.getAbsolutePath();
				sb.append(s);
				sb.append(' ');
				sb.append(mbToString(StorageUtils.storageCapacity(s)));
				sb.append(' ');
				try
				{
					sb.append(Environment.isExternalStorageEmulated(f) ? "emulated" : "not-emulated"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				catch (Throwable e)
				{ //
				}
				sb.append('\n');
			}
		}
		if (emulated != null)
		{
			sb.append("primary emulated:\n"); //$NON-NLS-1$
			for (File f : emulated)
			{
				final String s = f.getAbsolutePath();
				sb.append(s);
				sb.append(' ');
				sb.append(mbToString(StorageUtils.storageCapacity(s)));
				sb.append(' ');
				try
				{
					sb.append(Environment.isExternalStorageEmulated(f) ? "emulated" : "not-emulated"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				catch (Throwable e)
				{ //
				}
				sb.append('\n');
			}
		}
		if (secondary != null)
		{
			sb.append("secondary:\n"); //$NON-NLS-1$
			for (File f : secondary)
			{
				final String s = f.getAbsolutePath();
				sb.append(s);
				sb.append(' ');
				sb.append(mbToString(StorageUtils.storageCapacity(s)));
				sb.append(' ');
				try
				{
					sb.append(Environment.isExternalStorageEmulated(f) ? "emulated" : "not-emulated"); //$NON-NLS-1$ //$NON-NLS-2$
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
