/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.provider;

import org.sqlunet.provider.XNetControl.Result;

public class QueriesLegacy
{
	public static Result queryLegacy(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return queryLegacyMain(code, uriLast, projection0, selection0, selectionArgs0);
	}

	@SuppressWarnings("UnnecessaryLocalVariable")
	public static Result queryLegacyMain(final int code, @SuppressWarnings("unused") final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String[] projection = projection0;
		String selection = selection0;
		String[] selectionArgs = selectionArgs0;
		String groupBy = null;
		String sortOrder = null;

		switch (code)
		{
			case XNetControl.PREDICATEMATRIX:
				// table = "pm_pms";
				table = "pm_vn " + //
						"LEFT JOIN pm_pb USING (wordid) " + //
						"LEFT JOIN pm_fn USING (wordid)" //
				;
				break;

			case XNetControl.PREDICATEMATRIX_VERBNET:
				table = "pm_vn";
				break;

			case XNetControl.PREDICATEMATRIX_PROPBANK:
				table = "pm_pb";
				break;

			case XNetControl.PREDICATEMATRIX_FRAMENET:
				table = "pm_fn";
				break;

			case XNetControl.SOURCES:
				table = "sources";
				break;

			// J O I N S

			case XNetControl.WORDS_FNWORDS_PBWORDS_VNWORDS:
				table = "words AS " + XNetContract.AS_WORDS + ' ' + //
						"LEFT JOIN senses AS " + XNetContract.AS_SENSES + " USING (wordid) " + //
						"LEFT JOIN synsets AS " + XNetContract.AS_SYNSETS + " USING (synsetid) " + //
						"LEFT JOIN poses AS " + XNetContract.AS_POSES + " USING (posid) " + //
						"LEFT JOIN casedwords USING (wordid,casedwordid) " + //
						"LEFT JOIN domains USING (domainid) " + //
						"LEFT JOIN fn_words USING (wordid) " + //
						"LEFT JOIN vn_words USING (wordid) " + //
						"LEFT JOIN pb_words USING (wordid)";
				groupBy = "synsetid";
				break;

			case XNetControl.WORDS_PBWORDS_VNWORDS:
				table = "words AS " + XNetContract.AS_WORDS + ' ' + //
						"LEFT JOIN senses AS " + XNetContract.AS_SENSES + " USING (wordid) " + //
						"LEFT JOIN synsets AS " + XNetContract.AS_SYNSETS + " USING (synsetid) " + //
						"LEFT JOIN poses AS " + XNetContract.AS_POSES + " USING (posid) " + //
						"LEFT JOIN casedwords USING (wordid,casedwordid) " + //
						"LEFT JOIN domains USING (domainid) " + //
						"LEFT JOIN vn_words USING (wordid) " + //
						"LEFT JOIN pb_words USING (wordid)";
				groupBy = "synsetid";
				break;


			case XNetControl.WORDS_VNWORDS_VNCLASSES:
			{
				table = "vn_words " + //
						"INNER JOIN vn_members_senses USING (vnwordid,wordid) " + //
						"INNER JOIN vn_classes AS " + XNetContract.AS_CLASSES + " USING (classid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				groupBy = "wordid,synsetid,classid";
				break;
			}

			case XNetControl.WORDS_PBWORDS_PBROLESETS:
			{
				table = "pb_words " + //
						"INNER JOIN pb_rolesets AS " + XNetContract.AS_CLASSES + " USING (pbwordid)";
				groupBy = "wordid,synsetid,rolesetid";
				break;
			}

			case XNetControl.WORDS_VNWORDS_VNCLASSES_U:
			{
				final String table1 = "pm_vn " + //
						"INNER JOIN vn_classes USING (classid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				final String table2 = "vn_words " + //
						"INNER JOIN vn_members_senses USING (vnwordid,wordid) " + //
						"INNER JOIN vn_classes USING (classid)";
				final String[] unionProjection = {"wordid", "synsetid", "classid", "class", "classtag", "definition"};
				final String[] table1Projection = unionProjection;
				final String[] table2Projection = {"wordid", "synsetid", "classid", "class", "classtag"};
				final String[] groupByArray = {"wordid", "synsetid", "classid"};
				projection = BaseProvider.prependProjection(projection, "GROUP_CONCAT(DISTINCT source) AS sources");
				//noinspection ConstantConditions
				return Utils.makeUnionQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, selectionArgs, groupByArray, sortOrder, "vn");
			}

			case XNetControl.WORDS_PBWORDS_PBROLESETS_U:
			{
				final String table1 = "pm_pb " + //
						"INNER JOIN pb_rolesets USING (rolesetid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				final String table2 = "pb_words " + //
						"INNER JOIN pb_rolesets USING (pbwordid)";
				final String[] unionProjection = {"wordid", "synsetid", "rolesetid", "rolesetname", "rolesethead", "rolesetdescr", "definition"};
				final String[] table1Projection = unionProjection;
				final String[] table2Projection = {"wordid", "rolesetid", "rolesetname", "rolesethead", "rolesetdescr"};
				final String[] groupByArray = {"wordid", "synsetid", "rolesetid"};
				projection = BaseProvider.prependProjection(projection, "GROUP_CONCAT(DISTINCT source) AS sources");
				//noinspection ConstantConditions
				return Utils.makeUnionQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, selectionArgs, groupByArray, sortOrder, "pb");
			}

