/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * BNC provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Factory implements Function<String, String[]>, Supplier<String[]>
{
	//# instantiated at runtime
	static public final String URI_LAST = "#{uri_last}";

	public Factory()
	{
		System.out.println("BNC Factory");
	}

	@Override
	public String[] apply(String keyname)
	{
		final String last = URI_LAST;

		String table;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String sortOrder = null;

		Key key = Key.valueOf(keyname);
		switch (key)
		{
			// I T E M

			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case BNCS:
				table = "${bncs.table}";
				selection = "${bncs.posid} = ?";
				break;

			// J O I N S

			case WORDS_BNCS:
				table = "${bncs.table} " + //
						"LEFT JOIN ${spwrs.table} USING (${wnwords.wordid}, ${wnposes.posid}) " + //
						"LEFT JOIN ${convtasks.table} USING (${wnwords.wordid}, ${wnposes.posid}) " + //
						"LEFT JOIN ${imaginfs.table} USING (${wnwords.wordid}, ${wnposes.posid})"; //
				break;

			default:
				return null;
		}
		return new String[]{ //
				quote(table), //
				projection == null ? null : "{" + Arrays.stream(projection).map(Factory::quote).collect(Collectors.joining(",")) + "}", //
				quote(selection), //
				selectionArgs == null ? null : "{" + Arrays.stream(selectionArgs).map(Factory::quote).collect(Collectors.joining(",")) + "}", //
				quote(groupBy), //
				quote(sortOrder)};
	}

	@Override
	public String[] get()
	{
		return Arrays.stream(Key.values()).map(Enum::name).toArray(String[]::new);
	}

	private enum Key
	{
		BNCS, WORDS_BNCS;
	}

	private static String quote(String str)
	{
		return str == null ? null : '"' + str + '"';
	}
}
