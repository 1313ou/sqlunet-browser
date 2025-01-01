/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * VerbNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Factory implements Function<String, String[]>, Supplier<String[]>
{
	//# instantiated at runtime
	static public final String URI_LAST = "#{uri_last}";

	public Factory()
	{
		System.out.println("VN Factory");
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

			case WORDS:
				table = "${words.table}";
				break;

			case SENSES:
				table = "${wnsenses.table}";
				break;

			case SYNSETS:
				table = "${wnsynsets.table}";
				break;

			case VNCLASSES:
				table = "${classes.table}";
				break;

			case VNCLASSES_X_BY_VNCLASS:
				table = "${classes.table} " + //
						"LEFT JOIN ${members_groupings.table} USING (${classes.classid}) " + //
						"LEFT JOIN ${groupings.table} USING (${groupings.groupingid})"; //
				groupBy = "${classes.classid}";
				break;

			// I T E M

			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case VNCLASS1:
				table = "${classes.table}";
				selection = "${classes.classid} = #{uri_last}";
				break;

			// J O I N S

			case WORDS_VNCLASSES:
				table = "${wnwords.table} " + // 1
						"INNER JOIN ${words.table} USING (${wnwords.wordid}) " + // 2
						"INNER JOIN ${members_senses.table} USING (${words.vnwordid}, ${wnwords.wordid}) " + // 3
						"LEFT JOIN ${classes.table} USING (${classes.classid})"; // 4
				break;

			case VNCLASSES_VNMEMBERS_X_BY_WORD:
				table = "${wnwords.table} " + // 1
						"INNER JOIN ${words.table} USING (${wnwords.wordid}) " + // 2
						"INNER JOIN ${members_senses.table} USING (${words.vnwordid}, ${wnwords.wordid}) " + // 3
						"LEFT JOIN ${members_groupings.table} USING (${members.classid}, ${words.vnwordid}) " + // 4
						"LEFT JOIN ${groupings.table} USING (${groupings.groupingid}) " + // 5
						"LEFT JOIN ${wnsynsets.table} USING (${wnsynsets.synsetid})"; // 6
				groupBy = "${words.vnwordid}";
				break;

			case VNCLASSES_VNROLES_X_BY_VNROLE:
				table = "${classes.table} " + //
						"INNER JOIN ${roles.table} USING (${classes.classid}) " + //
						"INNER JOIN ${roletypes.table} USING (${roletypes.roletypeid}) " + //
						"LEFT JOIN ${restrs.table} USING (${restrs.restrsid})"; //
				groupBy = "${roles.roleid}";
				break;

			case VNCLASSES_VNFRAMES_X_BY_VNFRAME:
				table = "${classes_frames.table} " + //
						"INNER JOIN ${frames.table} USING (${frames.frameid}) " + //
						"LEFT JOIN ${framenames.table} USING (${framenames.framenameid}) " + //
						"LEFT JOIN ${framesubnames.table} USING (${framesubnames.framesubnameid}) " + //
						"LEFT JOIN ${syntaxes.table} USING (${syntaxes.syntaxid}) " + //
						"LEFT JOIN ${semantics.table} USING (${semantics.semanticsid}) " + //
						"LEFT JOIN ${frames_examples.table} USING (${frames.frameid}) " + //
						"LEFT JOIN ${examples.table} USING (${examples.exampleid})"; //
				groupBy = "${frames.frameid}";
				break;

			// L O O K U P

			case LOOKUP_FTS_EXAMPLES:
				table = "${examples.table}_${examples.example}_fts4";
				break;

			case LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE:
				groupBy = "${examples.exampleid}";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case LOOKUP_FTS_EXAMPLES_X:
				table = "${examples.table}_${examples.example}_fts4 " + //
						"LEFT JOIN ${classes.table} USING (${classes.classid})"; //
				break;

			// S U G G E S T

			case SUGGEST_WORDS:
			{
				table = "${words.table} " + //
						"INNER JOIN ${wnwords.table} USING (${wnwords.wordid})"; //
				projection = new String[]{"${words.vnwordid} AS _id", //
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
				projection = new String[]{"${words.vnwordid} AS _id",//
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
		WORDS, SENSES, SYNSETS, //
		VNCLASSES, //
		VNCLASS1, //
		WORDS_VNCLASSES, //
		VNCLASSES_X_BY_VNCLASS, VNCLASSES_VNMEMBERS_X_BY_WORD, VNCLASSES_VNROLES_X_BY_VNROLE, VNCLASSES_VNFRAMES_X_BY_VNFRAME, //
		LOOKUP_FTS_EXAMPLES, LOOKUP_FTS_EXAMPLES_X, LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE, //
		SUGGEST_WORDS, SUGGEST_FTS_WORDS,
	}

	private static String quote(String str)
	{
		return str == null ? null : String.format("\"%s\"", str);
	}
}