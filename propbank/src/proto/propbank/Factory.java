/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * PropBank provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Factory implements Function<String, String[]>, Supplier<String[]>
{
	//# instantiated at runtime
	static public final String URI_LAST = "#{uri_last}";

	public Factory()
	{
		System.out.println("PB Factory");
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
			// T A B L E

			case PBWORDS:
				table = "${words.table}";
				break;

			case PBROLESETS:
				table = "${rolesets.table}";
				break;

			// I T E M

			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case PBROLESET1:
				table = "${rolesets.table}";
				selection = "${rolesets.rolesetid} = #{uri_last}";
				break;

			// J O I N S

			case PBROLESETS_X_BY_ROLESET:
				groupBy = "${rolesets.rolesetid}";
				//$FALL-THROUGH$
				//noinspection fallthrough

			case PBROLESETS_X:
				table = "${rolesets.table} " + //
						"LEFT JOIN ${members.table} AS ${as_members} USING (${rolesets.rolesetid}) " + //
						"LEFT JOIN ${words.table} AS ${as_pbwords} USING (${words.pbwordid}) " + //
						"LEFT JOIN ${wnwords.table} AS ${as_words} USING (${wnwords.wordid})"; //
				break;

			case WORDS_PBROLESETS:
				table = "${wnwords.table} " + //
						"INNER JOIN ${words.table} USING (${words.wordid}) " + //
						"INNER JOIN ${rolesets.table} USING (${words.pbwordid})"; //
				break;

			case PBROLESETS_PBROLES:
				table = "${rolesets.table} " + //
						"INNER JOIN ${roles.table} USING (${rolesets.rolesetid}) " + //
						"LEFT JOIN ${argtypes.table} USING (${argtypes.argtypeid}) " + //
						"LEFT JOIN ${funcs.table} USING (${funcs.funcid}) " + //
						"LEFT JOIN ${vnroles.table} USING (${vnroles.vnroleid})"; //
				sortOrder = "${roles.argtypeid}";
				break;

			case PBROLESETS_PBEXAMPLES_BY_EXAMPLE:
				groupBy = "${as_examples}.${examples.exampleid}";
				//$FALL-THROUGH$
				//noinspection fallthrough

			case PBROLESETS_PBEXAMPLES:
				table = "${rolesets.table} " + // 1
						"INNER JOIN ${examples.table} AS ${as_examples} USING (${rolesets.rolesetid}) " + // 2
						"LEFT JOIN ${rels.table} AS ${as_relations} USING (${examples.exampleid}) " + // 3
						"LEFT JOIN ${args.table} AS ${as_args} USING (${examples.exampleid}) " + // 4
						"LEFT JOIN ${argtypes.table} USING (${argtypes.argtypeid}) " + // 5
						"LEFT JOIN ${funcs.table} AS ${as_funcs} ON (${as_args}.${funcs.funcid} = ${as_funcs}.${funcs.funcid}) " + // 6
						"LEFT JOIN ${roles.table} USING (${rolesets.rolesetid},${args.argtypeid}) " + // 12
						"LEFT JOIN ${vnroles.table} USING (${vnroles.vnroleid})"; // 13
				sortOrder = "${as_examples}.${examples.exampleid},${args.arg}";
				break;

			// L O O K U P

			case LOOKUP_FTS_EXAMPLES:
				table = "${examples.table}_${examples.text}_fts4";
				break;

			case LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE:
				groupBy = "${examples.exampleid}";
				//$FALL-THROUGH$
				//noinspection fallthrough

			case LOOKUP_FTS_EXAMPLES_X:
				table = "${examples.table}_${examples.text}_fts4 " + //
						"LEFT JOIN ${rolesets.table} USING (${rolesets.rolesetid})"; //
				break;

			// S U G G E S T

			case SUGGEST_WORDS:
			{
				table = "${words.table} " + //
						"INNER JOIN ${wnwords.table} USING (${wnwords.wordid})"; //
				projection = new String[]{ //
						"${words.pbwordid} AS _id", //
						"${words.word} AS #{suggest_text_1}", //
						"${words.word} AS #{suggest_query}" //
				};
				selection = "${words.word} LIKE ? || '%'";
				selectionArgs = new String[]{last};
				break;
			}

			case SUGGEST_FTS_WORDS:
			{
				table = "${words.table}_${words.word}_fts4";
				projection = new String[]{ //
						"${words.pbwordid} AS _id", //
						"${words.word} AS #{suggest_text_1}", //
						"${words.word} AS #{suggest_query}" //
				};
				selection = "${words.word} MATCH ?";
				selectionArgs = new String[]{last + '*'};
				break;
			}

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
		PBWORDS, PBROLESETS, PBROLESETS_X, PBROLESETS_X_BY_ROLESET, //
		PBROLESET1, //
		PBROLESETS_PBROLES, PBROLESETS_PBEXAMPLES, PBROLESETS_PBEXAMPLES_BY_EXAMPLE, //
		WORDS_PBROLESETS, LOOKUP_FTS_EXAMPLES, LOOKUP_FTS_EXAMPLES_X, LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE, //
		SUGGEST_WORDS, SUGGEST_FTS_WORDS,
	}

	private static String quote(String str)
	{
		return str == null ? null : String.format("\"%s\"", str);
	}
}
