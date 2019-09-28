/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.sql;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Prepared statement
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Utils
{
	/**
	 * Join ids into string
	 *
	 * @param ids ids
	 * @return joined ids
	 */
	@Nullable
	@SuppressWarnings("unused")
	static public String join(@Nullable final int... ids)
	{
		if (ids == null)
		{
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final int id : ids)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				sb.append(',');
			}
			sb.append(id);
		}
		return sb.toString();
	}

	/**
	 * Join ids into string
	 *
	 * @param ids ids
	 * @return joined ids
	 */
	@Nullable
	@SuppressWarnings("unused")
	static public String join(@Nullable final long... ids)
	{
		if (ids == null)
		{
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final long id : ids)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				sb.append(',');
			}
			sb.append(id);
		}
		return sb.toString();
	}

	/**
	 * Join strings
	 *
	 * @param strings strings to join
	 * @return joined strings
	 */
	@Nullable
	static public String join(@Nullable final String... strings)
	{
		if (strings == null)
		{
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final String string : strings)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				sb.append(',');
			}
			sb.append(string);
		}
		return sb.toString();
	}

	/**
	 * Arguments to single string
	 *
	 * @param args arguments
	 * @return arguments as string
	 */
	static public String argsToString(@Nullable final String... args)
	{
		final StringBuilder sb = new StringBuilder();
		if (args != null && args.length > 0)
		{
			for (final String arg : args)
			{
				if (sb.length() > 0)
				{
					sb.append(", ");
				}
				sb.append(arg);
			}
		}
		return sb.toString();
	}

	/**
	 * Replace argument placeholders with argument values
	 *
	 * @param sql  sql
	 * @param args arguments
	 * @return expanded sql
	 */
	static public String replaceArgs(final String sql, @Nullable final String... args)
	{
		String processedSql = sql;
		if (args != null && args.length > 0)
		{
			for (final String a : args)
			{
				processedSql = processedSql.replaceFirst("\\?", a);
			}
		}
		return processedSql;
	}

	@Nullable
	static public String[] toArgs(@Nullable final String... args)
	{
		if (args == null)
		{
			return null;
		}
		final String[] result = new String[args.length];
		for (int i = 0; i < args.length; i++)
		{
			/* if (args[i] == null)
			{
				result[i] = "NULL";
			}
			else */

			// A single quote within the string can be encoded by putting two single quotes in a row
			final String arg = args[i].replaceAll("'","''");
			if (arg.matches("-?\\d+(\\.\\d+)?"))
			{
				result[i] = arg;
			}
			else
			{
				result[i] = '\'' + arg + '\'';
			}
		}
		return result;
	}

	/**
	 * Convert to ids
	 *
	 * @param string ,-separated string of ids
	 * @return ids as long array
	 */
	@NonNull
	static public long[] toIds(@NonNull final String string)
	{
		final String[] strings = string.split(",");
		final long[] ids = new long[strings.length];
		for (int i = 0; i < strings.length; i++)
		{
			ids[i] = Long.parseLong(strings[i]);
		}
		return ids;
	}
}
