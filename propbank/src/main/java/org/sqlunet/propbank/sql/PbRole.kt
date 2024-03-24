/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

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
	public final String argType;

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
	 * @param argType   role arg type
	 * @param roleFunc  role f
	 * @param roleTheta role theta
	 */
	private PbRole(final long roleId, final String roleDescr, final String argType, final String roleFunc, final String roleTheta)
	{
		super();
		this.roleId = roleId;
		this.roleDescr = roleDescr;
		this.argType = argType;
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
	@Nullable
	static public List<PbRole> make(final SQLiteDatabase connection, final long roleSetId)
	{
		List<PbRole> result = null;
		try (PbRoleQueryFromRoleSetId query = new PbRoleQueryFromRoleSetId(connection, roleSetId))
		{
			query.execute();

			while (query.next())
			{
				final long roleId = query.getRoleId();
				final String roleDescr = query.getRoleDescr();
				final String roleArgType = query.getArgType();
				final String roleFunc = query.getRoleFunc();
				final String roleTheta = query.getRoleTheta();
				if (result == null)
				{
					result = new ArrayList<>();
				}
				result.add(new PbRole(roleId, roleDescr, roleArgType, roleFunc, roleTheta));
			}
		}
		return result;
	}
}