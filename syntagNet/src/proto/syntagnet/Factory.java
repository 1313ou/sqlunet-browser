/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * SyntagNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Factory implements Function<String, String[]>, Supplier<String[]>
{
	//# instantiated at runtime
	static public final String URI_LAST = "#{uri_last}";

	public Factory()
	{
		System.out.println("SN Factory");
	}

	@Override
	public String[] apply(String keyname)
	{
		String table;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String sortOrder = null;

		Key key = Key.valueOf(keyname);
		switch (key)
		{
			// T A B L E

			case COLLOCATIONS:
				table = "${syntagms.table}";
				break;

			case COLLOCATION1:
				table = "${syntagms.table}";
				selection = "${syntagms.syntagmid} = #{uri_last}";
				break;

			// J O I N S

			case COLLOCATIONS_X:
				table = "${syntagms.table} " + //
						"JOIN ${wnwords.table} AS ${as_words1} ON ${syntagms.word1id} = ${as_words1}.${wnwords.wordid} " + //
						"JOIN ${wnwords.table} AS ${as_words2} ON (${syntagms.word2id} = ${as_words2}.${wnwords.wordid}) " + //
						"JOIN ${wnsynsets.table} AS ${as_synsets1} ON (${syntagms.synset1id} = ${as_synsets1}.${wnsynsets.synsetid}) " + //
						"JOIN ${wnsynsets.table} AS ${as_synsets2} ON (${syntagms.synset2id} = ${as_synsets2}.${wnsynsets.synsetid})"; //
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
		COLLOCATIONS, COLLOCATION1, COLLOCATIONS_X
	}

	private static String quote(String str)
	{
		return str == null ? null : '"' + str + '"';
	}
}
