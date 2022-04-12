/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * PredicateMatrix provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Factory implements Function<String, String[]>, Supplier<String[]>
{
	static public final String TABLE = "pm";

	static public final String AS_PMROLES = "mr";
	static public final String AS_PMPREDICATES = "mp";
	static public final String AS_VNCLASSES = "vc";
	static public final String AS_VNROLES = "vr";
	static public final String AS_VNROLETYPES = "vt";
	static public final String AS_PBROLESETS = "pc";
	static public final String AS_PBROLES = "pr";
	static public final String AS_PBARGS = "pa";
	static public final String AS_FNFRAMES = "ff";
	static public final String AS_FNFES = "fe";
	static public final String AS_FNFETYPES = "ft";
	static public final String AS_FNLUS = "fu";

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public Factory()
	{
	}

	private static String quote(String str)
	{
		return str == null ? null : String.format("\"%s\"", str);
	}

	@Override
	public String[] apply(final String keyname)
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
			case PM:
				table = TABLE;
				break;

			case PM_X:
				table = "pm " + //
						"LEFT JOIN s{pm_roles.table}      AS s{as_pmroles}      USING (s{pm_roles.roleid}) " + //
						"LEFT JOIN s{pm_predicates.table} AS s{as_pmpredicates} USING (s{pm_predicates.predid}) " + //
						"LEFT JOIN ${synsets.table]                             USING (${synsets.synsetid}) " + //

						"LEFT JOIN s{vn_classes.table}    AS s{as_vnclasses}    ON s{vnclasses.classid}          = s{as_vnclasses}.s{vnclasses.classid} " + //
						"LEFT JOIN s{vn_roles.table}      AS s{as_vnroles}      ON s{vnroleid}                   = s{as_vnroles}.s{vnroles.roleid} " + //
						"LEFT JOIN s{vn_roletypes.table}  AS s{as_vnroletypes}  ON s{vnroletypes.roletypeid}     = s{as_vnroletypes}.s{vnroletypes.roletypeid} " + //

						"LEFT JOIN s{pb_rolesets.table}   AS s{as_pbrolesets}   ON ${pbrolesets.rolesetid}       = s{as_pbrolesets}.s{pbrolesets.rolesetid} " + //
						"LEFT JOIN s{pb_roles.table}      AS s{as_pbroles}      ON s{pbroles.roleid}             = s{as_pbroles}.s{pbroles.roleid} " + //
						"LEFT JOIN s{pb_argns.table}      AS s{as_pbargs}       ON s{as_pbroles}.s{pbnargs.narg} = s{as_pbargs}.${pbargs.narg} " + //

						"LEFT JOIN s{fn_frames.table}     AS s{as_fnframes}     ON s{fnframes.frameid}           = s{as_fnframes}.${fnframes.frameid} " + //
						"LEFT JOIN s{fn_fes.table}        AS s{as_fnfes}        ON s{fnfes.feid}                 = s{as_fnfes}.s{fnfes.feid} " + //
						"LEFT JOIN s{fn_fetypes.table}    AS s{as_fnfetypes}    ON s{as_fnfes}.s{fetypeid}       = s{as_fnfetypes}.s{fnfetypes.fetypeid} " + //
						"LEFT JOIN s{fn_lexunits.table}   AS s{as_fnlus}        ON s{fnlexunits.luid}            = s{as_fnlus}.s{fnlexunits.luid}";
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
		PM, PM_X
	}
}
