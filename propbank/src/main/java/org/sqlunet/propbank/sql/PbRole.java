package org.sqlunet.propbank.sql;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author bbou
 */
class PbRole
{
	/**
	 * Role id
	 */
	public final long theRoleId;

	/**
	 * Role description
	 */
	public final String theRoleDescr;

	/**
	 * Role N arg
	 */
	public final String theNArg;

	/**
	 * Role F
	 */
	public final String theRoleFunc;

	/**
	 * Role theta
	 */
	public final String theRoleTheta;

	/**
	 * Constructor
	 *
	 * @param thisRoleId
	 *            is the role id
	 * @param thisRoleDescr
	 *            is the role description
	 * @param thisNArg
	 *            is the role N
	 * @param thisRoleFunc
	 *            is the role F
	 * @param thisRoleTheta
	 *            is the role theta
	 */
	private PbRole(final long thisRoleId, final String thisRoleDescr, final String thisNArg, final String thisRoleFunc, final String thisRoleTheta)
	{
		super();
		this.theRoleId = thisRoleId;
		this.theRoleDescr = thisRoleDescr;
		this.theNArg = thisNArg;
		this.theRoleFunc = thisRoleFunc;
		this.theRoleTheta = thisRoleTheta;
	}

	/**
	 * Make sets of PropBank roles from query built from rolesetid
	 *
	 * @param thisConnection
	 *            is the database connection
	 * @param thisRoleSetId
	 *            is the roleset id to build query from
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
		}
		finally
		{
			if (thisQuery != null)
			{
				thisQuery.release();
			}
		}
		return thisResult;
	}
}