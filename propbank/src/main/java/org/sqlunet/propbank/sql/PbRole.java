package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
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
	 * @param thisRoleId    is the role id
	 * @param thisRoleDescr is the role description
	 * @param thisNArg      is the role N
	 * @param thisRoleFunc  is the role F
	 * @param thisRoleTheta is the role theta
	 */
	private PbRole(final long thisRoleId, final String thisRoleDescr, final String thisNArg, final String thisRoleFunc, final String thisRoleTheta)
	{
		super();
		this.roleId = thisRoleId;
		this.roleDescr = thisRoleDescr;
		this.nArg = thisNArg;
		this.roleFunc = thisRoleFunc;
		this.roleTheta = thisRoleTheta;
	}

	/**
	 * Make sets of PropBank roles from query built from rolesetid
	 *
	 * @param thisConnection is the database connection
	 * @param thisRoleSetId  is the roleset id to build query from
	 * @return list of PropBank roles
	 */
	public static List<PbRole> make(final SQLiteDatabase thisConnection, final long thisRoleSetId)
	{
		final List<PbRole> thisResult = new ArrayList<>();
		PbRoleQueryCommand thisQuery = null;
		try
		{
			thisQuery = new PbRoleQueryCommand(thisConnection, thisRoleSetId);
			thisQuery.execute();

			while (thisQuery.next())
			{
				final long thisRoleId = thisQuery.getRoleId();
				final String thisRoleDescr = thisQuery.getRoleDescr();
				final String thisNArg = thisQuery.getNArg();
				final String thisRoleFunc = thisQuery.getRoleFunc();
				final String thisRoleTheta = thisQuery.getRoleTheta();
				thisResult.add(new PbRole(thisRoleId, thisRoleDescr, thisNArg, thisRoleFunc, thisRoleTheta));
			}
		} finally
		{
			if (thisQuery != null)
			{
				thisQuery.release();
			}
		}
		return thisResult;
	}
}