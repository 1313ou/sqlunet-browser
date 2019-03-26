package org.sqlunet.download;

import android.content.Context;

import java.io.File;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

	@SuppressWarnings("WeakerAccess")
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
		final long date = Settings.getDbDate(context);
		final long size = Settings.getDbSize(context);
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

	public static void recordDatabase(@NonNull final Context context, final File databaseFile)
	{
		final FileData fileData = makeFileDataFrom(databaseFile);
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
