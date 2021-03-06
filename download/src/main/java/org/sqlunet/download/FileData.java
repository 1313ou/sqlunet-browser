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
		final String name = Settings.getDbName(context);
		final long date = Settings.getDbDate(context);
		final long size = Settings.getDbSize(context);
		final String etag = Settings.getDbSourceEtag(context);
		final String version = Settings.getDbSourceVersion(context);
		final String staticVersion = Settings.getDbSourceStaticVersion(context);
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
}
