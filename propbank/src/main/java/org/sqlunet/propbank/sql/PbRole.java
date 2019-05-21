/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * PropBank role
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
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
	 * @param roleId    role id
	 * @param roleDescr role description
	 * @param nArg      role N
	 * @param roleFunc  role F
	 * @param roleTheta role theta
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
	 * Make sets of PropBank roles from query built from roleSetId
	 *
	 * @param connection connection
	 * @param roleSetId  role set id to build query from
	 * @return list of PropBank roles
	 */
	@NonNull
	static public List<PbRole> make(final SQLiteDatabase connection, final long roleSetId)
	{
		final List<PbRole> result = new ArrayList<>();
		PbRoleQueryFromRoleSetId query = null;
		try
		{
			query = new PbRoleQueryFromRoleSetId(connection, roleSetId);
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