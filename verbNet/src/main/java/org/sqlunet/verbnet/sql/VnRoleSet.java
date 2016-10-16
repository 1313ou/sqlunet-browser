package org.sqlunet.verbnet.sql;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

/**
 * Roles attached to a VerbNet Class
 *
 * @author bbou
 */
public class VnRoleSet
{
	/**
	 * Roles
	 */
	public final List<VnRole> roles;

	/**
	 * Constructor
	 *
	 * @param roles
	 *            is the list of roles
	 */
	private VnRoleSet(final List<VnRole> roles)
	{
		this.roles = roles;
	}

	/**
	 * Make VerbNet role sets from query built from classid, wordid and synsetid
	 *
	 * @param connection
	 *            is the database connection
	 * @param classId
	 *            is the class id to build query from
	 * @return list of VerbNet role sets
	 */
	static public VnRoleSet make(final SQLiteDatabase connection, final long classId)
	{
		VnRoleQueryCommand query = null;
		VnRoleSet roleSet = null;
		VnRole role;

		try
		{
			query = new VnRoleQueryCommand(connection, classId);
			query.execute();

			while (query.next())
			{
				final String roleType = query.getRoleType();
				final String selectionRestrictions = query.getSelectionRestriction();

				// new role
				role = new VnRole(roleType, selectionRestrictions);

				// allocate
				if (roleSet == null)
				{
					roleSet = new VnRoleSet(new ArrayList<VnRole>());
				}

				// add role to role set
				roleSet.roles.add(role);
			}
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return roleSet;
	}

	/**
	 * Make VerbNet role sets from query built from classid, wordid and synsetid
	 *
	 * @param connection
	 *            is the database connection
	 * @param classId
	 *            is the class id to build query from
	 * @param wordId
	 *            is the word id to build query from
	 * @param synsetId
	 *            is the synset id to build query from (-1 for any)
	 * @return list of VerbNet role sets
	 */
	static public VnRoleSet make(final SQLiteDatabase connection, final long classId, final long wordId, final Long synsetId)
	{
		VnRoleQueryFromSenseCommand query = null;
		VnRoleSet roleSet = null;
		VnRole role;

		try
		{
			query = new VnRoleQueryFromSenseCommand(connection, classId, wordId, synsetId);
			query.execute();

			while (query.next())
			{
				final String roleType = query.getRoleType();
				final String selectionRestrictions = query.getSelectionRestriction();

				// new role
				role = new VnRole(roleType, selectionRestrictions);

				// allocate
				if (roleSet == null)
				{
					roleSet = new VnRoleSet(new ArrayList<VnRole>());
				}

				// add role to role set
				roleSet.roles.add(role);
			}
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return roleSet;
	}
}
