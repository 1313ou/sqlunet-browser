package org.sqlunet.browser.config;

import android.content.Context;

import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageSettings;

import java.io.File;
import java.util.Date;

/**
 * File data
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FileData
{
	public static final Object PREF_DB_DATE = Settings.PREF_DB_DATE;

	public static final Object PREF_DB_SIZE = Settings.PREF_DB_SIZE;

	private final long size;

	private final long date;

	public FileData(final long date, final long size)
	{
		this.size = size;
		this.date = date;
	}

	public FileData(final File file)
	{
		if (file.exists())
		{
			this.size = file.length();
			this.date = file.lastModified();
		}
		else
		{
			this.size = -1;
			this.date = -1;
		}
	}

	public Long getSize()
	{
		return this.size == -1 ? null : this.size;
	}

	public Date getDate()
	{
		return this.date == -1 ? null : new Date(this.date);
	}

	public static void registerDb(final Context context)
	{
		final File file = new File(StorageSettings.getDatabasePath(context));
		final FileData fileData = new FileData(file);
		if (fileData.date != -1)
		{
			Settings.setDbDate(context, fileData.date);
		}
		if (fileData.size != -1)
		{
			Settings.setDbSize(context, fileData.size);
		}
	}
}
