/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

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
	public final String name;

	public final long date;

	public final long size;

	public final String etag;

	public final String version;

	public final String staticVersion;

	public FileData(final String name, final long date, final long size, final String etag, final String version, final String staticVersion)
	{
		this.name = name;
		this.date = date;
		this.size = size;
		this.etag = etag;
		this.version = version;
		this.staticVersion = staticVersion;
	}

	@SuppressWarnings("WeakerAccess")
	@Nullable
	static public FileData makeFileDataFrom(@Nullable final File file)
	{
		if (file != null && file.exists())
		{
			return new FileData(file.getName(), file.lastModified(), file.length(), null, null, null);
		}
		return null;
	}

	@NonNull
	static public FileData getCurrent(@NonNull final Context context)
	{
		final String name = Settings.getDatapackName(context);
		final long date = Settings.getDatapackDate(context);
		final long size = Settings.getDatapackSize(context);
		final String etag = Settings.getDatapackSourceEtag(context);
		final String version = Settings.getDatapackSourceVersion(context);
		final String staticVersion = Settings.getDatapackSourceStaticVersion(context);
		return new FileData(name, date, size, etag, version, staticVersion);
	}

	@Nullable
	public Long getSize()
	{
		return this.size == -1 ? null : this.size;
	}

	@Nullable
	public Date getDate()
	{
		return this.date == -1 || this.date == 0 ? null : new Date(this.date);
	}

	@Nullable
	public String getEtag()
	{
		return this.etag;
	}

	@Nullable
	public String getVersion()
	{
		return this.version;
	}

	@Nullable
	public String getStaticVersion()
	{
		return this.staticVersion;
	}

	public static void recordDatapackFile(@NonNull final Context context, @NonNull final File datapackFile)
	{
		Settings.setDatapackName(context, datapackFile.getName());
		final FileData fileData = makeFileDataFrom(datapackFile);
		if (fileData != null)
		{
			if (fileData.date != -1)
			{
				Settings.setDatapackDate(context, fileData.date);
			}
			if (fileData.size != -1)
			{
				Settings.setDatapackSize(context, fileData.size);
			}
		}
	}
}
