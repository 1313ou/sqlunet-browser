package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Set of roles attached to a VerbNet class
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
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
	 * @param roles is the list of roles
	 */
	private VnRoleSet(final List<VnRole> roles)
	{
		this.roles = roles;
	}

	/**
	 * Make VerbNet role sets from query built from class id, word id and synset id
	 *
	 * @param connection connection
	 * @param classId    is the class id to build query from
	 * @return set of VerbNet roles
	 */
	@Nullable
	static public VnRoleSet make(final SQLiteDatabase connection, final long classId)
	{
		VnRoleQueryFromClassId query = null;
		VnRoleSet roleSet = null;

		try
		{
			query = new VnRoleQueryFromClassId(connection, classId);
			query.execute();

			while (query.next())
			{
				final String roleType = query.getRoleType();
				final String selectionRestrictions = query.getSelectionRestriction();

				// new role
				VnRole role = new VnRole(roleType, selectionRestrictions);

				// allocate
				if (roleSet == null)
				{
					roleSet = new VnRoleSet(new ArrayList<VnRole>());
				}

				// addItem role to role set
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
	 * Make VerbNet role sets from query built from class id, word id and synset id
	 *
	 * @param connection connection
	 * @param classId    is the class id to build query from
	 * @param wordId     is the word id to build query from
	 * @param synsetId   is the synset id to build query from (null for any)
	 * @return set of VerbNet roles
	 */
	@Nullable
	static public VnRoleSet make(final SQLiteDatabase connection, final long classId, final long wordId, final Long synsetId)
	{
		VnRoleQueryFromClassIdAndSense query = null;
		VnRoleSet roleSet = null;

		try
		{
			query = new VnRoleQueryFromClassIdAndSense(connection, classId, wordId, synsetId);
			query.execute();

			while (query.next())
			{
				final String roleType = query.getRoleType();
				final String selectionRestrictions = query.getSelectionRestriction();

				// new role
				VnRole role = new VnRole(roleType, selectionRestrictions);

				// allocate
				if (roleSet == null)
				{
					roleSet = new VnRoleSet(new ArrayList<VnRole>());
				}

				// addItem role to role set
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
