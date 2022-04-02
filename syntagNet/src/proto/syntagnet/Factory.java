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
				selection = "${syntagms.syntagmid} = ?";
				break;

			// J O I N S

			case COLLOCATIONS_X:
				table = String.format("%s " + //
								"JOIN %s AS %s ON (%s = %s.%s) " + //
								"JOIN %s AS %s ON (%s = %s.%s) " + //
								"JOIN %s AS %s ON (%s = %s.%s) " + //
								"JOIN %s AS %s ON (%s = %s.%s)", "${syntagms.table}", //
						"${wnwords.table}", "${as_words1}", "${syntagms.word1id}", "${as_words1}", "${wnwords.wordid}", //
						"${wnwords.table}", "${as_words2}", "${syntagms.word2id}", "${as_words2}", "${wnwords.wordid}", //
						"${wnsynsets.table}", "${as_synsets1}", "${syntagms.synset1id}", "${as_synsets1}", "${wnsynsets.synsetid}", //
						"${wnsynsets.table}", "${as_synsets2}", "${syntagms.synset2id}", "${as_synsets2}", "${wnsynsets.synsetid}");
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
		COLLOCATIONS, COLLOCATIONS_X
	}

	private static String quote(String str)
	{
		return str == null ? null : String.format("\"%s\"", str);
	}
}
