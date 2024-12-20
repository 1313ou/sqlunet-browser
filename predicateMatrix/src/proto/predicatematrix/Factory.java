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
				table = "${pms.table} " + //
						"LEFT JOIN ${roles.table} AS ${as_pmroles} USING (${roles.roleid}) " + //
						"LEFT JOIN ${predicates.table} AS ${as_pmpredicates} USING (${predicates.predicateid}) " + //
						"LEFT JOIN ${wnsynsets.table} USING (${wnsynsets.synsetid}) " + //

						"LEFT JOIN ${vnclasses.table} AS ${as_vnclasses} ON ${pms.vn_classid} = ${as_vnclasses}.${vnclasses.vnclassid} " + //
						"LEFT JOIN ${vnroles.table} AS ${as_vnroles} ON ${pms.vn_roleid} = ${as_vnroles}.${vnroles.vnroleid} " + //
						"LEFT JOIN ${vnroletypes.table} AS ${as_vnroletypes} ON ${as_vnroles}.${vnroletypes.vnroletypeid} = ${as_vnroletypes}.${vnroletypes.vnroletypeid} " + //

						"LEFT JOIN ${pbrolesets.table} AS ${as_pbrolesets} ON ${pms.pb_rolesetid} = ${as_pbrolesets}.${pbrolesets.pbrolesetid} " + //
						"LEFT JOIN ${pbroles.table} AS ${as_pbroles} ON ${pms.pb_roleid} = ${as_pbroles}.${pbroles.pbroleid} " + //
						"LEFT JOIN ${pbargtypes.table} AS ${as_pbargs} ON ${as_pbroles}.${pbargtypes.pbargtypeid} = ${as_pbargs}.${pbargtypes.pbargtypeid} " + //

						"LEFT JOIN ${fn_frames.table} AS ${as_fnframes} ON ${pms.fn_frameid} = ${as_fnframes}.${fn_frames.fnframeid} " + //
						"LEFT JOIN ${fn_fes.table} AS ${as_fnfes} ON ${pms.fn_feid} = ${as_fnfes}.${fn_fes.fnfeid} " + //
						"LEFT JOIN ${fn_fetypes.table} AS ${as_fnfetypes} ON ${as_fnfes}.${fn_fetypes.fnfetypeid} = ${as_fnfetypes}.${fn_fetypes.fnfetypeid}"
				// + "LEFT JOIN ${fn_lexunits.table} AS ${as_fnlus} ON ${pms.fn_luid} = ${as_fnlus}.${fn_lexunits.fnluid}"
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
		return str == null ? null : String.format("\"%s\"", str);
	}
}
