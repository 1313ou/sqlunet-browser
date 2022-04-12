/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Extended cross WordNet-FrameNet-PropBank-VerbNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Factory implements Function<String, String[]>, Supplier<String[]>
{

	static public final String AS_WORDS = "w";
	static public final String AS_SENSES = "s";
	static public final String AS_SYNSETS = "y";
	static public final String AS_POSES = "p";
	static public final String AS_CLASSES = "c";

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public Factory()
	{
	}

	// Q U E R Y

	private static String quote(String str)
	{
		return str == null ? null : String.format("\"%s\"", str);
	}

	@Override
	public String[] apply(final String keyname)
	{
		String table = null;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String sortOrder = null;

		Key key = Key.valueOf(keyname);
		switch (key)
		{
			case PREDICATEMATRIX:
				// table = "pm";
				table = "${pmvn.table} " + //
						"LEFT JOIN ${pmpb.table} USING (wordid)" + //
						"LEFT JOIN ${pmfn.table} USING (wordid)" //
				;
				break;

			case PREDICATEMATRIX_VERBNET:
				table = "${pmvn.table}";

				break;

			case PREDICATEMATRIX_PROPBANK:
				table = "${pmpb.table}";
				break;

			case PREDICATEMATRIX_FRAMENET:
				table = "${pmfn.table}";
				break;

			case SOURCES:
				table = "${sources.table}";
				break;

			// J O I N S

			case WORDS_FNWORDS_PBWORDS_VNWORDS:
				table = "${words.table} AS " + AS_WORDS + ' ' + //
						"LEFT JOIN ${senses.table} AS ${as_senses} USING (${words.wordid}) " + //
						"LEFT JOIN ${synsets.table} AS ${as_synsets} USING (${synsets.synsetid}) " + //
						"LEFT JOIN ${poses.table} AS ${as_poses} USING (${poses.posid}) " + //
						"LEFT JOIN ${casedwords.table} USING (${words.wordid},${casedwords.casedwordid}) " + //
						"LEFT JOIN ${domains.table} USING (${domains.domainid}) " + //
						"LEFT JOIN ${fn_words.table} USING (${words.wordid}) " + //
						"LEFT JOIN ${vn_words.table} USING (${words.wordid}) " + //
						"LEFT JOIN ${pb_words.table} USING (${words.wordid})";
				groupBy = "${synsets.synsetid}";
				break;

			case WORDS_PBWORDS_VNWORDS:
				table = "${words.table} AS " + AS_WORDS + ' ' + //
						"LEFT JOIN ${senses.table} AS ${as_senses} USING (${words.wordid}) " + //
						"LEFT JOIN ${synsets.table} AS ${as_synsets} USING (${synsets.synsetid}) " + //
						"LEFT JOIN ${poses.table} AS ${as_poses} USING (${poses.posid}) " + //
						"LEFT JOIN ${casedwords.table} USING (${words.wordid},${casedwords.casedwordid}) " + //
						"LEFT JOIN ${domains.table} USING (${domains.domainid}) " + //
						"LEFT JOIN ${vn_words.table} USING (${words.wordid}) " + //
						"LEFT JOIN ${pb_words.table} USING (${words.wordid})";
				groupBy = "${synsets.synsetid}";
				break;

			case WORDS_VNWORDS_VNCLASSES:
			{
				table = "${vn_words.table} " + //
						"INNER JOIN ${vn_members_senses.table} USING (${words.wordid}) " + //
						"INNER JOIN ${vn_classes.table} AS ${as_classes} USING (${vn_classes.classid}) " + //
						"LEFT JOIN ${synsets.table} USING (${synsets.synsetid})";
				groupBy = "${words.wordid},${synsets.synsetid},${vn_classes.classid}";
				break;
			}

			case WORDS_VNWORDS_VNCLASSES_U:
			{
				final String table1 = "${pmvn.table} " + //
						"INNER JOIN ${vn_classes.table} USING (${vn_classes.classid}) " + //
						"LEFT JOIN ${synsets.table} USING (${synsets.synsetid})";
				final String table2 = "${vn_words.table} " + //
						"INNER JOIN ${vn_members_senses.table} USING (${vn_words.vnwordid}) " + //
						"INNER JOIN ${vn_classes.table} USING (${vn_classes.classid})";
				final String[] unionProjection = {"${words.wordid}", "${synsets.synsetid}", "${vn_classes.classid}", "${vn_classes.class}", "${vn_classes.classtag}", "${synsets.definition}"};
				final String[] table1Projection = unionProjection;
				final String[] table2Projection = {"${words.wordid}", "${synsets.synsetid}", "${vn_classes.classid}", "${vn_classes.class}", "${vn_classes.classtag}"};
				final String[] groupByArray = {"${words.wordid}", "${synsets.synsetid}", "${vn_classes.classid}"};
				assert projection != null;
				final String query = makeQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, groupByArray, sortOrder, "vn");
				break;
			}

			case WORDS_PBWORDS_PBROLESETS:
			{
				table = "${pb_words.table} " + //
						"INNER JOIN ${pb_rolesets.table} AS ${as_classes} USING (${pb_words.pbwordid})";
				groupBy = "${words.wordid},${synsets.synsetid},${pb_rolesets.rolesetid}";
				break;
			}

			case WORDS_PBWORDS_PBROLESETS_U:
			{
				final String table1 = "${pmpb.table} " + //
						"INNER JOIN ${pb_rolesets.table} USING (${pb_rolesets.rolesetid}) " + //
						"LEFT JOIN ${synsets.table} USING (${synsets.synsetid})";
				final String table2 = "${pb_words.table} " + //
						"INNER JOIN ${pb_rolesets.table} USING (${pb_words.pbwordid})";
				final String[] unionProjection = {"${words.wordid}", "${synsets.synsetid}", "${pb_rolesets.rolesetid}", "${pb_rolesets.rolesetname}", "${pb_rolesets.rolesethead}", "${pb_rolesets.rolesetdescr}", "${synsets.definition}"};
				final String[] table1Projection = unionProjection;
				final String[] table2Projection = {"${words.wordid}", "${pb_rolesets.rolesetid}", "${pb_rolesets.rolesetname}", "${pb_rolesets.rolesethead}", "${pb_rolesets.rolesetdescr}"};
				final String[] groupByArray = {"${words.wordid}", "${synsets.synsetid}", "${pb_rolesets.rolesetid}"};
				assert projection != null;
				final String query = makeQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, groupByArray, sortOrder, "pb");
				break;
			}

			case WORDS_FNWORDS_FNFRAMES_U:
			{
				final String table1 = "${pmfn.table} " + //
						"INNER JOIN ${fn_frames.table} USING (${fn_frames.frameid}) " + //
						"LEFT JOIN ${fn_lexunits.table} USING (${fn_lexunits.luid},${fn_frames.frameid}) " + //
						"LEFT JOIN ${synsets.table} USING (${synsets.synsetid})";
				final String table2 = "${fn_words.table} " + //
						"INNER JOIN ${fn_lexemes.table} USING (${fn_words.fnwordid}) " + //
						"INNER JOIN ${fn_lexunits.table} USING (${fn_lexunits.luid},${poses.posid}) " + //
						"INNER JOIN ${fn_frames.table} USING (${vn_frames.frameid})";
				final String[] unionProjection = {"${words.wordid}", "${synsets.synsetid}", "${fn_frames.frameid}", "${fn_frames.frame}", "${fn_frames.framedefinition}", "${fn_lexunits.luid}", "${fn_lexunits.lexunit}", "${fn_lexunits.ludefinition}", "${synsets.definition}"};
				final String[] table1Projection = unionProjection;
				final String[] table2Projection = {"${words.wordid}", "${fn_frames.frameid}", "${fn_frames.frame}", "${fn_frames.framedefinition}", "${fn_lexunits.luid}", "${fn_lexunits.lexunit}", "${fn_lexunits.ludefinition}"};
				final String[] groupByArray = {"${words.wordid}", "${synsets.synsetid}", "${fn_frames.frameid}"};
				assert projection != null;
				final String query = makeQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, groupByArray, sortOrder, "fn");
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

	/**
	 * Make union query
	 *
	 * @param table1           table1
	 * @param table2           table2
	 * @param table1Projection table1 projection
	 * @param table2Projection table2 projection
	 * @param unionProjection  union projection
	 * @param projection       final projection
	 * @param selection        selection
	 * @param groupBys         group by
	 * @param sortOrder        sort
	 * @param tag              tag
	 * @return union sql
	 */
	private String makeQuery(final String table1, final String table2, //
			final String[] table1Projection, final String[] table2Projection, //
			final String[] unionProjection, final String[] projection, //
			final String selection, //
			final String[] groupBys, final String sortOrder, final String tag)
	{
		return "#{query}";
	}

	@Override
	public String[] get()
	{
		return Arrays.stream(Key.values()).map(Enum::name).toArray(String[]::new);
	}

	private enum Key
	{
		PREDICATEMATRIX, //
		PREDICATEMATRIX_VERBNET, //
		PREDICATEMATRIX_PROPBANK, //
		PREDICATEMATRIX_FRAMENET, //
		WORDS_FNWORDS_PBWORDS_VNWORDS, //

		WORDS_PBWORDS_VNWORDS, //
		WORDS_VNWORDS_VNCLASSES, //
		WORDS_VNWORDS_VNCLASSES_U, //
		WORDS_PBWORDS_PBROLESETS, //
		WORDS_PBWORDS_PBROLESETS_U, //
		WORDS_FNWORDS_FNFRAMES_U, //

		SOURCES,
	}
}
