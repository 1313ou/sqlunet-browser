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
	/**
	 * Constructor
	 */
	public Factory()
	{
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
				table = "${pms.table}";
				break;

			case PM_X:
				table = "${pms.table} AS ${as_pm} " + //
						"LEFT JOIN ${roles.table} AS ${as_pmroles} USING (${roles.roleid}) " + //
						"LEFT JOIN ${predicates.table} AS ${as_pmpredicates} USING (${predicates.predicateid}) " + //
						"LEFT JOIN ${wnsynsets.table} USING (${wnsynsets.synsetid}) " + //

						"LEFT JOIN ${vnclasses.table} AS ${as_vnclasses} ON ${as_pm}.${pms.vn_classid} = ${as_vnclasses}.${vnclasses.vnclassid} " + //
						"LEFT JOIN ${vnroles.table} AS ${as_vnroles} ON ${as_pm}.${pms.vn_roleid} = ${as_vnroles}.${vnroles.vnroleid} " + //
						"LEFT JOIN ${vnroletypes.table} AS ${as_vnroletypes} ON ${as_vnroles}.${vnroletypes.vnroletypeid} = ${as_vnroletypes}.${vnroletypes.vnroletypeid} " + //

						"LEFT JOIN ${pbrolesets.table} AS ${as_pbrolesets} ON ${as_pm}.${pms.pb_rolesetid} = ${as_pbrolesets}.${pbrolesets.pbrolesetid} " + //
						"LEFT JOIN ${pbroles.table} AS ${as_pbroles} ON ${as_pm}.${pms.pb_roleid} = ${as_pbroles}.${pbroles.pbroleid} " + //
						"LEFT JOIN ${pbargtypes.table} AS ${as_pbargs} ON ${as_pbroles}.${pbargtypes.pbargtypeid} = ${as_pbargs}.${pbargtypes.pbargtypeid} " + //

						"LEFT JOIN ${fnframes.table} AS ${as_fnframes} ON ${as_pm}.${pms.fn_frameid} = ${as_fnframes}.${fnframes.fnframeid} " + //
						"LEFT JOIN ${fnfes.table} AS ${as_fnfes} ON ${as_pm}.${pms.fn_feid} = ${as_fnfes}.${fnfes.fnfeid} " + //
						"LEFT JOIN ${fnfetypes.table} AS ${as_fnfetypes} ON ${as_fnfes}.${fnfetypes.fnfetypeid} = ${as_fnfetypes}.${fnfetypes.fnfetypeid}"
						// + "LEFT JOIN ${fnlexunits.table} AS ${as_fnlus} ON ${pms.fn_luid} = ${as_fnlus}.${fnlexunits.fnluid}"
				;
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

	private static String quote(String str)
	{
		return str == null ? null : '"' + str + '"';
	}
}
