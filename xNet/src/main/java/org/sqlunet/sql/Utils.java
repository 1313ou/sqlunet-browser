package org.sqlunet.sql;

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
	@SuppressWarnings("unused")
	static public String join(final int... ids)
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
	@SuppressWarnings("unused")
	static public String join(final long... ids)
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
	static public String join(final String... strings)
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
	static public String argsToString(final String... args)
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
	static public String replaceArgs(final String sql, final String... args)
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

	static public String[] toArgs(final String... args)
	{
		if (args == null)
		{
			return null;
		}
		final String[] result = new String[args.length];
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].matches("-?\\d+(\\.\\d+)?"))
			{
				result[i] = args[i];
			}
			else
			{
				result[i] = '\'' + args[i] + '\'';
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
	static public long[] toIds(final String string)
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
