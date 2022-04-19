package org.sqlunet.provider;

import org.sqlunet.xnet.provider.Q;
import org.sqlunet.xnet.provider.V;

public class XSqlUNetDispatcher
{
	static public class Result
	{
		final String table;
		final String[] projection;
		final String selection;
		final String[] selectionArgs;
		final String groupBy;
		final String orderBy;

		public Result(final String table, final String[] projection, final String selection, final String[] selectionArgs, final String groupBy, final String orderBy)
		{
			this.table = table;
			this.projection = projection;
			this.selection = selection;
			this.selectionArgs = selectionArgs;
			this.groupBy = groupBy;
			this.orderBy = orderBy;
		}
	}

	// table codes
	static protected final int PREDICATEMATRIX = 200;
	static protected final int PREDICATEMATRIX_VERBNET = 210;
	static protected final int PREDICATEMATRIX_PROPBANK = 220;
	static protected final int PREDICATEMATRIX_FRAMENET = 230;
	// join codes
	static protected final int WORDS_FNWORDS_PBWORDS_VNWORDS = 100;
	static protected final int WORDS_PBWORDS_VNWORDS = 101;
	static protected final int WORDS_VNWORDS_VNCLASSES = 310;
	static protected final int WORDS_VNWORDS_VNCLASSES_U = 311;
	static protected final int WORDS_VNWORDS_VNCLASSES_1 = 312;
	static protected final int WORDS_VNWORDS_VNCLASSES_2 = 313;
	static protected final int WORDS_VNWORDS_VNCLASSES_1U2 = 314;
	static protected final int WORDS_PBWORDS_PBROLESETS = 320;
	static protected final int WORDS_PBWORDS_PBROLESETS_U = 321;
	static protected final int WORDS_PBWORDS_PBROLESETS_1 = 322;
	static protected final int WORDS_PBWORDS_PBROLESETS_2 = 323;
	static protected final int WORDS_PBWORDS_PBROLESETS_1U2 = 324;
	static protected final int WORDS_FNWORDS_FNFRAMES_U = 331;
	static protected final int WORDS_FNWORDS_FNFRAMES_1 = 332;
	static protected final int WORDS_FNWORDS_FNFRAMES_2 = 333;
	static protected final int WORDS_FNWORDS_FNFRAMES_1U2 = 334;
	static protected final int SOURCES = 400;

	public static Result queryMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String[] projection = projection0;
		String selection = selection0;
		String[] selectionArgs = selectionArgs0;
		String groupBy = null;
		String orderBy = null;

