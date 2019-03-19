package org.sqlunet.browser.config;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
	private final long date;

	private final long size;

	public FileData(final long date, final long size)
	{
		this.date = date;
		this.size = size;
	}

	@Nullable
	static public FileData makeFileDataFrom(@Nullable final File file)
	{
		if (file != null && file.exists())
		{
			return new FileData(file.lastModified(), file.length());
		}
		return null;
	}

	static public FileData getCurrent(final Context context)
	{
		long date = Settings.getDbDate(context);
		long size = Settings.getDbSize(context);
		if (date != -1)
		{
			return new FileData(date, size);
		}
		return null;
	}

	@Nullable
	public Long getSize()
	{
		return this.size == -1 ? null : this.size;
	}

	@Nullable
	public Date getDate()
	{
		return this.date == -1 ? null : new Date(this.date);
	}

	public static void recordDb(@NonNull final Context context)
	{
		final File file = new File(StorageSettings.getDatabasePath(context));
		final FileData fileData = makeFileDataFrom(file);
		if (fileData != null)
		{
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
}
