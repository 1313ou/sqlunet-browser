package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Role set
 * @author Bernard Bou
 */
class PbRoleSet
{
	/**
	 * Name
	 */
	public final String roleSetName;

	/**
	 * Head
	 */
	public final String roleSetHead;

	/**
	 * Description
	 */
	public final String roleSetDescr;

	/**
	 * Id
	 */
	public final long roleSetId;

	/**
	 * Constructor
	 * @param roleSetName name
	 * @param roleSetHead head
	 * @param roleSetDescr description
	 * @param roleSetId id
	 */
	private PbRoleSet(final String roleSetName, final String roleSetHead, final String roleSetDescr, final long roleSetId)
	{
		super();
		this.roleSetName = roleSetName;
		this.roleSetHead = roleSetHead;
		this.roleSetDescr = roleSetDescr;
		this.roleSetId = roleSetId;
	}

	/**
	 * Make sets of PropBank rolesets from query built from wordid
	 *
	 * @param connection is the database connection
	 * @param word       is the word to build query from
	 * @return list of PropBank rolesets
	 */
	static public Pair<Long, List<PbRoleSet>> makeFromWord(final SQLiteDatabase connection, final String word)
	{
		final List<PbRoleSet> result = new ArrayList<>();
		PbRoleSetQueryFromWordCommand query = null;
		try
		{
			query = new PbRoleSetQueryFromWordCommand(connection, word);
			query.execute();

			long wordId = 0;
			while (query.next())
			{
				wordId = query.getWordId();
				final String roleSetName = query.getRoleSetName();
				final String roleSetHead = query.getRoleSetHead();
				final String roleSetDescr = query.getRoleSetDescr();
				final long roleSetId = query.getRoleSetId();
				result.add(new PbRoleSet(roleSetName, roleSetHead, roleSetDescr, roleSetId));
			}
			return new Pair<>(wordId, result);
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
	}

	/**
	 * Make sets of PropBank rolesets from query built from wordid
	 *
	 * @param connection is the database connection
	 * @param wordId     is the word id to build query from
	 * @return list of PropBank rolesets
	 */
	static public List<PbRoleSet> makeFromWordId(final SQLiteDatabase connection, final long wordId)
	{
		final List<PbRoleSet> result = new ArrayList<>();
		PbRoleSetQueryFromWordIdCommand query = null;
		try
		{
			query = new PbRoleSetQueryFromWordIdCommand(connection, wordId);
			query.execute();

			while (query.next())
			{
				final String roleSetName = query.getRoleSetName();
				final String roleSetHead = query.getRoleSetHead();
				final String roleSetDescr = query.getRoleSetDescr();
				final long roleSetId = query.getRoleSetId();
				result.add(new PbRoleSet(roleSetName, roleSetHead, roleSetDescr, roleSetId));
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

	/**
	 * Make sets of PropBank rolesets from query built from wordid
	 *
	 * @param connection is the database connection
	 * @param roleSetId  is the roleset id to build query from
	 * @return list of PropBank rolesets
	 */
	static public List<PbRoleSet> make(final SQLiteDatabase connection, final long roleSetId)
	{
		final List<PbRoleSet> result = new ArrayList<>();
		PbRoleSetQueryCommand query = null;
		try
		{
			query = new PbRoleSetQueryCommand(connection, roleSetId);
			query.execute();

			while (query.next())
			{
				final String roleSetName = query.getRoleSetName();
				final String roleSetHead = query.getRoleSetHead();
				final String roleSetDescr = query.getRoleSetDescr();
				result.add(new PbRoleSet(roleSetName, roleSetHead, roleSetDescr, roleSetId));
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