		switch (code)
		{
			case XSqlUNetDispatcher.PREDICATEMATRIX:
				// table = "pm";
				table = Q.PREDICATEMATRIX.TABLE;
				break;

			case XSqlUNetDispatcher.PREDICATEMATRIX_VERBNET:
				table = Q.PREDICATEMATRIX_VERBNET.TABLE;
				break;

			case XSqlUNetDispatcher.PREDICATEMATRIX_PROPBANK:
				table = Q.PREDICATEMATRIX_PROPBANK.TABLE;
				break;

			case XSqlUNetDispatcher.PREDICATEMATRIX_FRAMENET:
				table = Q.PREDICATEMATRIX_FRAMENET.TABLE;
				break;

			case XSqlUNetDispatcher.SOURCES:
				table = Q.SOURCES.TABLE;
				break;

			// J O I N S

			case XSqlUNetDispatcher.WORDS_FNWORDS_PBWORDS_VNWORDS:
				table = Q.WORDS_FNWORDS_PBWORDS_VNWORDS.TABLE;
				groupBy = V.SYNSETID;
				break;

			case XSqlUNetDispatcher.WORDS_PBWORDS_VNWORDS:
				table = Q.WORDS_PBWORDS_VNWORDS.TABLE;
				groupBy = V.SYNSETID;
				break;

			case XSqlUNetDispatcher.WORDS_VNWORDS_VNCLASSES:
			{
				table = Q.WORDS_VNWORDS_VNCLASSES.TABLE;
				groupBy = String.format("%s,%s,%s", V.WORDID, V.SYNSETID, V.CLASSID);
				break;
			}

			case XSqlUNetDispatcher.WORDS_PBWORDS_PBROLESETS:
			{
				table = Q.WORDS_PBWORDS_PBROLESETS.TABLE;
				groupBy = String.format("%s,%s,%s", V.WORDID, V.SYNSETID, V.ROLESETID);
				break;
			}

			// U

			case WORDS_VNWORDS_VNCLASSES_1:
				table = Q.WORDS_VNWORDS_VNCLASSES_1.TABLE;
				projection = Q.WORDS_VNWORDS_VNCLASSES_1.PROJECTION;
				break;

			case WORDS_VNWORDS_VNCLASSES_2:
				table = Q.WORDS_VNWORDS_VNCLASSES_2.TABLE;
				projection = Q.WORDS_VNWORDS_VNCLASSES_2.PROJECTION;
				break;

			case WORDS_VNWORDS_VNCLASSES_1U2:
				table = Q.WORDS_VNWORDS_VNCLASSES_1U2.TABLE; //.replaceAll("#\\{selection\\}", selection);
				break;

			case XSqlUNetDispatcher.WORDS_VNWORDS_VNCLASSES_U:
			{
				/*
				final String table1 = Q.WORDS_VNWORDS_VNCLASSES_1.TABLE;
				final String table2 = Q.WORDS_VNWORDS_VNCLASSES_2.TABLE;
				final String[] table1Projection = Q.WORDS_VNWORDS_VNCLASSES_1.PROJECTION;
				final String[] table2Projection = Q.WORDS_VNWORDS_VNCLASSES_2.PROJECTION;
				final String[] unionProjection = table1Projection;
				final String[] groupByArray = {V.WORDID, V.SYNSETID, V.CLASSID};
				return Utils.makeUnionQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, selectionArgs, groupByArray, orderBy, "vn");
				*/
				table = Q.WORDS_VNWORDS_VNCLASSES_1U2.TABLE.replaceAll("#\\{selection\\}", selection);
				selection = null;
				selectionArgs = Utils.unfoldSelectionArgs(selectionArgs);
				groupBy = String.format("%s,%s,%s", V.WORDID, V.SYNSETID, V.CLASSID);
				break;
			}

			case WORDS_PBWORDS_PBROLESETS_1:
				table = Q.WORDS_PBWORDS_PBROLESETS_1.TABLE;
				projection = Q.WORDS_PBWORDS_PBROLESETS_1.PROJECTION;
				break;

			case WORDS_PBWORDS_PBROLESETS_2:
				table = Q.WORDS_PBWORDS_PBROLESETS_2.TABLE;
				projection = Q.WORDS_PBWORDS_PBROLESETS_2.PROJECTION;
				break;

			case WORDS_PBWORDS_PBROLESETS_1U2:
				table = Q.WORDS_PBWORDS_PBROLESETS_1U2.TABLE; //.replaceAll("#\\{selection\\}", selection);
				break;

			case XSqlUNetDispatcher.WORDS_PBWORDS_PBROLESETS_U:
			{
				/*
				final String table1 = Q.WORDS_PBWORDS_PBROLESETS_1.TABLE;
				final String table2 = Q.WORDS_PBWORDS_PBROLESETS_2.TABLE;
				final String[] table1Projection = Q.WORDS_PBWORDS_PBROLESETS_1.PROJECTION;
				final String[] table2Projection = Q.WORDS_PBWORDS_PBROLESETS_2.PROJECTION;
				final String[] unionProjection = table1Projection;
				final String[] groupByArray = {V.WORDID, V.SYNSETID, V.ROLESETID};
				return Utils.makeUnionQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, selectionArgs, groupByArray, orderBy, "pb");
				*/
				table = Q.WORDS_PBWORDS_PBROLESETS_1U2.TABLE.replaceAll("#\\{selection\\}", selection);
				selection = null;
				selectionArgs = Utils.unfoldSelectionArgs(selectionArgs);
				groupBy = String.format("%s,%s,%s", V.WORDID, V.SYNSETID, V.ROLESETID);
				break;
			}

			case WORDS_FNWORDS_FNFRAMES_1:
				table = Q.WORDS_FNWORDS_FNFRAMES_1.TABLE;
				projection = Q.WORDS_FNWORDS_FNFRAMES_1.PROJECTION;
				break;

			case WORDS_FNWORDS_FNFRAMES_2:
				table = Q.WORDS_FNWORDS_FNFRAMES_2.TABLE;
				projection = Q.WORDS_FNWORDS_FNFRAMES_2.PROJECTION;
				break;

			case WORDS_FNWORDS_FNFRAMES_1U2:
				table = Q.WORDS_FNWORDS_FNFRAMES_1U2.TABLE; //.replaceAll("\\$\\{selection\\}", selection);
				break;

			case XSqlUNetDispatcher.WORDS_FNWORDS_FNFRAMES_U:
			{
				/*
				final String table1 = Q.WORDS_FNWORDS_FNFRAMES_1.TABLE;
				final String table2 = Q.WORDS_FNWORDS_FNFRAMES_2.TABLE;
				final String[] table1Projection = Q.WORDS_FNWORDS_FNFRAMES_1.PROJECTION;
				final String[] table2Projection = Q.WORDS_FNWORDS_FNFRAMES_2.PROJECTION;
				final String[] unionProjection = table1Projection;
				final String[] groupByArray = {V.WORDID, V.SYNSETID, V.FRAMEID};
				return Utils.makeUnionQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, selectionArgs, groupByArray, orderBy, "fn");
				*/
				table = Q.WORDS_FNWORDS_FNFRAMES_1U2.TABLE.replaceAll("#\\{selection\\}", selection);
				selection = null;
				selectionArgs = Utils.unfoldSelectionArgs(selectionArgs);
				groupBy = String.format("%s,%s,%s", V.WORDID, V.SYNSETID, V.FRAMEID);
				break;
			}

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, groupBy, orderBy);
	}
}
