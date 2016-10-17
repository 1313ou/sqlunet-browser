package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Role
 *
 * @author bbou
 */
class PbRole
{
	/**
	 * Role id
	 */
	public final long roleId;

	/**
	 * Role description
	 */
	public final String roleDescr;

	/**
	 * Role N arg
	 */
	public final String nArg;

	/**
	 * Role F
	 */
	public final String roleFunc;

	/**
	 * Role theta
	 */
	public final String roleTheta;

	/**
	 * Constructor
	 *
	 * @param roleId    is the role id
	 * @param roleDescr is the role description
	 * @param nArg      is the role N
	 * @param roleFunc  is the role F
	 * @param roleTheta is the role theta
	 */
	private PbRole(final long roleId, final String roleDescr, final String nArg, final String roleFunc, final String roleTheta)
	{
		super();
		this.roleId = roleId;
		this.roleDescr = roleDescr;
		this.nArg = nArg;
		this.roleFunc = roleFunc;
		this.roleTheta = roleTheta;
	}

	/**
	 * Make sets of PropBank roles from query built from rolesetid
	 *
	 * @param connection is the database connection
	 * @param roleSetId  is the roleset id to build query from
	 * @return list of PropBank roles
	 */
	public static List<PbRole> make(final SQLiteDatabase connection, final long roleSetId)
	{
		final List<PbRole> result = new ArrayList<>();
		PbRoleQueryCommand query = null;
		try
		{
			query = new PbRoleQueryCommand(connection, roleSetId);
			query.execute();

			while (query.next())
			{
				final long roleId = query.getRoleId();
				final String roleDescr = query.getRoleDescr();
				final String nArg = query.getNArg();
				final String roleFunc = query.getRoleFunc();
				final String roleTheta = query.getRoleTheta();
				result.add(new PbRole(roleId, roleDescr, nArg, roleFunc, roleTheta));
			}
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return result;
	}
}