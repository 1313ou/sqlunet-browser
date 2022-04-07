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
				table = String.format("%s " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s)", //
						"${classes.table}", //
						"${members_groupings.table}", "${classes.classid}", //
						"${groupings.table}", "${groupings.groupingid}"); //
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
				table = String.format("%s " +//
								"INNER JOIN %s USING (%s) " +//
								"INNER JOIN %s USING (%s) " +//
								"LEFT JOIN %s USING (%s)", //
						"${wnwords.table}", //
						"${words.table}", "${wnwords.wordid}", //
						"${members_senses.table}", "${words.vnwordid}", //
						"${classes.table}", "${classes.classid}");
				break;

			case VNCLASSES_VNMEMBERS_X_BY_WORD:
				table = String.format("%s " + //
								"INNER JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s, %s) " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s)", //
						"${wnwords.table}", //
						"${members_senses.table}", "${wnwords.wordid}", //
						"${members_groupings.table}", "${members.classid}", "${members.vnwordid}", //
						"${groupings.table}", "${groupings.groupingid}", //
						"${wnsynsets.table}", "${wnsynsets.synsetid}");
				groupBy = "${words.vnwordid}";
				break;

			case VNCLASSES_VNROLES_X_BY_VNROLE:
				table = String.format("%s " + //
								"INNER JOIN %s USING (%s) " + //
								"INNER JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s)", //
						"${classes.table}", //
						"${roles.table}", "${classes.classid}", //
						"${roletypes.table}", "${roletypes.roletypeid}", //
						"${restrs.table}", "${restrs.restrsid}");
				groupBy = "${roles.roleid}";
				break;

			case VNCLASSES_VNFRAMES_X_BY_VNFRAME:
				table = String.format("%s " + //
								"INNER JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s)", //
						"${classes_frames.table}", //
						"${frames.table}", "${frames.frameid}", //
						"${framenames.table}", "${framenames.framenameid}", //
						"${framesubnames.table}", "${framesubnames.framesubnameid}", //
						"${syntaxes.table}", "${syntaxes.syntaxid}", //
						"${semantics.table}", "${semantics.semanticsid}", //
						"${frames_examples.table}", "${frames.frameid}", //
						"${examples.table}", "${examples.exampleid}");
				groupBy = "${frames.frameid}";
				break;

			// L O O K U P

			case LOOKUP_FTS_EXAMPLES:
				table = String.format("%s_%s_fts4", "${examples.table}", "${examples.example}");
				break;

			case LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE:
				groupBy = "${examples.exampleid}";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case LOOKUP_FTS_EXAMPLES_X:
				table = String.format("%s_%s_fts4 " + //
								"LEFT JOIN %s USING (%s)", //
						"${examples.table}", "${examples.example}", //
						"${classes.table}", "${classes.classid}");
				break;

			// S U G G E S T

			case SUGGEST_WORDS:
			{
				table = String.format("%s " + //
								"INNER JOIN %s USING (%s)", //
						"${words.table}", //
						"${wnwords.table}", "${wnwords.wordid}");
				projection = new String[]{String.format("%s AS _id", "${words.vnwordid}"), //
						String.format("%s AS #{suggest_text_1}", "${words.word}"), //
						String.format("%s AS #{suggest_query}", "${words.word}")}; //
				selection = String.format("%s LIKE ? || '%%'", "${words.word}");
				selectionArgs = new String[]{last};
				break;
			}

			case SUGGEST_FTS_WORDS:
			{
				table = String.format("%s_%s_fts4", "${words.table}", "${words.word}");
				projection = new String[]{String.format("%s AS _id", "${words.vnwordid}"),//
						String.format("%s AS #{suggest_text_1}", "${words.word}"), //
						String.format("%s AS #{suggest_query}", "${words.word}")}; //
				selection = String.format("%s MATCH ?", "${words.word}");
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