/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

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
	private final String name;

	private final long date;

	private final long size;

	public FileData(final String name, final long date, final long size)
	{
		this.name = name;
		this.date = date;
		this.size = size;
	}

	@SuppressWarnings("WeakerAccess")
	@Nullable
	static public FileData makeFileDataFrom(@Nullable final File file)
	{
		if (file != null && file.exists())
		{
			return new FileData(file.getName(), file.lastModified(), file.length());
		}
		return null;
	}

	@Nullable
	static public FileData getCurrent(final Context context)
	{
		final String name = Settings.getDbName(context);
		final long date = Settings.getDbDate(context);
		final long size = Settings.getDbSize(context);
		if (date != -1)
		{
			return new FileData(name, date, size);
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
			if (fileData.name != null)
			{
				Settings.setDbName(context, fileData.name);
			}
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

	public static void unrecordDatabase(@NonNull final Context context)
	{
		Settings.unrecordDb(context);
	}
}