			case XNetControl.WORDS_FNWORDS_FNFRAMES_U:
			{
				final String table1 = "pm_fn " + //
						"INNER JOIN fn_frames USING (frameid) " + //
						"LEFT JOIN fn_lexunits USING (luid,frameid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				final String table2 = "fn_words " + //
						"INNER JOIN fn_lexemes USING (fnwordid) " + //
						"INNER JOIN fn_lexunits USING (luid,posid) " + //
						"INNER JOIN fn_frames USING (frameid)";
				final String[] unionProjection = {"wordid", "synsetid", "frameid", "frame", "framedefinition", "luid", "lexunit", "ludefinition", "definition"};
				final String[] table1Projection = unionProjection;
				final String[] table2Projection = {"wordid", "frameid", "frame", "framedefinition", "luid", "lexunit", "ludefinition"};
				final String[] groupByArray = {"wordid", "synsetid", "frameid"};
				projection = BaseProvider.prependProjection(projection, "GROUP_CONCAT(DISTINCT source) AS sources");
				//noinspection ConstantConditions
				return Utils.makeUnionQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, selectionArgs, groupByArray, sortOrder, "fn");
			}

			case XNetControl.WORDS_VNWORDS_VNCLASSES_1:
				table = "pm_vn " + //
						"INNER JOIN vn_classes USING (classid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				projection = new String[]{"wordid", "synsetid", "classid", "class", "classtag", "definition"};
				break;

			case XNetControl.WORDS_VNWORDS_VNCLASSES_2:
				table = "vn_words " + //
						"INNER JOIN vn_members_senses USING (vnwordid,wordid) " + //
						"INNER JOIN vn_classes USING (classid)";
				projection = new String[]{"wordid", "synsetid", "classid", "class", "classtag"};
				break;

			case XNetControl.WORDS_VNWORDS_VNCLASSES_1U2:
				table = "( " + //
						"SELECT wordid, synsetid, classid, class, classtag, definition, 'pmvn' AS source " + //
						"FROM pm_vn " + //
						"INNER JOIN vn_classes USING (classid) " + //
						"LEFT JOIN synsets USING (synsetid) " + //
						"WHERE (#{selection}) " + //
						"UNION " + //
						"SELECT wordid, synsetid, classid, class, classtag, NULL AS definition, 'vn' AS source " + //
						"FROM vn_words " + //
						"INNER JOIN vn_members_senses USING (vnwordid,wordid) " + //
						"INNER JOIN vn_classes USING (classid) " + //
						"WHERE (#{selection}) " + ")";
				break;

			case XNetControl.WORDS_PBWORDS_PBROLESETS_1:
				table = "pm_pb " + //
						"INNER JOIN pb_rolesets USING (rolesetid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				projection = new String[]{"wordid", "synsetid", "rolesetid", "rolesetname", "rolesethead", "rolesetdescr", "definition"};
				break;

			case XNetControl.WORDS_PBWORDS_PBROLESETS_2:
				table = "pb_words " + //
						"INNER JOIN pb_rolesets USING (pbwordid)";
				projection = new String[]{"wordid", "rolesetid", "rolesetname", "rolesethead", "rolesetdescr"};
				break;

			case XNetControl.WORDS_PBWORDS_PBROLESETS_1U2:
				table = "( " + "SELECT wordid, synsetid, rolesetid, rolesetname, rolesethead, rolesetdescr, definition, 'pmpb' AS source " + //
						"FROM pm_pb " + //
						"INNER JOIN pb_rolesets USING (rolesetid) " + //
						"LEFT JOIN synsets USING (synsetid) " + //
						"WHERE (#{selection}) " + //
						"UNION " + //
						"SELECT wordid, NULL AS synsetid, rolesetid, rolesetname, rolesethead, rolesetdescr, NULL AS definition, 'pb' AS source " + //
						"FROM pb_words " + //
						"INNER JOIN pb_rolesets USING (pbwordid) " + //
						"WHERE (#{selection}) " + //
						")";
				break;

			case XNetControl.WORDS_FNWORDS_FNFRAMES_1:
				table = "pm_fn " + //
						"INNER JOIN fn_frames USING (frameid) " + //
						"LEFT JOIN fn_lexunits USING (luid,frameid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				projection = new String[]{"wordid", "synsetid", "frameid", "frame", "framedefinition", "luid", "lexunit", "ludefinition", "definition"};
				break;

			case XNetControl.WORDS_FNWORDS_FNFRAMES_2:
				table = "fn_words " + //
						"INNER JOIN fn_lexemes USING (fnwordid) " + //
						"INNER JOIN fn_lexunits USING (luid,posid) " + //
						"INNER JOIN fn_frames USING (frameid)";
				projection = new String[]{"wordid", "frameid", "frame", "framedefinition", "luid", "lexunit", "ludefinition"};
				break;

			case XNetControl.WORDS_FNWORDS_FNFRAMES_1U2:
				table = "( " + //
						"SELECT wordid, synsetid, frameid, frame, framedefinition, luid, lexunit, ludefinition, definition, 'pmfn' AS source " + //
						"FROM pm_fn " + //
						"INNER JOIN fn_frames USING (frameid) " + //
						"LEFT JOIN fn_lexunits USING (luid,frameid) " + //
						"LEFT JOIN synsets USING (synsetid) " + //
						"WHERE (#{selection}) " + //
						"UNION " + //
						"SELECT wordid, NULL AS synsetid, frameid, frame, framedefinition, luid, lexunit, ludefinition, NULL AS definition, 'fn' AS source " + //
						"FROM fn_words " + //
						"INNER JOIN fn_lexemes USING (fnwordid) " + //
						"INNER JOIN fn_lexunits USING (luid,posid) " + //
						"INNER JOIN fn_frames USING (frameid) " + //
						"WHERE (#{selection}) " + //
						")";
				break;

			default:
				return null;
		}
		//noinspection ConstantConditions
		return new Result(table, projection, selection, selectionArgs, groupBy, sortOrder);
	}
}
